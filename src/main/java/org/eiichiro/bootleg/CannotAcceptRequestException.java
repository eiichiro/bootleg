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

/**
 * {@code CannotAcceptRequestException} is thrown by {@code Receiver} when the 
 * HTTP request cannot be accepted for any reason.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class CannotAcceptRequestException extends WebException {

	private static final long serialVersionUID = -3976740372239784649L;

	/**
	 * Constructs a new {@code CannotAcceptRequestException} with the specified 
	 * HTTP status code that describes the reason.
	 * 
	 * @param status HTTP status code that describes the reason.
	 */
	public CannotAcceptRequestException(int status) {
		super(status);
	}
	
	/**
	 * Constructs a new {@code CannotAcceptRequestException} with the specified 
	 * HTTP status code and message that describe the reason.
	 * 
	 * @param status HTTP status code that describes the reason.
	 * @param message Error message that describes the reason.
	 */
	public CannotAcceptRequestException(int status, String message) {
		super(status, message);
	}

	/**
	 * Constructs a new {@code CannotAcceptRequestException} with the specified 
	 * HTTP status code and the cause of this exception.
	 * 
	 * @param status HTTP status code that describes the reason.
	 * @param cause The cause of this exception.
	 */
	public CannotAcceptRequestException(int status, Throwable cause) {
		super(status, cause.getMessage(), cause);
	}
	
}
