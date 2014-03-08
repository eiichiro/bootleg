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
 * {@code CannotSendResponseException} is thrown by {@code Receiver} when the 
 * HTTP response cannot be sent for any reason.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class CannotSendResponseException extends WebException {

	private static final long serialVersionUID = 6513484088936224576L;

	/**
	 * Constructs a new {@code CannotSendResponseException} with the specified 
	 * HTTP status code that describes the reason.
	 * 
	 * @param status HTTP status code that describes the reason.
	 */
	public CannotSendResponseException(int status) {
		super(status);
	}
	
	/**
	 * Constructs a new {@code CannotSendResponseException} with the specified 
	 * HTTP status code and message that describe the reason.
	 * 
	 * @param status HTTP status code that describes the reason.
	 * @param message Error message that describes the reason.
	 */
	public CannotSendResponseException(int status, String message) {
		super(status, message);
	}

	/**
	 * Constructs a new {@code CannotSendResponseException} with the specified 
	 * HTTP status code, message and the cause of this exception.
	 * 
	 * @param status HTTP status code that describes the reason.
	 * @param message Error message that describes the reason.
	 * @param cause The cause of this exception.
	 */
	public CannotSendResponseException(int status, String message, Throwable cause) {
		super(status, message, cause);
	}
	
}
