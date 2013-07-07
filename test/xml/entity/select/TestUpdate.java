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
import static xml.entity.immutableelement.ImmutableElements.attr;
import static xml.entity.immutableelement.ImmutableElements.hasChild;
import static xml.entity.immutableelement.ImmutableMatchers.hasAttr;
import static xml.entity.immutableelement.ImmutableMatchers.hasChild;
import static xml.entity.immutableelement.ImmutableMatchers.isAbsent;
import static xml.entity.immutableelement.ImmutableMatchers.nameIs;
import static xml.entity.immutableelement.ImmutableMatchers.valueIs;
import static xml.entity.select.dsl.ExpectedMatches.exactlyOne;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import xml.entity.CommonData;
import xml.entity.immutableelement.ImmutableElement;
import xml.entity.mutableelement.Element;
import xml.entity.mutableelement.ElementFactory;
import xml.entity.select.dsl.DSLException;
import xml.entity.select.dsl.ExpectedMatches;
import xml.entity.select.dsl.NodeSelection;

public class TestUpdate
{
    @Rule public ExpectedException thrown = ExpectedException.none();
    private final ElementFactory factory = ElementFactory.create();

    @Test
    public void testAddAttr()
    {
        final ImmutableElement element = CommonData.singleElement.get().immutableCopy();

        final ImmutableElement updated = element.update().from("/Foo").setAttr("test", "bar").element();

        assertThat(updated, hasChild(nameIs("@test")));
        assertThat(updated, hasChild(valueIs("bar")));
        assertThat(updated.children().size(), equalTo(1));
    }

    @Test
    public void testSetAttr()
    {
        final ImmutableElement element = CommonData.singleElementWithAttr.get().immutableCopy();

        final ImmutableElement updated = element.update().from("/Foo").setAttr("name", "baz").element();

        assertThat(updated, hasChild(nameIs("@name")));
        assertThat(updated, hasChild(valueIs("baz")));
        assertThat(updated.children().size(), equalTo(1));
    }

    @Test
    public void testRemoveAttr()
    {
        final ImmutableElement element = CommonData.singleElementWithAttr.get().immutableCopy();

        final ImmutableElement updated = element.update().from("/Foo").setAttr("name", null).element();

        assertThat(updated, not(hasChild(nameIs("@name"))));
        assertThat(updated.children().size(), equalTo(0));
    }

    @Test
    public void testAddText()
    {
        final ImmutableElement element = CommonData.singleElement.get().immutableCopy();

        final ImmutableElement updated = element.update().from("/Foo").setText("bar").element();

        assertThat(updated, valueIs("bar"));
        assertThat(updated.children().size(), equalTo(1));
        assertThat(updated, hasChild(nameIs("#text")));
    }

    @Test
    public void testSetText()
    {
        final ImmutableElement element = CommonData.singleElementWithText.get().immutableCopy();

        final ImmutableElement updated = element.update().from("Foo").setText("baz").element();

        assertThat(updated, valueIs("baz"));
        assertThat(updated.children().size(), equalTo(1));
        assertThat(updated, hasChild(nameIs("#text")));
    }

    @Test
    public void testRemoveText()
    {
        final ImmutableElement element = CommonData.singleElementWithText.get().immutableCopy();

        final ImmutableElement updated = element.update().from("Foo").setText(null).element();

        assertThat(updated, valueIs(""));
        assertThat(updated.children().size(), equalTo(0));
        assertThat(updated, not(hasChild(nameIs("#text"))));
    }

    @Test
    public void testSetTextAndAttr()
    {
        final Element root = factory.createNode("Foo");

        final ImmutableElement updated = root.immutableCopy()
                .update()
                .from("Foo")
                .setText("Hello")
                .setAttr("name", "bar")
                .expect(exactlyOne())
                .element();

        assertThat(updated, valueIs("Hello"));
        assertThat(updated, hasAttr("name").withValue("bar"));
    }

    @Test
    public void testFilteredUpdate()
    {
        final ImmutableElement element = CommonData.collectionOfElmentsWithAttr.get().immutableCopy();

        final ImmutableElement updated = element.update()
                .from("/Collection/Bar")
                .setAttr("name", "c")
                .where(hasChild(attr("name").value("a"))).element();

        NodeSelection selector = updated.select().from("/Collection/Bar@name=a");
        assertThat(selector, isAbsent());

        selector = updated.select().from("/Collection/Bar@name=c");
        assertThat(selector, not(isAbsent()));

        selector = updated.select().from("/Collection/Bar@name=b");
        assertThat(selector, not(isAbsent()));
    }

    @Test
    public void testExpectMatch()
    {
        final ImmutableElement element = CommonData.collectionOfElmentsWithAttr.get().immutableCopy();

        element.update()
                .from("/Collection/Bar")
                .setAttr("name", "c")
                .expect(ExpectedMatches.exactlyOne())
                .where(hasChild(attr("name").value("a"))).element();
    }

    @Test
    public void testExpectMatchFail()
    {
        final ImmutableElement element = CommonData.collectionOfElmentsWithAttr.get().immutableCopy();
        thrown.expect(DSLException.class);
        element.update()
                .from("/Collection/Bar")
                .setText("foo")
                .expect(ExpectedMatches.exactlyOne())
                .where(hasChild(attr("name"))).element();
    }
}
