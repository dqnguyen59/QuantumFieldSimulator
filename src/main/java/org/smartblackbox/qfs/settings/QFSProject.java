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
package org.smartblackbox.qfs.settings;

import java.util.ArrayList;
import java.util.List;

import org.ini4j.Wini;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.opengl.model.Camera;
import org.smartblackbox.qfs.opengl.model.DetectorModel;
import org.smartblackbox.qfs.opengl.model.Material;
import org.smartblackbox.qfs.opengl.model.ObjFileModel;
import org.smartblackbox.qfs.opengl.model.Oscillator;
import org.smartblackbox.qfs.opengl.model.QFSModel;
import org.smartblackbox.qfs.opengl.model.RainModel;
import org.smartblackbox.qfs.opengl.model.Scene;
import org.smartblackbox.qfs.opengl.model.Terrain;
import org.smartblackbox.qfs.opengl.model.entity.Entity;
import org.smartblackbox.qfs.opengl.model.entity.QFSNode;
import org.smartblackbox.qfs.opengl.model.lights.Light;
import org.smartblackbox.qfs.opengl.utils.OBJFormatLoader;
import org.smartblackbox.utils.AbstractSettings;
import org.smartblackbox.utils.ISettings;

public class QFSProject extends AbstractSettings implements ISettings {

	private static QFSProject instance;

	private boolean isChanged;
	private boolean isFileSaved;
	
	/**
	 * Set the universe dimension.
	 * 0: number of x nodes
	 * 1: number of y nodes
	 * 2: number of z nodes
	 */
	private Vector3i dimension = new Vector3i();
	
	private double constantFrequency = 0.60;
	private double constantWaveSpeed = 1.00;
	private double constantRadiation = 1.00;

	// Don't set the glSwapInterval below 1! It will cause instability of the application and freeze.
	private int glSwapInterval = 1;

	private QFSModel qfsModel = new QFSModel();
	public Camera camera = new Camera();
	public Scene scene;
	public Terrain terrain;
	public Vector3d baseRotation = new Vector3d(-66.6, 0, 0);
	
	public QFSSettings settings = new QFSSettings();
	public SlitWallSettings slitWall = new SlitWallSettings();
	public DetectorModel detectorModel = new DetectorModel();
	public RainModel rainModel = new RainModel(); 
	
	private ArrayList<Light> lights = new ArrayList<Light>();

	public List<Oscillator> oscillators = new ArrayList<Oscillator>();

	public static QFSProject getInstance() {
		if (instance == null) {
			instance = new QFSProject();
			instance.scene = new Scene();
		}
		return instance;
	}
	
	public QFSProject() {
		super();
	}

	public QFSProject(QFSProject qfsProject) {
		super();
		set(qfsProject);
	}

	public void set(QFSProject qfsProject) {
		isChanged = qfsProject.isChanged;
		isFileSaved = qfsProject.isFileSaved;
		dimension.x = qfsProject.getDimensionX();
		dimension.y = qfsProject.getDimensionY();
		dimension.z = qfsProject.getDimensionZ();
		constantFrequency = qfsProject.constantFrequency;
		constantWaveSpeed = qfsProject.constantWaveSpeed;
		constantRadiation = qfsProject.constantRadiation;
		glSwapInterval = qfsProject.glSwapInterval;

		camera.setFieldOfFiew(qfsProject.camera.getFieldOfFiew());
		camera.setZ_near(qfsProject.camera.getZ_near());
		camera.setZ_far(qfsProject.camera.getZ_far());
		Vector3f pos = qfsProject.camera.getPosition();
		camera.setPosition(pos.x, pos.y, pos.z);
		Vector3f rot = qfsProject.camera.getRotation();
		camera.setRotation(rot.x, rot.y, rot.z);
		baseRotation.x = qfsProject.baseRotation.x;
		baseRotation.y = qfsProject.baseRotation.y;
		baseRotation.z = qfsProject.baseRotation.z;
	}
	
	@Override
	public QFSProject clone() {
		return new QFSProject(this);
	}

