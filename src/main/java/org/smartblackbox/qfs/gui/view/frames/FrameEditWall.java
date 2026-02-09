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
package org.smartblackbox.qfs.gui.view.frames;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.opengl.model.QFSModel;
import org.smartblackbox.qfs.opengl.model.QFSModel.EditMode;
import org.smartblackbox.qfs.settings.QFSProject;
import org.smartblackbox.qfs.settings.SlitWallSettings;

public class FrameEditWall extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();
	private SlitWallSettings slitWall = qfsProject.slitWall;
	private QFSModel qfsModel = qfsProject.getQfsModel(); 

	private int width = 300;
	private int height = 420;
	private double leftCol = 0.50;
	private double rightCol = 0.50;

	private StringBuffer bufReflection = new StringBuffer();

	public FrameEditWall(NuklearModel frames) {
		super(frames);
	}

	@Override
	public String getTitle() {
		return "Edit Wall";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		createLayout(ctx, 0, model.getMenuBarHeight(), width, height);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			qfsModel.setWallVisible(nk_check_label(ctx, "Show Wall", qfsModel.isWallVisible(), leftCol, rightCol));
			
			Nuklear.nk_layout_row_static(ctx, rowHeight, 120, 1);
			if (Nuklear.nk_option_label(ctx, "None", qfsModel.getEditMode() == EditMode.none)) {
				qfsModel.setEditMode(EditMode.none);
			}

			if (Nuklear.nk_option_label(ctx, "Swap Node", qfsModel.getEditMode() == EditMode.swapNode)) {
				qfsModel.setEditMode(EditMode.swapNode);
			}

			if (Nuklear.nk_option_label(ctx, "Swap Nodes Z", qfsModel.getEditMode() == EditMode.swapNodesZ)) {
				qfsModel.setEditMode(EditMode.swapNodesZ);
			}

			if (Nuklear.nk_option_label(ctx, "Line", qfsModel.getEditMode() == EditMode.drawLine)) {
				qfsModel.setEditMode(EditMode.drawLine);
			}

			if (Nuklear.nk_option_label(ctx, "Wall", qfsModel.getEditMode() == EditMode.drawWall)) {
				qfsModel.setEditMode(EditMode.drawWall);
			}

			if (Nuklear.nk_option_label(ctx, "Erase Node", qfsModel.getEditMode() == EditMode.eraseNode)) {
				qfsModel.setEditMode(EditMode.eraseNode);
			}

			if (Nuklear.nk_option_label(ctx, "Erase Wall", qfsModel.getEditMode() == EditMode.eraseWall)) {
				qfsModel.setEditMode(EditMode.eraseWall);
			}

			if (Nuklear.nk_option_label(ctx, "Fill", qfsModel.getEditMode() == EditMode.fillWall)) {
				qfsModel.setEditMode(EditMode.fillWall);
			}

			nk_spacer(ctx, spacer1, 1);
			slitWall.setReflection(nk_label_edit(ctx, stack, " Reflection:",
				bufReflection, slitWall.getReflection(), leftCol, rightCol));
			slitWall.setReflection(nk_slider(ctx, 0.0, slitWall.getReflection(), 1.0, 0.01));
			nk_spacer(ctx, spacer1, 1);

			Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 4);
			Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_LEFT);
			if (nk_button_label(ctx, "Clear all")) {
				qfsModel.clearWalls();
			}
			if (nk_button_label(ctx, "Close")) {
				close();
			}
		}
	}

}