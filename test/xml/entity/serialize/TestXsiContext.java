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
package xml.entity.serialize;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.serilalize.ServiceContext;
import xml.entity.serilalize.XsiServiceContext;

public class TestXsiContext
{
    private ServiceContext context;

    @Before
    public void setup()
    {
        context = XsiServiceContext.create();
    }

    @Test
    public void addNamespaceOnSerialization()
    {
        final ImmutableElement element = context.factory().createLeaf("root");

        final String string = context.serializer().serialize(element).toString();
        assertThat(string, containsString("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""));
    }
}
