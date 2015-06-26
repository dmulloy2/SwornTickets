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

import net.dmulloy2.sworntickets.SwornTickets;
import net.dmulloy2.sworntickets.tickets.Event;
import net.dmulloy2.sworntickets.tickets.EventType;
import net.dmulloy2.sworntickets.tickets.Ticket;
import net.dmulloy2.sworntickets.types.Permission;
import net.dmulloy2.util.Util;

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */
public class CmdAssign extends SwornTicketsCommand {

	public CmdAssign(SwornTickets plugin) {
		super(plugin);
		this.name = "assign";
		this.aliases.add("take");
		this.addRequiredArg("id");
		this.addOptionalArg("player");
		this.description = "Assign yourself to a ticket";
		this.permission = Permission.CMD_ASSIGN;
	}

	@Override
	public void perform() {
		Ticket ticket = getTicket(0);
		if (ticket == null) {
			return;
		}

		OfflinePlayer target = null;
		if (args.length == 1) {
			if (isPlayer()) {
				target = player;
			} else {
				err("You must specify a player!");
				return;
			}
		} else {
			target = Util.matchOfflinePlayer(args[1]);
			if (target == null) {
				err("Player \"&c{0}&4\" not found!");
				return;
			}
		}

		Event assigned = ticket.getAssigned();
		if (assigned != null && assigned.getContent().equals(target.getName())) {
			// Remove their assignment
			Event remove = Event.create(EventType.ASSIGN, sender, "-" + target.getName());
			ticket.addEvent(remove);

			sendpMessage("You have removed &b{0}&e''s assignment.", target.getName());
			return;
		}

		assigned = Event.create(EventType.ASSIGN, sender, target.getName());
		ticket.addEvent(assigned);

		if (sender.equals(target)) {
			sendpMessage("You have assigned yourself to ticket #&b{0}", ticket.getId());
		} else {
			sendpMessage("You have assigned &b{0} &eto ticket #&b{1}", target.getName(), ticket.getId());
		}
	}
}
