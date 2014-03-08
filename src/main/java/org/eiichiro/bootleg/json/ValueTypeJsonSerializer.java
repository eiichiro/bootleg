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

import java.lang.reflect.Type;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.AbstractConverter;
import org.eiichiro.bootleg.Types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * {@code ValueTypeJsonSerializer} is a custom JSON serializer for user-defined 
 * value type object.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class ValueTypeJsonSerializer<T> implements JsonSerializer<T> {

	/**
	 * Serializes the specified user-defined value type object to 
	 * <code>JsonPrimitive</code> representation.
	 */
	public JsonElement serialize(T src, Type typeOfSrc,
			JsonSerializationContext context) {
		Class<?> type = Types.getRawType(typeOfSrc);
		Converter converter = ConvertUtils.lookup(type);
		
		if ((converter != null && converter instanceof AbstractConverter)) {
			String string = (String) ConvertUtils.convert(src, String.class);
			
			if (string != null) {
				return new JsonPrimitive(string);
			}
		}
		
		return new JsonPrimitive(src.toString());
	}

}
