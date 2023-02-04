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
package org.smartblackbox.qfs.opengl.controller;

import java.util.ArrayList;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.IMouseAndKeyboardEvents;
import org.smartblackbox.qfs.gui.model.NuklearModel.Frame;
import org.smartblackbox.qfs.opengl.model.DetectorModel;
import org.smartblackbox.qfs.opengl.model.Material;
import org.smartblackbox.qfs.opengl.model.ObjFileModel;
import org.smartblackbox.qfs.opengl.model.Oscillator;
import org.smartblackbox.qfs.opengl.model.QFSModel;
import org.smartblackbox.qfs.opengl.model.QFSModel.Mode;
import org.smartblackbox.qfs.opengl.model.QFSModel.NodeSelectionMode;
import org.smartblackbox.qfs.opengl.model.Scene;
import org.smartblackbox.qfs.opengl.model.Terrain;
import org.smartblackbox.qfs.opengl.model.Texture;
import org.smartblackbox.qfs.opengl.model.entity.Entity;
import org.smartblackbox.qfs.opengl.model.entity.QFSNode;
import org.smartblackbox.qfs.opengl.model.lights.DirectionalLight;
import org.smartblackbox.qfs.opengl.model.lights.Light;
import org.smartblackbox.qfs.opengl.utils.OBJFormatLoader;
import org.smartblackbox.qfs.opengl.utils.Screenshot;
import org.smartblackbox.qfs.opengl.view.GLWindow;
import org.smartblackbox.qfs.opengl.view.renderer.Renderer;
import org.smartblackbox.qfs.settings.QFSProject;
import org.smartblackbox.qfs.settings.QFSSettings;
import org.smartblackbox.qfs.settings.QFSSettings.RenderType;
import org.smartblackbox.qfs.settings.SlitWallSettings;
import org.smartblackbox.utils.PerformanceMonitor;
import org.smartblackbox.utils.PerformanceMonitor.Measurement;
import org.smartblackbox.utils.Utils;

public class QuantumFieldSimulator extends Engine implements IMouseAndKeyboardEvents {

	//////////////////////////////////////////////////////////////////////////////////
	/// Project Settings /////////////////////////////////////////////////////////////
	private QFSProject qfsProject = QFSProject.getInstance();
	private QFSModel qfsModel = qfsProject.getQfsModel(); 
	private Scene scene = qfsProject.scene;
	private QFSSettings settings = qfsProject.settings;
	private SlitWallSettings slitWall = qfsProject.slitWall;
	private DetectorModel detectorModel = qfsProject.detectorModel;
	/// Project Settings /////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////

	private final Renderer renderer;
	private final OBJFormatLoader loader;

	private QFSNode selectedNode;
	private final Vector2d lastMousePos = new Vector2d();

	private boolean leftButtonPressed = false;
	private boolean rightButtonPress = false;
	private boolean isReleased;
	private boolean leftButtonReleased;
	private boolean isDrawing;
	private boolean isCalcNextPhysicsReady = true;
	private boolean isRenderingReady;
	private boolean isLoadingStarted;
	private boolean isNodesUpdateRequired;
	private KeyEvents keyEvents;
	private ArrayList<QFSNode> customNodeFixed = new ArrayList<QFSNode>();
	private int waitAfterReset = 0;

	public QuantumFieldSimulator(GLWindow glWindow) {
		super(glWindow);

		renderer = new Renderer();
		loader = new OBJFormatLoader();
	}

	@Override
	public void init() throws Exception {
		super.init();
		addMouseAndKeyboardListener(this);
		renderer.init();

		initDefaultLights();
		initDefaultTerrain();
		initDefaultScene();
		updateNodesProperties();
		startMonitoringDeadThreads();
		qfsModel.startSimulation();
	}

