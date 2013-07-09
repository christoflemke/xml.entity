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

public abstract class ImmutableElements
{
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

    public static Predicate<ImmutableElement> isText()
    {
        return isText;
    }

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

        public Predicate<ImmutableElement> value(final String value)
        {
            return Predicates.and(this, byValue(value));
        }

    }

    public static AttrPredicate attr(final String name)
    {
        return new AttrPredicate(name);
    }

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
    public static Function<ImmutableElement, String> toName()
    {
        return toName;
    }
}
