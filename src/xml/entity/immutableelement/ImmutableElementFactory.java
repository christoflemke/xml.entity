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
package xml.entity.immutableelement;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import xml.entity.select.DefaultSelector;
import xml.entity.select.PathParser;
import xml.entity.select.Selector;

import com.google.common.collect.ImmutableList;

@Immutable
public class ImmutableElementFactory
{
    private final Selector selector;

    public ImmutableElementFactory(@Nonnull final Selector selector)
    {
        this.selector = selector;
    }

    private ImmutableElementFactory()
    {
        this.selector = new DefaultSelector(PathParser.create(), this);
    }

    /**
     * Create an instance of the default implementation
     * 
     * @return A new factory
     */
    public static ImmutableElementFactory create()
	{
        return new ImmutableElementFactory();
    }

    /**
     * Create an internal node.
     * 
     * @param name
     *            The element name.
     * @param children
     *            The children of the new node.
     * @return A node
     */
    public ImmutableElement createNode(
            @Nonnull final String name,
            @Nonnull final ImmutableList<ImmutableElement> children)
	{
        return new InternalElement(name, children, this.selector);
	}

    /**
     * Create an attribute node
     * 
     * @param name
     *            The name of the attribute
     * @param value
     *            The value of the attribute
     * @return A new attribute node
     */
    public ImmutableElement createAttr(
            @Nonnull final String name,
            @Nonnull final String value)
	{
        return new Attribute(name, value, this.selector);
	}

    /**
     * Create a text node.
     * 
     * @param value
     *            The text content of this node
     * @return A text node
     */
    public ImmutableElement createText(@Nonnull final String value)
	{
        return new Text(value, this.selector);
	}

    /**
     * Create a node without children
     * 
     * @param name
     *            The element name
     * @return A node
     */
    public ImmutableElement createLeaf(@Nonnull final String name)
    {
        final ImmutableList<ImmutableElement> of = ImmutableList.of();
        return new InternalElement(name, of, this.selector);
    }
}
