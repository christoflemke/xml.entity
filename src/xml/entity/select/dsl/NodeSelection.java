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
package xml.entity.select.dsl;

import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import xml.entity.immutableelement.ImmutableElement;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

public interface NodeSelection
{
	/**
	 * Returns the single child contained in {@code XmlElement}.
	 * 
	 * @throws NoSuchElementException
	 *             if the element has no children
	 * @throws IllegalArgumentException
	 *             if the element has multiple children
	 */
    @Nonnull
    ImmutableElement one();
    @Nonnull
    ImmutableList<ImmutableElement> all();
    @Nonnull
    FluentIterable<ImmutableElement> iterable();
}
