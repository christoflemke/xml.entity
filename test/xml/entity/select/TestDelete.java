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
package xml.entity.select;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static xml.entity.immutableelement.ImmutableElements.attr;
import static xml.entity.immutableelement.ImmutableElements.hasChild;
import static xml.entity.immutableelement.ImmutableMatchers.hasChild;
import static xml.entity.immutableelement.ImmutableMatchers.nameIs;
import static xml.entity.mutableentity.MutableMatchers.isAbsent;

import org.junit.Test;

import xml.entity.CommonData;
import xml.entity.immutableelement.ImmutableElement;

public class TestDelete
{
    @Test
    public void testDeleteAll()
    {
        final ImmutableElement element = CommonData.singleElementWithSubNode.get().immutableCopy();
        final ImmutableElement updated = element.delete().from("/Foo/*").element();
        assertThat(updated, not(hasChild(nameIs("Bar"))));
    }

    @Test
    public void testDeleteNothing()
    {
        final ImmutableElement element = CommonData.singleElementWithSubNode.get().immutableCopy();
        final ImmutableElement updated = element.delete().from("/Bar/*").element();
        assertThat(updated, hasChild(nameIs("Bar")));
    }

    @Test
    public void testDeleteWhere()
    {
        final ImmutableElement element = CommonData.collectionOfElmentsWithAttr.get().immutableCopy();
        final ImmutableElement updated = element.delete().from("/Collection/*").where(hasChild(attr("name").value("a"))).element();

        assertThat(updated.select().from("/Collection/Bar@name=a"), isAbsent());
        assertThat(updated.select().from("/Collection/Bar@name=b"), not(isAbsent()));
    }

    @Test
    public void testDeleteNothingWhere()
    {
        final ImmutableElement element = CommonData.collectionOfElmentsWithAttr.get().immutableCopy();
        final ImmutableElement updated = element.delete().from("/Collection/*").where(hasChild(attr("name").value("dasda"))).element();

        assertThat(updated.select().from("/Collection/Bar@name=a"), not(isAbsent()));
        assertThat(updated.select().from("/Collection/Bar@name=b"), not(isAbsent()));
    }

}
