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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElementFactory;

import com.google.common.base.Preconditions;

class Attribute extends AbstractElement implements Element
{
    private String value;

    Attribute(final String name, final String value, final ImmutableElementFactory factory)
    {
        super("@" + name, factory);
        Preconditions.checkNotNull(value);
        this.value = value;
    }

    @Override public Element value(final String value)
    {
        this.value = value;
        return this;
    }

    @Override @Nullable public String value()
    {
        return this.value;
    }

    @Override
    @Nonnull
    public Element copy()
    {
        return new Attribute(attributeName(), this.value, this.factory);
    }

    @Override
    public ImmutableElement immutableCopy()
    {
        return this.factory.createAttr(attributeName(), this.value);
    }

    private String attributeName()
    {
        return name().replaceFirst("@", "");
    }

    @Override public String toString()
    {
        return name() + "=" + this.value;
    }
}
