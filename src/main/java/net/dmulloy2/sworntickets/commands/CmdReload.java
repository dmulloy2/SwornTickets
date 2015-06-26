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
import net.dmulloy2.sworntickets.types.Permission;

/**
 * @author dmulloy2
 */
public class CmdReload extends SwornTicketsCommand {

	public CmdReload(SwornTickets plugin) {
		super(plugin);
		this.name = "reload";
		this.aliases.add("rl");
		this.description = "reload " + plugin.getName();
		this.permission = Permission.CMD_RELOAD;
	}

	@Override
	public void perform() {
		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Reloading {0}...", plugin.getName());
		sendpMessage("&aReloading {0}...", plugin.getName());

		plugin.reload();

		sendpMessage("&aReload Complete! Took {0} ms!", System.currentTimeMillis() - start);
		plugin.getLogHandler().log("Reload complete! Took {0} ms!", System.currentTimeMillis() - start);
	}
}
