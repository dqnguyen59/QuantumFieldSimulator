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
package org.smartblackbox.qfs.opengl.model.entity;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.smartblackbox.qfs.opengl.model.Material;
import org.smartblackbox.qfs.opengl.model.ObjFileModel;
import org.smartblackbox.qfs.settings.QFSProject;
import org.smartblackbox.qfs.settings.QFSSettings;

public class Entity {
	
	protected QFSProject qfsProject = QFSProject.getInstance();
	protected QFSSettings settings = qfsProject.settings;
	
	protected String name = "";
	protected Entity parent = null;
	protected ObjFileModel model;
	protected Material material;
	protected boolean overrideModelMaterial = false;
	
	// Swap between two positions buffers. One for reading and the other for writing.
	// So data will not be messed up when doing some interacting calculations.
	// After all has been nodes has been calculated, then copy the positionBuff to position.
	protected Vector3d position = new Vector3d();
	protected Vector3d positionBuff = new Vector3d();
	protected static int swapBufIndex = 0;

	protected Vector3d direction = new Vector3d(0, 0, 1);
	protected Vector3d rotation;
	protected Vector3d rotationRad = new Vector3d();
	protected double scale;
	private Matrix4d transformMatrix = new Matrix4d();
	protected Matrix4f[] transformMatrixf = new Matrix4f[2];

	protected boolean isVisible = true;
	protected boolean isSelected = false;
	protected boolean isHiLighted = false;
	protected static boolean isSimulating;
	
	
	public Entity(Entity parent, ObjFileModel model, Vector3d position, Vector3d rotation, double scale) {
		this.parent = parent;
		this.model = model;
		this.position = new Vector3d(position);
		this.positionBuff = new Vector3d(position);
		this.rotation = new Vector3d(rotation);
		this.scale = scale;
		transformMatrixf[0] = new Matrix4f();
		transformMatrixf[1] = new Matrix4f();
		
		if (model != null)
			material = new Material(model.getMaterial());
		else
			material = new Material();

		updateMatrix();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
	}
	
	/**
	 * This is a read only method.
	 * Use {@link #getPosiitonBuff()} to modify values.
	 * 
	 * @return position
	 */
	public Vector3d getPosition() {
		return position;
	}

	public Vector3d getPositionBuff() {
		return positionBuff;
	}

	public void setPosition(Vector3d positionBuff) {
		this.positionBuff.x = positionBuff.x;
		this.positionBuff.y = positionBuff.y;
		this.positionBuff.z = positionBuff.z;
	}

	public void setPosition(double x, double y, double z) {
		positionBuff.x = x;
		positionBuff.y = y;
		positionBuff.z = z;
	}
	
	public void incPosition(Vector3d p) {
		incPosition(p.x, p.y, p.z);
	}

	public void incPosition(double x, double y, double z) {
		positionBuff.x += x;
		positionBuff.y += y;
		positionBuff.z += z;
	}
	
	public void decPosition(double x, double y, double z) {
		positionBuff.x -= x;
		positionBuff.y -= y;
		positionBuff.z -= z;
	}
	
	public double setPositionDX(double dx) {
		return positionBuff.x += dx;
	}
	
	public double setPositionDY(double dy) {
		return positionBuff.y += dy;
	}
	
	public double setPositionDZ(double dz) {
		return positionBuff.z += dz;
	}
	
	public double setPositionX(double newPos) {
		return positionBuff.x = newPos;
	}
	
	public double setPositionY(double newPos) {
		return positionBuff.y = newPos;
	}
	
	public double setPositionZ(double newPos) {
		return positionBuff.z = newPos;
	}
	
	public void updatePosition() {
		position.x = positionBuff.x;
		position.y = positionBuff.y;
		position.z = positionBuff.z;
	}
	
	public Vector3d getDirection() {
		return direction;
	}

