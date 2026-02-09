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

import java.util.ArrayList;
import java.util.List;

import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.smartblackbox.qfs.opengl.model.entity.Entity;
import org.smartblackbox.qfs.opengl.model.entity.QFSNode;
import org.smartblackbox.qfs.opengl.model.lights.DirectionalLight;
import org.smartblackbox.qfs.settings.AppSettings;
import org.smartblackbox.qfs.settings.QFSProject;
import org.smartblackbox.qfs.settings.QFSSettings;
import org.smartblackbox.qfs.settings.SlitWallSettings;

public class Scene {

	private AppSettings appSettings = AppSettings.getInstance();

	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSSettings settings = qfsProject.settings;
	private SlitWallSettings slitWall = qfsProject.slitWall;
	private QFSModel qfsModel = qfsProject.getQfsModel();

	private List<Terrain> terrains;
	private List<Entity> entities;

	private Object[] nodeArray;
	private Object[] entityArray;

	private QFSFields qfsFields;

	private ArrayList<Vector3i> wallNodeIndexList = new ArrayList<Vector3i>();
	private ArrayList<QFSNode> hiLightedNodes = new ArrayList<QFSNode>();
	private QFSNode firstHiLightedNode;

	private Object tasks = new Object();
	private int numTask;

	private DirectionalLight directionalLight;

	private boolean animated;

	protected double animateInc;

	private int animateDirection = -1;

	public String status = "";

	private float lastSlitWallPos = qfsProject.slitWall.getPosition();

	public Scene() {
		entities = new ArrayList<>();
		terrains = new ArrayList<>();
		qfsFields = new QFSFields(this);
	}

	public Entity createEntity(Entity parent, ObjFileModel model, Vector3d position, Vector3d rotation, double scale) {
		return addEntity(new Entity(parent, model, position, rotation, scale));
	}

	public QFSNode createQFSNode(Entity parent, ObjFileModel model, Vector3d position, Vector3d rotation,
			double scale) {
		return (QFSNode) addEntity(new QFSNode(parent, model, position, rotation, scale));
	}

	public Oscillator createOscillator(String name, QFSNode entity) {
		Oscillator oscillator = new Oscillator(name, entity.getIndex());
		QFSProject.getInstance().oscillators.add(oscillator);
		return oscillator;
	}

	public List<Terrain> getTerrains() {
		return terrains;
	}

	public void setTerrains(List<Terrain> terrains) {
		this.terrains = terrains;
	}

