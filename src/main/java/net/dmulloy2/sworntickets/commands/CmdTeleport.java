/**
 * Copyright (c) 2016 dmulloy2
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
import net.dmulloy2.sworntickets.tickets.Ticket;
import net.dmulloy2.sworntickets.types.Permission;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

public class CmdTeleport extends SwornTicketsCommand {

	public CmdTeleport(SwornTickets plugin) {
		super(plugin);
		this.name = "teleport";
		this.aliases.add("tp");
		this.addRequiredArg("id");
		this.description = "Teleport to a ticket''s location";
		this.permission = Permission.CMD_TELEPORT;
		this.mustBePlayer = true;
	}

	@Override
	public void perform() {
		Ticket ticket = getTicket(0);
		if (ticket == null) {
			return;
		}

		Location location = ticket.getLocation().getLocation();
		player.teleport(location);

		sendpMessage("You have been teleported to ticket &b{0}&e''s location.", ticket.getId());
	}
}