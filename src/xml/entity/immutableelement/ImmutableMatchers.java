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

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import xml.entity.select.dsl.NodeSelection;

import com.google.common.base.Objects;

public class ImmutableMatchers
{
    public static final class HasAttr extends HasChild
    {
        private HasAttr(final Matcher<ImmutableElement> childMatcher)
        {
            super(childMatcher);
        }
        public Matcher<ImmutableElement> withValue(final String value)
        {
            return new BaseMatcher<ImmutableElement>() {

                @Override
                public boolean matches(final Object item)
                {
                    final Matcher<ImmutableElement> allOf = allOf(getChildMatcher(), valueIs(value));
                    return hasChild(allOf).matches(item);
                }

                @Override
                public void describeTo(final Description description)
                {
                    description.appendText("attribute with:");
                    getChildMatcher().describeTo(description);
                    description.appendText(" and ");
                    description.appendText("value=" + value);
                }
            };
        }
    }

    private static class HasChild extends BaseMatcher<ImmutableElement>
    {
        private final Matcher<ImmutableElement> childMatcher;
        private HasChild(final Matcher<ImmutableElement> childMatcher)
        {
            this.childMatcher = childMatcher;
        }
        @Override
        public boolean matches(final Object object)
        {
            if(object instanceof ImmutableElement)
            {
                final ImmutableElement parent = (ImmutableElement) object;
                return hasItem(this.childMatcher).matches(parent.children());
            }
            else
            {
                return false;
            }
        }
        public Matcher<ImmutableElement> getChildMatcher()
        {
            return this.childMatcher;
        }
        @Override
        public void describeTo(final Description description)
        {
            description.appendText("had child that matches: ");
            this.childMatcher.describeTo(description);
        }
    }

    public static Matcher<ImmutableElement> nameIs(final String name)
	{
        return new BaseMatcher<ImmutableElement>() {

			@Override
			public void describeMismatch(final Object item, final Description description)
			{
                super.describeMismatch(((ImmutableElement) item).name(), description);
			}

			@Override
			public boolean matches(final Object object)
			{
                if(object instanceof ImmutableElement)
                {
                    final ImmutableElement e = (ImmutableElement) object;
                    return Objects.equal(name, e.name());
                }
                else
                {
                    return false;
                }
			}

			@Override
			public void describeTo(final Description description)
			{
                description.appendValue("name=" + name);
			}
		};
	}

    public static Matcher<ImmutableElement> valueIs(final String value)
	{
        return new BaseMatcher<ImmutableElement>() {

			@Override
			public void describeMismatch(final Object item, final Description description)
			{
                super.describeMismatch(((ImmutableElement) item).value(), description);
			}

			@Override
			public boolean matches(final Object object)
			{
                if(object instanceof ImmutableElement)
                {
                final ImmutableElement e = (ImmutableElement) object;
				return Objects.equal(value, e.value());
                }
                else
                {
                    return false;
                }
			}

			@Override
			public void describeTo(final Description description)
			{
				description.appendValue(value);
			}
		};
	}

    public static Matcher<ImmutableElement> isLeaf()
	{
        return new BaseMatcher<ImmutableElement>() {

			@Override
			public boolean matches(final Object object)
			{
                if(object instanceof ImmutableElement)
                {
                    final ImmutableElement e = (ImmutableElement) object;
                    return e.children().isEmpty();
                }
                else
                {
                    return false;
                }
			}

			@Override
			public void describeTo(final Description description)
			{

			}
		};
	}

    public static Matcher<ImmutableElement> hasChild(final Matcher<ImmutableElement> childMatcher)
	{
        return new HasChild(childMatcher);
	}

    private static final BaseMatcher<NodeSelection> isAbsent = new BaseMatcher<NodeSelection>() {

        @Override
        public boolean matches(final Object object)
        {
            if(object instanceof NodeSelection)
            {
                final NodeSelection selections = (NodeSelection) object;
                return selections.all().isEmpty();
            }
            else
            {
                return false;
            }
        }

        @Override
        public void describeTo(final Description description)
        {
            description.appendText("absent");
        }
    };
    public static Matcher<NodeSelection> isAbsent()
    {
        return isAbsent;
    }

    public static HasAttr hasAttr(final String name)
    {
        return new HasAttr(nameIs("@" + name));
    }
}
