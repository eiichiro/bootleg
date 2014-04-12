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

/**
 * {@code Redirect} is a {@link Response} implementation to send a temporary 
 * redirect response to the client.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Redirect implements Response {

	private final String url;
	
	private String mediaType = "";
	
	private int status = 0;
	
	/**
	 * Constructs a new {@code Redirect} instance to redirect to the specified 
	 * URL.
	 * 
	 * @param url The URL to redirect.
	 */
	public Redirect(String url) {
		this.url = url;
	}
	
	/**
	 * Sets HTTP response status code.
	 * 
	 * @param status HTTP response status code.
	 */
	public void status(int status) {
		if (!(300 <= status && status < 400)) {
			throw new IllegalArgumentException("'status' must not be [" + status + "]");
		}
		
		this.status = status;
	}

	/**
	 * Sets HTTP response MIME media type.
	 * 
	 * @param mediaType HTTP response MIME media type.
	 */
	public void mediaType(String mediaType) {
		if (mediaType == null) {
			throw new IllegalArgumentException("'mediaType' must not be [" + mediaType + "]");
		}
		
		this.mediaType = mediaType;
	}

	/** Unsupported operation. {@code UnsupportedOperationException} is thrown. */
	public void entity(Object entity) {
		throw new UnsupportedOperationException(
				"'public void entity(Object entity)' is not supoprted in ["
						+ getClass().getName() + "]");
	}

	/**
	 * Writes this HTTP response to the specified Web context.
	 * 
	 * @param context Web context.
	 */
	public void to(WebContext context) {
		HttpServletResponse response = context.response();
		if (!mediaType.isEmpty()) {
			response.setHeader("Content-Type", mediaType);
		}
		
		if (status > 0) {
			response.setStatus(status);
		}
		
		try {
			response.sendRedirect(response.encodeRedirectURL(url));
		} catch (IOException e) {
			throw new UncheckedException(e);
		}
	}

}
