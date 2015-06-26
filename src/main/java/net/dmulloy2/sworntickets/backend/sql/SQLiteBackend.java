/**
 * Copyright (c) 2015 dmulloy2
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.dmulloy2.sworntickets.backend.sql;

import java.io.File;
import java.sql.DriverManager;
import java.sql.Statement;

import net.dmulloy2.io.Closer;
import net.dmulloy2.sworntickets.SwornTickets;

/**
 * @author dmulloy2
 */
public class SQLiteBackend extends SQLBackend {
	private static final String TABLE_NAME = "Tickets";

	public SQLiteBackend(SwornTickets plugin) throws Throwable {
		super(plugin);

		Class.forName("org.sqlite.JDBC");
		File tickets = new File(plugin.getDataFolder(), "tickets.db");
		connection = DriverManager.getConnection("jdbc:sqlite:" + tickets.getAbsolutePath());

		Statement statement = null;

		try {
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(id INTEGER, yaml varchar(max));");
		} finally {
			Closer.closeQuietly(statement);
		}
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}
}