	public void initDefaultLights() {
		ObjFileModel modelSphere = loader.loadOBJModel(Constants.MODEL_LIGHT_BULB);
		ObjFileModel modelSpotLight = loader.loadOBJModel(Constants.MODEL_LIGHT_CAP);

		modelSpotLight.getMaterial().setAmbientColor(0.0f, 1.0f, 0.0f, 1.0f);
		modelSpotLight.getMaterial().setAmbientIntensity(6);
		modelSpotLight.getMaterial().setDiffuseColor(0.5f, 0.2f, 0.0f, 1.0f);
		modelSpotLight.getMaterial().setDiffuseIntensity(0.8);

		//Point light
		float lightIntensity = 3.0f;
		Vector3d lightPosition = new Vector3d(30, 200, 175);
		Vector3d lightRotation = new Vector3d(0, 0, 0);
		Vector3f lightColor = new Vector3f(0.67f, 0.8f, 1.0f);
		Vector3d lightDirection = new Vector3d(0, 0, 1);
		float cutOff = 90;
		Light pointLight = qfsProject.createLight(null, modelSphere, modelSpotLight, lightPosition, lightRotation, 1.0);
		pointLight.set(lightColor, lightIntensity, 1.0, 0.01, 0.000003, cutOff);
		pointLight.setDirection(lightDirection);
		pointLight.setName("DefaultPointLight");
		pointLight.setSpotLight(false);

		//Spot light
		lightIntensity = 3.0f;
		lightPosition = new Vector3d(25.0f, 40.0f, -10.0f);
		lightRotation = new Vector3d(0, 0, 0);
		lightDirection = new Vector3d(-0.3, -1, 0.1);
		cutOff = 25;
		lightColor = new Vector3f(0.5f, 1.0f, 0.5f);
		Light spotLight = qfsProject.createLight(null, modelSphere, modelSpotLight, lightPosition, lightRotation, 3.0);
		spotLight.set(lightColor, lightIntensity, 0.0, 0.01, 0.0007, cutOff);
		spotLight.setDirection(lightDirection);
		spotLight.setName("DefaultSpotLight");
		spotLight.setSpotLight(true);

		// Directional light
		lightIntensity = 0.0f;
		lightColor = new Vector3f(1.0f, 1.0f, 1.0f);
		DirectionalLight directionalLight = new DirectionalLight(lightColor, new Vector3f(0, 100, 0), lightIntensity);
		scene.setDirectionalLight(directionalLight);
	}

	public void initDefaultScene() {
		qfsProject.camera.movePosition(0, 0, 120);
		qfsProject.camera.updateProjectionMatrix();

		// Create a default scene with xyz nodes.
		qfsProject.setDimension(51, 51, 31);
		// The scene is (re)build when settings has been changed and reset, see checksettingsChanged().
		qfsProject.reset();
	}

	public void initDefaultTerrain() throws Exception {
		qfsProject.terrain = new Terrain(
				new Vector3f(-Terrain.SIZE / 2,  -150,  -Terrain.SIZE / 2),
				loader,
				new Material());
		qfsProject.terrain.setTerrainTexturePath(Constants.TEXTURE_FILE_PATH);
		qfsProject.terrain.setTerrainTextureFile("default2.jpg");
		Texture texture = new Texture(loader.loadTexture(qfsProject.terrain.getTerrainTextureFilePath()));
		qfsProject.terrain.getMaterial().setTexture(texture);
		scene.addTerrain(qfsProject.terrain);
	}

