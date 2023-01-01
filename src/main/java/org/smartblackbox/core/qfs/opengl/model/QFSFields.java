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
package org.smartblackbox.core.qfs.opengl.model;

import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.smartblackbox.core.qfs.Constants;
import org.smartblackbox.core.qfs.opengl.model.entity.Entity;
import org.smartblackbox.core.qfs.opengl.model.entity.QFSNode;
import org.smartblackbox.core.qfs.opengl.utils.Neighbor;
import org.smartblackbox.core.qfs.opengl.utils.OBJFormatLoader;
import org.smartblackbox.core.qfs.settings.QFSProject;

public class QFSFields {

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel(); 
	private Scene scene; 
	
	private Entity baseField;

	private OBJFormatLoader loader;
	public QFSNode nForce;
	public QFSNode nVelocity;

	public QFSFields(Scene scene) {
		loader = new OBJFormatLoader();
		this.scene = scene;  
	}

	public void drawArrows(Entity baseField, float step, ObjFileModel modelSphere, int sizeI, int sizeJ, int sizeK) {
		ObjFileModel modelArrow = loader.loadOBJModel("models/arrow.obj");
		float scale = 0.5f;
		
		Entity arrowC = scene.createEntity(baseField, modelSphere,
				new Vector3d(-(sizeI + 1) / 2 * step, -(sizeJ / 2 + 1) * step, -(sizeK / 2 + 1) * step),
				new Vector3d(0, 0, 0), scale);
		arrowC.setOverrideModelMaterial(true);
		arrowC.getMaterial().setDiffuseColor(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
		arrowC.getMaterial().setAmbientColor(new Vector4f(10.0f, 10.0f, 10.0f, 1.0f));
		arrowC.getMaterial().setShininess(0.0f);

		Entity arrowX = scene.createEntity(baseField, modelArrow,
				new Vector3d(-(sizeI + 1) / 2 * step, -(sizeJ / 2 + 1) * step, -(sizeK / 2 + 1) * step),
				new Vector3d(0, 90, 0), scale);
		arrowX.setOverrideModelMaterial(true);
		arrowX.getMaterial().setDiffuseColor(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
		arrowX.getMaterial().setAmbientColor(new Vector4f(10.0f, 4.5f, 4.5f, 1.0f));
		arrowX.getMaterial().setShininess(0.0f);
		
		Entity arrowY = scene.createEntity(baseField, modelArrow, 
				new Vector3d(-(sizeI + 1) / 2 * step, -(sizeJ / 2 + 1) * step, -(sizeK / 2 + 1) * step),
				new Vector3d(0, 0, 0), scale);
		arrowY.setOverrideModelMaterial(true);
		arrowY.getMaterial().setDiffuseColor(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
		arrowY.getMaterial().setAmbientColor(new Vector4f(4.5f, 4.5f, 10.0f, 1.0f));
		arrowY.getMaterial().setShininess(0.0f);
		
		Entity arrowZ = scene.createEntity(baseField, modelArrow,
				new Vector3d(-(sizeI + 1) / 2 * step, -(sizeJ / 2 + 1) * step, -(sizeK / 2 + 1) * step),
				new Vector3d(-90, 0, 0), scale);
		arrowZ.setOverrideModelMaterial(true);
		arrowZ.getMaterial().setDiffuseColor(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
		arrowZ.getMaterial().setAmbientColor(new Vector4f(4.5f, 10.0f, 4.5f, 1.0f));
		arrowZ.getMaterial().setShininess(0.0f);
	}
	
	public void build(Vector3i dimension, Vector3d rotation) {
		int sizeI = dimension.x;
		int sizeJ = dimension.y;
		int sizeK = dimension.z;
		Statistics.numNodes = sizeI * sizeJ * sizeK;
		Statistics.numNodesX = sizeI;
		Statistics.numNodesY = sizeJ;
		Statistics.numNodesZ = sizeK;

		ObjFileModel modelSphere = loader.loadOBJModel("models/sphere2.obj");

		baseField = new Entity(null, null, new Vector3d(), new Vector3d(), 1.0f);
		baseField.setPosition(0, 0, 0);
		baseField.setRotation(rotation.x, rotation.y, rotation.z);
		scene.addEntity(baseField);

		float step = Constants.EP_DEFAULT_DISTANCE;
		
		// Draw xyz arrows on entity baseField.
		drawArrows(baseField, step, modelSphere, sizeI, sizeJ, sizeK);
		
		QFSNode[][][] fieldMatrix = new QFSNode[sizeK][sizeJ][sizeI];

		int NumNodes = sizeI * sizeJ * sizeK;
		
		qfsModel.setLoadingReady(false);
		qfsModel.setProgress(0.0);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int countNode = 0;
				for (int k = 0; k < sizeK; k++) {
					for (int j = 0; j < sizeJ; j++) {
						for (int i = 0; i < sizeI; i++) {
							QFSNode n = scene.createQFSNode(baseField, modelSphere,
									new Vector3d((i - sizeI / 2) * step, (j - sizeJ / 2) * step, (k - sizeK / 2) * step),
									new Vector3d(0, 0, 0), Constants.EP_DEFAULT_DISTANCE);
							n.setIndex(i, j, k);
							n.setOverrideModelMaterial(true);
							n.getMaterial().setDiffuseColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
							n.getMaterial().setAmbientColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
							fieldMatrix[k][j][i] = n;

							if ((sizeI > 1 && (i == 0 || i == sizeI - 1)) ||
									(sizeJ > 1 && (j == 0 || j == sizeJ - 1)) ||
									(sizeK > 1 && (k == 0 || k == sizeK - 1))) {
								n.setFixed(true);
							}

							// experimentalMode, when only the edges are fixed. This creates a cool effect.
//							if (!(
//								(i > 0 && i < sizeI - 1) && (j > 0 && j < sizeJ - 1)
//								 || (i > 0 && i < sizeI - 1) && (k > 0 && k < sizeK - 1)
//								 || (j > 0 && j < sizeJ - 1) && (k > 0 && k < sizeK - 1)
//								)) {
//								n.setFixed(true);
//							}


							countNode++;
							qfsModel.setProgress((double) countNode / (double) NumNodes * 100);
						}
					}
				}

				// Connect all neighbors in fieldMatrix.
				for (int k = 0; k < sizeK; k++) {
					for (int j = 0; j < sizeJ; j++) {
						for (int i = 0; i < sizeI; i++) {
							if (i > 0)
								fieldMatrix[k][j][i].setNeighbor(Neighbor.LEFT, fieldMatrix[k][j][i - 1]);
							else
								fieldMatrix[k][j][i].setNeighbor(Neighbor.LEFT, fieldMatrix[k][j][sizeI - 1]);
							if (i < sizeI - 1)
								fieldMatrix[k][j][i].setNeighbor(Neighbor.RIGHT, fieldMatrix[k][j][i + 1]);
							else
								fieldMatrix[k][j][i].setNeighbor(Neighbor.RIGHT, fieldMatrix[k][j][0]);

							if (j > 0)
								fieldMatrix[k][j][i].setNeighbor(Neighbor.BOTTOM, fieldMatrix[k][j - 1][i]);
							else
								fieldMatrix[k][j][i].setNeighbor(Neighbor.BOTTOM, fieldMatrix[k][sizeJ - 1][i]);
							if (j < sizeJ - 1)
								fieldMatrix[k][j][i].setNeighbor(Neighbor.TOP, fieldMatrix[k][j + 1][i]);
							else
								fieldMatrix[k][j][i].setNeighbor(Neighbor.TOP, fieldMatrix[k][0][i]);

							if (k > 0)
								fieldMatrix[k][j][i].setNeighbor(Neighbor.BACK, fieldMatrix[k - 1][j][i]);
							else
								fieldMatrix[k][j][i].setNeighbor(Neighbor.BACK, fieldMatrix[sizeK - 1][j][i]);
							if (k < sizeK - 1)
								fieldMatrix[k][j][i].setNeighbor(Neighbor.FRONT, fieldMatrix[k + 1][j][i]);
							else
								fieldMatrix[k][j][i].setNeighbor(Neighbor.FRONT, fieldMatrix[0][j][i]);
						}
					}
				}
				
				qfsModel.setLoadingReady(true);
			}
		}).start();
		
	}
	
	public void build() {
		Vector3i dimension = new Vector3i();
		qfsProject.getDimension(dimension);
		build(dimension, qfsProject.baseRotation);
	}

	public Entity getBaseField() {
		return baseField;
	}

	public QFSNode getNode(int x, int y, int z) {
		for (Entity entity : scene.getEntities()) {
			if (entity.getClass() == QFSNode.class) {
				if (((QFSNode) entity).isIndex(x, y, z))
					return (QFSNode) entity;
			}
		}
		return null;
	}

	public QFSNode getNode(Vector3i node) {
		return getNode(node.x, node.y, node.z);
	}
	
}
