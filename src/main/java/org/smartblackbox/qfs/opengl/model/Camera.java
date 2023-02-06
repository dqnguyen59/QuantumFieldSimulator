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

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.smartblackbox.qfs.settings.AppSettings;

public class Camera {
	
	private AppSettings appSettings = AppSettings.getInstance();

	private Vector3f position = new Vector3f(0, 0, 0);
	private Vector3f rotation = new Vector3f(0, 0, 0);
	
	private float fieldOfFiew = 45;
	private float z_near = 0.01f;
	private float z_far = 1000f;

	private Matrix4f viewMatrix = new Matrix4f();
	private Matrix4f projectionMatrix = new Matrix4f();

	public Camera() {
	}
	
	public Camera(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
	}
	
	public float getFieldOfFiew() {
		return fieldOfFiew;
	}

	public void setFieldOfFiew(float fieldOfFiew) {
		this.fieldOfFiew = fieldOfFiew;
		updateProjectionMatrix();
	}

	public float getZ_near() {
		return z_near;
	}

	public void setZ_near(float z_near) {
		this.z_near = z_near;
		updateProjectionMatrix();
	}

	public float getZ_far() {
		return z_far;
	}

	public void setZ_far(float z_far) {
		this.z_far = z_far;
		updateProjectionMatrix();
	}

	public void movePosition(float x, float y, float z) {
		if (z != 0) {
			position.x += Math.sin(Math.toRadians(rotation.y)) * -1.0f * z;
			position.z += Math.cos(Math.toRadians(rotation.y)) * z;
		}
		if (x != 0) {
			position.x += Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * x;
			position.z += Math.cos(Math.toRadians(rotation.y - 90)) * x;
		}
		
		position.y += y;
	}
	
	public void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}
	
	public void moveRotation(float x, float y, float z) {
		this.rotation.x += x;
		this.rotation.y += y;
		this.rotation.z += z;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(float x, float y, float z) {
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
	}

	public Matrix4f getViewMatrix() {
		return viewMatrix.identity()
		.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
		.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
		.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1))
		.translate(-position.x, -position.y, -position.z);
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Matrix4f updateProjectionMatrix() {
		float aspectRatio = (float) appSettings.getWindowWidth() / appSettings.getWindowHeight();
		return projectionMatrix.setPerspective(
				(float) Math.toRadians(fieldOfFiew),
				aspectRatio,
				z_near,
				z_far
		);
	}

}
