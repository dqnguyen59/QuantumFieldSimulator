/*
 * Copyright (C) 2023 Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org> and contributors
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <https://www.gnu.org/licenses/>.
 * 
 * File created on 01/01/2023
 */
package org.smartblackbox.qfs.opengl.model.entity;

import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.smartblackbox.qfs.opengl.model.Material;
import org.smartblackbox.qfs.opengl.model.ObjFileModel;
import org.smartblackbox.qfs.opengl.model.QFSModel;
import org.smartblackbox.qfs.opengl.model.Scene;
import org.smartblackbox.qfs.opengl.utils.Neighbor;
import org.smartblackbox.qfs.settings.AppSettings;
import org.smartblackbox.qfs.settings.QFSProject;

public class QFSNode extends Entity {

	private static final Vector3d FIXED_COLOR = new Vector3d(1f, 1f, 1f);

	private AppSettings appSettings = AppSettings.getInstance();
	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel();
	private Scene scene = qfsProject.scene;

	/*
	 * 0: neighbors right 1: neighbors top 2: neighbors front 3: neighbors left 4:
	 * neighbors bottom 5: neighbors back
	 * 
	 */
	private QFSNode[] neighbors = new QFSNode[6];
	private boolean hasNeightbors;
	private boolean isFixed;
	private boolean isXFixed;
	private boolean isYFixed;
	private boolean isZFixed;
	private boolean isWall;

	private Vector3d fixedPos;
	private AV avp = new AV();
	private AV avr = new AV();
	// private Vector3d acceleration = new Vector3d();
	// private Vector3d velocity = new Vector3d();
	private Vector3i index = new Vector3i(-1);
	private double customScale = -1;
	private Vector4f customColor;

	private double reflection;

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

