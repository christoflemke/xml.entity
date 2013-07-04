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
package xml.entity.immutableentity;

import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElements;
import xml.entity.mutableelement.Element;
import xml.entity.mutableelement.ElementFactory;

import com.google.common.base.Predicate;

public class TestImmutableElements
{
    private final ElementFactory factory = ElementFactory.create();
    @Rule public ErrorCollector collector = new ErrorCollector();

    @Test
    public void testHashPath()
    {
        final Element match = factory.createNode("Foo");
        match.child("Bar").create();

        final Predicate<ImmutableElement> expr = ImmutableElements.hasPath("/Foo/Bar/");
        collector.checkThat(expr.apply(match.immutableCopy()), equalTo(true));

        final Element mismatch = factory.createNode("Bar");
        mismatch.child("Foo").create();
        collector.checkThat(expr.apply(mismatch.immutableCopy()), equalTo(false));
    }
}
