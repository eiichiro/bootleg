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
public class ReceiveTest {

	private ServletTester tester = new ServletTester();
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		tester.setContextPath("/bootleg");
		tester.addFilter(BootlegFilter.class, "/*", 0);
		tester.addServlet(DefaultServlet.class, "/");
		tester.start();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		tester.stop();
	}

	@Test
	public void testReceive() throws Exception {
		// All HTTP methods are acceptable.
		HttpTester request = new HttpTester();
		request.setURI("/bootleg/ReceiveTestEndpoint/acceptAll");
		request.setMethod("GET");
		request.setHeader("Host", "");
		String req = request.generate();
		System.out.println(req);
		HttpTester response = new HttpTester();
		String res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		request.setMethod("POST");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		request.setMethod("PUT");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		
		// GET method is acceptable.
		request.setURI("/bootleg/ReceiveTestEndpoint/acceptGet");
		request.setMethod("GET");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		request.setMethod("POST");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(405));
		request.setMethod("PUT");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(405));
		
		// POST method is acceptable.
		request.setURI("/bootleg/ReceiveTestEndpoint/acceptPost");
		request.setMethod("GET");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(405));
		request.setMethod("POST");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		request.setMethod("PUT");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(405));
		
		// Both of GET and POST are acceptable.
		request.setURI("/bootleg/ReceiveTestEndpoint/acceptGetPost");
		request.setMethod("GET");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		request.setMethod("POST");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		request.setMethod("PUT");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(405));
		
		// Acceptable MIME media type.
		request.setURI("/bootleg/ReceiveTestEndpoint/acceptXML");
		request.setMethod("POST");
		request.setHeader("Content-Type", MediaType.APPLICATION_XML);
		request.setContent("<parameter></parameter>");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		request.setHeader("Content-Type", MediaType.TEXT_PLAIN);
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(415));
	}

}