	private void updateNodesProperties() {
		renderer.cleanupNodes();

		for (Entity entity : scene.getEntities()) {
			if (entity.getClass() == QFSNode.class) {
				QFSNode node = (QFSNode) entity;
				int i = node.getIndex().x;
				int j = node.getIndex().y;
				int k = node.getIndex().z;
				int dimX = qfsProject.getDimensionX();
				int dimY = qfsProject.getDimensionY();
				int dimZ = qfsProject.getDimensionZ();

				if (i != -1 || j != -1 || k != -1) {
					boolean isBoundingLine =
							i == 0								&& j == 0								||
							i == 0								&& j == qfsProject.getDimensionY() - 1  ||
							i == qfsProject.getDimensionX() - 1	&& j == 0								||
							i == qfsProject.getDimensionX() - 1	&& j == qfsProject.getDimensionY() - 1	||

							i == 0								&& k == 0								||
							i == 0								&& k == qfsProject.getDimensionZ() - 1  ||
							i == qfsProject.getDimensionX() - 1	&& k == 0								||
							i == qfsProject.getDimensionX() - 1	&& k == qfsProject.getDimensionZ() - 1	||

							j == 0								&& k == 0								||
							j == 0								&& k == qfsProject.getDimensionZ() - 1  ||
							j == qfsProject.getDimensionY() - 1	&& k == 0								||
							j == qfsProject.getDimensionY() - 1	&& k == qfsProject.getDimensionZ() - 1
							;

					boolean isVisible = isBoundingLine
							|| 
							(settings.getRenderType() == RenderType.all?
									i > 0 && i < dimX - 1 && j > 0 && j < dimY - 1 && k >  0 && k < dimZ - 1
									:
										k == settings.getVisibleIndexZ()
									);
					node.setVisible(isVisible || node.isHiLighted());

				}

			}
			renderer.processEntity(entity);
		}
	}

	private void updateNodesArray() {
		if (qfsModel.isLoadingReady()) {
			updateNodesProperties();
			scene.update();
			nuklearModel.closeFrame(Frame.progress);

			settings.setVisibleIndexZ(qfsProject.getDimensionZ() / 2);

			qfsModel.getCurrentMouseNodeIndex().x = qfsProject.getDimensionX() / 2;
			qfsModel.getCurrentMouseNodeIndex().y = qfsProject.getDimensionY() / 2;
			qfsModel.getCurrentMouseNodeIndex().z = qfsProject.getDimensionZ() / 2;
			qfsModel.setCurrentMouseNode(scene.getNodeByIndex(qfsModel.getCurrentMouseNodeIndex()));
			//isNodesUpdateRequired = true;

			isLoadingStarted = false;
		}
	}

	private void mouseReleaseWall(QFSNode node) {
		switch (qfsModel.getEditMode()) {
		case swapNode:
			scene.swapNodeWall(node);
			isNodesUpdateRequired = true;
			break;
		case swapNodesZ:
			scene.swapNodeWallZ(node);
			isNodesUpdateRequired = true;
			break;
		case drawLine:
		case drawWall:
			if (!isDrawing) {
				if (node != null) {
					scene.setFirstHiLightNode(node);
					isDrawing = true;
				}
			}
			else {
				scene.makeWallFromHiLightNodes();
				isNodesUpdateRequired = true;
				isDrawing = false;
			}
			break;
		default:
			break;

		}
	}

	public void mouseDrawWall(QFSNode node) {
		switch (qfsModel.getEditMode()) {
		case drawLine:
			scene.drawLine(node);
			isNodesUpdateRequired = true;
			break;
		case drawWall:
			scene.drawWall(node);
			isNodesUpdateRequired = true;
			break;
		default:
			break;
		}
	}

	public void checkAndErase(QFSNode node) {
		switch (qfsModel.getEditMode()) {
		case eraseNode:
			scene.eraseWall(node);
			isNodesUpdateRequired = true;
			break;
		case eraseWall:
			scene.eraseWallZ(node);
			isNodesUpdateRequired = true;
			break;
		default:
			break;
		}
	}

	@Override
	public boolean mouseCursorPosCallback(long handle, double xPos, double yPos) {
		qfsModel.getMousePos().x = xPos;
		qfsModel.getMousePos().y = yPos;

		QFSNode node = scene.getNodeFromMousePos(xPos, yPos);
		if (node != null) {
			if (selectedNode != null) selectedNode.setSelected(false);
			selectedNode = node;
			selectedNode.setSelected(true);
			qfsModel.getCurrentMouseNodeIndex().set(selectedNode.getIndex());
			qfsModel.setCurrentMouseNode(selectedNode);

			if (isDrawing)
				mouseDrawWall(node);

			if (leftButtonPressed)
				checkAndErase(node);
		}

		float deltaX = 0;
		float deltaY = 0;

		if (lastMousePos.x > 0 && lastMousePos.y > 0) {
			double x = xPos - lastMousePos.x;
			double y = yPos - lastMousePos.y;
			boolean rotateX = x != 0;
			boolean rotateY = y != 0;

			if (rotateX)
				deltaY = (float) x;
			if (rotateY)
				deltaX = (float) y;
		}
		if (rightButtonPress) {
			qfsProject.camera.moveRotation(deltaX * Constants.MOUSE_SENSITIVITY, deltaY * Constants.MOUSE_SENSITIVITY, 0);
		}

		lastMousePos.x = xPos;
		lastMousePos.y = yPos;

		return false;
	}

