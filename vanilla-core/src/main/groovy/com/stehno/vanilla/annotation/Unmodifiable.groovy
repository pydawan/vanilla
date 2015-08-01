/*
 * Copyright (c) 2015 Christopher J. Stehno
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stehno.vanilla.annotation

import com.stehno.vanilla.transform.UnmodifiableTransform
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.*

/**
 * FIXME: document me
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
@GroovyASTTransformationClass(classes = [UnmodifiableTransform])
@interface Unmodifiable {

    /**
     * Allows you to provide the generated Immutable class with a list of classes which are deemed immutable. By supplying a class in this list,
     * you are vouching for its immutability and the Immutable will do no further checks.
     */
    Class[] knownImmutableClasses() default []

    /**
     * Allows you to provide the generated Immutable with a list of property names which are deemed immutable. By supplying a property's name in
     * this list, you are vouching for its immutability the Immutable will do no further checks.
     */
    String[] knownImmutables() default []
}