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

import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletContext;

/**
 * Bootleg framework configuration.
 * You can specify your custom configuration by declaring {@code ServletContext} 
 * init parameter in web.xml as follow:
 * <pre>
 * &lt;context-param&gt;
 *     &lt;param-name&gt;org.eiichiro.bootleg.configuration&lt;/param-name&gt;
 *     &lt;param-value&gt;com.example.your.app.CustomConfiguration&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public interface Configuration {

	/**
	 * Initializes this configuration with the specified {@code ServletContext}.
	 * 
	 * @param context The current {@code ServletContext}.
	 */
	public void init(ServletContext context);
	
	/**
	 * Specifies URL routing configuration of the current application.
	 * 
	 * @return URL routing configuration of the current application.
	 * @see Routing
	 */
	public Routing routing();
	
	/**
	 * Specifies HTTP request processing pipeline configuration of the current 
	 * application.
	 * 
	 * @return HTTP request processing pipeline container.
	 */
	public Pipeline<WebContext> pipeline();
	
	/**
	 * Returns a map of content type and {@link Request} implementation class.
	 * 
	 * @return The map of content type and {@code Request} implementation class.
	 */
	public Map<String, Class<? extends Request>> requestTypes();
	
	/**
	 * Returns a map of content type and {@link Response} implementation class.
	 * 
	 * @return The map of content type and {@code Response} implementation class.
	 */
	public Map<String, Class<? extends Response>> responseTypes();
	
	/**
	 * Returns Web endpoint classes to be deployed.
	 * 
	 * @return The Web endpoint classes to be deployed.
	 */
	public Collection<Class<?>> endpoints();
	
}