	@Override
	public boolean mouseButtonCallback(long handle, int button, int action, int mods) {
		if (nuklearModel.getFocusedFrame() == null) {
			leftButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
			leftButtonReleased = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE;
			rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;

			QFSNode node = scene.getNodeFromMousePos(qfsModel.getMousePos().x, qfsModel.getMousePos().y);

			if (node != null) {
				if (leftButtonPressed)
					checkAndErase(node);

				if (leftButtonReleased) {
					switch (qfsModel.getNodeSelectionMode()) {
					case none:
						if (qfsModel.isSimulating() && selectedNode != null)
							selectedNode.incPosition(new Vector3d(0, 0, qfsProject.rainModel.getForce()));
						break;
					case oscillator:
						qfsModel.setNodeSelectionMode(NodeSelectionMode.none);
						qfsModel.selectedOscillator.setNode(node.getIndex());
						nuklearModel.showFrame(Frame.oscillator);
						if (qfsModel.isSimulating() && selectedNode != null)
							selectedNode.incPosition(new Vector3d(0, 0, qfsProject.rainModel.getForce()));
						break;
					case ignitionNode:
						qfsModel.setNodeSelectionMode(NodeSelectionMode.none);
						qfsProject.detectorModel.setIgnitorNode(node.getIndex());
						nuklearModel.showFrame(Frame.detector);
						break;
					case detectorNode:
						qfsModel.setNodeSelectionMode(NodeSelectionMode.none);
						qfsProject.detectorModel.setDetectorNode(node.getIndex());
						nuklearModel.showFrame(Frame.detector);
						break;
					default:
						break;
					}

					mouseReleaseWall(node);
				}
			}
		}
		else {
			leftButtonPressed = false;
			leftButtonReleased = false;
			rightButtonPress = false;
		}
		return false;
	}

	@Override
	public boolean mouseScrollCallback(long handle, double xOffset, double yOffset) {
		float incCamera = keyEvents != null &&  keyEvents.isCtrlPressed? 2 : 12;
		qfsProject.camera.movePosition(
				(float) (incCamera * xOffset * Constants.CAMERA_MOVE_SPEED),
				0,
				(float) (incCamera * yOffset * Constants.CAMERA_MOVE_SPEED)
				);

		return false;
	}

	@Override
	public boolean mouseCursorEnterCallback(long handle, boolean entered) {
		return false;
	}

	@Override
	public boolean charCallback(long handle, int unicode) {
		return false;
	}

