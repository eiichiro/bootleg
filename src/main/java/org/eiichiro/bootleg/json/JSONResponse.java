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

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.eiichiro.bootleg.AbstractResponse;
import org.eiichiro.bootleg.MediaType;
import org.eiichiro.bootleg.Types;
import org.eiichiro.bootleg.WebContext;
import org.eiichiro.reverb.lang.UncheckedException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * {@code JSONResponse} is a JSON-based implementation of {@code Response}.
 * If the generated/negotiated MIME media type is 'application/json', Bootleg 
 * uses this implementation.
 * Web endpoint method invocation result is deserialized to JSON with 
 * <a href='http://code.google.com/p/google-gson/'>Google Gson library</a>. 
 * UTF-8 is used for the character encoding (MIME charset) at any time.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class JSONResponse extends AbstractResponse {

	public JSONResponse() {
		mediaType = MediaType.APPLICATION_JSON;
	}
	
	/**
	 * Writes {@code JSONResponse} to the current {@code WebContext}.
	 * 
	 * @param context The current {@code WebContext}.
	 */
	public void to(WebContext context) {
		if (entity == null) {
			return;
		}
		
		HttpServletResponse response = context.response();
		
		try {
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Type", mediaType);
			
			if (status > 0) {
				response.setStatus(status);
			}
			
			if (entity instanceof String) {
				response.getWriter().write((String) entity);
				return;
			}
			
			GsonBuilder builder = new GsonBuilder();
			Set<Class<?>> classes = new HashSet<Class<?>>();
			parse(entity.getClass(), classes);
			
			for (Class<?> clazz : classes) {
				builder.registerTypeAdapter(clazz, new ValueTypeJsonSerializer<Object>());
			}
			
			// XXX: Any options?
			Gson gson = builder.create();
			response.getWriter().write(gson.toJson(entity));
		} catch (IOException e) {
			throw new UncheckedException(e);
		}
	}
	
	private void parse(Class<?> clazz, Set<Class<?>> values) {
		if (Types.isArray(clazz) || Types.isCollection(clazz)) {
			parse(Types.getElementType(clazz), values);
		} else if (Types.isUserDefinedValueType(clazz)
				|| Types.isCoreValueType(clazz)) {
			if (!values.contains(clazz)) {
				values.add(clazz);
			}
			
			return;
		}
		
		for (Field field : clazz.getDeclaredFields()) {
			parse(field.getType(), values);
		}
	}

}
