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
package org.eiichiro.bootleg.xml;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
public class XMLRequestTest {

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
	 * Test method for {@link org.eiichiro.bootleg.xml.XMLRequest#from(org.eiichiro.bootleg.WebContext)}.
	 * @throws Exception 
	 */
	@Test
	public void testFrom() throws Exception {
		HttpTester request = new HttpTester();
		request.setURI("/bootleg/XMLRequestTestEndpoint/testXMLBody");
		request.setMethod("POST");
		request.setHeader("Host", "");
		request.setHeader("Content-type", "application/xml");
		String xml = "<parameter>"
				+ "<int>9</int>" 
				+ "<char>c</char>" 
				+ "<strings comment=\"comment\">aaa</strings>" 
				+ "<strings>bbb</strings>" 
				+ "<type1>hello</type1>" 
				+ "<type2>hi</type2>" 
				+ "<type3>" 
						+ "<userDefinedType3Value>bonjour</userDefinedType3Value>" 
				+ "</type3>" 
				+ "<value1s>aloha</value1s>" 
				+ "<value1s>jambo</value1s>" 
				+ "<object1s>" 
						+ "<userDefinedType3Value>Adieu</userDefinedType3Value>"
				+ "</object1s>" 
				+ "<object1s>" 
						+ "<userDefinedType3Value>Adios</userDefinedType3Value>"
				+ "</object1s>" 
				+ "<userDefinedType3Value>goodbye</userDefinedType3Value>"
				+ "</parameter>";
		request.setContent(xml);
		String req = request.generate();
		System.out.println(req);
		HttpTester response = new HttpTester();
		String res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
	}

}
