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
import org.lwjgl.system.MemoryStack;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.gui.model.Theme;
import org.smartblackbox.qfs.opengl.model.DetectorModel;
import org.smartblackbox.qfs.opengl.model.QFSModel;
import org.smartblackbox.qfs.opengl.model.QFSModel.NodeSelectionMode;
import org.smartblackbox.qfs.settings.QFSProject;

public class FrameDetector extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel(); 
	private DetectorModel detectorModel = qfsProject.detectorModel;

	private int width = 400;
	private int height = 400;
	private double leftCol = 0.50;
	private double rightCol = 0.50;
	private StringBuffer bufDetectionVelocity = new StringBuffer();

	public FrameDetector(NuklearModel frames) {
		super(frames);
	}

	@Override
	public String getTitle() {
		return "QFS Detector";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		createLayout(ctx, 0, model.getMenuBarHeight(), width, height);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
			Nuklear.nk_label(ctx, " Set the ignitor node: ", Nuklear.NK_TEXT_LEFT);

			pushTextColor(ctx, Theme.green);
			Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 2);
			if (detectorModel.getIgnitorNode().x != -1 && detectorModel.getIgnitorNode().y != -1 && detectorModel.getIgnitorNode().z != -1) {
				Nuklear.nk_label(ctx, String.format(" Node: [%d, %d, %d]",
						detectorModel.getIgnitorNode().x,
						detectorModel.getIgnitorNode().y,
						detectorModel.getIgnitorNode().z), Nuklear.NK_TEXT_LEFT);
			}
			else
				Nuklear.nk_label(ctx, " Node: [?, ?, ?]", Nuklear.NK_TEXT_LEFT);
			popTextColor(ctx);
			
	        if (nk_button_label(ctx, "Select Ignitor Node")) {
	        	qfsModel.setNodeSelectionMode(NodeSelectionMode.ignitionNode);
				close();
			}
	        
			Nuklear.nk_label(ctx, " Set the Detector node: ", Nuklear.NK_TEXT_LEFT);

			pushTextColor(ctx, Theme.yellow);
			Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 2);
			if (detectorModel.getDetectorNode().x != -1 && detectorModel.getDetectorNode().y != -1 && detectorModel.getDetectorNode().z != -1) {
				Nuklear.nk_label(ctx, String.format(" Node: [%d, %d, %d]",
						detectorModel.getDetectorNode().x,
						detectorModel.getDetectorNode().y,
						detectorModel.getDetectorNode().z), Nuklear.NK_TEXT_LEFT);
			}
			else
				Nuklear.nk_label(ctx, " Node: [?, ?, ?]", Nuklear.NK_TEXT_LEFT);
			popTextColor(ctx);
			
	        if (nk_button_label(ctx, "Select Detector Node")) {
	        	qfsModel.setNodeSelectionMode(NodeSelectionMode.detectorNode);
				close();
			}
	        
			detectorModel.setDetectionVelocity(nk_label_edit(ctx, stack, 
					" Detection Velocity:", 
					bufDetectionVelocity,
					detectorModel.getDetectionVelocity(),
					appSettings.getFormatScientific8(),
					leftCol, rightCol));
			nk_spacer(ctx, spacer1, 1);
			
	        nk_label_value(ctx, " Detection Velocity: ", "" + detectorModel.getNodeVelocity(), leftCol, rightCol);
	        nk_label_value(ctx, " Init Frame:", "" + detectorModel.getInitFrame(), leftCol, rightCol);
	        nk_label_value(ctx, " First Detection Frame:", "" + detectorModel.getFirstDetectionFrame(), leftCol, rightCol);
	        nk_label_value(ctx, " First Detection Frames:", "" + detectorModel.getFirstDetectionFrames(), leftCol, rightCol);
	        nk_label_value(ctx, " Second Detection Frame:", "" + detectorModel.getSecondDetectionFrame(), leftCol, rightCol);
	        nk_label_value(ctx, " Second Detection Frames:", "" + detectorModel.getSecondDetectionFrames(), leftCol, rightCol);

			nk_spacer(ctx, spacer1, 1);
			Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 5);
			Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_LEFT);
	        if (nk_button_label(ctx, "Reset")) {
	        	detectorModel.reset();
	        	qfsProject.reset();
			}
	        if (nk_button_label(ctx, "Ignite")) {
	        	detectorModel.ignite();
			}
	        if (nk_button_label(ctx, "Close")) {
	        	close();
			}

		}
	}
}