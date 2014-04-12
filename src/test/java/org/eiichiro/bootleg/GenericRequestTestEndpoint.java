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

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eiichiro.bootleg.Request;
import org.eiichiro.bootleg.WebContext;
import org.eiichiro.bootleg.annotation.Application;
import org.eiichiro.bootleg.annotation.Body;
import org.eiichiro.bootleg.annotation.Cookie;
import org.eiichiro.bootleg.annotation.Endpoint;
import org.eiichiro.bootleg.annotation.Header;
import org.eiichiro.bootleg.annotation.Path;
import org.eiichiro.bootleg.annotation.Query;
import org.eiichiro.bootleg.annotation.Session;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Endpoint
public class GenericRequestTestEndpoint {

	public void testBuiltinType(
			WebContext context, 
			HttpServletRequest request,
			HttpServletResponse response, 
			HttpSession session,
			ServletContext servletContext, 
			Request req) {
		System.out.println("GenericRequestTestEndpoint#testBuiltinType");
		assertThat(context, notNullValue());
		assertThat(request, notNullValue());
		assertThat(response, notNullValue());
		assertThat(session, notNullValue());
		assertThat(servletContext, notNullValue());
		assertThat(req, notNullValue());
	}
	
	public void testQuery(
			@Query("booleans") boolean[] bs, 				// Array is not supported -> null.
			@Query("notexist_obj") Object object, 			// Not found -> null.
			@Query("notexist_url") Collection<URL> urls, 	// Not found -> empty collection.
			@Query("notexist_double") double d, 			// Not found -> default primitive value.
			@Query("int") int i,	 						// Core value type.
			@Application("notexist_char") @Query("char") char c, // Found on query.
			@Query("strings") List<String> strings, 		// Collection.
			@Query("type1") UserDefinedValue1 type1, 		// Constructor that takes String.class.
			@Query("type2") UserDefinedValue2 type2, 		// valueOf(String.class).
			@Query("type3") UserDefinedObject1 type3, 		// Cannot construct -> null.
			@Query Long long1, 								// Must be qualified with the name -> null.
			@Query Collection<Date> dates, 					// Must be qualified with the name -> empty.
			@Query UserDefinedValue1 type12, 				// Cannot construct -> null.
			@Query UserDefinedValue2 type22, 				// Cannot construct -> null.
			@Query UserDefinedObject1 type32) {				// Constructed and the field is populated.
		System.out.println("GenericRequestTestEndpoint#testQuery");
		assertThat(bs, nullValue());
		assertThat(object, nullValue());
		assertThat(urls.isEmpty(), is(true));
		assertThat(d, is(0.0));
		assertThat(i, is(9));
		assertThat(c, is('c'));
		assertThat(strings.size(), is(2));
		assertThat(strings.get(0), is("aaa"));
		assertThat(strings.get(1), is("bbb"));
		assertThat(type1.getValue(), is("hello"));
		assertThat(type2.getValue(), is("hi"));
		assertThat(type3, nullValue());
		assertThat(long1, nullValue());
		assertThat(dates.isEmpty(), is(true));
		assertThat(type12, nullValue());
		assertThat(type22, nullValue());
		assertThat(type32.getUserDefinedType3Value(), is("goodbye"));
	}
	
	public void testBody(
			@Body("booleans") boolean[] bs, 				// Array is not supported -> null.
			@Body("notexist_obj") Object object, 			// Not found -> null.
			@Body("notexist_url") Collection<URL> urls, 	// Not found -> empty collection.
			@Body("notexist_double") double d, 			// Not found -> default primitive value. 
			@Body("int") int i,	 						// Core value type.
			@Application("notexist_char") @Body("char") char c, // Found on body.
			@Body("strings") List<String> strings, 		// Collection.
			@Body("type1") UserDefinedValue1 type1, 		// Constructor that takes String.class.
			@Body("type2") UserDefinedValue2 type2, 		// valueOf(String.class).
			@Body("type3") UserDefinedObject1 type3, 		// Cannot construct -> null.
			@Body Long long1, 								// Must be qualified with the name -> null.
			@Body Collection<Date> dates, 					// Must be qualified with the name -> empty.
			@Body UserDefinedValue1 type12, 				// Cannot construct -> null.
			@Body UserDefinedValue2 type22, 				// Cannot construct -> null.
			@Body UserDefinedObject1 type32) {				// Constructed and the field is populated.
		System.out.println("GenericRequestTestEndpoint#testBody");
		assertThat(bs, nullValue());
		assertThat(object, nullValue());
		assertThat(urls.isEmpty(), is(true));
		assertThat(d, is(0.0));
		assertThat(i, is(9));
		assertThat(c, is('c'));
		assertThat(strings.size(), is(2));
		assertThat(strings.get(0), is("aaa"));
		assertThat(strings.get(1), is("bbb"));
		assertThat(type1.getValue(), is("hello"));
		assertThat(type2.getValue(), is("hi"));
		assertThat(type3, nullValue());
		assertThat(long1, nullValue());
		assertThat(dates.isEmpty(), is(true));
		assertThat(type12, nullValue());
		assertThat(type22, nullValue());
		assertThat(type32.getUserDefinedType3Value(), is("goodbye"));
	}
	
