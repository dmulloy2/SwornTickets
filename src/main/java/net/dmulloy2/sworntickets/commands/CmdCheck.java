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
package net.dmulloy2.sworntickets.commands;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dmulloy2.chat.ComponentBuilder;
import net.dmulloy2.chat.HoverEvent;
import net.dmulloy2.chat.HoverEvent.Action;
import net.dmulloy2.sworntickets.SwornTickets;
import net.dmulloy2.sworntickets.tickets.Event;
import net.dmulloy2.sworntickets.tickets.EventType;
import net.dmulloy2.sworntickets.tickets.Label;
import net.dmulloy2.sworntickets.tickets.Ticket;
import net.dmulloy2.sworntickets.types.Permission;
import net.dmulloy2.types.LazyLocation;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.TimeUtil;
import net.md_5.bungee.api.ChatColor;

/**
 * @author dmulloy2
 */
public class CmdCheck extends SwornTicketsCommand {

	public CmdCheck(SwornTickets plugin) {
		super(plugin);
		this.name = "check";
		this.aliases.add("info");
		this.addRequiredArg("id");
		this.description = "Check a ticket";
		this.permission = Permission.CMD_CHECK;
	}

	@Override
	public void perform() {
		Ticket ticket = getTicket(0);
		if (ticket == null) {
			return;
		}

		sendMessage("&3[ &eTicket #&b{0} &3]", ticket.getId());

		List<Label> labels = ticket.getLabels();
		if (labels.size() > 0) {
			ComponentBuilder builder = new ComponentBuilder(ChatColor.YELLOW + "Labels:");
			for (Label label : labels) {
				builder.append(" " + label.getFormat());
				builder.event(new HoverEvent(Action.SHOW_TEXT, label.getDescription()));
			}

			sendMessage(builder.create());
		}

		Event assigned = ticket.getAssigned();
		if (assigned != null && ! assigned.getContent().contains("-")) {
			sendMessage("&eAssigned: &b{0}", assigned.getContent());
		}

		LazyLocation location = ticket.getLocation();
		sendMessage("&eLocation: &b{0}&e, &b{1}&e, &b{2} &ein &b{3}", location.getX(), location.getY(), location.getZ(),
				location.getWorldName());

		sendMessage("");

		List<Event> events = ticket.getEvents();
		if (! events.isEmpty()) {
			sendMessage("&eActivity:");
			for (Event event : events) {
				EventType type = event.getType();

				StringBuilder actionBuilder = new StringBuilder();
				String[] actions = type.getActions();

				String name = event.getName();
				String content = event.getContent();
				if (type == EventType.ASSIGN) {
					if (content.equals(name)) { // Self assignment
						actionBuilder.append(actions[1]);
					} else if (content.equals("-" + name)) { // Removal of self assignment
						actionBuilder.append(actions[2].replace("%t", "their"));
					} else if (content.startsWith("-")) { // Standard removal
						actionBuilder.append(actions[2].replace("%t", "&e" + content.substring(1) + "&3''s"));
					} else { // Standard assignment
						actionBuilder.append(actions[0].replace("%t", content));
					}
				} else {
					actionBuilder.append(actions[0]);
				}

				long timestamp = event.getTimestamp();
				long difference = System.currentTimeMillis() - timestamp;

				StringBuilder diffBuilder = new StringBuilder();
				if (difference < TimeUnit.DAYS.toMillis(1)) { // Today
					diffBuilder.append("&e").append(TimeUtil.formatTime(difference)).append(" &3ago");
				} else if (difference < TimeUnit.DAYS.toMillis(2)) { // Yesterday
					diffBuilder.append("&eYesterday");
				} else if (difference < TimeUnit.DAYS.toMillis(30)) { // This month
					diffBuilder.append("&e").append(TimeUnit.MILLISECONDS.toDays(difference)).append(" &3days ago");
				} else { // Any other day
					diffBuilder.append("&3on &e").append(FormatUtil.format("{0,date,medium}", new Date(timestamp)));
				}

				if (type.hasContent()) {
					actionBuilder.append(":");
				}

				String action = actionBuilder.toString()
						.replace("%p", name)
						.replace("%d", diffBuilder.toString());

				if (type == EventType.LABEL) {
					String[] split = content.split(";");
					Label label = Label.getLabel(split[1]);
					if (label != null) {
						action = action
								.replace("%a", split[0])
								.replace("%l", label.getFormat())
								.replace("removeed", "removed");
					}
				}

				sendMessage(" &3- " + action);

				if (type.hasContent()) {
					sendMessage("   &r" + capitalizeFirst(content));
				}
			}
		}
	}
}
