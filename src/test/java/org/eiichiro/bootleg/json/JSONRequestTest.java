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
package org.eiichiro.bootleg.json;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.eiichiro.bootleg.BootlegFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class JSONRequestTest {

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

	/**
	 * Test method for {@link org.eiichiro.bootleg.json.JSONRequest#from(org.eiichiro.bootleg.WebContext)}.
	 * @throws Exception 
	 */
	@Test
	public void testFrom() throws Exception {
		HttpTester request = new HttpTester();
		request.setURI("/bootleg/JSONRequestTestEndpoint/testJSONBody");
		request.setMethod("POST");
		request.setHeader("Host", "");
		request.setHeader("Content-type", "application/json");
		String json = "{" 
				+ "\"int\": 9, " 
				+ "\"char\": \"c\", "
				+ "\"strings\": [\"aaa\", \"bbb\"], " 
				+ "\"type1\": \"hello\", " 
				+ "\"type2\": \"hi\", " 
				+ "\"type3\": {" 
						+ "\"userDefinedType3Value\": \"bonjour\"" 
						+ "}, " 
				+ "\"value1s\": [\"aloha\", \"jambo\"], " 
				+ "\"object1s\": [{"
						+ "\"userDefinedType3Value\": \"Adieu\""
						+ "}, {"
						+ "\"userDefinedType3Value\": \"Adios\""
						+ "}], "
				+ "\"userDefinedType3Value\": \"goodbye\"" 
				+ "}";
		request.setContent(json);
		String req = request.generate();
		System.out.println(req);
		HttpTester response = new HttpTester();
		String res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
	}

	@Test
	public void testJSONCollection1() throws Exception {
		HttpTester request = new HttpTester();
		request.setURI("/bootleg/JSONRequestTestEndpoint/testJSONCollection1");
		request.setMethod("POST");
		request.setHeader("Host", "");
		request.setHeader("Content-type", "application/json");
		String json = "[1,2]";
		request.setContent(json);
		String req = request.generate();
		System.out.println(req);
		HttpTester response = new HttpTester();
		String res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
	}
	
	@Test
	public void testJSONCollection2() throws Exception {
		HttpTester request = new HttpTester();
		request.setURI("/bootleg/JSONRequestTestEndpoint/testJSONCollection2");
		request.setMethod("POST");
		request.setHeader("Host", "");
		request.setHeader("Content-type", "application/json");
		String json = "[\"a\",\"b\"]";
		request.setContent(json);
		String req = request.generate();
		System.out.println(req);
		HttpTester response = new HttpTester();
		String res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
	}
	
	@Test
	public void testJSONCollection3() throws Exception {
		HttpTester request = new HttpTester();
		request.setURI("/bootleg/JSONRequestTestEndpoint/testJSONCollection3");
		request.setMethod("POST");
		request.setHeader("Host", "");
		request.setHeader("Content-type", "application/json");
		String json = "[{\"userDefinedType3Value\": \"c\"},{\"userDefinedType3Value\": \"d\"}]";
		request.setContent(json);
		String req = request.generate();
		System.out.println(req);
		HttpTester response = new HttpTester();
		String res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
	}
	
}
