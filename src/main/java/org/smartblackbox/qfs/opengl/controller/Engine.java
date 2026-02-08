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
package org.smartblackbox.qfs.opengl.controller;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.opengl.GL11;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.IMouseAndKeyboardEvents;
import org.smartblackbox.qfs.gui.controller.NuklearController;
import org.smartblackbox.qfs.gui.model.AbstractDialogModel;
import org.smartblackbox.qfs.gui.model.MouseButtonEvent;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.gui.model.NuklearModel.Frame;
import org.smartblackbox.qfs.gui.view.NuklearView;
import org.smartblackbox.qfs.opengl.utils.Screenshot;
import org.smartblackbox.qfs.opengl.view.GLWindow;
import org.smartblackbox.qfs.settings.QFSProject;
import org.smartblackbox.utils.PerformanceMonitor;
import org.smartblackbox.utils.PerformanceMonitor.Measurement;

public abstract class Engine {

	private String customTitle;
	
	private boolean isRunning;
	
	private List<IMouseAndKeyboardEvents> eventListeners = new ArrayList<>();
	
	private GLFWErrorCallback errorCallback;

	protected GLWindow glWindow;
	protected NuklearController nuklearController;
	protected NuklearView nuklearView;
	protected NuklearModel nuklearModel;
	
	private KeyEvents keyEvents = new KeyEvents();

	private long tKeyboardInterval;
	private boolean lastButtonDown;
	private List<MouseButtonEvent> mouseButtonEvents = new ArrayList<MouseButtonEvent>();

	private int clickCount;
	private long tDoubleClickInterval;
	//private int menuIndex;
	//private boolean isMenuActive;
	private long handle;
	private int renderCounter;
    private long currentTime;

	public Engine(GLWindow glWindow) {
		this.glWindow = glWindow;
	}

	public String getCustomTitle() {
		return customTitle;
	}

	public void setCustomTitle(String customTitle) {
		this.customTitle = customTitle;
	}
	
	public void init() throws Exception {
		GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		glWindow.init();
		
		nuklearModel = new NuklearModel();
		
	    nuklearView = new NuklearView(nuklearModel);
        nuklearView.init(glWindow.getWindowHandle());

	    nuklearController = new NuklearController(nuklearView.getContext(), nuklearModel);
    	addMouseAndKeyboardListener(nuklearController);
    	
    	nuklearController.showFrame(Frame.menu);
    	nuklearController.showFrame(Frame.statusBar);
		
    	initMouseAndKeyboard(glWindow.getWindowHandle());
    	
    	nuklearController.unfocusAllFrames();

		QFSProject.getInstance().setNuklearModel(nuklearModel);
	}
	
	protected void addMouseAndKeyboardListener(IMouseAndKeyboardEvents listener) {
		eventListeners.add(listener);
	}

	private void checkFrameFocus() {
		if (nuklearModel.isNkButtonDown() && nuklearModel.isNkButtonDown() != lastButtonDown) {
			nuklearModel.unfocusAllFrames();
		}
		lastButtonDown = nuklearModel.isNkButtonDown();
	}

	private void menuMouseMove() {
		if (nuklearModel.getMouseY() >  0 && nuklearModel.getMouseY() < nuklearModel.getMenuBarHeight()) {
			int menuWidth = nuklearModel.getMenuItemWidth() + 5;
			int menuIndex = (int) (nuklearModel.getMouseX() / menuWidth);

			if (menuIndex > 3) menuIndex = 3;

			if (nuklearModel.isSubMenuActive() && menuIndex != nuklearModel.getMenuIndex()) {

				// A fix when sometimes submenu is not visible while subMenu should be active,
				// otherwise simulate button click to hide the current sub menu and show the sub menu at menu index.
				if (nuklearModel.isSubMenuVisible())
					simulateMouseClick();
				simulateMouseClick();
				nuklearModel.setMenuIndex(menuIndex);
			}
		}
	}
	
	private void menuMouseButtonDown(int action) {
		if (nuklearModel.getMouseY() >  0 && nuklearModel.getMouseY() < nuklearModel.getMenuBarHeight()) {
			if (action == GLFW.GLFW_RELEASE) {
				nuklearModel.setSubMenuActive(!nuklearModel.isSubMenuActive());
				simulateMouseClick();
			}
		}
		else
			nuklearModel.setSubMenuActive(false);
	}

