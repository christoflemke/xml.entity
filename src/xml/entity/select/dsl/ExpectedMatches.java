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
package xml.entity.select.dsl;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

/**
 * Predicates used to define how many nodes are allowed to be affected by an update
 */
public abstract class ExpectedMatches implements Predicate<Integer>
{
    private static final class Any extends ExpectedMatches
    {
        @Override
        public boolean apply(@Nullable final Integer input)
        {
            return true;
        }
        @Override
        public String toString()
        {
            return "any";
        }
    }

    private static final class ExactlyOne extends ExpectedMatches
    {
        @Override
        public boolean apply(@Nullable final Integer input)
        {
            return Integer.valueOf(1).equals(input);
        }
        @Override
        public String toString()
        {
            return "exactlyOne";
        }
    }

    private ExpectedMatches()
    {}
    private static final ExpectedMatches exactlyOne = new ExactlyOne();

    private static final ExpectedMatches any = new Any();

    /**
     * Exactly one node is affected
     * 
     * @return
     */
    public static ExpectedMatches exactlyOne()
    {
        return exactlyOne;
    }
    /**
     * Any number of matches
     * 
     * @return
     */
    public static ExpectedMatches any()
    {
        return any;
    }
}