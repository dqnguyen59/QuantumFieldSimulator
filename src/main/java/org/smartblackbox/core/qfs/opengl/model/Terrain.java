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

import org.ini4j.Wini;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.smartblackbox.core.qfs.Constants;
import org.smartblackbox.core.qfs.opengl.utils.OBJFormatLoader;
import org.smartblackbox.core.utils.AbstractSettings;

public class Terrain extends AbstractSettings {

	public static final float SIZE = 10000;
	private static final int VERTEXT_COUNT = 120;
	
	private Vector3f position;
	private ObjFileModel model;
	private Matrix4f transformationMatrix = new Matrix4f();

	private String terrainTexturePath = "";
	private String terrainTextureFile = "";

	private boolean isVisible = true;
	private float scale = 1;
	private Float depthVisibility = 1.0f;
	
	public Terrain(Vector3f position, OBJFormatLoader loader, Material material) {
		this.position = position;
		this.model = generateTerrain(loader);
		this.model.setMaterial(material);
		updateTransformationMatrix();
	}

	private ObjFileModel generateTerrain(OBJFormatLoader loader) {
		int count = VERTEXT_COUNT * VERTEXT_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 3];
		int[] indices = new int[6 * (VERTEXT_COUNT - 1) * (VERTEXT_COUNT - 1)];

		int vertexPointer = 0;
		
		for (int i = 0; i < VERTEXT_COUNT; i++) {
			for (int j = 0; j < VERTEXT_COUNT; j++) {
				vertices[vertexPointer * 3] = j / (VERTEXT_COUNT - 1.0f) * SIZE;
				vertices[vertexPointer * 3 + 1] = 0; // height map
				vertices[vertexPointer * 3 + 2] = i / (VERTEXT_COUNT - 1f) * SIZE;
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				textureCoords[vertexPointer * 2] = j / (VERTEXT_COUNT - 1.0f) * SIZE;
				textureCoords[vertexPointer * 2 + 1] = i / (VERTEXT_COUNT - 1.0f) * SIZE;
				vertexPointer++;
			}
		}
		
		int pointer = 0;
		for (int z = 0; z < VERTEXT_COUNT - 1; z++) {
			for (int x = 0; x < VERTEXT_COUNT - 1; x++) {
				int topLeft = (z * VERTEXT_COUNT) + x;
				int topRight = topLeft + 1;
				int bottomLeft = ((z + 1) * VERTEXT_COUNT) + x;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		
		return loader.loadModel(vertices, textureCoords, normals, indices);
	}

	public Vector3f getPosition() {
		return position;
	}

	public ObjFileModel getModel() {
		return model;
	}

	public Material getMaterial() {
		return model.getMaterial();
	}
	
	public Texture getTexture() {
		return model.getTexture();
	}

	public void updateTransformationMatrix() {
		transformationMatrix.identity().translate(position).scale(scale);
	}
	
	public Matrix4f getTransformationMatrix() {
		return transformationMatrix;
	}

	public String getTerrainTexturePath() {
		return terrainTexturePath;
	}

	public void setTerrainTexturePath(String terrainTexturePath) {
		this.terrainTexturePath = terrainTexturePath;
	}

	public String getTerrainTextureFile() {
		return terrainTextureFile;
	}

	public void setTerrainTextureFile(String terrainTextureFile) {
		this.terrainTextureFile = terrainTextureFile;
	}

	public String getTerrainTextureFilePath() {
		return terrainTexturePath + terrainTextureFile;
	}


	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
		updateTransformationMatrix();
	}

	public Float getDepthVisibility() {
		return depthVisibility;
	}

	public void setDepthVisibility(Float depthVisibility) {
		this.depthVisibility = depthVisibility;
	}

	public float getDepthVisibilityFactor() {
		return (1 - depthVisibility) * Constants.TERRAIN_DEPTH_FADING_FACTOR;
	}

	@Override
	public void loadFromFile(Wini ini, String section, int index) {
		String s;
		
		terrainTexturePath = ((s = ini.get(section, "texturePath")) == null? Constants.TEXTURE_FILE_PATH : s);
		terrainTextureFile = ((s = ini.get(section, "textureFile")) == null? "default2.jpg" : s);
		isVisible = ((s = ini.get(section, "visible")) == null? false : Boolean.parseBoolean(s));
		scale = ((s = ini.get(section, "scale")) == null? 1.0f : Float.parseFloat(s));
		depthVisibility = ((s = ini.get(section, "depthVisibility")) == null? 1.0f : Float.parseFloat(s));
		getMaterial().loadFromFile(ini, section, 0);
		updateTransformationMatrix();
	}

	@Override
	public void saveToFile(Wini ini, String section, int index) {
		ini.put(section, "visible", isVisible);
		ini.put(section, "texturePath", terrainTexturePath);
		ini.put(section, "textureFile", "" + terrainTextureFile);
		ini.put(section, "visible", isVisible);
		ini.put(section, "scale", scale);
		ini.put(section, "depthVisibility", depthVisibility);
		getMaterial().saveToFile(ini, section, 0);
	}

	@Override
	public AbstractSettings clone() {
		// TODO Auto-generated method stub
		return null;
	}

}