	public void addTerrain(Terrain terrain) {
		terrains.add(terrain);
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public List<Entity> getNodes() {
		List<Entity> nodes = new ArrayList<>();
		for (Entity entity : entities) {
			if (QFSNode.class.isAssignableFrom(entity.getClass())) {
				nodes.add(entity);
			}
		}
		return nodes;
	}

	public ArrayList<Vector3i> getWallNodeIndexList() {
		return wallNodeIndexList;
	}

	public QFSNode getNodeByIndex(Vector3i nodeIndex) {
		int index = nodeIndex.z * qfsProject.getDimensionY() * qfsProject.getDimensionX()
				+ nodeIndex.y * qfsProject.getDimensionX() + nodeIndex.x;
		if (index >= 0 && index < nodeArray.length)
			return (QFSNode) nodeArray[index];
		else
			return null;

	}

	public Entity addEntity(Entity entity) {
		entities.add(entity);
		return entity;
	}

	public QFSFields getQfsFields() {
		return qfsFields;
	}

	public void setQfsFields(QFSFields qfsFields) {
		this.qfsFields = qfsFields;
	}

	public DirectionalLight getDirectionalLight() {
		return directionalLight;
	}

	public void setDirectionalLight(DirectionalLight directionalLight) {
		this.directionalLight = directionalLight;
	}

	public void update() {
		nodeArray = getNodes().toArray();
		entityArray = getEntities().toArray();
		updateWalls();
	}

	public void clearHiLightNodes() {
		for (QFSNode node : hiLightedNodes) {
			node.setHiLighted(false);
		}
		hiLightedNodes.clear();
	}

	private void iAddNodeHiLight(int x, int y, int z) {
		QFSNode node = (QFSNode) nodeArray[z * qfsProject.getDimensionX() * qfsProject.getDimensionY()
				+ y * qfsProject.getDimensionX() + x];
		node.setHiLighted(true);
		hiLightedNodes.add(node);
	}

	public void addNodeHiLight(int x, int y, int z) {
		int w = qfsProject.getDimensionX();
		int d = qfsProject.getDimensionY();
		int h = qfsProject.getDimensionZ();
		int size = 1;

		for (int k = z - size; k <= z + size; k++) {
			for (int j = y - size; j <= y + size; j++) {
				for (int i = x - size; i <= x + size; i++) {
					if (i >= 0 && i < w && j >= 0 && j < d && k >= 0 && k < h) {
						iAddNodeHiLight(i, j, k);
					}
				}
			}
		}
	}

	private void iRemoveNodeHiLight(int x, int y, int z) {
		QFSNode node = (QFSNode) nodeArray[z * qfsProject.getDimensionX() * qfsProject.getDimensionY()
				+ y * qfsProject.getDimensionX() + x];
		node.setHiLighted(false);
		hiLightedNodes.remove(node);
	}

	public void removeNodeHiLight(int x, int y, int z) {
		int w = qfsProject.getDimensionX();
		int d = qfsProject.getDimensionY();
		int h = qfsProject.getDimensionZ();
		int size = 1;

		for (int k = z - size; k <= z + size; k++) {
			for (int j = y - size; j <= y + size; j++) {
				for (int i = x - size; i <= x + size; i++) {
					if (i >= 0 && i < w && j >= 0 && j < d && k >= 0 && k < h) {
						iRemoveNodeHiLight(i, j, k);
					}
				}
			}
		}
	}

	private Vector3i isWallNodeExists(QFSNode node) {
		for (Vector3i nodeIndex : wallNodeIndexList) {
			if (node.isIndex(nodeIndex)) {
				return nodeIndex;
			}
		}
		return null;
	}

	private void addWallNode(QFSNode node) {
		if (isWallNodeExists(node) == null)
			wallNodeIndexList.add(node.getIndex());
	}

	private void removeWallNode(QFSNode node) {
		Vector3i nodeIndex;
		if ((nodeIndex = isWallNodeExists(node)) != null)
			wallNodeIndexList.remove(nodeIndex);
	}

	private void setNodeWall(QFSNode node, boolean value) {
		node.setWall(value);
		if (node.isWall())
			addWallNode(node);
		else
			removeWallNode(node);
	}

	private void iFillNodeWall(QFSNode node, boolean value, int startX, int startY, int startZ) {
		int w = qfsProject.getDimensionX();
		int d = qfsProject.getDimensionY();
		int h = qfsProject.getDimensionZ();
		int x = startX;
		int y = startY;
		int z = startZ;

		if (x >= 0 && x < w && y >= 0 && y < d && z >= 0 && z < h) {
			node = (QFSNode) nodeArray[startZ * w * d + startY * w + x];
			if (!node.isWall() && !node.isFixed()) {
				while (!node.isWall() && !node.isFixed()) {
					setNodeWall(node, value);
					x++;
					node = (QFSNode) nodeArray[startZ * w * d + startY * w + x];
				}
				iFillNodeWall(node, value, x - 2, startY + 1, startZ);
				iFillNodeWall(node, value, x - 2, startY - 1, startZ);
			}
		}

		x = startX - 1;
		if (x >= 0 && x < w && y >= 0 && y < d && z >= 0 && z < h) {
			node = (QFSNode) nodeArray[startZ * w * d + startY * w + x];
			if (!node.isWall() && !node.isFixed()) {
				while (!node.isWall() && !node.isFixed()) {
					setNodeWall(node, value);
					x--;
					node = (QFSNode) nodeArray[startZ * w * d + startY * w + x];
				}
				iFillNodeWall(node, value, x + 2, startY + 1, startZ);
				iFillNodeWall(node, value, x + 2, startY - 1, startZ);
			}
		}
	}

	public void fillNodeWall(QFSNode node, boolean value) {
		int startX = node.getIndex().x;
		int startY = node.getIndex().y;
		int startZ = node.getIndex().z;

		iFillNodeWall(node, value, startX, startY, startZ);
	}

	public void swapNodeWall(QFSNode node) {
		setNodeWall(node, !node.isWall());
	}

	public void swapNodeWallZ(QFSNode node) {
		int x = node.getIndex().x;
		int y = node.getIndex().y;

		for (int z = 0; z < qfsProject.getDimensionZ(); z++) {
			node = (QFSNode) nodeArray[z * qfsProject.getDimensionX() * qfsProject.getDimensionY()
					+ y * qfsProject.getDimensionX() + x];
			swapNodeWall(node);
		}
	}

	public void eraseWall(QFSNode node) {
		setNodeWall(node, false);
	}

	public void eraseWallZ(QFSNode node) {
		Vector3i index = node.getIndex();
		for (int z = 0; z < qfsProject.getDimensionZ(); z++) {
			int i = z * qfsProject.getDimensionX() * qfsProject.getDimensionY() + index.y * qfsProject.getDimensionX()
					+ index.x;
			node = (QFSNode) nodeArray[i];
			eraseWall(node);
		}
	}

	public void clearAllWalls() {
		for (Vector3i nodeIndex : wallNodeIndexList) {
			QFSNode node = getNodeByIndex(nodeIndex);
			if (node != null) {
				node.setWall(false);
			}
		}
		wallNodeIndexList.clear();
	}

	public void makeWallFromHiLightNodes() {
		for (QFSNode node : hiLightedNodes) {
			node.setHiLighted(false);
			node.setVisible(false);
			setNodeWall(node, true);
			System.out.println("slitWall.getAbsorption(): " + slitWall.getReflection());
			node.setReflection(slitWall.getReflection());
		}
		hiLightedNodes.clear();
	}

	public void updateWalls() {
		for (Vector3i nodeIndex : wallNodeIndexList) {
			QFSNode node = getNodeByIndex(nodeIndex);
			if (node != null) {
				node.setWall(true);
			}
		}
	}

	public void drawSlitWall() {
		clearHiLightNodes();

		int p, d, iSize, jSize, ic, jc;
		boolean isSlit;
		int numSlits = slitWall.getNumSlits();

		int jStart = 1;

		float slitWallPos = qfsProject.slitWall.getPosition();

		switch (slitWall.direction) {
		case x:
			p = (int) (qfsProject.slitWall.getPosition() * qfsProject.getDimensionX());

			if (slitWallPos != lastSlitWallPos) {
				if (slitWallPos > lastSlitWallPos) {
					p++;
				}
				slitWallPos = (float) p / (float) qfsProject.getDimensionX();
				qfsProject.slitWall.setPosition(slitWallPos);
			}

			if (p < 1)
				p = 1;
			if (p > qfsProject.getDimensionX() - 2)
				p = qfsProject.getDimensionX() - 2;
			iSize = qfsProject.getDimensionY();
			jSize = qfsProject.getDimensionZ();
			ic = iSize / 2;
			jc = jSize / 2;
			d = slitWall.getSlitDistance();

			double shw = slitWall.getSlitWidth() * 0.5;
			double shh = slitWall.getSlitHeight() * 0.5;

			if (jSize == 1) {
				jStart = 0;
				jSize = 2;
			}

			for (int j = jStart; j < jSize - 1; j++) {
				for (int i = 1; i < iSize - 1; i++) {
					addNodeHiLight(p, i, j);
				}
			}

			for (int j = jStart; j < jSize - 1; j++) {
				for (int i = 1; i < iSize - 1; i++) {
					for (int n = 1; n <= numSlits; n++) {
						double c = (ic + ((n - numSlits * 0.5) - 0.5) * d);

						isSlit = (i > c - shw && i < c + shw && j > jc - shh && j < jc + shh);
						if (isSlit) {
							removeNodeHiLight(p, i, j);

						}
					}
				}
			}

			break;
		case y:
			break;
		case z:
			break;
		default:
			break;

		}

		lastSlitWallPos = slitWallPos;
	}

	public void drawLine(QFSNode node) {
		clearHiLightNodes();

		int z = settings.getVisibleIndexZ();
		int x1 = firstHiLightedNode.getIndex().x;
		int y1 = firstHiLightedNode.getIndex().y;
		int x2 = node.getIndex().x;
		int y2 = node.getIndex().y;
		int xD = x2 - x1;
		int yD = y2 - y1;
		if (xD == 0 && yD == 0)
			return;

		int xStart = x1, xEnd = x2, yStart = y1, yEnd = y2;
		if (x2 < x1) {
			xStart = x2;
			xEnd = x1;
		}
		if (y2 < y1) {
			yStart = y2;
			yEnd = y1;
		}

		if (Math.abs(xD) >= Math.abs(yD)) {
			yStart = x1 <= x2 ? y1 : y2;
			for (int x = xStart; x <= xEnd; x++) {
				int y = (int) Math.round(yStart + (x - xStart) / (double) xD * yD);
				addNodeHiLight(x, y, z);
			}
		} else {
			xStart = y1 <= y2 ? x1 : x2;
			for (int y = yStart; y <= yEnd; y++) {
				int x = (int) Math.round(xStart + (y - yStart) / (double) yD * xD);
				addNodeHiLight(x, y, z);
			}
		}
	}

	public void drawWall(QFSNode node) {
		clearHiLightNodes();

		int x1 = firstHiLightedNode.getIndex().x;
		int y1 = firstHiLightedNode.getIndex().y;
		int x2 = node.getIndex().x;
		int y2 = node.getIndex().y;
		int xD = x2 - x1;
		int yD = y2 - y1;
		if (xD == 0 && yD == 0)
			return;

		int xStart = x1, xEnd = x2, yStart = y1, yEnd = y2;
		if (x2 < x1) {
			xStart = x2;
			xEnd = x1;
		}
		if (y2 < y1) {
			yStart = y2;
			yEnd = y1;
		}

		for (int z = 0; z < qfsProject.getDimensionZ(); z++) {
			if (Math.abs(xD) >= Math.abs(yD)) {
				yStart = x1 <= x2 ? y1 : y2;
				for (int x = xStart; x <= xEnd; x++) {
					int y = (int) Math.round(yStart + (x - xStart) / (double) xD * yD);
					addNodeHiLight(x, y, z);
				}
			} else {
				xStart = y1 <= y2 ? x1 : x2;
				for (int y = yStart; y <= yEnd; y++) {
					int x = (int) Math.round(xStart + (y - yStart) / (double) yD * xD);
					addNodeHiLight(x, y, z);
				}
			}
		}
	}

	public void setFirstHiLightNode(QFSNode node) {
		firstHiLightedNode = node;
		firstHiLightedNode.setHiLighted(true);
	}

	public QFSNode getNodeFromMousePos(double xPos, double yPos) {
		if (nodeArray == null)
			return null;
		Matrix4f projectionMatrix = qfsProject.camera.getProjectionMatrix();
		Matrix4f viewMatrix = qfsProject.camera.getViewMatrix();
		Vector3f cameraPos = qfsProject.camera.getPosition();

		float x = (float) ((2 * (xPos - 0)) / appSettings.getWindowWidth() - 1);
		float y = (float) ((2 * (appSettings.getWindowHeight() - (yPos + 0))) / appSettings.getWindowHeight() - 1);

		Vector4f clipCoords = new Vector4f(x, y, 0, 1);

		Matrix4f invProjection = new Matrix4f(projectionMatrix).invert();
		Vector4f eyeCoords = invProjection.transform(clipCoords);
		eyeCoords.w = 0;
		Matrix4f invViewMatrix = new Matrix4f(viewMatrix).invert();
		Vector4f rayWorld = invViewMatrix.transform(eyeCoords);
		Vector3f ray = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z).normalize();

		Vector4f nodePos = new Vector4f();
		Vector2f result = new Vector2f();
		Vector3f scale = new Vector3f();

		int iMin = settings.getVisibleIndexZ() * qfsProject.getDimensionX() * qfsProject.getDimensionY();
		int iMax = (settings.getVisibleIndexZ() + 1) * qfsProject.getDimensionX() * qfsProject.getDimensionY();

		// FPS drops when using large number of nodes.
		// TODO: use BSP to improve performance.
		for (int i = iMin; i >= 0 && i < iMax && i < nodeArray.length; i++) {
			QFSNode node = (QFSNode) nodeArray[i];
			if (node.isVisible() && (!node.isFixed() || node.isWall())) {
				node.getTransformMatrixf().transform(0, 0, 0, 1, nodePos);
				node.getTransformMatrixf().getScale(scale);

				double radius = settings.scale * node.getScale() * 0.5;

				if (Intersectionf.intersectRaySphere(cameraPos.x, cameraPos.y, cameraPos.z, ray.x, ray.y, ray.z,
						nodePos.x, nodePos.y, nodePos.z, (float) (radius * radius), result)) {
					return node;
				}
			}
		}
		return null;
	}

