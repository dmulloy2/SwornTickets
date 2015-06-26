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
package net.dmulloy2.sworntickets.backend;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.dmulloy2.sworntickets.SwornTickets;
import net.dmulloy2.sworntickets.tickets.Ticket;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author dmulloy2
 */
public class YAMLBackend extends Backend {
	private static final String FILE_NAME = "tickets.yml";

	private final File file;

	public YAMLBackend(SwornTickets plugin) {
		super(plugin);
		this.file = new File(plugin.getDataFolder(), FILE_NAME);
	}

	@Override
	public void save(Map<Integer, Ticket> tickets) {
		try {
			if (file.exists())
				file.delete();

			file.createNewFile();

			YamlConfiguration config = new YamlConfiguration();
			config.set("tickets", tickets);

			config.save(file);
		} catch (Throwable ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving tickets to " + file));
		}
	}

	@Override
	public Map<Integer, Ticket> load() {
		try {
			if (! file.exists())
				return null;

			YamlConfiguration config = new YamlConfiguration();
			config.load(file);

			Map<Integer, Ticket> ret = new HashMap<Integer, Ticket>();
			ConfigurationSection section = config.getConfigurationSection("tickets");
			for (String key : section.getKeys(false)) {
				int id = NumberUtil.toInt(key);
				Ticket ticket = (Ticket) section.get(key);
				ret.put(id, ticket);
			}

			return ret;
		} catch (Throwable ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading tickets from " + file));
			return null;
		}
	}
}
