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
package org.smartblackbox;

import org.lwjgl.Version;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.opengl.controller.Engine;
import org.smartblackbox.qfs.opengl.controller.QuantumFieldSimulator;
import org.smartblackbox.qfs.opengl.view.GLWindow;
import org.smartblackbox.qfs.settings.AppSettings;
import org.smartblackbox.utils.AppInfo;
import org.smartblackbox.utils.Utils;

public class Main {
	
	private static GLWindow glWindow;

	public static void main(String[] args) {
		AppInfo.getManifestInfo("QuantumFieldSimulator");

		System.out.println(AppInfo.getTitle());
		System.out.println(AppInfo.getCopyRight());
		System.out.println("Version: " + AppInfo.getVersion());
		System.out.println("Version Date: " + AppInfo.getDateTime());
		System.out.println("Built By: " + AppInfo.getBuiltBy());
		System.out.println("Creator: " + AppInfo.getCreator());
		System.out.println("Contributers: " + AppInfo.getContributers());
		System.out.println(AppInfo.getWebsite());
		System.out.println("");
		System.out.println("JWGL Version: " + Version.getVersion());
		System.out.println("");

		Utils.forceJarCurrentDirectory();
		
		Utils.deleteAllTmpFiles("tmp");
		
		AppSettings.getInstance().loadFromFile(Constants.APP_SETTINGS_FILE);
		
		glWindow = new GLWindow(Constants.TITLE);
		Engine engine = new QuantumFieldSimulator(glWindow);
	
		try {
			engine.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			engine.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
