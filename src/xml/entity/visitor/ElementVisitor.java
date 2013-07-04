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
package xml.entity.visitor;

import xml.entity.immutableelement.ImmutableElement;

public interface ElementVisitor<E extends Exception>
{
    void onElementStart(ImmutableElement root) throws E;
    void onElementStop(ImmutableElement child) throws E;
    void onAttribute(ImmutableElement attribute) throws E;
    void onText(ImmutableElement textElement) throws E;
}
