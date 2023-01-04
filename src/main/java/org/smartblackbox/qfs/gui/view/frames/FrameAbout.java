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

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.utils.AppInfo;

public class FrameAbout extends AbstractFrame {

    private int width = 850;
	private int height = 320;

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
		
		String copyRight = String.format("(c) Copyright %s <%s> and contributers. All right reserved.", AppInfo.getAuthor(), AppInfo.getEmail());
		String version = String.format("Version: %s    (%s)", AppInfo.getVersion(), AppInfo.getDateTime());
		
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight * 5, 1);
		Nuklear.nk_label_wrap(ctx, AppInfo.getDescription() != null? AppInfo.getDescription() : "Only visible when running from JAR MANIFEST.MF.");
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
		Nuklear.nk_label(ctx, version, Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_label(ctx, copyRight, Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_label(ctx, AppInfo.getWebsite(), Nuklear.NK_TEXT_LEFT);

		nk_spacer(ctx, spacer1 , 1);
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 3);
		Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_CENTERED);
		if (nk_button_label(ctx, "Close")) {
			close();
		}
		nk_spacer(ctx, spacer1 , 1);
	}

}