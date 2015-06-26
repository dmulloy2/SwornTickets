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

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dmulloy2.util.NumberUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.google.common.base.Objects;

/**
 * @author dmulloy2
 */
@Getter
@AllArgsConstructor
@SerializableAs("net.dmulloy2.Event")
public class Event implements ConfigurationSerializable {
	private final EventType type;
	private final String name;
	private final String uniqueId;
	private final String content;
	private final long timestamp;

	public static Event create(EventType type, CommandSender sender, String content) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			return new Event(type, player.getName(), player.getUniqueId().toString(), content, System.currentTimeMillis());
		} else {
			return new Event(type, sender.getName(), sender.getName(), content, System.currentTimeMillis());
		}
	}

	public Event(Map<String, Object> args) {
		this.type = EventType.valueOf((String) args.get("type"));
		this.name = (String) args.get("playerName");
		this.uniqueId = (String) args.get("player");
		this.content = (String) args.get("content");
		this.timestamp = (long) args.get("timestamp");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new LinkedHashMap<String, Object>();

		data.put("type", type.name());
		data.put("playerName", name);
		data.put("player", uniqueId);
		data.put("content", content);
		data.put("timestamp", timestamp);

		return data;
	}

	public long contentAsLong() {
		return NumberUtil.toLong(content);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(type, uniqueId, content);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Event) {
			Event other = (Event) object;
			return other.type == this.type && other.uniqueId.equals(this.uniqueId) && other.content.equals(this.content);
		}

		return false;
	}

	@Override
	public String toString() {
		return "Event[type=" + type + ", player=" + name + ", content=" + content + ", timestamp=" + timestamp + "]";
	}
}
