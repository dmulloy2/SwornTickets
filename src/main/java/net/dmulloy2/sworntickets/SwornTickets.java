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
package net.dmulloy2.sworntickets;

import java.util.Date;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.SwornAPI;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.commands.CmdHelp;
import net.dmulloy2.commands.Command;
import net.dmulloy2.handlers.CommandHandler;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.handlers.PermissionHandler;
import net.dmulloy2.sworntickets.backend.Backend;
import net.dmulloy2.sworntickets.backend.YAMLBackend;
import net.dmulloy2.sworntickets.backend.sql.MySQLBackend;
import net.dmulloy2.sworntickets.backend.sql.SQLiteBackend;
import net.dmulloy2.sworntickets.commands.CmdAssign;
import net.dmulloy2.sworntickets.commands.CmdCheck;
import net.dmulloy2.sworntickets.commands.CmdClose;
import net.dmulloy2.sworntickets.commands.CmdDelete;
import net.dmulloy2.sworntickets.commands.CmdLabel;
import net.dmulloy2.sworntickets.commands.CmdList;
import net.dmulloy2.sworntickets.commands.CmdOpen;
import net.dmulloy2.sworntickets.commands.CmdReload;
import net.dmulloy2.sworntickets.commands.CmdReply;
import net.dmulloy2.sworntickets.commands.CmdVersion;
import net.dmulloy2.sworntickets.data.TicketDataCache;
import net.dmulloy2.sworntickets.listeners.PlayerListener;
import net.dmulloy2.sworntickets.tickets.Event;
import net.dmulloy2.sworntickets.tickets.Label;
import net.dmulloy2.sworntickets.tickets.Ticket;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;

/**
 * @author dmulloy2
 */
@Getter
public class SwornTickets extends SwornPlugin {
	private Backend backend;
	private TicketDataCache dataCache;

	private PlayerListener listener;

	private final String prefix = FormatUtil.format("&3[&eSwornTickets&3]&e ");

	@Override
	public void onLoad() {
		SwornAPI.checkRegistrations();
		ConfigurationSerialization.registerClass(Event.class);
		ConfigurationSerialization.registerClass(Ticket.class);
	}

	@Override
	public void onEnable() {
		long start = System.currentTimeMillis();

		// Log handler
		logHandler = new LogHandler(this);

		// Configuration
		saveDefaultConfig();
		reloadConfig();

		// Other handlers
		commandHandler = new CommandHandler(this);
		permissionHandler = new PermissionHandler(this);

		// Register commands
		commandHandler.setCommandPrefix("ticket");
		commandHandler.registerPrefixedCommand(new CmdAssign(this));
		commandHandler.registerPrefixedCommand(new CmdCheck(this));
		commandHandler.registerPrefixedCommand(new CmdClose(this));
		commandHandler.registerPrefixedCommand(new CmdDelete(this));
		commandHandler.registerPrefixedCommand(new CmdHelp(this));
		commandHandler.registerPrefixedCommand(new CmdLabel(this));
		commandHandler.registerPrefixedCommand(new CmdList(this));
		commandHandler.registerPrefixedCommand(new CmdOpen(this));
		commandHandler.registerPrefixedCommand(new CmdReload(this));
		commandHandler.registerPrefixedCommand(new CmdReply(this));
		commandHandler.registerPrefixedCommand(new CmdVersion(this));

		// Register listener
		PluginManager pm = getServer().getPluginManager();
		listener = new PlayerListener(this);
		pm.registerEvents(listener, this);

		// Backend

		try {
			String type = getConfig().getString("backend");
			if (type.equalsIgnoreCase("yaml")) {
				backend = new YAMLBackend(this);
			} else if (type.equalsIgnoreCase("sqlite")) {
				backend = new SQLiteBackend(this);
			} else if (type.equalsIgnoreCase("mysql")) {
				String hostname = getConfig().getString("MySQL.hostname");
				String hostport = getConfig().getString("MySQL.hostport");
				String database = getConfig().getString("MySQL.database");
				String user = getConfig().getString("MySQL.user");
				String password = getConfig().getString("MySQL.password");

				backend = new MySQLBackend(this, hostname, hostport, database, user, password);
			} else {
				throw new IllegalArgumentException("Unsupported backend: " + type);
			}
		} catch (Throwable ex) {
			logHandler.log(Level.SEVERE, Util.getUsefulStack(ex, "setting up backend"));
			pm.disablePlugin(this);
			return;
		}

		// Labels
		Label.loadLabels(this);

		// Data Cache
		dataCache = new TicketDataCache(this);

		// Delete expired tickets
		int expired = expireTickets();
		if (expired > 0)
			logHandler.log("{0} expired tickets deleted.", expired);

		logHandler.log("{0} has been enabled. Took {1} ms.", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	@Override
	public void onDisable() {
		long start = System.currentTimeMillis();

		// Delete expired tickets
		int expired = expireTickets();
		if (expired > 0)
			logHandler.log("{0} expired tickets deleted.", expired);

		// Save data
		dataCache.save();

		// Shutdown backend
		backend.shutdown();

		logHandler.log("{0} has been disabled. Took {1} ms.", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	@Override
	public void reload() {
		reloadConfig();

		listener.reload();
		Label.loadLabels(this);
	}

	private int expireTickets() {
		int expirations = 0;

		for (Entry<Integer, Ticket> entry : dataCache.getTickets().entrySet()) {
			int id = entry.getKey();

			try {
				Ticket ticket = entry.getValue();
				Event closed = ticket.getClosed();
				if (closed == null)
					continue;

				long expiration = closed.getTimestamp();

				Date date = new Date();
				Date expires = new Date(expiration);
				if (date.compareTo(expires) >= 0) {
					dataCache.deleteTicket(id);
					if (backend.delete(id)) {
						expirations++;
					}
				}
			} catch (Throwable ex) {
				logHandler.log(Level.WARNING, Util.getUsefulStack(ex, "checking if ticket {0} expired", id));
			}
		}

		return expirations;
	}

	@Override
	public Command getDefaultCommand() {
		return commandHandler.getCommand("open");
	}
}
