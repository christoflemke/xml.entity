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

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElementFactory;

class MissingElement implements Element
{
    private final ImmutableElementFactory factory;
    private final Element parent;
    private final String name;
    private String value;

    MissingElement(final Element parent, final String name, final ImmutableElementFactory factory)
    {
        super();
        this.parent = parent;
        this.name = name;
        this.factory = factory;
    }

    protected Element getParent()
    {
        return this.parent;
    }

    @Override
    @Nonnull
    public String name()
    {
        return this.name;
    }
    @Override
    @Nullable
    public String value()
    {
        return null;
    }
    @Override
    @Nonnull
    public Collection<Element> children()
    {
        return create().children();
    }
    @Override
    @Nonnull
    public Collection<Element> children(final String name)
    {
        return create().children(name);
    }
    @Override
    @Nonnull
    public Element child(final String name)
    {
        return new MissingElement(this, name, this.factory);
    }
    @Override
    @Nonnull
    public Element copy()
    {
        return create().copy();
    }
    @Override
    public ImmutableElement immutableCopy()
    {
        return create().immutableCopy();
    }
    @Override
    @Nonnull
    public Element create()
    {
        final Element p = this.parent.create();
        final Element child;
        if(this.name.startsWith("@"))
        {
            child = new Attribute(this.name.replaceFirst("@", ""), this.value, this.factory);
        }
        else if(this.name.startsWith("#"))
        {
            child = new Text(this.value, this.factory);
        }
        else
        {
            child = new InternalElement(this.name, this.factory);
        }
        p.children().add(child);
        return child;
    }

    @Override
    @Nonnull
    public Element attribute(final String string)
    {
        final String attrName = "@" + string;
        return new MissingElement(this, attrName, this.factory);
    }
    @Override
    public Element value(final String value)
    {
        this.value = value;
        final Element node = create();
        node.value(value);
        return node;
    }
    @Override
    public boolean isMissing()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "Missing: " + this.name;
    }

}
