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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code BootlegFilter} is a central Servlet {@code Filter} to process Web 
 * request in Bootleg framework.
 * You need to setup this filter in web.xml in order to use Bootleg, as the 
 * following: 
 * <pre>
 * &lt;filter&gt;
 * 	&lt;filter-name&gt;bootleg&lt;/filter-name&gt;
 * 	&lt;filter-class&gt;org.eiichiro.bootleg.BootlegFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 * 	&lt;filter-name&gt;bootleg&lt;/filter-name&gt;
 * 	&lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class BootlegFilter implements Filter {

	/** Servlet context parameter key to get custom {@code Configuration} class. */
	public static final String CONFIGURATION = "org.eiichiro.bootleg.configuration";
	
	private static Logger logger = LoggerFactory.getLogger(BootlegFilter.class);
	
	private Configuration configuration;
	
	private Pipeline<WebContext> pipeline;
	
	static {
		Logger logger = LoggerFactory.getLogger(Version.class);
		logger.info("Bootleg " + Version.MAJOR + "." + Version.MINER + "." + Version.BUILD);
//		logger.info("Copyright (C) 2011-2013 Eiichiro Uchiumi. All Rights Reserved.");
	}
	
	/**
	 * Initializes this filter with the specified configuration.
	 * Constructs the {@code Configuration} and sets up the HTTP request processing 
	 * pipeline. If the custom configuration class has not been specified, 
	 * {@link DefaultConfiguration} is used by default.
	 * 
	 * @param filterConfig Servlet filter configuration.
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("Starting Bootleg on the Servlet ["
				+ filterConfig.getServletContext().getMajorVersion() + "."
				+ filterConfig.getServletContext().getMinorVersion() + "] environment");
		Configuration configuration = configuration(filterConfig);
		
		if (configuration == null) {
			configuration = new DefaultConfiguration();
			configuration.init(filterConfig.getServletContext());
			logger.info("Default configuration [" + DefaultConfiguration.class + "] loaded");
		}
		
		Pipeline<WebContext> pipeline = configuration.pipeline();
		
		if (pipeline == null) {
			throw new ServletException("Pipeline must not be [" + pipeline
					+ "]: Configuration [" + configuration + "]");
		} else {
			logger.debug("HTTP request processing pipeline " + pipeline + " constructed");
		}
		
		this.configuration = configuration;
		this.pipeline = pipeline;
	}

	/**
	 * Processes the Web request in Bootleg.
	 * 
	 * @param request HTTP request.
	 * @param response HTTP response.
	 * @throws IOException If any I/O error has occurred.
	 * @throws ServletException If any exception has occurred in processing the 
	 * request.
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			pipeline.apply(new WebContext(configuration, (HttpServletRequest) request, (HttpServletResponse) response, chain));
		} catch (Exception e) {
			logger.warn("Failed to process HTTP request", e);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/** Destroys this fileter. */
	public void destroy() {
		logger.info("Bootleg stopped");
	}
	
	/**
	 * Constructs the {@code Configuration}.
	 * This method attempts to get custom configuration class from {@code ServletContext} 
	 * init parameter and instantiate and initialize it. If the custom configuration 
	 * class has not been specified as the {@code ServletContext} init parameter, 
	 * this method returns <code>null</code>.
	 * 
	 * @param config Servlet filter configuration.
	 * @return {@code Configuration} constructed.
	 * @throws ServletException If the custom configuration loading is failed.
	 */
	protected Configuration configuration(FilterConfig config) throws ServletException {
		ServletContext context = config.getServletContext();
		String clazz = context.getInitParameter(CONFIGURATION);
		Configuration configuration = null;
		
		if (clazz != null) {
			try {
				configuration = (Configuration) Class.forName(clazz).newInstance();
				configuration.init(config.getServletContext());
				logger.info("Custom configuration [" + clazz + "] loaded");
			} catch (Exception e) {
				logger.error("Failed to load custom configuration [" + clazz + "]", e);
				throw new ServletException("Failed to load custom configuration [" + clazz + "]", e);
			}
		}
		
		return configuration;
	}

}
