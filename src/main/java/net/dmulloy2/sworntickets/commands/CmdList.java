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

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.commands.PaginatedCommand;
import net.dmulloy2.sworntickets.SwornTickets;
import net.dmulloy2.sworntickets.tickets.Event;
import net.dmulloy2.sworntickets.tickets.Label;
import net.dmulloy2.sworntickets.tickets.Ticket;
import net.dmulloy2.sworntickets.types.Permission;
import net.dmulloy2.util.FormatUtil;

/**
 * @author dmulloy2
 */
public class CmdList extends PaginatedCommand {
	private List<Ticket> tickets;
	private final SwornTickets plugin;

	public CmdList(SwornTickets plugin) {
		super(plugin);
		this.name = "list";
		this.syntaxes = new SyntaxBuilder()
			.optionalArg("page")
			.optionalArg("-c")
			.newSyntax()
			.optionalArg("page")
			.optionalArg("-l")
			.optionalArg("label")
			.build();
		this.description = "List tickets";
		this.permission = Permission.CMD_LIST;
		this.usesPrefix = true;
		this.plugin = plugin;
	}

	@Override
	public int getListSize() {
		if (args.length > 2 && args[1].equalsIgnoreCase("-l")) {
			Label label = Label.getLabel(args[2]);
			if (label == null) {
				err("Label \"&c{0}&4\" not found!", args[2]);
				this.tickets = new ArrayList<Ticket>();
			} else {
				this.tickets = plugin.getDataCache().getTickets(label);
			}
		} else {
			boolean closed = args.length > 1 && args[1].equalsIgnoreCase("-c");
			this.tickets = plugin.getDataCache().getTickets(closed);
		}

		return tickets.size();
	}

	@Override
	public String getHeader(int index) {
		return FormatUtil.format("&3---- &eTickets &3-- &e{0}&3/&e{1} &3----", index, getPageCount());
	}

	@Override
	public String getLine(int index) {
		Ticket ticket = tickets.get(index);
		Event opened = ticket.getOpened();

		StringBuilder line = new StringBuilder();
		line.append("&e").append(ticket.getId()).append("&3) ");
		line.append("&b").append(opened.getName());
		line.append("&3 - &e").append(truncate(opened.getContent(), 42));
		return line.toString();
	}

	private String truncate(String string, int length) {
		if (string.length() > length) {
			return string.substring(0, length) + "...";
		}

		return string;
	}
}
