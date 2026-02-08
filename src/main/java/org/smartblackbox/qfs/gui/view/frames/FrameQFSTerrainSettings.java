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
import org.smartblackbox.qfs.opengl.model.Terrain;
import org.smartblackbox.qfs.settings.QFSProject;

public class FrameQFSTerrainSettings extends AbstractFrame {

	private int width = 300;
	private int height = 326;
	private float leftCol = 0.6f;
	private float rightCol = 0.4f;

	private QFSProject qfsProject = QFSProject.getInstance();
	private Terrain terrain = qfsProject.terrain;
	
	private StringBuffer bufTerrainScale = new StringBuffer();
	private StringBuffer bufTerrainShininess = new StringBuffer();
	private StringBuffer bufTerrainDepthVisibility = new StringBuffer();

	public FrameQFSTerrainSettings(NuklearModel frames) {
		super(frames);
	}

	@Override
	public String getTitle() {
		return "QFS Terrain Settings";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);
		
		createLayout(ctx, 0, nuklearModel.getMenuBarHeight() + 120, width, height);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			terrain.setVisible(nk_check_label(ctx, " Terrain Visible", terrain.isVisible(), leftCol, rightCol));
			
			terrain.setScale(nk_label_edit(ctx, stack, " Scale:", bufTerrainScale, terrain.getScale(), leftCol, rightCol));
			terrain.setScale(nk_slider(ctx, 0.2, terrain.getScale(), 10000.0, 0.1));
			nk_spacer(ctx, spacer1, 1);
			
			terrain.setDepthVisibility(nk_label_edit(ctx, stack, " Terrain Depth Visibility:", bufTerrainDepthVisibility, terrain.getDepthVisibility(), leftCol, rightCol));
			terrain.setDepthVisibility(nk_slider(ctx, 0.0, terrain.getDepthVisibility(), 1.0, 0.01));
			nk_spacer(ctx, spacer1, 1);

			terrain.getMaterial().setShininess(nk_label_edit(ctx, stack, " Shininess:", bufTerrainShininess, terrain.getMaterial().getShininess(), leftCol, rightCol));
			terrain.getMaterial().setShininess(nk_slider(ctx, 0, terrain.getMaterial().getShininess(), 1.0, 0.01));
			nk_spacer(ctx, spacer1, 1);

			Nuklear.nk_layout_row_end(ctx);
		}
	}

}