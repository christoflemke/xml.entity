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
import java.util.List;

import javax.annotation.Nonnull;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElementFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


class InternalElement extends AbstractElement implements Element
{
    private final List<Element> children = Lists.newArrayList();
    InternalElement(
            @Nonnull final String name,
            final ImmutableElementFactory factory)
	{
        super(name, factory);
	}

	@Override
	public String value()
	{
        final String value = Joiner.on("").join(Iterables.filter(this.children, Elements.byName("#text")));
        return "".equals(value) ? null : value;
	}

	@Override
	public Element child(final String name)
	{
        final InternalElement child = new InternalElement(name, this.factory);
        children.add(child);
        return child;
	}


    @Override public Element value(final String value)
    {
        Iterables.removeIf(children(), Elements.byName("#text"));
        children().add(new Text(value, this.factory));
        return this;
    }

    @Override
    @Nonnull
    public Collection<Element> children()
    {
        return this.children;
    }

    @Override @Nonnull public Element copy()
    {
        final InternalElement copy = new InternalElement(name(), this.factory);
        copy.children().addAll(Collections2.transform(children(), Elements.copy));
        return copy;
    }

    @Override
    public ImmutableElement immutableCopy()
    {
        final ImmutableList<ImmutableElement> children = FluentIterable
                .from(children())
                .transform(Elements.immutableCopy)
                .toImmutableList();
        return this.factory.createNode(name(), children);
    }

    @Override @Nonnull public Element attribute(final String string)
    {
        final Optional<Element> matching = Iterables.tryFind(children(), Elements.byName("@" + string));
        if(matching.isPresent())
        {
            return matching.get();
        }
        else
        {
            final Attribute child = new Attribute(string, null, this.factory);
            children.add(child);
            return child;
        }
    }

    @Override public String toString()
    {
        return name() + this.children.toString();
    }
}