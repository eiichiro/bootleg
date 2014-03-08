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
package org.eiichiro.bootleg.xml;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eiichiro.bootleg.AbstractResponse;
import org.eiichiro.bootleg.MediaType;
import org.eiichiro.bootleg.WebContext;
import org.eiichiro.reverb.lang.UncheckedException;

/**
 * {@code XMLResponse} is a XML-based implementation of {@code Response}.
 * If the generated/negotiated MIME media type is 'application/xml', default 
 * configuration of Bootleg uses this implementation.
 * Web endpoint method invocation result is deserialized to XML with JAXB (Java 
 * Architecture for XML Binding). UTF-8 is used for the character encoding 
 * (MIME charset) at any time.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class XMLResponse extends AbstractResponse {

	public XMLResponse() {
		mediaType = MediaType.APPLICATION_XML;
	}
	
	/**
	 * Writes {@code XMLResponse} to the current {@code WebContext}.
	 * 
	 * @param context The current {@code WebContext}.
	 */
	public void to(WebContext context) {
		if (entity == null) {
			return;
		}
		
		HttpServletResponse response = context.response();
		
		try {
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Type", mediaType);
			
			if (status > 0) {
				response.setStatus(status);
			}
			
			if (entity instanceof String) {
				response.getWriter().write((String) entity);
				return;
			}
			
			Marshaller marshaller = JAXBContext.newInstance(entity.getClass()).createMarshaller();
			marshaller.setAdapter(new ValueTypeXmlAdapter<Object>());
			marshaller.marshal(entity, response.getWriter());
		} catch (Exception e) {
			throw new UncheckedException(e);
		}
	}

}
