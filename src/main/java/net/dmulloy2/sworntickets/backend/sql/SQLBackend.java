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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.dmulloy2.io.Closer;
import net.dmulloy2.sworntickets.SwornTickets;
import net.dmulloy2.sworntickets.backend.Backend;
import net.dmulloy2.sworntickets.tickets.Ticket;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author dmulloy2
 */
public abstract class SQLBackend extends Backend {
	protected PreparedStatement delete = null;
	protected PreparedStatement insert = null;
	protected PreparedStatement update = null;
	protected PreparedStatement load = null;

	protected Connection connection;

	public SQLBackend(SwornTickets plugin) {
		super(plugin);
	}

	public Connection getConnection() {
		return connection;
	}

	protected final void prepareStatements() throws SQLException {
		String tableName = getTableName();
		delete = connection.prepareStatement("DELETE FROM " + tableName + " WHERE id=?;");
		insert = connection.prepareStatement("INSERT INTO " + tableName + "(id, yaml) values(?, ?)");
		update = connection.prepareStatement("UPDATE " + tableName + " SET yaml=? WHERE id=?;");
		load = connection.prepareStatement("SELECT * FROM " + tableName + ";");
	}

	@Override
	public Map<Integer, Ticket> load() {
		Map<Integer, Ticket> ret = new HashMap<>();

		try {
			ResultSet results = load.executeQuery();
			while (results.next()) {
				Ticket ticket = Ticket.fromResultSet(results);
				ret.put(ticket.getId(), ticket);
			}
		} catch (Throwable ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading tickets"));
		}

		return ret;
	}

	@Override
	public boolean add(Ticket ticket) {
		try {
			YamlConfiguration config = new YamlConfiguration();
			config.createSection("ticket", ticket.serialize());
			String yaml = config.saveToString();
			
			insert.setInt(1, ticket.getId());
			insert.setString(2, yaml);

			insert.executeUpdate();
			return true;
		} catch (SQLException ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "adding ticket " + ticket.getId()));
			return false;
		}
	}

	@Override
	public void update(Ticket ticket) {
		try {
			YamlConfiguration config = new YamlConfiguration();
			config.createSection("ticket", ticket.serialize());
			String yaml = config.saveToString();

			update.setString(1, yaml);
			update.setInt(2, ticket.getId());
			
			update.executeUpdate();
		} catch (SQLException ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "updating ticket " + ticket.getId()));
		}
	}

	@Override
	public boolean delete(int id) {
		try {
			delete.setInt(1, id);
			delete.executeUpdate();
			return true;
		} catch (SQLException ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "deleting ticket " + id));
			return false;
		}
	}

	@Override
	public void shutdown() {
		Closer.closeQuietly(delete);
		delete = null;

		Closer.closeQuietly(insert);
		insert = null;

		Closer.closeQuietly(update);
		update = null;

		Closer.closeQuietly(load);
		load = null;

		Closer.closeQuietly(connection);
	}

	protected abstract String getTableName();
}
