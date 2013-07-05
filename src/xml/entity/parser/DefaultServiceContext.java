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

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElementFactory;
import xml.entity.immutableelement.ImmutableElements;
import xml.entity.mutableelement.ElementFactory;
import xml.entity.serilalize.Serializer;
import xml.entity.visitor.BaseVisitor;
import xml.entity.visitor.DFSTraversal;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class DefaultServiceContext implements ServiceContext
{
    private final Parser parser;
    private final Serializer serializer;
    private final ImmutableElementFactory factory;
    private final ElementFactory mutableFactory;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final Pattern namespaceDeclPattern = Pattern.compile("@xmlns:(\\w+)");
    private static final Pattern namespacePrefixPattern = Pattern.compile("@(\\w+):(\\w+)");
    private final ConcurrentMap<String, String> map = Maps.newConcurrentMap();

    @Inject
    public DefaultServiceContext(
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

    private DefaultServiceContext()
    {
        this.factory = ImmutableElementFactory.create();
        this.parser = Parser.create(this, this.factory);
        this.serializer = Serializer.create(this);
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
    public void addNamespaceDecl(@Nonnull final String prefix, @Nonnull final String url)
    {
        this.logger.debug("addNamespaceDecl: prefix: {}, url: {}", prefix, url);
        this.map.put(prefix, url);
    }

    @Override
    public String getUrlForPrefix(@Nonnull final String prefix)
    {
        return this.map.get(prefix);
    }

    @Override
    public void collectNamespaceDecls(final ImmutableElement element)
    {
        new DFSTraversal().visit(element, new BaseVisitor() {
            @Override
            public void onAttribute(final ImmutableElement attribute)
            {
                final Matcher matcher = namespaceDeclPattern.matcher(attribute.name());
                if(matcher.matches())
                {
                    addNamespaceDecl(matcher.group(1), attribute.value());
                }
            }
        });
    }
    public ImmutableMap<String, String> getNamespaceDecls(final ImmutableElement element)
    {
        final Map<String, String> map = Maps.newHashMap();
        new DFSTraversal().visit(element, new BaseVisitor() {
            @Override
            public void onAttribute(final ImmutableElement attribute)
            {
                if(attribute.name().startsWith("@xmlns:"))
                {
                    return;
                }
                final Matcher matcher = namespacePrefixPattern.matcher(attribute.name());
                if(matcher.matches())
                {
                    final String prefix = matcher.group(1);
                    final String url = DefaultServiceContext.this.map.get(prefix);
                    if((url != null) && !Iterables.any(element.children(), ImmutableElements.attr(prefix)))
                    {
                        DefaultServiceContext.this.logger.debug("add: {}, to: {}", prefix, element);
                        map.put(prefix, url);
                    }
                }
            }
        });

        return ImmutableMap.copyOf(map);
    }
}
