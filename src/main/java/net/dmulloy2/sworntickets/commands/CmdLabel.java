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

import java.util.List;

import net.dmulloy2.chat.ClickEvent;
import net.dmulloy2.chat.ComponentBuilder;
import net.dmulloy2.chat.HoverEvent;
import net.dmulloy2.sworntickets.SwornTickets;
import net.dmulloy2.sworntickets.tickets.Label;
import net.dmulloy2.sworntickets.tickets.Ticket;
import net.dmulloy2.sworntickets.types.Permission;
import net.dmulloy2.util.FormatUtil;
import net.md_5.bungee.api.ChatColor;

/**
 * @author dmulloy2
 */

public class CmdLabel extends SwornTicketsCommand {

	public CmdLabel(SwornTickets plugin) {
		super(plugin);
		this.name = "label";
		this.syntaxes = new SyntaxBuilder()
			.requiredArg("id")
			.requiredArg("label")
			.newSyntax()
			.requiredArg("-list")
			.build();
		this.description = "Label an issue";
		this.permission = Permission.CMD_LABEL;
	}

	@Override
	public void perform() {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("-list")) {
				List<Label> labels = Label.getLabels();
				if (labels.size() > 0) {
					ComponentBuilder builder = new ComponentBuilder(ChatColor.YELLOW + "Labels:");
					for (Label label : labels) {
						builder.append(" " + label.getFormat());
						builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, label.getDescription()));
						builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ticket list 1 -l " + label.getName()));
					}

					sendMessage(builder.create());
				} else {
					err("There are no labels to display.");
				}

				return;
			} else {
				invalidSyntax();
				return;
			}
		}

		Ticket ticket = getTicket(0);
		if (ticket == null) {
			return;
		}

		Label label = Label.getLabel(args[1]);
		if (label == null) {
			err("Label \"&c{0}&4\" does not exist!");
			return;
		}

		boolean added = false;
		if (ticket.hasLabel(label)) {
			ticket.removeLabel(sender, label);
		} else {
			ticket.addLabel(sender, label);
			added = true;
		}

		String message = plugin.getPrefix() + FormatUtil.format("&eYou have {0} label ", added ? "added" : "removed");
		ComponentBuilder builder = new ComponentBuilder(message)
			.append(label.getFormat())
			.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, label.getDescription()))
			.append(FormatUtil.format(" &e{0} ticket #&b{1}", added ? "to" : "from", ticket.getId()));

		sendMessage(builder.create());
	}
}
