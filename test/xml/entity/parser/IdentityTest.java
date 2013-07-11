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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import xml.entity.CommonData;
import xml.entity.immutableelement.ImmutableElement;
import xml.entity.serilalize.Serializer;

import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

@RunWith(Parameterized.class)
public class IdentityTest
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Parameters public static Collection<Object[]> parameters()
	{
		return Arrays.asList(
				new Object[][] {
                                { CommonData.simpleXml, Boolean.TRUE },
                                { CommonData.simpleXmlWithWhitespace, Boolean.FALSE },
                                { CommonData.withAttr, Boolean.TRUE },
                                { CommonData.withAttrAndText, Boolean.TRUE },
                                { CommonData.xmlWithMultipleElementsWithSameName, Boolean.TRUE },
                                { CommonData.xmlWithWildCharacters, Boolean.TRUE },
                                { CommonData.xmlWithMultipleWildCharacters, Boolean.TRUE },
                                { CommonData.xmlWithHeader, Boolean.FALSE },
                                { CharStreams.newReaderSupplier("<Foo><Bar i=\"1\"/><Baz i=\"2\"/><Bar i=\"3\"/><Baz i=\"4\"/></Foo>"), Boolean.TRUE },
                                { CharStreams.newReaderSupplier("<Foo name=\"&lt;a/&gt;&quot;&amp;&apos;\">&lt;a/&gt;&quot;&amp;&apos;</Foo>"), Boolean.FALSE }
				});
	}

	private final InputSupplier<Reader> input;
	private final boolean expectIdentity;
    private final Parser parser = NullServiceContext.create().parser();
    private final Serializer serializer = NullServiceContext.create().serializer();

	public IdentityTest(final InputSupplier<Reader> input, final boolean expectIdentity)
	{
		super();
		this.input = input;
		this.expectIdentity = expectIdentity;
	}

	@Test public void testIdentity() throws SAXException, IOException
	{
		final String asString = CharStreams.toString(this.input);
		this.logger.debug("asString:   {}", asString);
        final ImmutableElement element = this.parser.parse(this.input.getInput());
        final String serialized = this.serializer.serialize(element).toString();
		this.logger.debug("serialized: {}", serialized);
		if(this.expectIdentity)
		{
			assertThat(serialized, is(asString));
		}
		else
		{
			assertThat(serialized, not(is(asString)));
		}
	}
}
