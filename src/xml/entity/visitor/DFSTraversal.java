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

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static xml.entity.immutableelement.ImmutableElements.isAttribute;
import static xml.entity.immutableelement.ImmutableElements.isText;
import xml.entity.immutableelement.ImmutableElement;

public class DFSTraversal
{
    public void visit(final ImmutableElement element, final SelectionVisitor selectionVisitor)
    {
        visit(element, new ElementVisitor() {

            @Override
            public void onElementStart(final ImmutableElement root)
            {
                selectionVisitor.enterChild(root);
                selectionVisitor.match(root);
            }

            @Override
            public void onElementStop(final ImmutableElement child)
            {
                selectionVisitor.leaveChild(child);
            }

            @Override
            public void onAttribute(final ImmutableElement attribute)
            {
                selectionVisitor.match(attribute);
            }

            @Override
            public void onText(final ImmutableElement textElement)
            {
                selectionVisitor.match(textElement);
            }
        });
    }

    public <E extends Exception> void visit(final ImmutableElement element, final ElementVisitor visitor)
	{
		visitor.onElementStart(element);
        for(final ImmutableElement child : filter(element.children(), isAttribute()))
		{
			visitor.onAttribute(child);
		}
        for(final ImmutableElement child : filter(element.children(), isText()))
		{
			visitor.onText(child);
		}
        for(final ImmutableElement child : filter(element.children(), and(not(isAttribute()), not(isText()))))
		{
			visit(child, visitor);
		}
		visitor.onElementStop(element);
	}
}
