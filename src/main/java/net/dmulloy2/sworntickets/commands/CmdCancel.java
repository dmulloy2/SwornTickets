/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.sworntickets.commands;

import net.dmulloy2.sworntickets.SwornTickets;
import net.dmulloy2.sworntickets.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdCancel extends SwornTicketsCommand {

	public CmdCancel(SwornTickets plugin) {
		super(plugin);
		this.name = "cancel";
		this.description = "Cancel ticket creation";
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

		plugin.getPending().remove(player);
		sendpMessage("Ticket creation cancelled!");
	}
}