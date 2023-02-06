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
package org.smartblackbox.utils;

import java.io.File;
import java.io.IOException;

public abstract class AbstractSettings implements ISettings {
	
	protected String currentFilename = "";
	private QWini ini;

	public AbstractSettings() {
	}
	
	abstract public AbstractSettings clone();

	public void loadFromFile(String filename) {
		currentFilename = filename;
		try {
			File f = new File(filename);
			
			if (f.exists()) {
				loadFromFile(new QWini(f), "", 0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadFromFile(QWini ini, String section, int index) {
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
			
			saveToFile(new QWini(f), "", 0);
			ini.store();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveToFile(QWini ini, String section, int index) {
		this.ini = ini;
	}
	
	public void saveToFile() {
		if (!currentFilename.isEmpty())
			saveToFile(currentFilename);
	}
	
	public String getCurrentFilename() {
		return currentFilename;
	}

	public String iniGet(Object sectionName, Object optionName) {
		String s = ini.get(sectionName, optionName);
		return s == null? "" : s;
	}
	
	public String iniGet(Object sectionName, Object optionName, String defaultValue) {
		String s = ini.get(sectionName, optionName);
		return s == null? defaultValue : s;
	}
	
}
