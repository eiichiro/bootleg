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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.ClassUtils;
import org.eiichiro.bootleg.AbstractRequest;
import org.eiichiro.bootleg.Types;
import org.eiichiro.bootleg.WebContext;
import org.eiichiro.reverb.lang.UncheckedException;

import com.google.common.base.Function;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * {@code JSONRequest} is a JSON-based implementation of {@code Request}.
 * If the requested MIME media type is 'application/json', Bootleg uses this 
 * implementation by default.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class JSONRequest extends AbstractRequest {

	private JsonElement element;
	
	/**
	 * Constructs a new {@code JSONRequest} from the current {@code WebContext}.
	 * 
	 * @param context The current {@code WebContext}.
	 */
	public void from(WebContext context) {
		try {
			element = new JsonParser().parse(context.request().getReader());
		} catch (Exception e) {
			logger.warn("Cannot parse JSON string into a parse tree", e);
			throw new UncheckedException(e);
		}
		
		super.from(context);
	}
	
	/**
	 * Returns the Web endpoint method parameter value from the current HTTP 
	 * request body sent as JSON format.
	 * This method supports the following parameter declaration independently. 
	 * Other than them are the same as {@link AbstractRequest}.
	 * <ol>
	 * <li>Named collection of user-defined object type</li>
	 * <li>Named user-defined object type</li>
	 * <li>No-named collection of core value type</li>
	 * <li>No-named collection of user-defined value type</li>
	 * <li>No-named collection of user-defined object type</li>
	 * </ol>
	 * 
	 * @param type Web endpoint method parameter type.
	 * @param name Web endpoint method parameter name.
	 * @return Web endpoint method parameter value.
	 */
	@Override
	protected Object body(Type type, String name) {
		return body(type, name, element);
	}
	
	@SuppressWarnings("unchecked")
	private Object body(Type type, String name, final JsonElement element) {
		Function<String, Object> value = new Function<String, Object>() {

			public Object apply(String name) {
				if (element instanceof JsonObject) {
					JsonObject jsonObject = (JsonObject) element;
					return jsonObject.get(name);
				} else {
					return null;
				}
			}
			
		};
		Function<String, Collection<Object>> values = new Function<String, Collection<Object>>() {

			public Collection<Object> apply(String name) {
				if (element instanceof JsonObject) {
					JsonObject jsonObject = (JsonObject) element;
					JsonElement jsonElement = jsonObject.get(name);
					
					if (jsonElement != null && jsonElement.isJsonArray()) {
						Collection<Object> collection = new ArrayList<Object>();
						Iterator<JsonElement> iterator = jsonElement.getAsJsonArray().iterator();
						
						while (iterator.hasNext()) {
							collection.add(iterator.next());
						}
						
						return (collection.isEmpty()) ? null : collection;
					} else {
						logger.warn("Element [" + name + "] is not a JSON array");
						return null;
					}
					
				} else if ((name == null || name.isEmpty()) && element instanceof JsonArray) {
					JsonArray array = (JsonArray) element;
					Collection<Object> collection = new ArrayList<Object>();
					Iterator<JsonElement> iterator = array.iterator();
					
					while (iterator.hasNext()) {
						collection.add(iterator.next());
					}
					
					return (collection.isEmpty()) ? null : collection;
				} else {
					return null;
				}
			}
			
		};
		
		if (name != null && !name.isEmpty()) {
			if (Types.isCollection(type)) {
				if (Types.isSupportedCollection(type)) {
					Class<?> elementType = Types.getElementType(type);
					
					if (!Types.isCoreValueType(elementType)
							&& !Types.isUserDefinedValueType(elementType)) {
						// Named collection of user-defined object type.
						Collection<Object> jsonElements = values.apply(name);
						
						if (jsonElements == null) {
							logger.debug("Collection named [" + name + "] not found");
							return jsonElements;
						}
						
						try {
							Class<?> implementationType = Types.getDefaultImplementationType(type);
							Collection<Object> collection = (Collection<Object>) implementationType.newInstance();
							
							try {
								for (Object jsonElement : jsonElements) {
									Object instance = elementType.newInstance();
									
									for (Field field : elementType.getDeclaredFields()) {
										Object object = body(field.getGenericType(), field.getName(), (JsonElement) jsonElement);
										
										if (object != null) {
											field.setAccessible(true);
											field.set(instance, object);
										}
									}
									
									collection.add(instance);
								}
								
								return (collection.isEmpty()) ? null : collection;
							} catch (Exception e) {
								logger.warn("Cannot instantiate [" + elementType
										+ "] (Collection element type of [" + type
										+ "])", e);
							}
							
						} catch (Exception e) {
							logger.debug("Cannot instantiate ["
									+ Types.getDefaultImplementationType(type)
									+ "] (Default implementation type of ["
									+ type + "])", e);
						}
						
						return null;
					}
				}
				
			} else if (!Types.isCoreValueType(type)
					&& !Types.isUserDefinedValueType(type)) {
				// Named user-defined object type.
				Class<?> rawType = Types.getRawType(type);
				Object jsonElement = value.apply(name);
				
				try {
					Object instance = rawType.newInstance();
					
					for (Field field : rawType.getDeclaredFields()) {
						Object object = body(field.getGenericType(), field.getName(), (JsonElement) jsonElement);
						
						if (object != null) {
							field.setAccessible(true);
							field.set(instance, object);
						}
					}
					
					return instance;
				} catch (Exception e) {
					logger.warn("Cannot instantiate [" + type + "]", e);
				}
				
				return null;
			}
			
		} else {
			if (Types.isCollection(type)) {
				if (Types.isSupportedCollection(type)) {
					Collection<Object> objects = values.apply(name);
					
					if (objects == null) {
						logger.debug("Posted JSON element is not a JSON array");
						return objects;
					}
					
					try {
						Class<?> implementationType = Types.getDefaultImplementationType(type);
						Collection<Object> collection = (Collection<Object>) implementationType.newInstance();
						Class<?> elementType = Types.getElementType(type);
						boolean coreValueType = Types.isCoreValueType(elementType);
						
						try {
							if (coreValueType || Types.isUserDefinedValueType(elementType)) {
								// No-named collection of core value type.
								// No-named collection of user-defined value type.
								for (Object object : objects) {
									Object convert = (coreValueType) ? convert(object, elementType)
											: convertUserDefinedValueType(object, elementType);
									
									if (convert != null
											&& ClassUtils.primitiveToWrapper(elementType).isAssignableFrom(convert.getClass())) {
										collection.add(convert);
									} else {
										logger.debug("Parameter [" + convert
												+ "] cannot be converted to ["
												+ elementType + "]");
									}
								}
								
							} else {
								// No-named collection of user-defined object type.
								for (Object jsonElement : objects) {
									Object instance = elementType.newInstance();
									
									for (Field field : elementType.getDeclaredFields()) {
										Object object = body(field.getGenericType(), field.getName(), (JsonElement) jsonElement);
										
										if (object != null) {
											field.setAccessible(true);
											field.set(instance, object);
										}
									}
									
									collection.add(instance);
								}
							}
							
							return (collection.isEmpty()) ? null : collection;
						} catch (Exception e) {
							logger.warn("Cannot instantiate [" + elementType
									+ "] (Collection element type of [" + type
									+ "])", e);
						}
						
					} catch (Exception e) {
						logger.debug("Cannot instantiate ["
								+ Types.getDefaultImplementationType(type)
								+ "] (Default implementation type of ["
								+ type + "])", e);
					}
				}
				
				return null;
			}
		}
		
		return parameter(type, name, value, values);
	}
	
	/**
	 * Converts the specified object to the specified type.
	 * The object to be converted must be a {@code JsonPrimitive}, or this 
	 * method return <code>null</code>.
	 * 
	 * @param object The object to be converted
	 * @param type The type to which the specified object be converted.
	 * @return The converted object.
	 */
	@Override
	protected Object convert(Object object, Class<?> type) {
		if (object instanceof JsonPrimitive) {
			JsonPrimitive primitive = (JsonPrimitive) object;
			return super.convert(primitive.getAsString(), type);
		} else {
			logger.warn("Parameter [" + object + "] cannot be converted to ["
					+ type + "]; Converted 'object' must be a ["
					+ JsonPrimitive.class + "]");
			return null;
		}
	}
	
	/**
	 * Converts the specified object to the specified user-defined value type.
	 * The object to be converted must be a {@code JsonPrimitive}, or this 
	 * method return <code>null</code>.
	 * 
	 * @param object The object to be converted
	 * @param type The user-defined value type to which the specified object be 
	 * converted.
	 * @return The converted object.
	 */
	@Override
	protected Object convertUserDefinedValueType(Object object, Class<?> type) {
		if (object instanceof JsonPrimitive) {
			JsonPrimitive primitive = (JsonPrimitive) object;
			return super.convertUserDefinedValueType(primitive.getAsString(), type);
		} else {
			logger.warn("Parameter [" + object + "] cannot be converted to ["
					+ type + "]; Converted 'object' must be a ["
					+ JsonPrimitive.class + "]");
			return null;
		}
	}
	
}
