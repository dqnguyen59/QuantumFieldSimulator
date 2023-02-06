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

public class ObjFileModel {

	private int id;
	private int vertexCount;
	private Material material;
	
	public ObjFileModel(int id, int vertexCount) {
		this.id = id;
		this.vertexCount = vertexCount;
		this.material = new Material();
	}
	
	public ObjFileModel(int id, int vertexCount, Material material) {
		this.id = id;
		this.vertexCount = vertexCount;
		this.material = material;
	}
	
	public ObjFileModel(int id, int vertexCount, Texture texture) {
		this.id = id;
		this.vertexCount = vertexCount;
		this.material = new Material(texture);
	}
	
	public ObjFileModel(ObjFileModel model, Texture texture) {
		this.id = model.getId();
		this.vertexCount = model.getVertexCount();
		this.material = model.getMaterial();
		this.material.setTexture(texture);
	}
	
	public int getId() {
		return id;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material= material;
	}

	public Texture getTexture() {
		return material.getTexture();			
	}
	
	public void setTexture(Texture texture) {
		material.setTexture(texture);			
	}
	
	public void setTexture(Texture texture, float reflactance) {
		material.setTexture(texture);
		material.setShininess(reflactance);
	}
	
}

