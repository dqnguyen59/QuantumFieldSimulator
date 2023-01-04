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
package org.smartblackbox.qfs.gui.model;

import org.lwjgl.nuklear.NkRect;

public abstract class AbstractDialogModel {

	public enum ConfirmState {
		none("None"),
		yes("Yes"),
		no("No"),
		abort("Abort"),
		cancel("Cancel"),
		ok("Ok"),
		open("Open"),
		save("Save"),
		;
		
	    private String value;
	    
	    ConfirmState(String value) {
	        this.value = value;
	    }
	 
	    public String value() {
	        return value;
	    }
	}
	
	public static final ConfirmState[] NO_YES = {ConfirmState.no, ConfirmState.yes};
	public static final ConfirmState[] OK = {ConfirmState.ok};
	public static final ConfirmState[] CANCEL_OK = {ConfirmState.cancel, ConfirmState.ok};
	public static final ConfirmState[] CANCEL_OPEN = {ConfirmState.cancel, ConfirmState.open};
	public static final ConfirmState[] CANCEL_SAVE = {ConfirmState.cancel, ConfirmState.save};

	protected ConfirmState[] confirmStates;
	protected ConfirmState confirmState = ConfirmState.none;
	private NkRect windowRect;
	
	public AbstractDialogModel(ConfirmState[] confirmStates) {
		setConfirmStates(confirmStates);
	}

	public ConfirmState[] getConfirmStates() {
		return confirmStates;
	}

	public void setConfirmStates(ConfirmState[] confirmStates) {
		this.confirmStates = confirmStates;
	}
	
	public ConfirmState getConfirmState() {
		return confirmState;
	}

	public void setConfirmState(ConfirmState confirmState) {
		this.confirmState = confirmState;
	}

	public NkRect getWindowRect() {
		return windowRect;
	}

	public void setWindowRect(NkRect rect) {
		this.windowRect = rect;
	}

}
