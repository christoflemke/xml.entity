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

import static xml.entity.immutableelement.ImmutableElements.toName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xml.entity.immutableelement.ImmutableElement;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class AddNamespacesTransformation implements ElementTransformation
{
    private static final String XMLNS_PREFIX = "@xmlns:";
    private final ImmutableMap<String, String> prefixToUrl;
    private static final Predicate<ImmutableElement> isNamespaceDecl = new Predicate<ImmutableElement>() {

        @Override
        public boolean apply(@Nullable final ImmutableElement input)
        {
            return input.name().startsWith(XMLNS_PREFIX);
        }
    };

    private static final Function<String, String> namespaceDeclToPrefix = new Function<String, String>() {

        @Override
        @Nonnull
        public String apply(@Nonnull final String input)
        {
            return input.replaceFirst(XMLNS_PREFIX, "");
        }
    };

    public AddNamespacesTransformation(final ImmutableMap<String, String> prefixToUrl)
    {
        super();
        this.prefixToUrl = prefixToUrl;
    }

    @Override
    @Nonnull
    public ImmutableElement apply(@Nonnull final ImmutableElement input)
    {
        final ImmutableSet<String> declaredNamespaces = input
                .select()
                .from("/*/*/")
                .where(isNamespaceDecl)
                .iterable()
                .transform(toName())
                .transform(namespaceDeclToPrefix)
                .toImmutableSet();

        final SetView<String> missingNamespaces = Sets.difference(prefixToUrl.keySet(), declaredNamespaces);

        ImmutableElement result = input;
        for(final String missing : missingNamespaces)
        {
            final String attrName = "xmlns:" + missing;
            final String url = prefixToUrl.get(missing);
            result = result
                    .update()
                    .from("/*/")
                    .setAttr(attrName, url)
                    .element();
        }

        return result;
    }

}
