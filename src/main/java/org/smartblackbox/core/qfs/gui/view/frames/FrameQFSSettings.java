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
import org.lwjgl.system.MemoryStack;
import org.smartblackbox.core.qfs.Constants;
import org.smartblackbox.core.qfs.gui.model.NuklearModel;
import org.smartblackbox.core.qfs.settings.QFSProject;
import org.smartblackbox.core.qfs.settings.QFSSettings;
import org.smartblackbox.core.qfs.settings.QFSSettings.ColorMode;
import org.smartblackbox.core.qfs.settings.QFSSettings.RenderType;

public class FrameQFSSettings extends AbstractFrame {

	private int width = 300;
	private int height = 726;
	private float leftCol = 0.6f;
	private float rightCol = 0.4f;

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSSettings settings = qfsProject.settings;
	private StringBuffer bufRadiation = new StringBuffer();
	
	private StringBuffer bufScale = new StringBuffer();
	private StringBuffer bufDepthVisibility = new StringBuffer();
	private StringBuffer bufAlphaSlice = new StringBuffer();
	private StringBuffer bufIntensitySlice = new StringBuffer();
	private StringBuffer bufVisibleIndexZ = new StringBuffer();
	private StringBuffer bufAlphaAll = new StringBuffer();
	private StringBuffer bufIntensityAll = new StringBuffer();
	private StringBuffer bufShininess = new StringBuffer();
	private StringBuffer bufConstantFrequency = new StringBuffer();

	public FrameQFSSettings(NuklearModel frames) {
		super(frames);
	}

	@Override
	public String getTitle() {
		return "QFS Settings";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);
		
		createLayout(ctx, 0, model.getMenuBarHeight() + 120, width, height);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			settings.scale = nk_label_edit(ctx, stack, " Scale:", bufScale, settings.scale, leftCol, rightCol);
			settings.scale = nk_slider(ctx, 0.2, settings.scale, 8.0, 0.1);
			nk_spacer(ctx, spacer1, 1);
			
			settings.setDepthVisibility(nk_label_edit(ctx, stack, " Depth Visibility:", bufDepthVisibility, settings.getDepthVisibility(), leftCol, rightCol));
			settings.setDepthVisibility(nk_slider(ctx, 0.0, settings.getDepthVisibility(), 1.0, 0.01));
			nk_spacer(ctx, spacer1, 1);

			settings.setColorMode(ColorMode.valueOf(
					nk_label_combo_options(ctx, stack, " Color Mode:", ColorMode.getValues(), settings.getColorMode().name(), leftCol, rightCol)
			));
			
			if (qfsProject.getDimensionZ() > 0) {
				settings.setRenderType(RenderType.valueOf(
						nk_label_combo_options(ctx, stack, " Render Type:", RenderType.getValues(), settings.getRenderType().name(), leftCol, rightCol)
				));
			}
			
			nk_spacer(ctx, spacer1, 1);
			switch (settings.getRenderType()) {
			case all:
				settings.setAlphaAll(nk_label_edit(ctx, stack, " Alpha Color:", bufAlphaAll, settings.getAlphaAll(), leftCol, rightCol));
				settings.setAlphaAll(nk_slider(ctx, 0.0, settings.getAlphaAll(), 1.0, 0.01));
				nk_spacer(ctx, spacer1, 1);

				settings.setIntensityAll(nk_label_edit(ctx, stack, " Intensity Color:", bufIntensityAll, settings.getIntensityAll(), leftCol, rightCol));
				settings.setIntensityAll(nk_slider(ctx, 0.0, settings.getIntensityAll(), Constants.MAX_INTENSITY, 1.0));
				nk_spacer(ctx, spacer1, 1);

				break;
			case slice:
				settings.setAlphaSlice(nk_label_edit(ctx, stack, " Alpha Color:", bufAlphaSlice, settings.getAlphaSlice(), leftCol, rightCol));
				settings.setAlphaSlice(nk_slider(ctx, 0.0, settings.getAlphaSlice(), 1.0, 0.01));
				nk_spacer(ctx, spacer1, 1);

				settings.setIntensitySlice(nk_label_edit(ctx, stack, " Intensity Color:", bufIntensitySlice, settings.getIntensitySlice(), leftCol, rightCol));
				settings.setIntensitySlice(nk_slider(ctx, 0.0, settings.getIntensitySlice(), Constants.MAX_INTENSITY, 1.0));
				nk_spacer(ctx, spacer1, 1);

				settings.setVisibleIndexZ(nk_label_edit(ctx, stack, " Visible Z:", bufVisibleIndexZ, settings.getVisibleIndexZ(), leftCol, rightCol));
				settings.setVisibleIndexZ((int) nk_slider(ctx, 1, settings.getVisibleIndexZ(), qfsProject.getDimensionZ() - 2, 1.0));
				nk_spacer(ctx, spacer1, 1);

				break;
			default:
				break;
			
			}
			
			settings.setShininess(nk_label_edit(ctx, stack, " Shininess:", bufShininess, settings.getShininess(), leftCol, rightCol));
			settings.setShininess(nk_slider(ctx, 0, settings.getShininess(), 1.0, 0.01));
			nk_spacer(ctx, spacer1, 1);

			qfsProject.setConstantFrequency(nk_label_edit(ctx, stack, " Constant Frequency:", bufConstantFrequency, qfsProject.getConstantFrequency(), leftCol, rightCol));
			qfsProject.setConstantFrequency(nk_slider(ctx, 0, qfsProject.getConstantFrequency(), 1.0, 0.0001));
			nk_spacer(ctx, spacer1, 1);

			qfsProject.setRadiation(nk_label_edit(ctx, stack, " Radiation:", bufRadiation, qfsProject.getRadiation(), appSettings.getFormatScientific8(), leftCol, rightCol));
			nk_spacer(ctx, spacer1, 1);
			
			Nuklear.nk_layout_row_end(ctx);
		}
	}

}