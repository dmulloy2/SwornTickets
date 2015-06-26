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

import java.text.SimpleDateFormat;

import net.dmulloy2.commands.Command;
import net.dmulloy2.sworntickets.SwornTickets;
import net.dmulloy2.sworntickets.tickets.Ticket;
import net.dmulloy2.sworntickets.types.Permission;

/**
 * @author dmulloy2
 */
public abstract class SwornTicketsCommand extends Command {
	protected static final SimpleDateFormat FORMAT = new SimpleDateFormat("EEE, MMM d, yyyy @ hh:mm a");
	protected final SwornTickets plugin;

	public SwornTicketsCommand(SwornTickets plugin) {
		super(plugin);
		this.plugin = plugin;
		this.usesPrefix = true;
	}

	protected final Ticket getTicket(int arg) {
		return getTicket(arg, true);
	}

	protected final Ticket getTicket(int arg, boolean msg) {
		int id = argAsInt(arg, msg);
		if (id == -1) {
			return null;
		}

		Ticket ticket = plugin.getDataCache().get(id);
		if (ticket == null && msg) {
			err("Ticket &c{0} &4not found!", id);
			return null;
		}

		if (msg && ! checkAccess(ticket)) {
			return null;
		}

		return ticket;
	}

	protected final boolean checkAccess(Ticket ticket) {
		if (isPlayer()) {
			if (! hasPermission(Permission.MODERATOR) && ! ticket.isOwner(player)) {
				err("You do not have permission to see this ticket!");
				return false;
			}
		}

		return true;
	}
}
