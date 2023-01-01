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
package org.smartblackbox.core.qfs.settings;

import java.util.ArrayList;
import java.util.List;

import org.ini4j.Wini;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.smartblackbox.core.qfs.opengl.model.Camera;
import org.smartblackbox.core.qfs.opengl.model.DetectorModel;
import org.smartblackbox.core.qfs.opengl.model.Material;
import org.smartblackbox.core.qfs.opengl.model.ObjFileModel;
import org.smartblackbox.core.qfs.opengl.model.Oscillator;
import org.smartblackbox.core.qfs.opengl.model.QFSModel;
import org.smartblackbox.core.qfs.opengl.model.RainModel;
import org.smartblackbox.core.qfs.opengl.model.Scene;
import org.smartblackbox.core.qfs.opengl.model.Terrain;
import org.smartblackbox.core.qfs.opengl.model.entity.Entity;
import org.smartblackbox.core.qfs.opengl.model.entity.QFSNode;
import org.smartblackbox.core.qfs.opengl.model.lights.Light;
import org.smartblackbox.core.qfs.opengl.utils.OBJFormatLoader;
import org.smartblackbox.core.utils.AbstractSettings;
import org.smartblackbox.core.utils.ISettings;

public class QFSProject extends AbstractSettings implements ISettings {

	private static QFSProject instance;

	public static int MAX_NUM_THREADS = Runtime.getRuntime().availableProcessors();
	public int numThreads = MAX_NUM_THREADS;

	private boolean isResetting;
	
	/**
	 * Set the universe dimension.
	 * 0: number of x nodes
	 * 1: number of y nodes
	 * 2: number of z nodes
	 */
	private Vector3i dimension = new Vector3i();
	
	private double constantFrequency = 1.0;
	private double constantLightSpeed = 1;
	private double radiation = 1.0;

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
		isResetting = qfsProject.isResetting;
		dimension.x = qfsProject.getDimensionX();
		dimension.y = qfsProject.getDimensionY();
		dimension.z = qfsProject.getDimensionZ();
		constantFrequency = qfsProject.constantFrequency;
		constantLightSpeed = qfsProject.constantLightSpeed;
		radiation = qfsProject.radiation;

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
		String s;
		
		setConstantFrequency(((s = ini.get("QuantumField", "constantFrequency")) == null? 10 : Double.parseDouble(s)));
		radiation = ((s = ini.get("QuantumField", "radiation")) == null? 1.0 : Double.parseDouble(s));
		int x = ((s = ini.get("QuantumField", "dimension.x")) == null? 15 : Integer.parseInt(s));
		int y = ((s = ini.get("QuantumField", "dimension.y")) == null? 15 : Integer.parseInt(s));
		int z = ((s = ini.get("QuantumField", "dimension.z")) == null? 15 : Integer.parseInt(s));
		setDimension(x, y, z);

		numThreads = ((s = ini.get("Common", "numThreads")) == null? MAX_NUM_THREADS : Integer.parseInt(s));
		if (numThreads >= MAX_NUM_THREADS) numThreads = MAX_NUM_THREADS;

		camera.setFieldOfFiew(((s = ini.get("Camera", "fieldOfFiew")) == null? 45 : Float.parseFloat(s)));
		camera.getPosition().x = ((s = ini.get("Camera", "position.x")) == null? 0   : Float.parseFloat(s));
		camera.getPosition().y = ((s = ini.get("Camera", "position.y")) == null? 0   : Float.parseFloat(s));
		camera.getPosition().z = ((s = ini.get("Camera", "position.z")) == null? 120 : Float.parseFloat(s));
		camera.getRotation().x = ((s = ini.get("Camera", "rotation.x")) == null? 0   : Float.parseFloat(s));
		camera.getRotation().y = ((s = ini.get("Camera", "rotation.y")) == null? 0   : Float.parseFloat(s));
		camera.getRotation().z = ((s = ini.get("Camera", "rotation.z")) == null? 1   : Float.parseFloat(s));
		baseRotation.x = ((s = ini.get("Base", "rotation.x")) == null? 0 : Double.parseDouble(s));
		baseRotation.y = ((s = ini.get("Base", "rotation.y")) == null? 0 : Double.parseDouble(s));
		baseRotation.z = ((s = ini.get("Base", "rotation.z")) == null? 0 : Double.parseDouble(s));
		
		terrain.loadFromFile(ini, "Terrain", index);

		loadLights(ini);
		
		settings.loadFromFile(ini, "QFSSettings",  0);
		slitWall.loadFromFile(ini, "SlitWall", 0);
		
