/*
 * Copyright (c) 2016 Maxim Yunusov
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.maxur.ddd.dao;

import org.h2.tools.RunScript;
import org.maxur.ddd.Launcher;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.lang.String.format;
import static org.maxur.ddd.config.ServiceLocatorProvider.service;

/**
 * ## A collection of JDBI helper methods.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>02.02.2016</pre>
 */
public final class DBUtils {

    /**
     * Utils class with private constructor.
     */
    private DBUtils() {
    }

    /**
     * Executes SQL queries by scripts files names.
     *
     * @param scripts scripts files names.
     */
    public static void runScripts(List<String> scripts) {
        DBI dbi = service(DBI.class);
        try (Handle handle = dbi.open()) {
            for (String script : scripts) {
                runScript(handle, script);
            }
        }
    }

    private static void runScript(Handle h, String script) {
        try (
                InputStream is = Launcher.class.getResourceAsStream(script);
                Reader reader = new InputStreamReader(is);
                Connection connection = h.getConnection();
        ) {
            final ResultSet resultSet = RunScript.execute(connection, reader);
            if (resultSet != null) { // NOSONAR
                resultSet.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException(format("Error reading from file %s", script), e);
        } catch (SQLException e) {
            throw new IllegalStateException(format("Error executing SQL script from %s", script), e);
        }
    }
}
