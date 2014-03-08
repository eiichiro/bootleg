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
 * {@code AbstractResponse} is an abstract base class of {@link Response} 
 * implementation and provides default behaviors common to most {@code Response} 
 * implementation classes.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public abstract class AbstractResponse implements Response {

	/** Web endpoint invocation result. */
	protected Object entity;
	
	/** MIME media type. */
	protected String mediaType = "";
	
	/** HTTP status code. */
	protected int status = 200;
	
	/**
	 * Sets HTTP response status code.
	 * 
	 * @param status HTTP response status code.
	 */
	public void status(int status) {
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

	/**
	 * Sets HTTP response entity.
	 * 
	 * @param entity HTTP response entity.
	 */
	public void entity(Object entity) {
		this.entity = entity;
	}

}
