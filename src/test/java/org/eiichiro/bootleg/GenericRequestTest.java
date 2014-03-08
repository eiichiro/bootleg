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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.eiichiro.bootleg.BootlegFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class GenericRequestTest {

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
		initParams.put(BootlegFilter.CONFIGURATION, GenericRequestTestConfiguration.class.getName());
		tester.getContext().setInitParams(initParams);
		tester.start();
	}

	@After
	public void tearDown() throws Exception {
		tester.stop();
	}
	
	/**
	 * Test method for {@link org.eiichiro.bootleg.AbstractRequest#get(java.lang.reflect.Type, java.util.List)}.
	 * @throws Exception 
	 */
	@Test
	public void testGet() throws Exception {
		HttpTester request = new HttpTester();
		request.setURI("/bootleg/GenericRequestTestEndpoint/testBuiltinType");
		request.setMethod("GET");
		request.setHeader("Host", "");
		String req = request.generate();
		System.out.println(req);
		HttpTester response = new HttpTester();
		String res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		
		request.reset();
		String query = "int=9&char=c&strings[1]=bbb&strings[0]=aaa&type1=hello&type2=hi&type3=ccc&userDefinedType3Value=goodbye";
		request.setURI("/bootleg/GenericRequestTestEndpoint/testQuery" + "?" + query);
		request.setMethod("GET");
		request.setHeader("Host", "");
		req = request.generate();
		System.out.println(req);
		response = new HttpTester();
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		
		request.reset();
		String form = "int=9&char=c&strings[1]=bbb&strings[0]=aaa&type1=hello&type2=hi&type3=ccc&userDefinedType3Value=goodbye";
		request.setURI("/bootleg/GenericRequestTestEndpoint/testBody");
		request.setMethod("POST");
		request.setHeader("Host", "");
		request.setHeader("Content-type", "application/x-www-form-urlencoded");
		request.setContent(form);
		req = request.generate();
		System.out.println(req);
		response = new HttpTester();
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		
		request.reset();
		request.setURI("/bootleg/GenericRequestTestEndpoint/testHeader");
		request.setMethod("GET");
		request.setHeader("Host", "");
		request.setHeader("int", "9");
		request.setHeader("char", "c");
		request.setHeader("strings[1]", "bbb");
		request.setHeader("strings[0]", "aaa");
		request.setHeader("type1", "hello");
		request.setHeader("type2", "hi");
		request.setHeader("type3", "ccc");
		request.setHeader("userDefinedType3Value", "goodbye");
		req = request.generate();
		System.out.println(req);
		response = new HttpTester();
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		
		request.reset();
		request.setURI("/bootleg/GenericRequestTestEndpoint/testCookie");
		request.setMethod("GET");
		request.setHeader("Host", "");
		request.setHeader("Cookie", "int=9;char=c;strings[1]=bbb;strings[0]=aaa;type1=hello;type2=hi;type3=ccc;userDefinedType3Value=goodbye");
		req = request.generate();
		System.out.println(req);
		response = new HttpTester();
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		
		request.reset();
		request.setURI("/bootleg/GenericRequestTestEndpoint/setUpSession");
		request.setMethod("GET");
		request.setHeader("Host", "");
		req = request.generate();
		System.out.println(req);
		response = new HttpTester();
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		request.reset();
		request.setURI("/bootleg/GenericRequestTestEndpoint/testSession");
		request.setMethod("GET");
		request.setHeader("Host", "");
		request.setHeader("Cookie", response.getHeader("Set-Cookie"));
		req = request.generate();
		System.out.println(req);
		response = new HttpTester();
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		
		request.reset();
		request.setURI("/bootleg/GenericRequestTestEndpoint/setUpApplication");
		request.setMethod("GET");
		request.setHeader("Host", "");
		req = request.generate();
		System.out.println(req);
		response = new HttpTester();
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		request.reset();
		request.setURI("/bootleg/GenericRequestTestEndpoint/testApplication");
		request.setMethod("GET");
		request.setHeader("Host", "");
		req = request.generate();
		System.out.println(req);
		response = new HttpTester();
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		request.reset();
		request.setURI("/bootleg/GenericRequestTestEndpoint/clearApplication");
		request.setMethod("GET");
		request.setHeader("Host", "");
		req = request.generate();
		System.out.println(req);
		response = new HttpTester();
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		
		request.reset();
		request.setURI("/bootleg/GenericRequestTestEndpoint/setUpNoSource");
		request.setMethod("GET");
		request.setHeader("Host", "");
		req = request.generate();
		System.out.println(req);
		response = new HttpTester();
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		request.reset();
		request.setURI("/bootleg/GenericRequestTestEndpoint/testNoSource?int=9&type1=hello");
		request.setMethod("GET");
		request.setHeader("Host", "");
		request.setHeader("Cookie", response.getHeader("Set-Cookie") + ";char=c");
		request.setHeader("type2", "hi");
		req = request.generate();
		System.out.println(req);
		response = new HttpTester();
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		
		request.reset();
		request.setURI("/bootleg/9/c/aaa/hello/hi/ccc/goodbye");
		request.setMethod("GET");
		request.setHeader("Host", "");
		request.setHeader("Content-type", "application/x-www-form-urlencoded");
		req = request.generate();
		System.out.println(req);
		response = new HttpTester();
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
	}

}
