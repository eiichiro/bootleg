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

import org.eiichiro.bootleg.MediaType;
import org.eiichiro.bootleg.Response;
import org.eiichiro.bootleg.Responses;
import org.eiichiro.bootleg.annotation.Endpoint;
import org.eiichiro.bootleg.annotation.Generates;
import org.eiichiro.bootleg.annotation.Negotiated;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Endpoint
public class SendTestEndpoint {

	public Response generateResponse() {
		return Responses.xml("hello");
	}
	
	@Generates(MediaType.APPLICATION_XML)
	public String generateXML() {
		return "<message>hello</message>";
	}
	
	@Negotiated
	public String negotiate() {
		return "<message>hello</message>";
	}
	
	@Generates("")
	public String generateUnsupportedMediaType() {
		return "<message>hello</message>";
	}
	
}
