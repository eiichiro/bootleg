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

import org.eiichiro.bootleg.GenericResponse;
import org.eiichiro.bootleg.MediaType;
import org.eiichiro.bootleg.annotation.Endpoint;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Endpoint
public class GenericResponseTestEndpoint {

	public GenericResponse getNull() {
		// GenericResponse without content. Both of content type and status are default.
		GenericResponse response = new GenericResponse();
		response.entity(null);
		return response;
	}
	
	public GenericResponse getXMLNull() {
		// GenericResponse without content. Content type is 'application/xml' and status is default.
		GenericResponse response = new GenericResponse();
		response.entity(null);
		response.mediaType(MediaType.APPLICATION_XML);
		return response;
	}
	
	public GenericResponse get204Null() {
		// GenericResponse without content. Content type is default and status is 204 (No Content).
		GenericResponse response = new GenericResponse();
		response.entity(null);
		response.status(204);
		return response;
	}
	
	public GenericResponse getString() {
		GenericResponse response = new GenericResponse();
		response.entity("hello");
		return response;
	}
	
	public GenericResponse getXMLString() {
		GenericResponse response = new GenericResponse();
		response.entity("<message>hello</message>");
		response.mediaType(MediaType.APPLICATION_XML);
		return response;
	}
	
	public GenericResponse getSerializable() {
		GenericResponse response = new GenericResponse();
		response.entity(new SerializableObject("hello"));
		return response;
	}
	
	public GenericResponse getUserDefined() {
		GenericResponse response = new GenericResponse();
		response.entity(new UserDefinedResponseObject("hello"));
		return response;
	}
	
	public GenericResponse getXMLUserDefined() {
		GenericResponse response = new GenericResponse();
		response.entity(new UserDefinedResponseObject("<message>hello</message>"));
		response.mediaType(MediaType.APPLICATION_XML);
		return response;
	}
	
}
