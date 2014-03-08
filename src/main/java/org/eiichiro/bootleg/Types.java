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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.ClassUtils;

/**
 * {@code Types} is a type system utility for Bootleg built-in components.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public abstract class Types {

	private Types() {}

	private static Set<Class<?>> builtins = new CopyOnWriteArraySet<Class<?>>();
	
	private static Set<Class<?>> collections = new CopyOnWriteArraySet<Class<?>>();
	
	private static Set<Class<?>> values = new CopyOnWriteArraySet<Class<?>>();
	
	private static Map<Class<?>, Class<?>> implementations = new ConcurrentHashMap<Class<?>, Class<?>>();
	
	private static Map<Class<?>, Collection<?>> empties = new ConcurrentHashMap<Class<?>, Collection<?>>();
	
	static {
		// Built-in types.
		builtins.add(WebContext.class);
		builtins.add(HttpServletRequest.class);
		builtins.add(HttpServletResponse.class);
		builtins.add(HttpSession.class);
		builtins.add(ServletContext.class);
		builtins.add(Request.class);
		
		// Interfaces.
		collections.add(Collection.class);
		collections.add(List.class);
		collections.add(Set.class);
		collections.add(SortedSet.class);
		collections.add(NavigableSet.class);
		collections.add(Queue.class);
		collections.add(Deque.class);
		
		// Implementations.
		collections.add(ArrayList.class);
		collections.add(LinkedList.class);
		collections.add(CopyOnWriteArrayList.class);
		collections.add(Stack.class);
		collections.add(Vector.class);
		collections.add(HashSet.class);
		collections.add(LinkedHashSet.class);
		collections.add(CopyOnWriteArraySet.class);
		collections.add(TreeSet.class);
		collections.add(ConcurrentSkipListSet.class);
		collections.add(PriorityQueue.class);
		collections.add(ConcurrentLinkedQueue.class);
		collections.add(DelayQueue.class);
		collections.add(ArrayBlockingQueue.class);
		collections.add(LinkedBlockingQueue.class);
		collections.add(PriorityBlockingQueue.class);
		collections.add(SynchronousQueue.class);
		collections.add(ArrayDeque.class);
		collections.add(LinkedBlockingDeque.class);
		
		// Value types.
		values.add(Boolean.TYPE);
		values.add(Byte.TYPE);
		values.add(Character.TYPE);
		values.add(Double.TYPE);
		values.add(Float.TYPE);
		values.add(Integer.TYPE);
		values.add(Long.TYPE);
		values.add(Short.TYPE);
		values.add(BigDecimal.class);
		values.add(BigInteger.class);
		values.add(Boolean.class);
		values.add(Byte.class);
		values.add(Character.class);
		values.add(Double.class);
		values.add(Float.class);
		values.add(Integer.class);
		values.add(Long.class);
		values.add(Short.class);
		values.add(String.class);
		values.add(Class.class);
		values.add(java.util.Date.class);
		values.add(Calendar.class);
		values.add(File.class);
		values.add(java.sql.Date.class);
		values.add(java.sql.Time.class);
		values.add(Timestamp.class);
		values.add(URL.class);
		values.add(Object.class);
		
		// XXX: :-/
		DateConverter converter = new DateConverter();
		converter.setUseLocaleFormat(true);
		ConvertUtils.register(converter, Date.class);
		
		// Default collection implementations.
		implementations.put(Collection.class, ArrayList.class);
		implementations.put(List.class, ArrayList.class);
		implementations.put(Set.class, HashSet.class);
		implementations.put(SortedSet.class, TreeSet.class);
		implementations.put(NavigableSet.class, TreeSet.class);
		implementations.put(Queue.class, PriorityQueue.class);
		implementations.put(Deque.class, ArrayDeque.class);
		
		// Empty collections.
		empties.put(Collection.class, Collections.EMPTY_LIST);
		empties.put(List.class, Collections.EMPTY_LIST);
		empties.put(Set.class, Collections.EMPTY_SET);
		empties.put(SortedSet.class, new TreeSet<Object>());
		empties.put(NavigableSet.class, new TreeSet<Object>());
		empties.put(Queue.class, new PriorityQueue<Object>());
		empties.put(Deque.class, new ArrayDeque<Object>(0));
		empties.put(ArrayList.class, new ArrayList<Object>(0));
		empties.put(LinkedList.class, new LinkedList<Object>());
		empties.put(CopyOnWriteArrayList.class, new CopyOnWriteArrayList<Object>());
		empties.put(Stack.class, new Stack<Object>());
		empties.put(Vector.class, new Vector<Object>(0));
		empties.put(HashSet.class, new HashSet<Object>(0));
		empties.put(LinkedHashSet.class, new LinkedHashSet<Object>(0));
		empties.put(CopyOnWriteArraySet.class, new CopyOnWriteArraySet<Object>());
		empties.put(TreeSet.class, new TreeSet<Object>());
		empties.put(ConcurrentSkipListSet.class, new ConcurrentSkipListSet<Object>());
		empties.put(PriorityQueue.class, new PriorityQueue<Object>());
		empties.put(ConcurrentLinkedQueue.class, new ConcurrentLinkedQueue<Object>());
		empties.put(DelayQueue.class, new DelayQueue<Delayed>());
		empties.put(ArrayBlockingQueue.class, new ArrayBlockingQueue<Object>(1));
		empties.put(LinkedBlockingQueue.class, new LinkedBlockingDeque<Object>());
		empties.put(PriorityBlockingQueue.class, new PriorityBlockingQueue<Object>());
		empties.put(SynchronousQueue.class, new SynchronousQueue<Object>());
		empties.put(ArrayDeque.class, new ArrayDeque<Object>());
		empties.put(LinkedBlockingDeque.class, new LinkedBlockingDeque<Object>());
	}
	
	/**
	 * Returns <code>true</code> if the specified <code>type</code> is a 
	 * built-in type.
	 * 
	 * @param type The type to be tested.
	 * @return <code>true</code> if the specified <code>type</code> is a 
	 * built-in type.
	 */
	public static boolean isBuiltinType(Type type) {
		Class<?> rawType = getRawType(type);
		return (rawType == null) ? false : builtins.contains(rawType);
	}
	
	/**
	 * Returns <code>true</code> if the specified <code>type</code> is an 
	 * instance of {@code Collection}.
	 * 
	 * @param type The type to be tested.
	 * @return <code>true</code> if the specified <code>type</code> is an 
	 * instance of {@code Collection}.
	 */
	public static boolean isCollection(Type type) {
		Class<?> rawType = getRawType(type);
		return (rawType == null) ? false
				: (rawType.equals(Collection.class) 
						|| ClassUtils.getAllInterfaces(rawType).contains(Collection.class));
	}

	/**
	 * Returns <code>true</code> if the specified <code>type</code> is an 
	 * instance of supported {@code Collection}.<br>
	 * <br>
	 * Supported collections are: 
	 * <pre>
	 * java.util.Collection
	 * java.util.List
	 * java.util.Set
	 * java.util.SortedSet
	 * java.util.NavigableSet
	 * java.util.Queue
	 * java.util.Deque
	 * 
	 * java.util.ArrayList
	 * java.util.LinkedList
	 * java.util.concurrent.CopyOnWriteArrayList
	 * java.util.Stack
	 * java.util.Vector
	 * java.util.HashSet
	 * java.util.LinkedHashSet
	 * java.util.concurrent.CopyOnWriteArraySet
	 * java.util.TreeSet
	 * java.util.concurrent.ConcurrentSkipListSet
	 * java.util.PriorityQueue
	 * java.util.concurrent.ConcurrentLinkedQueue
	 * java.util.concurrent.PriorityBlockingQueue
	 * java.util.concurrent.DelayQueue
	 * java.util.concurrent.LinkedBlockingQueue
	 * java.util.concurrent.PriorityBlockingQueue
	 * java.util.ArrayDeque
	 * java.util.concurrent.LinkedBlockingDeque
	 * java.util.concurrent.SynchronousQueue
	 * </pre>
	 * 
	 * @param type The type to be tested.
	 * @return <code>true</code> if the specified <code>type</code> is an 
	 * instance of supported {@code Collection}.<br>
	 */
	public static boolean isSupportedCollection(Type type) {
		Class<?> rawType = getRawType(type);
		return (rawType == null) ? false : collections.contains(rawType);
	}

	/**
	 * Returns <code>true</code> if the specified <code>type</code> is an array 
	 * type.
	 * 
	 * @param type The type to be tested.
	 * @return <code>true</code> if the specified <code>type</code> is an array 
	 * type.
	 */
	public static boolean isArray(Type type) {
		if (type instanceof GenericArrayType) {
			return true;
		} else if (type instanceof Class<?>) {
			return ((Class<?>) type).isArray();
		} else {
			return false;
		}
	}
	
	/**
	 * Returns the element type of the specified collection type. 
	 * The specified type must be collection or array. To make it sure, use 
	 * {@code #isCollection(Type)} method or {@code #isArray(Type)} method.
	 * 
	 * @param type Collection or array type. 
	 * @return The element type of the specified collection or array.
	 * @throws IllegalArgumentException If the specified 'type' is not a 
	 * collection or array.
	 */
	public static Class<?> getElementType(Type type) {
		if (isCollection(type)) {
			if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				return (Class<?>) parameterizedType.getActualTypeArguments()[0];
			} else {
				return Object.class;
			}
			
		} else if (isArray(type)) {
			if (type instanceof GenericArrayType) {
				GenericArrayType genericArrayType = (GenericArrayType) type;
				return (Class<?>) genericArrayType.getGenericComponentType();
			} else {
				Class<?> clazz = (Class<?>) type;
				return clazz.getComponentType();
			}
			
		} else {
			throw new IllegalArgumentException("'type' must be a collection or array");
		}
	}
	
	/**
	 * Returns the default implementation type of the specified collection 
	 * interface type.
	 * The specified type must be an supported collection. To make it sure, use 
	 * {@code #isSupportedCollection(Type)}. If the specified collection type is 
	 * <b>not</b> an interface, this method returns the specified implementation 
	 * type directly.<br>
	 * <br>
	 * The default implementations are: 
	 * <pre>
	 * java.util.Collection -> java.util.ArrayList
	 * java.util.List -> java.util.ArrayList
	 * java.util.Set -> java.util.HashSet
	 * java.util.SortedSet -> java.util.TreeSet
	 * java.util.NavigableSet -> java.util.TreeSet
	 * java.util.Queue -> java.util.PriorityQueue
	 * java.util.Deque -> java.util.ArrayDeque
	 * </pre>
	 * 
	 * @param type Collection type (must be supported type).
	 * @return The default implementation type of the specified collection 
	 * interface type.
	 */
	public static Class<?> getDefaultImplementationType(Type type) {
		Class<?> clazz = getRawType(type);
		return (clazz.isInterface()) ? implementations.get(clazz) : clazz;
	}
	
	/**
	 * Returns the empty instance of the specified collection type.
	 * 
	 * @param type The collection type (must be supported type).
	 * @return The empty instance of the specified collection type.
	 */
	public static Collection<?> getEmptyCollection(Type type) {
		return empties.get(getRawType(type));
	}
	
	/**
	 * Returns <code>true</code> if the specified <code>type</code> is a 
	 * supported core value type.<br>
	 * <br>
	 * Supported core value types are: 
	 * <pre>
	 * boolean
	 * byte
	 * char
	 * double
	 * float
	 * int
	 * long
	 * short
	 * java.math.BigDecimal
	 * java.math.BigInteger
	 * Boolean
	 * Byte
	 * Character
	 * Double
	 * Float
	 * Integer
	 * Long
	 * Short
	 * String
	 * Class
	 * java.util.Date
	 * java.util.Calendar
	 * java.io.File
	 * java.sql.Date
	 * java.sql.Time
	 * java.sql.Timestamp
	 * java.net.URL
	 * Object
	 * </pre>
	 * 
	 * @param type The type to be tested.
	 * @return <code>true</code> if the specified type is supported core value 
	 * type.
	 */
	public static boolean isCoreValueType(Type type) {
		Class<?> rawType = getRawType(type);
		return (rawType == null) ? false : values.contains(rawType);
	}
	
	/**
	 * Returns <code>true</code> if the specified type is an user define value 
	 * type.
	 * User defined value type must satisfy either of the following condition.
	 * <ol>
	 * <li>(The class) has a public constructor that takes one String.class parameter.</li>
	 * <li>Has a public static factory method that named 'valueOf' and takes one 
	 * String.class parameter.</li>
	 * </ol>
	 * 
	 * @param type The type to be tested.
	 * @return <code>true</code> if the specified type is an user define value 
	 * type.
	 */
	public static boolean isUserDefinedValueType(Type type) {
		Class<?> rawType = getRawType(type);
		
		if (rawType == null) {
			return false;
		}
		
		for (Constructor<?> constructor : rawType.getConstructors()) {
			Class<?>[] parameterTypes = constructor.getParameterTypes();
			
			if (parameterTypes.length == 1
					&& parameterTypes[0].equals(String.class)) {
				return true;
			}
		}
		
		for (Method method : rawType.getMethods()) {
			if (method.getName().equals("valueOf")
					&& Modifier.isStatic(method.getModifiers())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Adds supported core value type.
	 * 
	 * @param clazz The core value type.
	 * @param converter Converter for the specified core value type.
	 */
	public static void addCoreValueType(Class<?> clazz, Converter converter) {
		ConvertUtils.register(converter, clazz);
		values.add(clazz);
	}
	
	/**
	 * Returns <code>true</code> if the specified <code>type</code> is a 
	 * primitive type.
	 * 
	 * @param type The type to be tested.
	 * @return <code>true</code> if the specified type is a primitive type.
	 */
	public static boolean isPrimitive(Type type) {
		return (type instanceof Class<?>) ? ((Class<?>) type).isPrimitive() : false;
	}
	
	/**
	 * Returns the raw type of the specified type.
	 * This method supports {@code ParameterizedType} or raw {@code Class} 
	 * (just returns it directly). The other types such as array type are not 
	 * supported. If the specified 'type' is not supported, this method returns 
	 * <code>null</code>.
	 * 
	 * @param type The type to be tested.
	 * @return The raw type of the specified type.
	 */
	public static Class<?> getRawType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return (Class<?>) parameterizedType.getRawType();
		} else if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			return (clazz.isArray()) ? null : clazz;
		} else {
			return null;
		}
	}
	
}
