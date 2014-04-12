/*
 * Copyright (C) 2013 Eiichiro Uchiumi. All Rights Reserved.
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.eiichiro.reverb.lang.ClassResolver;

/**
 * {@code CtClassClassResolver} is a 
 * <a href="http://www.csg.is.titech.ac.jp/~chiba/javassist/">Javassist</a>'s 
 * {@code javassist.CtClass} based extension of {@code ClassResolver}.
 * This class loads the class as {@code javassist.CtClass} from the specified 
 * search path.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class CtClassClassResolver extends ClassResolver<CtClass> {

	private ClassPool pool;
	
	/**
	 * Constructs a new {@code CtClassClassResolver} instance with the 
	 * {@code ClassLoader}'s search paths in the current thread context.
	 */
	public CtClassClassResolver(Iterable<URL> paths) throws NotFoundException {
		super(paths);
		ClassPool pool = ClassPool.getDefault();
		
		for (URL url : paths) {
			pool.appendClassPath(url.getPath());
		}
		
		this.pool = pool;
	}
	
	/**
	 * Loads the class of the specified name from the specified 
	 * {@code InputStream} and returns loaded class representation as 
	 * {@code javassist.CtClass}.
	 * 
	 * @param clazz The name of the class to be loaded.
	 * @param stream {@code InputStream} to load a class file.
	 * @return The loaded class representation as {@code javassist.CtClass}.
	 */
	@Override
	protected CtClass load(String clazz, InputStream stream) {
		try {
			return pool.makeClass(stream);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Resolves the classes that is annotated by the specified annotation as 
	 * {@code javassist.CtClass}.
	 * 
	 * @param annotation The annotation the class being annotated.
	 * @return Classes that is annotated by the specified annotation as 
	 * {@code javassist.CtClass}.
	 * @throws IOException If any I/O access fails while traversing the search 
	 * path.
	 */
	@Override
	public Set<CtClass> resolveByAnnotation(final Class<? extends Annotation> annotation)
			throws IOException {
		Matcher<CtClass> matcher = new Matcher<CtClass>() {

			@Override
			public boolean matches(CtClass ctClass) {
				try {
					for (Object object : ctClass.getAnnotations()) {
						Annotation a = (Annotation) object;
						
						if (a.annotationType().equals(annotation)) {
							return true;
						}
					}
					
				} catch (Exception e) {}
				
				ctClass.detach();
				return false;
			}
			
		};
		return resolve(matcher);
	}

	/**
	 * Resolves the classes that implements the specified interface as 
	 * {@code javassist.CtClass}.
	 * 
	 * @param interfaceClass The interface being implemented.
	 * @return Classes that implements the specified interface as 
	 * {@code javassist.CtClass}.
	 * @throws IOException If any I/O access fails while traversing the search 
	 * path.
	 */
	@Override
	public Set<CtClass> resolveByInterface(final Class<?> interfaceClass) throws IOException {
		Matcher<CtClass> matcher = new Matcher<CtClass>() {

			@Override
			public boolean matches(CtClass ctClass) {
				try {
					for (CtClass c : ctClass.getInterfaces()) {
						if (c.getName().equals(interfaceClass.getName())) {
							return true;
						}
					}
					
					CtClass superclass = ctClass.getSuperclass();
					
					if (superclass != null) {
						return matches(superclass);
					}
					
				} catch (Exception e) {}
				
				ctClass.detach();
				return false;
			}
			
		};
		return resolve(matcher);
	}

	/**
	 * Resolves the classes that contains the specified name as 
	 * {@code javassist.CtClass}.
	 * 
	 * @param name The part of the class name.
	 * @return Classes that contains the specified name as 
	 * {@code javassist.CtClass}.
	 * @throws IOException If any I/O access fails while traversing the search 
	 * path.
	 */
	@Override
	public Set<CtClass> resolveByName(final String name) throws IOException {
		Matcher<CtClass> matcher = new Matcher<CtClass>() {
			
			@Override
			public boolean matches(CtClass ctClass) {
				if (ctClass.getName().contains(name)) {
					return true;
				}
				
				ctClass.detach();
				return false;
			}
			
		};
		return resolve(matcher);
	}

	/**
	 * Resolves the classes that inherits the specified superclass as 
	 * {@code javassist.CtClass}.
	 * 
	 * @param superclass The superclass being inherited.
	 * @return Classes that inherits the specified superclass as 
	 * {@code javassist.CtClass}.
	 * @throws IOException If any I/O access fails while traversing the search 
	 * path.
	 */
	@Override
	public Set<CtClass> resolveBySuperclass(Class<?> superclass) throws IOException {
		Matcher<CtClass> matcher = new Matcher<CtClass>() {

			@Override
			public boolean matches(CtClass ctClass) {
				try {
					CtClass superclass = ctClass.getSuperclass();
					
					if (superclass != null) {
						if (superclass.getName().equals(ctClass.getName())) {
							return true;
						} else {
							return matches(superclass);
						}
					}
					
				} catch (Exception e) {
				}
				
				ctClass.detach();
				return false;
			}
			
		};
		return resolve(matcher);
	}

}
