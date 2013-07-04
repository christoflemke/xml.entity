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
								{ CommonData.simpleXml, true },
								{ CommonData.simpleXmlWithWhitespace, false },
								{ CommonData.withAttr, true },
								{ CommonData.withAttrAndText, true },
								{ CommonData.xmlWithMultipleElementsWithSameName, true },
								{ CommonData.xmlWithWildCharacters, true },
                                { CommonData.xmlWithMultipleWildCharacters, true },
								{ CommonData.xmlWithHeader, false },
                                { CharStreams.newReaderSupplier("<Foo><Bar i=\"1\"/><Baz i=\"2\"/><Bar i=\"3\"/><Baz i=\"4\"/></Foo>"), true },
                                { CharStreams.newReaderSupplier("<Foo name=\"&lt;a/&gt;&quot;&amp;&apos;\">&lt;a/&gt;&quot;&amp;&apos;</Foo>"), false }
				});
	}

	private final InputSupplier<Reader> input;
	private final boolean expectIdentity;
	private final Parser parser;
	public IdentityTest(final InputSupplier<Reader> input, final boolean expectIdentity)
	{
		super();
		this.input = input;
		this.expectIdentity = expectIdentity;
		parser = Parser.createDefault();
	}

	@Test public void testIdentity() throws SAXException, IOException
	{
		final String asString = CharStreams.toString(input);
		logger.debug("asString:   {}", asString);
        final ImmutableElement element = parser.parse(input.getInput());
		final String serialized = Serializer.createDefault().serialize(element).toString();
		logger.debug("serialized: {}", serialized);
		if(expectIdentity)
		{
			assertThat(serialized, is(asString));
		}
		else
		{
			assertThat(serialized, not(is(asString)));
		}
	}
}
