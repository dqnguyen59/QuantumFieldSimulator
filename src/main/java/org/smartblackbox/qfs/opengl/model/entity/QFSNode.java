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
	
	private final AppSettings appSettings = AppSettings.getInstance();
	private final QFSProject qfsProject = QFSProject.getInstance();
	private final QFSModel qfsModel = qfsProject.getQfsModel();
	private final Scene scene = qfsProject.scene;

    private static final int NEIGHBOR_RIGHT = 0;
    private static final int NEIGHBOR_TOP = 1;
    private static final int NEIGHBOR_FRONT = 2;
    private static final int NEIGHBOR_LEFT = 3;
    private static final int NEIGHBOR_BOTTOM = 4;
    private static final int NEIGHBOR_BACK = 5;

    /*
     * 0: neighbors right
     * 1: neighbors top
     * 2: neighbors front
     * 3: neighbors left
     * 4: neighbors bottom
     * 5: neighbors back
     */
	private final QFSNode[] neighbors = new QFSNode[6];
	private boolean hasNeighbors;
	private boolean isFixed;
	private boolean isXFixed;
	private boolean isYFixed;
	private boolean isZFixed;
	private boolean isWall;
	
	private Vector3d fixedPosition;
    private final Vector3d ef = new Vector3d();
	private final Vector3d mf = new Vector3d();
	private final Vector3d acceleration = new Vector3d();
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

	@Override
	public void setPosition(Vector3d position) {
		super.setPosition(position);
		if (fixedPosition == null) fixedPosition = new Vector3d();
		fixedPosition.set(position);
	}
	
	public QFSNode[] getNeighbors() {
		return neighbors;
	}

    public void setNeighbor(Neighbor neighbor, QFSNode node) {
		this.neighbors[neighbor.ordinal()] = node;
		hasNeighbors = true;
	}

	public boolean isHasNeightbors() {
		return hasNeighbors;
	}

	public void setHasNeighbors(boolean hasNeighbors) {
		this.hasNeighbors = hasNeighbors;
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
		transformMatrixfList[swapBufIndex].set(getTransformMatrix());
		customScale = -1;
	}

	private synchronized void setEMFieldColor(Vector3d emf) {
		Material m = getMaterial(); 
		
		Vector4f color = m.getDiffuseColor();
		float alpha = 0;
		float intensity = settings.getIntensity();
		float intensity2 = intensity * intensity;
		float intensity3 = intensity2 * intensity;
		float alphaIntensity = settings.getAlpha();
		
		if (customColor != null) {
            color.set(customColor).mul(2.0f);
			alpha = 1.0f;
		}
		else if (isSelected && !scene.isAnimated()) {
            color.set(settings.selectedColor).mul(2.0f);
			alpha = 1.0f;
		}
		else if (isHiLighted) {
            color.set(settings.hiLightColor).mul(2.0f);
			alpha = color.w;
		}
		else if (isWall) {
            color.set(settings.wallColor).mul(2.0f);
			alpha = color.w;
		}
		else if (isFixed) {
            color = settings.fixedColor;
			alpha = color.w;
		}
		else {
			switch (settings.getColorMode()) {
			case zColor:
				color.x = (float) (Math.max(0, -emf.z) * intensity);
				color.y = (float) (Math.abs(emf.y) * intensity);
				color.z = (float) (Math.max(0, emf.z) * intensity);
				color.w = 0;
				break;
			case zColor2:
				color.x = (float) (Math.max(0, -emf.z) * intensity2);
				color.y = (float) (Math.abs(emf.y) * intensity2);
				color.z = (float) (Math.max(0, emf.z) * intensity2);
				color.w = 0;
				break;
			case zColor3:
				color.x = (float) (Math.max(0, -emf.z) * Math.max(0, -emf.z) * intensity3);
				color.y = (float) (Math.abs(emf.y * emf.y) * intensity3);
				color.z = (float) (Math.max(0, emf.z) * Math.max(0, emf.z) * intensity3);
				color.w = 0;
				break;
			case xyzColor:
				color.x = (float) (Math.abs(emf.x)) * intensity;
				color.y = (float) (Math.abs(emf.y)) * intensity;
				color.z = (float) (Math.abs(emf.z)) * intensity;
				color.w = 0;
				break;
			case xyzColor2:
				color.x = (float) (Math.abs(emf.x) * intensity2);
				color.y = (float) (Math.abs(emf.y) * intensity2);
				color.z = (float) (Math.abs(emf.z) * intensity2);
				color.w = 0;
				break;
			case xyzColor3:
				color.x = (float) (emf.x * emf.x) * intensity3;
				color.y = (float) (emf.y * emf.y) * intensity3;
				color.z = (float) (emf.z * emf.z) * intensity3;
				color.w = 0;
				break;
			case normal:
			default:
				color.x = settings.defaultNodeColor.x * intensity / 100.0f;
				color.y = settings.defaultNodeColor.y * intensity / 100.0f;
				color.z = settings.defaultNodeColor.z * intensity / 100.0f;
				color.w = settings.defaultNodeColor.w * alphaIntensity;
				alphaIntensity = color.w;
				break;
			}
			alpha = (alphaIntensity * (m.getDiffuseColor().length()));
		}
		
		m.setAmbientColor(color);
		m.getDiffuseColor().w = alpha;
		m.getAmbientColor().w = m.getDiffuseColor().w;
		m.setShininess(settings.getShininess() * alpha);
		
		customColor = null;
	}

	/**
	 * Note that this is not the full quantum field formula, only the electric field is simulated.<br/>
	 * <br/>
	 * Some definitions:<br/>
	 * <p style="margin-left: 30px;">
	 * 		t = time																<br/>
	 * 		a = acceleration														<br/>
	 * 		v = node velocity														<br/>
	 * 		N = neighbor position array[Right, Up, Front, Left, Down, Back]			<br/>
	 * 		p = position (variable positionBuff)									<br/>
	 * 		r = radiation (wave will fade when radiation < 1)						<br/>
	 * 		k = constant light speed {k ∈ [0..1]}									<br/>
	 * 			this constant is responsible for the speed of light.				<br/>
	 * 			Meaning, changing this constant would change the speed of light.	<br/>
	 * 			Decreasing this constant will result in a lower speed of light.		<br/>
	 * </p>
	 * <br/>
	 * The equation of Quantum Field is simply based on motion formula:<br/>
	 * <p style="margin-left: 30px;">
	 * 	   ∆v = avg(N) = k² / |N| * ∑(n – p) {n ∈ N}	<br/>
	 * 		a = ∆v / ∆t									<br/>
	 * 		v = v + a * t								<br/>
	 * 		x = x0 + v0 * t + 1/2 a * t²				<br/>
	 * </p>
	 * <br/>
	 * Note that v is not the speed of light, but is actually the speed of the node itself.
	 * The propagation of the light wave is caused by the motion of the node
	 * and the variable k determines the speed of light.<br/>
	 * <br/>
	 * If ∆t is one frame and one frame is one Planck time unit, then the short formula can be written as:<br/> 
	 * <p style="margin-left: 30px;">
	 * 		a = avg(N)        = k² / |N| * ∑(n – p) {n ∈ N}		<br/>
	 * 		v = nVel(v, a)    = (v + a) * r						<br/>
	 * 		p = nPos(v, p, a) = p + v + a * 0.5					<br/>
	 * </p>
	 * <br/>
	 * A node can only have six neighbors in the field matrix, then the length of the array |N| is 6.<br/>
	 * <br/>
	 * <b>Remarks:</b>
	 * The higher the k value the higher the speed of light, but k cannot exceed 1. 
	 * If k is greater than 1, the affected nodes become unstable and eventually fly away.<br/>
	 * <br/>
	 * 
	 */
	public void calcNewPosition() {
		// If set to false then the node stops updating its position
		if (qfsModel.isSimulating()) {
			ef.set(0);
            mf.set(0);
			
			for (QFSNode neighbor : neighbors) {
				if (neighbor != null) {
					if (isFixed || isWall) {
						double alpha = isWall? 0.00 : (1.0 - appSettings.getAbsorptionFixedNodes());
						
						if (alpha == 0) {
							velocity.set(0);
							positionBuff.set(fixedPosition);
						}
						
						if ((isXFixed || isFixed && appSettings.isUseFixedNodes()) || isWall) {
							ef.y += (neighbor.position.y - position.y) * alpha;
							ef.z += (neighbor.position.z - position.z) * alpha;
						}
						if ((isYFixed || isFixed && appSettings.isUseFixedNodes()) || isWall) {
							ef.x += (neighbor.position.x - position.x) * alpha;
							ef.z += (neighbor.position.z - position.z) * alpha;
						}
						if ((isZFixed || isFixed && appSettings.isUseFixedNodes()) || isWall) {
							ef.x += (neighbor.position.x - position.x) * alpha;
							ef.y += (neighbor.position.y - position.y) * alpha;
						}
					}
					else {
						ef.add(neighbor.position).sub(position);
					}
				}
			}

			Vector3d nLeft = neighbors[NEIGHBOR_LEFT] == null? new Vector3d() : neighbors[NEIGHBOR_LEFT].velocity;
			Vector3d nRight = neighbors[NEIGHBOR_RIGHT] == null? new Vector3d() : neighbors[NEIGHBOR_RIGHT].velocity;
			Vector3d nTop = neighbors[NEIGHBOR_TOP] == null? new Vector3d() : neighbors[NEIGHBOR_TOP].velocity;
			Vector3d nBottom = neighbors[NEIGHBOR_BOTTOM] == null? new Vector3d() : neighbors[NEIGHBOR_BOTTOM].velocity;
			Vector3d nFront = neighbors[NEIGHBOR_FRONT] == null? new Vector3d() : neighbors[NEIGHBOR_FRONT].velocity;
			Vector3d nBack = neighbors[NEIGHBOR_BACK] == null? new Vector3d() : neighbors[NEIGHBOR_BACK].velocity;

            mf.x = (nFront.y - nBack.y) * 0.05 - (nTop.z - nBottom.z) * 0.05;
            mf.y = (nRight.z - nLeft.z) * 0.05 - (nFront.x - nBack.x) * 0.05;
            mf.z = (nTop.x - nBottom.x) * 0.05 - (nRight.y - nLeft.y) * 0.05;

            double cws = qfsProject.getConstantWaveSpeed() / 6;
            double gamma = qfsProject.getConstantElectricFactor();
            double beta = qfsProject.getConstantMagneticFactor();

            Vector3d e_f = ef.mul(gamma);
            Vector3d m_f = mf.mul(beta);

			acceleration.set(e_f.add(m_f));

			// Note that the result of the addition or multiplication is stored in the object variable itself.
			//positionBuff.add(velocity.add(e_f)).add(e_f.mul(0.5));
			positionBuff.add(velocity.add(acceleration.mul(cws))).add(acceleration.mul(0.5));

//			positionBuff.add(velocity.add(acceleration.mul(cws))).add(acceleration.mul(0.5));

			if ((isXFixed || isFixed && appSettings.isUseFixedNodes()) || isWall) positionBuff.x = fixedPosition.x;
			if ((isYFixed || isFixed && appSettings.isUseFixedNodes()) || isWall) positionBuff.y = fixedPosition.y;
			if ((isZFixed || isFixed && appSettings.isUseFixedNodes()) || isWall) positionBuff.z = fixedPosition.z;
		}

		setEMFieldColor(isFixed? FIXED_COLOR : ef);
	}

}
