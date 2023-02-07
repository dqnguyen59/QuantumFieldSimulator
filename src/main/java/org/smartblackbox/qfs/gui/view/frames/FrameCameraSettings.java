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
import org.lwjgl.system.MemoryStack;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.opengl.model.Camera;
import org.smartblackbox.qfs.settings.QFSProject;

public class FrameCameraSettings extends AbstractFrame {

	private int width = 300;
	private int height = 100;

	private QFSProject qfsProject = QFSProject.getInstance();
	private Camera camera = qfsProject.camera; 
	private StringBuffer bufFieldOfView = new StringBuffer();

	public FrameCameraSettings(NuklearModel frames) {
		super(frames);
	}

	@Override
	public String getTitle() {
		return "GL Settings";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);
		
		createLayout(ctx, 0, model.getMenuBarHeight(), width, height);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			camera.setFieldOfFiew(nk_label_edit(ctx, stack, " Field of View:", bufFieldOfView, camera.getFieldOfFiew(), 0.5, 0.5));
			camera.setFieldOfFiew(nk_slider(ctx, 0, camera.getFieldOfFiew(), 100, 1.00));
		}
	}

}