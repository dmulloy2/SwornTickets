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
import java.util.concurrent.TimeUnit;

import net.dmulloy2.sworntickets.SwornTickets;
import net.dmulloy2.sworntickets.tickets.Event;
import net.dmulloy2.sworntickets.tickets.EventType;
import net.dmulloy2.sworntickets.tickets.Ticket;
import net.dmulloy2.sworntickets.types.Permission;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */
public class CmdClose extends SwornTicketsCommand {

	public CmdClose(SwornTickets plugin) {
		super(plugin);
		this.name = "close";
		this.aliases.add("reopen");
		this.addRequiredArg("id");
		this.description = "Close or reopen a ticket";
		this.permission = Permission.CMD_CLOSE;
	}

	@Override
	public void perform() {
		Ticket ticket = getTicket(0);
		if (ticket == null) {
			return;
		}

		if (ticket.isOpen()) {
			int days = plugin.getConfig().getInt("ticketExpiration", 7);
			long expiration = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days);
			Event close = Event.create(EventType.CLOSE, sender, Long.toString(expiration));
			ticket.addEvent(close);

			String format = FORMAT.format(new Date(expiration));
			for (Player online : Util.getOnlinePlayers()) {
				if (hasPermission(online, Permission.MODERATOR)) {
					sendpMessage(online, "Ticket &b{0} &ewas closed by &b{1}&e. It will expire on &b{2}&e.",
							ticket.getId(), getName(sender), format);
				}
			}

			plugin.getBackend().update(ticket);
		} else {
			Event reopen = Event.create(EventType.REOPEN, sender, "");
			ticket.addEvent(reopen);

			for (Player online : Util.getOnlinePlayers()) {
				if (hasPermission(online, Permission.MODERATOR)) {
					sendpMessage(online, "Ticket &b{0} &ewas reopened by &b{1}.", ticket.getId(), getName(sender));
				}
			}

			plugin.getBackend().update(ticket);
		}
	}
}
