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

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElementFactory;
import xml.entity.mutableelement.ElementFactory;
import xml.entity.serilalize.transform.AddNamespacesTransformation;

import com.google.common.collect.ImmutableMap;

public class XsiServiceContext implements ServiceContext
{
    private final ServiceContext context = DefaultServiceContext.create();
    private final AddNamespacesTransformation addXsi =
            new AddNamespacesTransformation(ImmutableMap.of("xsi", "http://www.w3.org/2001/XMLSchema-instance"));

    public static ServiceContext create()
    {
        return new XsiServiceContext();
    }

    @Override
    @Nonnull
    public Parser parser()
    {
        return context.parser();
    }

    @Override
    @Nonnull
    public Seriallizer serializer()
    {
        return new Seriallizer() {

            @Override
            public SerializationContext serialize(final ImmutableElement element)
            {
                final ImmutableElement transformed = addXsi.apply(element);
                return context.serializer().serialize(transformed);
            }
        };
    }

    @Override
    @Nonnull
    public ImmutableElementFactory factory()
    {
        return context.factory();
    }

    @Override
    @Nonnull
    public
    ElementFactory mutableFactory()
    {
        return context.mutableFactory();
    }

}
