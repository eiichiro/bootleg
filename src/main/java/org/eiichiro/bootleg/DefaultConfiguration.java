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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;

import javassist.CtClass;

import org.eiichiro.bootleg.annotation.Endpoint;
import org.eiichiro.bootleg.json.JSONRequest;
import org.eiichiro.bootleg.json.JSONResponse;
import org.eiichiro.bootleg.xml.XMLRequest;
import org.eiichiro.bootleg.xml.XMLResponse;
import org.eiichiro.reverb.lang.ClassResolver;
import org.eiichiro.reverb.lang.UncheckedException;
import org.eiichiro.reverb.system.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bootleg framework's default configuration.
 * If you would not specify your own configuration in web.xml, Bootleg uses this 
 * configuration by default.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class DefaultConfiguration implements Configuration {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Map<String, Class<? extends Request>> requestTypes = new HashMap<String, Class<? extends Request>>() {
		
		private static final long serialVersionUID = 9066899440163278947L;

		@Override
		public Class<? extends Request> get(Object key) {
			Class<? extends Request> requestType = super.get(key);
			return (requestType == null) ? GenericRequest.class : requestType;
		}
		
	};
	
	{
		requestTypes.put(MediaType.APPLICATION_JSON, JSONRequest.class);
		requestTypes.put(MediaType.APPLICATION_XML, XMLRequest.class);
		requestTypes.put(MediaType.TEXT_HTML, GenericRequest.class);
		requestTypes.put(MediaType.TEXT_PLAIN, GenericRequest.class);
	}
	
	private Map<String, Class<? extends Response>> responseTypes = new HashMap<String, Class<? extends Response>>() {
		
		private static final long serialVersionUID = 9066899440163278947L;

		@Override
		public Class<? extends Response> get(Object key) {
			Class<? extends Response> responseType = super.get(key);
			return (responseType == null) ? GenericResponse.class : responseType;
		}
		
	};
	
	{
		responseTypes.put(MediaType.APPLICATION_JSON, JSONResponse.class);
		responseTypes.put(MediaType.APPLICATION_XML, XMLResponse.class);
		responseTypes.put(MediaType.TEXT_HTML, GenericResponse.class);
		responseTypes.put(MediaType.TEXT_PLAIN, GenericResponse.class);
	}
	
	private ServletContext context;
	
	/**
	 * Holds the specified {@code ServletContext}.
	 * This class is implemented basically as lazy initialized. So most 
	 * configuration settings are initialized when the method is invoked for the 
	 * first time.
	 * 
	 * @param context The current {@code ServletContext}.
	 */
	@Override
	public void init(ServletContext context) {
		this.context = context;
	}

	/**
	 * Returns the default ignore-routing patterns.
	 * Bootleg ignores static resources (<code>*.js</code>, <code>*.css</code>, 
	 * <code>*.gif</code>, <code>*.png</code>, <code>*.jpg</code>, 
	 * <code>*.jsp</code>, <code>*.html</code>) by default.
	 * 
	 * @return The default {@link Routing} configuration.
	 */
	public Routing routing() {
		Routing routing = new Routing();
		routing.ignore("*.js", "*.css", "*.gif", "*.png", "*.jpg", "*.jsp", "*.html");
		return routing;
	}
	
	/**
	 * Returns the default HTTP request processing pipeline.
	 * HTTP request processing pipeline consists of [Route] -&gt; [Receive] 
	 * -&gt; [Invoke] -&gt; [Send] by default.
	 * 
	 * @return HTTP request processing pipeline.
	 * @see Route
	 * @see Receive
	 * @see Invoke
	 * @see Send
	 */
	public Pipeline<WebContext> pipeline() {
		return new Pipeline<WebContext>().set(Route.class.getSimpleName(), new Route())
				.set(Receive.class.getSimpleName(), new Receive())
				.set(Invoke.class.getSimpleName(), new Invoke())
				.set(Send.class.getSimpleName(), new Send());
	}

	/**
	 * Returns the default map of content type and {@link Request} implementation 
	 * class.
	 * 
	 * 'Content-type'-{@code Request} type mapping is: 
	 * <ul>
	 * <li><code>application/xml</code> -&gt; {@code XMLRequest.class}</li>
	 * <li><code>application/json</code> -&gt; {@code JSONRequest.class}</li>
	 * <li><code>text/plain</code> -&gt; {@code GenericRequest.class}</li>
	 * <li><code>text/html</code> -&gt; {@code GenericRequest.class}</li>
	 * </ul>
	 * 
	 * @return The map of content type and {@code Request} implementation class.
	 */
	public Map<String, Class<? extends Request>> requestTypes() {
		return requestTypes;
	}

	/**
	 * Returns the default map of content type and {@link Response} implementation 
	 * class.
	 * 
	 * 'Content-type'-{@code Response} type mapping is: 
	 * <ul>
	 * <li><code>application/xml</code> -&gt; {@code XMLResponse.class}</li>
	 * <li><code>application/json</code> -&gt; {@code JSONResponse.class}</li>
	 * <li><code>text/plain</code> -&gt; {@code GenericResponse.class}</li>
	 * <li><code>text/html</code> -&gt; {@code GenericResponse.class}</li>
	 * </ul>
	 * 
	 * @return The map of content type and {@code Response} implementation class.
	 */
	public Map<String, Class<? extends Response>> responseTypes() {
		return responseTypes;
	}
	
	private Collection<Class<?>> endpoints;
	private final Object lock = new Object();

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<?>> endpoints() {
		if (endpoints == null) {
			synchronized (lock) {
				if (endpoints == null) {
					try {
						List<URL> paths = new ArrayList<>();
						ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
						
						if (classLoader instanceof URLClassLoader) {
							logger.debug("The application is running on URLClassLoader - "
									+ "directly getting the classpath from its own search paths");
							paths = Arrays.asList(((URLClassLoader) classLoader).getURLs());
						} else {
							URL classes = context.getResource("/WEB-INF/classes/");
							
							if (classes != null) {
								logger.debug("The application is running on a Servlet container "
										+ "with the standard web application layout - "
										+ "building the classpath from '/WEB-INF/classes/' "
										+ "directory and '/WEB-INF/lib/*.jar' files");
								paths.add(classes);
								
								for (String jar : (Set<String>) context.getResourcePaths("/WEB-INF/lib/")) {
									if (jar.endsWith(".jar")) {
										paths.add(context.getResource(jar));
									}
								}
								
							} else {
								logger.debug("The application is standalone or running on a embedded "
										+ "Servlet container - building the classpath from "
										+ "'java.class.path' system property");
								
								for (String path : Environment.getProperty("java.class.path").split(File.pathSeparator)) {
									paths.add(new File(path).toURI().toURL());
								}
							}
						}
						
						for (int i = 0; i < paths.size(); i++) {
							logger.debug("Web endpoint search path #" + (i + 1) + " [" + paths.get(i) + "]");
						}
						
						ClassResolver<CtClass> resolver = new CtClassClassResolver(paths);
						Set<Class<?>> endpoints = new TreeSet<Class<?>>(new Comparator<Class<?>>() {

							@Override
							public int compare(Class<?> o1, Class<?> o2) {
								return o1.getName().compareTo(o2.getName());
							}
							
						});
						
						for (CtClass ctClass : resolver.resolveByAnnotation(Endpoint.class)) {
							try {
								endpoints.add(Class.forName(ctClass.getName(), true, Thread.currentThread().getContextClassLoader()));
							} catch (Exception e) {
								logger.error("Failed to load Web endpoint class [" + ctClass.getName() + "]", e);
							}
						}
						
						logger.info("Web endpoints [" + endpoints.size() + "] loaded");
						this.endpoints = endpoints;
					} catch (Exception e) {
						logger.error("Failed to load Web endpoint classes", e);
						throw new UncheckedException(e);
					}
				}
			}
		}
		
		return endpoints;
	}

}
