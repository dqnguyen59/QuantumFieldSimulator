/*
 * Copyright (C) 2023  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org> and contributors
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

import java.util.ArrayList;

import org.smartblackbox.qfs.gui.model.AbstractDialogModel.ConfirmState;

public class DialogModelStack extends ArrayList<AbstractDialogModel> {

	private static final long serialVersionUID = -5345736749537814753L;

	public DialogFileModel pushDialogFileModel(ConfirmState[] confirmStates, String title, String message) {
		DialogFileModel dlgMessage = new DialogFileModel(confirmStates, title, message);
		add(dlgMessage);
		return dlgMessage;
	}
	
	public DialogDefaultModel pushDialogModel(ConfirmState[] confirmStates, String title, String message) {
		DialogDefaultModel dlgMessage = new DialogDefaultModel(confirmStates, title, message);
		add(dlgMessage);
		return dlgMessage;
	}
	
	public AbstractDialogModel pop() {
		if (size() > 0)
			remove(size() - 1);
		return getCurrent();
	}
	
	public AbstractDialogModel getCurrent() {
		if (size() > 0)
			return get(size() - 1);
		else
			return null;
	}

}
