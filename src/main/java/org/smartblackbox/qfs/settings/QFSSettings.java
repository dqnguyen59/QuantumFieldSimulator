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
package org.smartblackbox.qfs.settings;

import org.joml.Vector4f;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.utils.AbstractSettings;
import org.smartblackbox.utils.ISettings;
import org.smartblackbox.utils.QWini;

/**
 * To increase the performance, some have public variables to avoid calling getters and setters.
 * Most of these settings have a direct affect on the output,
 * while others might need further actions that can be checked with the method isChanged().
 * 
 * @author dqnguyen
 *
 */
public class QFSSettings extends AbstractSettings implements ISettings {
	
	public enum SliceType {
		sliceX,
		sliceY,
		sliceZ,
		sliceYZ,
		none,
		;
		
		private static String[] strEnums;

		public static String[] getValues() {
			if (strEnums == null) {
				SliceType[] v = values();
				strEnums = new String[v.length];
				for (int i = 0; i < v.length; i++) {
					strEnums[i] = v[i].name();
				}
			}
			return strEnums;
		}
		
		public static boolean contains(String value) {
			for (String item : getValues()) {
				if (item.equals(value)) return true;
			}
			return false;
		}
	}

	public enum ColorMode {
		normal,
		xyzColor,
		xyzColor2,
		xyzColor3,
		zColor,
		zColor2,
		zColor3,
		;

		private static String[] strEnums;

		public static String[] getValues() {
			if (strEnums == null) {
				ColorMode[] v = values();
				strEnums = new String[v.length];
				for (int i = 0; i < v.length; i++) {
					strEnums[i] = v[i].name();
				}
			}
			return strEnums;
		}
	}
	
	private boolean isChanged;

	public Vector4f defaultNodeColor	= new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	public Vector4f fixedColor			= new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	public Vector4f wallColor			= new Vector4f(1.0f, 1.0f, 0.0f, 0.5f);
	public Vector4f selectedColor		= new Vector4f(1.0f, 0.0f, 1.0f, 1.0f);
	public Vector4f hiLightColor		= new Vector4f(1.0f, 1.0f, 0.0f, 0.5f);
	
	public float scale = 1.0f;
	
	private Float depthVisibility = 0.8f;
	private float alphaSlice = 1.0f;
	private float alphaAll = 0.1f;
	private float intensitySlice = 150f;
	private float intensityAll = 130f;
	private float shininess = 0.5f;
	private SliceType sliceType = SliceType.sliceZ;
	private SliceType lastSliceType = SliceType.sliceZ;
	private ColorMode colorMode = ColorMode.normal;
	private ColorMode lastColorMode = ColorMode.normal;
	private int visibleIndexX;
	private int visibleIndexY;
	private int visibleIndexZ;

	public QFSSettings() {
		super();
	}

	public QFSSettings(QFSSettings qfsSettings) {
		super();
		set(qfsSettings);
	}

	public void set(QFSSettings qfsSettings) {
		isChanged = qfsSettings.isChanged;
		scale = qfsSettings.scale;
		depthVisibility = qfsSettings.depthVisibility;
		alphaSlice = qfsSettings.alphaSlice;
		alphaAll = qfsSettings.alphaAll;
		intensitySlice = qfsSettings.intensitySlice;
		intensityAll = qfsSettings.intensityAll;
		shininess = qfsSettings.shininess;
	}

	@Override
	public AbstractSettings clone() {
		return new QFSSettings(this);
	}
	
	public Float getDepthVisibility() {
		return depthVisibility;
	}

	public void setDepthVisibility(Float value) {
		this.depthVisibility = value;
	}

	public float getDepthVisibilityFactor() {
		return (1 - depthVisibility) * Constants.DEPTH_FADING_FACTOR;
	}
	
	public float getAlphaSlice() {
		return alphaSlice;
	}

	public void setAlphaSlice(float alphaIntensitySlice) {
		this.alphaSlice = alphaIntensitySlice;
	}

	public float getAlphaAll() {
		return alphaAll;
	}

	public void setAlphaAll(float alphaIntensiityAll) {
		this.alphaAll = alphaIntensiityAll;
	}

	public float getAlpha() {
		switch (sliceType) {
		case none:
			return getAlphaAll();
		default:
			return getAlphaSlice();
		}
	}