	@Override
	public boolean keyContinuousCallback(long handle, int key, KeyEvents keyEvents) {

		this.keyEvents = keyEvents;

		float incCamera = keyEvents.isShiftPressed? 2 : 8;
		float inc = keyEvents.isCtrlPressed? (keyEvents.isShiftPressed? 0.2f : 1.0f) : 0f;

		switch (key) {
		case GLFW.GLFW_KEY_W:
			moveCamera(0, 0, -incCamera);
			break;
		case GLFW.GLFW_KEY_S:
			moveCamera(0, 0, incCamera);
			break;
		case GLFW.GLFW_KEY_A:
			moveCamera(-incCamera, 0, 0);
			break;
		case GLFW.GLFW_KEY_D:
			moveCamera(incCamera, 0, 0);
			break;
		case GLFW.GLFW_KEY_Z:
			moveCamera(0, -incCamera, 0);
			break;
		case GLFW.GLFW_KEY_X:
			moveCamera(0, incCamera, 0);
			break;
		case GLFW.GLFW_KEY_LEFT:
			switch (qfsModel.getMode()) {
			case light:
				if (qfsModel.getSelectedLight() != null)
					qfsModel.getSelectedLight().getPositionBuff().x -= inc;
				break;
			default:
				break;
			}
			break;
		case GLFW.GLFW_KEY_RIGHT:
			switch (qfsModel.getMode()) {
			case light:
				if (qfsModel.getSelectedLight() != null)
					qfsModel.getSelectedLight().getPositionBuff().x += inc;
				break;
			default:
				break;
			}
			break;
		case GLFW.GLFW_KEY_UP:
			switch (qfsModel.getMode()) {
			case normal:
				if (keyEvents.isCtrlPressed) {
					qfsModel.getCurrentMouseNodeIndex().z++;
					if (qfsModel.getCurrentMouseNodeIndex().z >= qfsProject.getDimensionZ() - 2)
						qfsModel.getCurrentMouseNodeIndex().z = qfsProject.getDimensionZ() - 2;
					updateSelectedNode();

					if (qfsProject.getDimensionZ() > 1) {
						settings.setVisibleIndexZ(settings.getVisibleIndexZ() + 1);
						if (settings.getVisibleIndexZ() >= qfsProject.getDimensionZ() - 2)
							settings.setVisibleIndexZ(qfsProject.getDimensionZ() - 2);
						isNodesUpdateRequired = true;
					}
				}
				break;
			case alhpa:
				settings.incAlpha();
				break;
			case intensity:
				settings.incIntensity();
				break;
			case light:
				if (qfsModel.getSelectedLight() != null)
					qfsModel.getSelectedLight().getPositionBuff().z -= inc;
				break;
			default:
				break;
			}
			break;
		case GLFW.GLFW_KEY_DOWN:
			switch (qfsModel.getMode()) {
			case normal:
				if (keyEvents.isCtrlPressed) {
					qfsModel.getCurrentMouseNodeIndex().z--;
					if (qfsModel.getCurrentMouseNodeIndex().z < 1) qfsModel.getCurrentMouseNodeIndex().z = 1;
					updateSelectedNode();

					if (qfsProject.getDimensionZ() > 1) {
						settings.setVisibleIndexZ(settings.getVisibleIndexZ() - 1);
						if (settings.getVisibleIndexZ() < 1) settings.setVisibleIndexZ(1);
						isNodesUpdateRequired = true;
					}
				}
				break;
			case alhpa:
				settings.decAlpha();
				break;
			case intensity:
				settings.decIntensity();
				break;
			case light:
				if (qfsModel.getSelectedLight() != null)
					qfsModel.getSelectedLight().getPositionBuff().z += inc;
				break;
			default:
				break;
			}
			break;
		case GLFW.GLFW_KEY_PAGE_UP:
			switch (qfsModel.getMode()) {
			case light:
				if (qfsModel.getSelectedLight() != null)
					qfsModel.getSelectedLight().getPositionBuff().y += inc;
				break;
			default:
				break;
			}
			break;
		case GLFW.GLFW_KEY_PAGE_DOWN:
			switch (qfsModel.getMode()) {
			case light:
				if (qfsModel.getSelectedLight() != null)
					qfsModel.getSelectedLight().getPositionBuff().y -= inc;
				break;
			default:
				break;
			}
			break;
		case GLFW.GLFW_KEY_KP_4:
			if (keyEvents.isCtrlPressed) {
				scene.getQfsFields().getBaseField().incRotation(0.0f, inc, 0.0f);
			}
			break;
		case GLFW.GLFW_KEY_KP_6:
			if (keyEvents.isCtrlPressed) {
				scene.getQfsFields().getBaseField().incRotation(0.0f, -inc, 0.0f);
			}
			break;
		case GLFW.GLFW_KEY_KP_2:
			if (keyEvents.isCtrlPressed) {
				scene.getQfsFields().getBaseField().incRotation(-inc, 0.0f, 0.0f);
			}
			break;
		case GLFW.GLFW_KEY_KP_8:
			if (keyEvents.isCtrlPressed) {
				scene.getQfsFields().getBaseField().incRotation(inc, 0.0f, 0.0f);
			}
			break;
		case GLFW.GLFW_KEY_KP_3:
			if (keyEvents.isCtrlPressed) {
				scene.getQfsFields().getBaseField().incRotation(0.0f, 0.0f, inc);
			}
			break;
		case GLFW.GLFW_KEY_KP_1:
			if (keyEvents.isCtrlPressed) {
				scene.getQfsFields().getBaseField().incRotation(0.0f, 0.0f, -inc);
			}
			break;
		case GLFW.GLFW_KEY_KP_5:
			if (keyEvents.isCtrlPressed) {
				scene.getQfsFields().getBaseField().setRotation(0, 0, 0);
			}
			break;
		case GLFW.GLFW_KEY_SPACE:
			if (qfsModel.isSimulating() && selectedNode != null)
				selectedNode.incPosition(new Vector3d(0, 0, qfsProject.rainModel.getForce()));
			break;
		}
		

		return false;
	}

