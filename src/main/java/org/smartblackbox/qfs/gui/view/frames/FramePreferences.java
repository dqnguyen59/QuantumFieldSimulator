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
import org.smartblackbox.qfs.opengl.model.RainModel;
import org.smartblackbox.qfs.settings.QFSProject;

public class FramePreferences extends AbstractFrame {

	private int width = 300;
	private int height = 200;
	private float leftCol = 0.6f;
	private float rightCol = 0.4f;

	private QFSProject qfsProject = QFSProject.getInstance();
	private RainModel rainModel = qfsProject.rainModel;
	
	private StringBuffer bufNumThreads = new StringBuffer();
	private StringBuffer bufSwapInterval = new StringBuffer();
	private StringBuffer bufForce = new StringBuffer();

	public FramePreferences(NuklearModel frames) {
		super(frames);
	}

	@Override
	public String getTitle() {
		return "Preferences";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);
		
		createLayout(ctx, 0, model.getMenuBarHeight() + 120, width, height);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			appSettings.setNumThreads(nk_label_edit(ctx, stack, " CPU Threads:", bufNumThreads, appSettings.getNumThreads(), leftCol, rightCol));
			nk_spacer(ctx, spacer1, 1);
			
			qfsProject.setGlSwapInterval(nk_label_edit(ctx, stack, " GLSwap Interval:", bufSwapInterval, qfsProject.getGlSwapInterval(), leftCol, rightCol));
			nk_spacer(ctx, spacer1, 1);
			
			rainModel.setForce(nk_label_edit(ctx, stack, " Force:", bufForce, rainModel.getForce(), leftCol, rightCol));
			rainModel.setForce(nk_slider(ctx, -2500, rainModel.getForce(), 2500, 0.1));
			nk_spacer(ctx, spacer1, 1);

			Nuklear.nk_layout_row_end(ctx);
		}
	}

}