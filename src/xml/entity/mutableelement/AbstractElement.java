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

abstract class AbstractElement implements Element
{
    @Nonnull private final String name;

    AbstractElement(
            @Nonnull final String name)
    {
        super();
        this.name = name;
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    @Nonnull
    public Element create()
    {
        return this;
    }

    public boolean isMissing()
    {
        return false;
    };

    @Override
    @Nonnull
    public Collection<Element> children()
    {
        throw new IllegalStateException(name + " " + this + " can not have children");
    }

    @Override
    @Nonnull
    public Collection<Element> children(final String name)
    {
        throw new IllegalStateException(name + " " + this + " can not have children");
    }

    @Override
    @Nonnull
    public Element child(final String name)
    {
        throw new IllegalStateException(name + " " + this + " can not have children");
    }

    @Override
    @Nonnull
    public Element attribute(final String string)
    {
        throw new IllegalStateException(name + " " + this + " can not have children");
    }
}
