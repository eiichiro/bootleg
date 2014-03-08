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

import javax.servlet.http.HttpServletResponse;

import org.eiichiro.reverb.lang.UncheckedException;
import org.eiichiro.reverb.reflection.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * HTTP request processing pipeline stage to invoke Web endpoint method.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Invoke implements Predicate<WebContext> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Invokes Web endpoint method.
	 * This method processes the request as the following steps: 
	 * <ol>
	 * <li>Gets Web endpoint class by invoking {@code Method#getDeclaringClass()} 
	 * of the Web endpoint method set on the current HTTP request processing 
	 * context and instantiates it with the {@link Instantiator} that the 
	 * {@link Configuration#instantiator()} returns.</li>
	 * <li>Invokes Web endpoint method on the constructed instance with the 
	 * parameters set on the current HTTP request processing context and sets 
	 * the invocation result to the current HTTP request processing context.</li>
	 * <li>If the invocation is failed with {@code WebException}, this method 
	 * sends HTTP response with the status code the exception has.</li>
	 * <li>If the invocation is failed for any reasons, this method sends HTTP 
	 * response with the status code 500 (INTERNAL_SERVER_ERROR).</li>
	 * </ol>
	 * 
	 * @param context HTTP request processing context.
	 */
	public boolean apply(WebContext context) {
		WebException e = null;
		
		try {
			context.result(new MethodInvocation<Object>(
					context.method(), 
					instantiate(context.method().getDeclaringClass()), 
					context.parameters().toArray()).proceed());
			return true;
		} catch (WebException exception) {
			e = exception;
		} catch (Throwable throwable) {
			e = new EndpointInvocationFailedException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, throwable);
		}
		
		logger.warn("Failed to invoke Web endpoint", e);
		
		try {
			context.response().sendError(e.status());
			return false;
		} catch (IOException exception) {
			throw new UncheckedException(exception);
		}
	}
	
	protected <T> T instantiate(Class<T> endpoint) throws Exception {
		return endpoint.newInstance();
	}

}
