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
package xml.entity.select.matcher;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import xml.entity.immutableelement.ImmutableElement;

import com.google.common.base.Predicate;

public class PathMatcher
{
    public static Matcher<Predicate<ImmutableElement>> matches(final ImmutableElement element)
	{
        return new BaseMatcher<Predicate<ImmutableElement>>() {

			@Override public boolean matches(final Object item)
			{
				assertThat(item, instanceOf(Predicate.class));
				@SuppressWarnings("unchecked")
                final Predicate<ImmutableElement> pred = (Predicate<ImmutableElement>) item;
				return pred.apply(element);
			}

			@Override public void describeTo(final Description description)
			{
				description.appendValue(element);

			}
		};
	}
}
