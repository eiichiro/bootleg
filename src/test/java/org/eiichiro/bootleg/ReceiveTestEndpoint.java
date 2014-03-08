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
import org.eiichiro.bootleg.Verb;
import org.eiichiro.bootleg.annotation.Accepts;
import org.eiichiro.bootleg.annotation.Allows;
import org.eiichiro.bootleg.annotation.Endpoint;

/**
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Endpoint
public class ReceiveTestEndpoint {

	public void acceptAll() {}
	
	@Allows(Verb.GET)
	public void acceptGet() {}
	
	@Allows(Verb.POST)
	public void acceptPost() {}
	
	@Allows({Verb.GET, Verb.POST})
	public void acceptGetPost() {}
	
	@Accepts(MediaType.APPLICATION_XML)
	public void acceptXML() {}
	
}
