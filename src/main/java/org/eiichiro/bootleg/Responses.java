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

import org.eiichiro.bootleg.json.JSONResponse;
import org.eiichiro.bootleg.xml.XMLResponse;

/**
 * An utility which has some factory methods to create {@link Response} instance 
 * to be sent to the client form Web endpoint.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public abstract class Responses {

	private Responses() {}
	
	/**
	 * Creates generic {@code Response} that has no message body.
	 * 
	 * @return Generic {@code Response} that has no message body.
	 */
	public static Response response() {
		return new GenericResponse();
	}

	/**
	 * Creates generic {@code Response} that has the specified entity as message 
	 * body.
	 * 
	 * @param value The object to be sent to the client.
	 * @return Generic {@code Response} that has the specified entity as message 
	 * body.
	 */
	public static Response response(Object value) {
		GenericResponse response = new GenericResponse();
		response.entity(value);
		return response;
	}
	
	/**
	 * Creates forward {@link Response} to forward the request to the specified 
	 * path.
	 * 
	 * @param path The path to forward.
	 * @return {@code Forward} to forward the request to the specified path.
	 */
	public static Forward forward(String path) {
		return new Forward(path);
	}
	
	/**
	 * Creates redirect {@link Response} to redirect the response to the 
	 * specified URL.
	 * 
	 * @param url The URL to redirect.
	 * @return {@code Redirect} to redirect the response to the specified URL.
	 */
	public static Redirect redirect(String url) {
		return new Redirect(url);
	}
	
	/**
	 * Creates {@code JSONResponse} to send the specified entity to the client 
	 * as JSON format response.
	 * 
	 * @param value The object to be sent to the client.
	 * @return {@code JSONResponse}.
	 */
	public static JSONResponse json(Object value) {
		JSONResponse response = new JSONResponse();
		response.entity(value);
		return response;
	}
	
	/**
	 * Creates {@code XMLResponse} to send the specified entity to the client as 
	 * XML format response.
	 * 
	 * @param value The object to be sent to the client.
	 * @return {@code XMLResponse}.
	 */
	public static XMLResponse xml(Object value) {
		XMLResponse response = new XMLResponse();
		response.entity(value);
		return response;
	}
	
}
