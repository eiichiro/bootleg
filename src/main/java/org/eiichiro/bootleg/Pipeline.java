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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

/**
 * A lightweight Pipeline which is accepting {@code com.google.common.base.Function} 
 * and {@code com.google.common.base.Predicate} as the processing stage.
 * This class is used for HTTP request processing by {@link BootlegFilter}.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Pipeline<T> implements Function<T, T> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private List<Object> stages = new ArrayList<Object>();
	
	private Map<Object, String> names = new HashMap<Object, String>();
	
	/**
	 * Starts pipeline with the specified stream object that flows through this 
	 * pipeline.
	 * 
	 * @param io The stream object that flows through this pipeline.
	 */
	@SuppressWarnings("unchecked")
	public T apply(T io) {
		logger.debug("Pipeline began");
		
		try {
			for (int i = 0; i < stages.size(); i++) {
				Object stage = stages.get(i);
				String name = names.get(stage);
				logger.debug("Stage-" + i
						+ ((name != null && !name.isEmpty()) ? " [" + name + "] " : " ")
						+ "processing");
				
				if (stage instanceof Function) {
					if ((io = ((Function<T, T>) stage).apply(io)) == null) {
						return io;
					}
					
				} else if (stage instanceof Predicate) {
					if (!((Predicate<T>) stage).apply(io)) {
						return io;
					}
				}
			}
			
			return io;
		} finally {
			logger.debug("Pipeline ended");
		}
	}
	
	/**
	 * Sets the specified {@code Function} to the next processing stage with the 
	 * class name of the {@code Function} as the stage name.
	 * 
	 * @param function The {@code Function} set to the next processing stage.
	 * @return This {@code Pipeline} instance.
	 */
	public Pipeline<T> set(Function<T, T> function) {
		return set(stages.size(), function.getClass().getName(), function);
	}
	
	/**
	 * Sets the specified {@code Function} to the next processing stage with the 
	 * specified index and the class name of the {@code Function} as the stage 
	 * name.
	 * 
	 * @param index The index the specified {@code Function} to be set.
	 * @param function The {@code Function} set to the next processing stage.
	 * @return This {@code Pipeline} instance.
	 */
	public Pipeline<T> set(int index, Function<T, T> function) {
		return set(index, function.getClass().getName(), function);
	}
	
	/**
	 * Sets the specified {@code Function} to the next processing stage with the 
	 * specified stage name.
	 * 
	 * @param name The stage name.
	 * @param function The {@code Function} set to the next processing stage.
	 * @return This {@code Pipeline} instance.
	 */
	public Pipeline<T> set(String name, Function<T, T> function) {
		return set(stages.size(), name, function);
	}
	
	/**
	 * Sets the specified {@code Function} to the next processing stage with the 
	 * specified index and stage name.
	 * 
	 * @param index The index the specified {@code Function} to be set.
	 * @param name The stage name.
	 * @param function The {@code Function} set to the next processing stage.
	 * @return This {@code Pipeline} instance.
	 */
	public Pipeline<T> set(int index, String name, Function<T, T> function) {
		Preconditions.checkArgument(function != null, 
				"Parameter 'function' must not be [" + function + "]");
		stages.add(index, function);
		names.put(function, name);
		return this;
	}
	
	/**
	 * Sets the specified {@code Predicate} to the next processing stage with 
	 * the class name of the {@code Predicate} as the stage name.
	 * 
	 * @param predicate The {@code Predicate} set to the next processing stage.
	 * @return This {@code Pipeline} instance.
	 */
	public Pipeline<T> set(Predicate<T> predicate) {
		return set(stages.size(), predicate.getClass().getName(), predicate);
	}
	
	/**
	 * Sets the specified {@code Predicate} to the next processing stage with 
	 * the specified index and the class name of the {@code Predicate} as the 
	 * stage name.
	 * 
	 * @param index The index the specified {@code Predicate} to be set.
	 * @param predicate The {@code Predicate} set to the next processing stage.
	 * @return This {@code Pipeline} instance.
	 */
	public Pipeline<T> set(int index, Predicate<T> predicate) {
		return set(index, predicate.getClass().getName(), predicate);
	}
	
	/**
	 * Sets the specified {@code Predicate} to the next processing stage with 
	 * the specified stage name.
	 * 
	 * @param name The stage name.
	 * @param predicate The {@code Predicate} set to the next processing stage.
	 * @return This {@code Pipeline} instance.
	 */
	public Pipeline<T> set(String name, Predicate<T> predicate) {
		return set(stages.size(), name, predicate);
	}
	
	/**
	 * Sets the specified {@code Predicate} to the next processing stage with 
	 * the specified index and stage name.
	 * 
	 * @param index The index the specified {@code Predicate} to be set.
	 * @param name The stage name.
	 * @param predicate The {@code Function} set to the next processing stage.
	 * @return This {@code Pipeline} instance.
	 */
	public Pipeline<T> set(int index, String name, Predicate<T> predicate) {
		Preconditions.checkArgument(predicate != null, 
				"Parameter 'predicate' must not be [" + predicate + "]");
		stages.add(index, predicate);
		names.put(predicate, name);
		return this;
	}
	
	/**
	 * Removes {@code Function} or {@code Predicate} at the specified index.
	 * 
	 * @param index The index the specified {@code Function} or {@code Predicate} to be removed.
	 * @return This {@code Pipeline} instance.
	 */
	public Pipeline<T> remove(int index) {
		Object object = stages.remove(index);
		names.remove(object);
		return this;
	}
	
	/**
	 * Returns {@code String} representation of this {@code Pipeline}.
	 * 
	 * @return {@code String} representation of this {@code Pipeline}.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < stages.size(); i++) {
			Object stage = stages.get(i);
			String name = names.get(stage);
			builder.append("Stage-" + i);
			
			if (name != null && !name.isEmpty()) {
				builder.append(" [").append(name).append("]");
			}
			
			if (i < stages.size() - 1) {
				builder.append(" -> ");
			}
		}
		
		return builder.toString();
	}
	
}
