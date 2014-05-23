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
package xml.entity.mutableelement;

import xml.entity.immutableelement.ImmutableElementFactory;

public class ElementFactory
{
    private final ImmutableElementFactory immutableFactory;

    public ElementFactory(final ImmutableElementFactory factory)
    {
        super();
        this.immutableFactory = factory;
    }

    /**
     * Create a new instace of this factory.
     * 
     * @return A new instance.
     */
    public static ElementFactory create()
	{
        return new ElementFactory(ImmutableElementFactory.create());
	}

    /**
     * Create a new element node with the given name.
     * 
     * @param name
     *            The element name.
     * @return A new node.
     */
	public Element createNode(final String name)
	{
        return new InternalElement(name, this.immutableFactory);
	}

    /**
     * Create a attribute.
     * 
     * @param name
     *            The name of the attribute.
     * @param value
     *            The value of the attribute.
     * @return A new attribute
     */
	public Element createAttr(final String name, final String value)
	{
        return new Attribute(name, value, this.immutableFactory);
	}

    /**
     * Create a new text node.
     * 
     * @param value
     *            The text content.
     * @return A new node.
     */
	public Element createText(final String value)
	{
        return new Text(value, this.immutableFactory);
	}
}
