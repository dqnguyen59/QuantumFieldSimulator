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

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;
import org.smartblackbox.core.qfs.gui.model.DialogDefaultModel;
import org.smartblackbox.core.qfs.gui.model.NuklearModel;
import org.smartblackbox.core.qfs.gui.model.AbstractDialogModel.ConfirmState;

public class FrameConfirmDialog extends FrameDialog {

	private int width = 500;
	private int height = 200;
	private DialogDefaultModel dlgModel;

	public FrameConfirmDialog(NuklearModel frames) {
		super(frames);
		dlgModel = (DialogDefaultModel) getDialogModel();
	}

	@Override
	public String getTitle() {
		return dlgModel.getTitle();
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		createLayoutCentered(ctx, width, height);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
    		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
    		
    		String[] lines = dlgModel.getMessage().split("\n");
    		
    		Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_LEFT);
    		for (String line : lines) {
        		Nuklear.nk_label(ctx, line, Nuklear.NK_TEXT_CENTERED);
			}
    		Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_LEFT);

    		ConfirmState[] confirmStates = dlgModel.getConfirmStates();
    		
    		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, confirmStates.length + 2);
    		Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_CENTERED);

    		for (ConfirmState confirmState : confirmStates) {
    			if (nk_button_label(ctx, confirmState.value())) {
    				dlgModel.setConfirmState(confirmState);
    				close();
                }
			}

    		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
    		Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_CENTERED);
        }
		
	}

}