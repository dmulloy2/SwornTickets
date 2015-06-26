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
package net.dmulloy2.sworntickets.tickets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author dmulloy2
 */
@Data
@AllArgsConstructor
public class Label {
	private String name;
	private String format;
	private String description;

	private Label() {
	}

	private static List<Label> labels;
	public static List<Label> getLabels() {
		return labels;
	}

	public static Label getLabel(String name) {
		for (Label label : labels) {
			if (label.getName().equalsIgnoreCase(name)) {
				return label;
			}
		}

		return null;
	}

	public static void loadLabels(SwornPlugin plugin) {
		try {
			labels = new ArrayList<Label>();

			FileConfiguration config = plugin.getConfig();
			if (config.isSet("labels")) {
				ConfigurationSection section = config.getConfigurationSection("labels");

				Set<String> keys = section.getKeys(false);
				for (String key : keys) {
					try {
						Label label = new Label();
						label.name = section.getString(key + ".name", "");
						label.format = FormatUtil.format(section.getString(key + ".format", ""));
						label.description = FormatUtil.format(section.getString(key + ".description", ""));
						labels.add(label);
					} catch (Throwable ex) {
						plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading ticket {0}", key));
					}
				}
			}
		} catch (Throwable ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading tickets"));
		}
	}
}
