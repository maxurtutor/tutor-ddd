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

package org.maxur.ddd.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import lombok.Data;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * The type Config.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/4/2016</pre>
 */
public class Config extends Configuration {

/*
    @Valid
    @NotNull
    @JsonProperty
    @Getter
    private HikariConfig datasource = new HikariConfig();
*/

/*
    public DataSource dataSource() {
        final HikariConfig config = new HikariConfig();

        return new HikariDataSource(config);
    }
*/



    @Valid
    @NotNull
    @JsonProperty
    @Getter
    private JooqFactory jooqFactory = new JooqFactory();

    @Valid
    @NotNull
    @JsonProperty
    @Getter
    private Security security = new Security();


    /**
     * The type Security.
     */
    @Data
    public static class Security {

        @NotNull
        private List<String> tokens = newArrayList();

    }

    @Data
    public static class JooqFactory {

        private String dialect;
        private String logExecutedSql;
        private String renderSchema;
        private String renderNameStyle;
        private String renderKeywordStyle;
        private String renderFormatted;
        private String paramType;
        private String statementType;
        private String executeLogging;
        private String executeWithOptimisticLocking;
        private String attachRecords;
        private String updatablePrimaryKeys;

    }
}
