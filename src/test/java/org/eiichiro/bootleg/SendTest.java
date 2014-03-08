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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.eiichiro.bootleg.BootlegFilter;
import org.eiichiro.bootleg.MediaType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class SendTest {

	private ServletTester tester = new ServletTester();
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		tester.setContextPath("/bootleg");
		tester.addFilter(BootlegFilter.class, "/*", 0);
		tester.addServlet(DefaultServlet.class, "/");
		Map<String, String> initParams = new HashMap<String, String>();
		initParams.put(BootlegFilter.CONFIGURATION, SendTestConfiguration.class.getName());
		tester.getContext().setInitParams(initParams);
		tester.start();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		tester.stop();
	}

	/**
	 * Test method for {@link org.eiichiro.bootleg.Send#apply(org.eiichiro.bootleg.WebContext)}.
	 * @throws Exception 
	 */
	@Test
	public void testApply() throws Exception {
		// Always generate 'application/xml'.
		HttpTester request = new HttpTester();
		request.setURI("/bootleg/SendTestEndpoint/generateXML");
		request.setMethod("GET");
		request.setHeader("Host", "");
		String req = request.generate();
		System.out.println(req);
		HttpTester response = new HttpTester();
		String res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeader("Content-Type"), is(MediaType.APPLICATION_XML));
		assertThat(response.getContent(), is("<message>hello</message>"));
		
		request.setURI("/bootleg/SendTestEndpoint/generateResponse");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeader("Content-Type"), is(MediaType.APPLICATION_XML));
		assertThat(response.getContent(), is("hello"));
		
		request.setURI("/bootleg/SendTestEndpoint/generateUnsupportedMediaType");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(415));
		
		// Negotiated content type.
		request.setURI("/bootleg/SendTestEndpoint/negotiate");
		// 'application/xml'.
		request.setHeader("Accept", MediaType.APPLICATION_XML);
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeader("Content-Type"), is(MediaType.APPLICATION_XML));
		assertThat(response.getContent(), is("<message>hello</message>"));
		// 'text/plain'.
		request.setHeader("Accept", MediaType.TEXT_PLAIN);
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeader("Content-Type"), is(MediaType.TEXT_PLAIN));
		assertThat(response.getContent(), is("<message>hello</message>"));
	}

}
