/*
 * Copyright (C) 2013 Eiichiro Uchiumi. All Rights Reserved.
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

/**
 * {@code WebException} is thrown when the HTTP request processing pipeline is 
 * failed for any reason.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class WebException extends RuntimeException {

	private static final long serialVersionUID = -2529019177106835403L;
	
	private final int status;
	
	/**
	 * Constructs a new {@code WebException} with the specified HTTP status code 
	 * that describes the reason.
	 * 
	 * @param status HTTP status code that describes the reason.
	 */
	public WebException(int status) {
		this.status = status;
	}
	
	/**
	 * Constructs a new {@code WebException} with the specified HTTP status code 
	 * and message that describe the reason.
	 * 
	 * @param status HTTP status code that describes the reason.
	 * @param message Error message that describes the reason.
	 */
	public WebException(int status, String message) {
		super(message);
		this.status = status;
	}

	/**
	 * Constructs a new {@code WebException} with the specified HTTP status code, 
	 * message and the cause of this exception.
	 * 
	 * @param status HTTP status code that describes the reason.
	 * @param message Error message that describes the reason.
	 * @param cause The cause of this exception.
	 */
	public WebException(int status, String message, Throwable cause) {
		super(message, cause);
		this.status = status;
	}
	
	/**
	 * Returns HTTP status code that describes the reason.
	 * 
	 * @return HTTP status code that describes the reason.
	 */
	public int status() {
		return status;
	}
	
}
