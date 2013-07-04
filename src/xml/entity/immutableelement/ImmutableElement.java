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
package xml.entity.immutableelement;

import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xml.entity.select.dsl.DSL;
import xml.entity.select.dsl.DSL.Join;

import com.google.common.collect.ImmutableList;

public interface ImmutableElement
{
    @Nonnull
    String name();
    @Nullable
    String value();
    @Nonnull
    ImmutableList<ImmutableElement> children();
    @Nonnull
    ImmutableList<ImmutableElement> children(String name);
    /**
     * Returns the single child contained in {@code XmlElement} with {@code name}
     * 
     * @throws NoSuchElementException
     *             if the element has no with that name
     * @throws IllegalArgumentException
     *             if the element has multiple children with that name
     */
    @Nonnull
    ImmutableElement child(String name);

    @Nonnull
    DSL.Select select();
    @Nonnull
    DSL.Insert insert();
    @Nonnull
    DSL.Update update();
    @Nonnull
    DSL.Delete delete();
	Join join();
}