	@Override
	public void loadFromFile(Wini ini, String section, int index) {
		super.loadFromFile(ini, section, index);
		
		setConstantFrequency(getDouble("QuantumField", "constantFrequency", 1.0));
		setConstantRadiation(getDouble("QuantumField", "constantRadiation", 1.0));
		setGlSwapInterval(getInt("QuantumField", "glSwapInterval", 1));
		int x = getInt("QuantumField", "dimension.x", 15);
		int y = getInt("QuantumField", "dimension.y", 15);
		int z = getInt("QuantumField", "dimension.z", 15);
		setDimension(x, y, z, false);

		camera.setFieldOfFiew(getFloat("Camera", "fieldOfFiew", 45));
		camera.getPosition().x = getFloat("Camera", "position.x", 0);
		camera.getPosition().y = getFloat("Camera", "position.y", 0);
		camera.getPosition().z = getFloat("Camera", "position.z", 120);
		camera.getRotation().x = getFloat("Camera", "rotation.x", 0);
		camera.getRotation().y = getFloat("Camera", "rotation.y", 0);
		camera.getRotation().z = getFloat("Camera", "rotation.z", 1);
		baseRotation.x = getDouble("Base", "rotation.x", 0.0);
		baseRotation.y = getDouble("Base", "rotation.y", 0.0);
		baseRotation.z = getDouble("Base", "rotation.z", 0.0);
		
		terrain.loadFromFile(ini, "Terrain", index);

		loadLights(ini);
		
		settings.loadFromFile(ini, "QFSSettings",  0);
		slitWall.loadFromFile(ini, "SlitWall", 0);
		
		int size = getInt("Oscillators", "Size", 0);
		oscillators.clear();
		for (int i = 0; i < size; i++) {
			Oscillator oscillator = new Oscillator("", new Vector3i());
			oscillator.loadFromFile(ini, "Oscillator", i);
			oscillators.add(oscillator);
		}

		rainModel.loadFromFile(ini, "Rain", index);
		detectorModel.loadFromFile(ini, "Detector", index);
		
		if (oscillators.size() > 0)
			qfsModel.selectedOscillator = oscillators.get(0);
		
		if (settings.getVisibleIndexX() >= dimension.x) {
			settings.setVisibleIndexX(dimension.x / 2);
		}
		
		if (settings.getVisibleIndexY() >= dimension.y) {
			settings.setVisibleIndexY(dimension.y / 2);
		}
		
		if (settings.getVisibleIndexZ() >= dimension.z) {
			settings.setVisibleIndexZ(dimension.z / 2);
		}
		
		qfsModel.getCurrentMouseNodeIndex().x = dimension.x / 2;
		qfsModel.getCurrentMouseNodeIndex().y = dimension.y / 2;
		qfsModel.getCurrentMouseNodeIndex().z = dimension.z / 2;

		reset();
		qfsModel.setLoadingReady(false);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (!qfsModel.isLoadingReady()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
				loadWalls(ini);
			}
		}).start();
		
	}

	@Override
	public void saveToFile(Wini ini, String section, int index) {
		super.saveToFile(ini, section, index);
		put("QuantumField", "constantFrequency", constantFrequency);
		put("QuantumField", "constantRadiation", constantRadiation);
		put("QuantumField", "glSwapInterval", glSwapInterval);
		put("QuantumField", "dimension.x", dimension.x);
		put("QuantumField", "dimension.y", dimension.y);
		put("QuantumField", "dimension.z", dimension.z);

		saveLights(ini);
		
		baseRotation = scene.getQfsFields().getBaseField().getRotation();

		put("Camera", "fieldOfFiew", camera.getFieldOfFiew());
		put("Camera", "position.x", camera.getPosition().x);
		put("Camera", "position.y", camera.getPosition().y);
		put("Camera", "position.z", camera.getPosition().z);
		put("Camera", "rotation.x", camera.getRotation().x);
		put("Camera", "rotation.y", camera.getRotation().y);
		put("Camera", "rotation.z", camera.getRotation().z);
		put("Base", "rotation.x", baseRotation.x);
		put("Base", "rotation.y", baseRotation.y);
		put("Base", "rotation.z", baseRotation.z);
		
		terrain.saveToFile(ini, "Terrain", index);
		
		settings.saveToFile(ini, "QFSSettings",  0);
		slitWall.saveToFile(ini, "SlitWall", 0);
		
		String value = get("Oscillators", "Size");
		int oldSize = value == null || value.isEmpty()? 0 : Integer.parseInt(value);
		for (int i = 0; i < oldSize; i++) {
			ini.remove("Oscillator" + i);
		}

		int size = oscillators.size();
		put("Oscillators", "Size", size);
		for (int i = 0; i < size; i++) {
			oscillators.get(i).saveToFile(ini, "Oscillator", i);
		}

		rainModel.saveToFile(ini, "Rain", index);
		detectorModel.saveToFile(ini, "Detector", index);
		
		saveWalls(ini);
		isFileSaved = true;
	}

	private void loadWalls(Wini ini) {
		Vector3i nodeIndex;
		
		scene.clearAllWalls();
		scene.getWallNodeIndexList().clear();
		
		int numWalls = getInt("Walls", "numWalls", 0);
		for (int i = 0; i < numWalls; i++) {
			nodeIndex = new Vector3i();
			nodeIndex.x = getInt("Wall" + i, "index.x", -1);
			nodeIndex.y = getInt("Wall" + i, "index.y", -1);
			nodeIndex.z = getInt("Wall" + i, "index.z", -1);
			QFSNode node = scene.getNodeByIndex(nodeIndex);
			if (node != null) {
				scene.getWallNodeIndexList().add(nodeIndex);
			}
		}
		scene.updateWalls();
	}

	private void saveWalls(Wini ini) {
		List<Vector3i> walls = scene.getWallNodeIndexList();
		int i = 0;
		put("Walls", "numWalls", walls.size());
		for (Vector3i nodeIndex : walls) {
			put("Wall" + i, "index.x", nodeIndex.x);
			put("Wall" + i, "index.y", nodeIndex.y);
			put("Wall" + i, "index.z", nodeIndex.z);
			i++;
		}
	}

	public void loadLights(Wini ini) {
		Vector3f vec3f;
		Vector3d vec3d;
		
		lights.clear();
		
		int numLights = getInt("Lights", "numLights", 0);
		
		for (int i = 0; i < numLights; i++) {
			Light light = createLight();
			light.setName(getString("Lights" + i, "name", ""));
			vec3d = light.getPosition();
			vec3d.x = getFloat("Lights" + i, "position.x", 0);
			vec3d.y = getFloat("Lights" + i, "position.y", 10);
			vec3d.z = getFloat("Lights" + i, "position.z", 0);
			light.setPosition(vec3d);
			vec3d = light.getRotation();
			vec3d.x = getFloat("Lights" + i, "rotation.x", 0);
			vec3d.y = getFloat("Lights" + i, "rotation.y", 0);
			vec3d.z = getFloat("Lights" + i, "rotation.z", 0);
			vec3d = light.getDirection();
			vec3d.x = getFloat("Lights" + i, "direction.x", 0);
			vec3d.y = getFloat("Lights" + i, "direction.y", -1);
			vec3d.z = getFloat("Lights" + i, "direction.z", 0);
			vec3f = light.getColor();
			vec3f.x = getFloat("Lights" + i, "color.x", 1);
			vec3f.y = getFloat("Lights" + i, "color.y", 1);
			vec3f.z = getFloat("Lights" + i, "color.z", 1);
			light.setColor(vec3f);

			light.setIntensity(getFloat("Lights" + i, "intensity", 1));
			light.setConstant(getFloat("Lights" + i, "constant", 1));
			light.setLinear(getFloat("Lights" + i, "linear", 1));
			light.setExponent(getFloat("Lights" + i, "exponent", 1));
			light.setScale(getFloat("Lights" + i, "scale", 1));
			light.setSpotLight(getBool("Lights" + i, "spotlight", false));
			light.setCutOff(getFloat("Lights" + i, "cutOff", 1));

			Material m = light.getSpotLightHolder().getMaterial();
			m.getAmbientColor().x = getFloat("Lights" + i, "spotLight.ambient.x", 1);
			m.getAmbientColor().y = getFloat("Lights" + i, "spotLight.ambient.y", 1);
			m.getAmbientColor().z = getFloat("Lights" + i, "spotLight.ambient.z", 1);
			m.setAmbientIntensity(getFloat("Lights" + i, "spotLight.ambientIntensity", 1));
			m.getDiffuseColor().x = getFloat("Lights" + i, "spotLight.diffuse.x", 1);
			m.getDiffuseColor().y = getFloat("Lights" + i, "spotLight.diffuse.y", 1);
			m.getDiffuseColor().z = getFloat("Lights" + i, "spotLight.ambient.z", 1);
			m.setDiffuseIntensity(getFloat("Lights" + i, "spotLight.diffuseIntensity", 1));
			m.getSpecularColor().x = getFloat("Lights" + i, "spotLight.specular.x", 1);
			m.getSpecularColor().y = getFloat("Lights" + i, "spotLight.specular.y", 1);
			m.getSpecularColor().z = getFloat("Lights" + i, "spotLight.specular.z", 1);
			m.setSpecularIntensity(getFloat("Lights" + i, "spotLight.specularIntensity", 1));
			m.setShininess(getFloat("Lights" + i, "spotLight.shininess", 1));
		}
		
	}
	
	public void saveLights(Wini ini) {
		int i = 0;
		put("Lights", "numLights", lights.size());
		for (Light light : lights) {
			put("Lights" + i, "name", light.getName());
			put("Lights" + i, "position.x", light.getPosition().x);
			put("Lights" + i, "position.y", light.getPosition().y);
			put("Lights" + i, "position.z", light.getPosition().z);
			put("Lights" + i, "rotation.x", light.getRotation().x);
			put("Lights" + i, "rotation.y", light.getRotation().y);
			put("Lights" + i, "rotation.z", light.getRotation().z);
			put("Lights" + i, "direction.x", light.getDirection().x);
			put("Lights" + i, "direction.y", light.getDirection().y);
			put("Lights" + i, "direction.z", light.getDirection().z);
			put("Lights" + i, "color.x", light.getColor().x);
			put("Lights" + i, "color.y", light.getColor().y);
			put("Lights" + i, "color.z", light.getColor().z);
			put("Lights" + i, "intensity", light.getIntensity());
			put("Lights" + i, "constant", light.getConstant());
			put("Lights" + i, "linear", light.getLinear());
			put("Lights" + i, "exponent", light.getExponent());
			put("Lights" + i, "scale", light.getScale());
			put("Lights" + i, "spotlight", light.isSpotLight());
			put("Lights" + i, "cutOff", light.getCutOff());
			Material m = light.getSpotLightHolder().getMaterial();
			put("Lights" + i, "spotLight.ambient.x", m.getAmbientColor().x);
			put("Lights" + i, "spotLight.ambient.y", m.getAmbientColor().y);
			put("Lights" + i, "spotLight.ambient.z", m.getAmbientColor().z);
			put("Lights" + i, "spotLight.ambientIntensity", m.getAmbientIntensity());
			put("Lights" + i, "spotLight.diffuse.x", m.getDiffuseColor().x);
			put("Lights" + i, "spotLight.diffuse.y", m.getDiffuseColor().y);
			put("Lights" + i, "spotLight.diffuse.z", m.getDiffuseColor().z);
			put("Lights" + i, "spotLight.diffuseIntensity", m.getDiffuseIntensity());
			put("Lights" + i, "spotLight.specular.x", m.getSpecularColor().x);
			put("Lights" + i, "spotLight.specular.y", m.getSpecularColor().y);
			put("Lights" + i, "spotLight.specular.z", m.getSpecularColor().z);
			put("Lights" + i, "spotLight.specularIntensity", m.getSpecularIntensity());
			put("Lights" + i, "spotLight.shininess", m.getShininess());
			i++;
		}
	}
	
	/**
	 * Reset the project.
	 * 
	 * Use {@link #isChanged()} to check if reset is required.
	 * If true, then define your customized reset.
	 */
	public void reset() {
		isChanged = true;
		setConstantFrequency(constantFrequency);
		slitWall.reset();
		for (Oscillator oscillator : oscillators) {
			oscillator.reset();
		}
	}

	/**
	 * Use this function to check if reset is required.
	 * If true, then define your customized reset.
	 * 
	 * @return true if {@link #reset()} has been called.
	 */
	public boolean isChanged() {
		if (isChanged) {
			isChanged = false;
			return true;
		}
		return false;
	}

	public boolean isFileSaved() {
		return isFileSaved;
	}

	public void setFileSaved(boolean isFileSaved) {
		this.isFileSaved = isFileSaved;
	}

	public int getDimensionX() {
		return dimension.x;
	}
	
	public int getDimensionY() {
		return dimension.y;
	}
	
	public int getDimensionZ() {
		return dimension.z;
	}
	
	public void getDimension(Vector3i dimension) {
		dimension.x = this.dimension.x;
		dimension.y = this.dimension.y;
		dimension.z = this.dimension.z;
	}

	public void setDimension(Vector3i dimension) {
		this.dimension = dimension;
	}

	public void setDimension(int x, int y, int z) {
		dimension.x = x;
		dimension.y = y;
		dimension.z = z;
		reset();
	}

	public void setDimension(int x, int y, int z, boolean reset) {
		dimension.x = x;
		dimension.y = y;
		dimension.z = z;
		if (reset) reset();
	}

	public double getConstantFrequency() {
		return constantFrequency;
	}

	public void setConstantFrequency(double value) {
		if (value > 1) value = 1;
		if (value < 0) value = 0;
		constantFrequency = value;
		constantWaveSpeed = value * value;
	}

	public double getConstantWaveSpeed() {
		return constantWaveSpeed;
	}

	public double getConstantRadiation() {
		return constantRadiation;
	}

	public void setConstantRadiation(double constantRadiation) {
		this.constantRadiation = constantRadiation;
	}

	public int getGlSwapInterval() {
		return glSwapInterval;
	}

	public void setGlSwapInterval(int glSwapInterval) {
		if (glSwapInterval < 1) this.glSwapInterval = 1;
		else this.glSwapInterval = glSwapInterval;
	}

	public Light createLight(Entity parent, ObjFileModel modelLightBulb, ObjFileModel modelSpotLight, Vector3d position, Vector3d rotation, double scale) {
		Light light = new Light(parent, modelLightBulb, modelSpotLight, position, rotation, scale);
		addLight(light);
		return light;
	}

	public Light createLight() {
		ObjFileModel modelLightBulb = new OBJFormatLoader().loadOBJModel(Constants.MODEL_LIGHT_BULB);
		ObjFileModel modelSpotLight = new OBJFormatLoader().loadOBJModel(Constants.MODEL_LIGHT_CAP);
		Light light = createLight(null, modelLightBulb, modelSpotLight, new Vector3d(0, 10, 0), new Vector3d(0, 0, 0), 1.0);
		light.set(new Vector3f(1.0f, 1.0f, 1.0f), 1.0f, 1.0f, 0.001f, 0.00001f, 60);
		light.setDirection(new Vector3d(0, -1, 0));
		return light;
	}

	public ArrayList<Light> getLights() {
		return lights;
	}

	public void addLight(Light light) {
		lights.add(light);
	}

	public void removeLight(Light light) {
		lights.remove(light);
	}

	public QFSModel getQfsModel() {
		return qfsModel;
	}

	public void setQfsModel(QFSModel qfsModel) {
		this.qfsModel = qfsModel;
	}

}
