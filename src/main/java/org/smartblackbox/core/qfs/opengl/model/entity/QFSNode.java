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
package org.smartblackbox.core.qfs.opengl.model.entity;

import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.smartblackbox.core.qfs.opengl.model.Material;
import org.smartblackbox.core.qfs.opengl.model.ObjFileModel;
import org.smartblackbox.core.qfs.opengl.model.QFSModel;
import org.smartblackbox.core.qfs.opengl.utils.Neighbor;
import org.smartblackbox.core.qfs.settings.QFSProject;

public class QFSNode extends Entity {
	
	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel();
	
	/*
	 * 0: neighbors right
	 * 1: neighbors top
	 * 2: neighbors front
	 * 3: neighbors left
	 * 4: neighbors bottom
	 * 5: neighbors back
	 * 
	 */
	private QFSNode[] neighbors = new QFSNode[6];
	private boolean hasNeightbors;
	private boolean isFixed = false;
	private boolean isWall = false;
	
	private Vector3d acceleration = new Vector3d();
	private Vector3d velocity = new Vector3d();
	private Vector3i index = new Vector3i(-1);
	private double customScale = -1;
	private Vector4f customColor;

	public QFSNode(Entity parent, ObjFileModel model, Vector3d position, Vector3d rotation, double scale) {
		super(parent, model, position, rotation, scale);
	}
	
	public Vector3i getIndex() {
		return index;
	}

	public void setIndex(Vector3i index) {
		this.index = index;
	}

	public boolean isIndex(int x, int y, int z) {
		return index.x == x && index.y == y && index.z == z;
	}

	public boolean isIndex(Vector3i index) {
		return isIndex(index.x, index.y, index.z);
	}

	public void setIndex(int x, int y, int z) {
		index.x = x;
		index.y = y;
		index.z = z;
	}
	
	public String printIndex() {
		return String.format("(%d, %d, %d)", index.x, index.y, index.z);
	}

	public QFSNode[] getNeighbors() {
		return neighbors;
	}

	public void setNeighbor(int index, QFSNode node) {
		this.neighbors[index] = node;
		hasNeightbors = true;
	}

	public void setNeighbor(Neighbor neighbor, QFSNode node) {
		this.neighbors[neighbor.ordinal()] = node;
		hasNeightbors = true;
	}

	public boolean isHasNeightbors() {
		return hasNeightbors;
	}

	public void setHasNeightbors(boolean hasNeightbors) {
		this.hasNeightbors = hasNeightbors;
	}

	@Override
	public boolean isVisible() {
		return isVisible || isWall && qfsModel.isWallVisible();
	}

	public boolean isFixed() {
		return isFixed;
	}

	public void setFixed(boolean isFixed) {
		this.isFixed = isFixed;
	}

	public boolean isWall() {
		return isWall;
	}

	public void setWall(boolean isWall) {
		this.isWall = isWall;
	}

	public Vector3d getForce() {
		return acceleration;
	}

