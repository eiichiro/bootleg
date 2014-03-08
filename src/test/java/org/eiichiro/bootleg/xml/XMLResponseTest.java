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

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.eiichiro.bootleg.Link;
import org.eiichiro.bootleg.LinkConverter;
import org.eiichiro.bootleg.MediaType;
import org.eiichiro.bootleg.BootlegFilter;
import org.eiichiro.bootleg.Types;
import org.eiichiro.bootleg.UserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class XMLResponseTest {

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
	 * Test method for {@link org.eiichiro.bootleg.xml.XMLResponse#to(org.eiichiro.bootleg.WebContext)}.
	 * @throws Exception 
	 */
	@Test
	public void testTo() throws Exception {
		Types.addCoreValueType(Link.class, new LinkConverter());
		HttpTester request = new HttpTester();
		request.setURI("/bootleg/XMLResponseTestEndpoint/getXMLUserInfo");
		request.setMethod("GET");
		request.setHeader("Host", "");
		String req = request.generate();
		System.out.println(req);
		HttpTester response = new HttpTester();
		String res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		UserInfo userInfo;
		Unmarshaller unmarshaller = JAXBContext.newInstance(UserInfo.class).createUnmarshaller();
		userInfo = (UserInfo) unmarshaller.unmarshal(new StringReader(response.getContent()));
		assertThat(response.getStatus(), is(200));
		assertThat(response.getContentType(), is(MediaType.APPLICATION_XML));
		assertThat(userInfo.id, is("eiichiro"));
		assertThat(userInfo.name, is("Eiichiro Uchiumi"));
		assertThat(userInfo.rank, is(9));
		assertThat(userInfo.interests.get(0), is("Listening to music"));
		assertThat(userInfo.interests.get(1), is("Buddhist art"));
	}

}
