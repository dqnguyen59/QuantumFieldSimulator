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
package org.smartblackbox.core.utils;

import java.io.File;
import java.io.IOException;

import org.ini4j.Wini;

public abstract class AbstractSettings implements ISettings {
	
	protected String currentFilename = "";

	public AbstractSettings() {
	}
	
	abstract public AbstractSettings clone();

	public void loadFromFile(String filename) {
		currentFilename = filename;
		try {
			File f = new File(filename);
			
			if (f.exists()) {
				Wini ini = new Wini(f);
				loadFromFile(ini, "", 0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			
			Wini ini = new Wini(f);
			saveToFile(ini, "", 0);
			ini.store();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveToFile() {
		if (!currentFilename.isEmpty())
			saveToFile(currentFilename);
	}
	
	public String getCurrentFilename() {
		return currentFilename;
	}

}
