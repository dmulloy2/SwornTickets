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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.types.LazyLocation;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */
@SerializableAs("net.dmulloy2.Ticket")
public class Ticket implements ConfigurationSerializable {
	private int id;
	private LazyLocation location;
	private List<String> labels = new ArrayList<String>();
	private List<Event> events = new ArrayList<Event>();

	private transient List<Label> actualLabels = null;

	private Ticket() { }

	public Ticket(int id, Player player, String description) {
		this.id = id;

		Event open = Event.create(EventType.OPEN, player, description);
		events.add(open);

		this.location = new LazyLocation(player);
	}

	public Ticket(Map<String, Object> args) {
		for (Entry<String, Object> entry : args.entrySet()) {
			try {
				for (Field field : getClass().getDeclaredFields()) {
					if (field.getName().equals(entry.getKey())) {
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						field.set(this, entry.getValue());
						field.setAccessible(accessible);
					}
				}
			} catch (Throwable ex) {
			}
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new LinkedHashMap<>();

		for (Field field : getClass().getDeclaredFields()) {
			if (Modifier.isTransient(field.getModifiers()))
				continue;

			try {
				boolean accessible = field.isAccessible();

				field.setAccessible(true);

				if (field.getType().equals(Integer.TYPE)) {
					if (field.getInt(this) != 0)
						data.put(field.getName(), field.getInt(this));
				} else if (field.getType().equals(Long.TYPE)) {
					if (field.getLong(this) != 0)
						data.put(field.getName(), field.getLong(this));
				} else if (field.getType().equals(Boolean.TYPE)) {
					if (field.getBoolean(this))
						data.put(field.getName(), field.getBoolean(this));
				} else if (field.getType().isAssignableFrom(Collection.class)) {
					if (! ((Collection<?>) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				} else if (field.getType().isAssignableFrom(String.class)) {
					if (((String) field.get(this)) != null)
						data.put(field.getName(), field.get(this));
				} else if (field.getType().isAssignableFrom(Map.class)) {
					if (! ((Map<?, ?>) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				} else {
					if (field.get(this) != null)
						data.put(field.getName(), field.get(this));
				}

				field.setAccessible(accessible);
			} catch (Throwable ex) {
			}
		}

		return data;
	}

	public int getId() {
		return id;
	}

	public LazyLocation getLocation() {
		return location;
	}

	public List<Label> getLabels() {
		if (actualLabels == null) {
			actualLabels = new ArrayList<Label>();
			for (String name : labels) {
				Label label = Label.getLabel(name);
				if (label != null) {
					actualLabels.add(label);
				}
			}
		}

		return actualLabels;
	}

	public void addLabel(CommandSender sender, Label label) {
		getLabels().add(label);
		labels.add(label.getName());

		events.add(Event.create(EventType.LABEL, sender, "add;" + label.getName()));
	}

	public boolean hasLabel(Label label) {
		return getLabels().contains(label);
	}

	public void removeLabel(CommandSender sender, Label label) {
		getLabels().remove(label);
		labels.remove(label.getName());

		events.add(Event.create(EventType.LABEL, sender, "remove;" + label.getName()));
	}

	public List<Event> getEvents() {
		return events;
	}

	public void addEvent(Event event) {
		events.add(event);
	}

	private Event getByType(EventType type) {
		ListIterator<Event> iter = events.listIterator(events.size());
		while (iter.hasPrevious()) {
			Event event = iter.previous();
			if (event.getType() == type) {
				return event;
			}
		}

		return null;
	}

	public boolean hasEvent(EventType type) {
		return getByType(type) != null;
	}

	// Will always be the first event
	public Event getOpened() {
		return events.get(0);
	}

	public Event getClosed() {
		return getByType(EventType.CLOSE);
	}

	public boolean isOpen() {
		ListIterator<Event> iter = events.listIterator(events.size());
		while (iter.hasPrevious()) {
			Event event = iter.previous();
			if (event.getType() == EventType.REOPEN) {
				return true;
			} else if (event.getType() == EventType.CLOSE) {
				return false;
			}
		}

		return true;
	}

	public Event getAssigned() {
		return getByType(EventType.ASSIGN);
	}

	public boolean isOwner(Player player) {
		return getOpened().getUniqueId().equals(player.getUniqueId().toString());
	}

	// TODO Use proper SQL
	public static Ticket fromResultSet(ResultSet rs) throws Throwable {
		Ticket ticket = new Ticket();
		ticket.id = rs.getInt("id");

		String yaml = rs.getString("yaml");
		YamlConfiguration config = new YamlConfiguration();
		config.loadFromString(yaml);

		return (Ticket) ConfigurationSerialization.deserializeObject(config.getValues(false), Ticket.class);
	}
}