	public void incAlpha() {
		if (getSliceType() == SliceType.none) {
			setAlphaAll(Math.min(1, getAlphaAll() + 0.01f));
		}
		else {
			setAlphaSlice(Math.min(1, getAlphaSlice() + 0.01f));
		}
	}

	public void decAlpha() {
		if (getSliceType() == SliceType.none) {
			setAlphaAll(Math.max(0, getAlphaAll() - 0.01f));
		}
		else {
			setAlphaSlice(Math.max(0, getAlphaSlice() - 0.01f));
		}
	}

	public float getIntensitySlice() {
		return intensitySlice;
	}

	public void setIntensitySlice(float intensitySlice) {
		this.intensitySlice = intensitySlice;
	}

	public float getIntensityAll() {
		return intensityAll;
	}

	public void setIntensityAll(float intensiityAll) {
		this.intensityAll = intensiityAll;
	}

	public float getIntensity() {
		switch (sliceType) {
		case none:
			return getIntensityAll();
		default:
			return getIntensitySlice();
		}
	}

	public void incIntensity() {
		if (getSliceType() == SliceType.none) {
			setIntensityAll(Math.min(Constants.MAX_INTENSITY, getIntensityAll() + 10.0f));
		}
		else {
			setIntensitySlice(Math.min(Constants.MAX_INTENSITY, getIntensitySlice() + 10.0f));
		}
	}

	public void decIntensity() {
		if (getSliceType() == SliceType.none) {
			setIntensityAll(Math.max(0, getIntensityAll() - 10.0f));
		}
		else {
			setIntensitySlice(Math.max(0, getIntensitySlice() - 10.0f));
		}
	}

	public float getShininess() {
		return shininess;
	}

	public void setShininess(float shininess) {
		this.shininess = shininess;
	}

	public SliceType getSliceType() {
		return sliceType;
	}

	public void setSliceType(SliceType sliceType) {
		if (lastSliceType != sliceType)
			isChanged = true;
		this.sliceType = sliceType;
		lastSliceType  = sliceType; 
	}

	public ColorMode getColorMode() {
		return colorMode;
	}

	public void setColorMode(ColorMode colorMode) {
		this.colorMode = colorMode;
		if (lastColorMode != colorMode)
			isChanged = true;
		lastColorMode = colorMode; 
	}

	public int getVisibleIndexX() {
		return visibleIndexX;
	}

	public void setVisibleIndexX(int visibleIndexX) {
		if (visibleIndexX < 0)
			visibleIndexX = 0;
		else if (visibleIndexX > QFSProject.getInstance().getDimensionX() - 1)
			visibleIndexX = QFSProject.getInstance().getDimensionX() - 1;
		if (this.visibleIndexX != visibleIndexX) {
			this.visibleIndexX = visibleIndexX;
			isChanged = true;
		}
	}

	public int getVisibleIndexY() {
		return visibleIndexY;
	}

	public void setVisibleIndexY(int visibleIndexY) {
		if (visibleIndexY < 0)
			visibleIndexY = 0;
		else if (visibleIndexY > QFSProject.getInstance().getDimensionY() - 1)
			visibleIndexY = QFSProject.getInstance().getDimensionY() - 1;
		if (this.visibleIndexY != visibleIndexY) {
			this.visibleIndexY = visibleIndexY;
			isChanged = true;
		}
	}

	public int getVisibleIndexZ() {
		return visibleIndexZ;
	}

