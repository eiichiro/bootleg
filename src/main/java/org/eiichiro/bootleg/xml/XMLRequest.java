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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eiichiro.bootleg.AbstractRequest;
import org.eiichiro.bootleg.Types;
import org.eiichiro.bootleg.WebContext;
import org.eiichiro.reverb.lang.UncheckedException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.google.common.base.Function;

/**
 * {@code XMLRequest} is a XML-based implementation of {@code Request}.
 * If the requested MIME media type is 'application/xml', default configuration of 
 * Bootleg uses this implementation.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class XMLRequest extends AbstractRequest {

	private Node node;

	/**
	 * Constructs a new {@code XMLRequest} from the current {@code WebContext}.
	 * 
	 * @param context The current {@code WebContext}.
	 */
	public void from(WebContext context) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(context.request().getReader()));
			node = document.getDocumentElement();
		} catch (Exception e) {
			logger.warn("Cannot parse XML document into DOM object", e);
			throw new UncheckedException(e);
		}
		
		super.from(context);
	}
	
	/**
	 * Returns the Web endpoint method parameter value from the current HTTP 
	 * request body sent as XML format.
	 * This method supports the following parameter declaration independently. 
	 * Other than them are the same as {@link AbstractRequest}.
	 * <ol>
	 * <li>Named collection of user-defined object type</li>
	 * <li>Named user-defined object type</li>
	 * </ol>
	 * 
	 * @param type Web endpoint method parameter type.
	 * @param name Web endpoint method parameter name.
	 * @return Web endpoint method parameter value.
	 */
	@Override
	protected Object body(Type type, String name) {
		return body(type, name, node);
	}
	
	@SuppressWarnings("unchecked")
	private Object body(Type type, String name, final Node node) {
		Function<String, Object> value = new Function<String, Object>() {

			public Object apply(String name) {
				NodeList nodes = node.getChildNodes();
				
				for (int i = 0; i < nodes.getLength(); i++) {
					Node item = nodes.item(i);
					
					if (item.getNodeType() == Node.ELEMENT_NODE
							&& item.getNodeName().equals(name)) {
						return item;
					}
				}
				
				return null;
			}
			
		};
		Function<String, Collection<Object>> values = new Function<String, Collection<Object>>() {

			public Collection<Object> apply(String name) {
				Collection<Object> collection = new ArrayList<Object>();
				NodeList nodes = node.getChildNodes();
				
				for (int i = 0; i < nodes.getLength(); i++) {
					Node item = nodes.item(i);
					
					if (item.getNodeType() == Node.ELEMENT_NODE
							&& item.getNodeName().equals(name)) {
						collection.add(item);
					}
				}
				
				return (collection.isEmpty()) ? null : collection;
			}
			
		};
		
		if (name != null && !name.isEmpty()) {
			if (Types.isCollection(type)) {
				if (Types.isSupportedCollection(type)) {
					Class<?> elementType = Types.getElementType(type);
					
					if (!Types.isCoreValueType(elementType)
							&& !Types.isUserDefinedValueType(elementType)) {
						// Named collection of user-defined object type.
						Collection<Object> nodes = values.apply(name);
						
						if (nodes == null) {
							logger.debug("Collection named [" + name + "] not found");
							return nodes;
						}
						
						try {
							Class<?> implementationType = Types.getDefaultImplementationType(type);
							Collection<Object> collection = (Collection<Object>) implementationType.newInstance();
							
							try {
								for (Object n : nodes) {
									Object instance = elementType.newInstance();
									
									for (Field field : elementType.getDeclaredFields()) {
										Object object = body(field.getGenericType(), field.getName(), (Node) n);
										
										if (object != null) {
											field.setAccessible(true);
											field.set(instance, object);
										}
									}
									
									collection.add(instance);
								}
								
								return (collection.isEmpty()) ? null : collection;
							} catch (Exception e) {
								logger.debug("Cannot instantiate ["
										+ Types.getDefaultImplementationType(type)
										+ "] (Default implementation type of ["
										+ type + "])", e);
							}
							
						} catch (Exception e) {
							logger.warn("Cannot instantiate [" + elementType
									+ "] (Collection element type of [" + type
									+ "])", e);
						}
						
						return null;
					}
				}
				
			} else if (!Types.isCoreValueType(type)
					&& !Types.isUserDefinedValueType(type)) {
				// Named user-defined object type.
				Class<?> rawType = Types.getRawType(type);
				Object n = value.apply(name);
				
				try {
					Object instance = rawType.newInstance();
					
					for (Field field : rawType.getDeclaredFields()) {
						Object object = body(field.getGenericType(), field.getName(), (Node) n);
						
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
		}
		
		return parameter(type, name, value, values);
	}

	/**
	 * Converts the specified object to the specified type.
	 * The object to be converted must be a {@code Node} that has no child, or 
	 * this method return <code>null</code>.
	 * 
	 * @param object The object to be converted
	 * @param type The type to which the specified object be converted.
	 * @return The converted object.
	 */
	@Override
	protected Object convert(Object object, Class<?> type) {
		if (object instanceof Node) {
			Node node = (Node) object;
			NodeList nodes = node.getChildNodes();
			Text text = null;
			
			for (int i = 0; i < nodes.getLength(); i++) {
				Node child = nodes.item(i);
				
				if (child.getNodeType() == Node.TEXT_NODE) {
					text = (Text) child;
				}
			}
			
			if (text == null) {
				logger.warn("Parameter [" + object + "] cannot be converted to ["
						+ type + "]; Converted 'object' must have one child ["
						+ Text.class + "] node");
				return null;
			}
			
			return super.convert(text.getNodeValue(), type);
		} else {
			logger.warn("Parameter [" + object + "] cannot be converted to ["
					+ type + "]; Converted 'object' must be a ["
					+ Node.class + "]");
			return null;
		}
	}
	
	/**
	 * Converts the specified object to the specified user-defined value type.
	 * The object to be converted must be a {@code Node} that has no child, or 
	 * this method return <code>null</code>.
	 * 
	 * @param object The object to be converted
	 * @param type The user-defined value type to which the specified object be 
	 * converted.
	 * @return The converted object.
	 */
	@Override
	protected Object convertUserDefinedValueType(Object object, Class<?> type) {
		if (object instanceof Node) {
			Node node = (Node) object;
			NodeList nodes = node.getChildNodes();
			Text text = null;
			
			for (int i = 0; i < nodes.getLength(); i++) {
				Node child = nodes.item(i);
				
				if (child.getNodeType() == Node.TEXT_NODE) {
					text = (Text) child;
				}
			}
			
			if (text == null) {
				logger.warn("Parameter [" + object + "] cannot be converted to ["
						+ type + "]; Converted 'object' must have one child ["
						+ Text.class + "] node");
				return null;
			}
			
			return super.convertUserDefinedValueType(text.getNodeValue(), type);
		} else {
			logger.warn("Parameter [" + object + "] cannot be converted to ["
					+ type + "]; Converted 'object' must be a ["
					+ Node.class + "]");
			return null;
		}
	}
	
}
