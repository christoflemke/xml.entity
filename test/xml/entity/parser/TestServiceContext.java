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
package xml.entity.parser;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import xml.entity.mutableelement.Element;
import xml.entity.mutableelement.ElementFactory;
import xml.entity.serilalize.Serializer;

public class TestServiceContext
{
	private ServiceContext context;
	private Parser parser;
	private Serializer serializer;
    private final ElementFactory factory = ElementFactory.create();

	@Before public void setup()
	{
        this.context = DefaultServiceContext.create();
        this.parser = this.context.parser();
		this.serializer = this.context.serializer();
	}

    /*
     * Test that namespaces are added when parsing
     */
    @Test
    public void testCollectDecls() throws SAXException
	{
        this.parser.parse("<Foo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>");
		assertThat(this.context.getUrlForPrefix("xsi"), is("http://www.w3.org/2001/XMLSchema-instance"));
	}

    /*
     * Test known namespaces are used for serialization
     */
    @Test
    public void testLearnNamespace() throws SAXException
	{
        this.parser.parse("<Foo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>"); // learn xsi
        final Element element = this.factory.createNode("Foo");
        element.child("Bar").attribute("xsi:nil").value("true");
        final String asString = this.serializer.serialize(element.immutableCopy()).toString();
		assertThat(asString, containsString("<Foo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"));
	}

    /*
     * Test that namespaces can be added manually
     */
    @Test
    public void testAddNamespace()
    {
        this.context.addNamespaceDecl("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        final Element element = this.factory.createNode("Foo");
        element.child("Bar").attribute("xsi:nil").value("true");
        final String asString = this.serializer.serialize(element.immutableCopy()).toString();
        assertThat(asString, containsString("<Foo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"));
    }
}
