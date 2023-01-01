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
package org.smartblackbox.core.qfs.gui.model;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

public class GroupModel {

	private String name;
	private ByteBuffer bufGroupName;
	private int _lastScrollX;
	private int _lastScrollY;
	private int scrollX;
	private int scrollY;
	private int lastScrollX;
	private int lastScrollY;
	private int targetScrollY;
	private boolean isScrolling;
	private int minFocusIndex = Integer.MAX_VALUE;
	private int maxFocusIndex = 0;

	public GroupModel(String name) {
		this.name = name;
	}
	
	public int getDeltaY() {
		return scrollY -lastScrollY;
	}
	
	public boolean is_scroll_changed(NkContext ctx, MemoryStack stack) {
		IntBuffer bufScrollX = stack.callocInt(1);
		IntBuffer bufScrollY = stack.callocInt(1);
		Nuklear.nk_group_get_scroll(ctx, bufGroupName, bufScrollX, bufScrollY);
		scrollX = bufScrollX.get();
		scrollY = bufScrollY.get();
		return lastScrollX != scrollX || lastScrollY != scrollY;
	}
	
	public void group_end() {
		lastScrollX = _lastScrollX;
		lastScrollY = _lastScrollY;
		_lastScrollX = scrollX;
		_lastScrollY = scrollY;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ByteBuffer getBufGroupName() {
		return bufGroupName;
	}

	public void setBufGroupName(ByteBuffer bufGroupName) {
		this.bufGroupName = bufGroupName;
	}

	public int getScrollX() {
		return scrollX;
	}

	public void setScrollX(int scrollX) {
		this.scrollX = scrollX;
	}

	public int getScrollY() {
		return scrollY;
	}

	public void setScrollY(int scrollY) {
		this.scrollY = scrollY;
	}

	public int getLastScrollX() {
		return lastScrollX;
	}

	public void setLastScrollX(int lastScrollX) {
		this.lastScrollX = lastScrollX;
	}

	public int getLastScrollY() {
		return lastScrollY;
	}

	public void setLastScrollY(int lastScrollY) {
		this.lastScrollY = lastScrollY;
	}

	public int getTargetScrollY() {
		return targetScrollY;
	}

	public void setTargetScrollY(int targetScrollY) {
		if (targetScrollY < 1) targetScrollY = 1;
		this.targetScrollY = targetScrollY;
	}

	public boolean isScrolling() {
		return isScrolling;
	}

	public void setScrolling(boolean isScrolling) {
		this.isScrolling = isScrolling;
	}

	public int scrollYDirection() {
		return scrollY > lastScrollY? -1 : 1;
	}

	public boolean isScrollYTargetReached() {
		return scrollY == targetScrollY;
	}

	public int getMinFocusIndex() {
		return minFocusIndex;
	}

	public void setMinFocusIndex(int minFocusIndex) {
		if (this.minFocusIndex > minFocusIndex)
			this.minFocusIndex = minFocusIndex;
	}

	public int getMaxFocusIndex() {
		return maxFocusIndex;
	}

	public void setMaxFocusIndex(int maxFocusIndex) {
		if (this.maxFocusIndex < maxFocusIndex)
			this.maxFocusIndex = maxFocusIndex;
	}

}
