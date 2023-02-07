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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.lwjgl.system.MemoryUtil;
import org.smartblackbox.qfs.Constants;

public class Utils {
	
	private static String osName;

	public static String getOsName() {
		if (osName == null) osName = System.getProperty("os.name");
		return osName;
	}
	
	public static boolean isWindows() {
		return getOsName().startsWith("Windows");
	}
	
	public static boolean isMac() {
		return getOsName().startsWith("Mac");
	}
	
	public static boolean isLinux() {
		return getOsName().startsWith("Linux");
	}
	
    public static String generateString() {
    	return UUID.randomUUID().toString();
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
	
	public static String loadResource(String fileName) {
		String result;

        ClassLoader classLoader = Utils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
        	// If running from jar file, then JAR_RESOURCES_PATH is required.
        	inputStream = classLoader.getResourceAsStream(fileName);
        }
		try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
			result = scanner.useDelimiter("\\A").next();
		}

		return result;
	}
	
	public static List<String> loadResourceToStringList(String fileName) {
		List<String> result = new ArrayList<String>();

        ClassLoader classLoader = Utils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
        	// If running from jar file, then JAR_RESOURCES_PATH is required.
        	inputStream = classLoader.getResourceAsStream(fileName);
        }
		try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
			while (scanner.hasNextLine()) {
				result.add(scanner.nextLine());
			}
		}

		return result;
	}
	
	public static String loadTextFile(String fileName) throws IOException {
		return new String(Files.readAllBytes(Paths.get(fileName)));
	}

	public static List<File> getFiles(String path, String filter) {
		List<File> files = new ArrayList<File>();
		File f = new File(path);
		if (f.exists()) {
			String[] list = f.list();
			for (String filename : list) {
				if (filename.endsWith(filter))
					files.add(new File(path + File.separatorChar + filename));
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

	public static void copyStream(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1) {
	        out.write(buffer, 0, read);
	    }
	    in.close();
	    out.close();
	}
	
	public static void streamToFile(InputStream stream, String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
        OutputStream output = new BufferedOutputStream(fos);
        copyStream(stream, output);
        fos.close();
    }
	
	public static void deleteAllTmpFiles(String path, String filter) {
		List<File> files = getFiles(path, filter);
		for (File file : files) {
	        if (file.exists())
	        	file.delete();
		}
	}
	
	public static String getSystemTempFolder() {
		return System.getProperty("java.io.tmpdir");
	}
	
	/**
	 * Delete all tmp files that is created in the application path.
	 * 
	 * @param path
	 */
	public static void deleteAllTmpFiles(String folder) {
		List<File> files = getFiles(folder, ".tmp");
		for (File file : files) {
	        if (file.exists())
	        	file.delete();
		}
	}
	
	public static ByteBuffer resourceToByteBuffer(String fileName) throws IOException, URISyntaxException {
        ClassLoader classLoader = Utils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
        	// If running from jar file, then JAR_RESOURCES_PATH is required.
        	inputStream = classLoader.getResourceAsStream(Constants.JAR_RESOURCES_PATH + fileName);
        }

        // When loading resource files from jar is needed, there's no way to convert inputStream to MappedByteBuffer.
        // So, the file from resource must be saved to a temporary file that is saved outside the jar.
        
        String dir = "tmp";
        
        File directory = new File(dir);
        if (!directory.exists()){
            directory.mkdir();
        }
        
        String tmpFile = dir + File.separator + "file" + generateString() + ".tmp";
        streamToFile(inputStream, tmpFile);

        // Now, the temporary file can be read and put it into MappedByteBuffer.
        ByteBuffer buffer = fileToByteBuffer(tmpFile);
        
        return buffer;
    }

    public static ByteBuffer fileToByteBuffer(String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(new File(fileName));
        FileChannel fc = fis.getChannel();
        ByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        fc.close();
        fis.close();
        return buffer;
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
	
	public static String runCommand(String command, String suffix) throws Exception {
		try {
			Process p = Runtime.getRuntime().exec(command);

			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new 
					InputStreamReader(p.getErrorStream()));

			String s = null;
			String sOutput = "";
			String sError = null;

			while ((s = stdError.readLine()) != null) {
				sError += s;
			}

			if (sError != null) throw new Exception(sError);

			while ((s = stdInput.readLine()) != null) {
				sOutput += s + suffix;
			}

			return sOutput;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String runCommand(String command) throws Exception {
		return runCommand(command, "\n");
	}

	public static String runCommandWithThrows(String command, String suffix) throws Exception {
		Process p = Runtime.getRuntime().exec(command);

		BufferedReader stdInput = new BufferedReader(new 
				InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new 
				InputStreamReader(p.getErrorStream()));

		String s = null;
		String sOutput = "";
		String sError = null;

		while ((s = stdError.readLine()) != null) {
			sError += s;
		}

		if (sError != null) throw new Exception(sError);

		while ((s = stdInput.readLine()) != null) {
			sOutput += s + suffix;
		}

		return sOutput;
	}

	public static String runCommandWithThrows(String command) throws Exception {
		return runCommand(command, "\n");
	}

	/**
	 * If you are unable to run Jar file from Linux, 
	 * then call this method to force run from the current directory instead of home folder.
	 */
	public static void forceJarCurrentDirectory() {
		String path = System.getProperty("java.class.path");
		
		// If running from Eclipse, then do nothing
		if (path.contains("target")) return;
		
		File f = new File(path);
		path = f.getParent();
		if (path != null) {
			System.getProperties().put("user.dir", path);
		}
	}
	
	public static String getCurrentPath() {
		return System.getProperty("user.dir");
	}

	public static String getDatetimeAsString() {
		final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
		final String dateTime = sdf.format(new Date());

		return dateTime;
	}

	public static String getFileDatetimeAsString() {
		final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FILE_FORMAT);
		final String dateTime = sdf.format(new Date());

		return dateTime;
	}
}
