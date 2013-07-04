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

import static org.junit.Assert.assertThat;
import static xml.entity.mutableelement.Elements.byName;
import static xml.entity.mutableelement.Elements.byValue;
import static xml.entity.mutableentity.MutableMatchers.nameIs;
import static xml.entity.mutableentity.MutableMatchers.valueIs;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import xml.entity.mutableelement.Element;
import xml.entity.mutableelement.ElementFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class TestElements
{
	private List<Element> elements;
	@Before public void setup()
	{
		elements = Lists.newArrayList();
        final ElementFactory factory = ElementFactory.create();
        elements.add(factory.createNode("Foo").value("a"));
        elements.add(factory.createNode("Foo").value("b"));
        elements.add(factory.createNode("Bar").value("c"));
	}

	@Test public void testFilterByName()
	{
		Element element = Iterables.find(elements, byName("Foo"));
		assertThat(element, nameIs("Foo"));
		assertThat(element, valueIs("a"));
		element = Iterables.find(elements, byName("Bar"));
		assertThat(element, nameIs("Bar"));
		assertThat(element, valueIs("c"));
	}

	@Test public void testFilterByValue()
	{
		Element element = Iterables.find(elements, byValue("a"));
		assertThat(element, nameIs("Foo"));
		element = Iterables.find(elements, byValue("b"));
		assertThat(element, nameIs("Foo"));
		element = Iterables.find(elements, byValue("c"));
		assertThat(element, nameIs("Bar"));
	}
}
