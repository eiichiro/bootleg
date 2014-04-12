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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.ClassUtils;
import org.eiichiro.bootleg.annotation.Application;
import org.eiichiro.bootleg.annotation.Body;
import org.eiichiro.bootleg.annotation.Header;
import org.eiichiro.bootleg.annotation.Path;
import org.eiichiro.bootleg.annotation.Query;
import org.eiichiro.bootleg.annotation.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * {@code AbstractRequest} is an abstract base class of {@link Request} 
 * implementation and provides default behaviors common to most {@code Request} 
 * implementation classes.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public abstract class AbstractRequest implements Request {

	/** Logger. */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/** Web context. */
	protected WebContext context;
	
	/**
	 * Constructs this HTTP request from the specified Web context.
	 * 
	 * @param context Web context.
	 */
	public void from(WebContext context) {
		this.context = context;
	}

	/**
	 * Constructs Web endpoint parameter from the specified source list or type.
	 * This method is invoked for every Web endpoint method parameter by 
	 * {@link Receive}. This method processes according to the following steps: 
	 * <ol>
	 * <li>If the specified type is an array type, returns <code>null</code>. An 
	 * array type is not supported in this class.</li>
	 * <li>If the specified type is built-in type (See {@link Types#isBuiltinType(Type)}), 
	 * returns the built-in instance.</li>
	 * <li>Constructs the value as the specified type from the specified sources 
	 * and names. If the Web endpoint parameter is declared without any source 
	 * annotation except built-in type, this method always returns <code>null</code>.
	 * </li>
	 * <li>If the construction result is not <code>null</code>, returns the 
	 * result to the client.</li>
	 * <li>If the result is <code>null</code> of primitive type, returns the 
	 * default value of each primitive type to the client.</li>
	 * <li>If the result is <code>null</code> of supported collection type (See 
	 * {@link Types#isSupportedCollection(Type)}), returns the empty collection 
	 * of the specified type to the client.</li>
	 * <li>Otherwise, returns <code>null</code>.</li>
	 * </ol>
	 * 
	 * @param type The type of Web endpoint parameter.
	 * @param sources The source list from which Web endpoint parameter is 
	 * constructed.
	 * @return Web endpoint method parameter.
	 */
	public Object get(Type type, List<Annotation> sources) {
		if (Types.isArray(type)) {
			logger.warn("Array type is not supported in [" + getClass() + "]");
			return null;
		} else if (Types.isBuiltinType(type)) {
			return builtin(type);
		}
		
		for (Annotation source : sources) {
			Object parameter = null;
			
			if (source instanceof Query) {
				Query query = (Query) source;
				parameter = query(type, query.value());
			} else if (source instanceof Body) {
				Body body = (Body) source;
				parameter = body(type, body.value());
			} else if (source instanceof Header) {
				Header header = (Header) source;
				parameter = header(type, header.value());
			} else if (source instanceof org.eiichiro.bootleg.annotation.Cookie) {
				org.eiichiro.bootleg.annotation.Cookie cookie = (org.eiichiro.bootleg.annotation.Cookie) source;
				parameter = cookie(type, cookie.value());
			} else if (source instanceof Session) {
				Session session = (Session) source;
				parameter = session(type, session.value());
			} else if (source instanceof Application) {
				Application application = (Application) source;
				parameter = application(type, application.value());
			} else if (source instanceof Path) {
				Path path = (Path) source;
				parameter = path(type, path.value());
			} else {
				logger.warn("Unknown source [" + source + "]");
			}
			
			if (parameter != null) {
				return parameter;
			}
		}
		
		if (Types.isPrimitive(type)) {
			logger.debug("Cannot construct [" + type + "] primitive; Returns the default value");
			return primitive(type);
		} else if (Types.isCollection(type)) {
			if (Types.isSupportedCollection(type)) {
				logger.debug("Cannot construct [" + type + "] collection; Returns the empty colleciton");
				return Types.getEmptyCollection(type);
			} else {
				logger.warn("Collection type " + type + " is not supported in [" + getClass() + "]");
				return null;
			}
		}
		
		StringBuilder builder = new StringBuilder();
		
		for (Annotation source : sources) {
			builder.append(source + " ");
		}
		
		logger.debug("Cannot construct Web endpoint method parameter [" + builder + type + "]");
		return null;
	}
	
	/**
	 * Returns the value of built-in type.
	 * 
	 * @param type The parameter type.
	 * @return The value of built-in type.
	 */
	protected Object builtin(Type type) {
		Class<?> rawType = Types.getRawType(type);
		
		if (rawType.equals(WebContext.class)) {
			return context;
		} else if (rawType.equals(HttpServletRequest.class)) {
			return context.request();
		} else if (rawType.equals(HttpServletResponse.class)) {
			return context.response();
		} else if (rawType.equals(HttpSession.class)) {
			return context.session();
		} else if (rawType.equals(ServletContext.class)) {
			return context.application();
		} else {
			// org.eiichiro.bootleg.Request.
			return this;
		}
	}
	
	/**
	 * Returns the default value of the specified primitive type.
	 * 
	 * @param type The primitive type.
	 * @return The default value of the specified primitive type.
	 */
	protected Object primitive(Type type) {
		Class<?> rawType = Types.getRawType(type);
		
		if (rawType.equals(Boolean.TYPE)) {
			return (boolean) false;
		} else if (rawType.equals(Character.TYPE)) {
			return (char) 0;
		} else if (rawType.equals(Byte.TYPE)) {
			return (byte) 0;
		} else if (rawType.equals(Double.TYPE)) {
			return (double) 0.0;
		} else if (rawType.equals(Float.TYPE)) {
			return (float) 0.0;
		} else if (rawType.equals(Integer.TYPE)) {
			return (int) 0;
		} else {
			// short.
			return (short) 0;
		}
	}
	
	/**
	 * Constructs the value of Web endpoint parameter.
	 * Web endpoint parameter declaration falls into the several patterns and 
	 * each pattern has the own construction logic as below: 
	 * <ol>
	 * <li>Named array type - Unsupported. Always returns <code>null</code>.</li>
	 * <li>Named collection of core value type - Supported. The collection 
	 * instance is constructed according to the default implementation type 
	 * (See {@code Types#getDefaultImplementationType(Type)}). If the collection 
	 * type is not supported (See {@code Types#isSupportedCollection(Type)}), 
	 * this method returns <code>null</code>. Each of the collection elements is 
	 * constructed from the values that {@code values} has returned as the same 
	 * way of the following core value type. If the constructed collection has 
	 * no element, this method returns <code>null</code>.</li>
	 * <li>Named collection of user-defined value type - Supported. The 
	 * collection instance is constructed according to the default 
	 * implementation type (as the same as collection of core value type). Each 
	 * of the collection elements is constructed from the values that 
	 * {@code values} has returned as the same way of following user-defined 
	 * value type.
	 * </li>
	 * <li>Named collection of user-defined object type - Unsupported. Always 
	 * returns <code>null</code>.</li>
	 * <li>Named core value type - Supported. The value that {@code value} has 
	 * returned is converted to the core value type. If the conversion 
	 * failed, returns <code>null</code></li>
	 * <li>Named user-defined value type - Supported. If the value that 
	 * {@code value} has returned is assignable to the user-defined value type, 
	 * returns it. If the value that {@code value} has returned is 
	 * {@code String.class}, this method constructs the user-defined value type 
	 * with public constructor that takes one String.class parameter or public 
	 * static <code>valueOf(String.class)</code> method. If the conversion 
	 * failed, returns <code>null</code>.
	 * </li>
	 * <li>Named user-defined object type - Partially supported. If the value 
	 * that {@code value} method has returned is assignable to the user-defined 
	 * object type, returns it. Otherwise, returns <code>null</code> (does not 
	 * any type conversion). </li>
	 * <li>Not named array type - Unsupported. Always returns <code>null</code>.
	 * </li>
	 * <li>Not named collection of core value type - Unsupported. Always returns 
	 * <code>null</code>.</li>
	 * <li>Not named collection of user-defined value type - Unsupported. Always 
	 * returns <code>null</code>.</li>
	 * <li>Not named collection of user-defined object type - Unsupported. 
	 * Always returns <code>null</code>.</li>
	 * <li>Not named core value type - Unsupported. Always returns 
	 * <code>null</code>.</li>
	 * <li>Not named user-defined value type - Unsupported. Always returns 
	 * <code>null</code>.</li>
	 * <li>Not named user-defined object type - Supported. First, this method 
	 * instantiates the user-defined object instance form public default 
	 * constructor, and then constructs each of the instances' fields value 
	 * according to the named type construction described above (by invoking 
	 * {@code #newParameter(WebContext, Type, String)} with the field 
	 * type and field name). If the instantiation is failed, returns <code>null
	 * </code>.
	 * </li>
	 * </ol>
	 * This method is overridable. You can provide your own value construction 
	 * to Web endpoint parameter by overriding this method.
	 * 
	 * @param type The parameter type.
	 * @param name The parameter name.
	 * @param value The {@code Function} that returns the value corresponding to 
	 * the specified name from its own source.
	 * The returned value is converted to appropriate parameter type.
	 * @param values The {@code Function} that returns the value corresponding 
	 * to the specified name from its own source, as {@code Collection} view.
	 * @return The value of Web endpoint parameter.
	 */
	@SuppressWarnings("unchecked")
	protected Object parameter(Type type, String name,
			Function<String, Object> value,
			Function<String, Collection<Object>> values) {
		if (name != null && !name.isEmpty()) {
			// Named parameter construction.
			if (Types.isArray(type)) {
				// Named array type.
				logger.debug("Array type [" + type + "] is not supported in ["
						+ getClass() + "]");
			} else if (Types.isCollection(type)) {
				// Named collection type.
				if (Types.isSupportedCollection(type)) {
					Collection<Object> objects = values.apply(name);
					
					if (objects == null) {
						logger.debug("Collection named [" + name + "] not found");
					} else {
						Class<?> elementType = Types.getElementType(type);
						boolean coreValueType = Types.isCoreValueType(elementType);
						
						if (!coreValueType && !Types.isUserDefinedValueType(elementType)) {
							// Named collection of user-defined object.
							logger.debug("Collection element type ["
									+ elementType + "] is not supported in ["
									+ getClass() + "]");
						} else {
							try {
								Class<?> implementationType = Types.getDefaultImplementationType(type);
								Collection<Object> collection = (Collection<Object>) implementationType.newInstance();
								
								for (Object object : objects) {
									// Named collection of core value type.
									// Named collection of user-defined value type.
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
								
								return (!collection.isEmpty()) ? collection : null;
							} catch (Exception e) {
								logger.debug("Cannot instantiate ["
										+ Types.getDefaultImplementationType(type)
										+ "] (Default implementation type of ["
										+ type + "])", e);
							}
						}
					}
					
				} else {
					logger.debug("Parameter type [" + type
							+ "] is not supported in [" + getClass() + "]");
				}
				
			} else if (Types.isCoreValueType(type)) {
				// Named core value type.
				Class<?> rawType = Types.getRawType(type);
				Object object = value.apply(name);
				
				if (object == null) {
					logger.debug("Value named [" + name + "] not found");
				} else {
					Object convert = convert(object, rawType);
					
					if (convert != null 
							&& ClassUtils.primitiveToWrapper(rawType).isAssignableFrom(convert.getClass())) {
						return convert;
					} else {
						logger.warn("Parameter [" + convert
								+ "] cannot be converted to [" + type + "]");
					}
				}
				
			} else if (Types.isUserDefinedValueType(type)) {
				// Named user-defined value type.
				Object object = value.apply(name);
				Class<?> rawType = Types.getRawType(type);
				
				if (object == null) {
					logger.debug("Value named [" + name + "] not found");
				} else {
					Object userDefinedValueType = convertUserDefinedValueType(object, rawType);
					
					if (userDefinedValueType == null) {
						logger.warn("Parameter [" + object
								+ "] cannot be converted to [" + type + "]");
					}
					
					return userDefinedValueType;
				}
				
			} else {
				// Named user-defined object type.
				Object object = value.apply(name);
				
				if (object == null) {
					logger.debug("Value named [" + name + "] not found");
				} else if (Types.getRawType(type).isAssignableFrom(object.getClass())) {
					return object;
				} else {
					logger.warn("Parameter [" + object
							+ "] cannot be converted to [" + type + "]");
				}
			}
			
		} else {
			// Non-named parameter construction.
			if (Types.isArray(type) || Types.isCollection(type)
					|| Types.isCoreValueType(type)
					|| Types.isUserDefinedValueType(type)) {
				// Not named array type.
				// Not named collection (of core value type or user-defined object type).
				// Not named core value type.
				// Not named user-defined value type.
				logger.debug("Non-named parameter type [" + type
						+ "] is not supported in [" + getClass() + "]");
			} else {
				// Not named user-defined object type.
				Class<?> rawType = Types.getRawType(type);
				
				try {
					Object instance = rawType.newInstance();
					
					for (Field field : rawType.getDeclaredFields()) {
						Object object = parameter(field.getGenericType(), field.getName(), value, values);
						
						if (object != null) {
							field.setAccessible(true);
							field.set(instance, object);
						}
					}
					
					return instance;
				} catch (Exception e) {
					logger.warn("Cannot instantiate [" + type + "]", e);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Converts the specified object to the specified type.
	 * This method is overridable. You can provide your own conversion to the 
	 * parameter construction by overriding this method.
	 * 
	 * @param object The object to be converted
	 * @param type The type to which the specified object be converted.
	 * @return The converted object.
	 */
	protected Object convert(Object object, Class<?> type) {
		try {
			return ConvertUtils.convert(object, type);
		} catch (Exception e) {
			logger.warn("Cannot convert [" + object + "] to [" + type + "]", e);
			return null;
		}
	}
	
	/**
	 * Converts the specified object to the specified user-defined value type.
	 * This method is overridable. You can provide your own conversion to the 
	 * parameter construction by overriding this method.
	 * 
	 * @param object The object to be converted.
	 * @param type The type to which the specified object be converted.
	 * @return The converted object.
	 */
	protected Object convertUserDefinedValueType(Object object, Class<?> type) {
		if (type.isAssignableFrom(object.getClass())) {
			return object;
		} else if (object instanceof String) {
			try {
				Constructor<?> constructor = type.getConstructor(String.class);
				return constructor.newInstance(object);
			} catch (Exception e) {
				logger.debug("Cannot invoke [public " + type.getName() 
						+ "(String.class)] constrcutor on [" + type + "]", e);
			}
			
			try {
				return type.getMethod("valueOf", String.class).invoke(null, object);
			} catch (Exception e1) {
				logger.debug("Cannot invoke [public static " 
						+ type.getName() + ".valueOf(String.class)]" 
						+ "method on [" + type + "]", e1);
			}
			
		} else {
			logger.warn("Parameter [" + object
					+ "] cannot be converted to [" + type + "]");
		}
		
		return null;
	}
	
	/**
	 * Returns the Web endpoint method parameter from query string.
	 * 
	 * @param type The parameter type.
	 * @param name The parameter name.
	 * @return The Web endpoint method parameter from the HTTP request's query 
	 * string.
	 */
	protected Object query(Type type, String name) {
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
	
	/**
	 * Returns the Web endpoint method parameter from HTTP request header.
	 * 
	 * @param type The parameter type.
	 * @param name The parameter name.
	 * @return The Web endpoint method parameter from the HTTP request header.
	 */
	protected Object header(Type type, String name) {
		return parameter(type, name, new Function<String, Object>() {

			public Object apply(String name) {
				return context.request().getHeader(name);
			}
			
		}, new Function<String, Collection<Object>>() {

			@SuppressWarnings("unchecked")
			public Collection<Object> apply(String name) {
				HttpServletRequest request = context.request();
				Map<String, Object> map = new TreeMap<String, Object>();
				
				for (Object object : Collections.list(request.getHeaderNames())) {
					String key = (String) object;
					
					if (key.startsWith(name + "[")) {
						map.put(key, request.getHeader(key));
					}
				}
				
				return (map.isEmpty()) ? null : map.values();
			}
			
		});
	}
	
	/**
	 * Returns the Web endpoint method parameter from cookie in the HTTP request.
	 * 
	 * @param type The parameter type.
	 * @param name The parameter name.
	 * @return The Web endpoint method parameter from cookie in the HTTP request.
	 */
	protected Object cookie(Type type, String name) {
		return parameter(type, name, new Function<String, Object>() {

			public Object apply(String name) {
				Cookie[] cookies = context.request().getCookies();
				
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						if (cookie.getName().equals(name)) {
							return cookie.getValue();
						}
					}
				}
				
				return null;
			}
			
		}, new Function<String, Collection<Object>>() {

			public Collection<Object> apply(String name) {
				HttpServletRequest request = context.request();
				Map<String, Object> map = new TreeMap<String, Object>();
				Cookie[] cookies = request.getCookies();
				
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						String key = cookie.getName();
						
						if (key.startsWith(name + "[")) {
							map.put(key, cookie.getValue());
						}
					}
				}
				
				return (map.isEmpty()) ? null : map.values();
			}
			
		});
	}
	
	/**
	 * Returns the Web endpoint method parameter from HTTP session.
	 * 
	 * @param type The parameter type.
	 * @param name The parameter name.
	 * @return The Web endpoint method parameter from HTTP session.
	 */
	protected Object session(Type type, String name) {
		return parameter(type, name, new Function<String, Object>() {

			public Object apply(String name) {
				return context.session().getAttribute(name);
			}
			
		}, new Function<String, Collection<Object>>() {

			@SuppressWarnings("unchecked")
			public Collection<Object> apply(String name) {
				HttpSession session = context.session();
				Object attribute = session.getAttribute(name);
				
				if (attribute instanceof Collection<?>) {
					return (Collection<Object>) attribute;
				}
				
				Map<String, Object> map = new TreeMap<String, Object>();
				
				for (Object object : Collections.list(session.getAttributeNames())) {
					String key = (String) object;
					
					if (key.startsWith(name + "[")) {
						map.put(key, session.getAttribute(key));
					}
				}
				
				return (map.isEmpty()) ? null : map.values();
			}
			
		});
	}
	
	/**
	 * Returns the Web endpoint method parameter from Web application 
	 * ({@code ServletContext}).
	 * 
	 * @param type The parameter type.
	 * @param name The parameter name.
	 * @return The Web endpoint method parameter from Web application 
	 * ({@code ServletContext}).
	 */
	protected Object application(Type type, String name) {
		return parameter(type, name, new Function<String, Object>() {

			public Object apply(String name) {
				return context.application().getAttribute(name);
			}
			
		}, new Function<String, Collection<Object>>() {

			@SuppressWarnings("unchecked")
			public Collection<Object> apply(String name) {
				ServletContext servletContext = context.application();
				Object attribute = servletContext.getAttribute(name);
				
				if (attribute instanceof Collection<?>) {
					return (Collection<Object>) attribute;
				}
				
				Map<String, Object> map = new TreeMap<String, Object>();
				
				for (Object object : Collections.list(servletContext.getAttributeNames())) {
					String key = (String) object;
					
					if (key.startsWith(name + "[")) {
						map.put(key, servletContext.getAttribute(key));
					}
				}
				
				return (map.isEmpty()) ? null : map.values();
			}
			
		});
	}
	
	protected Object path(Type type, String name) {
		return parameter(type, name, new Function<String, Object>() {

			@SuppressWarnings("unchecked")
			public Object apply(String name) {
				Map<String, String> variables = (Map<String, String>) context.request().getAttribute(PATH);
				
				if (variables != null) {
					return variables.get(name);
				}
				
				return null;
			}
			
		}, new Function<String, Collection<Object>>() {

			public Collection<Object> apply(String name) {
				return null;
			}
			
		});
	}
	
	/**
	 * Returns the Web endpoint method parameter from the HTTP request's posted 
	 * form.
	 * 
	 * @param type The parameter type.
	 * @param name The parameter name.
	 * @return The endpoint method parameter from the HTTP request's posted form.
	 */
	protected abstract Object body(Type type, String name);

}
