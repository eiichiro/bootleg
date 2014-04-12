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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.testing.ServletTester;
import org.eiichiro.bootleg.DefaultConfiguration;
import org.eiichiro.bootleg.GenericRequest;
import org.eiichiro.bootleg.GenericResponse;
import org.eiichiro.bootleg.MediaType;
import org.eiichiro.bootleg.Pipeline;
import org.eiichiro.bootleg.Routing;
import org.eiichiro.bootleg.WebContext;
import org.eiichiro.bootleg.json.JSONRequest;
import org.eiichiro.bootleg.json.JSONResponse;
import org.eiichiro.bootleg.xml.XMLRequest;
import org.eiichiro.bootleg.xml.XMLResponse;
import org.eiichiro.reverb.system.Environment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class DefaultConfigurationTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {}

	@Test
	public void testEndpoints() throws Exception {
		DefaultConfiguration configuration = new DefaultConfiguration();
		Collection<String> endpoints = new ArrayList<>();
		
		for (Class<?> endpoint : configuration.endpoints()) {
			endpoints.add(endpoint.getName());
		}
		
		assertTrue(endpoints.contains(DefaultConfigurationTestEndpoint1.class.getName()));
		assertTrue(!endpoints.contains(DefaultConfigurationTestEndpoint2.class.getName()));
		assertTrue(!endpoints.contains("org.eiichiro.bootleg.DefaultConfigurationTestEndpoint3"));
		assertTrue(!endpoints.contains("org.eiichiro.bootleg.DefaultConfigurationTestEndpoint4"));
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(new ClassLoader(loader) {});
		ServletTester tester = new ServletTester();
		tester.setContextPath("/bootleg");
		tester.addFilter(BootlegFilter.class, "/*", 0);
		tester.addServlet(DefaultServlet.class, "/");
		tester.start();
		configuration = new DefaultConfiguration();
		ServletContextHandler context = tester.getContext();
		configuration.init(context.getServletContext());
		endpoints = new ArrayList<>();
		
		for (Class<?> endpoint : configuration.endpoints()) {
			endpoints.add(endpoint.getName());
		}
		
		assertTrue(endpoints.contains(DefaultConfigurationTestEndpoint1.class.getName()));
		assertTrue(!endpoints.contains(DefaultConfigurationTestEndpoint2.class.getName()));
		assertTrue(!endpoints.contains("org.eiichiro.bootleg.DefaultConfigurationTestEndpoint3"));
		assertTrue(!endpoints.contains("org.eiichiro.bootleg.DefaultConfigurationTestEndpoint4"));
		
		List<URL> urls = new ArrayList<>();
		
		for (String path : Environment.getProperty("java.class.path").split(File.pathSeparator)) {
			urls.add(new File(path).toURI().toURL());
		}
		
		for (URL url : urls) {
			if (url.getPath().endsWith("bootleg/target/test-classes/")) {
				context.setResourceBase(url.getPath());
				Thread.currentThread().setContextClassLoader(
						new ClassLoader(URLClassLoader.newInstance(new URL[] {
								new URL(url + "WEB-INF/classes/"), 
								new URL(url + "WEB-INF/lib/bootleg-0.5.1-SNAPSHOT.jar")}, 
								loader)) {});
			}
		}
		
		configuration = new DefaultConfiguration();
		configuration.init(context.getServletContext());
		endpoints = new ArrayList<>();
		
		for (Class<?> endpoint : configuration.endpoints()) {
			endpoints.add(endpoint.getName());
		}
		
		assertTrue(!endpoints.contains(DefaultConfigurationTestEndpoint1.class.getName()));
		assertTrue(!endpoints.contains(DefaultConfigurationTestEndpoint2.class.getName()));
		assertTrue(endpoints.contains("org.eiichiro.bootleg.DefaultConfigurationTestEndpoint3"));
		assertTrue(endpoints.contains("org.eiichiro.bootleg.DefaultConfigurationTestEndpoint4"));
		
		tester.stop();
		Thread.currentThread().setContextClassLoader(loader);
	}
	
	/**
	 * Test method for {@link org.eiichiro.bootleg.DefaultConfiguration#routing(org.eiichiro.bootleg.Routing)}.
	 */
	@Test
	public void testRouting() {
		DefaultConfiguration configuration = new DefaultConfiguration();
		Routing routing = configuration.routing();
		assertTrue(routing.ignores("js.js"));
		assertTrue(routing.ignores("css.css"));
		assertTrue(routing.ignores("gif.gif"));
		assertTrue(routing.ignores("png.png"));
		assertTrue(routing.ignores("jpg.jpg"));
		assertTrue(routing.ignores("jsp.jsp"));
		assertTrue(routing.ignores("html.html"));
		assertFalse(routing.ignores("ignores"));
	}

	/**
	 * Test method for {@link org.eiichiro.bootleg.DefaultConfiguration#pipeline(org.eiichiro.bootleg.Pipeline)}.
	 */
	@Test
	public void testPipeline() {
		DefaultConfiguration configuration = new DefaultConfiguration();
		Pipeline<WebContext> pipeline = configuration.pipeline();
		assertThat(pipeline.toString(), is("Stage-0 [Route] -> Stage-1 [Receive] -> Stage-2 [Invoke] -> Stage-3 [Send]"));
	}

	/**
	 * Test method for {@link org.eiichiro.bootleg.DefaultConfiguration#requestType(java.lang.String)}.
	 */
	@Test
	public void testRequestTypes() {
		DefaultConfiguration configuration = new DefaultConfiguration();
		assertThat(configuration.requestTypes().get(MediaType.APPLICATION_JSON), is((Object) JSONRequest.class));
		assertThat(configuration.requestTypes().get(MediaType.APPLICATION_XML), is((Object) XMLRequest.class));
		assertThat(configuration.requestTypes().get(MediaType.TEXT_HTML), is((Object) GenericRequest.class));
		assertThat(configuration.requestTypes().get(MediaType.TEXT_PLAIN), is((Object) GenericRequest.class));
		assertThat(configuration.requestTypes().get(""), is((Object) GenericRequest.class));
	}

	/**
	 * Test method for {@link org.eiichiro.bootleg.DefaultConfiguration#responseType(java.lang.String)}.
	 */
	@Test
	public void testResponseTypes() {
		DefaultConfiguration configuration = new DefaultConfiguration();
		assertThat(configuration.responseTypes().get(MediaType.APPLICATION_JSON), is((Object) JSONResponse.class));
		assertThat(configuration.responseTypes().get(MediaType.APPLICATION_XML), is((Object) XMLResponse.class));
		assertThat(configuration.responseTypes().get(MediaType.TEXT_HTML), is((Object) GenericResponse.class));
		assertThat(configuration.responseTypes().get(MediaType.TEXT_PLAIN), is((Object) GenericResponse.class));
		assertThat(configuration.responseTypes().get(""), is((Object) GenericResponse.class));
	}

}