	public void testHeader(
			@Header("booleans") boolean[] bs, 				// Array is not supported -> null.
			@Header("notexist_obj") Object object, 			// Not found -> null.
			@Header("notexist_url") Collection<URL> urls, 	// Not found -> empty collection.
			@Header("notexist_double") double d, 			// Not found -> default primitive value. 
			@Header("int") int i,	 						// Core value type.
			@Application("notexist_char") @Header("char") char c, // Found on header.
			@Header("strings") List<String> strings, 		// Collection.
			@Header("type1") UserDefinedValue1 type1, 		// Constructor that takes String.class.
			@Header("type2") UserDefinedValue2 type2, 		// valueOf(String.class).
			@Header("type3") UserDefinedObject1 type3, 		// Cannot construct -> null.
			@Header Long long1, 								// Must be qualified with the name -> null.
			@Header Collection<Date> dates, 					// Must be qualified with the name -> empty.
			@Header UserDefinedValue1 type12, 				// Cannot construct -> null.
			@Header UserDefinedValue2 type22, 				// Cannot construct -> null.
			@Header UserDefinedObject1 type32) {				// Constructed and the field is populated.
		System.out.println("GenericRequestTestEndpoint#testHeader");
		assertThat(bs, nullValue());
		assertThat(object, nullValue());
		assertThat(urls.isEmpty(), is(true));
		assertThat(d, is(0.0));
		assertThat(i, is(9));
		assertThat(c, is('c'));
		assertThat(strings.size(), is(2));
		assertThat(strings.get(0), is("aaa"));
		assertThat(strings.get(1), is("bbb"));
		assertThat(type1.getValue(), is("hello"));
		assertThat(type2.getValue(), is("hi"));
		assertThat(type3, nullValue());
		assertThat(long1, nullValue());
		assertThat(dates.isEmpty(), is(true));
		assertThat(type12, nullValue());
		assertThat(type22, nullValue());
		assertThat(type32.getUserDefinedType3Value(), is("goodbye"));
	}
	
	public void testCookie(
			@Cookie("booleans") boolean[] bs, 				// Array is not supported -> null.
			@Cookie("notexist_obj") Object object, 			// Not found -> null.
			@Cookie("notexist_url") Collection<URL> urls, 	// Not found -> empty collection.
			@Cookie("notexist_double") double d, 			// Not found -> default primitive value. 
			@Cookie("int") int i,	 						// Core value type.
			@Application("notexist_char") @Cookie("char") char c, // Found on cookie.
			@Cookie("strings") List<String> strings, 		// Collection.
			@Cookie("type1") UserDefinedValue1 type1, 		// Constructor that takes String.class.
			@Cookie("type2") UserDefinedValue2 type2, 		// valueOf(String.class).
			@Cookie("type3") UserDefinedObject1 type3, 		// Cannot construct -> null.
			@Cookie Long long1, 								// Must be qualified with the name -> null.
			@Cookie Collection<Date> dates, 					// Must be qualified with the name -> empty.
			@Cookie UserDefinedValue1 type12, 				// Cannot construct -> null.
			@Cookie UserDefinedValue2 type22, 				// Cannot construct -> null.
			@Cookie UserDefinedObject1 type32) {				// Constructed and the field is populated.
		System.out.println("GenericRequestTestEndpoint#testCookie");
		assertThat(bs, nullValue());
		assertThat(object, nullValue());
		assertThat(urls.isEmpty(), is(true));
		assertThat(d, is(0.0));
		assertThat(i, is(9));
		assertThat(c, is('c'));
		assertThat(strings.size(), is(2));
		assertThat(strings.get(0), is("aaa"));
		assertThat(strings.get(1), is("bbb"));
		assertThat(type1.getValue(), is("hello"));
		assertThat(type2.getValue(), is("hi"));
		assertThat(type3, nullValue());
		assertThat(long1, nullValue());
		assertThat(dates.isEmpty(), is(true));
		assertThat(type12, nullValue());
		assertThat(type22, nullValue());
		assertThat(type32.getUserDefinedType3Value(), is("goodbye"));
	}
	
