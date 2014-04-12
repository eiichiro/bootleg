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
 * {@code Response} abstracts HTTP response Web endpoint method respond.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public interface Response {

	/**
	 * Sets HTTP response status code.
	 * 
	 * @param status HTTP response status code.
	 */
	public void status(int status);
	
	/**
	 * Sets HTTP response MIME media type.
	 * 
	 * @param mediaType HTTP response MIME media type.
	 */
	public void mediaType(String mediaType);
	
	/**
	 * Sets HTTP response entity.
	 * 
	 * @param entity HTTP response entity.
	 */
	public void entity(Object entity);
	
	/**
	 * Writes this HTTP response to the specified Web context.
	 * 
	 * @param context Web context.
	 */
	public void to(WebContext context);
	
}
