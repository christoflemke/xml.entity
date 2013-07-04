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
package xml.entity.mutableentity;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import xml.entity.mutableelement.Element;
import xml.entity.select.dsl.NodeSelection;

import com.google.common.base.Objects;

public class MutableMatchers
{
    public static Matcher<Element> nameIs(final String name)
    {
        return new BaseMatcher<Element>() {

            @Override
            public void describeMismatch(final Object item, final Description description)
            {
                super.describeMismatch(((Element) item).name(), description);
            }

            @Override
            public boolean matches(final Object object)
            {
                assertThat(object, instanceOf(Element.class));
                final Element e = (Element) object;
                return Objects.equal(name, e.name());
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendValue(name);
            }
        };
    }

    public static Matcher<Element> isMissing = new BaseMatcher<Element>() {

        @Override
        public boolean matches(final Object object)
        {
            assertThat(object, instanceOf(Element.class));
            final Element element = (Element) object;
            return element.isMissing();
        }

        @Override
        public void describeTo(final Description paramDescription)
        {

        }
    };

    public static Matcher<Element> valueIs(final String value)
    {
        return new BaseMatcher<Element>() {

            @Override
            public void describeMismatch(final Object item, final Description description)
            {
                super.describeMismatch(((Element) item).value(), description);
            }

            @Override
            public boolean matches(final Object object)
            {
                assertThat(object, instanceOf(Element.class));
                final Element e = (Element) object;
                return Objects.equal(value, e.value());
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendValue(value);
            }
        };
    }

    public static Matcher<Element> isLeaf()
    {
        return new BaseMatcher<Element>() {

            @Override
            public boolean matches(final Object object)
            {
                assertThat(object, instanceOf(Element.class));
                final Element e = (Element) object;
                return e.children().isEmpty();
            }

            @Override
            public void describeTo(final Description description)
            {

            }
        };
    }

    public static Matcher<Element> hasChild(final Matcher<Element> childMatcher)
    {
        return new BaseMatcher<Element>() {

            @Override
            public boolean matches(final Object object)
            {
                if(object instanceof Element)
                {
                    final Element parent = (Element) object;
                    return hasItem(childMatcher).matches(parent.children());
                }
                else
                {
                    return false;
                }
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendText("had child that matches: ");
                childMatcher.describeTo(description);
            }
        };
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
}