		String value = ini.get("Oscillators", "Size");
		int size = value.isEmpty()? 0 : Integer.parseInt(value);
		oscillators.clear();
		for (int i = 0; i < size; i++) {
			Oscillator oscillator = new Oscillator("", new Vector3i());
			oscillator.loadFromFile(ini, "Oscillator", index);
			oscillators.add(oscillator);
		}

		rainModel.loadFromFile(ini, "Rain", index);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (qfsModel.isLoadingReady()) {
					loadWalls(ini);
				}
			}
		}).start();
		
		if (oscillators.size() > 0)
			qfsModel.selectedOscillator = oscillators.get(0);
		
		if (settings.getVisibleIndexZ() >= dimension.z) {
			settings.setVisibleIndexZ(dimension.z / 2);
		}
		
		qfsModel.getCurrentMouseNodeIndex().x = dimension.x / 2;
		qfsModel.getCurrentMouseNodeIndex().y = dimension.y / 2;
		qfsModel.getCurrentMouseNodeIndex().z = dimension.z / 2;
	
		reset();
	}

	@Override
	public void saveToFile(Wini ini, String section, int index) {
		ini.put("Common", "numThreads", numThreads);

		ini.put("QuantumField", "constantFrequency", constantFrequency);
		ini.put("QuantumField", "radiation", radiation);
		ini.put("QuantumField", "dimension.x", dimension.x);
		ini.put("QuantumField", "dimension.y", dimension.y);
		ini.put("QuantumField", "dimension.z", dimension.z);

		saveLights(ini);
		
		baseRotation = scene.getQfsFields().getBaseField().getRotation();

		ini.put("Camera", "fieldOfFiew", camera.getFieldOfFiew());
		ini.put("Camera", "position.x", camera.getPosition().x);
		ini.put("Camera", "position.y", camera.getPosition().y);
		ini.put("Camera", "position.z", camera.getPosition().z);
		ini.put("Camera", "rotation.x", camera.getRotation().x);
		ini.put("Camera", "rotation.y", camera.getRotation().y);
		ini.put("Camera", "rotation.z", camera.getRotation().z);
		ini.put("Base", "rotation.x", baseRotation.x);
		ini.put("Base", "rotation.y", baseRotation.y);
		ini.put("Base", "rotation.z", baseRotation.z);
		
		terrain.saveToFile(ini, "Terrain", index);
		
		settings.saveToFile(ini, "QFSSettings",  0);
		slitWall.saveToFile(ini, "SlitWall", 0);
		
		String value = ini.get("Oscillators", "Size");
		int oldSize = value == null || value.isEmpty()? 0 : Integer.parseInt(value);
		for (int i = 0; i < oldSize; i++) {
			ini.remove("Oscillator" + i);
		}

		int size = oscillators.size();
		ini.put("Oscillators", "Size", size);
		for (int i = 0; i < size; i++) {
			oscillators.get(i).saveToFile(ini, "Oscillator", i);
		}

		rainModel.saveToFile(ini, "Rain", index);
		
		saveWalls(ini);
	}

	private void loadWalls(Wini ini) {
		String s;
		Vector3i nodeIndex;
		
		scene.getWallNodeIndexList().clear();
		
		int numWalls = (s = ini.get("Walls", "numWalls")) == null? 0   : Integer.parseInt(s);
		for (int i = 0; i < numWalls; i++) {
			nodeIndex = new Vector3i();
			nodeIndex.x = (s = ini.get("Wall" + i, "index.x")) == null? -1   : Integer.parseInt(s);
			nodeIndex.y = (s = ini.get("Wall" + i, "index.y")) == null? -1   : Integer.parseInt(s);
			nodeIndex.z = (s = ini.get("Wall" + i, "index.z")) == null? -1   : Integer.parseInt(s);
			QFSNode node = scene.getNodeByIndex(nodeIndex);
			if (node != null) {
				scene.getWallNodeIndexList().add(nodeIndex);
			}
		}
	}

	private void saveWalls(Wini ini) {
		List<Vector3i> walls = scene.getWallNodeIndexList();
		int i = 0;
		ini.put("Walls", "numWalls", walls.size());
		for (Vector3i nodeIndex : walls) {
			ini.put("Wall" + i, "index.x", nodeIndex.x);
			ini.put("Wall" + i, "index.y", nodeIndex.y);
			ini.put("Wall" + i, "index.z", nodeIndex.z);
			i++;
		}
	}

	public void loadLights(Wini ini) {
		String s;
		Vector3f vec3f;
		Vector3d vec3d;
		
		lights.clear();
		
		int numLights = (s = ini.get("Lights", "numLights")) == null? 0   : Integer.parseInt(s);
		
		for (int i = 0; i < numLights; i++) {
			Light light = createLight();
			light.setName((s = ini.get("Lights" + i, "name")) == null? "" : s);
			vec3d = light.getPosition();
			vec3d.x = (s = ini.get("Lights" + i, "position.x")) == null? 0   : Float.parseFloat(s);
			vec3d.y = (s = ini.get("Lights" + i, "position.y")) == null? 10  : Float.parseFloat(s);
			vec3d.z = (s = ini.get("Lights" + i, "position.z")) == null? 0   : Float.parseFloat(s);
			light.setPosition(vec3d);
			vec3d = light.getRotation();
			vec3d.x = (s = ini.get("Lights" + i, "rotation.x")) == null? 0   : Float.parseFloat(s);
			vec3d.y = (s = ini.get("Lights" + i, "rotation.y")) == null? 0   : Float.parseFloat(s);
			vec3d.z = (s = ini.get("Lights" + i, "rotation.z")) == null? 0   : Float.parseFloat(s);
			vec3d = light.getDirection();
			vec3d.x = (s = ini.get("Lights" + i, "direction.x")) == null? 0   : Float.parseFloat(s);
			vec3d.y = (s = ini.get("Lights" + i, "direction.y")) == null? -1  : Float.parseFloat(s);
			vec3d.z = (s = ini.get("Lights" + i, "direction.z")) == null? 0   : Float.parseFloat(s);
			vec3f = light.getColor();
			vec3f.x = (s = ini.get("Lights" + i, "color.x")) == null? 1   : Float.parseFloat(s);
			vec3f.y = (s = ini.get("Lights" + i, "color.y")) == null? 1   : Float.parseFloat(s);
			vec3f.z = (s = ini.get("Lights" + i, "color.z")) == null? 1   : Float.parseFloat(s);
			light.setColor(vec3f);

			light.setIntensity((s = ini.get("Lights" + i, "intensity")) == null? 1 : Float.parseFloat(s));
			light.setConstant((s = ini.get("Lights" + i, "constant")) == null? 1 : Double.parseDouble(s));
			light.setLinear((s = ini.get("Lights" + i, "linear")) == null? 1 : Double.parseDouble(s));
			light.setExponent((s = ini.get("Lights" + i, "exponent")) == null? 1 : Double.parseDouble(s));
			light.setScale((s = ini.get("Lights" + i, "scale")) == null? 1 : Float.parseFloat(s));
			light.setSpotLight((s = ini.get("Lights" + i, "spotlight")) == null? false : Boolean.parseBoolean(s));
			light.setCutOff((s = ini.get("Lights" + i, "cutOff")) == null? 1 : Float.parseFloat(s));

			Material m = light.getSpotLightHolder().getMaterial();
			m.getAmbientColor().x = (s = ini.get("Lights" + i, "spotLight.ambient.x")) == null? 1 : Float.parseFloat(s);
			m.getAmbientColor().y = (s = ini.get("Lights" + i, "spotLight.ambient.y")) == null? 1 : Float.parseFloat(s);
			m.getAmbientColor().z = (s = ini.get("Lights" + i, "spotLight.ambient.z")) == null? 1 : Float.parseFloat(s);
			m.setAmbientIntensity((s = ini.get("Lights" + i, "spotLight.ambientIntensity")) == null? 1 : Float.parseFloat(s));
			m.getDiffuseColor().x = (s = ini.get("Lights" + i, "spotLight.diffuse.x")) == null? 1 : Float.parseFloat(s);
			m.getDiffuseColor().y = (s = ini.get("Lights" + i, "spotLight.diffuse.y")) == null? 1 : Float.parseFloat(s);
			m.getDiffuseColor().z = (s = ini.get("Lights" + i, "spotLight.ambient.z")) == null? 1 : Float.parseFloat(s);
			m.setDiffuseIntensity((s = ini.get("Lights" + i, "spotLight.diffuseIntensity")) == null? 1 : Float.parseFloat(s));
			m.getSpecularColor().x = (s = ini.get("Lights" + i, "spotLight.specular.x")) == null? 1 : Float.parseFloat(s);
			m.getSpecularColor().y = (s = ini.get("Lights" + i, "spotLight.specular.y")) == null? 1 : Float.parseFloat(s);
			m.getSpecularColor().z = (s = ini.get("Lights" + i, "spotLight.specular.z")) == null? 1 : Float.parseFloat(s);
			m.setSpecularIntensity((s = ini.get("Lights" + i, "spotLight.specularIntensity")) == null? 1 : Float.parseFloat(s));
			m.setShininess((s = ini.get("Lights" + i, "spotLight.shininess")) == null? 1 : Float.parseFloat(s));
		}
		
	}
	
	public void saveLights(Wini ini) {
		int i = 0;
		ini.put("Lights", "numLights", lights.size());
		for (Light light : lights) {
			ini.put("Lights" + i, "name", light.getName());
			ini.put("Lights" + i, "position.x", light.getPosition().x);
			ini.put("Lights" + i, "position.y", light.getPosition().y);
			ini.put("Lights" + i, "position.z", light.getPosition().z);
			ini.put("Lights" + i, "rotation.x", light.getRotation().x);
			ini.put("Lights" + i, "rotation.y", light.getRotation().y);
			ini.put("Lights" + i, "rotation.z", light.getRotation().z);
			ini.put("Lights" + i, "direction.x", light.getDirection().x);
			ini.put("Lights" + i, "direction.y", light.getDirection().y);
			ini.put("Lights" + i, "direction.z", light.getDirection().z);
			ini.put("Lights" + i, "color.x", light.getColor().x);
			ini.put("Lights" + i, "color.y", light.getColor().y);
			ini.put("Lights" + i, "color.z", light.getColor().z);
			ini.put("Lights" + i, "intensity", light.getIntensity());
			ini.put("Lights" + i, "constant", light.getConstant());
			ini.put("Lights" + i, "linear", light.getLinear());
			ini.put("Lights" + i, "exponent", light.getExponent());
			ini.put("Lights" + i, "scale", light.getScale());
			ini.put("Lights" + i, "spotlight", light.isSpotLight());
			ini.put("Lights" + i, "cutOff", light.getCutOff());
			Material m = light.getSpotLightHolder().getMaterial();
			ini.put("Lights" + i, "spotLight.ambient.x", m.getAmbientColor().x);
			ini.put("Lights" + i, "spotLight.ambient.y", m.getAmbientColor().y);
			ini.put("Lights" + i, "spotLight.ambient.z", m.getAmbientColor().z);
			ini.put("Lights" + i, "spotLight.ambientIntensity", m.getAmbientIntensity());
			ini.put("Lights" + i, "spotLight.diffuse.x", m.getDiffuseColor().x);
			ini.put("Lights" + i, "spotLight.diffuse.y", m.getDiffuseColor().y);
			ini.put("Lights" + i, "spotLight.diffuse.z", m.getDiffuseColor().z);
			ini.put("Lights" + i, "spotLight.diffuseIntensity", m.getDiffuseIntensity());
			ini.put("Lights" + i, "spotLight.specular.x", m.getSpecularColor().x);
			ini.put("Lights" + i, "spotLight.specular.y", m.getSpecularColor().y);
			ini.put("Lights" + i, "spotLight.specular.z", m.getSpecularColor().z);
			ini.put("Lights" + i, "spotLight.specularIntensity", m.getSpecularIntensity());
			ini.put("Lights" + i, "spotLight.shininess", m.getShininess());
			i++;
		}
	}
	
	/**
	 * Reset the project.
	 * 
	 * Use {@link #isResetting()} to check if reset is required.
	 * If true, then define your customized reset.
	 */
	public void reset() {
		isResetting = true;
		setConstantFrequency(constantFrequency);
	}

	/**
	 * Use this function to check if reset is required.
	 * If true, then define your customized reset.
	 * 
	 * @return true if {@link #reset()} has been called.
	 */
	public boolean isResetting() {
		if (isResetting) {
			isResetting = false;
			return true;
		}
		return false;
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

	public double getConstantFrequency() {
		return constantFrequency;
	}

	public void setConstantFrequency(double value) {
		if (value > 1) value = 1;
		if (value < 0) value = 0;
		constantFrequency = value;
		constantLightSpeed = value * value;
	}

	public double getConstantLightSpeed() {
		return constantLightSpeed;
	}

	public double getRadiation() {
		return radiation;
	}

	public void setRadiation(double radiation) {
		this.radiation = radiation;
	}

	public Light createLight(Entity parent, ObjFileModel modelLightBulb, ObjFileModel modelSpotLight, Vector3d position, Vector3d rotation, double scale) {
		Light light = new Light(parent, modelLightBulb, modelSpotLight, position, rotation, scale);
		addLight(light);
		return light;
	}

	public Light createLight() {
		ObjFileModel modelLightBulb = new OBJFormatLoader().loadOBJModel("models/sphere2.obj");
		ObjFileModel modelSpotLight = new OBJFormatLoader().loadOBJModel("models/spotLight.obj");
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