	public void setDirection(Vector3d direction) {
		this.direction.x = direction.x;
		this.direction.y = direction.y;
		this.direction.z = direction.z;
	}

	public Vector3d getRotation() {
		return rotation;
	}

	/**
	 * Values are in degrees.
	 * Range: 0..360
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setRotation(double x, double y, double z) {
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
		updateRotation();
	}
	
	/**
	 * Values are in degrees.
	 * Range: 0..360
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void incRotation(double x, double y, double z) {
		this.rotation.x += x;
		this.rotation.y += y;
		this.rotation.z += z;
		updateRotation();
	}
	
	/**
	 * Values are in degrees.
	 * Range: 0..360
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void decRotation(double x, double y, double z) {
		this.rotation.x -= x;
		this.rotation.y -= y;
		this.rotation.z -= z;
		updateRotation();
	}
	
	public Vector3d getRotationRad() {
		return rotationRad;
	}

	public void setRotationRad(Vector3d rotationRad) {
		this.rotationRad = rotationRad;
	}

	protected void updateRotation() {
		rotationRad.x = (double) Math.toRadians(rotation.x);
		rotationRad.y = (double) Math.toRadians(rotation.y);
		rotationRad.z = (double) Math.toRadians(rotation.z);
	}

	public ObjFileModel getModel() {
		return model;
	}

	public void setModel(ObjFileModel model) {
		this.model = model;
	}

	public boolean isOverrideModelMaterial() {
		return overrideModelMaterial;
	}

	/**
	 * If overrideModelMaterial is true then use the entity material,
	 * otherwise use material from the model.
	 * 
	 * This is useful, if each entity must have its own material.
	 * 
	 * @param overrideModelMaterial
	 */
	public void setOverrideModelMaterial(boolean overrideModelMaterial) {
		this.overrideModelMaterial = overrideModelMaterial;
	}

	public Material getMaterial() {
		if (overrideModelMaterial || model == null) return material;
		else return model.getMaterial();
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isHiLighted() {
		return isHiLighted;
	}

	public void setHiLighted(boolean isHiLighted) {
		this.isHiLighted = isHiLighted;
	}

	public static boolean isSimulating() {
		return isSimulating;
	}

	public static void setSimulating(boolean isSimulating) {
		Entity.isSimulating = isSimulating;
	}

	/**
	 * See {@link #getTransformMatrixf()}.
	 */
	public static void swapBuffer() {
		QFSNode.swapBufIndex = 1 - QFSNode.swapBufIndex;
	}

	public static Object getSwapBufferIndex() {
		return QFSNode.swapBufIndex;
	}

	public void setTransformMatrix(Matrix4d transformMatrix) {
		this.transformMatrix = transformMatrix;
	}

	public void updateMatrix() {
		updatePosition();
		updateRotation();
		
		getTransformMatrix()
		.identity()
		.translate(position)
		.setRotationXYZ(rotationRad.x, rotationRad.y, rotationRad.z)
		.scale(scale);
		
		if (parent != null)
			parent.getTransformMatrix().mul(getTransformMatrix(), getTransformMatrix());
		transformMatrixf[swapBufIndex].set(getTransformMatrix());
	}
	
	public Matrix4d getTransformMatrixd() {
		return getTransformMatrix();
	}

	/**
	 * The returned matrix is divided in two fold, enables to use parallel processing.</br></br>
	 * While one matrix is used for sending data to GPU,
	 * the other matrix can be used to prepare / processing new updated data for the next frame.</br></br>
	 * Use the method {@link #swapBuffer()} to swap between these matrices.</br></br>
	 * When all data for the next frame has been processed,
	 * call {@link #swapBuffer()} before sending data to the GPU.
	 * </br></br>
	 * @return the processed matrix buffer
	 */
	public Matrix4f getTransformMatrixf() {
		return transformMatrixf[1 - swapBufIndex];
	}

	public Matrix4d getTransformMatrix() {
		return transformMatrix;
	}

}
