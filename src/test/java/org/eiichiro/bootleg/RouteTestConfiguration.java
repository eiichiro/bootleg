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

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.eiichiro.bootleg.DefaultConfiguration;
import org.eiichiro.bootleg.Routing;
import org.eiichiro.bootleg.Verb;
import org.eiichiro.bootleg.annotation.Endpoint;

/**
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class RouteTestConfiguration extends DefaultConfiguration {

	@Override
	public Routing routing() {
		Routing routing = super.routing();
		routing.ignore("*/method2");
		routing.add("me/th/od2", org.eiichiro.bootleg.RouteTestEndpoint.class, "method2");
		
		// 0.4.0
		routing.add(Verb.GET, "path/to/endpoint/method", org.eiichiro.bootleg.RouteTestEndpoint.class, "method1");
		routing.add(Verb.POST, "path/to/endpoint/method", org.eiichiro.bootleg.RouteTestEndpoint.class, "method2");
		routing.add(Verb.GET, "path/to/endpoint/m", org.eiichiro.bootleg.RouteTestEndpoint.class, "method1");
		routing.add("path/to/endpoint/m", org.eiichiro.bootleg.RouteTestEndpoint.class, "method2");
		return routing;
	}
	
	@Override
	public Set<Class<?>> endpoints() {
		Set<Class<?>> classes = new TreeSet<Class<?>>(new Comparator<Class<?>>() {

			public int compare(Class<?> o1, Class<?> o2) {
				return (o1.equals(RouteTestEndpoint.class)) ? -1 : 1;
			}
			
		});
		classes.add(RouteTestEndpoint.class);
		classes.add(org.eiichiro.bootleg.RouteTestEndpoint.class);
		return classes;
	}
	
	@Endpoint
	public static class RouteTestEndpoint {
		
		public String method1() {
			throw new UnsupportedOperationException();
		}
		
		public String method2() {
			throw new UnsupportedOperationException();
		}
		
	}
	
}
