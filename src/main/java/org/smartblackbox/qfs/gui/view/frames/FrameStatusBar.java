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

import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.gui.model.NuklearModel.Frame;
import org.smartblackbox.qfs.gui.model.Theme;
import org.smartblackbox.qfs.opengl.model.QFSModel;
import org.smartblackbox.qfs.opengl.model.QFSModel.Mode;
import org.smartblackbox.qfs.settings.QFSProject;

public class FrameStatusBar extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel(); 

	private int width;
	private int height;
	private float[] col = {230, 90, 60, 60, 10, 50, 40, 40, 40, 40, 40, 40, 40};

	public FrameStatusBar(NuklearModel frames) {
		super(frames);
		windowOptions = Nuklear.NK_WINDOW_NO_SCROLLBAR;
	}

	@Override
	public String getTitle() {
		return "Statusbar";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		width = getWindowWidth();
		height = Constants.STATUS_BAR_HEIGHT;

		NkColor savedColor = NkColor.create().set(ctx.style().window().fixed_background().data().color());
		
		ctx.style().window().fixed_background().data().color(Constants.TRANSPARENT_BG_COLOR);

		createLayout(ctx, 0, getWindowHeight() - height, width, height);
		ctx.style().window().fixed_background().data().color(savedColor);
	}

	@Override
	public void layout(NkContext ctx, int x, int y, int width, int height) {
		NkColor savedCol = NkColor.create().set(ctx.style().button().normal().data().color());

		int i = 0;
		Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, 30, 10);
		Nuklear.nk_layout_row_push(ctx, col[i++] / width);
		Nuklear.nk_label(ctx, String.format(" CurrentNode.xyz: (%d, %d, %d);",
				qfsModel.getCurrentMouseNodeIndex().x,
				qfsModel.getCurrentMouseNodeIndex().y,
				qfsModel.getCurrentMouseNodeIndex().z
        		), Nuklear.NK_TEXT_LEFT);

		Nuklear.nk_layout_row_push(ctx, col[i++] / width);
		Nuklear.nk_label(ctx, String.format(" Simulation: "), Nuklear.NK_TEXT_LEFT);

		ctx.style().button().normal().data().color(qfsModel.isSimulating()? Theme.blue1 : savedCol);
		
		Nuklear.nk_layout_row_push(ctx, col[i++] / width);
		if (nk_button_label(ctx, qfsModel.getBtnSimulatingLabel()))
			qfsModel.toggleSimulation();

		ctx.style().button().normal().data().color(Theme.blue1);
		Nuklear.nk_layout_row_push(ctx, col[i++] / width);
		if (nk_button_label(ctx, "Reset")) {
			qfsProject.reset();
		}

		Nuklear.nk_layout_row_push(ctx, col[i++] / width);
		Nuklear.nk_label(ctx, String.format(""), Nuklear.NK_TEXT_LEFT);

		Nuklear.nk_layout_row_push(ctx, col[i++] / width);
		Nuklear.nk_label(ctx, String.format(" Mode: "), Nuklear.NK_TEXT_LEFT);
		
		String s = "";
		String tip = "";
		for (Mode m : Mode.values()) {
			Nuklear.nk_layout_row_push(ctx, col[i++] / width);

			ctx.style().button().normal().data().color(m == qfsModel.getMode()? Theme.blue1 : savedCol);
			
			switch (m) {
			case normal:
				s = "N";
				tip = "Normal mode";
				break;
			case rain:
				s = "R";
				tip = "Rain mode: click on a node to simulate a rain drop";
				break;
			case alhpa:
				s = "A";
				tip = "Alpha mode: keys [up | down ]";
				break;
			case intensity:
				s = "I";
				tip = "Intensity mode: keys [up | down]";
				break;
			case light:
				s = "L";
				tip = "Light mode: Ctrl-{Shift}->[up | down | left | right | page-up | page-down]";
				break;
			default:
				s = "";
				break;
			}
			
			tooltip(ctx, tip);
			if (nk_button_label(ctx, s)) {
				qfsModel.setMode(m);
				if (m == Mode.rain)
					nuklearModel.toggleFrame(Frame.rain);
			}
		}
		
		Nuklear.nk_layout_row_end(ctx);

		ctx.style().button().normal().data().color(savedCol);
	}

}