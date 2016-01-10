/**
 * (c) 2015 dmulloy2
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