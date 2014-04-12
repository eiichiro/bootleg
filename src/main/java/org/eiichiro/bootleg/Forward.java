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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eiichiro.reverb.lang.UncheckedException;

/**
 * {@code Forward} is a {@link Response} implementation to forward HTTP request 
 * to another resource.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Forward implements Response {

	private final String path;
	
	/**
	 * Constructs {@code Forward} instance to forward to the specified path.
	 * 
	 * @param path The path to be forwarded. The specified path must be 
	 * relative.
	 */
	public Forward(String path) {
		this.path = path;
	}
	
	/** Unsupported operation. {@code UnsupportedOperationException} is thrown. */
	public void status(int status) {
		throw new UnsupportedOperationException(
				"'public void status(int status)' is not supoprted in ["
						+ getClass().getName() + "]");
	}

	/** Unsupported operation. {@code UnsupportedOperationException} is thrown. */
	public void mediaType(String mediaType) {
		throw new UnsupportedOperationException(
				"'public void mediaType(String mediaType)' is not supoprted in ["
						+ getClass().getName() + "]");
	}

	/** Unsupported operation. {@code UnsupportedOperationException} is thrown. */
	public void entity(Object entity) {
		throw new UnsupportedOperationException(
				"'public void entity(Object entity)' is not supoprted in ["
						+ getClass().getName() + "]");
	}

	/**
	 * Forwards HTTP request to the specified path.
	 * 
	 * @param context HTTP request processing context.
	 */
	public void to(WebContext context) {
		HttpServletRequest request = context.request();
		HttpServletResponse response = context.response();
		
		try {
			request.getRequestDispatcher(path).forward(request, response);
		} catch (ServletException e) {
			throw new UncheckedException(e);
		} catch (IOException e) {
			throw new UncheckedException(e);
		}
	}

}
