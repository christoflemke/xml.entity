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

/**
 * The classes defined in this package can be used to construct node trees
 * from scratch.
 * 
 * 
 * Example:
 * <pre>
 * {@code
 *  final Element root = factory.createNode("Foo");
 *
 *  root.attribute("name").value("a");
 *  root.value("abc");
 * }
 * </pre>
 * Creates:
 * <pre>
 * {@code
 *  <Foo name="a">abc</Foo>
 * }
 * </pre>
 * 
 */
package xml.entity.mutableelement;