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

import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.opengl.model.Statistics;
import org.smartblackbox.qfs.settings.QFSProject;
import org.smartblackbox.utils.PerformanceMonitor;
import org.smartblackbox.utils.PerformanceMonitor.Measurement;

public class FrameStatistics extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();

	private int width = 250;
	private int height = 420;
	private float leftCol = 0.7f;
	private float rightCol = 0.3f;
	private long savedTime;
	private double mPhysics;
	private double mUpdateMatrix;
	private double mTotalCPURenderTime;
	private double mSendToGPU;
	private double mGUI2D;
	private double mTotalRenderTime;

	public FrameStatistics(NuklearModel frames) {
		super(frames);
		windowOptions = 0;
	}

	@Override
	public String getTitle() {
		return "Statistics";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		NkColor savedColor = NkColor.create().set(ctx.style().window().fixed_background().data().color());
		
		ctx.style().window().fixed_background().data().color(Constants.TRANSPARENT_BG_COLOR);

		createLayout(ctx, getWindowWidth() - width, model.getMenuBarHeight(), width, height);
		
		ctx.style().window().fixed_background().data().color(savedColor);
	}

	@Override
	public void layout(NkContext ctx, int x, int y, int width, int height) {
		
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
		Nuklear.nk_label(ctx, String.format(" #Nodes: %d (%d, %d, %d)",
        		Statistics.numNodes,
        		Statistics.numNodesX,
        		Statistics.numNodesY,
        		Statistics.numNodesZ
        		), Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_label(ctx, String.format(" #Nodes on plane.xy: %d (%d, %d)",
				Statistics.numNodesX * Statistics.numNodesY,
        		Statistics.numNodesX,
        		Statistics.numNodesY
        		), Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());

		Nuklear.nk_layout_row_dynamic(ctx, spacer1 , 1);
		Nuklear.nk_spacer(ctx);

		// Only updating every 250 msecs
		if (System.nanoTime() - savedTime > 250 * 1000000) {
			savedTime = System.nanoTime();
			mPhysics = PerformanceMonitor.getMeasurement(Measurement.physics);
			mUpdateMatrix = PerformanceMonitor.getMeasurement(Measurement.updateMatrix);
			mTotalCPURenderTime = PerformanceMonitor.getMeasurement(Measurement.totalCPURenderTime);
			mSendToGPU = PerformanceMonitor.getMeasurement(Measurement.sendToGPU);
			mGUI2D = PerformanceMonitor.getMeasurement(Measurement.gui2D);
			mTotalRenderTime = PerformanceMonitor.getMeasurement(Measurement.totalRenderTime);
		}
		
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, " CPU:", Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());
		nk_label_value(ctx, "   - Threads:", String.format("%d", qfsProject.numThreads), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, "   - Physics:", String.format("%.2f ms", mPhysics), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, "   - Matrix Update:", String.format("%.2f ms", mUpdateMatrix), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, "   - Total:", String.format("%.2f ms", mTotalCPURenderTime), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		
		Nuklear.nk_layout_row_dynamic(ctx, spacer1, 1);
		Nuklear.nk_spacer(ctx);

		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, " GPU:", Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());
		nk_label_value(ctx, "   - OPEN GL:", String.format("%.2f ms", mSendToGPU), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, "   - GUI 2D:", String.format("%.2f ms", mGUI2D), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		
		Nuklear.nk_layout_row_dynamic(ctx, spacer1, 1);
		Nuklear.nk_spacer(ctx);

		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		nk_label_value(ctx, " Total Render Time:", String.format("%.2f ms", mTotalRenderTime), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " FPS:", String.format("%.2f", PerformanceMonitor.fps), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		nk_label_value(ctx, " Frame:", String.format("%d", PerformanceMonitor.totalFrames), leftCol, rightCol, Nuklear.NK_TEXT_LEFT, Nuklear.NK_TEXT_RIGHT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());

    }

}