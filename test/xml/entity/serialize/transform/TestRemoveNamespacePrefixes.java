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
package xml.entity.serialize.transform;

import static org.junit.Assert.assertThat;
import static xml.entity.immutableelement.ImmutableMatchers.hasAttr;
import static xml.entity.immutableelement.ImmutableMatchers.hasChild;
import static xml.entity.immutableelement.ImmutableMatchers.nameIs;

import org.junit.Before;
import org.junit.Test;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElementFactory;
import xml.entity.mutableelement.Element;
import xml.entity.mutableelement.ElementFactory;
import xml.entity.serilalize.transform.RemoveNamespacePrefixes;

public class TestRemoveNamespacePrefixes
{
    private final ElementFactory factory = ElementFactory.create();
    private RemoveNamespacePrefixes tranformation;

    @Before
    public void setup()
    {
        tranformation = new RemoveNamespacePrefixes(ImmutableElementFactory.create());
    }

    @Test
    public void removeOnAttribute()
    {
        final Element node = factory.createNode("root");
        final String url = "http://somewhere";
        node.attribute("foo:bar").value(url);
        final ImmutableElement element = node.immutableCopy();

        final ImmutableElement transformed = tranformation.apply(element);

        assertThat(transformed, hasAttr("bar").withValue(url));
    }

    @Test
    public void removeOnElement()
    {
        final Element node = factory.createNode("root");
        node.child("foo:bar");
        final ImmutableElement element = node.immutableCopy();

        final ImmutableElement transformed = tranformation.apply(element);

        assertThat(transformed, hasChild(nameIs("bar")));
    }

    @Test
    public void removeOnRoot()
    {
        final Element node = factory.createNode("foo:root");
        final ImmutableElement element = node.immutableCopy();

        final ImmutableElement transformed = tranformation.apply(element);

        assertThat(transformed, nameIs("root"));
    }
}
