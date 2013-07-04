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

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.serilalize.Serializer;

import com.google.common.collect.ImmutableMap;

public class NullServiceContext implements ServiceContext
{

	@Override public void addNamespaceDecl(@Nonnull final String prefix, @Nonnull final String url)
	{}

	@Override public String getUrlForPrefix(@Nonnull final String prefix)
	{
		return null;
	}

    @Override
    public void collectNamespaceDecls(final ImmutableElement element)
	{}

	@Override public Parser createParser()
	{
		return Parser.createDefault();
	}

	@Override public Serializer createSerializer()
	{
		return Serializer.createDefault();
	}

    @Override
    public ImmutableMap<String, String> getNamespaceDecls(final ImmutableElement element)
    {
        return ImmutableMap.of();
    }

}
