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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static xml.entity.immutableelement.ImmutableElements.attr;
import static xml.entity.immutableelement.ImmutableMatchers.hasAttr;

import org.hamcrest.Matcher;
import org.junit.Test;

import xml.entity.CommonData;
import xml.entity.immutableelement.ImmutableElement;
import xml.entity.mutableelement.Element;
import xml.entity.mutableelement.ElementFactory;
import xml.entity.serilalize.transform.AddNamespacesTransformation;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TestAddNamespaceTransformation
{
    private static final String XMLNS_XSI = "xmlns:xsi";
    private static final String XSI_URL = "http://www.w3.org/2001/XMLSchema-instance";
    final AddNamespacesTransformation xsiNsTransformation = new AddNamespacesTransformation(ImmutableMap.of("xsi", XSI_URL));

    @Test
    public void addNamespaceToElementWithoutNamespaces()
    {
        final ImmutableElement element = CommonData.singleElementWithSubNode.get().immutableCopy();

        final ImmutableElement transformed = xsiNsTransformation.apply(element);

        assertThat(transformed, hasAttr(XMLNS_XSI).withValue(XSI_URL));
    }

    @Test
    public void addOnlyMissing()
    {
        final Element node = ElementFactory.create().createNode("root");
        node.attribute(XMLNS_XSI).value(XSI_URL);
        final ImmutableElement element = node.immutableCopy();
        final Matcher<ImmutableElement> hasXsiNs = hasAttr(XMLNS_XSI).withValue(XSI_URL);
        assertThat(element, hasXsiNs);

        final ImmutableElement transformed = xsiNsTransformation.apply(element);

        assertThat(transformed, hasXsiNs);
        final ImmutableList<ImmutableElement> all = FluentIterable
                .from(transformed.children())
                .filter(attr(XMLNS_XSI))
                .toImmutableList();
        assertThat(all.size(), equalTo(1));
    }
}