	public void threadUpdateMatrix(final int start, final int end) {
		incTask();
		status = "threadUpdateMatrix(" + start + ", " + end + "); TaskNo.:" + numTask;
		new Thread(new Runnable() {

			volatile int i = start;

			@Override
			public void run() {
				while (i < end) {
					((Entity) entityArray[i++]).updateMatrix();
				}
				taskCompleted();
			}
		}).start();
	}

	public void processMatrixUpdateTasks(int numThreads, int segmentSize) {
		resetTasks();

		for (int i = 0; i < numThreads; i++) {
			int start = i * segmentSize;
			int end = start + segmentSize;
			end = end >= entityArray.length ? entityArray.length : end;
			threadUpdateMatrix(start, end);
		}
	}

	public void threadCalcPhysics(final int start, final int end) {
		incTask();
		status = "threadCalcPhysics(" + start + ", " + end + "); TaskNo.:" + numTask;
		new Thread(new Runnable() {

			volatile int i = start;

			@Override
			public void run() {
				// If set to false then the node stops updating its position
				if (qfsModel.isSimulating()) {
					switch (settings.getNodeFormulaVersion()) {
					case v_1_3:
						while (i < end) {
							((QFSNode) nodeArray[i++]).updateNodePropertiesV1_3();
						}
						break;
					case v_1_2:
						while (i < end) {
							((QFSNode) nodeArray[i++]).updateNodePropertiesV1_2();
						}
						break;
					case v_1_1:
						while (i < end) {
							((QFSNode) nodeArray[i++]).updateNodePropertiesV1_1();
						}
						break;
					case v_1_0:
					default:
						while (i < end) {
							((QFSNode) nodeArray[i++]).updateNodePropertiesV1_0();
						}
						break;

					}
				}

				taskCompleted();
			}
		}).start();
	}

