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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * {@code Request} abstracts HTTP request to invoke Web endpoint method.
 * Web endpoint method parameters are constructed from this object.<br>
 * <br>
 * You can implement this interface yourself to construct Web endpoint method 
 * parameter from the content type that Bootleg doesn't support. You can 
 * specify your own {@code Request} implementation type by overriding 
 * {@code Configuration#requestType(String)} in your custom {@code Configuration}.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public interface Request {

	/** HTTP request attribute key to store path variables. */
	public static final String PATH = "org.eiichiro.bootleg.request.path";
	
	/**
	 * Constructs this HTTP request from the specified Web context.
	 * 
	 * @param context Web context.
	 */
	public void from(WebContext context);
	
	/**
	 * Returns Web endpoint method parameter value according to the specified 
	 * parameter type and source annotations from this HTTP request.
	 * 
	 * @param type Web endpoint method parameter type.
	 * @param sources Source annotations on the Web endpoint method parameter.
	 * @return Web endpoint method parameter value according to the specified 
	 * parameter type and source annotations.
	 */
	public Object get(Type type, List<Annotation> sources);
	
}
