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

import java.util.Collection;

import javax.annotation.Nonnull;

import xml.entity.select.Selector;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;


class InternalElement extends AbstractElement implements ImmutableElement
{
    private final ImmutableList<ImmutableElement> children;
    InternalElement(
			@Nonnull final String name,
            @Nonnull final ImmutableList<ImmutableElement> children,
			@Nonnull final Selector selector)
	{
        super(name, selector);
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(children);
        Preconditions.checkNotNull(selector);
        this.children = children;
	}

	@Override
	public String value()
	{
        return Joiner.on("").join(Iterables.filter(children, ImmutableElements.byName("#text")));
	}

	@Override
	public ImmutableElement child(final String name)
	{
		final Collection<ImmutableElement> collection = children(name);
        return Iterables.getOnlyElement(collection);
	}


    @Override
    @Nonnull
    public ImmutableList<ImmutableElement> children()
    {
        return children;
    }

    @Override
    @Nonnull
    public ImmutableList<ImmutableElement> children(final String name)
    {
        return FluentIterable.from(children).filter(ImmutableElements.byName(name)).toImmutableList();
    }

    @Override public String toString()
    {
        return name() + children.toString();
    }
}