	@Override
	public boolean keyCallback(long handle, int key, int scanCode, int action, int mods) {
		isReleased = action == GLFW.GLFW_RELEASE;
		
//		System.out.println("scanCode: " + scanCode);
//		System.out.println("action: " + action);
//		System.out.println("mods: " + mods);

		boolean isShiftPressed = (mods & GLFW.GLFW_MOD_SHIFT) == GLFW.GLFW_MOD_SHIFT;
		//System.out.println("isShiftPressed: " + isShiftPressed);
		
		if (isReleased) {
			switch (key) {
			case GLFW.GLFW_KEY_N:
				qfsModel.setMode(Mode.normal);
				break;
			case GLFW.GLFW_KEY_R:
				qfsModel.setMode(Mode.rain);
				nuklearModel.toggleFrame(Frame.rain);
				break;
			case GLFW.GLFW_KEY_G:
				qfsModel.setMode(Mode.alhpa);
				break;
			case GLFW.GLFW_KEY_I:
				qfsModel.setMode(Mode.intensity);
				break;
			case GLFW.GLFW_KEY_L:
				qfsModel.setMode(Mode.light);
				break;
			case GLFW.GLFW_KEY_F4:
				scene.toggleAnimation();
				isAnimating = scene.isAnimated();
				break;
			case GLFW.GLFW_KEY_F5:
				scene.updateWalls();
				break;
			case GLFW.GLFW_KEY_F6:
				if (isShiftPressed) {
					// Release all nodes that was fixed by the list of customNodeFixed.
					for (QFSNode qfsNode : customNodeFixed) {
						qfsNode.setFixed(false);
					}
				}
				else {
					if (!selectedNode.isFixed()) {
						// Set the node position and make it fixed.
						selectedNode.getPositionBuff().z = qfsProject.rainModel.getForce();
						selectedNode.setFixed(true);
						// Remember that this node is set fixed.
						customNodeFixed.add(selectedNode);
					}
					else {
						// Release the node.
						selectedNode.setFixed(false);
					}
				}
				break;
			case GLFW.GLFW_KEY_F8:
				qfsModel.toggleSimulation();
				break;
			case GLFW.GLFW_KEY_F10:
				qfsProject.reset();
				slitWall.reset();
				break;
			case GLFW.GLFW_KEY_F11:
				Screenshot.prepareScreenshot(4, 4, 0xFF);
				break;
			case GLFW.GLFW_KEY_F12:
				settings.setRenderType(settings.getRenderType() == RenderType.all? RenderType.slice : RenderType.all);
				updateNodesProperties();
				break;
			}
		}

		return false;
	}

	private void moveCamera(float x, float y, float z) {
		qfsProject.camera.movePosition(
				x * Constants.CAMERA_MOVE_SPEED,
				y * Constants.CAMERA_MOVE_SPEED,
				z * Constants.CAMERA_MOVE_SPEED
				);
	}

	private void updateSelectedNode() {
		if (selectedNode != null) {
			selectedNode.setSelected(false);
		}

		selectedNode = scene.getQfsFields().getNode(qfsModel.getCurrentMouseNodeIndex());
		if (selectedNode != null) {
			selectedNode.setSelected(true);
		}
	}

