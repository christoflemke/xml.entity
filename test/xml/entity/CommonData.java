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
package xml.entity;

import java.io.StringReader;

import xml.entity.mutableelement.Element;
import xml.entity.mutableelement.ElementFactory;

import com.google.common.base.Supplier;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

public class CommonData
{
	/*
	 * Readers
	 */
	public static final InputSupplier<StringReader> simpleXml =
			CharStreams.newReaderSupplier("<Foo><Bar>baz</Bar></Foo>");
	public static final InputSupplier<StringReader> simpleXmlWithWhitespace =
			CharStreams.newReaderSupplier("<Foo>\n" +
			"  <Bar>baz</Bar>\n" +
			"</Foo>");
	public static final InputSupplier<StringReader> xmlWithWildCharacters =
            CharStreams.newReaderSupplier("<Foo>booo<Bar>baz</Bar>moin</Foo>");
	public static final InputSupplier<StringReader> xmlWithMultipleWildCharacters =
			CharStreams.newReaderSupplier("<Foo>booo<Bar>baz</Bar>baaa</Foo>");
	public static final InputSupplier<StringReader> withAttrAndText =
			CharStreams.newReaderSupplier("<Foo><Bar name=\"baz\">moin</Bar></Foo>");
	public static final InputSupplier<StringReader> withAttr =
			CharStreams.newReaderSupplier("<Foo><Bar name=\"baz\"/></Foo>");
	public static final InputSupplier<StringReader> xmlWithMultipleElementsWithSameName =
			CharStreams.newReaderSupplier("<Foo><Collection><Bar>baz</Bar><Bar>moin</Bar></Collection></Foo>");
	public static final InputSupplier<StringReader> xmlWithHeader =
			CharStreams.newReaderSupplier("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Foo/>");
	public static final InputSupplier<StringReader> withComments =
			CharStreams.newReaderSupplier("<Foo><!-- Comment--><Bar/><!-- More Comments--></Foo>");
    public static final InputSupplier<StringReader> withCDATASimple =
            CharStreams.newReaderSupplier("<Foo><![CDATA[foo]]></Foo>");
    public static final InputSupplier<StringReader> withCDATASpecialChars =
            CharStreams.newReaderSupplier("<Foo><![CDATA[foo \n &\"'<>]]></Foo>");

	/*
	 * XmlElements
	 */
    private static final ElementFactory factory = ElementFactory.create();

	public static final Supplier<Element> singleElement = new Supplier<Element>() {
		public Element get()
		{

            return factory.createNode("Foo");
		};
	};

	public static final Supplier<Element> singleElementWithText = new Supplier<Element>() {
		public Element get()
		{
            final Element root = factory.createNode("Foo").value("bar");
            return root;
		};
	};

	public static final Supplier<Element> singleElementWithAttr = new Supplier<Element>() {

		@Override public Element get()
		{
            final Element root = factory.createNode("Foo");
            root.attribute("name").value("bar");
            return root;
		}
	};

	public static final Supplier<Element> singleElementWithAttrAndText = new Supplier<Element>() {

		@Override public Element get()
		{
            final Element root = factory.createNode("Foo");
            root.attribute("name").value("bar");
            root.value("baz");
            return root;
		}
	};

	public static final Supplier<Element> singleElementWithSubNode = new Supplier<Element>() {

		@Override public Element get()
		{
            final Element root = factory.createNode("Foo");
            root.child("Bar").create();
            return root;
		}
	};

    public static final Supplier<Element> collectionOfElmentsWithAttr = new Supplier<Element>() {

        @Override
        public Element get()
        {
            final Element root = factory.createNode("Collection");
            root.child("Bar").attribute("name").value("a");
            root.child("Bar").attribute("name").value("b");
            return root;
        }
    };
}
