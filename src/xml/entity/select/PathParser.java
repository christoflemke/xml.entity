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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElements;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Immutable
public class PathParser
{
	private PathParser()
	{}

	public static PathParser create()
	{
		return new PathParser();
	}

    static class PathExpr implements Predicate<ImmutableElement>
	{
        final Predicate<ImmutableElement> namePredicate;
        final ImmutableList<Predicate<ImmutableElement>> subExprs;
		final boolean marked;

        public PathExpr(
                final Predicate<ImmutableElement> namePredicate,
                final ImmutableList<Predicate<ImmutableElement>> subExprs, final boolean marked)
		{
			super();
			this.namePredicate = namePredicate;
			this.subExprs = subExprs;
			this.marked = marked;
		}

        @Override
        public boolean apply(@Nullable final ImmutableElement input)
		{
			return namePredicate.apply(input) && Predicates.and(subExprs).apply(input);
		}

		@Override public String toString()
		{
			return "/" + namePredicate + Joiner.on("").join(subExprs);
		}

		public boolean isMarked()
		{
			return marked;
		}
	}

    private static Predicate<ImmutableElement> starExpr = new Predicate<ImmutableElement>() {

        @Override
        public boolean apply(@Nullable final ImmutableElement paramT)
		{
			return true;
		}
	
		@Override
		public String toString() {
			return "*";
		};
	};
	
    static class AttrExpr implements Predicate<ImmutableElement>
	{
		private final String name;
		private final String value;
		public AttrExpr(final String name, final String value)
		{
			super();
			this.name = name;
			this.value = value;
		}
        @Override
        public boolean apply(@Nullable final ImmutableElement input)
		{
			return nameMatches(input) && valueMatches(input);
		}
        private boolean nameMatches(final ImmutableElement input)
		{
			return name.equals(input.name());
		}
        private boolean valueMatches(final ImmutableElement input)
		{
			return value == null ? true : value.equals(input.value());
		}

		@Override public String toString()
		{
			return name + "=" + value;
		}
	}

    static class TextExpr implements Predicate<ImmutableElement>
	{
		private final String value;

		public TextExpr(final String value)
		{
			super();
			this.value = value;
		}

        @Override
        public boolean apply(@Nullable final ImmutableElement input)
		{
			return value.equals(input.value());
		}

		@Override public String toString()
		{
			return "#text=" + value;
		}
	}

	static class Path
	{
		final ImmutableList<PathExpr> segments;

		Path(final ImmutableList<PathExpr> iterable)
		{
			super();
			segments = iterable;
		}

        PathExpr head()
        {
            return Iterables.getFirst(segments, null);
        }

		Path tail()
		{
			return new Path(segments.subList(1, segments.size()));
		}

		@Override public String toString()
		{
			return Joiner.on("").join(segments);
		}

        public boolean isEmpty()
        {
            return segments.isEmpty();
        }
	}

	public Path parse(String exp)
	{
		if(exp.startsWith("/"))
		{
			exp = exp.substring(1);
		}
		if(exp.endsWith("/"))
		{
			exp = exp.substring(0, exp.length() - 1);
		}
		final Iterable<String> childSelections = Arrays.asList(exp.split("/"));
		return new Path(ImmutableList.copyOf(Iterables.transform(childSelections, pathToSegment)));
	}
	private static final Function<String, PathExpr> pathToSegment = new Function<String, PathExpr>() {
		
		@Override public PathExpr apply(@Nullable final String subPath)
		{
            final Predicate<ImmutableElement> namePred = extractPathExpr(subPath);

            final Iterable<Predicate<ImmutableElement>> attrExprs = extractAttrExprs(subPath);
            final Iterable<Predicate<ImmutableElement>> textExprs = extractTextExprs(subPath);

            final Builder<Predicate<ImmutableElement>> builder = ImmutableList.builder();
			builder.addAll(toChildExprs(attrExprs));
			builder.addAll(toChildExprs(textExprs));
			return new PathExpr(namePred, builder.build(), subPath.startsWith("!"));
		}
	};

	private static final Pattern childSelectionPattern = Pattern.compile("!?([^#@]+).*");
	private static final Pattern childSelectionStarPattern = Pattern.compile("\\*.*");
    private static Predicate<ImmutableElement> extractPathExpr(final String subPath)
	{
		if(childSelectionStarPattern.matcher(subPath).matches())
		{
			return starExpr;
		}
		final Matcher matcher = childSelectionPattern.matcher(subPath);
		if(matcher.matches())
		{
			return ImmutableElements.byName(matcher.group(1));
		}
		else
		{
			throw new IllegalArgumentException("Unable to parse: " + subPath);
		}
	}

	private static final Pattern textSelectionPattern = Pattern.compile("[^#]*#text=([^#@]+).*");
    private static Iterable<Predicate<ImmutableElement>> extractTextExprs(final String subPath)
	{
		final Matcher matcher = textSelectionPattern.matcher(subPath);
        final List<Predicate<ImmutableElement>> textMatchers = Lists.newLinkedList();
		if(matcher.matches())
		{
			final String value = matcher.group(1);
			textMatchers.add(new TextExpr(value));
		}
		else if(subPath.contains("#"))
		{
			throw new IllegalArgumentException("Unable to parse as text: " + subPath);
		}
		return textMatchers;
	}

	private static final Pattern attrWithValuePattern = Pattern.compile("[^@]*(@\\w+)=([^@#]+)(.*)");
	private static final Pattern attrWithoutValuePattern = Pattern.compile("[^@]*(@\\w+)(.*)");
    private static Iterable<Predicate<ImmutableElement>> extractAttrExprs(String subPath)
	{
        final List<Predicate<ImmutableElement>> exprs = Lists.newLinkedList();
		while(subPath.contains("@"))
		{
			Matcher matcher = attrWithValuePattern.matcher(subPath);
			if(matcher.matches())
			{
				final String attrName = matcher.group(1);
				final String attValue = matcher.group(2);
				exprs.add(new AttrExpr(attrName, attValue));
				subPath = matcher.group(3);
			}
			else
			{
				matcher = attrWithoutValuePattern.matcher(subPath);
				if(matcher.matches())
				{
					final String name = matcher.group(1);
					subPath = matcher.group(2);
					exprs.add(new AttrExpr(name, null));
				}
				else
				{
					throw new IllegalArgumentException("Unable to parse: " + subPath);
				}
			}
		}
		return exprs;
	}

    private static ArrayList<Predicate<ImmutableElement>> toChildExprs(final Iterable<Predicate<ImmutableElement>> attrExprs)
	{
        return Lists.newArrayList(Iterables.transform(attrExprs, new Function<Predicate<ImmutableElement>, Predicate<ImmutableElement>>() {

            @Override
            public Predicate<ImmutableElement> apply(@Nullable final Predicate<ImmutableElement> input)
			{
				return ImmutableElements.hasChild(input);
			}
		}));
	}
}
