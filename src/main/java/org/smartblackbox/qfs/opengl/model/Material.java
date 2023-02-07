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
		super.loadFromFile(ini, section, index);
		
		ambientColor.x = getFloat(section, "material.ambientColor.x", 1);
		ambientColor.y = getFloat(section, "material.ambientColor.y", 1);
		ambientColor.z = getFloat(section, "material.ambientColor.z", 1);
		diffuseColor.x = getFloat(section, "material.diffuseColor.x", 1);
		diffuseColor.y = getFloat(section, "material.diffuseColor.y", 1);
		diffuseColor.z = getFloat(section, "material.diffuseColor.z", 1);
		specularColor.x = getFloat(section, "material.specularColor.x", 1);
		specularColor.y = getFloat(section, "material.specularColor.y", 1);
		specularColor.z = getFloat(section, "material.specularColor.z", 1);
		ambientIntensity = getFloat(section, "material.ambientIntensity", 1);
		diffuseIntensity = getFloat(section, "material.diffuseIntensity", 1);
		specularIntensity = getFloat(section, "material.specularIntensity", 1);
		shininess = getFloat(section, "material.shininess", 1);
		disableCulling = getBool(section, "material.disableCulling", false);
	}

	@Override
	public void saveToFile(Wini ini, String section, int index) {
		super.saveToFile(ini, section, index);
		put(section, "material.ambientColor.x", ambientColor.x);
		put(section, "material.ambientColor.y", ambientColor.y);
		put(section, "material.ambientColor.z", ambientColor.z);
		put(section, "material.diffuseColor.x", diffuseColor.x);
		put(section, "material.diffuseColor.y", diffuseColor.y);
		put(section, "material.diffuseColor.z", diffuseColor.z);
		put(section, "material.specularColor.x", specularColor.x);
		put(section, "material.specularColor.y", specularColor.y);
		put(section, "material.specularColor.z", specularColor.z);
		put(section, "material.ambientIntensity", ambientIntensity);
		put(section, "material.diffuseIntensity", diffuseIntensity);
		put(section, "material.specularIntensity", specularIntensity);
		put(section, "material.shininess", shininess);
		put(section, "material.disableCulling", disableCulling);
	}

}