	private void updateTerrain() {
		if (qfsProject.terrain.isVisible())
			qfsProject.terrain.updateTransformationMatrix();
	}
	
	private void checkSettingsChanged() {
		if (qfsProject.isResetting()) {
			setCustomTitle(qfsProject.getCurrentFilename());
			nuklearModel.showFrame(Frame.progress);
			try {
				qfsProject.terrain.getMaterial().setTexture(new Texture(loader.loadTexture(qfsProject.terrain.getTerrainTextureFilePath())));
			} catch (Exception e) {
				String message = String.format("Texture file '%s' not found!", qfsProject.terrain.getTerrainTextureFilePath());
				nuklearModel.showErrorDialog("Loading terrain texture", message);
			}
			scene.getEntities().clear();
			isLoadingStarted = true;
			qfsModel.setLoadingReady(false);
			// Build the Quantum Fields or the Ether.
			scene.getQfsFields().build();
			waitAfterReset = 0;
		}
		
		if (!isLoadingStarted) {
			if (qfsProject.isFileSaved()) {
				qfsProject.setFileSaved(false);
				setCustomTitle(qfsProject.getCurrentFilename());
			}
			
			if (qfsModel.isChanged()) {
				qfsModel.setChanged(false);
				isNodesUpdateRequired = true;
			}

			if (settings.isChanged()) {
				settings.setChanged(false);
				updateTerrain();
				isNodesUpdateRequired = true;
			}

			if (slitWall.isChanged()) {
				slitWall.setChanged(false);
				if (slitWall.isActive()) scene.drawSlitWall();
				else scene.clearHiLightNodes();
				isNodesUpdateRequired = true;
			}

			if (slitWall.isUpdated()) {
				slitWall.setUpdated(false);
				scene.makeWallFromHiLightNodes();
				isNodesUpdateRequired = true;
			}

			if (isNodesUpdateRequired) {
				updateNodesProperties();
				isNodesUpdateRequired = false;
			}
		}
	}

	private void waitForRenderingReady() {
		while (!isRenderingReady) {
			Utils.sleepMS(1);
		}
	}

