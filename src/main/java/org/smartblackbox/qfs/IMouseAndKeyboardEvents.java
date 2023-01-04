/*
 * Copyright (C) 2023  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.nl> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * 
 * File created on 01/01/2023
 */
package org.smartblackbox.qfs;

import org.smartblackbox.qfs.opengl.controller.KeyEvents;

public interface IMouseAndKeyboardEvents {

	public boolean mouseCursorPosCallback(long handle, double xPos, double yPos);
	
	public boolean mouseButtonCallback(long handle, int button, int action, int mods);
	
	public boolean mouseScrollCallback(long handle, double xoffset, double yoffset);
	
	public boolean mouseCursorEnterCallback(long handle, boolean entered);
	
	public boolean charCallback(long handle, int unicode);

	public boolean keyCallback(long handle, int key, int scanCode, int action, int mods);
	
	public boolean keyContinuousCallback(long handle, int key, KeyEvents keyEvents);
	
}
