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
package net.dmulloy2.sworntickets.listeners;

import java.util.List;

import net.dmulloy2.sworntickets.SwornTickets;
import net.dmulloy2.sworntickets.tickets.Ticket;
import net.dmulloy2.sworntickets.types.Permission;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author dmulloy2
 */
public class PlayerListener implements Listener, Reloadable {
	private final SwornTickets plugin;

	private boolean displayTicketsStaff;
	private boolean displayTicketsUsers;

	public PlayerListener(SwornTickets plugin) {
		this.plugin = plugin;
		this.reload();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (displayTicketsStaff && plugin.getPermissionHandler().hasPermission(player, Permission.MODERATOR)) {
			List<Ticket> tickets = plugin.getDataCache().getTickets(false);
			if (tickets.size() > 0) {
				player.sendMessage(plugin.getPrefix() +
						FormatUtil.format("There are &b{0} &eopen tickets. Type &b/ticket &3list &eto view them.", tickets.size()));
			}
		} else if (displayTicketsUsers) {
			List<Ticket> tickets = plugin.getDataCache().getTicketsFor(player, false);
			if (tickets.size() > 0) {
				player.sendMessage(plugin.getPrefix() + FormatUtil.format("You have &b{0} &eopen tickets.", tickets.size()));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (plugin.getPending().containsKey(player)) {
			plugin.getPending().remove(player);
		}
	}

	@Override
	public void reload() {
		this.displayTicketsStaff = plugin.getConfig().getBoolean("onJoin.displayTicketsStaff");
		this.displayTicketsUsers = plugin.getConfig().getBoolean("onJoin.displayTicketsUsers");
	}
}
