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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import org.eclipse.jetty.io.ByteArrayBuffer;
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
public class GenericResponseTest {

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
	 * Test method for {@link org.eiichiro.bootleg.GenericResponse#to(org.eiichiro.bootleg.WebContext)}.
	 * @throws Exception 
	 * @throws IOException 
	 */
	@Test
	public void testTo() throws IOException, Exception {
		// GenericResponse without content. Both of content type and status are default.
		HttpTester request = new HttpTester();
		request.setURI("/bootleg/GenericResponseTestEndpoint/getNull");
		request.setMethod("GET");
		request.setHeader("Host", "");
		String req = request.generate();
		System.out.println(req);
		HttpTester response = new HttpTester();
		String res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeader("Content-Type"), nullValue());
		assertThat(response.getContent(), nullValue());
		
		// GenericResponse without content. Content type is 'application/xml' and status is default.
		request.setURI("/bootleg/GenericResponseTestEndpoint/getXMLNull");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeader("Content-Type"), is(MediaType.APPLICATION_XML));
		assertThat(response.getContent(), nullValue());
		
		// GenericResponse without content. Content type is default and status is 204 (No Content).
		request.setURI("/bootleg/GenericResponseTestEndpoint/get204Null");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(204));
		assertThat(response.getHeader("Content-Type"), nullValue());
		assertThat(response.getContent(), nullValue());
		
		// Plain text string.
		request.setURI("/bootleg/GenericResponseTestEndpoint/getString");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeader("Content-Type"), is(MediaType.TEXT_PLAIN));
		assertThat(response.getContent(), is("hello"));
		
		// XML string.
		request.setURI("/bootleg/GenericResponseTestEndpoint/getXMLString");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeader("Content-Type"), is(MediaType.APPLICATION_XML));
		assertThat(response.getContent(), is("<message>hello</message>"));
		
		// Serializable
		request.setURI("/bootleg/GenericResponseTestEndpoint/getSerializable");
		req = request.generate();
		System.out.println(req);
		ByteArrayBuffer responses = tester.getResponses(new ByteArrayBuffer(req));
		System.out.println(responses);
		response.parse(responses.toString());
		
		byte[] array = responses.asArray();
		int offset = 0;
		
		for (int i = 0; i < array.length; i++) {
			// Start of content.
			if (array[i] == 13 && array[i + 1] == 10 && array[i + 2] == 13 && array[i + 3] == 10) {
				offset = i + 4;
				break;
			}
		}
		
		byte[] range = Arrays.copyOfRange(array, offset, array.length);
		SerializableObject object = (SerializableObject) new ObjectInputStream(new ByteArrayInputStream(range)).readObject();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeader("Content-Type"), nullValue());
		assertThat(object.getValue(), is("hello"));
		
		// User defined object as plain text string.
		request.setURI("/bootleg/GenericResponseTestEndpoint/getUserDefined");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeader("Content-Type"), is(MediaType.TEXT_PLAIN));
		assertThat(response.getContent(), is("hello"));
		
		// User defined object as XML string.
		request.setURI("/bootleg/GenericResponseTestEndpoint/getXMLUserDefined");
		req = request.generate();
		System.out.println(req);
		res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeader("Content-Type"), is(MediaType.APPLICATION_XML));
		assertThat(response.getContent(), is("<message>hello</message>"));
	}

}
