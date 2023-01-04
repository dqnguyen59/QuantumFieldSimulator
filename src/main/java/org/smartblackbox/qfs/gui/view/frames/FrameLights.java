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

import org.joml.Vector3d;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.gui.model.GroupModel;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.opengl.model.Material;
import org.smartblackbox.qfs.opengl.model.QFSModel;
import org.smartblackbox.qfs.opengl.model.lights.Light;
import org.smartblackbox.qfs.settings.QFSProject;

public class FrameLights extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel(); 

	private int groupNoScroll = Nuklear.NK_WINDOW_TITLE | Nuklear.NK_WINDOW_NO_SCROLLBAR | Nuklear.NK_WINDOW_BORDER;
	private int autoHideScroll = Nuklear.NK_WINDOW_SCROLL_AUTO_HIDE;
	private int windowAutoHideScroll = Nuklear.NK_WINDOW_TITLE | Nuklear.NK_WINDOW_BORDER | Nuklear.NK_WINDOW_SCROLL_AUTO_HIDE;
	
	private int width = 640;
	private int height = 640;
	private double leftCol = 0.5;
	private double rightCol = 0.5;
	//private float contentWidth;
	private float contentHeight;
	private float controlButtonsHeight = 76;

	private NkVec2 contentSize;

	private static int indexLight;

	GroupModel groupModelProperties = new GroupModel("Properties");
	GroupModel groupModelConeShape = new GroupModel("Cone Shape");

	private StringBuffer bufName = new StringBuffer();
	private StringBuffer bufIntensity = new StringBuffer();
	private StringBuffer bufCutOff = new StringBuffer();
	private StringBuffer bufDirectionX = new StringBuffer();
	private StringBuffer bufDirectionY = new StringBuffer();
	private StringBuffer bufDirectionZ = new StringBuffer();
	private StringBuffer bufConstant = new StringBuffer();
	private StringBuffer bufLinear = new StringBuffer();
	private StringBuffer bufExponent = new StringBuffer();

	private StringBuffer bufAmbientIntensity = new StringBuffer();
	private StringBuffer bufDiffuseIntensity = new StringBuffer();
	private StringBuffer bufSpecularIntensity = new StringBuffer();
	private StringBuffer bufScale = new StringBuffer();

	public FrameLights(NuklearModel frames) {
		super(frames);
	}

	@Override
	public String getTitle() {
		return "Light";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		createLayout(ctx, 0, getWindowHeight() - (Constants.STATUS_BAR_HEIGHT + height), width, height);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			NkVec2 _result = NkVec2.create();
			contentSize = Nuklear.nk_window_get_content_region_size(ctx, _result);
			//contentWidth = contentSize.x() - 8;
			contentHeight = contentSize.y() - 8;

			Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, contentSize.y(), 2);

			Nuklear.nk_layout_row_push(ctx, 0.5f);
			if (Nuklear.nk_group_begin(ctx, "Group List", Nuklear.NK_WINDOW_NO_SCROLLBAR)) {
				Nuklear.nk_layout_row_dynamic(ctx, contentHeight, 1);
				layoutLeftSide(ctx, stack);
				Nuklear.nk_group_end(ctx);
			}

			float f = qfsModel.getSelectedLight() != null && qfsModel.getSelectedLight().isSpotLight()? 0.5f : 1f;
			Nuklear.nk_layout_row_push(ctx, f);
			if (qfsModel.getSelectedLight() != null && Nuklear.nk_group_begin(ctx, "Group Properties", Nuklear.NK_WINDOW_NO_SCROLLBAR)) {
				Nuklear.nk_layout_row_dynamic(ctx, contentHeight * f - 2, 1);
				layoutRightSide(ctx, stack);
				Nuklear.nk_group_end(ctx);
			}

			Nuklear.nk_layout_row_end(ctx);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// LEFT LAYOUT //////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void layoutLeftSide(NkContext ctx, MemoryStack stack) {
		if (Nuklear.nk_group_begin(ctx, "Lights", groupNoScroll)) {

			Nuklear.nk_layout_row_dynamic(ctx, contentHeight - controlButtonsHeight, 1);

			if (Nuklear.nk_group_begin(ctx, "Light List View", autoHideScroll)) {

				Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
				for (Light light : qfsProject.getLights()) {
					boolean selected = qfsModel.getSelectedLight() == light;
					selected = Nuklear.nk_select_label(ctx, light.getName(), Nuklear.NK_TEXT_ALIGN_LEFT, selected);
					if (selected)
						qfsModel.setSelectedLight(light);
				}
				Nuklear.nk_group_end(ctx);
			}

			Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 3);
			if (nk_button_label(ctx, "Add")) {
				qfsModel.setSelectedLight(qfsProject.createLight());
				qfsModel.getSelectedLight().setName("Light" + indexLight);
				indexLight++;
			}

			if (nk_button_label(ctx, "Remove")) {
				int i = qfsProject.getLights().indexOf(qfsModel.getSelectedLight());
				qfsProject.getLights().remove(qfsModel.getSelectedLight());

				if (i >= qfsProject.getLights().size())
					i = qfsProject.getLights().size() - 1;
				
				qfsModel.setSelectedLight(qfsProject.getLights().size() > 0? qfsProject.getLights().get(i) : null);
			}

			if (nk_button_label(ctx, "Close"))
				close();

			Nuklear.nk_group_end(ctx);
		}

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// LEFT LAYOUT //////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// RIGHT LAYOUT /////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void layoutRightSide(NkContext ctx, MemoryStack stack) {
		Light light = qfsModel.getSelectedLight();

		if (nk_group_begin(ctx, stack, groupModelProperties, windowAutoHideScroll)) {
			
			light.setName(nk_label_edit(ctx, stack, " Name: ", bufName  , light.getName(), leftCol, rightCol));
			
			light.setColor(nk_combo_color_picker(ctx, stack, " Color:", light.getColor(), leftCol, rightCol));

			light.setIntensity(nk_label_edit(ctx, stack, " Intensity:", bufIntensity, light.getIntensity(), leftCol, rightCol));
			light.setIntensity(nk_slider(ctx, 0, light.getIntensity(), 10.0, 0.01));
			
			light.setConstant(nk_label_edit(ctx, stack, " Constant:", bufConstant, light.getConstant(), appSettings.getFormatScientific12(), leftCol, rightCol));
			light.setConstant(nk_slider(ctx, 0, light.getConstant(), 2.0f, 0.0001));
			
			light.setLinear(nk_label_edit(ctx, stack, " Linear:", bufLinear, light.getLinear(), appSettings.getFormatScientific12(), leftCol, rightCol));
			light.setLinear(nk_slider(ctx, 0, light.getLinear(), 0.1f, 0.0001));
			
			light.setExponent(nk_label_edit(ctx, stack, " Exponent:", bufExponent, light.getExponent(), appSettings.getFormatScientific12(), leftCol, rightCol));
			light.setExponent(nk_slider(ctx, 0, light.getExponent(), 0.0025, 0.000001));

			nk_spacer(ctx, spacer2, 1);

			light.setSpotLight(nk_check_label(ctx, "Spotlight", light.isSpotLight(), leftCol, rightCol));

			if (light.isSpotLight()) {
				light.setCutOff(nk_label_edit(ctx, stack, " CutOff:", bufCutOff, light.getCutOff(), leftCol, rightCol));
				light.setCutOff(nk_slider(ctx, 0f, light.getCutOff(), 180f, 0.01f));
				nk_spacer(ctx, spacer1, 1);

				Vector3d r = light.getDirection();
				r.x = nk_label_edit(ctx, stack, " Direction.X:", bufDirectionX, r.x, leftCol, rightCol);
				r.x = nk_slider(ctx, -1f, r.x, 1f, 0.01f);
				nk_spacer(ctx, spacer1, 1);
				r.y = nk_label_edit(ctx, stack, " Direction.Y:", bufDirectionY, r.y, leftCol, rightCol);
				r.y = nk_slider(ctx, -1f, r.y, 1f, 0.01f);
				nk_spacer(ctx, spacer1, 1);
				r.z = nk_label_edit(ctx, stack, " Direction.Z:", bufDirectionZ, r.z, leftCol, rightCol);
				r.z = nk_slider(ctx, -1f, r.z, 1f, 0.01f);
			}

			nk_group_end(ctx);
		}
		
		if (light.isSpotLight() && nk_group_begin(ctx, stack, groupModelConeShape, windowAutoHideScroll)) {
			Material m = light.getModelSpotLight().getMaterial();

			m.setAmbientColor(nk_combo_color_picker(ctx, stack, " Ambient Color:", m.getAmbientColor(), leftCol, rightCol));
			m.setAmbientIntensity(nk_label_edit(ctx, stack, " Ambient Intensity:", bufAmbientIntensity, m.getAmbientIntensity(), leftCol, rightCol));
			m.setAmbientIntensity(nk_slider(ctx, 0, m.getAmbientIntensity(), 10.0, 0.01));
			nk_spacer(ctx, spacer1, 1);

			m.setDiffuseColor(nk_combo_color_picker(ctx, stack, " Diffuse Color:", m.getDiffuseColor(), leftCol, rightCol));
			m.setDiffuseIntensity(nk_label_edit(ctx, stack, " Diffuse Intensity:", bufDiffuseIntensity, m.getDiffuseIntensity(), leftCol, rightCol));
			m.setDiffuseIntensity(nk_slider(ctx, 0, m.getDiffuseIntensity(), 10.0, 0.01));
			nk_spacer(ctx, spacer1, 1);

			m.setSpecularColor(nk_combo_color_picker(ctx, stack, " Specular Color:", m.getSpecularColor(), leftCol, rightCol));
			m.setSpecularIntensity(nk_label_edit(ctx, stack, " Specular Intensity:", bufSpecularIntensity, m.getSpecularIntensity(), leftCol, rightCol));
			m.setSpecularIntensity(nk_slider(ctx, 0, m.getSpecularIntensity(), 10.0, 0.01));
			nk_spacer(ctx, spacer1, 1);
			
			light.setScale(nk_label_edit(ctx, stack, " Scale:", bufScale, light.getScale(), leftCol, rightCol));
			light.setScale(nk_slider(ctx, 0, light.getScale(), 5.0, 0.01));
			nk_spacer(ctx, spacer1, 1);
			
			m.setShininess(nk_label_edit(ctx, stack, " Shininess:", bufScale, m.getShininess(), leftCol, rightCol));
			m.setShininess(nk_slider(ctx, 0, m.getShininess(), 1.0, 0.01));
			
			nk_group_end(ctx);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// RIGHT LAYOUT /////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}