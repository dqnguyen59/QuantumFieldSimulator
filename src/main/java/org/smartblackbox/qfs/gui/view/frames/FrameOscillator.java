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

import org.joml.Vector3i;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.opengl.model.Oscillator;
import org.smartblackbox.qfs.opengl.model.QFSModel;
import org.smartblackbox.qfs.opengl.model.QFSModel.NodeSelectionMode;
import org.smartblackbox.qfs.opengl.utils.OscillatorType;
import org.smartblackbox.qfs.settings.QFSProject;

public class FrameOscillator extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel(); 

	private int optionsNoScroll = Nuklear.NK_WINDOW_TITLE | Nuklear.NK_WINDOW_NO_SCROLLBAR;
	private int groupNoScroll = optionsNoScroll | Nuklear.NK_WINDOW_BORDER;
	private int autoHideScroll = Nuklear.NK_WINDOW_SCROLL_AUTO_HIDE;

	private int width = 510;
	private int height = 640;
	private double leftCol = 0.55;
	private double rightCol = 0.45;
	private int groupHeight = rowHeightEdit * 7;
	private Oscillator oscillatorEdit;

	private StringBuffer bufStartAngleX = new StringBuffer();
	private StringBuffer bufStartAngleY = new StringBuffer();
	private StringBuffer bufStartAngleZ = new StringBuffer();
	private StringBuffer bufAngleIncX = new StringBuffer();
	private StringBuffer bufAngleIncY = new StringBuffer();
	private StringBuffer bufAngleIncZ = new StringBuffer();
	private StringBuffer bufAmplitudeX = new StringBuffer();
	private StringBuffer bufAmplitudeY = new StringBuffer();
	private StringBuffer bufAmplitudeZ = new StringBuffer();
	private static int index;

	public FrameOscillator(NuklearModel frames) {
		super(frames);
	}

	@Override
	public String getTitle() {
		return "Oscillator";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		createLayout(ctx, 0, getWindowHeight() - (Constants.STATUS_BAR_HEIGHT + height), width, height);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, 590, 2);
			
			Nuklear.nk_layout_row_push(ctx, 0.45f);
			layoutLeftSide(ctx, stack);
			
			Nuklear.nk_layout_row_push(ctx, 0.54999f);
			layoutRightSide(ctx, stack);
			
			Nuklear.nk_layout_row_end(ctx);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// LEFT LAYOUT //////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void layoutLeftSide(NkContext ctx, MemoryStack stack) {
		if (Nuklear.nk_group_begin(ctx, "Oscillator List", groupNoScroll)) {
			
			Nuklear.nk_layout_row_dynamic(ctx, 516, 1);
			if (Nuklear.nk_group_begin(ctx, "Oscillator List View", autoHideScroll)) {

				Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
				for (Oscillator oscillator : qfsProject.oscillators) {
					boolean selected = qfsModel.getSelectedOscillator() == oscillator;
					selected = Nuklear.nk_select_label(ctx, oscillator.getName(), Nuklear.NK_TEXT_ALIGN_LEFT, selected);
					if (selected)
						qfsModel.setSelectedOscillator(oscillator);
				}
				
				Nuklear.nk_group_end(ctx);
			}
			
			Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 3);
			if (nk_button_label(ctx, "Add")) {
				qfsModel.setSelectedOscillator(new Oscillator("Oscillator " + index, new Vector3i(-1, -1, -1)));
				qfsProject.oscillators.add(qfsModel.getSelectedOscillator());
				index++;
			}
			
			if (nk_button_label(ctx, "Remove")) {
				int i = qfsProject.oscillators.indexOf(qfsModel.getSelectedOscillator());
				qfsProject.oscillators.remove(qfsModel.getSelectedOscillator());
				
				if (i >= qfsProject.oscillators.size())
					i = qfsProject.oscillators.size() - 1;
				
				qfsModel.setSelectedOscillator(qfsProject.oscillators.size() > 0? qfsProject.oscillators.get(i) : null);
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
	
	private void layoutOscillatorX(NkContext ctx, MemoryStack stack) {
		Nuklear.nk_layout_row_dynamic(ctx, groupHeight, 1);
		if (Nuklear.nk_group_begin(ctx, "X-Oscillator", optionsNoScroll)) {
			oscillatorEdit.getActive().x = nk_check_label(ctx, "Active", oscillatorEdit.getActive().x, leftCol, rightCol);
			oscillatorEdit.getOscillatorType().x = OscillatorType.valueOf(nk_label_Options(ctx,
					" Type: ",
					OscillatorType.getValues(),
					oscillatorEdit.getOscillatorType().x.name(),
					leftCol, rightCol,
					2
			));
			oscillatorEdit.getStartangle().x = nk_label_edit(ctx, stack, " Start Angle:", bufStartAngleX, oscillatorEdit.getStartangle().x, leftCol, rightCol);
			oscillatorEdit.getAngleIncrement().x = nk_label_edit(ctx, stack, " Angle Increment:", bufAngleIncX, oscillatorEdit.getAngleIncrement().x, leftCol, rightCol);
			oscillatorEdit.getAmplitude().x = nk_label_edit(ctx, stack, " Amplitude:", bufAmplitudeX, oscillatorEdit.getAmplitude().x, leftCol, rightCol);
			Nuklear.nk_group_end(ctx);
		}
		Nuklear.nk_layout_row_dynamic(ctx, spacer2, 1);
	}
	
	private void layoutOscillatorY(NkContext ctx, MemoryStack stack) {
		Nuklear.nk_layout_row_dynamic(ctx, groupHeight, 1);
		if (Nuklear.nk_group_begin(ctx, "Y-Oscillator", optionsNoScroll)) {
			oscillatorEdit.getActive().y = nk_check_label(ctx, "Active", oscillatorEdit.getActive().y, leftCol, rightCol);
			oscillatorEdit.getOscillatorType().y = OscillatorType.valueOf(nk_label_Options(ctx,
					" Type: ",
					OscillatorType.getValues(),
					oscillatorEdit.getOscillatorType().y.name(),
					leftCol, rightCol,
					2
			));
			oscillatorEdit.getStartangle().y = nk_label_edit(ctx, stack, " Start Angle:", bufStartAngleY, oscillatorEdit.getStartangle().y, leftCol, rightCol);
			oscillatorEdit.getAngleIncrement().y = nk_label_edit(ctx, stack, " Angle Increment:", bufAngleIncY, oscillatorEdit.getAngleIncrement().y, leftCol, rightCol);
			oscillatorEdit.getAmplitude().y = nk_label_edit(ctx, stack, " Amplitude:", bufAmplitudeY, oscillatorEdit.getAmplitude().y, leftCol, rightCol);
			Nuklear.nk_group_end(ctx);
		}
		Nuklear.nk_layout_row_dynamic(ctx, spacer2, 1);
	}
	
	private void layoutOscillatorZ(NkContext ctx, MemoryStack stack) {
		Nuklear.nk_layout_row_dynamic(ctx, groupHeight, 1);
		if (Nuklear.nk_group_begin(ctx, "Z-Oscillator", optionsNoScroll)) {
			oscillatorEdit.getActive().z = nk_check_label(ctx, "Active", oscillatorEdit.getActive().z, leftCol, rightCol);
			oscillatorEdit.getOscillatorType().z = OscillatorType.valueOf(nk_label_Options(ctx,
					" Type: ",
					OscillatorType.getValues(),
					oscillatorEdit.getOscillatorType().z.name(),
					leftCol, rightCol,
					2
			));
			oscillatorEdit.getStartangle().z = nk_label_edit(ctx, stack, " Start Angle:", bufStartAngleZ, oscillatorEdit.getStartangle().z, leftCol, rightCol);
			oscillatorEdit.getAngleIncrement().z = nk_label_edit(ctx, stack, " Angle Increment:", bufAngleIncZ, oscillatorEdit.getAngleIncrement().z, leftCol, rightCol);
			oscillatorEdit.getAmplitude().z = nk_label_edit(ctx, stack, " Amplitude:", bufAmplitudeZ, oscillatorEdit.getAmplitude().z, leftCol, rightCol);
			Nuklear.nk_group_end(ctx);
		}
		Nuklear.nk_layout_row_dynamic(ctx, spacer2, 1);
	}
	
	private void layoutRightSide(NkContext ctx, MemoryStack stack) {
		if (qfsModel.getSelectedOscillator() != null
			&&
			Nuklear.nk_group_begin(ctx, "Oscillator", groupNoScroll)) {
			
			Oscillator oscillator = qfsModel.getSelectedOscillator();
			
			Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
			Nuklear.nk_label(ctx, String.format(" Add '%s' on selected node", qfsModel.selectedOscillator.getName()), Nuklear.NK_TEXT_LEFT);

			Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 2);
			if (oscillator.nodeIndex.x != -1 && oscillator.nodeIndex.y != -1 && oscillator.nodeIndex.z != -1) {
				Nuklear.nk_label(ctx, String.format(" Node: [%d, %d, %d]",
						oscillator.nodeIndex.x,
						oscillator.nodeIndex.y,
						oscillator.nodeIndex.z), Nuklear.NK_TEXT_LEFT);
				qfsModel.getCurrentMouseNodeIndex().x = oscillator.nodeIndex.x;
				qfsModel.getCurrentMouseNodeIndex().y = oscillator.nodeIndex.y;
				qfsModel.getCurrentMouseNodeIndex().z = oscillator.nodeIndex.z;
			}
			else
				Nuklear.nk_label(ctx, " Node: [?, ?, ?]", Nuklear.NK_TEXT_LEFT);
			
	        if (nk_button_label(ctx, "Select Node")) {
	        	qfsModel.setNodeSelectionMode(NodeSelectionMode.oscillator);
				close();
			}
	        
	        if (oscillatorEdit == null) {
	        	oscillatorEdit = qfsModel.getSelectedOscillator().clone();
	        }
	        
			Nuklear.nk_layout_row_dynamic(ctx, spacer1, 1);
			if (oscillatorEdit != null) {
				Nuklear.nk_layout_row_dynamic(ctx, 450, 1);
				if(Nuklear.nk_group_begin(ctx, "Scrollable", 0)) {
					layoutOscillatorX(ctx, stack);
					layoutOscillatorY(ctx, stack);
					layoutOscillatorZ(ctx, stack);
					
					Nuklear.nk_group_end(ctx);
				}
				
				Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 3);
				Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_LEFT);
				if (nk_button_label(ctx, "Apply")) {
					if (qfsModel.getSelectedOscillator().nodeIndex.x >= 0) {
						int i = qfsProject.oscillators.indexOf(qfsModel.getSelectedOscillator());
						qfsProject.oscillators.set(i, qfsModel.selectedOscillator = oscillatorEdit.clone());
					}
					else {
						model.showWarningDialog("Node Selection", "You haven't select a node, please select a node first.");
					}
				}
			}
			Nuklear.nk_group_end(ctx);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// RIGHT LAYOUT /////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}