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

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.gui.model.DialogFileModel;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.gui.model.NuklearModel.Frame;
import org.smartblackbox.qfs.opengl.model.QFSModel;
import org.smartblackbox.qfs.opengl.model.QFSModel.EditMode;
import org.smartblackbox.qfs.settings.QFSProject;

public class FrameMenu extends AbstractFrame {

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel(); 

	private int menuWidth = 150;
	private DialogFileModel dialogModel;

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

		createLayout(ctx, 0, 0, getWindowWidth(), model.getMenuBarHeight());
	}

	@Override
	public void layout(NkContext ctx, int x, int y, int width, int height) {
		Nuklear.nk_layout_row_static(ctx, menuItemHeight, model.getMenuItemWidth(), 5);
		
		model.setMenuActive(false);
		//model.setMenuIndex(-1);
		if (Nuklear.nk_menu_begin_label(ctx, "File", Nuklear.NK_TEXT_LEFT, NkVec2.create().set(menuWidth, 300))) {
			model.setMenuActive(true);
			model.setMenuIndex(0);
			Nuklear.nk_layout_row_dynamic(ctx, menuItemHeight, 1);
			if (nk_menu_item_label(ctx, "Open Project")) {
				dialogModel = model.showOpenFileDialog("Open Project File");
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "Save Project As ...")) {
				dialogModel = model.showSaveFileDialog("Save Project File As");
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Save Project")) {
				String fileName = qfsProject.getCurrentFilename();
				if (fileName.isEmpty())
					dialogModel = model.showSaveFileDialog("Save Project File As");
				else
					qfsProject.saveToFile();
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Quit")) {
	            GLFW.glfwSetWindowShouldClose(windowHandle, true);
			}

			Nuklear.nk_menu_end(ctx);
		}
		
		if (dialogModel != null) {
			String path = appSettings.getProjectFilePath() + Constants.SEPARATOR;
			
			switch(dialogModel.getConfirmState()) {
			case open:
				qfsProject.loadFromFile(path + dialogModel.getSelectedFileName());
				dialogModel = null;
				break;
			case save:
				qfsProject.saveToFile(path + dialogModel.getSelectedFileName());
				dialogModel = null;
				break;
			case cancel:
				break;
			default:
				break;
			}
		}
		
		if (Nuklear.nk_menu_begin_label(ctx, "Edit", Nuklear.NK_TEXT_LEFT, NkVec2.create().set(menuWidth, 300))) {
			model.setMenuActive(true);
			model.setMenuIndex(1);
			Nuklear.nk_layout_row_dynamic(ctx, menuItemHeight, 1);
			if (nk_menu_item_label(ctx, "Dimension")) {
				model.toggleFrame(Frame.qfsDimension);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Lights")) {
				model.toggleFrame(Frame.lights);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Camera Settings")) {
				model.toggleFrame(Frame.cameraSettings);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "QFS Colors")) {
				model.toggleFrame(Frame.qfsColors);
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "QFS Settings")) {
				model.toggleFrame(Frame.qfsSettings);
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "QFS Terrain Settings")) {
				model.toggleFrame(Frame.qfsTerrainSettings);
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "Oscillators")) {
				qfsModel.setEditMode(EditMode.none);
				model.toggleFrame(Frame.oscillator);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "slit wall")) {
				model.toggleFrame(Frame.slitWall);
				qfsProject.slitWall.setChanged(true);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Edit Wall")) {
				model.toggleFrame(Frame.editWall);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Rain Simulator")) {
				model.toggleFrame(Frame.rain);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Detector")) {
				model.toggleFrame(Frame.detector);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Preferences")) {
				model.toggleFrame(Frame.preferences);
				Nuklear.nk_menu_close(ctx);
			}
			
			Nuklear.nk_menu_end(ctx);
		}
		
		if (Nuklear.nk_menu_begin_label(ctx, "Run", Nuklear.NK_TEXT_LEFT, NkVec2.create().set(menuWidth, 300))) {
			model.setMenuActive(true);
			model.setMenuIndex(2);
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
			model.setMenuActive(true);
			model.setMenuIndex(3);
			Nuklear.nk_layout_row_dynamic(ctx, menuItemHeight, 1);
			if (nk_menu_item_label(ctx, "Statistic")) {
				model.toggleFrame(Frame.statistic);
				Nuklear.nk_menu_close(ctx);
			}

			if (nk_menu_item_label(ctx, "Properties")) {
				model.toggleFrame(Frame.windowProperty);
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "Keyboard Shortcuts")) {
				model.toggleFrame(Frame.keyboardShortcuts);
				Nuklear.nk_menu_close(ctx);
			}
			
			if (nk_menu_item_label(ctx, "About")) {
				model.toggleFrame(Frame.about);
				Nuklear.nk_menu_close(ctx);
			}
			
			Nuklear.nk_menu_end(ctx);
		}

	}

}