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
package org.smartblackbox.qfs.gui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkUserFont;
import org.lwjgl.nuklear.Nuklear;
import org.smartblackbox.qfs.gui.model.AbstractDialogModel.ConfirmState;
import org.smartblackbox.qfs.gui.view.frames.AbstractFrame;
import org.smartblackbox.qfs.gui.view.frames.FrameAbout;
import org.smartblackbox.qfs.gui.view.frames.FrameCameraSettings;
import org.smartblackbox.qfs.gui.view.frames.FrameDetector;
import org.smartblackbox.qfs.gui.view.frames.FrameEditWall;
import org.smartblackbox.qfs.gui.view.frames.FrameKeyboardShortcuts;
import org.smartblackbox.qfs.gui.view.frames.FrameLights;
import org.smartblackbox.qfs.gui.view.frames.FrameMenu;
import org.smartblackbox.qfs.gui.view.frames.FrameOscillator;
import org.smartblackbox.qfs.gui.view.frames.FrameProgress;
import org.smartblackbox.qfs.gui.view.frames.FrameProperties;
import org.smartblackbox.qfs.gui.view.frames.FrameQFSColors;
import org.smartblackbox.qfs.gui.view.frames.FrameQFSDimension;
import org.smartblackbox.qfs.gui.view.frames.FrameQFSSettings;
import org.smartblackbox.qfs.gui.view.frames.FrameQFSTerrainSettings;
import org.smartblackbox.qfs.gui.view.frames.FrameRain;
import org.smartblackbox.qfs.gui.view.frames.FrameSlitWall;
import org.smartblackbox.qfs.gui.view.frames.FrameStatistics;
import org.smartblackbox.qfs.gui.view.frames.FrameStatusBar;
import org.smartblackbox.qfs.gui.view.frames.IFrame;
import org.smartblackbox.qfs.gui.view.frames.dialogs.FrameConfirmDialog;
import org.smartblackbox.qfs.gui.view.frames.dialogs.FrameFileOpenDialog;
import org.smartblackbox.qfs.gui.view.frames.dialogs.FrameFileSaveDialog;

public class NuklearModel {
	
	public enum Frame {
		menu,
		statusBar,
		statistic,
		windowProperty,
		keyboardShortcuts,
		about,
		progress,
		openFileDialog,
		saveFileDialog,
		confirmDialog,
		qfsDimension,
		lights,
		qfsColors,
		cameraSettings,
		qfsSettings,
		qfsTerrainSettings,
		oscillator,
		slitWall,
		editWall,
		rain,
		detector,
	}
	
	private Map<Frame, AbstractFrame> framesMap = new HashMap<Frame, AbstractFrame>();
	private List<AbstractFrame> visibleFrames = new ArrayList<AbstractFrame>();
	private List<AbstractFrame> frames2Add = new ArrayList<AbstractFrame>();
	private List<AbstractFrame> frames2Remove = new ArrayList<AbstractFrame>();
	private DialogModelStack dialogModelStack = new DialogModelStack();
	
	private double mouseX;
	private double mouseY;
	private boolean nkButtonDown;
	private int nkButton;
	private boolean isDoubleClicked;

	public int dialogActiveCount = 0;
	
	private AbstractFrame focusedFrame;
	private boolean menuActive;
	private int menuIndex;
	private int menuBarHeight = 28;
	private int menuItemWidth = 40;
	private NkUserFont defaultFont = NkUserFont.create();
	private NkUserFont defaultFontBold = NkUserFont.create();
	private NkUserFont defaultFontItalic = NkUserFont.create();
	
	public double getMouseX() {
		return mouseX;
	}

	public void setMouseX(double mouseX) {
		this.mouseX = mouseX;
	}

	public double getMouseY() {
		return mouseY;
	}

	public void setMouseY(double mouseY) {
		this.mouseY = mouseY;
	}

	public boolean isNkButtonDown() {
		return nkButtonDown;
	}

	public void setNkButtonDown(boolean nkButtonDown) {
		this.nkButtonDown = nkButtonDown;
	}

	public int getNkButton() {
		return nkButton;
	}

	public void setNkButton(int nkButton) {
		this.nkButton = nkButton;
	}

