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

import org.eiichiro.bootleg.Routing;
import org.eiichiro.bootleg.Verb;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class RoutingTest {

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
	 * Test method for {@link org.eiichiro.bootleg.Routing#add(java.lang.String, java.lang.Class, java.lang.String)}.
	 */
	@Test
	public void testAdd() {
		Routing routing = new Routing();
		routing.add("/path/to/endpoint/method", RoutingTestEndpoint.class, "method1");
		assertNotNull(routing.route("/path/to/endpoint/method"));
		assertTrue(routing.route("/path/to/endpoint/notfound").isEmpty());
		
		routing = new Routing();
		routing.add("/path/to/*", RoutingTestEndpoint.class, "method1");
		routing.add("/path/to/endpoint/method", RoutingTestEndpoint.class, "method2");
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(null).getName(), is("method2"));
		
		routing = new Routing();
		routing.add("/path/to/endpoint/m*", RoutingTestEndpoint.class, "method1");
		routing.add("/path/to/endpoint/method", RoutingTestEndpoint.class, "method2");
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(null).getName(), is("method1"));
		
		try {
			routing.add(null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Parameter 'pattern' must not be [null]"));
			e.printStackTrace();
		}
		
		try {
			routing.add("path/to/endpoint/method", null, null);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Parameter 'pattern' must start with [/]"));
			e.printStackTrace();
		}
		
		try {
			routing.add("/", null, null);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Parameter 'endpoint' must not be [null]"));
			e.printStackTrace();
		}
		
		try {
			routing.add("/", RoutingTestEndpoint.class, null);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Parameter 'method' must not be [null]"));
			e.printStackTrace();
		}
		
		try {
			routing.add("/path/to/endpoint/method", RoutingTestEndpoint.class, "notfound");
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is(
					"Method [notfound] does not exist on Web endpoint " +
					"[class org.eiichiro.bootleg.RoutingTestEndpoint]"));
			e.printStackTrace();
		}
		
		// 0.4.0
		routing = new Routing();
		routing.add(Verb.GET, "/path/to/endpoint/method", RoutingTestEndpoint.class, "method1");
		routing.add(Verb.POST, "/path/to/endpoint/method", RoutingTestEndpoint.class, "method2");
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.GET).getName(), is("method1"));
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.POST).getName(), is("method2"));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.PUT));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(null));
		
		routing = new Routing();
		routing.add(Verb.GET, "/path/to/endpoint/m*", RoutingTestEndpoint.class, "method1");
		routing.add(Verb.POST, "/path/to/endpoint/method", RoutingTestEndpoint.class, "method2");
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.GET).getName(), is("method1"));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.POST));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(null));
		assertThat(routing.route("/path/to/endpoint/method").get(1).getValue().get(Verb.POST).getName(), is("method2"));
		assertNull(routing.route("/path/to/endpoint/method").get(1).getValue().get(Verb.PUT));
		assertNull(routing.route("/path/to/endpoint/method").get(1).getValue().get(null));
		
		routing = new Routing();
		routing.add(Verb.GET, "/path/to/e*", RoutingTestEndpoint.class, "method1");
		routing.add(Verb.POST, "/path/to/endpoint/method", RoutingTestEndpoint.class, "method2");
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.POST).getName(), is("method2"));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.GET));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(null));
		assertThat(routing.route("/path/to/endpoint/method").get(1).getValue().get(Verb.GET).getName(), is("method1"));
		assertNull(routing.route("/path/to/endpoint/method").get(1).getValue().get(Verb.POST));
		assertNull(routing.route("/path/to/endpoint/method").get(1).getValue().get(null));
		
		routing = new Routing();
		routing.add(Verb.GET, "/path/to/endpoint/method", RoutingTestEndpoint.class, "method1");
		routing.add(Verb.GET, "/path/to/endpoint/method", RoutingTestEndpoint.class, "method2");
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.GET).getName(), is("method2"));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.POST));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(null));
		
		routing = new Routing();
		routing.add(Verb.GET, "/path/to/endpoint/m*", RoutingTestEndpoint.class, "method1");
		routing.add(Verb.GET, "/path/to/endpoint/method", RoutingTestEndpoint.class, "method2");
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.GET).getName(), is("method1"));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.POST));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(null));
		assertThat(routing.route("/path/to/endpoint/method").get(1).getValue().get(Verb.GET).getName(), is("method2"));
		assertNull(routing.route("/path/to/endpoint/method").get(1).getValue().get(Verb.POST));
		assertNull(routing.route("/path/to/endpoint/method").get(1).getValue().get(null));
		
		routing = new Routing();
		routing.add(Verb.GET, "/path/to/e*", RoutingTestEndpoint.class, "method1");
		routing.add(Verb.GET, "/path/to/endpoint/method", RoutingTestEndpoint.class, "method2");
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.GET).getName(), is("method2"));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.POST));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(null));
		assertThat(routing.route("/path/to/endpoint/method").get(1).getValue().get(Verb.GET).getName(), is("method1"));
		assertNull(routing.route("/path/to/endpoint/method").get(1).getValue().get(Verb.POST));
		assertNull(routing.route("/path/to/endpoint/method").get(1).getValue().get(null));
		
		routing = new Routing();
		routing.add(Verb.GET, "/path/to/endpoint/method", RoutingTestEndpoint.class, "method1");
		routing.add("/path/to/endpoint/method", RoutingTestEndpoint.class, "method2");
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.GET).getName(), is("method1"));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.POST));
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(null).getName(), is("method2"));
		
		routing = new Routing();
		routing.add(Verb.GET, "/path/to/endpoint/m*", RoutingTestEndpoint.class, "method1");
		routing.add("/path/to/endpoint/method", RoutingTestEndpoint.class, "method2");
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.GET).getName(), is("method1"));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.POST));
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(null));
		assertNull(routing.route("/path/to/endpoint/method").get(1).getValue().get(Verb.POST));
		assertThat(routing.route("/path/to/endpoint/method").get(1).getValue().get(null).getName(), is("method2"));
		
		routing = new Routing();
		routing.add(Verb.GET, "/path/to/e*", RoutingTestEndpoint.class, "method1");
		routing.add("/path/to/endpoint/method", RoutingTestEndpoint.class, "method2");
		assertNull(routing.route("/path/to/endpoint/method").get(0).getValue().get(Verb.GET));
		assertThat(routing.route("/path/to/endpoint/method").get(0).getValue().get(null).getName(), is("method2"));
		assertThat(routing.route("/path/to/endpoint/method").get(1).getValue().get(Verb.GET).getName(), is("method1"));
		assertNull(routing.route("/path/to/endpoint/method").get(1).getValue().get(Verb.POST));
		assertNull(routing.route("/path/to/endpoint/method").get(1).getValue().get(null));
	}

	/**
	 * Test method for {@link org.eiichiro.bootleg.Routing#ignore(java.lang.String[])}.
	 */
	@Test
	public void testIgnore() {
		Routing routing = new Routing();
		routing.ignore("png", "*.png", "png.*");
		assertTrue(routing.ignores("png"));
		assertTrue(routing.ignores("pngpng.png"));
		assertTrue(routing.ignores("png.pngpng"));
		assertFalse(routing.ignores("pngpng"));
		
		try {
			String[] strings = null;
			routing.ignore(strings);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Parameter 'patterns' must not be [null]"));
			e.printStackTrace();
		}
		
		try {
			String[] strings = {"png", null};
			routing.ignore(strings);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Parameter 'patterns' must not include [null] or empty entry"));
			e.printStackTrace();
		}
		
		try {
			String[] strings = {"png", ""};
			routing.ignore(strings);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Parameter 'patterns' must not include [null] or empty entry"));
			e.printStackTrace();
		}
	}

}
