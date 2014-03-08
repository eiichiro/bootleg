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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.eiichiro.bootleg.annotation.Accepts;
import org.eiichiro.bootleg.annotation.Allows;
import org.eiichiro.bootleg.annotation.Source;
import org.eiichiro.reverb.lang.UncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * HTTP request processing pipeline stage to validate HTTP request and 
 * constructs Web endpoint method parameters.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Receive implements Predicate<WebContext> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Validates HTTP request and constructs Web endpoint method parameters.
	 * This method processes the request as the following steps: 
	 * <ol>
	 * <li>Checks the requested HTTP method is allowable. If the Web endpoint 
	 * method is qualified by {@code @Allows}, this class allows only the 
	 * HTTP methods qualified in it.</li>
	 * <li>Checks the requested MIME media type is acceptable. If the Web 
	 * endpoint method is qualified by {@code @Accepts}, this class accepts only 
	 * the qualified MIME media types.</li>
	 * <li>Determines {@code Request} type from the content type on HTTP request 
	 * header by invoking {@code Configuration#requestType(String)} method with the 
	 * content type.</li>
	 * <li>Constructs Web endpoint method parameters by invoking {@code Request#get()}
	 * methods and sets them to the current HTTP request processing context.</li>
	 * </ol>
	 * 
	 * @param context HTTP request processing context.
	 */
	public boolean apply(WebContext context) {
		Method method = context.method();
		
		try {
			Verb verb = null;
			
			try {
				verb = Verb.valueOf(context.request().getMethod());
			} catch (Exception e) {
				throw new CannotAcceptRequestException(
						HttpServletResponse.SC_METHOD_NOT_ALLOWED, 
						"HTTP verb [" + context.request().getMethod() + "] is not supported");
			}
			
			// Is the HTTP method allowable?
			if (method.isAnnotationPresent(Allows.class)) {
				Allows allows = method.getAnnotation(Allows.class);
				List<Verb> verbs = Arrays.asList(allows.value());
				
				if (!verbs.contains(verb)) {
					throw new CannotAcceptRequestException(
							HttpServletResponse.SC_METHOD_NOT_ALLOWED, 
							"Endpoint method [" 
									+ method.getDeclaringClass().getName() + "#" + method.getName() 
									+ "] is not capable of accepting [" + verb + "] method");
				}
			}
			
			// Is the MIME media type of the request acceptable?
			Accepts accepts = method.getAnnotation(Accepts.class);
			String type = context.request().getContentType();
			
			if (accepts != null) {
				boolean acceptable = false;
				
				for (String value : accepts.value()) {
					if (type.startsWith(value)) {
						acceptable = true;
						break;
					}
				}
				
				if (!acceptable) {
					throw new CannotAcceptRequestException(
							HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
							"Endpoint method [" + method.getDeclaringClass().getName() 
									+ "#" + method.getName()
									+ "] is not capable of accepting [" + type
									+ "] media type");
				}
			}
			
			Class<? extends Request> requestType = context.configuration().requestTypes().get(type);
			
			if (requestType == null) {
				throw new CannotAcceptRequestException(
						HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, 
						"Configuration [" + context.configuration().getClass().getName() 
								+ "] does not provide a request type corresponding to [" 
								+ type + "] media type");
			}
			
			Request request = requestType.newInstance();
			request.from(context);
			
			// Parameter construction.
			List<Object> parameters = new ArrayList<Object>();
			Annotation[][] parameterAnnotations = method.getParameterAnnotations();
			Type[] parameterTypes = method.getGenericParameterTypes();
			
			for (int i = 0; i < parameterTypes.length; i++) {
				List<Annotation> sources = new ArrayList<Annotation>();
				
				for (Annotation annotation : parameterAnnotations[i]) {
					if (annotation.annotationType().isAnnotationPresent(Source.class)) {
						sources.add(annotation);
					}
				}
				
				parameters.add(request.get(parameterTypes[i], sources));
			}
			
			context.parameters(parameters);
			return true;
		} catch (CannotAcceptRequestException exception) {
			logger.warn("Failed to accept HTTP request", exception);
			
			try {
				context.response().sendError(exception.status());
				return false;
			} catch (IOException e) {
				throw new UncheckedException(e);
			}
			
		} catch (Exception e) {
			throw new UncheckedException(e);
		}
	}

}
