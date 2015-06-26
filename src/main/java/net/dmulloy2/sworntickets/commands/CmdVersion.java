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
public class CmdVersion extends SwornTicketsCommand {

	public CmdVersion(SwornTickets plugin) {
		super(plugin);
		this.name = "version";
		this.aliases.add("v");
		this.description = "Display version information";
		this.permission = Permission.MODERATOR;
	}

	@Override
	public void perform() {
		sendMessage("&3====[ &eShadowTickets &3]====");
		sendMessage("&eVersion: &b{0}", plugin.getDescription().getVersion());
		sendMessage("&eAuthor: &bdmulloy2");
		sendMessage("&eIssues: &b/ticket");
	}
}
