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
package net.dmulloy2.sworntickets.tickets;

import lombok.Getter;

/**
 * @author dmulloy2
 */
@Getter
public enum EventType {
	ASSIGN("&e%p &3assigned this to &e%t &e%d", "&e%p &3self assigned this &e%d", "&e%p &3removed %t &3assignment &e%d"),
	CLOSE("&e%p &3closed this &e%d"),
	LABEL("&e%p &3%aed label &r%l &e%d"),
	OPEN("&e%p &3opened &3%d"),
	REOPEN("&e%p &3repoened this &e%d"),
	REPLY("&e%p &3commented &e%d"),
	;

	private final String[] actions;
	private EventType(String... actions) {
		this.actions = actions;
	}

	public boolean hasContent() {
		switch (this) {
			case OPEN:
			case REPLY:
				return true;
			default:
				return false;
		}
	}
}
