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
package xml.entity.immutableelement;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xml.entity.select.dsl.NodeSelection;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * The {@link Predicate}s returned by this class will expect all elements, in
 * the collection they are applied to, to be non null
 */
public abstract class ImmutableElements
{
    /**
     * Filter elements by name. For filtering attributes use
     * {@link #attr(String)} and for text {@link #isText()}.
     * 
     * @param name
     *            The name the resulting elements should match
     * @return A predicate matching the given name
     */
    public static Predicate<ImmutableElement> byName(@Nonnull final String name)
    {
        Preconditions.checkNotNull(name);
        return new Predicate<ImmutableElement>() {

            @Override
            public boolean apply(@Nonnull final ImmutableElement input)
            {
                return name.equals(input.name());
            }

            @Override
            public String toString()
            {
                return name;
            }
        };
    }

    /**
     * Match nodes by their value.
     * 
     * @param value
     * @return
     */
    public static Predicate<ImmutableElement> byValue(@Nonnull final String value)
    {
        Preconditions.checkNotNull(value);
        return new Predicate<ImmutableElement>() {

            @Override
            public boolean apply(@Nonnull final ImmutableElement input)
            {
                return value.equals(input.value());
            }

            @Override
            public String toString()
            {
                return "=" + value;
            }
        };
    }

    /**
     * Transforms a node to its value by calling .value().
     */
    public static final Function<ImmutableElement, String> toValue = new Function<ImmutableElement, String>() {

        @Override
        public String apply(@Nonnull final ImmutableElement elm)
        {
            return elm.value();
        }
    };

    private static Predicate<ImmutableElement> isAttribute = new Predicate<ImmutableElement>() {

        @Override
        public boolean apply(@Nonnull final ImmutableElement e)
        {
            return e.name().startsWith("@");
        }
    };

    /**
     * Test if a node is an attribute
     * 
     * @return
     */
    public static Predicate<ImmutableElement> isAttribute()
    {
        return isAttribute;
    }

    private static Predicate<ImmutableElement> isText = new Predicate<ImmutableElement>() {

        @Override
        public boolean apply(@Nonnull final ImmutableElement e)
        {
            return e.name().startsWith("#");
        }
    };

    /**
     * Test if a node is a text node
     * 
     * @return
     */
    public static Predicate<ImmutableElement> isText()
    {
        return isText;
    }

    /**
     * Test if this node has a child matching the given predicate.
     * 
     * @param matching
     *            This predicate will be applied to the children of the matched
     *            node.
     * @return true if any child matches the given predicate.
     */
    public static Predicate<ImmutableElement> hasChild(final Predicate<ImmutableElement> matching)
    {
        return new Predicate<ImmutableElement>() {

            @Override
            public boolean apply(@Nullable final ImmutableElement input)
            {
                return Iterables.any(input.children(), matching);
            }

            @Override
            public String toString()
            {
                return matching.toString();
            }
        };
    }

    public static class AttrPredicate implements Predicate<ImmutableElement>
    {
        private final String name;

        public AttrPredicate(final String name)
        {
            super();
            this.name = name;
        }

        @Override
        public boolean apply(@Nullable final ImmutableElement element)
        {
            return ("@" + this.name).equals(element.name());
        }

        /**
         * Does attribute also match the given value.
         * 
         * @param value
         * @return True if name and value match
         */
        public Predicate<ImmutableElement> value(final String value)
        {
            return Predicates.and(this, byValue(value));
        }

    }

    /**
     * Match attributes by their name.
     * 
     * @param name
     *            The attribute name without '@'
     * @return
     */
    public static AttrPredicate attr(final String name)
    {
        return new AttrPredicate(name);
    }

    /**
     * Does a path from the matched node matching the given path.
     * 
     * @param path
     * @return
     */
    public static Predicate<ImmutableElement> hasPath(final String path)
    {
        return new Predicate<ImmutableElement>() {

            @Override
            public boolean apply(@Nullable final ImmutableElement input)
            {
                final NodeSelection selection = input.select().from(path);
                return !selection.all().isEmpty();
            }
        };
    }

    /**
     * Is this node neither an attribute or a text node. The element can be an
     * child.
     * 
     * @return
     */
    public static Predicate<ImmutableElement> isInternal()
    {
        return and(not(isAttribute), not(isText));
    }

    private static final Function<ImmutableElement, String> toName = new Function<ImmutableElement, String>() {

        @Override
        @Nullable
        public String apply(@Nullable final ImmutableElement input)
        {
            return input.name();
        }
    };

    /**
     * Transform a node to its name by calling .name();
     * 
     * @return
     */
    public static Function<ImmutableElement, String> toName()
    {
        return toName;
    }
}
