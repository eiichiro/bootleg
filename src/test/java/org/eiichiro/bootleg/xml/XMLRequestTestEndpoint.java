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

import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.eiichiro.bootleg.UserDefinedObject1;
import org.eiichiro.bootleg.UserDefinedValue1;
import org.eiichiro.bootleg.UserDefinedValue2;
import org.eiichiro.bootleg.annotation.Application;
import org.eiichiro.bootleg.annotation.Body;
import org.eiichiro.bootleg.annotation.Endpoint;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Endpoint
public class XMLRequestTestEndpoint {

	public void testXMLBody(
			@Body("booleans") boolean[] bs, 						// Array is not supported -> null.
			@Body("notexist_obj") Object object, 					// Not found -> null.
			@Body("notexist_url") Collection<URL> urls, 			// Not found -> empty collection.
			@Body("notexist_double") double d, 						// Not found -> default primitive value. 
			@Body("int") int i,	 									// Core value type.
			@Application("notexist_char") @Body("char") char c, 	// Found on body.
			@Body("strings") List<String> strings, 					// Collection.
			@Body("type1") UserDefinedValue1 type1, 				// Constructor that takes String.class.
			@Body("type2") UserDefinedValue2 type2, 				// valueOf(String.class).
			@Body("type3") UserDefinedObject1 type3, 				// User-defined object type (JsonObject).
			@Body("value1s") List<UserDefinedValue1> value1s, 		// Collection of user-defined value type (JsonPrimitive).
			@Body("object1s") List<UserDefinedObject1> object1s, 	// Collection of user-defined object (JsonObject).
			@Body Long long1, 										// Must be qualified with the name -> null.
			@Body Collection<Date> dates, 							// Must be qualified with the name -> empty.
			@Body UserDefinedValue1 type12, 						// Cannot construct -> null.
			@Body UserDefinedValue2 type22, 						// Cannot construct -> null.
			@Body UserDefinedObject1 type32, 						// Constructed and the field is populated.
			@Body Set<UserDefinedValue1> value1s2, 					// Must be qualified with the name -> empty.
			@Body Set<UserDefinedObject1> object1s2) {				// Must be qualified with the name -> empty.
		System.out.println("XMLRequestTestEndpoint#testXMLBody");
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
		assertThat(type3.getUserDefinedType3Value(), is("bonjour"));
		assertThat(value1s.size(), is(2));
		assertThat(value1s.get(0).getValue(), is("aloha"));
		assertThat(value1s.get(1).getValue(), is("jambo"));
		assertThat(object1s.size(), is(2));
		assertThat(object1s.get(0).getUserDefinedType3Value(), is("Adieu"));
		assertThat(object1s.get(1).getUserDefinedType3Value(), is("Adios"));
		assertThat(long1, nullValue());
		assertThat(dates.isEmpty(), is(true));
		assertThat(type12, nullValue());
		assertThat(type22, nullValue());
		assertThat(type32.getUserDefinedType3Value(), is("goodbye"));
		assertThat(value1s2.isEmpty(), is(true));
		assertThat(object1s2.isEmpty(), is(true));
	}
	
}