	private void menuMouseActions() {
//		if (isMenuActive && handle != 0 && renderCounter % 2 == 0) {
//			if (menuIndex != nuklearModel.getMenuIndex()) {
//				simulateMouseClick();
//				menuIndex = nuklearModel.getMenuIndex();
//			}
//		}
	}

	private void simulateMouseClick() {
		mouseButtonEvents.add(new MouseButtonEvent(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT, GLFW.GLFW_PRESS, 0));
		mouseButtonEvents.add(new MouseButtonEvent(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT, GLFW.GLFW_RELEASE, 0));
	}
	
	private void performMouseActions() {
		if (!mouseButtonEvents.isEmpty()) { // && renderCounter % 2 == 0) {
			// Make sure Nuklear have the time to process the mouse events at low fps.
			// Only process one mouse event each rendering frame; 
			MouseButtonEvent event = mouseButtonEvents.get(0);
			nuklearModel.setDoubleClicked(event.action == GLFW.GLFW_REPEAT);
			for (IMouseAndKeyboardEvents listener : eventListeners) {
				if (listener.mouseButtonCallback(event.handle, event.button, event.action, event.mods))
					break;
			}
			mouseButtonEvents.remove(0);
		}
	}

	private void checkMouseDoubleClick(int button, int mods) {
		long t = System.currentTimeMillis() - tDoubleClickInterval;

		if (t < Constants.DOUBLE_CLICK_INTERVAL) {
			clickCount++;

			if (clickCount == 2) {
				mouseButtonEvents.add(new MouseButtonEvent(handle, button, GLFW.GLFW_REPEAT, mods));
			}
		}
		else 
			clickCount = 0;

		tDoubleClickInterval = System.currentTimeMillis();
	}

	/**
	 * @return true if a dialog is opened and mouse actions is outside the dialog.
	 */
	private boolean isMouseActionLocked() {
		AbstractDialogModel currentDialogModel = nuklearModel.getDialogStack().getCurrent();
		if (currentDialogModel != null) {
			NkRect rect = currentDialogModel.getWindowRect();
			if (rect != null) {
				float x1 = rect.x();
				float x2 = rect.x() + rect.w();
				float y1 = rect.y();
				float y2 = rect.y() + rect.h();
				return !(nuklearModel.getMouseX() > x1 && nuklearModel.getMouseX() < x2
						&& nuklearModel.getMouseY() > y1 && nuklearModel.getMouseY() < y2);
			}
		}
		else
			return false;
		
		return true;
	}

