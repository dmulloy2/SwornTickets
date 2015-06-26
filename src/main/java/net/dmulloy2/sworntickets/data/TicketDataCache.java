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
package net.dmulloy2.sworntickets.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.sworntickets.SwornTickets;
import net.dmulloy2.sworntickets.tickets.Label;
import net.dmulloy2.sworntickets.tickets.Ticket;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */
public final class TicketDataCache {
	private final Map<Integer, Ticket> tickets;
	private final SwornTickets plugin;

	public TicketDataCache(SwornTickets plugin) {
		this.plugin = plugin;
		this.tickets = load();
	}

	private Map<Integer, Ticket> load() {
		Map<Integer, Ticket> tickets = plugin.getBackend().load();
		Map<Integer, Ticket> ret = new LinkedHashMap<>();
		if (tickets != null) {
			for (Entry<Integer, Ticket> entry : tickets.entrySet()) {
				int id = entry.getKey();
				if (nextID <= id) {
					nextID = id + 1;
				}

				ret.put(id, entry.getValue());
			}
		}

		return ret;
	}

	public void save() {
		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Saving tickets...");

		plugin.getBackend().save(tickets);

		plugin.getLogHandler().log("Tickets saved. Took {0} ms.", System.currentTimeMillis() - start);
	}

	public Ticket get(int id) {
		return tickets.get(id);
	}

	public Ticket newTicket(Player player, String description) {
		int id = getNextID();
		Ticket ticket = new Ticket(id, player, description);
		tickets.put(id, ticket);
		return ticket;
	}

	public void deleteTicket(int id) {
		tickets.remove(id);
	}

	public List<Ticket> getTicketsFor(Player player, boolean closed) {
		List<Ticket> ret = new ArrayList<>();

		for (Ticket ticket : tickets.values()) {
			if (ticket.isOwner(player)) {
				if (closed || ! ticket.isOpen()) {
					ret.add(ticket);
				}
			}
		}

		return ret;
	}

	public List<Ticket> getTickets(boolean closed) {
		List<Ticket> ret = new ArrayList<>();

		for (Ticket ticket : tickets.values()) {
			if (closed || ticket.isOpen()) {
				ret.add(ticket);
			}
		}

		return ret;
	}

	public List<Ticket> getTickets(Label label) {
		List<Ticket> ret = new ArrayList<>();

		for (Ticket ticket : tickets.values()) {
			if (ticket.getLabels().contains(label)) {
				ret.add(ticket);
			}
		}

		return ret;
	}

	public Map<Integer, Ticket> getTickets() {
		return tickets;
	}

	private int nextID = 1;

	public int getNextID() {
		while (tickets.containsKey(nextID)) {
			nextID++;
		}

		return nextID;
	}
}
