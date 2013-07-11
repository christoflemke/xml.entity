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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static xml.entity.select.matcher.PathMatcher.matches;

import java.util.Iterator;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xml.entity.mutableelement.Element;
import xml.entity.mutableelement.ElementFactory;
import xml.entity.select.PathParser.Path;
import xml.entity.select.PathParser.PathExpr;

import com.google.common.collect.Iterables;
import com.google.common.collect.UnmodifiableIterator;

public class TestPathParser
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Rule public ExpectedException expected = ExpectedException.none();
	private static final PathParser parser = PathParser.create();
    ElementFactory factory = ElementFactory.create();

	@Test public void testSimpleChildSelect()
	{
		final Path path = parser.parse("/Foo/Bar/Baz");
		final List<PathExpr> segments = path.segments;
		assertThat(segments.size(), is(3));
		final Iterator<PathExpr> iterator = segments.iterator();
		assertThat(iterator.next().toString(), is("/Foo"));
		assertThat(iterator.next().toString(), is("/Bar"));
		assertThat(iterator.next().toString(), is("/Baz"));
	}
	@Test public void testSimpleChildSelectNotLeadingSlash()
	{
		final Path path = parser.parse("Foo/Bar/Baz");
		assertThat(path.segments.size(), is(3));
	}
	@Test public void testSimpleChildSelectTrailingSlash()
	{
		final Path path = parser.parse("/Foo/Bar/Baz/");
		assertThat(path.segments.size(), is(3));
	}
	@Test public void testMatchAttrEnd()
	{
		final Path path = parser.parse("/Foo/Bar@name=baz");
		assertThat(path.segments.size(), is(2));
		final Iterator<PathExpr> iterator = path.segments.iterator();
		assertThat(iterator.next().toString(), is("/Foo"));
		final PathExpr segment = iterator.next();
		assertThat(segment.toString(), startsWith("/Bar"));
        final Element matching = ElementFactory.create().createNode("Bar");
        matching.attribute("name").value("baz").create();
        final Element mismatchname = ElementFactory.create().createNode("Bar");
        mismatchname.attribute("name1").value("baz").create();
        final Element mismatchvalue = ElementFactory.create().createNode("Bar");
        mismatchvalue.attribute("name").value("baz1").create();
        assertThat(segment, matches(matching.immutableCopy()));
        assertThat(segment, not(matches(mismatchname.immutableCopy())));
        assertThat(segment, not(matches(mismatchvalue.immutableCopy())));
	}

	@Test public void testMatchAttrWithoutValue()
	{
		final Path path = parser.parse("/Foo@Bar");
		final PathExpr expr = Iterables.getOnlyElement(path.segments);
        final Element matching = factory.createNode("Foo");
        matching.attribute("Bar").value("sdas");
        assertThat(expr, matches(matching.immutableCopy()));
        final Element mismatch = factory.createNode("Foo");
        assertThat(expr, not(matches(mismatch.immutableCopy())));
	}

	@Test public void testMatchAttrMid()
	{
		final Path path = parser.parse("/Foo/Bar@name=baz/Moin");
		assertThat(path.segments.size(), is(3));
		final Iterator<PathExpr> iterator = path.segments.iterator();
		assertThat(iterator.next().toString(), is("/Foo"));
		assertThat(iterator.next().toString(), startsWith("/Bar"));
		assertThat(iterator.next().toString(), startsWith("/Moin"));
	}

	@Test public void testMatchText()
	{
		final Path path = parser.parse("/Foo/Bar#text=baz");
		assertThat(path.segments.size(), is(2));
		final Iterator<PathExpr> iterator = path.segments.iterator();
		assertThat(iterator.next().toString(), is("/Foo"));
		final PathExpr segment = iterator.next();
		assertThat(segment.toString(), startsWith("/Bar"));
        final Element matching = factory.createNode("Bar").value("baz");
        final Element mismatch = factory.createNode("Bar").value("asdasd");

        assertThat(segment, matches(matching.immutableCopy()));
        assertThat(segment, not(matches(mismatch.immutableCopy())));
	}

	@Test public void testMatchTextAndAttr()
	{
		final Path path = parser.parse("/Foo/Bar@name=moin#text=baz");
		assertThat(path.segments.size(), is(2));
		final Iterator<PathExpr> iterator = path.segments.iterator();
		assertThat(iterator.next().toString(), is("/Foo"));
		final PathExpr segment = iterator.next();
		assertThat(segment.toString(), startsWith("/Bar"));

        final Element matching = factory.createNode("Bar");
        matching.attribute("name").value("moin");
        matching.value("baz");
        final Element mismatchtext = factory.createNode("Bar");
        mismatchtext.attribute("name").value("moin");
        mismatchtext.value("baz1");
        final Element mismatchattr = factory.createNode("Bar");
        mismatchattr.attribute("name").value("moin1");
        mismatchattr.value("baz");

        assertThat(segment, matches(matching.immutableCopy()));
        assertThat(segment, not(matches(mismatchtext.immutableCopy())));
        assertThat(segment, not(matches(mismatchattr.immutableCopy())));
	}

	@Test public void testMatchTextAndAttr2()
	{
		final Path path = parser.parse("/Foo/Bar#text=baz@name=moin");
		assertThat(path.segments.size(), is(2));
		final Iterator<PathExpr> iterator = path.segments.iterator();
		assertThat(iterator.next().toString(), is("/Foo"));
		final PathExpr segment = iterator.next();
		assertThat(segment.toString(), startsWith("/Bar"));
        final Element matching = factory.createNode("Bar");
        matching.attribute("name").value("moin");
        matching.value("baz");
        final Element mismatchtext = factory.createNode("Bar");
        mismatchtext.attribute("name").value("moin");
        mismatchtext.value("baz1");
        final Element mismatchattr = factory.createNode("Bar");
        mismatchattr.attribute("name").value("moin1");
        mismatchattr.value("baz");

        assertThat(segment, matches(matching.immutableCopy()));
        assertThat(segment, not(matches(mismatchtext.immutableCopy())));
        assertThat(segment, not(matches(mismatchattr.immutableCopy())));
	}

	@Test public void testMark()
	{
		final Path path = parser.parse("/Foo/!Bar/Baz");
		final UnmodifiableIterator<PathExpr> iterator = path.segments.iterator();

		assertThat(iterator.next().isMarked(), is(false));
		final PathExpr marked = iterator.next();

		assertThat(marked.isMarked(), is(true));
		assertThat(marked.toString(), is("/Bar"));
		assertThat(iterator.next().isMarked(), is(false));
	}

	@Test public void testParseEmptyChildSelection()
	{
		expected.expect(IllegalArgumentException.class);
		final Path parse = parser.parse("/Foo//Bar");
		logger.debug(parse.toString());
	}

	@Test public void testParseHashInName()
	{
		expected.expect(IllegalArgumentException.class);
		final Path parse = parser.parse("/Foo/B#r/");
		logger.debug(parse.toString());
	}

	@Test public void testParsePathEndsWithAdd()
	{
		expected.expect(IllegalArgumentException.class);
		parser.parse("/Foo@");
	}

	@Test public void testParseStar()
	{
		final Path path = parser.parse("/Foo/*");
		assertThat(path.segments.size(), is(2));
		assertThat(path.toString(), is("/Foo/*"));
	}
	
    @Test
    public void testStartStart()
    {
        final Path path = parser.parse("/*/Foo");
        assertThat(path.segments.size(), is(2));
        assertThat(path.toString(), is("/*/Foo"));
    }

	@Test public void testParseStarWithAttr()
	{
		final Path path = parser.parse("/Foo/*@name=bar");
		assertThat(path.segments.size(), is(2));
		assertThat(path.toString(), is("/Foo/*@name=bar"));
		final PathExpr starSegment = Iterables.getLast(path.segments);
		assertThat(starSegment.subExprs.size(), is(1));
	}

	@Test public void testParseStarWithText()
	{
		final Path path = parser.parse("/Foo/*#text=bar");
		assertThat(path.segments.size(), is(2));
		assertThat(path.toString(), is("/Foo/*#text=bar"));
		final PathExpr starSegment = Iterables.getLast(path.segments);
		assertThat(starSegment.subExprs.size(), is(1));
	}
}
