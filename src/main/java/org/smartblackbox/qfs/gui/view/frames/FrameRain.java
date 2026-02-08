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
import org.smartblackbox.qfs.opengl.model.RainModel;
import org.smartblackbox.qfs.settings.QFSProject;

public class FrameRain extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();
	private RainModel rainModel = qfsProject.rainModel;

	private int width = 300;
	private int height = 290;
	private double leftCol = 0.50;
	private double rightCol = 0.50;

	private StringBuffer bufConstantFrequency = new StringBuffer();
	private StringBuffer bufRainForce = new StringBuffer();
	private StringBuffer bufRainIteration = new StringBuffer();

	public FrameRain(NuklearModel frames) {
		super(frames);
	}

	@Override
	public String getTitle() {
		return "QFS Rain";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		createLayout(ctx, 0, nuklearModel.getMenuBarHeight(), width, height);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			rainModel.setActive(nk_check_label(ctx, "Active", rainModel.isActive(), 0.5, 0.5));

			qfsProject.setConstantFrequency(nk_label_edit(ctx, stack, " Constant Frequency:", bufConstantFrequency, qfsProject.getConstantFrequency(), leftCol, rightCol));
			qfsProject.setConstantFrequency(nk_slider(ctx, 0, qfsProject.getConstantFrequency(), 1, 0.0001));
			nk_spacer(ctx, spacer1, 1);

			rainModel.setForce(nk_label_edit(ctx, stack, " Force:", bufRainForce, rainModel.getForce(), leftCol, rightCol));
			rainModel.setForce(nk_slider(ctx, -2500, rainModel.getForce(), 2500, 0.1));
			nk_spacer(ctx, spacer1, 1);

			rainModel.setIterations(nk_label_edit(ctx, stack, " Iterations:", bufRainIteration, rainModel.getIterations(), leftCol, rightCol));
			rainModel.setIterations((int) nk_slider(ctx, 1, rainModel.getIterations(), 50, 1));
			nk_spacer(ctx, spacer1, 1);
		}
	}

}