	public Vector3d getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3d velocity) {
		this.velocity = velocity;
	}

	/**
	 * Custom scale must be set before calling updateMatrix().
	 * 
	 * Custom scale will be reset after updateMatrix() is called.
	 * 
	 * @param customScale
	 */
	public void setCustomScale(double customScale) {
		this.customScale = customScale;		
	}

	/**
	 * Custom color must be set before calling calcNewPosition().
	 * 
	 * Custom color will be null after calcNewPosition() is called.
	 * 
	 * @param color
	 */
	public void setCustomColor(Vector4f color) {
		customColor = color;
	}

	@Override
	public void updateMatrix() {
		updatePosition();
		updateRotation();
		
		getTransformMatrix()
		.identity()
		.translate(position).
		setRotationXYZ(rotationRad.x, rotationRad.y, rotationRad.z)
		.scale(settings.scale)
		.scale(customScale > 0? customScale : scale);

		if (parent != null)
			parent.getTransformMatrix().mul(getTransformMatrix(), getTransformMatrix());
		transformMatrixf[swapBufIndex].set(getTransformMatrix());
		customScale = -1;
	}

	private void setAccelerationColor(Vector3d force) {
		Material m = getMaterial(); 
		
		Vector4f color = m.getDiffuseColor();
		float alpha = 0;
		float intensity = settings.getIntensity();
		float intensity2 = intensity * intensity;
		float intensity3 = intensity2 * intensity;
		float alphaIntensity = settings.getAlpha();
		
		if (customColor != null) {
			color.x = customColor.x * 2.0f;
			color.y = customColor.y * 2.0f;
			color.z = customColor.z * 2.0f;
			color.w = customColor.w * 2.0f;
			alpha = 1.0f;
		}
		else if (isSelected) {
			color.x = settings.selectedColor.x * 2.0f;
			color.y = settings.selectedColor.y * 2.0f;
			color.z = settings.selectedColor.z * 2.0f;
			color.w = settings.selectedColor.w * 2.0f;
			alpha = 1.0f;
		}
		else if (isHiLighted) {
			color.x = settings.hiLightColor.x;
			color.y = settings.hiLightColor.y;
			color.z = settings.hiLightColor.z;
			color.w = settings.hiLightColor.w;
			alpha = color.w;
		}
		else if (isWall) {
			color.x = settings.wallColor.x;
			color.y = settings.wallColor.y;
			color.z = settings.wallColor.z;
			color.w = settings.wallColor.w;
			alpha = color.w;
		}
		else if (isFixed) {
			color.x = settings.fixedColor.x;
			color.y = settings.fixedColor.y;
			color.z = settings.fixedColor.z;
			color.w = settings.fixedColor.w;
			alpha = color.w;
		}
		else {
			switch (settings.getColorMode()) {
			case zColor:
				color.x = (float) (Math.max(0, -force.z) * intensity);
				color.y = (float) (Math.abs(force.y) * intensity);
				color.z = (float) (Math.max(0, force.z) * intensity);
				color.w = 0;
				break;
			case zColor2:
				color.x = (float) (Math.max(0, -force.z) * intensity2);
				color.y = (float) (Math.abs(force.y) * intensity2);
				color.z = (float) (Math.max(0, force.z) * intensity2);
				color.w = 0;
				break;
			case zColor3:
				color.x = (float) (Math.max(0, -force.z) * Math.max(0, -force.z) * intensity3);
				color.y = (float) (Math.abs(force.y * force.y) * intensity3);
				color.z = (float) (Math.max(0, force.z) * Math.max(0, force.z) * intensity3);
				color.w = 0;
				break;
			case xyzColor:
				color.x = (float) (Math.abs(force.x)) * intensity;
				color.y = (float) (Math.abs(force.y)) * intensity;
				color.z = (float) (Math.abs(force.z)) * intensity;
				color.w = 0;
				break;
			case xyzColor2:
				color.x = (float) (Math.abs(force.x) * intensity2);
				color.y = (float) (Math.abs(force.y) * intensity2);
				color.z = (float) (Math.abs(force.z) * intensity2);
				color.w = 0;
				break;
			case xyzColor3:
				color.x = (float) (force.x * force.x) * intensity3;
				color.y = (float) (force.y * force.y) * intensity3;
				color.z = (float) (force.z * force.z) * intensity3;
				color.w = 0;
				break;
			case normal:
			default:
				color.x = settings.defaultNodeColor.x * (float) intensity / 100.0f;
				color.y = settings.defaultNodeColor.y * (float) intensity / 100.0f;
				color.z = settings.defaultNodeColor.z * (float) intensity / 100.0f;
				color.w = settings.defaultNodeColor.w * (float) alphaIntensity;
				alphaIntensity = color.w;
				break;
			}
			alpha = (float) (alphaIntensity * (m.getDiffuseColor().length()));// * 0.1);
		}
		
		m.setAmbientColor(color);
		m.getDiffuseColor().w = alpha;
		m.getAmbientColor().w = m.getDiffuseColor().w;
		m.setShininess(settings.getShininess());
		
		customColor = null;
	}

	/**
	 * Note that this is not the full quantum field formula, only the electric field is simulated. 
	 * 
	 * Some definitions:
	 * 
	 * 		t = time
	 * 		a = acceleration
	 * 		v = node velocity
	 * 		N = neighbor position array[Right, Up, Front, Left, Down, Back]
	 * 		p = position (variable positionBuff)
	 * 		r = radiation (wave will fade when radiation < 1)
	 * 		k = constant light speed {k ∈ [0..1]}
	 * 			this constant is responsible for the speed of light.
	 * 			Meaning, changing this constant would change the speed of light.
	 * 			Decreasing this constant will result in a lower speed of light.
	 * 
	 * The equation of Quantum Field is simply based on motion formula:
	 * 
	 * 	   ∆v = avg(N) = k² / |N|  ∑(n – p) {n ∈ N}
	 * 		a = ∆v / ∆t
	 * 		v = v + a * ∆t				
	 * 		p = p + v * ∆t + 1/2 a * ∆t²				
	 * 		v = v * r
	 * 
	 * Note that v is not the speed of light, but is actually the speed of the node itself.
	 * The propagation of the light wave is caused by the motion of the node
	 * and the variable k determines the speed of light.
	 * 
	 * If ∆t is one frame and one frame is one Planck time unit, then the short formula can be written as: 
	 * 
	 * 		a = avg(N) = k² / |N|  ∑(n – p) {n ∈ N}
	 * 		v = v + a
	 * 		p = p + v + a * 0.5
	 * 		v = v * r
	 * 
	 * A node can only have six neighbors in the field matrix, then the length of the array |N| is 6.
	 * 
	 * Remarks: the higher the k value the higher the speed of light, but k cannot exceed 1.
	 * If k is greater than 1, the affected nodes become unstable and eventually fly away.
	 * 
	 */
	public void calcNewPosition(boolean updatePosition) {
		if (!hasNeightbors || isFixed) {
			setAccelerationColor(new Vector3d(1f, 1f, 1f));
			return;
		}

		acceleration.set(0);
		if (updatePosition) {
			for (QFSNode neighbor : neighbors) {
				acceleration.add(neighbor.position).sub(position);
			}
			
			// Note that the result of the addition or multiplication is stored in the object variable itself.
			// Fortunately, the formula fits pretty well on one line.
			positionBuff.add(velocity.add(acceleration.mul(qfsProject.getConstantLightSpeed() / 6.0)).mul(qfsProject.getRadiation())).add(acceleration.mul(0.5));
		}

		setAccelerationColor(acceleration);
	}

}