	public void processPhysicsTasks(int numThreads, int segmentSize) {
		resetTasks();

		for (int i = 0; i < numThreads; i++) {
			int start = i * segmentSize;
			int end = start + segmentSize;
			end = end >= nodeArray.length ? nodeArray.length : end;
			threadCalcPhysics(start, end);
		}
	}

	public int getNumTask() {
		return numTask;
	}

	public synchronized void setNumTask(int numTask) {
		this.numTask = numTask;
	}

	public synchronized void resetTasks() {
		numTask = 0;
	}

	public synchronized void incTask() {
		numTask++;
	}

	public synchronized void taskCompleted() {
		numTask--;
		synchronized (tasks) {
			if (numTask <= 0) {
				tasks.notifyAll();
				numTask = 0;
			}
		}
	}

	public void waitForAllTasksReady() {
		synchronized (tasks) {
			try {
				tasks.wait();
			} catch (InterruptedException e) {
			}
		}
		numTask = 0;
	}

	public boolean isAnimated() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}

	public void startAnimation() {
		setAnimated(true);
	}

	public void stopAnimation() {
		setAnimated(false);
	}

	public void runAnimation() {
		if (animated) {
			double z = getQfsFields().getBaseField().getRotation().z;

			if (animateDirection == -1 && z <= -170) {
				animateDirection = 1;
			} else if (animateDirection == 1 && z >= -10) {
				animateDirection = -1;
			}

			getQfsFields().getBaseField().incRotation(0.0f, 0.0f, 0.5 * animateDirection);
		}
	}

	public void toggleAnimation() {
		setAnimated(!animated);
	}

}
