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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Deque;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElementFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;


public class Parser
{
    private static class ImmutableHandler extends DefaultHandler implements ContentHandler
    {
        private final Logger logger = LoggerFactory.getLogger(getClass());
        private final ImmutableElementFactory factory = ImmutableElementFactory.create();
        private final Deque<ImmutableList.Builder<ImmutableElement>> currentChildren = Lists.newLinkedList();
        private ImmutableElement root = null;
        private final ServiceContext serviceContext;
        private StringBuilder cdata = null;

        public ImmutableHandler(final ServiceContext serviceContext)
        {
            this.serviceContext = serviceContext;
        }
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
        {
            logger.debug("startElement: {}", qName);
            if(cdata != null)
            {
                currentChildren.peek().add(factory.createText(cdata.toString()));
                cdata = null;
            }
            final Builder<ImmutableElement> children = new ImmutableList.Builder<ImmutableElement>();
            currentChildren.push(children);

            for(int i = 0; i < attributes.getLength(); i++)
            {
                final String name = attributes.getQName(i);
                final String value = attributes.getValue(i);
                final ImmutableElement element = factory.createAttr(name, value);
                children.add(element);

                if(name.startsWith("xmlns:"))
                {
                    serviceContext.addNamespaceDecl(name.replaceFirst("xmlns:", ""), value);
                }
            }
        }
        @Override
        public void endElement(final String uri, final String localName, final String qName)
        {
            logger.debug("endElement: {}", qName);
            final Builder<ImmutableElement> builder = currentChildren.pop();
            if(cdata != null)
            {
                builder.add(factory.createText(cdata.toString()));
                cdata = null;
            }
            final ImmutableElement element = factory.createNode(qName, builder.build());
            if(currentChildren.isEmpty())
            {
                root = element;
            }
            else
            {
                currentChildren.peek().add(element);
            }
        }

        @Override
        public void characters(final char[] ch, final int start, final int length)
        {
            final String text = charsToString(ch, start, length);
            logger.debug("characters: {}", text);
            if(cdata == null)
            {
                cdata = new StringBuilder(text.trim());
            }
            else
            {
                cdata.append(text);
            }
        }

        @Override
        public void skippedEntity(final String name)
        {
            logger.debug("skippedEntity: {}", name);
        }

        @Override
        public void ignorableWhitespace(final char[] ch, final int start, final int length)
        {
            final String ignored = charsToString(ch, start, length);
            logger.debug("ignorableWhitespace: {}", ignored);
        }

        private static String charsToString(final char[] ch, final int start, final int length)
        {
            return new String(ch, start, length);
        }

        public ImmutableElement root()
        {
            return root;
        }
        @Override
        public void error(final SAXParseException e)
        {
            logger.debug(e.getMessage());
        }
        @Override
        public void fatalError(final SAXParseException e)
        {
            logger.debug(e.getMessage());
        }
        @Override
        public void warning(final SAXParseException e)
        {
            logger.debug(e.getMessage());
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
	private final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
	private final ServiceContext serviceContext;
	private Parser(final ServiceContext serviceContext) throws SAXException, ParserConfigurationException
	{
		this.serviceContext = serviceContext;
	}

	public static Parser createDefault()
	{
		return createFromContext(new NullServiceContext());
	}
	public static Parser createFromContext(final ServiceContext serviceContext)
	{
		try
		{
			return new Parser(serviceContext);
		}
		catch(final SAXException e)
		{
			throw new RuntimeException("failed to create parser", e);
		}
		catch(final ParserConfigurationException e)
		{
			throw new RuntimeException("failed to create parser", e);
		}
	}

    public ImmutableElement parse(final Reader reader) throws SAXException, IOException
	{
        final ImmutableHandler handler = new ImmutableHandler(serviceContext);
        saxParser.parse(new InputSource(reader), handler);
        logger.debug("root: {}", handler.root());
        return handler.root();
	}

    public ImmutableElement parse(final String string) throws SAXException
    {
        try
        {
            return parse(new StringReader(string));
        }
        catch(final IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