	public boolean isDoubleClicked() {
		if (isDoubleClicked) {
			isDoubleClicked = false;
			return true;
		}
		return false;
	}

	public void setDoubleClicked(boolean isDoubleClicked) {
		this.isDoubleClicked = isDoubleClicked;
	}

	public AbstractFrame getFocusedFrame() {
		return focusedFrame;
	}

	public void setFocusedFrame(AbstractFrame focusedFrame) {
		if (focusedFrame.getClass() == FrameMenu.class
			|| focusedFrame.getClass() == FrameStatusBar.class
			|| focusedFrame.getClass() == FrameStatistics.class
			|| focusedFrame.getClass() == FrameProperties.class
			|| focusedFrame.getClass() == FrameProgress.class
		)
			focusedFrame = null;
		else
			this.focusedFrame = focusedFrame;
	}

	public boolean isAnyFrameFocused() {
		return focusedFrame != null;
	}
	
	public void unfocusAllFrames() {
		focusedFrame = null;
	}
	
	public void addToVisibleFrames(AbstractFrame frame) {
		setFocusedFrame(frame);
		visibleFrames.add(frame);
	}

	public void removeFromVisibleFrames(AbstractFrame frame) {
		visibleFrames.remove(frame);
	}

	public void addFrame2Add(AbstractFrame frame) {
		frames2Add.add(frame);
	}

	public void addFrame2Remove(AbstractFrame frame) {
		frames2Remove.add(frame);
	}

	public DialogModelStack getDialogStack() {
		return dialogModelStack;
	}

	public void setDialogStack(DialogModelStack dialogMOdelStack) {
		this.dialogModelStack = dialogMOdelStack;
	}

	public boolean isAnyDialogActive() {
		return dialogActiveCount > 0;
	}

	public Map<Frame, AbstractFrame> getFrames() {
		return framesMap;
	}

	public AbstractFrame createInstance(Frame frame) {
		switch (frame) {
		case menu:
			return new FrameMenu(this);
		case statusBar:
			return new FrameStatusBar(this);
		case statistic:
			return new FrameStatistics(this);
		case windowProperty:
			return new FrameProperties(this);
		case keyboardShortcuts:
			return new FrameKeyboardShortcuts(this);
		case about:
			return new FrameAbout(this);
		case progress:
			return new FrameProgress(this);
		case confirmDialog:
			return new FrameConfirmDialog(this);
		case qfsDimension:
			return new FrameQFSDimension(this);
		case qfsColors:
			return new FrameQFSColors(this);
		case lights:
			return new FrameLights(this);
		case cameraSettings:
			return new FrameCameraSettings(this);
		case qfsSettings:
			return new FrameQFSSettings(this);
		case qfsTerrainSettings:
			return new FrameQFSTerrainSettings(this);
		case oscillator:
			return new FrameOscillator(this);
		case slitWall:
			return new FrameSlitWall(this);
		case editWall:
			return new FrameEditWall(this);
		case rain:
			return new FrameRain(this);
		case detector:
			return new FrameDetector(this);
		case openFileDialog:
			return new FrameFileOpenDialog(this);
		case saveFileDialog:
			return new FrameFileSaveDialog(this);
		default:
			break;
		
		}
		return null;
	}

	public void render(long windowHandle, NkContext context) {
		if (frames2Add.size() > 0) {
			while (frames2Add.size() > 0) {
				addToVisibleFrames(frames2Add.get(0));
				frames2Add.remove(0);
			}
		}
		else {
			while (frames2Remove.size() > 0) {
				removeFromVisibleFrames(frames2Remove.get(0));
				frames2Remove.remove(0);
			}
		}

		// Only render visible frames.
		for (IFrame frame : visibleFrames) {
			frame.render(windowHandle, context);
		}
	}

	public boolean isFrameVisible(Frame frame) {
		return framesMap.containsKey(frame);
	}

	public AbstractFrame showFrame(Frame frame) {
		if (!isFrameVisible(frame)) {
			AbstractFrame instance = createInstance(frame);
			addFrame2Add(instance);
			framesMap.put(frame, instance);
			
			if (instance.isDialog()) dialogActiveCount++;
			return instance;
		}
		return getFrames().get(frame);
	}
	
