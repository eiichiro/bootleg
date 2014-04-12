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
package org.eiichiro.bootleg.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.AbstractConverter;
import org.eiichiro.bootleg.Types;

/**
 * {@code ValueTypeXmlAdapter} is a custom XML marshaling adapter to convert 
 * user-defined value type object to XML value string representation.
 * If you want to marshal user-defined value type object to a single XML value 
 * string representation, annotate the type or field with 
 * {@code @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(ValueTypeXmlAdapter.class)}.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class ValueTypeXmlAdapter<BoundType> extends XmlAdapter<String, BoundType> {

	/**
	 * Marshals the specified user-defined value type object to single XML value 
	 * string representation.
	 * 
	 * @throws If the specified object is not a user-defined value type.
	 */
	@Override
	public String marshal(BoundType v) throws Exception {
		Class<? extends Object> type = v.getClass();
		
		if (!Types.isUserDefinedValueType(type)) {
			throw new IllegalArgumentException("Type [" + type
					+ "] must be an user-defined value type; "
					+ "@XmlJavaTypeAdapter(ValueTypeXmlAdapter.class) "
					+ "can be annotated to user-defined value type and field only");
		}
		
		Converter converter = ConvertUtils.lookup(type);
		
		if ((converter != null && converter instanceof AbstractConverter)) {
			String string = (String) ConvertUtils.convert(v, String.class);
			
			if (string != null) {
				return string;
			}
		}
		
		return v.toString();
	}

	/** Unsupported. Unmarshaling is provided out of JAXB in Bootleg. */
	@Override
	public BoundType unmarshal(String v) throws Exception {
		throw new UnsupportedOperationException("Unmarshaling is provided out of JAXB");
	}

}
