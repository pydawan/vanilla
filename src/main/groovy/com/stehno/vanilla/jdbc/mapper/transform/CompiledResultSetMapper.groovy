/*
 * Copyright (C) 2017 Christopher J. Stehno <chris@stehno.com>
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

import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
import groovy.transform.CompileStatic

import java.sql.ResultSet

/**
 * Abstract base class for ResultSetMapper implementations created using the compiled (AST) version of the DSL.
 */
@CompileStatic
@SuppressWarnings(['AbstractClassWithoutAbstractMethod', 'JdbcResultSetReference'])
abstract class CompiledResultSetMapper implements ResultSetMapper {

    private final String prefix

    protected CompiledResultSetMapper(final String prefix = '') {
        this.prefix = prefix
    }

    @Override
    String getPrefix() { prefix }

    @Override
    def mapRow(ResultSet rs, int row) {
        call(rs)
    }
}
