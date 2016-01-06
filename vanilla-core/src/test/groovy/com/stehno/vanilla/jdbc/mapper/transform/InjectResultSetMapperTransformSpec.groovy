/*
 * Copyright (C) 2016 Christopher J. Stehno <chris@stehno.com>
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
package com.stehno.vanilla.jdbc.mapper.transform

import com.stehno.vanilla.jdbc.DummyObjectC
import com.stehno.vanilla.test.Person
import spock.lang.Specification

import static com.stehno.vanilla.test.Assertions.assertMatches
import static com.stehno.vanilla.test.jdbc.mock.ResultSetBuilder.resultSet

class InjectResultSetMapperTransformSpec extends Specification {

    private final GroovyShell shell = new GroovyShell()

    def 'implicit mapper'() {
        setup:
        def person = new Person(name: 'Bob', age: 42, birthDate: new Date())

        def rs = resultSet {
            columns 'name', 'age', 'birth_date', 'bank_pin'
            data person.name, person.age, person.birthDate.format('yyyy-MM-dd'), person.bankPin
            data null, 0, null, null
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import java.time.format.*

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.IMPLICIT

            class Foo {
                @InjectResultSetMapper(
                    value = com.stehno.vanilla.test.Person,
                    style = IMPLICIT,
                    config = {
                        ignore 'bankPin'
                        ignore 'pet'
                        map 'birthDate' fromDate 'birth_date'
                        map 'age' from 2 using { a -> a - 5 }
                        map 'name' from 'name'
                        ignore 'children'
                    }
                )
                static ResultSetMapper createMapper(){}
            }

            Foo.createMapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        rs.next()
        def empty = mapper(rs)

        then:
        assertMatches(
            obj,
            name: person.name,
            age: person.age - 5,
            birthDate: { act -> person.birthDate.format('yyyy-MM-dd') == act.format('yyyy-MM-dd') },
            bankPin: person.bankPin
        )

        assertMatches(
            empty,
            name: null,
            age: -5,
            birthDate: null,
            bankPin: null
        )
    }

    def 'implicit mapper without config should map everything'() {
        setup:
        DummyObjectC objectC = new DummyObjectC('Larry', 56, 125.65f)
        objectC.somethingElse = 55 as byte

        def rs = resultSet {
            columns 'name', 'age', 'weight', 'something_else'
            object objectC
            object new DummyObjectC()
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import java.time.format.*
            import com.stehno.vanilla.jdbc.DummyObjectC

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.IMPLICIT

            class Foo {
                @InjectResultSetMapper(
                    value = DummyObjectC,
                    style = IMPLICIT
                )
                static ResultSetMapper createMapper(){}
            }

            Foo.createMapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        rs.next()
        def empty = mapper.call(rs)

        then:
        obj == objectC
        empty == new DummyObjectC()
    }

    def 'unspecified style should be implicit'() {
        setup:
        DummyObjectC objectC = new DummyObjectC('Larry', 56, 125.65f)
        objectC.somethingElse = 55 as byte

        def rs = resultSet {
            columns 'name', 'age', 'weight', 'something_else'
            object objectC
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import java.time.format.*
            import com.stehno.vanilla.jdbc.DummyObjectC

            class Foo {
                @InjectResultSetMapper(DummyObjectC)
                static ResultSetMapper createMapper(){}
            }

            Foo.createMapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        then:
        obj == objectC
    }

    def 'explicit mapper'() {
        setup:
        def person = new Person(name: 'Bob', age: 42, birthDate: new Date())

        def rs = resultSet {
            columns 'name', 'age', 'birth_date', 'bank_pin'
            data person.name, person.age, person.birthDate.format('yyyy-MM-dd'), person.bankPin
            data null, 0, null, null
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import java.time.format.*

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.EXPLICIT

            class Foo {
                @InjectResultSetMapper(
                    value = com.stehno.vanilla.test.Person,
                    style = EXPLICIT,
                    config = {
                        map 'birthDate' fromDate 'birth_date\'
                        map 'age' from 2 using { a -> a - 5 }
                        map 'name' using { n-> "Name: $n"}
                    }
                )
                static ResultSetMapper createMapper(){}
            }

            Foo.createMapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        rs.next()
        def empty = mapper(rs)

        then:
        assertMatches(
            obj,
            name: "Name: ${person.name}",
            age: person.age - 5,
            birthDate: { act -> person.birthDate.format('yyyy-MM-dd') == act.format('yyyy-MM-dd') },
            bankPin: person.bankPin
        )

        assertMatches(
            empty,
            name: 'Name: null',
            age: -5,
            birthDate: null,
            bankPin: null
        )
    }

    def 'explicit mapper with setter-property'() {
        setup:
        def dummy = new DummyObjectC('Fred', 42, 250.6f)
        dummy.somethingElse = 56 as byte

        def rs = resultSet {
            columns 'name', 'age', 'weight', 'something_else'
            object dummy
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import java.time.format.*
            import com.stehno.vanilla.jdbc.DummyObjectC

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.EXPLICIT

            class Foo {
                @InjectResultSetMapper(
                    value = DummyObjectC,
                    style = EXPLICIT,
                    config = {
                        map 'name' fromString 'name'
                        map 'age' fromInt 'age'
                        map 'weight' fromFloat 'weight'
                        map 'somethingElse' fromByte 'something_else'
                    }
                )
                static ResultSetMapper createMapper(){}
            }

            Foo.createMapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        then:
        obj == dummy
    }
}
