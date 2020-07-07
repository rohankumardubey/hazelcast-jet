/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.sql.impl.connector.file;

import com.hazelcast.jet.sql.SqlTestSupport;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import static com.hazelcast.jet.sql.JetSqlConnector.CSV_SERIALIZATION_FORMAT;
import static com.hazelcast.jet.sql.JetSqlConnector.TO_SERIALIZATION_FORMAT;
import static java.time.ZoneOffset.UTC;
import static java.util.Collections.singletonList;

public class SqlCsvTest extends SqlTestSupport {

    @Test
    public void supportsAllTypes() {
        String name = createRandomName();
        executeSql("CREATE EXTERNAL TABLE " + name + " ("
                + "string VARCHAR"
                + ", character0 CHAR"
                + ", boolean0 BOOLEAN"
                + ", byte0 TINYINT"
                + ", short0 SMALLINT"
                + ", int0 INT"
                + ", long0 BIGINT"
                + ", bigDecimal DEC(10, 1)"
                + ", bigInteger NUMERIC(5, 0)"
                + ", float0 REAL"
                + ", double0 DOUBLE"
                + ", \"localTime\" TIME"
                + ", localDate DATE"
                + ", localDateTime TIMESTAMP"
                + ", \"date\" TIMESTAMP WITH LOCAL TIME ZONE (\"DATE\")"
                + ", calendar TIMESTAMP WITH TIME ZONE (\"CALENDAR\")"
                + ", instant TIMESTAMP WITH LOCAL TIME ZONE"
                + ", zonedDateTime TIMESTAMP WITH TIME ZONE (\"ZONED_DATE_TIME\")"
                + ", offsetDateTime TIMESTAMP WITH TIME ZONE"
                + ") TYPE \"" + FileSqlConnector.TYPE_NAME + "\" "
                + "OPTIONS ("
                + "\"" + FileSqlConnector.TO_DIRECTORY + "\" '" + RESOURCES_PATH + "'"
                + ", \"" + FileSqlConnector.TO_GLOB + "\" '" + "all-types.csv" + "'"
                + ", \"" + TO_SERIALIZATION_FORMAT + "\" '" + CSV_SERIALIZATION_FORMAT + "'"
                + ")"
        );

        assertRowsEventuallyAnyOrder(
                "SELECT * FROM " + name,
                singletonList(new Row(
                        "string"
                        , "a"
                        , true
                        , (byte) 126
                        , (short) 32766
                        , 2147483646
                        , 9223372036854775806L
                        , new BigDecimal("9223372036854775.111")
                        , new BigDecimal("9223372036854775222")
                        , 1234567890.1F
                        , 123451234567890.1
                        , LocalTime.of(12, 23, 34)
                        , LocalDate.of(2020, 7, 1)
                        , LocalDateTime.of(2020, 7, 1, 12, 23, 34, 100_000_000)
                        , OffsetDateTime.of(2020, 7, 1, 12, 23, 34, 200_000_000, UTC)
                        , OffsetDateTime.of(2020, 7, 1, 12, 23, 34, 300_000_000, UTC)
                        , OffsetDateTime.of(2020, 7, 1, 12, 23, 34, 400_000_000, UTC)
                        , OffsetDateTime.of(2020, 7, 1, 12, 23, 34, 500_000_000, UTC)
                        , OffsetDateTime.of(2020, 7, 1, 12, 23, 34, 600_000_000, UTC)
                ))
        );
    }

    @Test
    public void supportsSchemaDiscovery() {
        String name = createRandomName();
        executeSql("CREATE EXTERNAL TABLE " + name + " "
                + "TYPE \"" + FileSqlConnector.TYPE_NAME + "\" "
                + "OPTIONS ("
                + "\"" + FileSqlConnector.TO_DIRECTORY + "\" '" + RESOURCES_PATH + "'"
                + ", \"" + FileSqlConnector.TO_GLOB + "\" '" + "file.csv" + "'"
                + ", \"" + TO_SERIALIZATION_FORMAT + "\" '" + CSV_SERIALIZATION_FORMAT + "'"
                + ")"
        );

        assertRowsEventuallyAnyOrder(
                "SELECT string2, string1 FROM " + name,
                singletonList(new Row("value2", "value1"))
        );
    }

    private static String createRandomName() {
        return "csv_" + randomString().replace('-', '_');
    }
}