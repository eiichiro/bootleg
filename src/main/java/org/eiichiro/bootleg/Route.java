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
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.eiichiro.reverb.lang.UncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * HTTP request processing pipeline stage to route the matched HTTP request to 
 * the corresponding Web endpoint method.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Route implements Predicate<WebContext> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Map<String, Class<?>> endpoints;
	
	private Routing routing;
	
	private final Object lock = new Object();
	
	/**
	 * Identifies Web endpoint method to be invoked from the HTTP request's URI.
	 * This method processes the request as the following steps: 
	 * <ol>
	 * <li>Gets Web endpoint classes from the {@code ServletContext}. If they 
	 * have not been set on the {@code ServletContext}, this method loads them 
	 * by invoking {@code Loader#load()} on the loader that the 
	 * {@code Configuration} returns.</li>
	 * <li>Determines if the requested URI is ignored by {@link Routing}. If the 
	 * requested URI is matched to the pattern specified by {@code Routing#ignore(String...)}, 
	 * the request is forwarded to the next filter chain.</li>
	 * <li>Determines if the requested URI is matched to the URI template 
	 * specified by {@code Routing#add(String, Class, String)}. If the requested 
	 * URI is matched to the URI template, the corresponding Web endpoint method 
	 * is set to the current HTTP request processing context.</li>
	 * <li>If the requested URI is not matched to any URI template, identifies 
	 * Web endpoint class and method according to the default URI convention 
	 * (URI ends with /.../&lt;simple-name-of-the-endpoint-class&gt;/&lt;endpoint-method&gt;).
	 * </li>
	 * <li>If any Web endpoint method is not identified, the request is 
	 * forwarded to the next filter chain.</li>
	 * </ol>
	 * 
	 * @param context HTTP request processing context.
	 */
	public boolean apply(WebContext context) {
		Configuration configuration = context.configuration();
		
		if (endpoints == null) {
			synchronized (lock) {
				if (endpoints == null) {
					endpoints = new HashMap<String, Class<?>>();
					
					for (Class<?> endpoint : configuration.endpoints()) {
						String name = endpoint.getSimpleName().toLowerCase();
						
						if (endpoints.containsKey(name)) {
							logger.warn("Web endpoint class name is duplicated: ["
									+ endpoints.get(name) + "] is overwritten by ["
									+ endpoint + "]");
						}
						
						endpoints.put(name, endpoint);
					}
				}
			}
		}
		
		if (routing == null) {
			synchronized (lock) {
				if (routing == null) {
					routing = configuration.routing();
				}
			}
		}
		
		HttpServletRequest request = context.request();
		String uri = request.getRequestURI().substring((request.getContextPath() + "/").length());
		
		try {
			if (routing.ignores(uri)) {
				logger.debug("URI [" + request.getRequestURI() + "] is ignored by routing configuration");
				context.chain().doFilter(request, context.response());
				return false;
			}
			
			Verb verb = null;
			
			try {
				verb = Verb.valueOf(request.getMethod());
			} catch (Exception e) {
				logger.warn("HTTP verb [" + request.getMethod() + "] is not supported");
			}
			
			List<Entry<URITemplate, Map<Verb, Method>>> route = routing.route(uri);
			
			for (Entry<URITemplate, Map<Verb, Method>> r : route) {
				Method method = r.getValue().get(verb);
				
				if (method == null) {
					method = r.getValue().get(null);
				}
				
				if (method != null) {
					context.method(method);
					request.setAttribute(Request.PATH, r.getKey().variables(uri));
					logger.debug("Web endpoint method is [" 
							+ method.getDeclaringClass().getName() 
							+ "#" + method.getName() + "]");
					return true;
				}
			}
			
			if (uri.endsWith("/")) {
				logger.debug("URI [" + request.getRequestURI() + "] is not correlated with any Web endpoint");
				context.chain().doFilter(request, context.response());
				return false;
			}
			
			String[] segments = uri.split("/");
			
			if (segments.length < 2) {
				logger.debug("URI [" + request.getRequestURI() + "] is not correlated with any Web endpoint");
				logger.debug("The requested URI pattern must end with " +
						"[/.../<simple-name-of-the-endpoint-class>/<endpoint-method>] or " + 
						"correlated with any Web endpoint in the routing configuration");
				context.chain().doFilter(request, context.response());
				return false;
			}
			
			Class<?> endpoint = endpoints.get(segments[segments.length - 2].toLowerCase());
			
			if (endpoint == null) {
				logger.warn("Web endpoint class is not found: Simple class name [" + segments[segments.length - 2] + "]");
				context.chain().doFilter(request, context.response());
				return false;
			}
			
			for (Method method : endpoint.getMethods()) {
				if (method.getName().compareToIgnoreCase(segments[segments.length - 1]) == 0) {
					context.method(method);
					logger.debug("Web endpoint method is ["
							+ method.getDeclaringClass().getName()
							+ "#" + method.getName() + "]");
					return true;
				}
			}
			
			logger.warn("Web endpoint method is not found: Method name [" + segments[segments.length - 1] + "]");
			context.chain().doFilter(request, context.response());
			return false;
		} catch (Exception e) {
			throw new UncheckedException(e);
		}
	}

}
