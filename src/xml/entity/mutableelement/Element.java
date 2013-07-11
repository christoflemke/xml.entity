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
    /**
     * The element name.
     * 
     * @return The element name
     */
    @Nonnull
    String name();

    /**
     * The element value.
     * 
     * @return The element value.
     */
    @Nullable
    String value();

    /**
     * The children of this node. Creates the node if its missing.
     * 
     * @return
     */
    @Nonnull
    Collection<Element> children();

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

    /**
     * Converts this element and its children to a tree of
     * {@link ImmutableElement}s.
     * 
     * @return An immutable deep copy of this element.
     */
    ImmutableElement immutableCopy();

    /**
     * Create this element if it does not exist
     * 
     * @return Either the existing element or a newly created element
     */
    @Nonnull
    Element create();

    /**
     * Add or access an attribute on this node. Creates the node if its missing.
     * 
     * @param name
     *            The name of the attribute node
     * @return The attribute node.
     */
    @Nonnull
    Element attribute(String name);

    /**
     * Set the value of the element. Creates the element if its missing
     * 
     * @param value
     *            The text content of this element
     * @return The node itself
     */
    @Nonnull
    Element value(String value);

    /**
     * Does this node exist.
     * 
     * @return true if the node exists.
     */
    boolean isMissing();

}
