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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eiichiro.bootleg.xml.ValueTypeXmlAdapter;

/**
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
@XmlRootElement
public class UserInfo {

	@XmlElement
	public String id;
	
	@XmlElement
	public String name;
	
	@XmlElement
	public int rank;
	
	@XmlElement
	@XmlJavaTypeAdapter(ValueTypeXmlAdapter.class)
	public Email email;
	
	@XmlElement
	@XmlJavaTypeAdapter(ValueTypeXmlAdapter.class)
	public Link link;
	
	@XmlElement
	public List<String> interests = new ArrayList<String>();
	
	public UserInfo() {}
	
	public UserInfo(String id, String name, String email, String url, int rank,
			String... interests) {
		this.id = id;
		this.name = name;
		this.email = new Email(email);
		link = new Link(url);
		this.rank = rank;
		this.interests.addAll(Arrays.asList(interests));
	}
	
}
