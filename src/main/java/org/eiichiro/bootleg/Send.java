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
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletResponse;

import org.eiichiro.bootleg.annotation.Generates;
import org.eiichiro.bootleg.annotation.Negotiated;
import org.eiichiro.reverb.lang.UncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * HTTP request processing pipeline stage to send Web endpoint invocation 
 * result to the client as HTTP response.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Send implements Predicate<WebContext> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Sends Web endpoint invocation result to the client as HTTP response.
	 * This method processes the response as the following steps: 
	 * <ol>
	 * <li>If the Web endpoint invocation result is instance of {@code Response}, 
	 * sends the HTTP response to the client by invoking {@code Response#to(WebContext)} 
	 * method with the current Web context.</li>
	 * <li>(If it does not so, ) If the Web endpoint method is qualified by 
	 * {@code @Generates} annotation, this class determines the {@code Response} 
	 * type from the specified MIME media type by invoking 
	 * {@code Configuration#responseType(String)}.</li>
	 * <li>If the Web endpoint method is qualified by {@code @Negotiated} 
	 * annotation, this class determines the {@code Response} type from the MIME 
	 * media type specified on "Accept" HTTP request header by invoking 
	 * {@code Configuration#responseType(String)}.</li>
	 * <li>(If it does not so, ) If the Web endpoint method is qualified neither 
	 * by {@code @Generates} nor {@code @Negotiated} annotation, this class 
	 * determines the {@code Response} type by invoking 
	 * {@code Configuration#responseType(String)} with empty string.</li>
	 * <li>Constructs {@code Response} instance and sets the MIME media type and 
	 * Web endpoint invocation result, then sends the HTTP response to the 
	 * client by invoking {@code Response#to(WebContext)} method with the 
	 * current Web context.</li>
	 * </ol>
	 * 
	 * @param context HTTP request processing context.
	 */
	public boolean apply(WebContext context) {
		Object result = context.result();
		
		if (result instanceof Response) {
			Response response = (Response) result;
			response.to(context);
			return true;
		}
		
		try {
			Method method = context.method();
			String contentType = "";
			Generates generates = method.getAnnotation(Generates.class);
			Negotiated negotiated = method.getAnnotation(Negotiated.class);
			
			if (generates != null) {
				logger.debug("Web endpoint ["
						+ method.getDeclaringClass().getName() + "#" + method.getName()
						+ "] is qualified by [" + generates + "]");
				contentType = generates.value();
			} else if (negotiated != null) {
				logger.debug("Web endpoint ["
						+ method.getDeclaringClass().getName() + "#" + method.getName()
						+ "] is qualified by [" + negotiated + "]");
				String header = context.request().getHeader("Accept");
				
				if (header != null) {
					// TODO: Parse.
					contentType = header;
				}
			}
			
			logger.debug("MIME media type is [" + contentType + "]");
			Class<? extends Response> responseType = context.configuration().responseTypes().get(contentType);
			
			if (responseType == null) {
				throw new CannotSendResponseException(
						HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, 
						"Configuration [" + context.configuration().getClass().getName() 
								+ "] does not provide a response type corresponding to [" 
								+ contentType + "] media type");
			}
			
			Response response = responseType.newInstance();
			response.mediaType(contentType);
			response.entity(result);
			response.to(context);
			return true;
		} catch (CannotSendResponseException exception) {
			logger.warn("Failed to send HTTP response", exception);
			
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
