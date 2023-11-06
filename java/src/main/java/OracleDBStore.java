/*
 * Since: November 2023
 * Author: gvenzl
 * Name: OracleDBStore.java
 * Description: OracleDBStore implementation of the DBStore interface.
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class OracleDBStore implements DBStore {

    private final Connection conn;

    public OracleDBStore(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insertCountry(Country country) {
        String sql =
                """
                INSERT INTO countries
                  (country_id,
                   country_code,
                   name,
                   official_name,
                   population,
                   area_sq_km,
                   latitude,
                   longitude,
                   timezone,
                   region_id)
                   VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, country.countryId());
            stmt.setString(2, country.countryCode());
            stmt.setString(3, country.name());
            stmt.setString(4, country.officialName());
            stmt.setInt(5, country.population());
            stmt.setFloat(6, country.areaSqKM());
            stmt.setFloat(7, country.latitude());
            stmt.setFloat(8, country.longitude());
            stmt.setString(9, country.timezone());
            stmt.setString(10, country.regionId());

            stmt.executeQuery();
            conn.commit();
        }
        catch (SQLException e) {
            System.out.println("Cannot store country.");
            System.out.println(e.getMessage());
        }

    }

    @Override
    public Country getCountry(String countryId)
            throws NoSuchElementException {

        String sql =
                """
                SELECT country_id,
                       country_code,
                       name,
                       official_name,
                       population,
                       area_sq_km,
                       latitude,
                       longitude,
                       timezone,
                       region_id
                   FROM countries
                      WHERE country_id = ?""";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, countryId);
            ResultSet r = stmt.executeQuery();

            if (!r.next()) {
                throw new NoSuchElementException(String.format("Country with ID %s not found.", countryId));
            }

            return new Country(r.getString(1),
                               r.getString(2),
                               r.getString(3),
                               r.getString(4),
                               r.getInt(5),
                               r.getFloat(6),
                               r.getFloat(7),
                               r.getFloat(8),
                               r.getString(9),
                               r.getString(10)
                    );

        }
        catch (SQLException e) {
            System.out.println("Cannot retrieve country.");
            System.out.println(e.getMessage());
            return null;
        }
    }
}
