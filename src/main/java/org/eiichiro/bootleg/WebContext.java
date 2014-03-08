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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * {@code WebContext} represents several Web contexts as packed for HTTP request 
 * processing.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class WebContext extends HashMap<Object, Object> {

	private static final long serialVersionUID = 1959481826393711441L;

	private final Configuration configuration;
	
	private final HttpServletRequest request;
	
	private final HttpServletResponse response;
	
	private final FilterChain chain;
	
	private Method method;
	
	private List<Object> parameters;
	
	private Object result;
	
	/**
	 * Constructs a new {@code WebContext} instance with the specified 
	 * {@code HttpServletRequest}, {@code HttpServletResponse} and 
	 * {@code FilterChain}.
	 * 
	 * @param configuration The current {@link Configuration}.
	 * @param request The current {@code HttpServletRequest}.
	 * @param response The current {@code HttpServletResponse}.
	 * @param chain The current {@code FilterChain}.
	 */
	public WebContext(Configuration configuration, HttpServletRequest request,
			HttpServletResponse response, FilterChain chain) {
		this.configuration = configuration;
		this.request = request;
		this.response = response;
		this.chain = chain;
	}
	
	/**
	 * Returns the {@link Configuration} which Bootleg is running on.
	 * 
	 * @return The {@code Configuration} which Bootleg is running on.
	 */
	public Configuration configuration() {
		return configuration;
	}
	
	/**
	 * Returns the current {@code HttpServletRequest}.
	 * 
	 * @return The current {@code HttpServletRequest}.
	 */
	public HttpServletRequest request() {
		return request;
	}
	
	/**
	 * Returns the current {@code HttpServletResponse}.
	 * 
	 * @return The current {@code HttpServletResponse}.
	 */
	public HttpServletResponse response() {
		return response;
	}
	
	/**
	 * Returns the current {@code HttpSession}.
	 * 
	 * @return The current {@code HttpSession}.
	 */
	public HttpSession session() {
		return request().getSession();
	}
	
	/**
	 * Returns the current {@code ServletContext}.
	 * 
	 * @return The current {@code ServletContext}.
	 */
	public ServletContext application() {
		return session().getServletContext();
	}
	
	/**
	 * Returns the current {@code FilterChain}.
	 * 
	 * @return The current {@code FilterChain}.
	 */
	public FilterChain chain() {
		return chain;
	}
	
	/**
	 * Returns the Web endpoint method to be invoked.
	 * This method returns <code>null</code> if this method is invoked before 
	 * {@link Route} processing stage.
	 * 
	 * @return The Web endpoint method to be invoked.
	 */
	public Method method() {
		return method;
	}
	
	/**
	 * Sets the Web endpoint method to be invoked.
	 * 
	 * @param method The Web endpoint method to be invoked.
	 */
	public void method(Method method) {
		this.method = method;
	}

	/**
	 * Sets the Web endpoint method invocation result.
	 * 
	 * @param result the result to set
	 */
	public void result(Object result) {
		this.result = result;
	}
	
	/**
	 * Returns the Web endpoint method invocation result.
	 * This method returns <code>null</code> if this method is invoked before 
	 * {@link Invoke} processing stage.
	 * 
	 * @return the result
	 */
	public Object result() {
		return result;
	}

	/**
	 * Sets the Web endpoint method parameters.
	 * 
	 * @param parameters the parameters to set
	 */
	public void parameters(List<Object> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Returns the Web endpoint method parameters.
	 * This method returns <code>null</code> if this method is invoked before 
	 * {@link Receive} processing stage.
	 * 
	 * @return the parameters
	 */
	public List<Object> parameters() {
		return parameters;
	}
	
}
