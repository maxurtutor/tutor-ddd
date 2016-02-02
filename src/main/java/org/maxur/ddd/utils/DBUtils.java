package org.maxur.ddd.utils;

import org.h2.tools.RunScript;
import org.maxur.ddd.Launcher;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;

import static org.maxur.ddd.commons.domain.ServiceLocatorProvider.service;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>02.02.2016</pre>
 */
public final class DBUtils {

    private DBUtils() {
    }

    public static void runScript(String script) throws IOException, SQLException {
        DBI dbi = service(DBI.class);
        try (Handle handle = dbi.open()) {
            runScript(handle, script);
        }
    }

    private static void runScript(Handle h, String script) throws IOException, SQLException {
        try (
                InputStream is = Launcher.class.getResourceAsStream(script);
                Reader reader = new InputStreamReader(is)
        ) {
            RunScript.execute(h.getConnection(), reader);
        }
    }
}
