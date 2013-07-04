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
package xml.entity;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import xml.entity.immutableentity.TestImmutableElements;
import xml.entity.mutableentity.TestElements;
import xml.entity.mutableentity.TestMutableEntity;
import xml.entity.parser.IdentityTest;
import xml.entity.parser.TestParser;
import xml.entity.parser.TestServiceContext;
import xml.entity.select.TestDelete;
import xml.entity.select.TestInsert;
import xml.entity.select.TestPathParser;
import xml.entity.select.TestSelect;
import xml.entity.select.TestUpdate;
import xml.entity.serialize.TestSerializer;

@RunWith(Suite.class)
@SuiteClasses({
               TestPathParser.class,
               TestParser.class,
               TestSelect.class,
               TestInsert.class,
               TestUpdate.class,
               TestDelete.class,
               TestMutableEntity.class,
               TestElements.class,
               TestSerializer.class,
               IdentityTest.class,
               TestServiceContext.class,
               TestImmutableElements.class
})
public class AllTests
{

}
