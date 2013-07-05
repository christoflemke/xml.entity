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
package xml.entity.parser;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElementFactory;
import xml.entity.mutableelement.ElementFactory;
import xml.entity.serilalize.Serializer;

import com.google.common.collect.ImmutableMap;

public class NullServiceContext implements ServiceContext
{
    private final Parser parser;
    private final Serializer serializer;
    private final ImmutableElementFactory factory;
    private final ElementFactory mutableFactory;
    @Inject
    public NullServiceContext(
            final Parser parser,
            final Serializer serializer,
            final ImmutableElementFactory factory,
            final ElementFactory mutableFactory)
    {
        super();
        this.parser = parser;
        this.serializer = serializer;
        this.factory = factory;
        this.mutableFactory = mutableFactory;
    }

    private NullServiceContext()
    {
        this.factory = ImmutableElementFactory.create();
        this.parser = Parser.create(this, this.factory);
        this.serializer = Serializer.create(this);
        this.mutableFactory = new ElementFactory(this.factory);
    }

    public static ServiceContext create()
    {
        return new NullServiceContext();
    }

    @Override
    public void addNamespaceDecl(@Nonnull final String prefix, @Nonnull final String url)
    {}

    @Override
    public String getUrlForPrefix(@Nonnull final String prefix)
    {
        return null;
    }

    @Override
    public void collectNamespaceDecls(final ImmutableElement element)
    {}

    @Override
    public Parser parser()
    {
        return this.parser;
    }

    @Override
    public Serializer serializer()
    {
        return this.serializer;
    }

    @Override
    @Nonnull
    public ImmutableElementFactory factory()
    {
        return this.factory;
    }

    @Override
    @Nonnull
    public ElementFactory mutableFactory()
    {
        return this.mutableFactory;
    }

    @Override
    public ImmutableMap<String, String> getNamespaceDecls(final ImmutableElement element)
    {
        return ImmutableMap.of();
    }

}
