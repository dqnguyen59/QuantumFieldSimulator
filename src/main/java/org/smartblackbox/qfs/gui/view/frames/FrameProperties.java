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

import org.joml.Vector3d;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.opengl.model.QFSModel;
import org.smartblackbox.qfs.settings.QFSProject;

public class FrameProperties extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel(); 

	private float leftCol = 0.6f;
	private float rightCol = 0.4f;

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
		nk_label_value(ctx, " Width:", String.format("%d", getWindowWidth()), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " Height", String.format("%d", getWindowHeight()), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_spacer(ctx, spacer1);

        nk_label_value(ctx, " Mouse.X", String.format("%.0f", qfsModel.getMousePos().x), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " Mouse.Y", String.format("%.0f", qfsModel.getMousePos().y), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_spacer(ctx, spacer1);

		nk_label_value(ctx, " CameraPos.x", String.format("%.2f", qfsProject.camera.getPosition().x), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " CameraPos.y", String.format("%.2f", qfsProject.camera.getPosition().y), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " CameraPos.z", String.format("%.2f", qfsProject.camera.getPosition().z), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " CameraRot.x", String.format("%.2f", qfsProject.camera.getRotation().x), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " CameraRot.y", String.format("%.2f", qfsProject.camera.getRotation().y), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " CameraRot.z", String.format("%.2f", qfsProject.camera.getRotation().z), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_spacer(ctx, spacer1);

        nk_label_value(ctx, " NodeSelector.x", String.format("%d", qfsModel.getCurrentMouseNodeIndex().x), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " NodeSelector.y", String.format("%d", qfsModel.getCurrentMouseNodeIndex().y), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " NodeSelector.z", String.format("%d", qfsModel.getCurrentMouseNodeIndex().z), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_spacer(ctx, spacer1);
		
        Vector3d p = qfsModel.getCurrentMouseNode().getPosition();
        nk_label_value(ctx, " NodePos.x", String.format("%.8f", p.x), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " NodePos.y", String.format("%.8f", p.y), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " NodePos.z", String.format("%.8f", p.z), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_spacer(ctx, spacer1);
	}

}