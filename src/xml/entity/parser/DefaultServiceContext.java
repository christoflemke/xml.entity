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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElements;
import xml.entity.serilalize.Serializer;
import xml.entity.visitor.BaseVisitor;
import xml.entity.visitor.DFSTraversal;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class DefaultServiceContext implements ServiceContext
{

    private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final Pattern namespaceDeclPattern = Pattern.compile("@xmlns:(\\w+)");
	private static final Pattern namespacePrefixPattern = Pattern.compile("@(\\w+):(\\w+)");
	private final ConcurrentMap<String, String> map = Maps.newConcurrentMap();

	@Override public void addNamespaceDecl(@Nonnull final String prefix, @Nonnull final String url)
	{
        logger.debug("addNamespaceDecl: prefix: {}, url: {}", prefix, url);
		map.put(prefix, url);
	}

	@Override public String getUrlForPrefix(@Nonnull final String prefix)
	{
		return map.get(prefix);
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
                        logger.debug("add: {}, to: {}", prefix, element);
                        map.put(prefix, url);
                    }
                }
            }
        });

        return ImmutableMap.copyOf(map);
    }

	@Override public Parser createParser()
	{
		return Parser.createFromContext(this);
	}

	@Override public Serializer createSerializer()
	{
		return Serializer.createFromContext(this);
	}
}