	public void setVisibleIndexZ(int visibleIndexZ) {
		if (visibleIndexZ < 0)
			visibleIndexZ = 0;
		else if (visibleIndexZ > QFSProject.getInstance().getDimensionZ() - 1)
			visibleIndexZ = QFSProject.getInstance().getDimensionZ() - 1;
		if (this.visibleIndexZ != visibleIndexZ) {
			this.visibleIndexZ = visibleIndexZ;
			isChanged = true;
		}
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	@Override
	public void loadFromFile(QWini ini, String section, int index) {
		super.loadFromFile(ini, section, index);
		String s;
		defaultNodeColor.x = ini.getFloat(section, "defaultNodeColor.x", 1);
		defaultNodeColor.y = ini.getFloat(section, "defaultNodeColor.y", 1);
		defaultNodeColor.z = ini.getFloat(section, "defaultNodeColor.z", 1);
		defaultNodeColor.w = ini.getFloat(section, "defaultNodeColor.w", 1);
		fixedColor.x = ini.getFloat(section, "fixedColor.x", 1);
		fixedColor.y = ini.getFloat(section, "fixedColor.y", 1);
		fixedColor.z = ini.getFloat(section, "fixedColor.z", 1);
		fixedColor.w = ini.getFloat(section, "fixedColor.w", 1);
		wallColor.x = ini.getFloat(section, "wallColor.x", 1);
		wallColor.y = ini.getFloat(section, "wallColor.y", 1);
		wallColor.z = ini.getFloat(section, "wallColor.z", 1);
		wallColor.w = ini.getFloat(section, "wallColor.w", 1);
		selectedColor.x = ini.getFloat(section, "selectedColor.x", 1);
		selectedColor.y = ini.getFloat(section, "selectedColor.y", 1);
		selectedColor.z = ini.getFloat(section, "selectedColor.z", 1);
		selectedColor.w = ini.getFloat(section, "selectedColor.w", 1);
		hiLightColor.x = ini.getFloat(section, "hiLightColor.x", 1);
		hiLightColor.y = ini.getFloat(section, "hiLightColor.y", 1);
		hiLightColor.z = ini.getFloat(section, "hiLightColor.z", 1);
		hiLightColor.w = ini.getFloat(section, "hiLightColor.w", 1);

		scale = ini.getFloat(section, "scale", 1);
		depthVisibility = ini.getFloat(section, "depthFadingOffset", 0.8f);
		colorMode = (s = ini.getString(section, "colorMode", "")).isEmpty()? ColorMode.normal : ColorMode.valueOf(s);
		setSliceType((s = ini.getString(section, "sliceType", "")).isEmpty()? SliceType.sliceZ : SliceType.valueOf(s));
		visibleIndexX = ini.getInt(section, "visibleIndexX", 15);
		visibleIndexY = ini.getInt(section, "visibleIndexY", 15);
		visibleIndexZ = ini.getInt(section, "visibleIndexZ", 15);
		alphaSlice = ini.getFloat(section, "alphaIntensitySlice", 1);
		alphaAll = ini.getFloat(section, "alphaIntensiityAll", 0.5f);
		intensitySlice = ini.getFloat(section, "intensitySlice", 100);
		intensityAll = ini.getFloat(section, "intensiityAll", 100);
		shininess = ini.getFloat(section, "shininess", 0.5f);
	}

	@Override
	public void saveToFile(QWini ini, String section, int index) {
		super.saveToFile(ini, section, index);
		ini.put(section, "defaultNodeColor.x", defaultNodeColor.x);
		ini.put(section, "defaultNodeColor.y", defaultNodeColor.y);
		ini.put(section, "defaultNodeColor.z", defaultNodeColor.z);
		ini.put(section, "defaultNodeColor.w", defaultNodeColor.w);
		ini.put(section, "fixedColor.x", fixedColor.x);
		ini.put(section, "fixedColor.y", fixedColor.y);
		ini.put(section, "fixedColor.z", fixedColor.z);
		ini.put(section, "fixedColor.w", fixedColor.w);
		ini.put(section, "wallColor.x", wallColor.x);
		ini.put(section, "wallColor.y", wallColor.y);
		ini.put(section, "wallColor.z", wallColor.z);
		ini.put(section, "wallColor.w", wallColor.w);
		ini.put(section, "selectedColor.x", selectedColor.x);
		ini.put(section, "selectedColor.y", selectedColor.y);
		ini.put(section, "selectedColor.z", selectedColor.z);
		ini.put(section, "selectedColor.w", selectedColor.w);
		ini.put(section, "hiLightColor.x", hiLightColor.x);
		ini.put(section, "hiLightColor.y", hiLightColor.y);
		ini.put(section, "hiLightColor.z", hiLightColor.z);
		ini.put(section, "hiLightColor.w", hiLightColor.w);

		ini.put(section, "scale", scale);
		ini.put(section, "depthFadingOffset", depthVisibility);
		ini.put(section, "colorMode", colorMode.toString());
		ini.put(section, "sliceType", sliceType.toString());
		ini.put(section, "visibleIndexX", visibleIndexX);
		ini.put(section, "visibleIndexY", visibleIndexY);
		ini.put(section, "visibleIndexZ", visibleIndexZ);
		ini.put(section, "alphaIntensitySlice", alphaSlice);
		ini.put(section, "alphaIntensiityAll", alphaAll);
		ini.put(section, "intensitySlice", intensitySlice);
		ini.put(section, "intensiityAll", intensityAll);
		ini.put(section, "shininess", shininess);
	}

}
