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

import org.eiichiro.bootleg.DefaultConfiguration;
import org.eiichiro.bootleg.Routing;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class GenericRequestTestConfiguration extends DefaultConfiguration {

	@Override
	public Routing routing() {
		Routing routing = super.routing();
		routing.add("/{int}/{char}/{strings}/{type1}/{type2}/{type3}/{userDefinedType3Value}", GenericRequestTestEndpoint.class, "testPath");
		return routing;
	}
	
}
