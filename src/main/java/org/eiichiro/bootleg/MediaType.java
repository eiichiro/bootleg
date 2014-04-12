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
 * {@code MediaType} provides MIME media type constants.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public abstract class MediaType {

	private MediaType() {}
	
	/** application/xml */
	public static final String APPLICATION_XML = "application/xml";
	
	/** application/json */
	public static final String APPLICATION_JSON = "application/json";
	
	/** text/plain */
	public static final String TEXT_PLAIN = "text/plain";
	
	/** text/html */
	public static final String TEXT_HTML = "text/html";
	
}
