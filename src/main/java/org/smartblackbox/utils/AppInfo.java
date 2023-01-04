package org.smartblackbox.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class AppInfo {

	private static String title = "";
	private static String description = "Only visible when running from JAR MANIFEST.MF.";
	private static String builtBy = "SmartBlackBox";
	private static String contributers = "";
	private static String version = "";
	private static String website = "https://www.smartblackbox.nl";
	private static String author = "Duy Quoc Nguyen";
	private static String email = "d.q.nguyen@smartblackbox.nl";
	private static String dateTime = "";

	public static void getManifestInfo(String containsName) {
		try {
			Enumeration<URL> resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);

			while (resEnum.hasMoreElements()) {
				try {
					URL url = (URL)resEnum.nextElement();

					if (url.getPath().contains(containsName)) {
						System.out.println("URL: " + url.getPath());

						InputStream is = url.openStream();
						if (is != null) {
							Manifest manifest = new Manifest(is);
							Attributes mainAttribs = manifest.getMainAttributes();

							setTitle(mainAttribs.getValue("Title"));
							setDescription(mainAttribs.getValue("Description"));
							setDateTime(mainAttribs.getValue("Date-Time"));
							setBuiltBy(mainAttribs.getValue("Built-By"));
							setAuthor(mainAttribs.getValue("Author"));
							setContributers(mainAttribs.getValue("Contributers"));
							setVersion(mainAttribs.getValue("Version"));
							setWebsite(mainAttribs.getValue("Website"));
							setEmail(mainAttribs.getValue("Email"));
							
							String copyRight = String.format("© Copyright %s <%s> and contributers. All right reserved.", author, email);
							System.out.println(copyRight);
							System.out.println("Date-Time: " + dateTime);
							System.out.println("Built By: " + builtBy);
							System.out.println("Author: " + author);
							System.out.println("Contributers: " + contributers);
							System.out.println("Version: " + version);
							System.out.println("Website: " + website);
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getTitle() {
		return title;
	}

	public static void setTitle(String title) {
		if (title != null)
			AppInfo.title = title;
	}

	public static String getDescription() {
		return description;
	}

	public static void setDescription(String description) {
		if (description != null)
			AppInfo.description = description;
	}

	public static String getDateTime() {
		return dateTime;
	}

	public static void setDateTime(String dateTime) {
		if (dateTime != null)
			AppInfo.dateTime = dateTime;
	}

	public static String getBuiltBy() {
		return builtBy;
	}

	public static void setBuiltBy(String builtBy) {
		if (builtBy != null)
			AppInfo.builtBy = builtBy;
	}

	public static String getAuthor() {
		return author;
	}

	public static void setAuthor(String author) {
		if (author != null)
			AppInfo.author = author;
	}	

	public static String getContributers() {
		return contributers;
	}

	public static void setContributers(String developers) {
		if (developers != null)
			AppInfo.contributers = developers;
	}

	public static String getVersion() {
		return version;
	}

	public static void setVersion(String version) {
		if (version != null)
			AppInfo.version = version;
	}

	public static String getWebsite() {
		return website;
	}

	public static void setWebsite(String website) {
		if (website != null)
			AppInfo.website = website;
	}

	public static String getEmail() {
		return email;
	}

	public static void setEmail(String email) {
		if (email != null)
			AppInfo.email = email;
	}

}
