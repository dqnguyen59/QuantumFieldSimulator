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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.system.MemoryUtil;

public class Utils {
	
	private static String osName;

	public static String getOsName() {
		if (osName == null) osName = System.getProperty("os.name");
		return osName;
	}
	
	public static boolean isWindows() {
		return osName.startsWith("Windows");
	}
	
	public static boolean isMac() {
		return osName.startsWith("Mac");
	}
	
	public static boolean isLinux() {
		return osName.startsWith("Linux");
	}
	
	public static DecimalFormat setFormat(int minFractionDigits, int maxFractionDigits,
			char decimalSeperator, boolean useGrouping, boolean alwaysShowDecimalSeperator) {
		DecimalFormat format = new DecimalFormat();
		format.setGroupingUsed(useGrouping);
		format.setDecimalSeparatorAlwaysShown(alwaysShowDecimalSeperator);

		format.setMinimumFractionDigits(minFractionDigits);
		format.setMaximumFractionDigits(maxFractionDigits);

		DecimalFormatSymbols dfs = format.getDecimalFormatSymbols();
		dfs.setDecimalSeparator(decimalSeperator);
		format.setDecimalFormatSymbols(dfs);
		return format;
	}

	public static FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
		buffer.put(data).flip();
		return buffer;
	}
	
	public static IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
		buffer.put(data).flip();
		return buffer;
	}
	
	public static String loadResource(String filename) throws Exception {
		String result;

        ClassLoader classLoader = Utils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filename);
		try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
			result = scanner.useDelimiter("\\A").next();
		}

		return result;
	}
	
	public static List<String> readAllLines(String fineName) {
		List<String> list = new ArrayList<>();
        
		ClassLoader classLoader = Utils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fineName);

        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
        	String line;
        	while ((line = br.readLine()) != null) {
				list.add(line);
			}
        } catch (IOException e) {
        	e.printStackTrace();
        };
		return list;
	}

	public static List<File> getFiles(String path, String filter) {
		List<File> files = new ArrayList<File>();
		File f = new File(path);
		if (f.exists()) {
			String[] list = f.list();
			for (String filename : list) {
				if (filename.endsWith(filter))
					files.add(new File(filename));
			}
		}
		
		files.sort(new Comparator<File>() {

			@Override
			public int compare(File file1, File file2) {
				return file1.getName().compareTo(file2.getName());
			}
			
		});
		
		return files;
	}

	public static void sleepMS(int msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
		}
	}

	public static boolean fileExists(String selectedFileName) {
		File f = new File(selectedFileName);
		return f.exists();
	}
	
}
