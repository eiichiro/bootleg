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

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Function;

/**
 * {@code GenericRequest} is a generic implementation of {@code Request}.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class GenericRequest extends AbstractRequest {

	/**
	 * Returns the Web endpoint method parameter value from the current HTTP 
	 * request body sent as 'application/x-www-form-urlencoded' posted form.
	 * 
	 * @param type Web endpoint method parameter type.
	 * @param name Web endpoint method parameter name.
	 * @return Web endpoint method parameter value.
	 */
	@Override
	public Object body(Type type, String name) {
		return parameter(type, name, new Function<String, Object>() {

			public Object apply(String name) {
				return context.request().getParameter(name);
			}
			
		}, new Function<String, Collection<Object>>() {

			@SuppressWarnings("unchecked")
			public Collection<Object> apply(String name) {
				HttpServletRequest request = context.request();
				Map<String, Object> map = new TreeMap<String, Object>();
				
				for (Object object : Collections.list(request.getParameterNames())) {
					String key = (String) object;
					
					if (key.startsWith(name + "[")) {
						map.put(key, request.getParameter(key));
					}
				}
				
				return (map.isEmpty()) ? null : map.values();
			}
			
		});
	}
	
}
