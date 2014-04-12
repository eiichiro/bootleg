/*
 * Copyright (C) 2011-2013 Eiichiro Uchiumi. All Rights Reserved.
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
package org.eiichiro.bootleg;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.eiichiro.bootleg.Pipeline;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class PipelineTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.eiichiro.bootleg.Pipeline#apply(java.lang.Object)}.
	 */
	@Test
	public void testApply() {
		Pipeline<List<String>> pipeline = new Pipeline<List<String>>();
		
		for (int i = 0; i < 5; i++) {
			final String string = String.valueOf(i);
			
			if (i % 2 == 0) {
				pipeline.set(new Function<List<String>, List<String>>() {

					public List<String> apply(List<String> strings) {
						strings.add(string);
						return strings;
					}
					
				});
			} else {
				pipeline.set(new Predicate<List<String>>() {
					
					public boolean apply(List<String> strings) {
						strings.add(string);
						return true;
					}
					
				});
			}
		}
		
		List<String> strings = pipeline.apply(new ArrayList<String>());
		assertThat(strings.size(), is(5));
		assertThat(strings.get(0), is("0"));
		assertThat(strings.get(1), is("1"));
		assertThat(strings.get(2), is("2"));
		assertThat(strings.get(3), is("3"));
		assertThat(strings.get(4), is("4"));
		
		pipeline = new Pipeline<List<String>>();
		
		for (int i = 0; i < 5; i++) {
			final String string = String.valueOf(i);
			
			if (i % 2 == 0) {
				if (i == 2) {
					pipeline.set(new Function<List<String>, List<String>>() {

						public List<String> apply(List<String> strings) {
							strings.add(string);
							return null;
						}
						
					});
				} else {
					pipeline.set(new Function<List<String>, List<String>>() {

						public List<String> apply(List<String> strings) {
							strings.add(string);
							return strings;
						}
						
					});
				}
				
			} else {
				pipeline.set(new Predicate<List<String>>() {
					
					public boolean apply(List<String> strings) {
						strings.add(string);
						return true;
					}
					
				});
			}
		}
		
		strings = new ArrayList<String>();
		assertNull(pipeline.apply(strings));
		assertThat(strings.size(), is(3));
		assertThat(strings.get(0), is("0"));
		assertThat(strings.get(1), is("1"));
		assertThat(strings.get(2), is("2"));
		
		pipeline = new Pipeline<List<String>>();
		
		for (int i = 0; i < 5; i++) {
			final String string = String.valueOf(i);
			
			if (i % 2 == 0) {
				if (i == 2) {
					pipeline.set(new Predicate<List<String>>() {
						
						public boolean apply(List<String> strings) {
							strings.add(string);
							return false;
						}
						
					});
				} else {
					pipeline.set(new Predicate<List<String>>() {
						
						public boolean apply(List<String> strings) {
							strings.add(string);
							return true;
						}
						
					});
				}
				
			} else {
				pipeline.set(new Function<List<String>, List<String>>() {

					public List<String> apply(List<String> strings) {
						strings.add(string);
						return strings;
					}
					
				});
			}
		}
		
		strings = pipeline.apply(new ArrayList<String>());
		assertThat(strings.size(), is(3));
		assertThat(strings.get(0), is("0"));
		assertThat(strings.get(1), is("1"));
		assertThat(strings.get(2), is("2"));
	}
	
	private static class Function1 implements Function<List<String>, List<String>> {

		int i;
		
		Function1(int i) {
			this.i = i;
		}
		
		/* (non-Javadoc)
		 * @see com.google.common.base.Function#apply(java.lang.Object)
		 */
		public List<String> apply(List<String> strings) {
			strings.add(String.valueOf(i));
			return strings;
		}
		
	}

	/**
	 * Test method for {@link org.eiichiro.bootleg.Pipeline#set(com.google.common.base.Function)}.
	 */
	@Test
	public void testStageFunctionOfTT() {
		Pipeline<List<String>> pipeline = new Pipeline<List<String>>();
		
		for (int i = 0; i < 5; i++) {
			pipeline.set(new Function1(i));
		}
		
		assertThat(pipeline.toString(), is(
				"Stage-0 [org.eiichiro.bootleg.PipelineTest$Function1] -> " +
				"Stage-1 [org.eiichiro.bootleg.PipelineTest$Function1] -> " +
				"Stage-2 [org.eiichiro.bootleg.PipelineTest$Function1] -> " +
				"Stage-3 [org.eiichiro.bootleg.PipelineTest$Function1] -> " +
				"Stage-4 [org.eiichiro.bootleg.PipelineTest$Function1]"));
		List<String> strings = pipeline.apply(new ArrayList<String>());
		assertThat(strings.size(), is(5));
		assertThat(strings.get(0), is("0"));
		assertThat(strings.get(1), is("1"));
		assertThat(strings.get(2), is("2"));
		assertThat(strings.get(3), is("3"));
		assertThat(strings.get(4), is("4"));
	}

	/**
	 * Test method for {@link org.eiichiro.bootleg.Pipeline#set(java.lang.String, com.google.common.base.Function)}.
	 */
	@Test
	public void testStageStringFunctionOfTT() {
		Pipeline<List<String>> pipeline = new Pipeline<List<String>>();
		
		for (int i = 0; i < 5; i++) {
			final String string = String.valueOf(i);
			pipeline.set(string, new Function<List<String>, List<String>>() {

				public List<String> apply(List<String> strings) {
					strings.add(string);
					return strings;
				}
				
			});
		}
		
		assertThat(pipeline.toString(), is("Stage-0 [0] -> Stage-1 [1] -> Stage-2 [2] -> Stage-3 [3] -> Stage-4 [4]"));
		List<String> strings = pipeline.apply(new ArrayList<String>());
		assertThat(strings.size(), is(5));
		assertThat(strings.get(0), is("0"));
		assertThat(strings.get(1), is("1"));
		assertThat(strings.get(2), is("2"));
		assertThat(strings.get(3), is("3"));
		assertThat(strings.get(4), is("4"));
		
		try {
			pipeline = new Pipeline<List<String>>();
			Function<List<String>, List<String>> function = null;
			pipeline.set("", function);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Parameter 'function' must not be [null]"));
			e.printStackTrace();
		}
	}

	private static class Predicate1 implements Predicate<List<String>> {

		int i;
		
		Predicate1(int i) {
			this.i = i;
		}
		
		/* (non-Javadoc)
		 * @see com.google.common.base.Predicate#apply(java.lang.Object)
		 */
		public boolean apply(List<String> strings) {
			strings.add(String.valueOf(i));
			return true;
		}
		
	}
	
	/**
	 * Test method for {@link org.eiichiro.bootleg.Pipeline#set(com.google.common.base.Predicate)}.
	 */
	@Test
	public void testStagePredicateOfT() {
		Pipeline<List<String>> pipeline = new Pipeline<List<String>>();
		
		for (int i = 0; i < 5; i++) {
			pipeline.set(new Predicate1(i));
		}
		
		assertThat(pipeline.toString(), is(
				"Stage-0 [org.eiichiro.bootleg.PipelineTest$Predicate1] -> " +
				"Stage-1 [org.eiichiro.bootleg.PipelineTest$Predicate1] -> " +
				"Stage-2 [org.eiichiro.bootleg.PipelineTest$Predicate1] -> " +
				"Stage-3 [org.eiichiro.bootleg.PipelineTest$Predicate1] -> " +
				"Stage-4 [org.eiichiro.bootleg.PipelineTest$Predicate1]"));
		List<String> strings = pipeline.apply(new ArrayList<String>());
		assertThat(strings.size(), is(5));
		assertThat(strings.get(0), is("0"));
		assertThat(strings.get(1), is("1"));
		assertThat(strings.get(2), is("2"));
		assertThat(strings.get(3), is("3"));
		assertThat(strings.get(4), is("4"));
	}

	/**
	 * Test method for {@link org.eiichiro.bootleg.Pipeline#set(java.lang.String, com.google.common.base.Predicate)}.
	 */
	@Test
	public void testStageStringPredicateOfT() {
		Pipeline<List<String>> pipeline = new Pipeline<List<String>>();
		
		for (int i = 0; i < 5; i++) {
			final String string = String.valueOf(i);
			pipeline.set(string, new Predicate<List<String>>() {

				public boolean apply(List<String> strings) {
					strings.add(string);
					return true;
				}
				
			});
		}
		
		assertThat(pipeline.toString(), is("Stage-0 [0] -> Stage-1 [1] -> Stage-2 [2] -> Stage-3 [3] -> Stage-4 [4]"));
		List<String> strings = pipeline.apply(new ArrayList<String>());
		assertThat(strings.size(), is(5));
		assertThat(strings.get(0), is("0"));
		assertThat(strings.get(1), is("1"));
		assertThat(strings.get(2), is("2"));
		assertThat(strings.get(3), is("3"));
		assertThat(strings.get(4), is("4"));
		
		try {
			pipeline = new Pipeline<List<String>>();
			Predicate<List<String>> predicate = null;
			pipeline.set("", predicate);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Parameter 'predicate' must not be [null]"));
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link org.eiichiro.bootleg.Pipeline#toString()}.
	 */
	@Test
	public void testToString() {
		Pipeline<List<String>> pipeline = new Pipeline<List<String>>();
		
		for (int i = 0; i < 5; i++) {
			if (i % 2 == 0) {
				pipeline.set(null, new Function<List<String>, List<String>>() {

					public List<String> apply(List<String> strings) {
						return strings;
					}
					
				});
			} else {
				pipeline.set(null, new Predicate<List<String>>() {
					
					public boolean apply(List<String> strings) {
						return true;
					}
					
				});
			}
		}
		
		assertThat(pipeline.toString(), is("Stage-0 -> Stage-1 -> Stage-2 -> Stage-3 -> Stage-4"));
		pipeline.apply(new ArrayList<String>());
		
		pipeline = new Pipeline<List<String>>();
		
		for (int i = 0; i < 5; i++) {
			if (i % 2 == 0) {
				pipeline.set("", new Function<List<String>, List<String>>() {

					public List<String> apply(List<String> strings) {
						return strings;
					}
					
				});
			} else {
				pipeline.set("", new Predicate<List<String>>() {
					
					public boolean apply(List<String> strings) {
						return true;
					}
					
				});
			}
		}
		
		assertThat(pipeline.toString(), is("Stage-0 -> Stage-1 -> Stage-2 -> Stage-3 -> Stage-4"));
		pipeline.apply(new ArrayList<String>());
	}

}