	private void calcNextPhysicsFrame() {
		isCalcNextPhysicsReady = false;
		// While the GPU is rendering on the current buffer,
		// swap the buffer to calculate physics on another buffer.
		QFSNode.swapBuffer();

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// Sleep shortly, to make sure renderFrame() is started before calculating next frame.
				// This will prevents render flickering.
				while (isRenderingReady) {
					Utils.sleepMS(1);
				}
				// Not sure why, but some nodes weren't rendered. Wait at least for 8 ms to fix it.
				Utils.sleepMS(8);

				PerformanceMonitor.start(Measurement.totalCPURenderTime);
				scene.resetTasks();

				int numThreads = qfsProject.numThreads;
				int numNodes = scene.getEntities().size();
				int segmentSize = numNodes > numThreads? numNodes / numThreads + 1 : numNodes;

				PerformanceMonitor.start(Measurement.physics);

				if (waitAfterReset <= 0) {
					simulateRain();
					oscillateSelectedNodes();
				}
				else {
					waitAfterReset--;
				}
				detector();
				scene.processPhysicsTasks(numThreads, segmentSize);
				scene.waitForAllTasksReady();

				if (qfsModel.isSimulating())
					PerformanceMonitor.stop(Measurement.physics);
				else 
					PerformanceMonitor.reset(Measurement.physics);

				PerformanceMonitor.start(Measurement.updateMatrix);
				scene.processMatrixUpdateTasks(numThreads, segmentSize);
				scene.waitForAllTasksReady();
				PerformanceMonitor.stop(Measurement.updateMatrix);

				PerformanceMonitor.stop(Measurement.totalCPURenderTime);
				isCalcNextPhysicsReady = true;
			}

		});
		thread.start();
	}

	private void detector() {
		if (nuklearModel.isFrameVisible(Frame.detector)) {
			QFSNode node = scene.getNodeByIndex(detectorModel.getIgnitorNode());
			if (node != null) {
				node.setCustomScale(2.0);
				node.setCustomColor(new Vector4f(0f, 1f, 0f, 1f));
				if (detectorModel.isIgnited())
					detectorModel.ignite(node, qfsProject.rainModel.getForce());
			}

			node = scene.getNodeByIndex(detectorModel.getDetectorNode());
			if (node != null) {
				node.setCustomScale(2.0);
				node.setCustomColor(new Vector4f(1f, 1f, 0f, 1f));
				detectorModel.detect(node);
			}
		}
	}

	private void simulateRain() {
		if (qfsModel.isSimulating() && qfsProject.rainModel.isActive()) {
			Vector3i index = new Vector3i();

			for (int i = 0; i < qfsProject.rainModel.getIterations(); i++) {
				index.x = (int) (Math.max(2, Math.min(Math.random() * qfsProject.getDimensionX(), qfsProject.getDimensionX() - 2)));
				index.y = (int) (Math.max(2, Math.min(Math.random() * qfsProject.getDimensionY(), qfsProject.getDimensionY() - 2)));
				index.z = settings.getVisibleIndexZ();

				QFSNode node = scene.getNodeByIndex(index);
				if (node != null)
					node.incPosition(new Vector3d(0, 0, Math.random() * qfsProject.rainModel.getForce()));
			}

		}
	}

	private void oscillateSelectedNodes() {
		if (qfsModel.isSimulating()) {
			for (Oscillator oscillator : qfsProject.oscillators) {
				QFSNode node = scene.getNodeByIndex(oscillator.nodeIndex);

				Vector3d p = oscillator.oscillate();

				if (node != null && p != null) {
					node.incPosition(p);
				}
			}
		}
	}

	private void waitForCalcNextPhysicsReady() {
		while (!isCalcNextPhysicsReady) {
			Utils.sleepMS(1);
		}
	}

	public void startMonitoringDeadThreads() {
		new Thread(new Runnable() {

			private long currentTime;
			private int lastNumTask;
			private int lastNumTask2;

			@Override
			public void run() {
				while (glWindow.getWindowHandle() != 0) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}

					if (System.currentTimeMillis() - currentTime > 100) {
						if (scene.getNumTask() != 0 && scene.getNumTask() == lastNumTask2) {
							System.out.println("numTask: " + scene.getNumTask() + " == lastNumTask: " + lastNumTask2 + " - dead threads found! This should never happens!");
							System.out.println("Status: " + scene.status);
							scene.taskCompleted();
							isCalcNextPhysicsReady = true;
						}
						currentTime = System.currentTimeMillis();
						lastNumTask = scene.getNumTask();
						lastNumTask2 = lastNumTask;
					}
				}
			}
		}).start();
	}

	@Override
	public void cleanUp() {
		scene.stopAnimation();
		renderer.cleanup();
		loader.cleanup();
		super.cleanUp();
	}

	@Override
	public void render() {
		if (Screenshot.isTakingScreenShot()) {
			Screenshot.updateScreenshotViewport();
		}
		else if (glWindow.isResize()) {
			GL11.glViewport(0, 0, glWindow.getWidth(), glWindow.getHeight());
		}

		waitForCalcNextPhysicsReady();

		checkSettingsChanged();

		if (isLoadingStarted) {
			// Make sure it is not doing any calculation while updating new scene.
			updateNodesArray();
		}
		else {
			waitForRenderingReady();
			// In the mean time, start the calculation of the next frame while rendering.
			calcNextPhysicsFrame();
		}
		
		PerformanceMonitor.start(Measurement.sendToGPU);
		isRenderingReady = false;
		GLFW.glfwSwapInterval(qfsProject.getGlSwapInterval());
		if (qfsProject.terrain.isVisible())
			renderer.processTerrain(qfsProject.terrain);
		renderer.render(qfsProject.camera, scene);
		isRenderingReady = true;
		PerformanceMonitor.stop(Measurement.sendToGPU);
		
		scene.runAnimation();
		
		super.render();

		if (Screenshot.isTakingScreenShot()) {
			Screenshot.takeScreenShot();
		}
	}

}
