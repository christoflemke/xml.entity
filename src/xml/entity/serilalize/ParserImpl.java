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
package xml.entity.serilalize;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Deque;

import javax.inject.Inject;
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


class ParserImpl implements Parser
{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
    private final ImmutableElementFactory factory;

    @Inject
    ParserImpl(final ImmutableElementFactory factory) throws SAXException, ParserConfigurationException
    {
        this.factory = factory;
    }

    public static Parser create(final ImmutableElementFactory factory)
    {
        try
        {
            return new ParserImpl(factory);
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

    private static class ImmutableHandler extends DefaultHandler implements ContentHandler
    {
        private final Logger logger = LoggerFactory.getLogger(getClass());
        private final ImmutableElementFactory factory;
        private final Deque<ImmutableList.Builder<ImmutableElement>> currentChildren = Lists.newLinkedList();
        private ImmutableElement root = null;
        private StringBuilder cdata = null;

        public ImmutableHandler(final ImmutableElementFactory factory)
        {
            this.factory = factory;
        }
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
        {
            this.logger.debug("startElement: {}", qName);
            if(this.cdata != null)
            {
                this.currentChildren.peek().add(this.factory.createText(this.cdata.toString()));
                this.cdata = null;
            }
            final Builder<ImmutableElement> children = new ImmutableList.Builder<ImmutableElement>();
            this.currentChildren.push(children);

            for(int i = 0; i < attributes.getLength(); i++)
            {
                final String name = attributes.getQName(i);
                final String value = attributes.getValue(i);
                final ImmutableElement element = this.factory.createAttr(name, value);
                children.add(element);
            }
        }
        @Override
        public void endElement(final String uri, final String localName, final String qName)
        {
            this.logger.debug("endElement: {}", qName);
            final Builder<ImmutableElement> builder = this.currentChildren.pop();
            if(this.cdata != null)
            {
                builder.add(this.factory.createText(this.cdata.toString()));
                this.cdata = null;
            }
            final ImmutableElement element = this.factory.createNode(qName, builder.build());
            if(this.currentChildren.isEmpty())
            {
                this.root = element;
            }
            else
            {
                this.currentChildren.peek().add(element);
            }
        }

        @Override
        public void characters(final char[] ch, final int start, final int length)
        {
            final String text = charsToString(ch, start, length);
            this.logger.debug("characters: {}", text);
            if(this.cdata == null)
            {
                this.cdata = new StringBuilder(text.trim());
            }
            else
            {
                this.cdata.append(text);
            }
        }

        @Override
        public void skippedEntity(final String name)
        {
            this.logger.debug("skippedEntity: {}", name);
        }

        @Override
        public void ignorableWhitespace(final char[] ch, final int start, final int length)
        {
            final String ignored = charsToString(ch, start, length);
            this.logger.debug("ignorableWhitespace: {}", ignored);
        }

        private static String charsToString(final char[] ch, final int start, final int length)
        {
            return new String(ch, start, length);
        }

        public ImmutableElement root()
        {
            return this.root;
        }
        @Override
        public void error(final SAXParseException e)
        {
            this.logger.debug(e.getMessage());
        }
        @Override
        public void fatalError(final SAXParseException e)
        {
            this.logger.debug(e.getMessage());
        }
        @Override
        public void warning(final SAXParseException e)
        {
            this.logger.debug(e.getMessage());
        }
    }



    @Override
    public ImmutableElement parse(final Reader reader) throws SAXException, IOException
	{
        final ImmutableHandler handler = new ImmutableHandler(this.factory);
        this.saxParser.parse(new InputSource(reader), handler);
        this.logger.debug("root: {}", handler.root());
        return handler.root();
	}

    @Override
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
