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
package org.eiichiro.bootleg.json;

import static org.eiichiro.bootleg.Responses.*;

import org.eiichiro.bootleg.Response;
import org.eiichiro.bootleg.UserInfo;
import org.eiichiro.bootleg.annotation.Endpoint;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Endpoint
public class JSONResponseTestEndpoint {

	public Response getJSONUserInfo() {
		UserInfo userInfo = new UserInfo("eiichiro", "Eiichiro Uchiumi",
				"mail@eiichiro.org", "http://www.eiichiro.org/", 9,
				"Listening to music", "Buddhist art");
		return json(userInfo);
	}
	
}
