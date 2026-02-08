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

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.gui.model.NuklearModel.Frame;
import org.smartblackbox.qfs.opengl.model.QFSModel;
import org.smartblackbox.qfs.opengl.model.QFSModel.EditMode;
import org.smartblackbox.qfs.settings.QFSProject;

public class FrameMenu extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel(); 

	private int menuWidth = 150;

	public FrameMenu(NuklearModel model) {
		super(model);
		windowOptions = Nuklear.NK_WINDOW_NO_SCROLLBAR;
	}

	@Override
	public String getTitle() {
		return "Menu";
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		super.render(windowHandle, ctx);

		createLayout(ctx, 0, 0, getWindowWidth(), nuklearModel.getMenuBarHeight() + 4);
	}

	@Override
	public void layout(NkContext ctx, int x, int y, int width, int height) {
		Nuklear.nk_layout_row_static(ctx, nuklearModel.getMenuBarHeight() - 4, nuklearModel.getMenuItemWidth(), 5);

		boolean isSubMenuVisible = false;

		if (Nuklear.nk_menu_begin_label(ctx, "File", Nuklear.NK_TEXT_LEFT, NkVec2.create().set(menuWidth, 300))) {
			isSubMenuVisible = true;
			Nuklear.nk_layout_row_dynamic(ctx, menuItemHeight, 1);
			if (nk_menu_item_label(ctx, "Open Project")) {
				qfsProject.loadFromFile();
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "Save Project As ...")) {
				qfsProject.saveAs();
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Save Project")) {
				qfsProject.saveToFile();
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Quit")) {
	            GLFW.glfwSetWindowShouldClose(windowHandle, true);
			}

			Nuklear.nk_menu_end(ctx);
		}

		qfsProject.performFileDialog(appSettings.getProjectFilePath());

		if (Nuklear.nk_menu_begin_label(ctx, "Edit", Nuklear.NK_TEXT_LEFT, NkVec2.create().set(menuWidth, 300))) {
			isSubMenuVisible = true;
			Nuklear.nk_layout_row_dynamic(ctx, menuItemHeight, 1);
			if (nk_menu_item_label(ctx, "Dimension")) {
				nuklearModel.toggleFrame(Frame.qfsDimension);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Lights")) {
				nuklearModel.toggleFrame(Frame.lights);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Camera Settings")) {
				nuklearModel.toggleFrame(Frame.cameraSettings);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "QFS Colors")) {
				nuklearModel.toggleFrame(Frame.qfsColors);
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "Oscillators")) {
				qfsModel.setEditMode(EditMode.none);
				nuklearModel.toggleFrame(Frame.oscillator);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "slit wall")) {
				nuklearModel.toggleFrame(Frame.slitWall);
				qfsProject.slitWall.setChanged(true);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Edit Wall")) {
				nuklearModel.toggleFrame(Frame.editWall);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Rain Simulator")) {
				nuklearModel.toggleFrame(Frame.rain);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Detector")) {
				nuklearModel.toggleFrame(Frame.detector);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "QFS Terrain Settings")) {
				nuklearModel.toggleFrame(Frame.qfsTerrainSettings);
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "QFS Settings")) {
				nuklearModel.toggleFrame(Frame.qfsSettings);
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "Preferences")) {
				nuklearModel.toggleFrame(Frame.preferences);
				Nuklear.nk_menu_close(ctx);
			}
			
			Nuklear.nk_menu_end(ctx);
		}
		
		if (Nuklear.nk_menu_begin_label(ctx, "Run", Nuklear.NK_TEXT_LEFT, NkVec2.create().set(menuWidth, 300))) {
			isSubMenuVisible = true;
			Nuklear.nk_layout_row_dynamic(ctx, menuItemHeight, 1);
			if (nk_menu_item_label(ctx, qfsModel.getBtnSimulatingLabel())) {
				qfsModel.toggleSimulation();
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "Reset")) {
				qfsProject.reset();
				Nuklear.nk_menu_close(ctx);
			}
			
			Nuklear.nk_menu_end(ctx);
		}
		
		if (Nuklear.nk_menu_begin_label(ctx, "Help", Nuklear.NK_TEXT_LEFT, NkVec2.create().set(menuWidth, 300))) {
			isSubMenuVisible = true;
			Nuklear.nk_layout_row_dynamic(ctx, menuItemHeight, 1);
			if (nk_menu_item_label(ctx, "Performance")) {
				nuklearModel.toggleFrame(Frame.performance);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Properties")) {
				nuklearModel.toggleFrame(Frame.windowProperty);
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "Keyboard Shortcuts")) {
				nuklearModel.toggleFrame(Frame.keyboardShortcuts);
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "About")) {
				nuklearModel.toggleFrame(Frame.about);
				Nuklear.nk_menu_close(ctx);
			}
			
			Nuklear.nk_menu_end(ctx);
		}

		nuklearModel.setSubMenuVisible(isSubMenuVisible);

	}

}