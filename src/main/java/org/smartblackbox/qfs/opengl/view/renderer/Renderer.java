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
package org.smartblackbox.qfs.opengl.view.renderer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.smartblackbox.qfs.opengl.model.Camera;
import org.smartblackbox.qfs.opengl.model.Scene;
import org.smartblackbox.qfs.opengl.model.Terrain;
import org.smartblackbox.qfs.opengl.model.entity.Entity;

public class Renderer {
	
	private EntityRenderer nodeRenderer;
	private TerrainRenderer terrainRenderer;
	
	private static boolean isCulling = false;
	
	public void init() throws Exception {
		terrainRenderer = new TerrainRenderer();
		nodeRenderer = new EntityRenderer();
		terrainRenderer.init();
		nodeRenderer.init();
	}
	
	public synchronized void render(Camera camera, Scene scene) {
		clear();
		terrainRenderer.render(camera, scene);
		nodeRenderer.render(camera, scene);
	}
	
	public void processEntity(Entity entity) {
		if (!entity.isVisible()) return;
		
		List<Entity> nodeList = nodeRenderer.getEntities().get(entity.getModel());
		if (nodeList != null)
			nodeList.add(entity);
		else {
			List<Entity> newNodeList = new ArrayList<>();
			newNodeList.add(entity);
			nodeRenderer.getEntities().put(entity.getModel(), newNodeList);		
		}
	}
	
	public void processTerrain(Terrain terrain) {
		terrainRenderer.getTerrains().add(terrain);
	}
	
	public void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public void cleanup() {
		nodeRenderer.cleanup();
		terrainRenderer.cleanup();
	}

	public void cleanupNodes() {
		nodeRenderer.getEntities().clear();
	}

	public static boolean isCulling() {
		return isCulling;
	}

	public static void setCulling(boolean isCulling) {
		Renderer.isCulling = isCulling;
	}
	
	public static void enableCulling() {
		if (!isCulling) {
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
			isCulling = true;
		}
	}

	public static void disableCulling() {
		if (isCulling) {
			GL11.glDisable(GL11.GL_CULL_FACE);
			isCulling = false;
		}
	}

}
