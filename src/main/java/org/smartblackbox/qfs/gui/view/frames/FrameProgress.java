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
package org.smartblackbox.qfs.gui.view.frames;

import org.lwjgl.PointerBuffer;
import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.opengl.model.QFSModel;
import org.smartblackbox.qfs.settings.QFSProject;

public class FrameProgress extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel(); 

	PointerBuffer progress = PointerBuffer.allocateDirect(1);

	public FrameProgress(NuklearModel frames) {
		super(frames);
		windowOptions = Nuklear.NK_WINDOW_NO_SCROLLBAR;
	}

	@Override
	public String getTitle() {
		return "Progress";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		NkColor color = ctx.style().window().fixed_background().data().color();
		NkColor tempColor = NkColor.create()
				.r(color.r())
				.g(color.g())
				.b(color.b())
				.a(color.a());

		ctx.style().window().fixed_background().data().color(Constants.TRANSPARENT_BG_COLOR);

		createLayoutCentered(ctx, 300, 40);

		ctx.style().window().fixed_background().data().color(tempColor);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {

		progress.put(0, (long) qfsModel.getProgress());

		Nuklear.nk_layout_row_dynamic(ctx, 30, 1);
		Nuklear.nk_progress(ctx, progress, 100, true);

	}

}