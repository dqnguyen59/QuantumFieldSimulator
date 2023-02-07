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

public abstract class AbstractSettings implements ISettings {
	
	protected String currentFilename = "";
	private Wini ini;

	public AbstractSettings() {
	}
	
	abstract public AbstractSettings clone();

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
		if (!currentFilename.isEmpty())
			loadFromFile(currentFilename);
	}

	public void saveToFile(String filename) {
		try {
			File f = new File(filename);
			
			if (!f.exists()) f.createNewFile();
			currentFilename = filename;
			
			saveToFile(new Wini(f), "", 0);
			ini.store();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveToFile(Wini ini, String section, int index) {
		this.ini = ini;
	}
	
	public void saveToFile() {
		if (!currentFilename.isEmpty())
			saveToFile(currentFilename);
	}
	
	public String getCurrentFilename() {
		return currentFilename;
	}

	protected void put(String section, String string, Object value) {
		ini.put(section, section, value);
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

}
