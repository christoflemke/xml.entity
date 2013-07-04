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

import xml.entity.select.Selector;
import xml.entity.select.dsl.DSL;
import xml.entity.select.dsl.DSL.Join;

import com.google.common.collect.ImmutableList;

abstract class AbstractElement implements ImmutableElement
{
    @Nonnull private final String name;
    @Nonnull private final Selector selector;

    AbstractElement(
            @Nonnull final String name,
            @Nonnull final Selector selector)
    {
        super();
        this.name = name;
        this.selector = selector;
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    @Nonnull
    public ImmutableList<ImmutableElement> children()
    {
        throw new IllegalStateException("node " + this + " has no children");
    }

    @Override
    @Nonnull
    public ImmutableList<ImmutableElement> children(final String name)
    {
        throw new IllegalStateException("node " + this + " has no children");
    }

    @Override
    @Nonnull
    public ImmutableElement child(final String name)
    {
        throw new IllegalStateException("node " + this + " has no children");
    }

    @Override
    @Nonnull
    public DSL.Insert insert()
    {
        return selector.createInsert(this);
    }

    @Override
    @Nonnull
    public DSL.Update update()
    {
        return selector.createUpdate(this);
    }

    @Override
    @Nonnull
    public DSL.Delete delete()
    {
        return selector.createDelete(this);
    }

    @Override
    public DSL.Select select()
    {
        return selector.createSelect(this);
    }
    
    @Override
    public Join join() {
    	return null;
    }
}
