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
package org.smartblackbox.qfs.opengl.model.lights;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.smartblackbox.qfs.opengl.model.Material;
import org.smartblackbox.qfs.opengl.model.ObjFileModel;
import org.smartblackbox.qfs.opengl.model.entity.Entity;

public class Light extends Entity {

	private Vector3f color;
	float intensity;
	private double constant, linear, exponent;
	private boolean isSpotLight = false;
	private float cutOff;
	private Entity spotLightHolder;
	private ObjFileModel modelLightBulb;
	private ObjFileModel modelSpotLight;
	
	public Light(Entity parent, ObjFileModel modelLightBulb, ObjFileModel modelSpotLight, Vector3d position, Vector3d rotation, double scale) {
		super(parent, null, position, rotation, scale);
		this.setModelLightBulb(modelLightBulb);
		this.setModelSpotLight(modelSpotLight);
		
		setSpotLightHolder(new Entity(this, modelSpotLight, new Vector3d(0, 0, -1.5), new Vector3d(0, 0, 0), 1.0));
		
		modelLightBulb.getMaterial().setDiffuseIntensity(0);
	}

	@Override
	public void updateMatrix() {
		updatePosition();
		updateRotation();
		
		if (direction.x == 0) direction.x = 0.000000001;
		if (direction.y == 0) direction.y = 0.000000001;
		if (direction.z == 0) direction.z = 0.000000001;
		
		Matrix4d lookM = new Matrix4d().rotateTowards(new Vector3d(direction), new Vector3d(0, 1, 0));
		
		getTransformMatrix()
		.identity()
		.translate(pos)
		.setRotationXYZ(rotRad.x, rotRad.y, rotRad.z)
		.scale(scale)
		.mul(lookM)
		;
		
		if (parent != null)
			parent.getTransformMatrix().mul(getTransformMatrix(), getTransformMatrix());
		transformMatrixfList[swapBufIndex].set(getTransformMatrix());
		
		if (spotLightHolder != null)
			spotLightHolder.updateMatrix();
	}
	
	public void set(Vector3f color, float intensity, double constant, double linear, double exponent, float cutOff) {
		setColor(color);
		setIntensity(intensity);
		setConstant(constant);
		setLinear(linear);
		setExponent(exponent);
		setCutOff(cutOff);
	}

	public Entity getSpotLightHolder() {
		return spotLightHolder;
	}

	public void setSpotLightHolder(Entity spotLightHolder) {
		this.spotLightHolder = spotLightHolder;
	}

	public ObjFileModel getModelLightBulb() {
		return modelLightBulb;
	}

	public void setModelLightBulb(ObjFileModel modelLightBulb) {
		this.modelLightBulb = modelLightBulb;
	}

	public ObjFileModel getModelSpotLight() {
		return modelSpotLight;
	}

	public void setModelSpotLight(ObjFileModel modelSpotLight) {
		this.modelSpotLight = modelSpotLight;
	}

	@Override
	public Material getMaterial() {
		if (overrideModelMaterial || getModel() == null) return material;
		else return getModel().getMaterial();
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		if (color != null) {
			modelLightBulb.getMaterial().setAmbientColor(new Vector4f(color, 1));
			this.color = color;
		}
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
		modelLightBulb.getMaterial().setAmbientIntensity(Math.max(intensity * 6.0, 1.0));
	}

	public double getConstant() {
		return constant;
	}

	public void setConstant(double constant) {
		this.constant = constant;
	}

	public double getLinear() {
		return linear;
	}

	public void setLinear(double linear) {
		this.linear = linear;
	}

	public double getExponent() {
		return exponent;
	}

	public void setExponent(double exponent) {
		this.exponent = exponent;
	}
	
	public boolean isSpotLight() {
		return isSpotLight;
	}
	
	public void setSpotLight(boolean isSpotLight) {
		this.isSpotLight = isSpotLight;
	}
	
	public float getCutOff() {
		return cutOff;
	}

	public float getCutOffRad() {
		return (float) Math.cos(Math.toRadians(cutOff));
	}

	public void setCutOff(float cutOff) {
		this.cutOff = cutOff;
	}

	/**
	 * Return the model based on whether it is a spotlight or not.
	 * 
	 * If it is a {@link #isSpotlight()}, then returns {@link #getModelSpotLight()) otherwise returns  {@link #getModelLightBulb()).
	 * 
	 */
	@Override
	public ObjFileModel getModel() {
		return modelLightBulb;
	}

	/**
	 * This method has no effect, because it is already defined by methods
	 * {@link #setModelSpotLight()} and {@link #setModelPointLight()}.
	 * 
	 * See {@link #getModel()}.
	 */
	public void setModel(ObjFileModel model) {
		// Do nothing here.
	}

}
