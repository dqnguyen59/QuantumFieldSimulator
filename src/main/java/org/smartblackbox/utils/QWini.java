package org.smartblackbox.utils;

import java.io.File;
import java.io.IOException;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class QWini extends Wini {
	private static final long serialVersionUID = 8999515718177895823L;

	
	public QWini(File input) throws InvalidFileFormatException, IOException {
		super(input);
	}

	private void printParseError(String parsedString, Object optionName, Object defaultValue) {
		System.out.println("Parse error! Option: '" + optionName + "', could not parse value '" + parsedString + "', default value '" + defaultValue + "' is used instead!");
	}
	
	public String getString(Object sectionName, Object optionName) {
		String s = get(sectionName, optionName);
		return s == null? "" : s;
	}
	
	public String getString(Object sectionName, Object optionName, String defaultValue) {
		String s = get(sectionName, optionName);
		return s == null? defaultValue : s;
	}
	
	public Integer getInt(Object sectionName, Object optionName, int defaultValue) {
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
	
	public Double getDouble(Object sectionName, Object optionName, double defaultValue) {
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
	
	public Float getFloat(Object sectionName, Object optionName, float defaultValue) {
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
