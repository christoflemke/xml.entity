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

import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xml.entity.immutableelement.ImmutableElement;

import com.google.common.base.Predicate;

public interface DSL
{
    /**
     * Start an select
     */
    public interface Select
    {
        /**
         * Select an path in the element. An path must start from root.
         * 
         * @param path
         *            The path
         * @return The next state of the statement
         */
        @Nonnull
        NodeSelection from(@Nonnull String path);
    }

    /**
     * @author lemke
     */
    public interface WithWhere<T extends WithWhere<T>>
    {
        /**
         * Reduce the number of affected nodes by supplying and {@link Predicate}
         * 
         * @param expr
         *            An {@link Predicate} for an {@link ImmutableElement}
         * @return The next state of the statement
         */
        T where(@Nonnull Predicate<ImmutableElement> expr);
        /**
         * Limit the allow number of affected nodes. See {@link ExpectedMatches} for applicable predicates if the number of affected nodes does not match the expected expression the statement will result in an {@link DSLException}
         * 
         * @param matches
         *            An predicate that restricts how many nodes are allowed to be affected
         * @return The next state of the statement
         */
        T expect(@Nonnull ExpectedMatches matches);
        /**
         * Execute the statement. No actions will be performed be the call to this method.
         * 
         * @return The updated state of the element
         */
        ImmutableElement element();
    }

    public interface Delete
    {
        /**
         * Select an path in the element. An path must start from root.
         * 
         * @param path
         *            The path
         * @return The next state of the statement
         */
        NodeDelete from(@Nonnull String path);

        public interface NodeDelete extends WithWhere<NodeDelete>
        {
        }

    }

    public interface Insert
    {
        /**
         * Select an path in the element. An path must start from root.
         * 
         * @param path
         *            The path
         * @return The next state of the statement
         */
        public InsertInto into(@Nonnull String path);

        public interface InsertInto extends WithWhere<InsertInto>
        {
            /**
             * Insert these values at the chosen location(s) If the supplied path and expressions match multiple elements, all elements will be updated.
             * 
             * @param values
             *            The values that should be inserted, may be empty
             * @return The next state of the statement
             */
            public InsertInto values(@Nonnull List<ImmutableElement> values);
            /**
             * Insert this value at the chosen location(s) If the supplied path and expressions match multiple elements, all elements will be updated.
             * 
             * @param value
             *            The values that should be inserted
             * @return The next state of the statement
             */
            public InsertInto values(@Nonnull ImmutableElement value);
        }

    }

    public interface Update
    {
        /**
         * Select an path in the element. An path must start from root.
         * 
         * @param path
         *            The path
         * @return The next state of the statement
         */
        @Nonnull
        public NodeUpdate from(@Nonnull String path);

        public interface NodeUpdate extends WithWhere<NodeUpdate>
        {
            /**
             * Sets an attribute at the selected element(s) If the attribute already exists, it will be update with the given value. The attribute will be remove if the value is null
             * 
             * @param name
             *            The name of the attribute
             * @param value
             *            The attribute value, may be null
             * @return The next state of the statement
             */
            @Nonnull
            NodeUpdate setAttr(@Nonnull String name, @Nullable String value);
            /**
             * Set the text of the selected element(s) Will replace all existing text content. The text will be inserted after all other sub elements. If the text is null, all text content will be removed from the selected element(s).
             * 
             * @param text
             *            The text of the node
             * @return The next state of the statement
             */
            @Nonnull
            NodeUpdate setText(@Nullable String text);
            NodeUpdate replace(Predicate<ImmutableElement> replace, ImmutableElement with);
        }
    }
    
    public interface Join
    {
    	/**
    	 * Insert result at this path
    	 */
    	public JoinOn join(String path);
    	public interface JoinOn
    	{
    		/**
    		 * Insert from these nodes
    		 */
    		JoinWith with(NodeSelection nodeSelection);
    	}
    	public interface JoinWith
    	{
    		/**
    		 * 
    		 * 
    		 * @param comparator
    		 * @return A string used to compare nodes for equality
    		 */
    		JoinWithPath on(Comparator<ImmutableElement> comparator);
    	}
    	public interface JoinWithPath
    	{
    		/**
    		 * Chain another join operation
    		 * @return
    		 */
    		Join and();
    		/**
    		 * Return the result
    		 * @return
    		 */
    		ImmutableElement element();
    	}
    }
}
