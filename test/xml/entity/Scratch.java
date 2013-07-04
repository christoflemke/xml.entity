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

import java.util.Comparator;

import org.junit.Test;

import xml.entity.immutableelement.ImmutableElement;
import xml.entity.select.dsl.DSL.Join;

public class Scratch {
	@Test
	public void testJoin()
	{
		final Join join = null;

		/*
		 * Task
		 *  * Organization/Id
		 *  * TaskGroup/Id
		 */
		final ImmutableElement task = null;
		/*
		 * Collection
		 *  * Organization/Id
		 */
		final ImmutableElement organizations = null;
		/*
		 * Collection
		 *  * TaskGoup/Id
		 */
		final ImmutableElement taskgroups = null;
		task.join()
			.join("/Task/Organization")
			.with( organizations.select().from("/Collection") )
			.on(path("/Organization/Id"))
			.and()
			.join("/Task/TaskGroup")
			.with( taskgroups.select().from("/Collection") )
			.on(path("/TaskGroup/Id"))
			.element();
	}

	private Comparator<ImmutableElement> path(final String path) {
		return new Comparator<ImmutableElement>() {

			@Override
			public int compare(final ImmutableElement o1, final ImmutableElement o2) {
				return 0;
			}
		};
	}
}
