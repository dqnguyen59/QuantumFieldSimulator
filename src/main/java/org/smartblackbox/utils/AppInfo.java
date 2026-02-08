package org.smartblackbox.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class AppInfo {

	private static String title = "Quantum Field Simulator";
	private static String description = "Only visible when running from JAR MANIFEST.MF.";
	private static String builtBy = "SmartBlackBox";
	private static String contributors = "";
	private static String version = "";
	private static String website = "https://www.smartblackbox.org";
	private static String author = "Duy Quoc Nguyen";
	private static String email = "d.q.nguyen@smartblackbox.org";
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

							String title = mainAttribs.getValue("Title");
							if (title == null) AppInfo.title = title;

							String description = mainAttribs.getValue("Description");
							if (description != null) AppInfo.description = description.replace("\t\t", " ");

							String dateTime = mainAttribs.getValue("Date-Time");
							if (dateTime != null) AppInfo.dateTime = dateTime;

							String builtBy = mainAttribs.getValue("Built-By");
							if (builtBy != null) AppInfo.builtBy = builtBy;

							String author = mainAttribs.getValue("Author");
							if (author != null) AppInfo.author = author;

							String contributors = mainAttribs.getValue("Contributors");
							if (contributors != null) AppInfo.contributors = contributors;

							String version = mainAttribs.getValue("Version");
							if (version != null) AppInfo.version = version;

							String website = mainAttribs.getValue("Website");
							if (website != null) AppInfo.website = website;

							String email = mainAttribs.getValue("Email");
							if (email != null) AppInfo.email = email;
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

	public static String getCopyRight() {
		return String.format("© Copyright %s <%s> and contributors. All right reserved.", author, email);
	}
	
	public static String getTitle() {
		return AppInfo.title;
	}

	public static String getDescription() {
		return AppInfo.description;
	}

	public static String getDateTime() {
		return AppInfo.dateTime;
	}

	public static String getBuiltBy() {
		return AppInfo.builtBy;
	}

	public static String getAuthor() {
		return author;
	}

	public static String getContributors() {
		return AppInfo.contributors;
	}

	public static String getVersion() {
		return AppInfo.version;
	}

	public static String getWebsite() {
		return AppInfo.website;
	}

	public static String getEmail() {
		return email;
	}

}
