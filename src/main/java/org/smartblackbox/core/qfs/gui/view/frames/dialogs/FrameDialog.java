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
package org.smartblackbox.core.qfs.gui.view.frames.dialogs;

import org.smartblackbox.core.qfs.gui.model.AbstractDialogModel;
import org.smartblackbox.core.qfs.gui.model.NuklearModel;
import org.smartblackbox.core.qfs.gui.view.frames.AbstractFrame;

public abstract class FrameDialog extends AbstractFrame {

	public FrameDialog(NuklearModel frames) {
		super(frames);
		isDialog = true;
	}

	@Override
	public void close() {
		model.getDialogStack().pop();
		super.close();
	}

	public AbstractDialogModel getDialogModel() {
		return model.getDialogStack().getCurrent();
	}

}