	@Override
	public void setPosition(Vector3d position) {
		super.setPosition(position);
		if (fixedPos == null)
			fixedPos = new Vector3d();
		fixedPos.set(position);
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

	public boolean isXFixed() {
		return isXFixed;
	}

	public void setXFixed(boolean isXFixed) {
		this.isXFixed = isXFixed;
	}

	public boolean isYFixed() {
		return isYFixed;
	}

	public void setYFixed(boolean isYFixed) {
		this.isYFixed = isYFixed;
	}

	public boolean isZFixed() {
		return isZFixed;
	}

	public void setZFixed(boolean isZFixed) {
		this.isZFixed = isZFixed;
	}

	public boolean isWall() {
		return isWall;
	}

	public void setWall(boolean isWall) {
		this.isWall = isWall;
	}

	public Vector3d getVelocity() {
		return avp.v;
		// return velocity;
	}

	public void setVelocity(Vector3d velocity) {
		avp.v.set(velocity);
		// this.velocity = velocity;
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

	public double getReflection() {
		return reflection;
	}

	public void setReflection(float reflection) {
		this.reflection = reflection;
	}

	@Override
	public void updateMatrix() {
		updatePosition();
		updateRotation();

		getTransformMatrix().identity().translate(pos).setRotationXYZ(rotRad.x, rotRad.y, rotRad.z)
				.scale(settings.scale).scale(customScale > 0 ? customScale : scale);

		if (parent != null)
			parent.getTransformMatrix().mul(getTransformMatrix(), getTransformMatrix());
		transformMatrixfList[swapBufIndex].set(getTransformMatrix());
		customScale = -1;
	}

	private synchronized void setAccelerationColor(Vector3d acceleration) {
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
		} else if (isSelected && !scene.isAnimated()) {
			color.x = settings.selectedColor.x * 2.0f;
			color.y = settings.selectedColor.y * 2.0f;
			color.z = settings.selectedColor.z * 2.0f;
			color.w = settings.selectedColor.w * 2.0f;
			alpha = 1.0f;
		} else if (isHiLighted) {
			color.x = settings.hiLightColor.x;
			color.y = settings.hiLightColor.y;
			color.z = settings.hiLightColor.z;
			color.w = settings.hiLightColor.w;
			alpha = color.w;
		} else if (isWall && reflection == 1.0) {
			color.x = settings.wallColor.x;
			color.y = settings.wallColor.y;
			color.z = settings.wallColor.z;
			color.w = settings.wallColor.w;
			alpha = color.w;
		} else if (isFixed && reflection == 1.0) {
			color.x = settings.fixedColor.x;
			color.y = settings.fixedColor.y;
			color.z = settings.fixedColor.z;
			color.w = settings.fixedColor.w;
			alpha = color.w;
		} else {
			switch (settings.getColorMode()) {
			case zColor:
				color.x = (float) (Math.max(0, -acceleration.z) * intensity);
				color.y = (float) (Math.abs(acceleration.y) * intensity);
				color.z = (float) (Math.max(0, acceleration.z) * intensity);
				color.w = 0;
				break;
			case zColor2:
				color.x = (float) (Math.max(0, -acceleration.z) * intensity2);
				color.y = (float) (Math.abs(acceleration.y) * intensity2);
				color.z = (float) (Math.max(0, acceleration.z) * intensity2);
				color.w = 0;
				break;
			case zColor3:
				color.x = (float) (Math.max(0, -acceleration.z) * Math.max(0, -acceleration.z) * intensity3);
				color.y = (float) (Math.abs(acceleration.y * acceleration.y) * intensity3);
				color.z = (float) (Math.max(0, acceleration.z) * Math.max(0, acceleration.z) * intensity3);
				color.w = 0;
				break;
			case xyzColor:
				color.x = (float) (Math.abs(acceleration.x)) * intensity;
				color.y = (float) (Math.abs(acceleration.y)) * intensity;
				color.z = (float) (Math.abs(acceleration.z)) * intensity;
				color.w = 0;
				break;
			case xyzColor2:
				color.x = (float) (Math.abs(acceleration.x) * intensity2);
				color.y = (float) (Math.abs(acceleration.y) * intensity2);
				color.z = (float) (Math.abs(acceleration.z) * intensity2);
				color.w = 0;
				break;
			case xyzColor3:
				color.x = (float) (acceleration.x * acceleration.x) * intensity3;
				color.y = (float) (acceleration.y * acceleration.y) * intensity3;
				color.z = (float) (acceleration.z * acceleration.z) * intensity3;
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
			alpha = (float) (alphaIntensity * (m.getDiffuseColor().length()));
		}

		m.setAmbientColor(color);
		m.getDiffuseColor().w = alpha;
		m.getAmbientColor().w = m.getDiffuseColor().w;
		m.setShininess(settings.getShininess() * alpha);

		customColor = null;
	}

	/**
	 * Note that this is not the full quantum field formula, only the electric field
	 * is simulated.
	 * 
	 * Some definitions:
	 * 
	 * t = time a = acceleration v = node velocity N = neighbor position
	 * array[Right, Up, Front, Left, Down, Back] p = position (variable
	 * positionBuff) r = radiation (wave will fade when radiation < 1) k = constant
	 * light speed {k ∈ [0..1]} this constant is responsible for the speed of light.
	 * Meaning, changing this constant would change the speed of light. Decreasing
	 * this constant will result in a lower speed of light.
	 * 
	 * The equation of Quantum Field is simply based on motion formula:
	 * 
	 * ∆v = avg(N) = k² / |N| * ∑(n – p) {n ∈ N} a = ∆v / ∆t v = v + a * t x = x0 +
	 * v0 * t + 1/2 a * t² v = v * r
	 * 
	 * Note that v is not the speed of light, but is actually the speed of the node
	 * itself. The propagation of the light wave is caused by the motion of the node
	 * and the variable k determines the speed of light.
	 * 
	 * If ∆t is one frame and one frame is one Planck time unit, then the short
	 * formula can be written as:
	 * 
	 * a = avg(N) = k² / |N| * ∑(n – p) {n ∈ N} v = (v + a) * r p = p + v + a * 0.5
	 * 
	 * A node can only have six neighbors in the field matrix, then the length of
	 * the array |N| is 6.
	 * 
	 * Remarks: the higher the k value the higher the speed of light, but k cannot
	 * exceed 1. If k is greater than 1, the affected nodes become unstable and
	 * eventually fly away.
	 * 
	 */
	public void updateNodePropertiesV1_0() {
		if (!hasNeightbors || isFixed) {
			setAccelerationColor(new Vector3d(1f, 1f, 1f));
			return;
		}

		if (qfsModel.isSimulating()) {
			avp.a.set(0);
			for (QFSNode neighbor : neighbors) {
				avp.a.add(neighbor.pos).sub(pos);
			}

			// Note that the result of the addition or multiplication is stored in the
			// object variable itself.
			posBuff.add(avp.v.add(avp.a.mul(qfsProject.getConstantWaveSpeed() / 6.0))
					.mul(qfsProject.getConstantRadiation())).add(avp.a.mul(0.5));
		}

		setAccelerationColor(avp.a);
	}

	/**
	 * Note that this is not the full quantum field formula, only the electric field
	 * is simulated.<br/>
	 * <br/>
	 * Some definitions:<br/>
	 * <p style="margin-left: 30px;">
	 * t = time <br/>
	 * a = acceleration <br/>
	 * v = node velocity <br/>
	 * N = neighbor position array[Right, Up, Front, Left, Down, Back] <br/>
	 * p = position (variable positionBuff) <br/>
	 * r = radiation (wave will fade when radiation < 1) <br/>
	 * k = constant light speed {k ∈ [0..1]} <br/>
	 * this constant is responsible for the speed of light. <br/>
	 * Meaning, changing this constant would change the speed of light. <br/>
	 * Decreasing this constant will result in a lower speed of light. <br/>
	 * </p>
	 * <br/>
	 * The equation of Quantum Field is simply based on motion formula:<br/>
	 * <p style="margin-left: 30px;">
	 * ∆v = avg(N) = k² / |N| * ∑(n – p) {n ∈ N} <br/>
	 * a = ∆v / ∆t <br/>
	 * v = v + a * t <br/>
	 * x = x0 + v0 * t + 1/2 a * t² <br/>
	 * </p>
	 * <br/>
	 * Note that v is not the speed of light, but is actually the speed of the node
	 * itself. The propagation of the light wave is caused by the motion of the node
	 * and the variable k determines the speed of light.<br/>
	 * <br/>
	 * If ∆t is one frame and one frame is one Planck time unit, then the short
	 * formula can be written as:<br/>
	 * <p style="margin-left: 30px;">
	 * a = avg(N) = k² / |N| * ∑(n – p) {n ∈ N} <br/>
	 * v = nVel(v, a) = (v + a) * r <br/>
	 * p = nPos(v, p, a) = p + v + a * 0.5 <br/>
	 * </p>
	 * <br/>
	 * A node can only have six neighbors in the field matrix, then the length of
	 * the array |N| is 6.<br/>
	 * <br/>
	 * <b>Remarks:</b> The higher the k value the higher the speed of light, but k
	 * cannot exceed 1. If k is greater than 1, the affected nodes become unstable
	 * and eventually fly away.<br/>
	 * <br/>
	 * 
	 */
	public void updateNodePropertiesV1_1() {
		avp.a.set(0);

		for (QFSNode neighbor : neighbors) {
			if (neighbor != null) {
				// if (false && (isFixed || isWall)) {
				// // double alpha = 1.0 - (isWall? reflection :
				// appSettings.getReflectionFixedNodes());
				//
				// if ((isXFixed || isFixed && appSettings.isUseFixedNodes()) || isWall) {
				// acceleration.y += (neighbor.position.y - position.y) * alpha;
				// acceleration.z += (neighbor.position.z - position.z) * alpha;
				// }
				// if ((isYFixed || isFixed && appSettings.isUseFixedNodes()) || isWall) {
				// acceleration.x += (neighbor.position.x - position.x) * alpha;
				// acceleration.z += (neighbor.position.z - position.z) * alpha;
				// }
				// if ((isZFixed || isFixed && appSettings.isUseFixedNodes()) || isWall) {
				// acceleration.x += (neighbor.position.x - position.x) * alpha;
				// acceleration.y += (neighbor.position.y - position.y) * alpha;
				// }
				// } else {
				// acceleration.add(neighbor.position).sub(position).mul(alpha);
				// }
				if (isFixed || isWall) {
					double alpha = 1.0 - (isWall ? reflection : appSettings.getReflectionFixedNodes());

					if (isFixed || isWall) {
						if (alpha == 0) {
							avp.v.set(0);
							posBuff.set(fixedPos);
						}
					}

					avp.a.x += (neighbor.pos.x - pos.x) * alpha;
					avp.a.y += (neighbor.pos.y - pos.y) * alpha;
					avp.a.z += (neighbor.pos.z - pos.z) * alpha;
				} else {
					avp.a.add(neighbor.pos).sub(pos);
				}
				// acceleration.add(neighbor.position).sub(position).mul(alpha);
			}
		}

		// Note that the result of the addition or multiplication is stored in the
		// object variable itself.
		posBuff.add(avp.v.add(avp.a.mul(qfsProject.getConstantWaveSpeed() / 6)).mul(qfsProject.getConstantRadiation()))
				.add(avp.a.mul(0.5));

		if ((isXFixed || isFixed && appSettings.isUseFixedNodes()))
			posBuff.x = fixedPos.x;
		if ((isYFixed || isFixed && appSettings.isUseFixedNodes()))
			posBuff.y = fixedPos.y;
		if ((isZFixed || isFixed && appSettings.isUseFixedNodes()))
			posBuff.z = fixedPos.z;

		setAccelerationColor(isFixed ? FIXED_COLOR : avp.a);
	}

	/**
	 * This formula enables to calculate absorptions.
	 * 
	 * Note that this is not the full quantum field formula, only the electric field
	 * is simulated.<br/>
	 * <br/>
	 * Some definitions:<br/>
	 * <p style="margin-left: 30px;">
	 * t = time <br/>
	 * a = acceleration <br/>
	 * v = node velocity <br/>
	 * N = neighbor position array[Right, Up, Front, Left, Down, Back] <br/>
	 * p = position (variable positionBuff) <br/>
	 * r = radiation (wave will fade when radiation < 1) <br/>
	 * k = constant light speed {k ∈ [0..1]} <br/>
	 * this constant is responsible for the speed of light. <br/>
	 * Meaning, changing this constant would change the speed of light. <br/>
	 * Decreasing this constant will result in a lower speed of light. <br/>
	 * </p>
	 * <br/>
	 * The equation of Quantum Field is simply based on motion formula:<br/>
	 * <p style="margin-left: 30px;">
	 * ∆v = avg(N) = k² / |N| * ∑(n – p) {n ∈ N} <br/>
	 * a = ∆v / ∆t <br/>
	 * v = v + a * t <br/>
	 * x = x0 + v0 * t + 1/2 a * t² <br/>
	 * </p>
	 * <br/>
	 * Note that v is not the speed of light, but is actually the speed of the node
	 * itself. The propagation of the light wave is caused by the motion of the node
	 * and the variable k determines the speed of light.<br/>
	 * <br/>
	 * If ∆t is one frame and one frame is one Planck time unit, then the short
	 * formula can be written as:<br/>
	 * <p style="margin-left: 30px;">
	 * a = avg(N) = k² / |N| * ∑(n – p) {n ∈ N} <br/>
	 * v = nVel(v, a) = (v + a) * r <br/>
	 * p = nPos(v, p, a) = p + v + a * 0.5 <br/>
	 * </p>
	 * <br/>
	 * A node can only have six neighbors in the field matrix, then the length of
	 * the array |N| is 6.<br/>
	 * <br/>
	 * <b>Remarks:</b> The higher the k value the higher the speed of light, but k
	 * cannot exceed 1. If k is greater than 1, the affected nodes become unstable
	 * and eventually fly away.<br/>
	 * <br/>
	 * 
	 */
	public void updateNodePropertiesV1_2() {
		avp.a.set(0);

		for (QFSNode neighbor : neighbors) {
			if (neighbor != null) {
				if (isFixed || isWall) {
					// Absorptions alpha
					double alpha = 1.0 - (isWall ? reflection : appSettings.getReflectionFixedNodes());

					if (alpha == 0) {
						avp.v.set(0);
						posBuff.set(fixedPos);
					}

					avp.a.add(neighbor.pos).add(neighbor.pos).sub(pos).sub(neighbor.posBuff).mul(alpha);
				} else {
					avp.a.add(neighbor.pos).sub(pos);
				}
			}
		}

		// Note that the result of the addition or multiplication is stored in the
		// object variable itself.
		posBuff.add(avp.v.add(avp.a.mul(qfsProject.getConstantWaveSpeed() / 6)).mul(qfsProject.getConstantRadiation()))
				.add(avp.a.mul(0.5));

		if ((isXFixed || isFixed && appSettings.isUseFixedNodes()))
			posBuff.x = fixedPos.x;
		if ((isYFixed || isFixed && appSettings.isUseFixedNodes()))
			posBuff.y = fixedPos.y;
		if ((isZFixed || isFixed && appSettings.isUseFixedNodes()))
			posBuff.z = fixedPos.z;

		lastPos.set(posBuff);

		setAccelerationColor(isFixed ? FIXED_COLOR : avp.a);
	}

	public void updateNodePropertiesV1_3() {
		avr.a.set(0);
		avp.a.set(0);

		for (QFSNode n : neighbors) {
			if (n != null) {
				Vector3d ar;
				Vector3d ap;
				if (isFixed || isWall) {
					// Absorptions alpha
					double alpha = 1.0 - (isWall ? reflection : appSettings.getReflectionFixedNodes());
					ar = (new Vector3d()).add(n.rot).add(n.rot).sub(rot).sub(n.rotBuff).mul(alpha);
					ap = (new Vector3d()).add(n.pos).sub(pos).sub(n.posBuff).mul(alpha);
				} else {
					ar = (new Vector3d()).add(n.rot).sub(rot).mul(2.0);
					ap = (new Vector3d()).add(n.pos).sub(pos);
//					if (n.help != null) {
//						n.help.setPosition(ap);
//
//						Vector4d nodePos1 = new Vector4d();
//						Vector4d nodePos2 = new Vector4d();
//						getTransformMatrixd().transform(0, 0, 0, 1, nodePos1);
//						n.help.getTransformMatrixd().transform(0, 0, 0, 1, nodePos2);
//						ap.sub((new Vector3d(nodePos1.x, nodePos1.y, nodePos1.z))
//								.sub(new Vector3d(nodePos2.x, nodePos2.y, nodePos2.z)).mul(0.001));
//					}
				}
				avp.a.add(ap);// .add(ar.y, ar.z, ar.x);
				double f = 10.0;
				avr.a.add(ar).sub(ap.x * f, ap.y * f, ap.z * f);
			}
		}

		double k = qfsProject.getConstantWaveSpeed() / 6;
		double r = qfsProject.getConstantRadiation();

		// Note that the result of the addition or multiplication is stored in the
		// object variable itself.
		rotBuff.add(avr.v.add(avr.a.mul(k)).mul(r)).add(avr.a.mul(0.5));
		posBuff.add(avp.v.add(avp.a.mul(k)).mul(r)).add(avp.a.mul(0.5));

		if ((isXFixed || isFixed && appSettings.isUseFixedNodes())) {
			posBuff.x = fixedPos.x;
		}
		if ((isYFixed || isFixed && appSettings.isUseFixedNodes())) {
			posBuff.y = fixedPos.y;
		}
		if ((isZFixed || isFixed && appSettings.isUseFixedNodes())) {
			posBuff.z = fixedPos.z;
		}

		lastRot.set(rotBuff);
		lastPos.set(posBuff);

		setAccelerationColor(isFixed ? FIXED_COLOR : avp.a);
	}
}
