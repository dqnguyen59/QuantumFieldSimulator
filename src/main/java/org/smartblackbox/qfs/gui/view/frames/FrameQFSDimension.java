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
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.settings.QFSProject;
import org.smartblackbox.qfs.settings.QFSSettings;

public class FrameQFSDimension extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSSettings settings = qfsProject.settings;

	private StringBuffer dimensionx = new StringBuffer();
	private StringBuffer dimensionY = new StringBuffer();
	private StringBuffer dimensionZ = new StringBuffer();

	private int width = 400;
	private int height = 200;
	private double leftCol = 0.3;
	private double rightCol = 0.7;
	
	private Vector3i dimension = new Vector3i();

	public FrameQFSDimension(NuklearModel frames) {
		super(frames);
		dimension.set(qfsProject.getDimensionX(), qfsProject.getDimensionY(), qfsProject.getDimensionZ());
	}

	@Override
	public String getTitle() {
		return "Quantum Field Simulator";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		createLayoutCentered(ctx, width, height);
	}

	@Override
	protected void layout(NkContext ctx, int x, int y, int width, int height) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
    		dimension.x = nk_label_edit(ctx, stack, " Dimension.X:", dimensionx, dimension.x, leftCol, rightCol);
    		dimension.y = nk_label_edit(ctx, stack, " Dimension.Y:", dimensionY, dimension.y, leftCol, rightCol);
    		dimension.z = nk_label_edit(ctx, stack, " Dimension.Z:", dimensionZ, dimension.z, leftCol, rightCol);
    		
    		nk_label_value(ctx, " #Nodes", "" + (dimension.x * dimension.y * dimension.z), leftCol, rightCol);
    		
    		nk_spacer(ctx, spacer1 , 1);
    		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 3);
    		Nuklear.nk_label(ctx, "", Nuklear.NK_TEXT_CENTERED);
    		if (nk_button_label(ctx, "Update")) {
    			if (dimension.x < 3|| dimension.y < 3 || dimension.z < 1) {
					model.showErrorDialog("Dimension Error", "Invalid dimension range.\n\nChoose a valid dimension: \nx >= 3\ny >= 3\nz >= 1");
    			}
				else
					update();
    		}
    		nk_spacer(ctx, spacer1 , 1);
        }
	}

	private void update() {
		qfsProject.setDimension(dimension);
		settings.setVisibleIndexZ(qfsProject.getDimensionZ() / 2);
        qfsProject.reset();
		close();
	}

}