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
package xml.entity.mutableelement;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xml.entity.immutableelement.ImmutableElement;

public interface Element
{
    @Nonnull
    String name();
    @Nullable
    String value();
    @Nonnull
    Collection<Element> children();
    @Nonnull
    Collection<Element> children(String name);
    /**
     * This will always create a missing child
     */
    @Nonnull
    Element child(String name);

    /**
     * Create a deep copy of this element
     */
    @Nonnull
    Element copy();
    ImmutableElement immutableCopy();

    /**
     * Create this element if it does not exist
     * 
     * @return Either the existing element or a newly created element
     */
    @Nonnull
    Element create();
    @Nonnull
    Element attribute(String string);
    @Nonnull
    Element value(String value);

    boolean isMissing();

}
