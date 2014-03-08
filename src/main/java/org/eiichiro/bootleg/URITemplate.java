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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

/**
 * URI template to route the matched HTTP request to the corresponding Web 
 * endpoint method.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class URITemplate implements Comparable<URITemplate> {

	private final String template;
	
	private final Pattern pattern;
	
	private List<String> variables = new ArrayList<String>();
	
	/**
	 * Constructs a new {@code URITemplate} with the specified URI pattern.
	 * 
	 * @param pattern URI pattern.
	 */
	public URITemplate(String pattern) {
		Preconditions.checkArgument(pattern != null, 
				"Parameter 'pattern' must not be [" + pattern + "]");
		char c = '{';
		int start = 0;
		String substring = "";
		List<String> variables = new ArrayList<String>();
		
		for (int i = 0; i < pattern.length(); i++) {
			if (pattern.charAt(i) == c) {
				substring = pattern.substring(start, i);
				
				if (substring.contains("{") || substring.contains("}")) {
					throw new IllegalArgumentException("Broken braces pair: " +
							"Path variable must be enclosed by braces pair " +
							"({variable}) [" + pattern + "]");
				}
				
				if (c == '}') {
					if (substring.length() == 0) {
						throw new IllegalArgumentException(
								"Path variable name must not be empty [" + pattern + "]");
					} else if (!substring.matches("^\\p{Alnum}[\\p{Alnum}|.|_|-]*")) {
						throw new IllegalArgumentException(
								"Path variable name must match to " +
								"'\\p{Alnum}[\\p{Alnum}|.|_|-]*' [" + pattern + "]");
					} else if (variables.contains(substring)) {
						throw new IllegalArgumentException(
								"Duplicated path variable [" + pattern + "]");
					}
					
					variables.add(substring);
				}
				
				start = i + 1;
				c = (c == '{') ? '}' : '{';
			}
		}
		
		substring = pattern.substring(start);
		
		if (substring.contains("{") || substring.contains("}") || c == '}') {
			throw new IllegalArgumentException("Broken braces pair: " +
					"Path variable must be enclosed by braces pair " +
					"({variable}) [" + pattern + "]");
		}
		
		if (pattern.contains("}{")) {
			throw new IllegalArgumentException(
					"Variables in compound path seqment must be separated by " +
					"at least one literal");
		}
		
		this.pattern = Pattern.compile(pattern
				.replaceAll("\\.", Matcher.quoteReplacement("\\."))
				.replaceAll("\\{.+?\\}", "(.+?)").replaceAll("\\*", ".*?"));
		this.template = pattern;
		this.variables = variables;
	}
	
	/**
	 * Returns <code>true</code> if this URI template is matched to the 
	 * specified actual URI.
	 * 
	 * @param uri The actual URI to be tested.
	 * @return <code>true</code> if this URI template is matched to the 
	 * specified actual URI.
	 */
	public boolean matches(String uri) {
		return pattern.matcher(uri).matches();
	}
	
	/**
	 * Returns template variable names.
	 * 
	 * @return The template variable names.
	 */
	public List<String> variables() {
		return variables;
	}
	
	/**
	 * Returns template variable name and value pairs extracted from the 
	 * specified actual URI.
	 * 
	 * @param uri The actual URI to be parsed.
	 * @return The template variable name and value pairs extracted from the 
	 * specified actual URI.
	 */
	public Map<String, String> variables(String uri) {
		Map<String, String> variables = new HashMap<String, String>();
		Matcher matcher = pattern.matcher(uri);
		
		if (matcher.matches()) {
			for (int i = 0; i < matcher.groupCount(); i++) {
				variables.put(this.variables.get(i), matcher.group(i + 1));
			}
		}
		
		return variables;
	}
	
	/**
	 * Compares this URI template to the specified one.
	 * Returns <code> &lt; 0</code> if this URI template's segment (split by '/') 
	 * count is <b>greater</b> than the specified one, <code>&gt; 0</code> if 
	 * this URI template's segment count is <b>less</b> than the specified one 
	 * or this URI template's segment count is equivalent to the specified one. 
	 * This method is used for sorting URI templates according to the priority 
	 * {@code URITemplate#matches(String)} method is tested by {@link Routing} 
	 * class, so a stricter template (which has more segments) is prior to a 
	 * less one.
	 * 
	 * @param o URI template to be compared.
	 * @return <code> &lt; 0</code> if this URI template's segment (split by '/') 
	 * count is <b>greater</b> than the specified one, <code>&gt; 0</code> if 
	 * this URI template's segment count is <b>less</b> than the specified one 
	 * or this URI template's segment count is equivalent to the specified one.
	 * @see Routing
	 */
	public int compareTo(URITemplate o) {
		if (template.equals(o.template)) {
			return 0;
		}
		
		int difference = template.split("/").length - o.template.split("/").length;
		return (difference == 0) ? 1 : difference * -1;
	}
	
}
