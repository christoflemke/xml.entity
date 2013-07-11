package xml.entity.select;

import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.select.PathParser.Path;
import xml.entity.select.dsl.NodeSelection;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;

final class NodeSelectImpl implements NodeSelection
{
    private final class SelectVisitor implements ISelectionVisitor, Supplier<ImmutableList<ImmutableElement>>
    {
        private final Builder<ImmutableElement> builder;
        private SelectVisitor()
        {
            this.builder = ImmutableList.builder();
        }

        @Override
        public ImmutableList<ImmutableElement> get()
        {
            NodeSelectImpl.this.defaultSelector.select(NodeSelectImpl.this.path, NodeSelectImpl.this.element, this);
            return this.builder.build();
        }
        @Override
        public void mismatch(final ImmutableElement element)
        {}
        @Override
        public void match(final ImmutableElement element)
        {
            if(NodeSelectImpl.this.expr.apply(element))
            {
                this.builder.add(element);
            }
        }
        @Override
        public void leaveChild(final ImmutableElement element)
        {}
        @Override
        public void enterChild(final ImmutableElement element)
        {}
    }
    private final DefaultSelector defaultSelector;
    private final ImmutableElement element;
    private final Path path;
    private final Predicate<ImmutableElement> expr;
    private final Supplier<ImmutableList<ImmutableElement>> supplier = Suppliers.memoize(new SelectVisitor());
    public NodeSelectImpl(final DefaultSelector defaultSelector, final ImmutableElement element, final Path path, final Predicate<ImmutableElement> expr)
    {
        this.defaultSelector = defaultSelector;
        this.element = element;
        this.path = path;
        this.expr = expr;
    }
    @Override
    @Nonnull
    public ImmutableElement one()
    {
        if(all().isEmpty())
        {
            throw new NoSuchElementException("no element found at path: " + this.path);
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
        return this.supplier.get();
    }
}