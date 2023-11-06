/*
 * Since: November 2023
 * Author: gvenzl
 * Name: OracleDBStoreTest.java
 * Description: JUnit tests for the OracleDBStore class.
 *
 * Copyright (c) 2023 Gerald Venzl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import oracle.jdbc.pool.OracleDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers
public class OracleDBStoreTest {

    private OracleDBStore store;

    @Container
    private static final OracleContainer oracle =
            new OracleContainer(
                    DockerImageName.parse("gvenzl/oracle-xe:slim-faststart"))
                    .withUsername("testUser")
                    .withPassword("superSecure")
                    .withCopyFileToContainer(
                            MountableFile.forHostPath("src/test/resources/setup.sh"),
                            "/container-entrypoint-initdb.d/"
                    );

    @Before
    public void setup() throws Exception {

        oracle.start();

        // Setup OracleDBStore
        var ods = new OracleDataSource();
        ods.setURL(oracle.getJdbcUrl());
        ods.setUser(oracle.getUsername());
        ods.setPassword(oracle.getPassword());

        var conn = ods.getConnection();
        conn.setAutoCommit(false);

        store = new OracleDBStore(conn);
    }

    @Test
    public void testRetrieveCountry() {
        System.out.println("Test whether country (Austria) can be retrieved.");
        var country = store.getCountry("AUT");
        Assertions.assertNotNull(country);
        System.out.println(country);
    }

    @Test
    public void testInsertCountry() {
        System.out.println("Test whether country can be inserted.");
        String countryId = "XYZ";

        var expectedCountry = new Country(
                countryId,
                "XZ",
                "mytest",
                null,
                0,
                0.0f,
                0.0f,
                0.0f,
                null,
                "EU");

        store.insertCountry(expectedCountry);

        var actualCountry = store.getCountry(countryId);

        Assertions.assertEquals(expectedCountry, actualCountry);
    }
}
