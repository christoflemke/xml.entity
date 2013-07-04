/*
 * Copyright 2013 Christof Lemke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xml.entity.select;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static xml.entity.immutableelement.ImmutableElements.byName;
import static xml.entity.immutableelement.ImmutableElements.isText;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElementFactory;
import xml.entity.immutableelement.ImmutableElements;
import xml.entity.select.PathParser.Path;
import xml.entity.select.PathParser.PathExpr;
import xml.entity.select.dsl.DSL;
import xml.entity.select.dsl.DSL.Delete;
import xml.entity.select.dsl.DSL.Delete.NodeDelete;
import xml.entity.select.dsl.DSL.Insert;
import xml.entity.select.dsl.DSL.Insert.InsertInto;
import xml.entity.select.dsl.DSL.Update;
import xml.entity.select.dsl.DSL.Update.NodeUpdate;
import xml.entity.select.dsl.DSL.WithWhere;
import xml.entity.select.dsl.DSLException;
import xml.entity.select.dsl.ExpectedMatches;
import xml.entity.select.dsl.NodeSelection;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@Immutable
public class DefaultSelector implements Selector
{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ImmutableElementFactory factory = ImmutableElementFactory.create(this);

    private abstract class ReplaceVisitor implements ISelectionVisitor
    {
        private final Map<ImmutableElement, ImmutableElement> replace = Maps.newHashMap();
        private ImmutableElement root;

        public ReplaceVisitor()
        {
            super();
            root = null;
        }

        @Override
        public final void mismatch(final ImmutableElement element)
        {}

        @Override
        public final void enterChild(final ImmutableElement element)
        {}

        @Override
        public final void leaveChild(final ImmutableElement element)
        {
            if(!ImmutableElements.isInternal().apply(element))
            {
                return;
            }
            // test if replacements for children exist
            final SetView<ImmutableElement> intersection = Sets.intersection(Sets.newHashSet(element.children()), replace.keySet());
            if(intersection.isEmpty())
            {
                // always set root to the last node left
                if(replace.containsKey(element))
                {
                    // if the root node has been replaced
                    root = replace.get(element);
                    logger.debug("replace root: {}, with: {}", element, root);
                }
                else
                {
                    // if the node is unmodified
                    root = element;
                }
            }
            else
            {
                // replace children
                final Builder<ImmutableElement> builder = ImmutableList.builder();
                for(final ImmutableElement e : element.children())
                {
                    if(replace.containsKey(e))
                    {
                        final ImmutableElement repacement = replace.remove(e);
                        logger.debug("replace: {}, with: {}", e, repacement);
                        if(repacement == null)
                        {
                            // skip
                        }
                        else
                        {
                            builder.add(repacement);
                        }
                    }
                    else
                    {
                        builder.add(e);
                    }
                }
                final ImmutableElement internalElement = factory.createNode(element.name(), builder.build());
                replace(element, internalElement);
                root = internalElement;
            }
        }

        protected void replace(final ImmutableElement old, final ImmutableElement newElement)
        {
            logger.debug("add replacement: {}, with: {}", old, newElement);
            replace.put(old, newElement);
        }

        public ImmutableElement element()
        {
            return root;
        }
    }

    private static interface UpdateOperation extends Function<ImmutableElement, ImmutableElement>
    {
    }

    private class ReplaceOperation implements UpdateOperation
    {
        private final Predicate<ImmutableElement> replace;
        private final ImmutableElement with;
        public ReplaceOperation(final Predicate<ImmutableElement> replace, final ImmutableElement with)
        {
            super();
            this.replace = replace;
            this.with = with;
        }
        @Override
        @Nullable
        public
        ImmutableElement apply(@Nullable final ImmutableElement input)
        {
            final Builder<ImmutableElement> builder = ImmutableList.builder();
            for(final ImmutableElement e : input.children())
            {
                if(replace.apply(e))
                {
                    builder.add(with);
                }
                else
                {
                    builder.add(e);
                }
            }
            return factory.createNode(input.name(), builder.build());
        }

    }

    private class AttrOperation implements UpdateOperation
    {
        private final String name;
        private final String value;

        public AttrOperation(final String name, final String value)
        {
            super();
            this.name = name;
            this.value = value;
        }

        @Override
        public ImmutableElement apply(final ImmutableElement element)
        {
            final Builder<ImmutableElement> builder = ImmutableList.builder();
            builder.addAll(filter(element.children(), not(byName("@" + name))));
            if(value != null)
            {
                builder.add(factory.createAttr(name, value));
            }
            return factory.createNode(element.name(), builder.build());
        }

        @Override
        public String toString()
        {
            return "@" + name + "=" + value;
        }
    }

    private class TextOperation implements UpdateOperation
    {
        private final String value;

        public TextOperation(final String value)
        {
            super();
            this.value = value;
        }

        @Override
        public ImmutableElement apply(final ImmutableElement element)
        {
            final Builder<ImmutableElement> builder = ImmutableList.builder();
            builder.addAll(filter(element.children(), not(isText())));
            if(value != null)
            {
                builder.add(factory.createText(value));
            }
            return factory.createNode(element.name(), builder.build());
        }

        @Override
        public String toString()
        {
            return "#text=" + value;
        }
    }

    private final class NodeSelectImpl implements NodeSelection
    {
        private final class SelectVisitor implements ISelectionVisitor, Supplier<ImmutableList<ImmutableElement>>
        {
            private final Builder<ImmutableElement> builder;
            private SelectVisitor()
            {
                builder = ImmutableList.builder();
            }

            @Override
            public ImmutableList<ImmutableElement> get()
            {
                select(path, element, this);
                return builder.build();
            }
            @Override
            public void mismatch(final ImmutableElement element)
            {}
            @Override
            public void match(final ImmutableElement element)
            {
                if(expr.apply(element))
                {
                    builder.add(element);
                }
            }
            @Override
            public void leaveChild(final ImmutableElement element)
            {}
            @Override
            public void enterChild(final ImmutableElement element)
            {}
        }
        private final ImmutableElement element;
        private final Path path;
        private final Predicate<ImmutableElement> expr;
        private final Supplier<ImmutableList<ImmutableElement>> supplier = Suppliers.memoize(new SelectVisitor());
        public NodeSelectImpl(final ImmutableElement element, final Path path, final Predicate<ImmutableElement> expr)
        {
            this.element = element;
            this.path = path;
            this.expr = expr;
        }
        @Override
        @Nonnull
        public NodeSelection where(final Predicate<ImmutableElement> expr)
        {
            return new NodeSelectImpl(element, path, Predicates.and(expr, this.expr));
        }
        @Override
        @Nonnull
        public ImmutableElement one()
        {
            if(all().isEmpty())
            {
                throw new NoSuchElementException("no element found at path: " + path);
            }
            return Iterables.getOnlyElement(all());
        }
        @Override
        @Nonnull
        public FluentIterable<ImmutableElement> iterable()
        {
            return FluentIterable.from(all());
        }
        @Override
        @Nonnull
        public ImmutableList<ImmutableElement> all()
        {
            return supplier.get();
        }
    }

    private abstract class AbstractSelectOperation<T extends WithWhere<T>> implements WithWhere<T>
    {
        private final ImmutableElement root;
        private final Path path;
        private final Predicate<ImmutableElement> expr;
        private final ExpectedMatches expectedMatches;
        AbstractSelectOperation(final ImmutableElement root, final Path path, final Predicate<ImmutableElement> expr, final ExpectedMatches expectedMatches)
        {
            super();
            this.root = root;
            this.path = path;
            this.expr = expr;
            this.expectedMatches = expectedMatches;
        }

        ImmutableElement getRoot()
        {
            return root;
        }

        Predicate<ImmutableElement> getExpr()
        {
            return expr;
        }

        Path getPath()
        {
            return path;
        }

        public ExpectedMatches getExpectedMatches()
        {
            return expectedMatches;
        }

        public final T expect(final ExpectedMatches matches)
        {
            return create(root, path, expr, matches);
        }

        @Override
        public final T where(final Predicate<ImmutableElement> expr)
        {
            return create(root, path, Predicates.and(getExpr(), expr), expectedMatches);
        }

        abstract T create(ImmutableElement root, Path path, Predicate<ImmutableElement> expr, ExpectedMatches expectedMatches);

        abstract void onMatch(ImmutableElement element, ReplaceVisitor visitor);

        private final class Visitor extends ReplaceVisitor
        {
            private int numMatches = 0;

            @Override
            public void match(final ImmutableElement element)
            {
                if(expr.apply(element))
                {
                    numMatches++;
                    onMatch(element, this);
                }
            }

            void checkMatches()
            {
                if(!expectedMatches.apply(numMatches))
                {
                    throw new DSLException("Expected: :" + expectedMatches + ", acutual: " + numMatches);
                }
            }
        }

        private Visitor getVisitor()
        {
            return new Visitor();
        }

        @Nonnull
        public final ImmutableElement element()
        {
            final Visitor visitor = getVisitor();
            select(getPath(), getRoot(), visitor);
            visitor.checkMatches();
            return visitor.element();
        }
    }

    private final class NodeUpdateImpl extends AbstractSelectOperation<NodeUpdate> implements Update.NodeUpdate
    {
        private final ImmutableList<UpdateOperation> ops;

        public NodeUpdateImpl(
                final ImmutableElement element,
                final Path parsed,
                final Predicate<ImmutableElement> expr,
                final ImmutableList<UpdateOperation> ops,
                final ExpectedMatches expectedMatches)
        {
            super(element, parsed, expr, expectedMatches);
            this.ops = ops;
        }

        @Override
        public void onMatch(final ImmutableElement element, final ReplaceVisitor visitor)
        {
            ImmutableElement copy = element;
            for(final UpdateOperation op : ops)
            {
                copy = op.apply(copy);
            }
            visitor.replace(element, copy);
        }

        @Override
        NodeUpdate create(final ImmutableElement root, final Path path, final Predicate<ImmutableElement> expr, final ExpectedMatches expectedMatches)
        {
            return new NodeUpdateImpl(root, path, expr, ops, expectedMatches);
        }
        @Override
        @Nonnull
        public NodeUpdate setText(final String text)
        {
            final TextOperation operation = new TextOperation(text);
            return withOp(operation);
        }
        @Override
        @Nonnull
        public NodeUpdate setAttr(final String name, final String value)
        {
            final AttrOperation operation = new AttrOperation(name, value);
            return withOp(operation);
        }
        private NodeUpdate withOp(final UpdateOperation operation)
        {
            final Builder<UpdateOperation> builder = ImmutableList.builder();
            builder.addAll(ops);
            builder.add(operation);
            return new NodeUpdateImpl(getRoot(), getPath(), getExpr(), builder.build(), getExpectedMatches());
        }

        @Override
        public NodeUpdate replace(final Predicate<ImmutableElement> replace, final ImmutableElement with)
        {
            return withOp(new ReplaceOperation(replace, with));
        }
    }

    private final class InsertIntoImpl extends AbstractSelectOperation<DSL.Insert.InsertInto> implements Insert.InsertInto
    {
        private final ImmutableList<ImmutableElement> nodes;
        public InsertIntoImpl(
                final ImmutableElement element,
                final Path path,
                final ImmutableList<ImmutableElement> nodes,
                final Predicate<ImmutableElement> expr,
                final ExpectedMatches expectedMatches)
        {
            super(element, path, expr, expectedMatches);
            this.nodes = nodes;
        }
        @Override
        public void onMatch(final ImmutableElement element, final ReplaceVisitor visitor)
        {
            final Builder<ImmutableElement> children = ImmutableList.builder();
            children
                    .addAll(element.children())
                    .addAll(nodes);
            final ImmutableElement copy = factory.createNode(element.name(), children.build());
            visitor.replace(element, copy);
        }
        @Override
        public InsertInto values(final ImmutableElement value)
        {
            Preconditions.checkNotNull(value);
            return this.values(ImmutableList.of(value));
        }
        @Override
        public InsertInto values(final List<ImmutableElement> values)
        {
            final Builder<ImmutableElement> builder = ImmutableList.builder();
            builder.addAll(nodes);
            builder.addAll(values);
            return new InsertIntoImpl(getRoot(), getPath(), builder.build(), getExpr(), getExpectedMatches());
        }

        @Override
        InsertInto create(final ImmutableElement root, final Path path, final Predicate<ImmutableElement> expr, final ExpectedMatches expectedMatches)
        {
            return new InsertIntoImpl(root, path, nodes, expr, expectedMatches);
        }
    }

    private final class NodeDeleteImpl extends AbstractSelectOperation<DSL.Delete.NodeDelete> implements DSL.Delete.NodeDelete
    {
        private NodeDeleteImpl(
                final ImmutableElement element,
                final Path parsed,
                final Predicate<ImmutableElement> expr,
                final ExpectedMatches expectedMatches)
        {
            super(element, parsed, expr, expectedMatches);
        }
        @Override
        public void onMatch(final ImmutableElement element, final ReplaceVisitor visitor)
        {
            visitor.replace(element, null);
        }

        @Override
        NodeDelete create(final ImmutableElement root, final Path path, final Predicate<ImmutableElement> expr, final ExpectedMatches expectedMatches)
        {
            return new NodeDeleteImpl(root, path, expr, expectedMatches);
        }
    }

    private final PathParser pathParser = PathParser.create();
    private DefaultSelector()
    {

    }

    public static Selector create()
    {
        return new DefaultSelector();
    }

    void select(final Path path, final ImmutableElement current, final ISelectionVisitor visitor)
    {
        visitor.enterChild(current);
        final PathExpr head = path.head();
        if(head.apply(current))
        {
            final Path tail = path.tail();
            if(tail.isEmpty())
            {
                visitor.match(current);
            }
            else
            {
                for(final ImmutableElement child : current.children())
                {
                    select(tail, child, visitor);
                }
            }

        }
        else
        {
            visitor.mismatch(current);
        }
        visitor.leaveChild(current);
    }

    @Override
    public DSL.Select createSelect(final ImmutableElement element)
    {
        return new DSL.Select() {

            @Override
            public NodeSelection from(final String path)
            {
                final Path parsed = pathParser.parse(path);
                final Predicate<ImmutableElement> expr = Predicates.alwaysTrue();
                return new NodeSelectImpl(element, parsed, expr);
            }
        };
    }

    @Override
    @Nonnull
    public DSL.Insert createInsert(@Nonnull final ImmutableElement element)
    {
        return new Insert() {

            @Override
            public InsertInto into(final String path)
            {
                final Path parse = pathParser.parse(path);
                final ImmutableList<ImmutableElement> nodes = ImmutableList.of();
                final Predicate<ImmutableElement> expr = Predicates.alwaysTrue();
                return new InsertIntoImpl(element, parse, nodes, expr, ExpectedMatches.any());
            }
        };
    }

    @Override
    @Nonnull
    public DSL.Update createUpdate(@Nonnull final ImmutableElement element)
    {
        return new Update() {

            @Override
            @Nonnull
            public NodeUpdate from(final String path)
            {
                final Path parsed = pathParser.parse(path);
                final Predicate<ImmutableElement> alwaysTrue = Predicates.alwaysTrue();
                final ImmutableList<UpdateOperation> ops = ImmutableList.of();
                return new NodeUpdateImpl(element, parsed, alwaysTrue, ops, ExpectedMatches.any());
            }
        };
    }

    @Override
    @Nonnull
    public DSL.Delete createDelete(@Nonnull final ImmutableElement element)
    {
        return new Delete() {

            @Override
            public NodeDelete from(final String path)
            {
                final Path parsed = pathParser.parse(path);
                final Predicate<ImmutableElement> expr = Predicates.alwaysTrue();
                return new NodeDeleteImpl(element, parsed, expr, ExpectedMatches.any());
            }
        };
    }
}
