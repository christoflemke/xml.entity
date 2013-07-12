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
package xml.entity.serilalize;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import xml.entity.immutableelement.ImmutableElementFactory;
import xml.entity.mutableelement.ElementFactory;

public class DefaultServiceContext implements ServiceContext
{
    private final Parser parser;
    private final Seriallizer serializer;
    private final ImmutableElementFactory factory;
    private final ElementFactory mutableFactory;
    @Inject
    public DefaultServiceContext(
            final Parser parser,
            final Seriallizer serializer,
            final ImmutableElementFactory factory,
            final ElementFactory mutableFactory)
    {
        super();
        this.parser = parser;
        this.serializer = serializer;
        this.factory = factory;
        this.mutableFactory = mutableFactory;
    }

    private DefaultServiceContext()
    {
        this.factory = ImmutableElementFactory.create();
        this.parser = ParserImpl.create(this.factory);
        this.serializer = SerializerImpl.create();
        this.mutableFactory = new ElementFactory(this.factory);
    }

    public static ServiceContext create()
    {
        return new DefaultServiceContext();
    }

    @Override
    public Parser parser()
    {
        return this.parser;
    }

    @Override
    public Seriallizer serializer()
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
}
