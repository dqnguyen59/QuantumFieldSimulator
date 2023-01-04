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
package org.smartblackbox.qfs.opengl.model;

import org.ini4j.Wini;
import org.joml.Vector4f;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.utils.AbstractSettings;

public class Material extends AbstractSettings {

	private Vector4f ambientColor = new Vector4f(Constants.DEFAULT_COLOUR);
	private Vector4f diffuseColor = new Vector4f(Constants.DEFAULT_COLOUR);
	private Vector4f specularColor = new Vector4f(Constants.DEFAULT_COLOUR);
	private Vector4f ambientIntensityColor = new Vector4f(Constants.DEFAULT_COLOUR);
	private Vector4f diffuseIntensityColor = new Vector4f(Constants.DEFAULT_COLOUR);
	private Vector4f specularIntensityColor = new Vector4f(Constants.DEFAULT_COLOUR);
	private double ambientIntensity = 1;
	private double diffuseIntensity = 1;
	private double specularIntensity = 1;
	private double shininess = 1;
	private Texture texture;
	private boolean disableCulling = false; 
	
	public Material() {
	}
	
	public Material(Vector4f color, float shininess) {
		this(color, color, color, shininess, null);
	}
	
	public Material(Texture texture) {
		this(Constants.DEFAULT_COLOUR, Constants.DEFAULT_COLOUR, Constants.DEFAULT_COLOUR, 0, texture);
	}
	
	public Material(Texture texture, float shininess) {
		this(Constants.DEFAULT_COLOUR, Constants.DEFAULT_COLOUR, Constants.DEFAULT_COLOUR, shininess, texture);
	}

	public Material(Vector4f color, float shininess, Texture texture) {
		this(color, color, color, shininess, texture);
	}
	
	public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float shininess,
			Texture texture) {
		this.ambientColor = ambientColor;
		this.diffuseColor = diffuseColor;
		this.specularColor = specularColor;
		this.shininess = shininess;
		this.texture = texture;
	}
	
	public Material(Material material) {
		this.ambientColor = material.ambientColor;
		this.diffuseColor = material.diffuseColor;
		this.specularColor = material.specularColor;
		this.shininess = material.shininess;
		this.texture = material.texture;
	}

	@Override
	public AbstractSettings clone() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public Texture getTexture() {
		return texture;
	}

	public boolean hasTexture() {
		return texture != null;
	}

	public Vector4f getAmbientColor() {
		return ambientColor;
	}

	public void setAmbientColor(Vector4f ambientColor) {
		this.ambientColor = ambientColor;
	}

	public void setAmbientColor(float x, float y, float z, float w) {
		ambientColor.x = x;
		ambientColor.y = y;
		ambientColor.z = z;
		ambientColor.w = w;
	}

	public double getAmbientIntensity() {
		return ambientIntensity;
	}

	public void setAmbientIntensity(double ambientIntensity) {
		this.ambientIntensity = ambientIntensity;
	}

	public Vector4f getAmbientIntensityColor() {
		return ambientIntensityColor.set(ambientColor).mul((float) ambientIntensity);
	}

	public Vector4f getDiffuseColor() {
		return diffuseColor;
	}

	public void setDiffuseColor(Vector4f diffuseColor) {
		this.diffuseColor = diffuseColor;
	}

	public void setDiffuseColor(float x, float y, float z, float w) {
		diffuseColor.x = x;
		diffuseColor.y = y;
		diffuseColor.z = z;
		diffuseColor.w = w;
	}

	public double getDiffuseIntensity() {
		return diffuseIntensity;
	}

	public void setDiffuseIntensity(double diffuseIntensity) {
		this.diffuseIntensity = diffuseIntensity;
	}

	public Vector4f getDiffuseIntensityColor() {
		return diffuseIntensityColor.set(diffuseColor).mul((float) diffuseIntensity);
	}

	public double getSpecularIntensity() {
		return specularIntensity;
	}

	public void setSpecularIntensity(double specularIntensity) {
		this.specularIntensity = specularIntensity;
	}

	public Vector4f getSpecularColor() {
		return specularColor;
	}

	public void setSpecularColor(Vector4f specularColor) {
		this.specularColor = specularColor;
	}

	public Vector4f getSpecularIntensityColor() {
		return specularIntensityColor.set(specularColor).mul((float) specularIntensity);
	}

	public double getShininess() {
		return shininess;
	}

	public void setShininess(double shininess) {
		this.shininess = shininess;
	}

	public boolean isDisableCulling() {
		return disableCulling;
	}

	public void setDisableCulling(boolean disableCulling) {
		this.disableCulling = disableCulling;
	}

	@Override
	public void loadFromFile(Wini ini, String section, int index) {
		String s;
		
		ambientColor.x = ((s = ini.get(section, "material.ambientColor.x")) == null? 1 : Float.parseFloat(s));
		ambientColor.y = ((s = ini.get(section, "material.ambientColor.y")) == null? 1 : Float.parseFloat(s));
		ambientColor.z = ((s = ini.get(section, "material.ambientColor.z")) == null? 1 : Float.parseFloat(s));
		diffuseColor.x = ((s = ini.get(section, "material.diffuseColor.x")) == null? 1 : Float.parseFloat(s));
		diffuseColor.y = ((s = ini.get(section, "material.diffuseColor.y")) == null? 1 : Float.parseFloat(s));
		diffuseColor.z = ((s = ini.get(section, "material.diffuseColor.z")) == null? 1 : Float.parseFloat(s));
		specularColor.x = ((s = ini.get(section, "material.specularColor.x")) == null? 1 : Float.parseFloat(s));
		specularColor.y = ((s = ini.get(section, "material.specularColor.y")) == null? 1 : Float.parseFloat(s));
		specularColor.z = ((s = ini.get(section, "material.specularColor.z")) == null? 1 : Float.parseFloat(s));
		ambientIntensity = ((s = ini.get(section, "material.ambientIntensity")) == null? 1 : Float.parseFloat(s));
		diffuseIntensity = ((s = ini.get(section, "material.diffuseIntensity")) == null? 1 : Float.parseFloat(s));
		specularIntensity = ((s = ini.get(section, "material.specularIntensity")) == null? 1 : Float.parseFloat(s));
		shininess = ((s = ini.get(section, "material.shininess")) == null? 1 : Float.parseFloat(s));
		disableCulling = ((s = ini.get(section, "material.disableCulling")) == null? false : Boolean.parseBoolean(s));
	}

	@Override
	public void saveToFile(Wini ini, String section, int index) {
		ini.put(section, "material.ambientColor.x", ambientColor.x);
		ini.put(section, "material.ambientColor.y", ambientColor.y);
		ini.put(section, "material.ambientColor.z", ambientColor.z);
		ini.put(section, "material.diffuseColor.x", diffuseColor.x);
		ini.put(section, "material.diffuseColor.y", diffuseColor.y);
		ini.put(section, "material.diffuseColor.z", diffuseColor.z);
		ini.put(section, "material.specularColor.x", specularColor.x);
		ini.put(section, "material.specularColor.y", specularColor.y);
		ini.put(section, "material.specularColor.z", specularColor.z);
		ini.put(section, "material.ambientIntensity", ambientIntensity);
		ini.put(section, "material.diffuseIntensity", diffuseIntensity);
		ini.put(section, "material.specularIntensity", specularIntensity);
		ini.put(section, "material.shininess", shininess);
		ini.put(section, "material.disableCulling", disableCulling);
	}

}
