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
package org.smartblackbox.qfs.opengl.model;

import org.joml.Vector2d;
import org.joml.Vector3i;
import org.smartblackbox.qfs.opengl.model.entity.QFSNode;
import org.smartblackbox.qfs.opengl.model.lights.Light;
import org.smartblackbox.qfs.settings.QFSProject;

public class QFSModel {
	
	public enum Mode {
		normal,
		rain,
		alhpa,
		intensity,
		light,
	};
	
	public enum EditMode {
		none,
		swapNode,
		swapNodesZ,
		drawLine,
		drawWall,
		eraseNode,
		eraseWall,
	}
	
	public enum NodeSelectionMode {
		none,
		oscillator,
		ignitionNode,
		detectorNode,
	}
	
	private boolean isSimulating;
	
	private int glSwapInterval = 0;

	private boolean isLoadingReady = false;

	private boolean isWallVisible = false;
	private boolean isChanged = false;

	private Mode mode = Mode.normal;
	private EditMode editMode = EditMode.none;

	private double progress;
	private Vector2d mousePos = new Vector2d();
	private QFSNode currentMouseNode;
	private Vector3i currentMouseNodeIndex = new Vector3i();
	
	private NodeSelectionMode nodeSelectionMode = NodeSelectionMode.none;
	public Oscillator selectedOscillator;
	private Light selectedLight;
	
	public String getBtnSimulatingLabel() {
		return isSimulating()? "Stop" : "Run";
	}
	
	public boolean isSimulating() {
		return isSimulating;
	}
	
	public void setSimulation(boolean isSimulating) {
		this.isSimulating = isSimulating;
	}
	
	public void startSimulation() {
		isSimulating = true;
	}
	
	public void stopSimulation() {
		isSimulating = false;
	}
	
	public void toggleSimulation() {
		isSimulating = !isSimulating; 
	}
	
	public int getGlSwapInterval() {
		return glSwapInterval;
	}

	public void setGlSwapInterval(int glSwapInterval) {
		this.glSwapInterval = glSwapInterval;
	}

	public boolean isLoadingReady() {
		return isLoadingReady;
	}

	public void setLoadingReady(boolean isLoadingReady) {
		this.isLoadingReady = isLoadingReady;
	}

	public boolean isWallVisible() {
		return isWallVisible;
	}

	public void setWallVisible(boolean isWallVisible) {
		if (this.isWallVisible != isWallVisible)
			isChanged = true;
		this.isWallVisible = isWallVisible;
	}

	public void clearWalls() {
		QFSProject.getInstance().scene.clearAllWalls();
		isChanged = true;
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public EditMode getEditMode() {
		return editMode;
	}

	public void setEditMode(EditMode editMode) {
		this.editMode = editMode;
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

	public Vector2d getMousePos() {
		return mousePos;
	}

	public void setMousePos(Vector2d mousePos) {
		this.mousePos = mousePos;
	}

	public QFSNode getCurrentMouseNode() {
		return currentMouseNode;
	}

	public void setCurrentMouseNode(QFSNode currentMouseNode) {
		this.currentMouseNode = currentMouseNode;
	}

	public Vector3i getCurrentMouseNodeIndex() {
		return currentMouseNodeIndex;
	}

	public void setCurrentMouseNodeIndex(Vector3i currentMouseNodeIndex) {
		this.currentMouseNodeIndex = currentMouseNodeIndex;
	}

	public NodeSelectionMode getNodeSelectionMode() {
		return nodeSelectionMode;
	}

	public void setNodeSelectionMode(NodeSelectionMode mode) {
		this.nodeSelectionMode = mode;
	}

	public Oscillator getSelectedOscillator() {
		return selectedOscillator;
	}

	public void setSelectedOscillator(Oscillator selectedOscillator) {
		this.selectedOscillator = selectedOscillator;
	}

	public Light getSelectedLight() {
		return selectedLight;
	}

	public void setSelectedLight(Light selectedLight) {
		this.selectedLight = selectedLight;
	}

	public void setSimulating(boolean isSimulating) {
		this.isSimulating = isSimulating;
	}

}
