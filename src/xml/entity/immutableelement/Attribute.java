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

import javax.annotation.Nullable;

import xml.entity.select.Selector;

import com.google.common.base.Preconditions;

class Attribute extends AbstractElement implements ImmutableElement
{
    private final String value;

    Attribute(final String name, final String value, final Selector selector)
    {
        super("@" + name, selector);
        Preconditions.checkNotNull(value);
        this.value = value;
    }

    @Override @Nullable public String value()
    {
        return value;
    }

    @Override public String toString()
    {
        return name() + "=" + value;
    }
}
