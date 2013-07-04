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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static xml.entity.parser.matcher.ImmutableMatchers.hasAttr;
import static xml.entity.parser.matcher.ImmutableMatchers.hasChild;
import static xml.entity.parser.matcher.ImmutableMatchers.nameIs;
import static xml.entity.parser.matcher.ImmutableMatchers.valueIs;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import xml.entity.CommonData;
import xml.entity.immutableelement.ImmutableElement;

public class TestParser
{
	private Parser parser;

	@Before public void setup()
	{
		parser = Parser.createDefault();
	}

	@Test
	public void testParseSimpleXml() throws SAXException, IOException
	{
        final ImmutableElement xmlElement = parser.parse(CommonData.simpleXml.getInput());
		assertThat(xmlElement, nameIs("Foo"));
        final ImmutableElement child = xmlElement.child("Bar");
		assertThat(child, nameIs("Bar"));
	}

	@Test
	public void testParseSimpleXmlWithWhiteSpace() throws SAXException, IOException
	{
        final ImmutableElement xmlElement = parser.parse(CommonData.simpleXmlWithWhitespace.getInput());
		assertThat(xmlElement, nameIs("Foo"));
		assertThat(xmlElement, hasChild(nameIs("Bar")));
	}

	@Test
	public void testParseWithWildCharacters() throws SAXException, IOException
	{
        final ImmutableElement xmlElement = parser.parse(CommonData.xmlWithWildCharacters.getInput());
		assertThat(xmlElement, nameIs("Foo"));
        assertEquals(3, xmlElement.children().size());
        assertThat(xmlElement, hasChild(valueIs("booo")));
        assertThat(xmlElement, hasChild(valueIs("moin")));
	}

	@Test
	public void testParseAttribute() throws SAXException, IOException
	{
        final ImmutableElement xmlElement = parser.parse(CommonData.withAttr.getInput());
        final ImmutableElement child = xmlElement.child("Bar");
		assertNotNull(child);
		assertThat(child, hasChild(nameIs("@name")));
		assertThat(child, hasChild(valueIs("baz")));
	}

	@Test
	public void testParseWithMultipeChildsWithSameName() throws SAXException, IOException
	{
        final ImmutableElement xmlElement = parser.parse(
                CommonData.xmlWithMultipleElementsWithSameName.getInput());
        final Collection<ImmutableElement> children = xmlElement.child("Collection").children();
		assertThat(children.size(), is(2));
        final Iterator<ImmutableElement> iterator = children.iterator();
        final ImmutableElement first = iterator.next();
		assertEquals("baz", first.child("#text").value());
        final ImmutableElement second = iterator.next();
		assertEquals("moin", second.child("#text").value());
	}

	@Test
	public void testParseTextWithAttribute() throws SAXException, IOException
	{
		parser.parse(CommonData.withAttrAndText.getInput());
	}

	@Test public void testIgnoreComments() throws SAXException, IOException
	{
        final ImmutableElement element = parser.parse(CommonData.withComments.getInput());
		assertThat(element, nameIs("Foo"));
		assertThat(element.children().size(), is(1));
		assertThat(element, hasChild(nameIs("Bar")));
	}

    @Test
    public void specialCharacters() throws Exception
    {
        final String specialCharacters = "!@#$%^*(()_+�";
        final ImmutableElement element = parser.parse(new StringReader("<Foo attr=\"" + specialCharacters + "\">" + specialCharacters + "</Foo>"));
        assertThat(element, valueIs(specialCharacters));
        assertThat(element, hasAttr("attr").withValue(specialCharacters));
        assertThat(element.children().size(), equalTo(2));
    }

    @Test
    public void specialUnicode() throws Exception
    {
        final String specialCharacters = "\\u0400\\u04FF";
        final ImmutableElement element = parser.parse(new StringReader("<Foo attr=\"" + specialCharacters + "\">" + specialCharacters + "</Foo>"));
        assertThat(element, valueIs(specialCharacters));
        assertThat(element, hasAttr("attr").withValue(specialCharacters));
        assertThat(element.children().size(), equalTo(2));
    }

    @Test
    public void escapedXmlInText() throws Exception
    {
        final ImmutableElement element = parser.parse(new StringReader("<Foo>&lt;a/&gt;&quot;&amp;&apos;</Foo>"));
        assertThat(element, valueIs("<a/>\"&'"));
        assertThat(element.children().size(), equalTo(1));
    }

    @Test
    public void escapedXmlInAttr() throws Exception
    {
        final ImmutableElement element = parser.parse(new StringReader("<Foo name=\"&lt;a/&gt;&quot;&amp;&apos;\"></Foo>"));
        assertThat(element, hasAttr("name").withValue("<a/>\"&'"));
        assertThat(element.children().size(), equalTo(1));
    }
}