	private void initMouseAndKeyboard(long handle) {
		
		GLFW.glfwSetCursorEnterCallback(handle, (window, entered) -> {
			for (IMouseAndKeyboardEvents listener : eventListeners) {
				if (listener.mouseCursorEnterCallback(handle, entered)) 
					break;
			}
		});
		
		GLFW.glfwSetCursorPosCallback(handle, (window, xPos, yPos) -> {
			this.handle = handle;
			
			for (IMouseAndKeyboardEvents listener : eventListeners) {
				if (listener.mouseCursorPosCallback(handle, xPos, yPos))
					break;
			}
			
			nuklearModel.setMouseX(xPos);
			nuklearModel.setMouseY(yPos);

			menuMouseMove();
        });
        
		GLFW.glfwSetMouseButtonCallback(handle, (window, button, action, mods) -> {
			//menuMouseMove();
			if (!isMouseActionLocked()) {
				menuMouseButtonDown(action);
				
				// Nuklear input does not always process the call backs in time at low fps.
				// This workaround makes sure Nuklear input is processed when a mouse button is clicked.
				// Call listener.mouseButtonCallback before rendering Nuklear.
				mouseButtonEvents.add(new MouseButtonEvent(handle, button, action, mods));

				checkMouseDoubleClick(button, mods);
			}
        });

		GLFW.glfwSetScrollCallback(handle, (window, xOffset, yOffset) -> {
			for (IMouseAndKeyboardEvents listener : eventListeners) {
				if (listener.mouseScrollCallback(handle, xOffset, yOffset))
					break;
			}
        });
        
		GLFW.glfwSetCharCallback(handle, (window, unicode) -> {
			for (IMouseAndKeyboardEvents listener : eventListeners) {
				if (listener.charCallback(handle, unicode))
					break;
			}
        });

		GLFW.glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
			keyEvents.isKeyPressed = action == GLFW.GLFW_PRESS;
			keyEvents.isKeyRepeating = action == GLFW.GLFW_REPEAT;
			keyEvents.isKeyReleased = action == GLFW.GLFW_RELEASE;
			
			for (IMouseAndKeyboardEvents listener : eventListeners) {
				if (listener.keyCallback(handle, key, scancode, action, mods))
					break;
			}
        });
	}
	
	/**
	 * Call this method to read continuously keyboard input and handle the callbacks.
	 */
	private void processKeyboardInput() {
		long handle = glWindow.getWindowHandle();
		
		keyEvents.isShiftLeftPressed = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;
		keyEvents.isShiftRightPressed = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
		keyEvents.isShiftPressed = keyEvents.isShiftLeftPressed || keyEvents.isShiftRightPressed; 
		keyEvents.isCtrlLeftPressed = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS;
		keyEvents.isCtrlRightPressed = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
		keyEvents.isCtrlPressed = keyEvents.isCtrlLeftPressed || keyEvents.isCtrlRightPressed;
		keyEvents.isAltLeftPressed = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS;
		keyEvents.isAltRightPressed = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
		keyEvents.isAltPressed = keyEvents.isAltLeftPressed || keyEvents.isAltRightPressed;
		
		for (int keyCode = 32; keyCode <= GLFW.GLFW_KEY_LAST; keyCode++) {
			int action = GLFW.glfwGetKey(handle, keyCode);
			if (action == GLFW.GLFW_PRESS) {
				for (IMouseAndKeyboardEvents listener : eventListeners) {
					if (listener.keyContinuousCallback(handle, keyCode, keyEvents))
						break;
				}

				if (keyEvents.isKeyRepeating) {
					if (System.currentTimeMillis() - tKeyboardInterval > Constants.KEYBOARD_INTERVAL_REPEAT) {
						for (IMouseAndKeyboardEvents listener : eventListeners) {
							if (listener.keyCallback(handle, keyCode, 0, GLFW.GLFW_PRESS, 0))
								break;
						}
						tKeyboardInterval = System.currentTimeMillis();
					}
				}
			}
		}
	}

	private void processGuiMouseInput() {
		nuklearView.beginInput();
		// Poll events for both OpenGL and NuklearGUI.
		GLFW.glfwPollEvents();
		nuklearView.processInput();
		nuklearView.endInput();
	}
	
	protected void preRender() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LESS);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	protected void render(boolean isAnimated) {
		PerformanceMonitor.start(Measurement.gui2D);

		processKeyboardInput();
		processGuiMouseInput();

		menuMouseActions();

		performMouseActions();

		checkFrameFocus();

		if (!isAnimated && !Screenshot.isTakingScreenShot()) {
			nuklearView.render();
		}

		PerformanceMonitor.stop(Measurement.gui2D);

		glWindow.update();

		renderCounter++;
	}
	
	public void start() throws Exception {
		if (isRunning) return;
		run();
	}

	private void run() {
		isRunning = true;
		currentTime = System.nanoTime();

		while (isRunning) {
			preRender();
			render(false);

			showPerformance();
		}
		cleanUp();
	}
	
	private void stop() {
		if (!isRunning) return;
		isRunning = false;
	}

	protected void cleanUp() {
		nuklearView.cleanUp();
		glWindow.cleanUp();
		errorCallback.free();
		GLFW.glfwTerminate();
	}

	private void showPerformance() {
		long deltaTime;
		if ((deltaTime = System.nanoTime() - currentTime) >= Constants.UPDATE_RATE * Constants.MILLISECOND) {
			if (glWindow.windowShouldClose()) stop();
			PerformanceMonitor.totalFrames += PerformanceMonitor.frames;
			PerformanceMonitor.fps = PerformanceMonitor.frames * Constants.NANOSECOND / deltaTime;
			String custom_title = customTitle;
			if (!custom_title.isEmpty())
				custom_title = "| File: " + custom_title;
			String title = String.format("%s %s | frames: %012d | FPS: %04.1f",
					Constants.TITLE, custom_title,
					PerformanceMonitor.totalFrames,
					PerformanceMonitor.fps);
			glWindow.setTitle(title);
			currentTime = System.nanoTime();
			PerformanceMonitor.frames = 0;
		}

		PerformanceMonitor.frames++;
		PerformanceMonitor.stop(Measurement.totalRenderTime);
		PerformanceMonitor.start(Measurement.totalRenderTime);
	}

}
