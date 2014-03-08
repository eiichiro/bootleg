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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Type;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.eiichiro.bootleg.Email;
import org.eiichiro.bootleg.Link;
import org.eiichiro.bootleg.LinkConverter;
import org.eiichiro.bootleg.MediaType;
import org.eiichiro.bootleg.BootlegFilter;
import org.eiichiro.bootleg.Types;
import org.eiichiro.bootleg.UserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class JSONResponseTest {

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
	 * Test method for {@link org.eiichiro.bootleg.json.JSONResponse#to(org.eiichiro.bootleg.WebContext)}.
	 * @throws Exception 
	 */
	@Test
	public void testTo() throws Exception {
		Types.addCoreValueType(Link.class, new LinkConverter());
		HttpTester request = new HttpTester();
		request.setURI("/bootleg/JSONResponseTestEndpoint/getJSONUserInfo");
		request.setMethod("GET");
		request.setHeader("Host", "");
		String req = request.generate();
		System.out.println(req);
		HttpTester response = new HttpTester();
		String res = tester.getResponses(req);
		System.out.println(res);
		response.parse(res);
		JsonDeserializer<Object> deserializer = new JsonDeserializer<Object>() {

			@Override
			public Object deserialize(JsonElement json, Type type,
					JsonDeserializationContext context) throws JsonParseException {
				if (type.getClass().equals(Email.class)) {
					return new Email(json.getAsString());
				} else if (type.getClass().equals(Link.class)) {
					return new Link(json.getAsString());
				}
				
				return null;
			}
			
		};
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Link.class, deserializer);
		builder.registerTypeAdapter(Email.class, deserializer);
		Gson gson = builder.create();
		UserInfo userInfo = gson.fromJson(response.getContent(), UserInfo.class);
		assertThat(response.getStatus(), is(200));
		assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON));
		assertThat(userInfo.id, is("eiichiro"));
		assertThat(userInfo.name, is("Eiichiro Uchiumi"));
		assertThat(userInfo.rank, is(9));
		assertThat(userInfo.interests.get(0), is("Listening to music"));
		assertThat(userInfo.interests.get(1), is("Buddhist art"));
	}

}
