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

import org.ini4j.Wini;
import org.joml.Vector4f;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.utils.AbstractSettings;
import org.smartblackbox.utils.ISettings;

/**
 * To increase the performance, some have public variables to avoid calling getters and setters.
 * Most of these settings have a direct affect on the output,
 * while others might need further actions that can be checked with the method isChanged().
 * 
 * @author dqnguyen
 *
 */
public class QFSSettings extends AbstractSettings implements ISettings {
	
	public enum RenderType {
		slice,
		all,
		;
		
		private static String[] strEnums;

		public static String[] getValues() {
			if (strEnums == null) {
				RenderType[] v = values();
				strEnums = new String[v.length];
				for (int i = 0; i < v.length; i++) {
					strEnums[i] = v[i].name();
				}
			}
			return strEnums;
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
	
	private Float depthVisibility = 0.4f;
	private float alphaSlice = 1.0f;
	private float alphaAll = 0.1f;
	private float intensitySlice = 150f;
	private float intensityAll = 130f;
	private float shininess = 0.5f;
	private RenderType renderType = RenderType.slice;
	private RenderType lastRenderType = RenderType.slice;
	private ColorMode colorMode = ColorMode.normal;
	private ColorMode lastColorMode = ColorMode.normal;
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
		switch (renderType) {
		case all:
			return getAlphaAll();
		case slice:
		default:
			return getAlphaSlice();
		}
	}

	public void incAlpha() {
		if (getRenderType() == RenderType.all) {
			setAlphaAll(Math.min(1, getAlphaAll() + 0.01f));
		}
		else {
			setAlphaSlice(Math.min(1, getAlphaSlice() + 0.01f));
		}
	}

	public void decAlpha() {
		if (getRenderType() == RenderType.all) {
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
		switch (renderType) {
		case all:
			return getIntensityAll();
		case slice:
		default:
			return getIntensitySlice();
		}
	}

	public void incIntensity() {
		if (getRenderType() == RenderType.all) {
			setIntensityAll(Math.min(Constants.MAX_INTENSITY, getIntensityAll() + 10.0f));
		}
		else {
			setIntensitySlice(Math.min(Constants.MAX_INTENSITY, getIntensitySlice() + 10.0f));
		}
	}

	public void decIntensity() {
		if (getRenderType() == RenderType.all) {
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

	public RenderType getRenderType() {
		return renderType;
	}

	public void setRenderType(RenderType renderType) {
		if (lastRenderType != renderType)
			isChanged = true;
		this.renderType = renderType;
		lastRenderType  = renderType; 
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

	public int getVisibleIndexZ() {
		return visibleIndexZ;
	}

	public void setVisibleIndexZ(int visibleIndexZ) {
		if (visibleIndexZ < 0)
			visibleIndexZ = 0;
		else if (visibleIndexZ > QFSProject.getInstance().getDimensionZ() - 1)
			visibleIndexZ = QFSProject.getInstance().getDimensionZ() - 1;
		this.visibleIndexZ = visibleIndexZ;
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	@Override
	public void loadFromFile(Wini ini, String section, int index) {
		String s;
		defaultNodeColor.x = ((s = ini.get(section, "defaultNodeColor.x")) == null? 1 : Float.parseFloat(s));
		defaultNodeColor.y = ((s = ini.get(section, "defaultNodeColor.y")) == null? 1 : Float.parseFloat(s));
		defaultNodeColor.z = ((s = ini.get(section, "defaultNodeColor.z")) == null? 1 : Float.parseFloat(s));
		defaultNodeColor.w = ((s = ini.get(section, "defaultNodeColor.w")) == null? 1 : Float.parseFloat(s));
		fixedColor.x = ((s = ini.get(section, "fixedColor.x")) == null? 1 : Float.parseFloat(s));
		fixedColor.y = ((s = ini.get(section, "fixefixedColordNodeColor.y")) == null? 1 : Float.parseFloat(s));
		fixedColor.z = ((s = ini.get(section, "fixedColor.z")) == null? 1 : Float.parseFloat(s));
		fixedColor.w = ((s = ini.get(section, "fixedColor.w")) == null? 1 : Float.parseFloat(s));
		wallColor.x = ((s = ini.get(section, "wallColor.x")) == null? 1 : Float.parseFloat(s));
		wallColor.y = ((s = ini.get(section, "wallColor.y")) == null? 1 : Float.parseFloat(s));
		wallColor.z = ((s = ini.get(section, "wallColor.z")) == null? 1 : Float.parseFloat(s));
		wallColor.w = ((s = ini.get(section, "wallColor.w")) == null? 1 : Float.parseFloat(s));
		selectedColor.x = ((s = ini.get(section, "selectedColor.x")) == null? 1 : Float.parseFloat(s));
		selectedColor.y = ((s = ini.get(section, "selectedColor.y")) == null? 1 : Float.parseFloat(s));
		selectedColor.z = ((s = ini.get(section, "selectedColor.z")) == null? 1 : Float.parseFloat(s));
		selectedColor.w = ((s = ini.get(section, "selectedColor.w")) == null? 1 : Float.parseFloat(s));
		hiLightColor.x = ((s = ini.get(section, "hiLightColor.x")) == null? 1 : Float.parseFloat(s));
		hiLightColor.y = ((s = ini.get(section, "hiLightColor.y")) == null? 1 : Float.parseFloat(s));
		hiLightColor.z = ((s = ini.get(section, "hiLightColor.z")) == null? 1 : Float.parseFloat(s));
		hiLightColor.w = ((s = ini.get(section, "hiLightColor.w")) == null? 1 : Float.parseFloat(s));

		scale = ((s = ini.get(section, "scale")) == null? 1.0f : Float.parseFloat(s));
		depthVisibility = ((s = ini.get(section, "depthFadingOffset")) == null? 0.8f : Float.parseFloat(s));
		colorMode = ((s = ini.get(section, "colorMode")) == null? ColorMode.normal : ColorMode.valueOf(s));
		setRenderType((s = ini.get(section, "renderType")) == null? RenderType.slice : RenderType.valueOf(s));
		visibleIndexZ = ((s = ini.get(section, "visibleIndexZ")) == null? 15 : Integer.parseInt(s));
		alphaSlice = ((s = ini.get(section, "alphaIntensitySlice")) == null? 1 : Float.parseFloat(s));
		alphaAll = ((s = ini.get(section, "alphaIntensiityAll")) == null? 0.5f : Float.parseFloat(s));
		intensitySlice = ((s = ini.get(section, "intensitySlice")) == null? 100 : Float.parseFloat(s));
		intensityAll = ((s = ini.get(section, "intensiityAll")) == null? 100 : Float.parseFloat(s));
		shininess = ((s = ini.get(section, "shininess")) == null? 0 : Float.parseFloat(s));
	}

	@Override
	public void saveToFile(Wini ini, String section, int index) {
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
		ini.put(section, "colorMode", colorMode == null? "" : colorMode.toString());
		ini.put(section, "renderType", renderType == null? "" : renderType.toString());
		ini.put(section, "visibleIndexZ", visibleIndexZ);
		ini.put(section, "alphaIntensitySlice", alphaSlice);
		ini.put(section, "alphaIntensiityAll", alphaAll);
		ini.put(section, "intensitySlice", intensitySlice);
		ini.put(section, "intensiityAll", intensityAll);
		ini.put(section, "shininess", shininess);
	}

}