	public void setUpSession(HttpSession session) {
		session.setAttribute("int", "9");
		session.setAttribute("char", "c");
		session.setAttribute("strings[1]", "bbb");
		session.setAttribute("strings[0]", "aaa");
		session.setAttribute("type1", "hello");
		session.setAttribute("type2", "hi");
		session.setAttribute("type3", "ccc");
		session.setAttribute("userDefinedType3Value", "goodbye");
		session.setAttribute("type4", UserDefinedUnconstructableParameter.newInstance("hey"));
	}
	
	public void testSession(
			@Session("booleans") boolean[] bs, 				// Array is not supported -> null.
			@Session("notexist_obj") Object object, 		// Not found -> null.
			@Session("notexist_url") Collection<URL> urls, 	// Not found -> empty collection.
			@Session("notexist_double") double d, 			// Not found -> default primitive value. 
			@Session("int") int i,	 						// Core value type.
			@Application("notexist_char") @Session("char") char c, // Found on session.
			@Session("strings") List<String> strings, 		// Collection.
			@Session("type1") UserDefinedValue1 type1, 		// Constructor that takes String.class.
			@Session("type2") UserDefinedValue2 type2, 		// valueOf(String.class).
			@Session("type3") UserDefinedObject1 type3, 		// Cannot construct -> null.
			@Session("type4") UserDefinedUnconstructableParameter type4, 		// Already stored.
			@Session Long long1, 							// Must be qualified with the name -> null.
			@Session Collection<Date> dates, 				// Must be qualified with the name -> empty.
			@Session UserDefinedValue1 type12, 				// Cannot construct -> null.
			@Session UserDefinedValue2 type22, 				// Cannot construct -> null.
			@Session UserDefinedObject1 type32) {				// Constructed and the field is populated.
		System.out.println("GenericRequestTestEndpoint#testSession");
		assertThat(bs, nullValue());
		assertThat(object, nullValue());
		assertThat(urls.isEmpty(), is(true));
		assertThat(d, is(0.0));
		assertThat(i, is(9));
		assertThat(c, is('c'));
		assertThat(strings.size(), is(2));
		assertThat(strings.get(0), is("aaa"));
		assertThat(strings.get(1), is("bbb"));
		assertThat(type1.getValue(), is("hello"));
		assertThat(type2.getValue(), is("hi"));
		assertThat(type3, nullValue());
		assertThat(type4, notNullValue());
		assertThat(type4.getUserDefinedType4Value(), is("hey"));
		assertThat(long1, nullValue());
		assertThat(dates.isEmpty(), is(true));
		assertThat(type12, nullValue());
		assertThat(type22, nullValue());
		assertThat(type32.getUserDefinedType3Value(), is("goodbye"));
	}
	
	public void setUpApplication(ServletContext context) {
		context.setAttribute("int", "9");
		context.setAttribute("char", "c");
		context.setAttribute("strings[1]", "bbb");
		context.setAttribute("strings[0]", "aaa");
		context.setAttribute("type1", "hello");
		context.setAttribute("type2", "hi");
		context.setAttribute("type3", "ccc");
		context.setAttribute("userDefinedType3Value", "goodbye");
		context.setAttribute("type4", UserDefinedUnconstructableParameter.newInstance("hey"));
	}
	
