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
package org.smartblackbox.utils;

import java.io.File;
import java.io.IOException;

import org.ini4j.Wini;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.gui.model.NuklearModel;

public abstract class AbstractSettings implements ISettings {

	protected NuklearModel nuklearModel;

	protected String currentFilename = "";
	private Wini ini;

	public AbstractSettings() {
	}
	
	abstract public AbstractSettings clone();

	public NuklearModel getNuklearModel() {
		return nuklearModel;
	}

	public void setNuklearModel(NuklearModel nuklearModel) {
		this.nuklearModel = nuklearModel;
	}

	public void loadFromFile(String filename) {
		currentFilename = filename;
		try {
			File f = new File(filename);
			
			if (f.exists()) {
				loadFromFile(new Wini(f), "", 0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadFromFile(Wini ini, String section, int index) {
		this.ini = ini;
	}

	public void loadFromFile() {
		if (currentFilename.isEmpty()) {
			getNuklearModel().setDialogFileModel(nuklearModel.showOpenFileDialog("Open Project File"));
		}
		else {
			loadFromFile(currentFilename);
		}
	}

	public void openFile() {
		getNuklearModel().setDialogFileModel(nuklearModel.showOpenFileDialog("Open Project File"));
	}

	public void saveToFile(String filename) {
		try {
			File f = new File(filename);
			
			if (!f.exists()) {
				f.createNewFile();
			}
			currentFilename = filename;
			
			saveToFile(new Wini(f), "", 0);
			ini.store();
			System.out.println("saveToFile: " + currentFilename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveToFile(Wini ini, String section, int index) {
		this.ini = ini;
	}
	
	public void saveToFile() {
		if (getCurrentFilename().isEmpty()) {
			getNuklearModel().setDialogFileModel(nuklearModel.showSaveFileDialog("Save Project File As"));
		}
		else {
			saveToFile(currentFilename);
		}
	}

	public void saveAs() {
		getNuklearModel().setDialogFileModel(nuklearModel.showSaveFileDialog("Save Project File As"));
	}

	public String getCurrentFilename() {
		return currentFilename;
	}

	protected void put(String sectionName, String optionName, Object value) {
		ini.put(sectionName, optionName, value);
	}

	private void printParseError(String parsedString, Object optionName, Object defaultValue) {
		System.out.println("Parse error! Option: '" + optionName + "', could not parse value '" + parsedString + "', default value '" + defaultValue + "' is used instead!");
	}
	
	public String get(String sectionName, String optionName) {
		String s = ini.get(sectionName, optionName);
		return s == null? "" : s;
	}
	
	public String getString(String sectionName, String optionName) {
		String s = ini.get(sectionName, optionName);
		return s == null? "" : s;
	}
	
	public String getString(String sectionName, String optionName, String defaultValue) {
		String s = ini.get(sectionName, optionName);
		return s == null? defaultValue : s;
	}
	
	public Integer getInt(String sectionName, String optionName, int defaultValue) {
		String s = getString(sectionName, optionName);
		if (s == null || s.isEmpty()) return defaultValue;
		else {
			try {
				return Integer.parseInt(s);
			}
			catch (Exception e) {
				printParseError(s, optionName, defaultValue);
				return defaultValue;
			}
		}
	}
	
	public Double getDouble(String sectionName, String optionName, double defaultValue) {
		String s = getString(sectionName, optionName);
		if (s == null || s.isEmpty()) return defaultValue;
		else {
			try {
				return Double.parseDouble(s);
			}
			catch (Exception e) {
				printParseError(s, optionName, defaultValue);
				return defaultValue;
			}
		}
	}
	
	public Float getFloat(String sectionName, String optionName, float defaultValue) {
		String s = getString(sectionName, optionName);
		if (s == null || s.isEmpty()) return defaultValue;
		else {
			try {
				return Float.parseFloat(s);
			}
			catch (Exception e) {
				printParseError(s, optionName, defaultValue);
				return defaultValue;
			}
		}
	}

	public boolean getBool(String sectionName, String optionName, boolean defaultValue) {
		String s = getString(sectionName, optionName);
		if (s == null || s.isEmpty()) return defaultValue;
		else {
			try {
				return Boolean.parseBoolean(s);
			}
			catch (Exception e) {
				printParseError(s, optionName, defaultValue);
				return defaultValue;
			}
		}
	}

	public void performFileDialog(String projectFilePath) {
		if (getNuklearModel().getDialogFileModel() != null) {
			String path = Constants.BASE_PATH + projectFilePath + Constants.SEPARATOR;

			switch(getNuklearModel().getDialogFileModel().getConfirmState()) {
				case open:
					loadFromFile(path + getNuklearModel().getDialogFileModel().getSelectedFileName());
					getNuklearModel().setDialogFileModel(null);
					break;
				case save:
					saveToFile(path + getNuklearModel().getDialogFileModel().getSelectedFileName());
					getNuklearModel().setDialogFileModel(null);
					break;
                default:
					break;
			}
		}
	}
}
