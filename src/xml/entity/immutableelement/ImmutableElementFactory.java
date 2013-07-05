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

import javax.inject.Inject;

import xml.entity.select.DefaultSelector;
import xml.entity.select.PathParser;
import xml.entity.select.Selector;

import com.google.common.collect.ImmutableList;

public class ImmutableElementFactory
{
    private final Selector selector;

    @Inject
    public ImmutableElementFactory(final Selector selector)
    {
        this.selector = selector;
    }

    private ImmutableElementFactory()
    {
        this.selector = new DefaultSelector(PathParser.create(), this);
    }

    public static ImmutableElementFactory create()
	{
        return new ImmutableElementFactory();
    }

    public ImmutableElement createNode(final String name, final ImmutableList<ImmutableElement> children)
	{
        return new InternalElement(name, children, this.selector);
	}

	public ImmutableElement createAttr(final String name, final String value)
	{
        return new Attribute(name, value, this.selector);
	}

	public ImmutableElement createText(final String value)
	{
        return new Text(value, this.selector);
	}

    public ImmutableElement createLeaf(final String name)
    {
        final ImmutableList<ImmutableElement> of = ImmutableList.of();
        return new InternalElement(name, of, this.selector);
    }
}
