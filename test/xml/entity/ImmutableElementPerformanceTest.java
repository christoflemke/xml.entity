package xml.entity;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElementFactory;
import xml.entity.parser.NullServiceContext;
import xml.entity.parser.Parser;
import xml.entity.select.dsl.ExpectedMatches;
import xml.entity.serilalize.Serializer;

import com.google.common.base.Charsets;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

public class ImmutableElementPerformanceTest
{
    private static final Random rand = new Random();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public @Rule TemporaryFolder tmpFolder = new TemporaryFolder();
    private final Serializer serializer = NullServiceContext.create().serializer();
    private final Parser parser = NullServiceContext.create().parser();

    private final class CharDomain extends DiscreteDomain<Character>
    {
        @Override
        public Character minValue()
        {
            return Character.MIN_VALUE;
        }

        @Override
        public long distance(final Character c1, final Character c2)
        {
            return c2 - c1;
        }

        @Override
        public Character next(final Character c)
        {
            final int i = c + 1;
            return (char) i;
        }

        @Override
        public Character previous(final Character c)
        {
            final int i = c - 1;
            return (char) i;
        }
    }

    private static class Timer
    {
        long start;

        public Timer()
        {
            this.start = System.nanoTime();
        }

        public long elapsedIn(final TimeUnit timeUnit)
        {
            return timeUnit.convert(System.nanoTime() - this.start, TimeUnit.NANOSECONDS);
        }
    }
    final CharDomain domain = new CharDomain();

    @Test
    public void select()
    {
        final Range<Character> range = Ranges.closed('A', 'z');
        final int iterations = 10;
        logRange(range);
        final ImmutableElement root = createTree(range);

        final String path = createPath(range);

        this.logger.info("Path + " + path);
        Timer timer = new Timer();

        for(int i = 0; i < iterations; i++)
        {
            root.select().from(path).one();
        }
        final long time = timer.elapsedIn(TimeUnit.MILLISECONDS);
        this.logger.info("Select took {} ms", time);

        timer = new Timer();
        this.logger.info("Update");
        for(int i = 0; i < iterations; i++)
        {
            root.update().from(path).setAttr("foo", "bar").expect(ExpectedMatches.exactlyOne()).element();
        }
        this.logger.info("Update took {} ms", timer.elapsedIn(TimeUnit.MILLISECONDS));

        timer = new Timer();
        this.logger.info("Delete");
        for(int i = 0; i < iterations; i++)
        {
            root.delete().from(path).expect(ExpectedMatches.exactlyOne()).element();
        }
        this.logger.info("Delete took {} ms", timer.elapsedIn(TimeUnit.MILLISECONDS));
    }

    @Test
    public void parse() throws Exception
    {
        // increase this range by one and you will run out of MEM
        final Range<Character> range = Ranges.closed('a', 'j');
        logRange(range);
        final ImmutableElement root = createTree(range);

        final File newFile = this.tmpFolder.newFile();
        this.logger.info("Temp file: {}", newFile);
        final FileOutputStream stream = new FileOutputStream(newFile);
        this.logger.info("Start serialize");
        Timer timer = new Timer();
        this.serializer.serialize(root).toStream(stream, Charsets.UTF_8);
        stream.close();
        this.logger.info("Stop serialize : {} ms", timer.elapsedIn(TimeUnit.MILLISECONDS));
        final long sizeInMB = newFile.length() / (1024 * 1024);
        this.logger.info("File size: {} MB", sizeInMB);

        this.logger.info("start parse");
        timer = new Timer();
        this.parser.parse(new InputStreamReader(new FileInputStream(newFile), Charsets.UTF_8));
        this.logger.info("stop parse: {} ms", timer.elapsedIn(TimeUnit.MILLISECONDS));
    }

    private void logRange(final Range<Character> range)
    {
        final int size = size(range);
        this.logger.info("[{},{}]", range.lowerEndpoint(), range.upperEndpoint());
        this.logger.info("domain size: {}", size);
    }

    private int size(final Range<Character> range)
    {
        final ContiguousSet<Character> asSet = range.asSet(this.domain);
        final int size = asSet.size();
        return size;
    }

    private String createPath(final Range<Character> range)
    {
        final StringBuilder stringBuilder = new StringBuilder("/root");

        int i = size(range);
        for(char c1 = range.lowerEndpoint(); c1 <= range.upperEndpoint(); c1++)
        {
            stringBuilder.append('/');
            final int randInRange = rand.nextInt(i);
            final char c = (char) (range.lowerEndpoint() + randInRange);
            assertTrue(range.contains(c));
            stringBuilder.append(c);
            i--;
        }

        return stringBuilder.toString();
    }

    private ImmutableElement createTree(final Range<Character> range)
    {
        ImmutableList<ImmutableElement> children = ImmutableList.of();
        int nodesConstructed = 0;
        int i = size(range) - 1;
        final ImmutableElementFactory factory = ImmutableElementFactory.create();
        for(char c1 = range.lowerEndpoint(); c1 <= range.upperEndpoint(); c1++)
        {
            final Builder<ImmutableElement> builder = ImmutableList.builder();
            for(char c2 = range.lowerEndpoint(); c2 <= (range.upperEndpoint() - i); c2++)
            {
                final ImmutableElement node = factory.createNode(String.valueOf(c2), children);
                builder.add(node);
                nodesConstructed++;
            }
            children = builder.build();
            i--;
        }
        this.logger.info("Nodes Constructed: {}" + nodesConstructed);

        return factory.createNode("root", children);
    }
}
