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
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

import org.eiichiro.reverb.lang.UncheckedException;

/**
 * {@code GenericResponse} is a generic implementation of {@code Response}.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class GenericResponse extends AbstractResponse {

	/**
	 * Writes HTTP response representation to output stream.
	 * This method determines the response form as the following convention: 
	 * <ul>
	 * <li>
	 * If the MIME media type ({@code #mediaType}) is not empty, set it to 
	 * 'Content-Type' HTTP response header.
	 * </li>
	 * <li>
	 * If the status ({@code #status}) is greater than 0, set it to HTTP 
	 * response code.
	 * </li>
	 * <li>
	 * If {@code #entity} is an instance of {@code String}, this method writes 
	 * it to the HTTP response directly. If MIME media type has not been 
	 * specified, 'text/plain' is used.
	 * </li>
	 * <li>
	 * If {@code #entity} is an instance of {@code Externalizable}, this method 
	 * writes it to the HTTP response as {@code ObjectOutputStream} by invoking 
	 * {@code Externalizable#writeExternal(java.io.ObjectOutput)}.
	 * </li>
	 * <li>
	 * If {@code #entity} is an instance of {@code Serializable}, this method 
	 * writes it to the HTTP response as {@code ObjectOutputStream}.
	 * </li>
	 * <li>
	 * The other, this method write it to the HTTP response as plain text by 
	 * invoking {@code #value#toString()}. If MIME media type has not been 
	 * specified, 'text/plain' is used.
	 * </li>
	 * </ul>
	 * UTF-8 is used for the character encoding (MIME charset) at any time.
	 * 
	 * @param context HTTP request processing context.
	 */
	public void to(WebContext context) {
		HttpServletResponse response = context.response();
		
		if (!mediaType.isEmpty()) {
			response.setHeader("Content-Type", mediaType);
		}
		
		if (status > 0) {
			response.setStatus(status);
		}
		
		if (entity == null) {
			return;
		}
		
		try {
			if (entity instanceof String) {
				if (mediaType.isEmpty()) {
					response.setHeader("Content-Type", MediaType.TEXT_PLAIN);
				}
				
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write((String) entity);
			} else if (entity instanceof Serializable) {
				new ObjectOutputStream(response.getOutputStream()).writeObject(entity);
			} else {
				if (mediaType.isEmpty()) {
					response.setHeader("Content-Type", MediaType.TEXT_PLAIN);
				}
				
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(entity.toString());
			}
			
		} catch (IOException e) {
			throw new UncheckedException(e);
		}
	}

}
