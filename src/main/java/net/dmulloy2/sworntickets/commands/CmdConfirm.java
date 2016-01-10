/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.sworntickets.commands;

import net.dmulloy2.sworntickets.SwornTickets;
import net.dmulloy2.sworntickets.tickets.Ticket;
import net.dmulloy2.sworntickets.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdConfirm extends SwornTicketsCommand {

	public CmdConfirm(SwornTickets plugin) {
		super(plugin);
		this.name = "confirm";
		this.description = "Confirm ticket creation";
		this.permission = Permission.CMD_OPEN;
		this.mustBePlayer = true;
	}

	@Override
	public void perform() {
		String description = plugin.getPending().get(player);
		if (description == null) {
			err("You must use &c/ticket open &4first!");
			return;
		}

		Ticket ticket = plugin.getDataCache().newTicket(player, description);
		sendpMessage("&eYou have opened ticket #&b{0}", ticket.getId());
	}
}