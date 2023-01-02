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

public class FrameAbout extends AbstractFrame {

    private int width = 850;
	private int height = 330;

	public FrameAbout(NuklearModel frames) {
		super(frames);
	}

	@Override
	public String getTitle() {
		return "About";
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
		Nuklear.nk_label(ctx, " About", Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_label(ctx, " ", Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_label(ctx, " Quantum Field Simulator", Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_label(ctx, " Version: 2023-01 (1.19)", Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_label(ctx, " ", Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_label(ctx, " (c) Copyright Duy Quoc Nguyen <d.q.nguyen@smartblackbox.nl> and contributers. All rights reserved. ", Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_label(ctx, " ", Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_label(ctx, " Website: https://www.smartblackbox.nl ", Nuklear.NK_TEXT_LEFT);

		nk_spacer(ctx, spacer1 , 1);
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 3);
		Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_CENTERED);
		if (nk_button_label(ctx, "Close")) {
			close();
		}
		nk_spacer(ctx, spacer1 , 1);
	}

}