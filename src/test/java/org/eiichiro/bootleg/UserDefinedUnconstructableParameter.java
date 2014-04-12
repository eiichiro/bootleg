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
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class UserDefinedUnconstructableParameter {

	private UserDefinedUnconstructableParameter(String userDefinedType4Value) {
		this.userDefinedType4Value = userDefinedType4Value;
	}
	
	public String getUserDefinedType4Value() {
		return userDefinedType4Value;
	}

	private final String userDefinedType4Value;
	
	public static UserDefinedUnconstructableParameter newInstance(String userDefinedType4Value) {
		return new UserDefinedUnconstructableParameter(userDefinedType4Value);
	}
	
}
