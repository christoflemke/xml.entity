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
package xml.entity.mutableelement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xml.entity.immutableelement.ImmutableElement;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public abstract class Elements
{
    /**
     * Match elements by name.
     */
	public static Predicate<Element> byName(@Nonnull final String name)
	{
		Preconditions.checkNotNull(name);
		return new Predicate<Element>() {

			@Override
            public boolean apply(@Nonnull final Element input)
			{
				return name.equals(input.name());
			}

			@Override public String toString()
			{
				return name;
			}
		};
	}

    /**
     * Match elements by value
     */
    public static Predicate<Element> byValue(@Nullable final String value)
	{
		return new Predicate<Element>() {

			@Override
            public boolean apply(@Nonnull final Element input)
			{
                return Objects.equal(value, input.value());
			}

			@Override public String toString()
			{
				return "=" + value;
			}
		};
	}

    /**
     * Transform elements to their value.
     */
	public static final Function<Element, String> toValue = new Function<Element, String>() {

		@Override public String apply(@Nonnull final Element elm)
		{
			return elm.value();
		}
	};

	private static Predicate<Element> isAttribute = new Predicate<Element>() {

		@Override public boolean apply(@Nonnull final Element e)
		{
			return e.name().startsWith("@");
		}
	};

    /**
     * Match attribute nodes.
     */
	public static Predicate<Element> isAttribute()
	{
		return isAttribute;
	}

	private static Predicate<Element> isText = new Predicate<Element>() {

		@Override public boolean apply(@Nonnull final Element e)
		{
			return e.name().startsWith("#");
		}
	};

    /**
     * Transform mutable to immutable elements.
     */
    public static Function<Element, ImmutableElement> immutableCopy = new Function<Element, ImmutableElement>() {

        @Override
        @Nullable
        public ImmutableElement apply(@Nullable final Element mutable)
        {
            return mutable.immutableCopy();
        }
    };

    /**
     * Copy elements.
     */
    public static final Function<Element, Element> copy = new Function<Element, Element>() {

        @Override public Element apply(@Nonnull final Element element)
        {
            return element.copy();
        }
    };

    /**
     * Match text nodes
     */
	public static Predicate<Element> isText()
	{
		return isText;
	}

    /**
     * Create a predicate that matches if the node has an child that matches the
     * given predicate.
     * 
     * @param matching
     *            This predicate will be applied the the children of the node.
     * @return true if any child matches.
     */
	public static Predicate<Element> hasChild(final Predicate<Element> matching)
	{
		return new Predicate<Element>() {

			@Override public boolean apply(@Nullable final Element input)
			{
                return Iterables.any(input.children(), matching);
			}

			@Override public String toString()
			{
				return matching.toString();
			}
		};
	}

    /**
     * Match attribute nodes with the given name and value.
     * 
     * @param name
     *            The attribute name.
     * @param value
     *            The attribute value.
     * @return true if name and value match.
     */
    @SuppressWarnings("unchecked")
    public static Predicate<Element> attr(final String name, final String value)
    {
        return Predicates.and(isAttribute(), byName("@" + name), byValue(value));
    }
}
