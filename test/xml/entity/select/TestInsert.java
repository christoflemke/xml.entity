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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static xml.entity.parser.matcher.ImmutableMatchers.isAbsent;

import org.junit.Test;

import xml.entity.CommonData;
import xml.entity.immutableelement.ImmutableElement;

public class TestInsert
{
    @Test
    public void testInsert()
    {
        final ImmutableElement element = CommonData.singleElementWithSubNode.get().immutableCopy();
        final ImmutableElement updated = element.insert().into("/Foo/Bar").values(CommonData.singleElement.get().immutableCopy()).element();

        assertThat(updated.select().from("/Foo/Bar/Foo"), not(isAbsent()));
        assertThat(element.select().from("/Foo/Bar/Foo"), isAbsent());
    }

    @Test
    public void testInsertDestinationNotFound()
    {
        final ImmutableElement element = CommonData.singleElementWithSubNode.get().immutableCopy();
        final ImmutableElement updated = element.insert().into("/Bar/Bar").values(CommonData.singleElement.get().immutableCopy()).element();

        assertThat(updated, equalTo(updated));
    }

    @Test
    public void testInsertMultipleDestinations()
    {
        final ImmutableElement element = CommonData.collectionOfElmentsWithAttr.get().immutableCopy();
        final ImmutableElement updated = element.insert().into("/Collection/Bar").values(CommonData.singleElement.get().immutableCopy()).element();

        assertThat(updated.select().from("/Collection/Bar/Foo").all().size(), equalTo(2));
    }

    @Test
    public void testInsertAtRoot()
    {
        final ImmutableElement element = CommonData.singleElement.get().immutableCopy();
        final ImmutableElement updated = element.insert().into("/Foo").values(CommonData.singleElement.get().immutableCopy()).element();

        assertThat(updated.select().from("/Foo/Foo"), not(isAbsent()));
        assertThat(element.select().from("/Foo/Foo"), isAbsent());
    }
}