	public void closeFrame(Frame frame) {
		AbstractFrame instance = framesMap.get(frame);
		if (instance.isDialog()) dialogActiveCount--;
		addFrame2Remove(instance);
		framesMap.remove(frame);
	}

	public void toggleFrame(Frame frame) {
        if (isFrameVisible(frame))
        	closeFrame(frame);
        else
            showFrame(frame);
	}

	public void close(AbstractFrame frame) {
	    for (Entry<Frame, AbstractFrame> entry : framesMap.entrySet()) {
	        if (entry.getValue().equals(frame)) {
	    		new Thread(new Runnable() {
	    			@Override
	    			public void run() {
	    	        	closeFrame(entry.getKey());
	    			}
	    		}).start();
	        }
	    }
	}
	
	public boolean is_mouse_in_rect(NkRect rect) {
		float x1 = rect.x();
		float x2 = rect.x() + rect.w();
		float y1 = rect.y();
		float y2 = rect.y() + rect.h();
		return (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2);
	}

	public void checkFocus(AbstractFrame frame, NkRect rect) {
		if (is_mouse_in_rect(rect)
				&& nkButtonDown
				&& nkButton == Nuklear.NK_BUTTON_LEFT) {
				setFocusedFrame(frame);
			}

	}

	public DialogDefaultModel showWarningDialog(String title, String message) {
		DialogDefaultModel dlgMessage = dialogModelStack.pushDialogModel(DialogDefaultModel.OK, "Warning: " + title, message);
		showFrame(Frame.confirmDialog);
		return dlgMessage;
	}

	public DialogDefaultModel showErrorDialog(String title, String message) {
		DialogDefaultModel dlgMessage = dialogModelStack.pushDialogModel(DialogDefaultModel.OK, "Error: " + title, message);
		showFrame(Frame.confirmDialog);
		return dlgMessage;
	}

	public DialogDefaultModel showConfirmDialog(ConfirmState[] confirmStates, String title, String message) {
		DialogDefaultModel dlgMessage = dialogModelStack.pushDialogModel(confirmStates, title, message);
		showFrame(Frame.confirmDialog);
		return dlgMessage;
	}

	public DialogFileModel showOpenFileDialog(String title) {
		DialogFileModel dlgMessage = dialogModelStack.pushDialogFileModel(DialogFileModel.CANCEL_OPEN, title, "");
		showFrame(Frame.openFileDialog);
		return dlgMessage;
	}

	public DialogFileModel showSaveFileDialog(String title) {
		DialogFileModel dlgMessage = dialogModelStack.pushDialogFileModel(DialogFileModel.CANCEL_SAVE,  title, "");
		showFrame(Frame.saveFileDialog);
		return dlgMessage;
	}

	public boolean isMenuActive() {
		return menuActive;
	}

	public void setMenuActive(boolean menuActive) {
		this.menuActive = menuActive;
	}

	public int getMenuIndex() {
		return menuIndex;
	}

	public void setMenuIndex(int menuIndex) {
		this.menuIndex = menuIndex;
	}

	public int getMenuBarHeight() {
		return menuBarHeight;
	}

	public void setMenuBarHeight(int menuBarHeight) {
		this.menuBarHeight = menuBarHeight;
	}

	public int getMenuItemWidth() {
		return menuItemWidth;
	}

	public void setMenuItemWidth(int menuItemWidth) {
		this.menuItemWidth = menuItemWidth;
	}

	public NkUserFont getDefaultFont() {
		return defaultFont;
	}

	public void setDefaultFont(NkUserFont default_font) {
		this.defaultFont = default_font;
	}

	public NkUserFont getDefaultFontBold() {
		return defaultFontBold;
	}

	public void setDefaultFontBold(NkUserFont default_font_bold) {
		this.defaultFontBold = default_font_bold;
	}

	public NkUserFont getDefaultFontItalic() {
		return defaultFontItalic;
	}

	public void setDefaultFontItalic(NkUserFont defaultFontItalic) {
		this.defaultFontItalic = defaultFontItalic;
	}

}
