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
package org.smartblackbox.qfs.gui.controller;

import java.nio.DoubleBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;
import org.smartblackbox.qfs.IMouseAndKeyboardEvents;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.gui.model.NuklearModel.Frame;
import org.smartblackbox.qfs.opengl.controller.KeyEvents;

public class NuklearController implements IMouseAndKeyboardEvents {

	private NkContext context;

	private NuklearModel nuklearModel;

	private boolean keyPressed;

	public NuklearController(NkContext context, NuklearModel nuklearModel) {
		super();
		this.context = context;
		this.nuklearModel = nuklearModel;
	}

	@Override
	public boolean mouseCursorPosCallback(long handle, double xPos, double yPos) {
		nuklearModel.setMouseX(xPos);
		nuklearModel.setMouseY(yPos);
		Nuklear.nk_input_motion(context, (int)xPos, (int)yPos);
		return Nuklear.nk_window_is_any_hovered(context) || nuklearModel.isAnyDialogActive();
	}

	@Override
	public boolean mouseButtonCallback(long handle, int button, int action, int mods) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer cx = stack.mallocDouble(1);
			DoubleBuffer cy = stack.mallocDouble(1);

			GLFW.glfwGetCursorPos(handle, cx, cy);

			int x = (int)cx.get(0);
			int y = (int)cy.get(0);

			if (action == GLFW.GLFW_REPEAT) {
				nuklearModel.setNkButton(Nuklear.NK_BUTTON_DOUBLE);
				nuklearModel.setNkButtonDown(true);
				nuklearModel.setDoubleClicked(true);
			}
			else {
				switch (button) {
				case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
					nuklearModel.setNkButton(Nuklear.NK_BUTTON_RIGHT);
					break;
				case GLFW.GLFW_MOUSE_BUTTON_MIDDLE:
					nuklearModel.setNkButton(Nuklear.NK_BUTTON_MIDDLE);
					break;
				default:
					nuklearModel.setNkButton(Nuklear.NK_BUTTON_LEFT);
				}
				nuklearModel.setNkButtonDown(action == GLFW.GLFW_PRESS);
			}

			Nuklear.nk_input_button(context, nuklearModel.getNkButton(), x, y, nuklearModel.isNkButtonDown());
		}
		return Nuklear.nk_window_is_any_hovered(context) || nuklearModel.isNkButtonDown() && nuklearModel.isAnyFrameFocused() || nuklearModel.isAnyDialogActive();
	}

	@Override
	public boolean mouseScrollCallback(long handle, double xoffset, double yoffset) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			NkVec2 scroll = NkVec2.malloc(stack)
					.x((float)xoffset)
					.y((float)yoffset);
			Nuklear.nk_input_scroll(context, scroll);
		}
		return Nuklear.nk_window_is_any_hovered(context) || nuklearModel.isAnyDialogActive();
	}

	@Override
	public boolean mouseCursorEnterCallback(long handle, boolean entered) {
		return Nuklear.nk_window_is_any_hovered(context) || nuklearModel.isAnyDialogActive();
	}

	@Override
	public boolean charCallback(long handle, int unicode) {
		Nuklear.nk_input_unicode(context, unicode);
		return nuklearModel.isAnyFrameFocused() || nuklearModel.isAnyDialogActive();
	}

	@Override
	public boolean keyCallback(long handle, int key, int scancode, int action, int mods) {
		keyPressed = action == GLFW.GLFW_PRESS;
		switch (key) {
		case GLFW.GLFW_KEY_DELETE:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_DEL, keyPressed);
			break;
		case GLFW.GLFW_KEY_ENTER:
		case GLFW.GLFW_KEY_KP_ENTER:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_ENTER, keyPressed);
			break;
		case GLFW.GLFW_KEY_TAB:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_TAB, keyPressed);
			//Nuklear.nk_input_key(context, Nuklear.NK_KEY_TEXT_SELECT_ALL, keyPressed);
			break;
		case GLFW.GLFW_KEY_BACKSPACE:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_BACKSPACE, keyPressed);
			break;
		case GLFW.GLFW_KEY_LEFT:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_LEFT, keyPressed);
			break;
		case GLFW.GLFW_KEY_RIGHT:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_RIGHT, keyPressed);
			break;
		case GLFW.GLFW_KEY_UP:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_UP, keyPressed);
			break;
		case GLFW.GLFW_KEY_DOWN:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_DOWN, keyPressed);
			break;
		case GLFW.GLFW_KEY_HOME:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_TEXT_START, keyPressed);
			//nk_input_key(ctx, NK_KEY_SCROLL_START, press);
			break;
		case GLFW.GLFW_KEY_END:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_TEXT_END, keyPressed);
			//nk_input_key(ctx, NK_KEY_SCROLL_END, press);
			break;
		case GLFW.GLFW_KEY_PAGE_DOWN:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_SCROLL_DOWN, keyPressed);
			break;
		case GLFW.GLFW_KEY_PAGE_UP:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_SCROLL_UP, keyPressed);
			break;
		case GLFW.GLFW_KEY_LEFT_SHIFT:
		case GLFW.GLFW_KEY_RIGHT_SHIFT:
			Nuklear.nk_input_key(context, Nuklear.NK_KEY_SHIFT, keyPressed);
			break;
		case GLFW.GLFW_KEY_LEFT_CONTROL:
		case GLFW.GLFW_KEY_RIGHT_CONTROL:
			if (keyPressed) {
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_COPY, GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_PASTE, GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_P) == GLFW.GLFW_PRESS);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_CUT, GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_X) == GLFW.GLFW_PRESS);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_TEXT_UNDO, GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_Z) == GLFW.GLFW_PRESS);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_TEXT_REDO, GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_TEXT_WORD_LEFT, GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_TEXT_WORD_RIGHT, GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_TEXT_LINE_START, GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_B) == GLFW.GLFW_PRESS);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_TEXT_LINE_END, GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_E) == GLFW.GLFW_PRESS);
			} else {
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_LEFT, GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_RIGHT, GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_COPY, false);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_PASTE, false);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_CUT, false);
				Nuklear.nk_input_key(context, Nuklear.NK_KEY_SHIFT, false);
			}
			break;
		}
		return nuklearModel.isAnyDialogActive();
	}

	@Override
	public boolean keyContinuousCallback(long handle, int key, KeyEvents keyEvents) {
		return false;
	}

	public void showFrame(Frame frame) {
		nuklearModel.showFrame(frame);
	}

	public void unfocusAllFrames() {
		nuklearModel.unfocusAllFrames();
	}

}
