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
package org.smartblackbox.qfs.settings;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import org.lwjgl.nuklear.NkRect;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.utils.AbstractSettings;
import org.smartblackbox.utils.ISettings;
import org.smartblackbox.utils.Utils;

public class AppSettings extends AbstractSettings implements ISettings {

	private static AppSettings instance;
	
	public static int MAX_NUM_THREADS = Runtime.getRuntime().availableProcessors();
	private int numThreads = MAX_NUM_THREADS;
	
	private boolean useFixedNodes = true;
	// Range: 0.0 - 1.0
	private double absorptionFixedNodes = 1.0;
	private boolean useLoop;
	private boolean isChanged;

	private String projectFilePath = Constants.PROJECT_FILE_PATH;

	private String fontFile = Constants.DEFAULT_FONT_FILE;
	private String fontFileBold = Constants.DEFAULT_FONT_FILE_BOLD;
	private String fontFileItalic = Constants.DEFAULT_FONT_FILE_ITALIC;
	private int fontSize = Constants.DEFAULT_FONT_SIZE;
	
	private int windowLeft = -9999;
	private int windowTop = -9999;
	private int windowWidth = 860;
	private int windowHeight = 640;
	private int displayWidth;
	private int displayHeight;
	private int maximized = 0;
	
	private HashMap<String, NkRect> frameRectMap = new HashMap<String, NkRect>();
	
	private char decimalSeperator = '.';
	private DecimalFormat formatInt = Utils.setFormat(0, 0, decimalSeperator, false, false);
	private DecimalFormat formatDefault = Utils.setFormat(2, 2, decimalSeperator, false, false); 
	private DecimalFormat formatScientific2 = Utils.setFormat(2, 2, decimalSeperator, false, false); 
	private DecimalFormat formatScientific4 = Utils.setFormat(4, 4, decimalSeperator, false, false); 
	private DecimalFormat formatScientific8 = Utils.setFormat(8, 8, decimalSeperator, false, false); 
	private DecimalFormat formatScientific12 = Utils.setFormat(12, 12, decimalSeperator, false, false);
	
	private int antiAliasing = 1;
	
	public static AppSettings getInstance() {
		if (instance == null) {
			instance = new AppSettings();
		}
		return instance;
	}

	public AppSettings() {
		super();
	}

	public AppSettings(AppSettings settings) {
		super();
		set(settings);
	}

	public void set(AppSettings settings) {
		setWindowLeft(settings.windowLeft);
		setWindowTop(settings.windowTop);
		setWindowWidth(settings.windowWidth);
		setWindowHeight(settings.windowHeight);
		setMaximized(settings.maximized);
	}
	
	@Override
	public AbstractSettings clone() {
		return new AppSettings(this);
	}

	@Override
	public void loadFromFile(Wini ini, String section, int index) {
		super.loadFromFile(ini, section, index);
		
		numThreads = getInt("System", "numThreads", MAX_NUM_THREADS);
		useFixedNodes = getBool("System", "useFixedNodes", true);
		useLoop = getBool("System", "useLoop", false);
		if (numThreads >= MAX_NUM_THREADS) numThreads = MAX_NUM_THREADS;

		setProjectFilePath(getString("System", "projectFilePath", Constants.PROJECT_FILE_PATH));
		setFontFile(getString("System", "fontFile", Constants.DEFAULT_FONT_FILE));
		setFontFileBold(getString("System", "fontFileBold", Constants.DEFAULT_FONT_FILE_BOLD));
		setFontFileItalic(getString("System", "fontFileItalic", Constants.DEFAULT_FONT_FILE_ITALIC));
		setFontSize(getInt("System", "fontSize", Constants.DEFAULT_FONT_SIZE));
		setWindowLeft(getInt("Window", "left", -9999));
		setWindowTop(getInt("Window", "top", -9999));
		setWindowWidth(getInt("Window", "width", 860));
		setWindowHeight(getInt("Window", "height", 640));
		setMaximized(getInt("Window", "maximized", 0));
		setAntiAliasing(getInt("OpenGL", "antiAliasing", 1));
		
		String sFrame = "Frame";
		Section frameSection = ini.get(sFrame);
		if (frameSection != null) {
	        for (String key: frameSection.keySet()) {
	        	String value = getString(sFrame, key);
	        	if (!value.isEmpty()) {
		        	String[] array = value.split(",");
		        	
		        	NkRect rect = NkRect.create();
		        	
		        	rect.x(Float.parseFloat(array[0]));
		        	rect.y(Float.parseFloat(array[1]));
		        	rect.w(Float.parseFloat(array[2]));
		        	rect.h(Float.parseFloat(array[3]));
		        	frameRectMap.put(key, rect);
	        	}
	        }
		}
	}