	public void testApplication(
			@Application("booleans") boolean[] bs, 				// Array is not supported -> null.
			@Application("notexist_obj") Object object, 		// Not found -> null.
			@Application("notexist_url") Collection<URL> urls, 	// Not found -> empty collection.
			@Application("notexist_double") double d, 			// Not found -> default primitive value. 
			@Application("int") int i,	 						// Core value type.
			@Session("notexist_char") @Application("char") char c, // Found on application.
			@Application("strings") List<String> strings, 		// Collection.
			@Application("type1") UserDefinedValue1 type1, 		// Constructor that takes String.class.
			@Application("type2") UserDefinedValue2 type2, 		// valueOf(String.class).
			@Application("type3") UserDefinedObject1 type3, 		// Cannot construct -> null.
			@Application("type4") UserDefinedUnconstructableParameter type4, 		// Already stored.
			@Application Long long1, 							// Must be qualified with the name -> null.
			@Application Collection<Date> dates, 				// Must be qualified with the name -> empty.
			@Application UserDefinedValue1 type12, 				// Cannot construct -> null.
			@Application UserDefinedValue2 type22, 				// Cannot construct -> null.
			@Application UserDefinedObject1 type32) {				// Constructed and the field is populated.
		System.out.println("GenericRequestTestEndpoint#testSession");
		assertThat(bs, nullValue());
		assertThat(object, nullValue());
		assertThat(urls.isEmpty(), is(true));
		assertThat(d, is(0.0));
		assertThat(i, is(9));
		assertThat(c, is('c'));
		assertThat(strings.size(), is(2));
		assertThat(strings.get(0), is("aaa"));
		assertThat(strings.get(1), is("bbb"));
		assertThat(type1.getValue(), is("hello"));
		assertThat(type2.getValue(), is("hi"));
		assertThat(type3, nullValue());
		assertThat(type4, notNullValue());
		assertThat(type4.getUserDefinedType4Value(), is("hey"));
		assertThat(long1, nullValue());
		assertThat(dates.isEmpty(), is(true));
		assertThat(type12, nullValue());
		assertThat(type22, nullValue());
		assertThat(type32.getUserDefinedType3Value(), is("goodbye"));
	}
	
	@SuppressWarnings("unchecked")
	public void clearApplication(ServletContext context) {
		for (Object name : Collections.list(context.getAttributeNames())) {
			context.removeAttribute((String) name);
		}
	}
	
	public void setUpNoSource(HttpSession session, ServletContext context) {
		context.setAttribute("type4", UserDefinedUnconstructableParameter.newInstance("hey"));
		session.setAttribute("strings[1]", "bbb");
		session.setAttribute("strings[0]", "aaa");
	}
	
	public void testNoSource(
			boolean[] bs, 				// Not annotated with source -> null.
			Collection<Date> dates, 	// Not annotated with source -> empty.
			Long long1, 				// Not annotated with source -> null.
			double d, 					// Not annotated with source -> default primitive value.
			UserDefinedValue1 type1, 	// Not annotated with source -> null.
			UserDefinedValue2 type2, 	// Not annotated with source -> null.
			UserDefinedObject1 type3, 	// Not annotated with source -> null.
			UserDefinedUnconstructableParameter type4) {	// Not annotated with source -> null.
		System.out.println("GenericRequestTestEndpoint#testNoSource");
		assertThat(bs, nullValue());
		assertThat(dates.isEmpty(), is(true));
		assertThat(long1, nullValue());
		assertThat(d, is(0.0));
		assertThat(type1, nullValue());
		assertThat(type2, nullValue());
		assertThat(type3, nullValue());
		assertThat(type4, nullValue());
	}
	
	public void testPath(
			@Path("booleans") boolean[] bs, 				// Array is not supported -> null.
			@Path("notexist_obj") Object object, 			// Not found -> null.
			@Path("notexist_url") Collection<URL> urls, 	// Collection is not supported -> empty.
			@Path("notexist_double") double d, 				// Not found -> default primitive value.
			@Path("int") int i,	 							// Core value type.
			@Application("notexist_char") @Path("char") char c, // Found on query.
			@Path("strings") List<String> strings, 			// Collection is not supported -> empty.
			@Path("type1") UserDefinedValue1 type1, 		// Constructor that takes String.class.
			@Path("type2") UserDefinedValue2 type2, 		// valueOf(String.class).
			@Path("type3") UserDefinedObject1 type3, 		// Cannot construct -> null.
			@Path Long long1, 								// Must be qualified with the name -> null.
			@Path Collection<Date> dates, 					// Collection is not supported -> empty.
			@Path UserDefinedValue1 type12, 				// Cannot construct -> null.
			@Path UserDefinedValue2 type22, 				// Cannot construct -> null.
			@Path UserDefinedObject1 type32) {				// Constructed and the field is populated.
		System.out.println("GenericRequestTestEndpoint#testPath");
		assertThat(bs, nullValue());
		assertThat(object, nullValue());
		assertThat(urls.isEmpty(), is(true));
		assertThat(d, is(0.0));
		assertThat(i, is(9));
		assertThat(c, is('c'));
		assertThat(strings.isEmpty(), is(true));
		assertThat(type1.getValue(), is("hello"));
		assertThat(type2.getValue(), is("hi"));
		assertThat(type3, nullValue());
		assertThat(long1, nullValue());
		assertThat(dates.isEmpty(), is(true));
		assertThat(type12, nullValue());
		assertThat(type22, nullValue());
		assertThat(type32.getUserDefinedType3Value(), is("goodbye"));
	}
	
}
