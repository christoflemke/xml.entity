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
package xml.entity.serilalize.transform;

import javax.annotation.Nonnull;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElementFactory;
import xml.entity.immutableelement.ImmutableElements;
import xml.entity.visitor.DFSTraversal;
import xml.entity.visitor.ReplaceVisitor;

public class RemoveNamespacePrefixes implements ElementTransformation
{

    private final ImmutableElementFactory factory;

    public RemoveNamespacePrefixes(final ImmutableElementFactory factory)
    {
        super();
        this.factory = factory;
    }

    @Override
    @Nonnull
    public ImmutableElement apply(@Nonnull final ImmutableElement input)
    {
        final ReplaceVisitor visitor = new ReplaceVisitor(factory) {

            @Override
            public void match(final ImmutableElement element)
            {
                String name = element.name();
                if(name.contains(":"))
                {
                    name = name.replaceFirst("[^:]+:", "");
                    final ImmutableElement repacement;
                    if(ImmutableElements.isAttribute().apply(element))
                    {
                         repacement = factory.createAttr(name, element.value());
                    }
                    else
                    // must be internal
                    {
                        repacement = factory.createNode(name, element.children());
                    }
                    replace(element, repacement);
                }

            }
        };
        new DFSTraversal().visit(input, visitor);
        return visitor.element();
    }

}
