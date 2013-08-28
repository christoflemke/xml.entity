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
package xml.entity.mutableentity;

import static org.hamcrest.CoreMatchers.not;
import static xml.entity.mutableentity.MutableMatchers.hasChild;
import static xml.entity.mutableentity.MutableMatchers.isLeaf;
import static xml.entity.mutableentity.MutableMatchers.isMissing;
import static xml.entity.mutableentity.MutableMatchers.nameIs;
import static xml.entity.mutableentity.MutableMatchers.valueIs;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import xml.entity.mutableelement.Element;
import xml.entity.mutableelement.ElementFactory;

public class TestMutableEntity
{
    private final ElementFactory factory = ElementFactory.create();
    public @Rule ErrorCollector collector = new ErrorCollector();
    public @Rule ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreateRoot()
    {
        final Element node = factory.createNode("Foo");
        collector.checkThat(node, nameIs("Foo"));
        collector.checkThat(node, valueIs(null));
        collector.checkThat(node, isLeaf());
        collector.checkThat(node, not(isMissing));
    }

    @Test
    public void testSubNode()
    {
        final Element root = factory.createNode("Bar");
        final Element node = root.child("Foo");
        collector.checkThat(node, nameIs("Foo"));
        collector.checkThat(node, valueIs(null));
        collector.checkThat(node, isLeaf());
        collector.checkThat(node, not(isMissing));
    }

    @Test
    public void testAttrOnMissing()
    {
        final Element root = factory.createNode("Foo");
        root.child("Bar").attribute("name").value("a");
        collector.checkThat(root, hasChild(nameIs("Bar")));
    }

    @Test
    public void testTextNode()
    {
        final Element root = factory.createNode("Bar");
        final Element node = root.value("baz");
        collector.checkThat(node, nameIs("Bar"));
        collector.checkThat(node, valueIs("baz"));
        collector.checkThat(node, not(isLeaf()));
        collector.checkThat(node, not(isMissing));
    }

    @Test
    public void testReplaceText()
    {
        final Element root = factory.createNode("Bar");
        root.value("adsdasdbaz");

        root.value("baz");

        collector.checkThat(root, nameIs("Bar"));
        collector.checkThat(root, valueIs("baz"));
        collector.checkThat(root, not(isLeaf()));
        collector.checkThat(root, not(isMissing));
    }

    @Test
    public void testAddTextNode()
    {
        final Element root = factory.createNode("Bar");
        root.children().add(factory.createText("baz"));
        collector.checkThat(root, nameIs("Bar"));
        collector.checkThat(root, valueIs("baz"));
        collector.checkThat(root, not(isLeaf()));
        collector.checkThat(root, not(isMissing));
    }

    @Test
    public void testTextNodeChangeValue()
    {
        final Element root = factory.createNode("Bar");
        final Element text = factory.createText("asdasdasdad");
        root.children().add(text);
        text.value("baz");
        collector.checkThat(root, nameIs("Bar"));
        collector.checkThat(root, valueIs("baz"));
        collector.checkThat(root, not(isLeaf()));
        collector.checkThat(root, not(isMissing));
    }

    @Test
    public void testAttrNode()
    {
        final Element root = factory.createNode("Bar");
        final Element node = root.attribute("name").value("Hej");
        collector.checkThat(root, nameIs("Bar"));
        collector.checkThat(root, valueIs(null));
        collector.checkThat(root, not(isLeaf()));
        collector.checkThat(root, not(isMissing));
        collector.checkThat(root, hasChild(nameIs("@name")));
        collector.checkThat(root, hasChild(valueIs("Hej")));
    }

    @Test
    public void testAddAttrNode()
    {
        final Element root = factory.createNode("Bar");
        root.children().add(factory.createAttr("name", "Hej"));
        collector.checkThat(root, nameIs("Bar"));
        collector.checkThat(root, valueIs(null));
        collector.checkThat(root, not(isLeaf()));
        collector.checkThat(root, not(isMissing));
        collector.checkThat(root, hasChild(nameIs("@name")));
        collector.checkThat(root, hasChild(valueIs("Hej")));
    }

    @Test
    public void testAttrNodeChangeValue()
    {
        final Element root = factory.createNode("Bar");
        final Element attr = factory.createAttr("name", "dasdasdasda");
        root.children().add(attr);
        attr.value("Hej");
        collector.checkThat(root, nameIs("Bar"));
        collector.checkThat(root, valueIs(null));
        collector.checkThat(root, not(isLeaf()));
        collector.checkThat(root, not(isMissing));
        collector.checkThat(root, hasChild(nameIs("@name")));
        collector.checkThat(root, hasChild(valueIs("Hej")));
    }

    @Test
    public void testReplaceAttr()
    {
        final Element root = factory.createNode("Foo");
        root.attribute("name").value("a");
        root.attribute("name").value("b");

        collector.checkThat(root, nameIs("Foo"));
        collector.checkThat(root, valueIs(null));
        collector.checkThat(root, not(isLeaf()));
        collector.checkThat(root, not(isMissing));
        collector.checkThat(root, hasChild(nameIs("@name")));
        collector.checkThat(root, hasChild(valueIs("b")));
    }

    @Test
    public void testCopy()
    {
        final Element root = factory.createNode("Foo");

        root.attribute("name").value("a");
        root.value("abc");

        final Element copy = root.copy();
        collector.checkThat(copy, nameIs("Foo"));
        collector.checkThat(copy, valueIs("abc"));
        collector.checkThat(copy, not(isLeaf()));
        collector.checkThat(copy, not(isMissing));
        collector.checkThat(copy, hasChild(nameIs("@name")));
        collector.checkThat(copy, hasChild(valueIs("a")));
        collector.checkThat(copy, hasChild(valueIs("abc")));
    }

    @Test
    public void testCopyUnchanged()
    {
        final Element root = factory.createNode("Foo");

        root.attribute("name").value("a");
        root.value("abc");

        final Element copy = root.copy();

        root.attribute("name").value("b");
        root.value("cda");

        collector.checkThat(copy, nameIs("Foo"));
        collector.checkThat(copy, valueIs("abc"));
        collector.checkThat(copy, not(isLeaf()));
        collector.checkThat(copy, not(isMissing));
        collector.checkThat(copy, hasChild(nameIs("@name")));
        collector.checkThat(copy, hasChild(valueIs("a")));
        collector.checkThat(copy, hasChild(valueIs("abc")));
    }

    @Test
    public void childrenOnAttr()
    {
        final Element node = factory.createAttr("asd", "asds");
        thrown.expect(IllegalStateException.class);
        node.children();
    }

    @Test
    public void childOnAttr()
    {
        final Element node = factory.createAttr("asd", "asds");
        thrown.expect(IllegalStateException.class);
        node.child("dasdas");
    }

    @Test
    public void attrOnAttr()
    {
        final Element node = factory.createAttr("asd", "asds");
        thrown.expect(IllegalStateException.class);
        node.attribute("asds");
    }

    @Test
    public void childrenOnText()
    {
        final Element node = factory.createText("dasdas");
        thrown.expect(IllegalStateException.class);
        node.children();
    }

    @Test
    public void childOnText()
    {
        final Element node = factory.createText("dasdas");
        thrown.expect(IllegalStateException.class);
        node.child("dsads");
    }

    @Test
    public void attrOnText()
    {
        final Element node = factory.createText("dasdas");
        thrown.expect(IllegalStateException.class);
        node.attribute("adsda");
    }
}