	@Override
	public void saveToFile(Wini ini, String section, int index) {
		super.saveToFile(ini, section, index);
		put("System", "numThreads", numThreads);
		put("System", "useFixedNodes", useFixedNodes);
		put("System", "useLoop", useLoop);
		put("System", "projectFilePath", projectFilePath);
		put("System", "fontFile", fontFile);
		put("System", "fontFileBold", fontFileBold);
		put("System", "fontFileItalic", fontFileItalic);
		put("System", "fontSize", fontSize);
		put("Window", "left", windowLeft);
		put("Window", "top", windowTop);
		put("Window", "width", windowWidth);
		put("Window", "height", windowHeight);
		put("Window", "maximized", maximized);
		put("OpenGL", "antiAliasing", antiAliasing);

		String sFrame = "Frame";
		for (Map.Entry<String, NkRect> entry : frameRectMap.entrySet()) {
		    String key = entry.getKey();
		    NkRect value = entry.getValue();
			put(sFrame, key, value.x() + "," + value.y() + "," + value.w() + "," + value.h());
		}
	}
	
	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		if (this.numThreads != numThreads) {
			this.numThreads = numThreads;
			saveToFile();
		}
	}

	public boolean isUseFixedNodes() {
		return useFixedNodes;
	}

	public void setUseFixedNodes(boolean useFixedNodes) {
		if (this.useFixedNodes != useFixedNodes) {
			this.useFixedNodes = useFixedNodes;
			saveToFile();
			isChanged = true;
		}
	}

	public double getAbsorptionFixedNodes() {
		return absorptionFixedNodes;
	}

	public void setAbsorptionFixedNodes(double absorptionFixedNodes) {
		this.absorptionFixedNodes = absorptionFixedNodes;
	}

	public boolean isUseLoop() {
		return useLoop;
	}

	public void setUseLoop(boolean useLoop) {
		if (this.useLoop != useLoop) {
			this.useLoop = useLoop;
			saveToFile();
			isChanged = true;
		}
	}

	/**
	 * Use this function to check if reset is required.
	 * If true, then define your customized reset.
	 * 
	 * @return true if {@link #reset()} has been called.
	 */
	public boolean isChanged() {
		if (isChanged) {
			isChanged = false;
			return true;
		}
		return false;
	}

	public String getProjectFilePath() {
		return projectFilePath;
	}

	public void setProjectFilePath(String projectFilePath) {
		this.projectFilePath = projectFilePath;
	}

	public String getFontFile() {
		return fontFile;
	}

	public void setFontFile(String fontFile) {
		this.fontFile = fontFile;
	}

	public String getFontFileBold() {
		return fontFileBold;
	}

	public void setFontFileBold(String fontFileBold) {
		this.fontFileBold = fontFileBold;
	}

	public String getFontFileItalic() {
		return fontFileItalic;
	}

	public void setFontFileItalic(String fontFileItalic) {
		this.fontFileItalic = fontFileItalic;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getWindowLeft() {
		return windowLeft;
	}

	public void setWindowLeft(int windowLeft) {
		this.windowLeft = windowLeft;
	}

	public int getWindowTop() {
		return windowTop;
	}

	public void setWindowTop(int windowTop) {
		this.windowTop = windowTop;
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int windowWidth) {
		if (this.windowWidth != windowWidth) {
			this.windowWidth = windowWidth;
			QFSProject.getInstance().camera.updateProjectionMatrix();
		}
	}

	public int getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(int windowHeight) {
		if (this.windowHeight != windowHeight) {
			this.windowHeight = windowHeight;
			QFSProject.getInstance().camera.updateProjectionMatrix();
		}
	}

	public int getDisplayWidth() {
		return displayWidth;
	}

	public void setDisplayWidth(int displayWidth) {
		this.displayWidth = displayWidth;
	}

	public int getDisplayHeight() {
		return displayHeight;
	}

	public void setDisplayHeight(int displayHeight) {
		this.displayHeight = displayHeight;
	}

	public int getMaximized() {
		return maximized;
	}

	public void setMaximized(int maximized) {
		this.maximized = maximized;
	}

	public HashMap<String, NkRect> getFrameRectMap() {
		return frameRectMap;
	}

	public void setFrameRectMap(HashMap<String, NkRect> frameRectMap) {
		this.frameRectMap = frameRectMap;
	}

	public char getDecimalSeperator() {
		return decimalSeperator;
	}

	public void setDecimalSeperator(char decimalSeperator) {
		this.decimalSeperator = decimalSeperator;
	}

	public DecimalFormat getFormatInt() {
		return formatInt;
	}

	public void setFormatInt(DecimalFormat formatInt) {
		this.formatInt = formatInt;
	}

	public DecimalFormat getFormatDefault() {
		return formatDefault;
	}

	public void setFormatDefault(DecimalFormat formatDefault) {
		this.formatDefault = formatDefault;
	}

	public DecimalFormat getFormatScientific2() {
		return formatScientific2;
	}

	public void setFormatScientific2(DecimalFormat formatScientific2) {
		this.formatScientific2 = formatScientific2;
	}

	public DecimalFormat getFormatScientific4() {
		return formatScientific4;
	}

	public void setFormatScientific4(DecimalFormat formatScientific4) {
		this.formatScientific4 = formatScientific4;
	}

	public DecimalFormat getFormatScientific8() {
		return formatScientific8;
	}

	public void setFormatScientific8(DecimalFormat formatScientific8) {
		this.formatScientific8 = formatScientific8;
	}

	public DecimalFormat getFormatScientific12() {
		return formatScientific12;
	}

	public void setFormatScientific12(DecimalFormat formatScientific12) {
		this.formatScientific12 = formatScientific12;
	}

	public int getAntiAliasing() {
		return antiAliasing;
	}

	public void setAntiAliasing(int antiAliasing) {
		this.antiAliasing = antiAliasing;
	}

}
