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

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static xml.entity.immutableelement.ImmutableElements.isAttribute;
import static xml.entity.immutableelement.ImmutableElements.isText;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElements;

import com.google.common.collect.ImmutableList;

class SerializerImpl implements Seriallizer
{
    @Inject
    public SerializerImpl()
    {
    }

    public static Seriallizer create()
    {
        return new SerializerImpl();
    }

    @Override
    public SerializationContext serialize(final ImmutableElement element)
	{
        return new SerializationContextImpl(element);
	}

    private class SerializationContextImpl implements SerializationContext
	{
        private final ImmutableElement element;

        public SerializationContextImpl(final ImmutableElement element)
		{
			this.element = element;
		}

        @Override
        public void toWriter(final Writer writer) throws XMLStreamException
		{
            final XMLStreamWriter streamWriter;
            try
            {
                streamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(writer);
            }
            catch(final Exception e)
            {
                throw new RuntimeException(e);
            }
            writeInternal(streamWriter, this.element);
            streamWriter.writeEndDocument();
            streamWriter.flush();
		}

        @Override
        public void toStream(final OutputStream stream, final Charset charset) throws XMLStreamException, IOException
        {
            final OutputStreamWriter writer = new OutputStreamWriter(stream, charset);
            toWriter(writer);
            writer.flush();
        }

        @Override
        public String toString()
		{
			final StringWriter writer = new StringWriter();
			try
			{
				toWriter(writer);
			}
            catch(final XMLStreamException e)
			{
				throw new RuntimeException(e);
			}
			return writer.toString();
		}

        private void writeInternal(final XMLStreamWriter streamWriter, final ImmutableElement current) throws XMLStreamException
		{
            final ImmutableList<ImmutableElement> nonAttrChildren = ImmutableList.copyOf(filter(current.children(), not(isAttribute())));
            if(nonAttrChildren.isEmpty())
            {
                streamWriter.writeEmptyElement(current.name());
            }
            else
            {
                streamWriter.writeStartElement(current.name());
            }
            for(final ImmutableElement attr : filter(current.children(), isAttribute()))
			{
                streamWriter.writeAttribute(attr.name().replaceFirst("@", ""), attr.value());
			}
            for(final ImmutableElement child : nonAttrChildren)
            {
                if(isText().apply(child))
                {
                    streamWriter.writeCharacters(child.value());
                }
                if(ImmutableElements.isInternal().apply(child))
                {
                    writeInternal(streamWriter, child);
                }
            }
            if(!nonAttrChildren.isEmpty())
            {
                streamWriter.writeEndElement();
            }
		}
	}
}
