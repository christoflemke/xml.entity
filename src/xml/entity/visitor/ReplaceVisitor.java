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
package xml.entity.visitor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.immutableelement.ImmutableElementFactory;
import xml.entity.immutableelement.ImmutableElements;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public abstract class ReplaceVisitor implements SelectionVisitor
{

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<ImmutableElement, ImmutableElement> replace = Maps.newHashMap();
    private ImmutableElement root;
    private final ImmutableElementFactory factory;

    public ReplaceVisitor(final ImmutableElementFactory factory)
    {
        super();
        this.root = null;
        this.factory = factory;
    }

    @Override
    public final void mismatch(final ImmutableElement element)
    {}

    @Override
    public final void enterChild(final ImmutableElement element)
    {}

    @Override
    public final void leaveChild(final ImmutableElement element)
    {
        if(!ImmutableElements.isInternal().apply(element))
        {
            return;
        }
        // test if replacements for children exist
        final SetView<ImmutableElement> intersection = Sets.intersection(Sets.newHashSet(element.children()), this.replace.keySet());
        if(intersection.isEmpty())
        {
            // always set root to the last node left
            if(this.replace.containsKey(element))
            {
                // if the root node has been replaced
                this.root = this.replace.get(element);
                logger.debug("replace root: {}, with: {}", element, this.root);
            }
            else
            {
                // if the node is unmodified
                this.root = element;
            }
        }
        else
        {
            // replace children
            final Builder<ImmutableElement> builder = ImmutableList.builder();
            for(final ImmutableElement e : element.children())
            {
                if(this.replace.containsKey(e))
                {
                    final ImmutableElement repacement = this.replace.remove(e);
                    logger.debug("replace: {}, with: {}", e, repacement);
                    if(repacement == null)
                    {
                        // skip
                    }
                    else
                    {
                        builder.add(repacement);
                    }
                }
                else
                {
                    builder.add(e);
                }
            }
            final ImmutableElement internalElement = factory.createNode(element.name(), builder.build());
            replace(element, internalElement);
            this.root = internalElement;
        }
    }

    public void replace(final ImmutableElement old, final ImmutableElement newElement)
    {
        logger.debug("add replacement: {}, with: {}", old, newElement);
        this.replace.put(old, newElement);
    }

    public ImmutableElement element()
    {
        return this.root;
    }
}