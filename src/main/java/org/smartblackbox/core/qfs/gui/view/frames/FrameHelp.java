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

public class FrameHelp extends AbstractFrame {

    private int width = 850;
	private int height = 830;
	private float leftCol = 0.3f;
	private float rightCol = 0.7f;
	private int alignLabel = Nuklear.NK_TEXT_LEFT;
	private int alignValue = Nuklear.NK_TEXT_LEFT;

	public FrameHelp(NuklearModel frames) {
		super(frames);
	}

	@Override
	public String getTitle() {
		return "Help";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		createLayoutCentered(ctx, width, height);
	}

	@Override
	public void layout(NkContext ctx, int x, int y, int width, int height) {
		
		nk_spacer(ctx, spacer1 , 1);
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
		Nuklear.nk_label(ctx, " Move keys:", Nuklear.NK_TEXT_LEFT);
		nk_label_value(ctx, "   - A:", "Move Left", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - D:", "Move Right", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - W:", "Move Forward", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - S:", "Move Backward", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - Z:", "Move Down", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - X:", "Move Up", leftCol, rightCol, alignLabel, alignValue);
		Nuklear.nk_label(ctx, " Camera:", Nuklear.NK_TEXT_LEFT);
		nk_label_value(ctx, "   - Right-click:", "Rotate Camera", leftCol, rightCol, alignLabel, alignValue);
		
		nk_spacer(ctx, spacer1 , 1);
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
		Nuklear.nk_label(ctx, " Rotate Field:", Nuklear.NK_TEXT_LEFT);
		nk_label_value(ctx, "   - Ctrol->KeyPad-4:", "Rotate Z-Axis", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - Ctrol->KeyPad-6:", "Rotate Z-Axis", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - Ctrol->KeyPad-2:", "Rotate X-Axis", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - Ctrol->KeyPad-8:", "Rotate X-Axis", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - Ctrol->KeyPad-1:", "Rotate Y-Axis", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - Ctrol->KeyPad-3:", "Rotate Y-Axis", leftCol, rightCol, alignLabel, alignValue);
		
		nk_spacer(ctx, spacer1 , 1);
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
		Nuklear.nk_label(ctx, " Mode keys:", Nuklear.NK_TEXT_LEFT);
		nk_label_value(ctx, "   - N:", "Normal Mode", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - N:", "Ctrl-> [Up, Down] (Move Z slice plane)", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - R:", "Rain Mode", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - G:", "Alpha Mode (change the alpha color of the nodes)", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - I:", "Intensity Mode (change the intensity color of the nodes)", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - L:", "Move Light Mode (Ctrl-{Shift}-> [Left, Right, Up, Down, Page Up, Page Down]])",
				leftCol, rightCol, alignLabel, alignValue);
		
		nk_spacer(ctx, spacer1 , 1);
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
		Nuklear.nk_label(ctx, " Other keys:", Nuklear.NK_TEXT_LEFT);
		nk_label_value(ctx, "   - F8:", "Turn physics simulatation on or off", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - F12:", "Swap between slice (XY plance) and XYZ (All)", leftCol, rightCol, alignLabel, alignValue);
		nk_label_value(ctx, "   - SPACEBAR:", "Force node to move down, see Menu Rain to change the force", leftCol, rightCol, alignLabel, alignValue);

		nk_spacer(ctx, spacer1 , 1);
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 3);
		Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_CENTERED);
		if (nk_button_label(ctx, "Close")) {
			close();
		}
		nk_spacer(ctx, spacer1 , 1);
	}

}