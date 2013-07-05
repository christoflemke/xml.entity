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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static xml.entity.parser.matcher.ImmutableMatchers.hasChild;
import static xml.entity.parser.matcher.ImmutableMatchers.isLeaf;
import static xml.entity.parser.matcher.ImmutableMatchers.nameIs;
import static xml.entity.parser.matcher.ImmutableMatchers.valueIs;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;

import xml.entity.CommonData;
import xml.entity.immutableelement.ImmutableElement;
import xml.entity.parser.NullServiceContext;
import xml.entity.parser.Parser;
import xml.entity.parser.matcher.ImmutableMatchers;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

public class TestSelect
{
    private final Parser parser = NullServiceContext.create().parser();
	@Rule public ExpectedException expected = ExpectedException.none();
    @Rule public ErrorCollector errorCollector = new ErrorCollector();

    @Test
    public void testSelectRoot()
    {
        final ImmutableElement element = CommonData.singleElement.get().immutableCopy();

        ImmutableElement match = element.select().from("/Foo").one();
        this.errorCollector.checkThat(match, nameIs("Foo"));

        match = element.select().from("/*").one();
        this.errorCollector.checkThat(match, nameIs("Foo"));
    }

	@Test
	public void testSelectText() throws SAXException, IOException
	{
        final ImmutableElement element = this.parser.parse(CommonData.simpleXml.getInput());
        final ImmutableElement selected = element.select().from("/Foo/Bar").one();
		assertThat(selected, valueIs("baz"));
	}

	@Test
	public void testSelectAttr() throws SAXException, IOException
	{
        final ImmutableElement xmlElement = this.parser.parse(CommonData.withAttr.getInput());
        final ImmutableElement selected = xmlElement.select().from("/Foo/Bar@name").one();
		assertThat(selected, nameIs("Bar"));
		assertThat(selected, hasChild(nameIs("@name")));
		assertThat(selected, hasChild(valueIs("baz")));
	}

	@Test public void testSelectByAttrValue() throws SAXException, IOException
	{
        final ImmutableElement xmlElement = this.parser.parse(CommonData.withAttr.getInput());
        final ImmutableElement selected = xmlElement.select().from("Foo/Bar@name=baz").one();
		assertThat(selected, nameIs("Bar"));
		assertThat(selected, hasChild(nameIs("@name")));
		assertThat(selected, hasChild(valueIs("baz")));
	}

	@Test
	public void testSelectAttrWithText() throws SAXException, IOException
	{
        final ImmutableElement xmlElement = this.parser.parse(CommonData.withAttrAndText.getInput());
        final ImmutableElement selected = xmlElement.select().from("/Foo/Bar@name").one();
		assertThat(selected, nameIs("Bar"));
		assertThat(selected, ImmutableMatchers.hasChild(valueIs("baz")));
	}

	@Test
	public void testPartialSelect() throws SAXException, IOException
	{
        final ImmutableElement xmlElement = this.parser.parse(CommonData.withAttrAndText.getInput());
        final ImmutableElement selected = xmlElement.select().from("Foo/Bar").one();
		assertThat(selected, nameIs("Bar"));
		assertThat(selected, not(isLeaf()));
	}

	@Test
	public void testSelectWithMultipeResults() throws SAXException, IOException
	{
        final ImmutableElement element = this.parser.parse(CommonData.xmlWithMultipleElementsWithSameName.getInput());
        final ImmutableList<ImmutableElement> selected = element.select().from("Foo/Collection/*").all();
		assertThat(selected.size(), is(2));
		assertThat(selected, hasItem(nameIs("Bar")));
        assertThat(Collections2.filter(selected, xml.entity.immutableelement.ImmutableElements.byName("Bar")).size(), is(2));
		assertThat(selected, hasItem(valueIs("baz")));
		assertThat(selected, hasItem(valueIs("moin")));
	}

    @Test
    public void testSelectByValue()
	{

	}

    @Test
    public void testSelectValueNotFound()
	{

	}

    @Test
    public void testBackTrack()
	{

	}

    @Test
    public void testBackTrackFail()
	{

	}
    @Test
    public void testSelectFromWhere()
    {

    }
}
