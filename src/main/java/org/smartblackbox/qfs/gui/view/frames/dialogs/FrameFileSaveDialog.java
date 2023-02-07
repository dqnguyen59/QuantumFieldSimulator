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
package org.smartblackbox.qfs.gui.view.frames.dialogs;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.gui.model.AbstractDialogModel.ConfirmState;
import org.smartblackbox.qfs.gui.model.DialogDefaultModel;
import org.smartblackbox.qfs.gui.model.DialogFileModel;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.utils.Utils;

public class FrameFileSaveDialog extends FrameDialog {

	private int width = 400;
	private int height = 370;

	private double leftCol = 0.2;
	private double rightCol = 0.8;
	private DialogFileModel dlgFileModel;
	private DialogDefaultModel confirmDialog;
	private StringBuffer bufFileName = new StringBuffer();

	public FrameFileSaveDialog(NuklearModel frames) {
		super(frames);
		dlgFileModel = (DialogFileModel) getDialogModel();
	}

	@Override
	public String getTitle() {
		return dlgFileModel.getTitle();
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);
		
		createLayoutCentered(ctx, width, height);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			Nuklear.nk_layout_row_dynamic(ctx, 250, 1);
			boolean isSaveFilePerformed = nk_file_list_view(ctx, Constants.BASE_PATH + appSettings.getProjectFilePath(), Constants.PROJECT_FILE_EXT, dlgFileModel);
			
			dlgFileModel.setSelectedFileName(nk_label_edit(ctx, stack, " File: ", bufFileName , dlgFileModel.getSelectedFileName(), leftCol, rightCol));
    		
    		nk_spacer(ctx, spacer1, 1);
    		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, dlgFileModel.getConfirmStates().length + 2);
    		Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_LEFT);
    		for (ConfirmState confirmState : dlgFileModel.getConfirmStates()) {
    			if (nk_button_label(ctx, confirmState.value())) {
    				if (confirmState == ConfirmState.save)
    					isSaveFilePerformed = true;
    				else {
    					dlgFileModel.setConfirmState(confirmState);
    					close();
    				}
                }
			}
			nk_spacer(ctx, spacer1, 1);

			if (isSaveFilePerformed) {
				if (Utils.fileExists(appSettings.getProjectFilePath() + Constants.SEPARATOR + dlgFileModel.getSelectedFileName())) {
					confirmDialog = model.showConfirmDialog(DialogDefaultModel.NO_YES, "File save",
							"File '" + dlgFileModel.getSelectedFileName() + "' already exists!\nDo you want to overwrite?");
				}
				else
					save();
			}

			if (confirmDialog != null && confirmDialog.getConfirmState() == ConfirmState.yes)
				save();
		}
	}
	
	private void save() {
		dlgFileModel.setConfirmState(ConfirmState.save);
		close();
	}
	
}