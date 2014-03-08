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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.base.Preconditions;

/**
 * URL routing configuration of the current application.
 * {@code Routing} instance for the current application is passed to 
 * {@code Configuration#routing(Routing)} method by Bootleg and you can configure 
 * the configuration by overriding this method in your own {@link Configuration} class.
 * 
 * @see Configuration
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Routing {

	private Map<String, URITemplate> templates = new HashMap<String, URITemplate>();
	
	private Map<URITemplate, Map<Verb, Method>> routes = new TreeMap<URITemplate, Map<Verb, Method>>();
	
	private Set<Pattern> ignores = new HashSet<Pattern>();
	
	/**
	 * Adds URL routing pattern.
	 * The routing pattern can be specified as URI template format and the path 
	 * variables can be passed to Web endpoint method like this: 
	 * <pre>
	 * {@code @Override}
	 * public void routing(Routing routing) {
	 *     routing.add("user/{id}/*", User.class, "handle");
	 * }
	 * </pre>
	 * <pre>
	 * {@code}
	 * public class User {
	 * 
	 *     // 'user/{id}/*' is routed to this method.
	 *     public void handle({@code} String id) {
	 *         ...
	 * </pre>
	 * <b>Note</b>: If a certain URI matches to multiple routing patterns, the 
	 * routing pattern that has the most path segments (split by '/') is used in 
	 * preference to the others. If there are multiple routing patterns that 
	 * have the same path segments, the first-specified one is used.
	 * 
	 * @param pattern URL routing pattern as URI template format.
	 * @param endpoint Web endpoint class corresponding to the routing pattern.
	 * @param method Web endpoint method corresponding to the routing pattern.
	 * @see URITemplate
	 */
	public void add(String pattern, Class<?> endpoint, String method) {
		add(null, pattern, endpoint, method);
	}
	
	/**
	 * Adds URL routing pattern.
	 * The routing pattern can be specified as URI template format and the path 
	 * variables can be passed to Web endpoint method like this: 
	 * <pre>
	 * {@code @Override}
	 * public void routing(Routing routing) {
	 *     routing.add("user/{id}/*", User.class, "handle");
	 * }
	 * </pre>
	 * <pre>
	 * {@code}
	 * public class User {
	 * 
	 *     // 'user/{id}/*' is routed to this method.
	 *     public void handle({@code} String id) {
	 *         ...
	 * </pre>
	 * <b>Note</b>: If a certain URI matches to multiple routing patterns, the 
	 * routing pattern that has the most path segments (split by '/') is used in 
	 * preference to the others. If there are multiple routing patterns that 
	 * have the same path segments, the first-specified one is used.
	 * 
	 * @param pattern URL routing pattern as URI template format.
	 * @param endpoint Web endpoint class corresponding to the routing pattern.
	 * @param method Web endpoint method corresponding to the routing pattern.
	 * @see URITemplate
	 */
	public void add(Verb verb, String pattern, Class<?> endpoint, String method) {
		Preconditions.checkArgument(pattern != null, 
				"Parameter 'pattern' must not be [" + pattern + "]");
		Preconditions.checkArgument(endpoint != null, 
				"Parameter 'endpoint' must not be [" + endpoint + "]");
		Preconditions.checkArgument(method != null, 
				"Parameter 'method' must not be [" + method + "]");
		
		for (Method m : endpoint.getMethods()) {
			if (m.getName().equals(method)) {
				URITemplate template = null;
				
				if (templates.containsKey(pattern)) {
					template = templates.get(pattern);
				} else {
					template = new URITemplate(pattern);
					templates.put(pattern, template);
				}
				
				Map<Verb, Method> methods = null;
				
				if (routes.containsKey(template)) {
					methods = routes.get(template);
				} else {
					methods = new HashMap<Verb, Method>();
					routes.put(template, methods);
				}
				
				methods.put(verb, m);
				return;
			}
		}
		
		throw new IllegalArgumentException("Method [" + method
				+ "] does not exist on Web endpoint [" + endpoint + "]");
	}
	
	/**
	 * Adds ignore-routing patterns.
	 * The requested URL that matches to the patterns is not handled by 
	 * Bootleg. <code>*</code> (meta character) can be used in the pattern 
	 * (e.g., <code>*.png</code>). 
	 * <br><b>Note</b>: Ignore-routing patterns are in preference to routing 
	 * patterns.
	 * 
	 * @param patterns The ignore-routing patterns.
	 */
	public void ignore(String... patterns) {
		Preconditions.checkArgument(patterns != null, 
				"Parameter 'patterns' must not be [" + patterns + "]");
		
		for (String pattern : patterns) {
			Preconditions.checkArgument(pattern != null && !pattern.isEmpty(), 
					"Parameter 'patterns' must not include [null] or empty entry");
			ignores.add(Pattern.compile(pattern.replaceAll("\\.",
					Matcher.quoteReplacement("\\.")).replaceAll("\\*", ".*?")));
		}
	}
	
	List<Entry<URITemplate, Map<Verb, Method>>> route(String uri) {
		List<Entry<URITemplate, Map<Verb, Method>>> route = new ArrayList<Map.Entry<URITemplate,Map<Verb,Method>>>();
		
		for (Entry<URITemplate, Map<Verb, Method>> r : routes.entrySet()) {
			if (r.getKey().matches(uri)) {
				route.add(r);
			}
		}
		
		return route;
	}
	
	boolean ignores(String uri) {
		for (Pattern ignore : ignores) {
			if (ignore.matcher(uri).matches()) {
				return true;
			}
		}
		
		return false;
	}
	
}
