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
package org.smartblackbox.core.qfs.gui.view.frames;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.smartblackbox.core.qfs.gui.model.NuklearModel;
import org.smartblackbox.core.qfs.opengl.model.QFSModel;
import org.smartblackbox.core.qfs.settings.QFSProject;

public class FrameProperties extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel(); 

	public FrameProperties(NuklearModel frames) {
		super(frames);
		windowOptions = 0
				| Nuklear.NK_WINDOW_TITLE
				| Nuklear.NK_WINDOW_BORDER
				| Nuklear.NK_WINDOW_MOVABLE
				| Nuklear.NK_WINDOW_MINIMIZABLE
				| Nuklear.NK_WINDOW_CLOSABLE
		;
	}

	@Override
	public String getTitle() {
		return "Properties";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		int width = 250, height = 600;

        createLayout(ctx, getWindowWidth() - width, getWindowHeight() - height, width, height);
	}

	@Override
    public void layout(NkContext ctx, int x, int y, int width, int height) {
		Nuklear.nk_layout_row_static(ctx, 30, width / 2 - 16, 2);
        Nuklear.nk_label(ctx, " Width:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, "" + getWindowWidth(), Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, " Height:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, "" + getWindowHeight(), Nuklear.NK_TEXT_LEFT);
        
        Nuklear.nk_label(ctx, " Mouse.X:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, String.format("%.0f", qfsModel.getMousePos().x), Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, " Mouse.Y:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, String.format("%.0f", qfsModel.getMousePos().y), Nuklear.NK_TEXT_LEFT);
        
        Nuklear.nk_label(ctx, " CameraPos.x:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, "" + qfsProject.camera.getPosition().x, Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, " CameraPos.y:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, "" + qfsProject.camera.getPosition().y, Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, " CameraPos.z:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, "" + qfsProject.camera.getPosition().z, Nuklear.NK_TEXT_LEFT);
        
        Nuklear.nk_label(ctx, " CameraRot.x:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, "" + qfsProject.camera.getRotation().x, Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, " CameraRot.y:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, "" + qfsProject.camera.getRotation().y, Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, " CameraRot.z:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, "" + qfsProject.camera.getRotation().z, Nuklear.NK_TEXT_LEFT);
        
        Nuklear.nk_label(ctx, " NodeSelector.x:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, "" + qfsModel.getCurrentMouseNodeIndex().x, Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, " NodeSelector.y:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, "" + qfsModel.getCurrentMouseNodeIndex().y, Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, " NodeSelector.z:", Nuklear.NK_TEXT_LEFT);
        Nuklear.nk_label(ctx, "" + qfsModel.getCurrentMouseNodeIndex().z, Nuklear.NK_TEXT_LEFT);
    }

}