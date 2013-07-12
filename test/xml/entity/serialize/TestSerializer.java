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
package xml.entity.serialize;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import xml.entity.CommonData;
import xml.entity.immutableelement.ImmutableElement;
import xml.entity.serilalize.DefaultServiceContext;
import xml.entity.serilalize.Seriallizer;

import com.google.common.base.Charsets;

public class TestSerializer
{
    private final Seriallizer serializer = DefaultServiceContext.create().serializer();

	@Test public void testSingleElement()
	{
        final ImmutableElement element = CommonData.singleElement.get().immutableCopy();
		final String string = this.serializer.serialize(element).toString();
		assertThat(string, is("<Foo/>"));
	}

    @Test
    public void testToWriter() throws Exception
	{
        final ImmutableElement element = CommonData.singleElement.get().immutableCopy();

		final StringWriter writer = new StringWriter();
		this.serializer.serialize(element).toWriter(writer);

		assertThat(writer.toString(), is("<Foo/>"));
	}

    @Test
    public void testToStream() throws XMLStreamException, IOException
	{
        final ImmutableElement element = CommonData.singleElement.get().immutableCopy();

		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		this.serializer.serialize(element).toStream(stream, Charsets.UTF_8);

		final String actual = new String(stream.toByteArray(), Charsets.UTF_8);
		assertThat(actual, is("<Foo/>"));
	}

	@Test public void testSingleElementWithText()
	{
        final ImmutableElement element = CommonData.singleElementWithText.get().immutableCopy();
		final String string = this.serializer.serialize(element).toString();
		assertThat(string, is("<Foo>bar</Foo>"));
	}

	@Test public void testSingleElementWithAttr()
	{
        final ImmutableElement element = CommonData.singleElementWithAttr.get().immutableCopy();
		final String string = this.serializer.serialize(element).toString();
		assertThat(string, is("<Foo name=\"bar\"/>"));
	}

	@Test public void testSingleElementWithAttrAndText()
	{
        final ImmutableElement element = CommonData.singleElementWithAttrAndText.get().immutableCopy();
		final String string = this.serializer.serialize(element).toString();
		assertThat(string, is("<Foo name=\"bar\">baz</Foo>"));
	}

	@Test public void testSingleElementWithSubNode()
	{
        final ImmutableElement element = CommonData.singleElementWithSubNode.get().immutableCopy();
		final String string = this.serializer.serialize(element).toString();
		assertThat(string, is("<Foo><Bar/></Foo>"));
	}
}
