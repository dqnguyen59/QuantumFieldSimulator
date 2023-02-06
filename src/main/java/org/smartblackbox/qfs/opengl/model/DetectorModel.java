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

import org.joml.Vector3d;
import org.joml.Vector3i;
import org.smartblackbox.qfs.opengl.model.entity.QFSNode;
import org.smartblackbox.utils.AbstractSettings;
import org.smartblackbox.utils.PerformanceMonitor;
import org.smartblackbox.utils.QWini;

public class DetectorModel extends AbstractSettings {

	private boolean isIgnited = false;
	private boolean isStarted = false;
	private Vector3i ignitorNode = new Vector3i(-1, -1, -1);
	private Vector3i detectorNode = new Vector3i(-1, -1, -1);
	private double detectionVelocity = 0.00005;
	private int firstDetectionFrames;
	private int initFrame;
	private int firstDetectionFrame;
	private int secondDetectionFrame;
	private int secondDetectionFrames;
	private double nodeVelocity;

	public DetectorModel() {
		reset();
	}

	public DetectorModel(DetectorModel model) {
		set(model);
		reset();
	}

	public void set(DetectorModel model) {
		setIgnitorNode(model.ignitorNode);
		setDetectorNode(model.detectorNode);
	}

	@Override
	public AbstractSettings clone() {
		return new DetectorModel(this);
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public boolean isIgnited() {
		return isIgnited;
	}

	public void setIgnited(boolean isIgnited) {
		this.isIgnited = isIgnited;
	}

	public void reset() {
		isIgnited = false;
		isStarted = false;
		initFrame = -1;
		firstDetectionFrame = -1;
		secondDetectionFrame = -1;
		firstDetectionFrames = -1;
		secondDetectionFrames = -1;
	}

	public void ignite() {
		isIgnited = true;
	}

	public void ignite(QFSNode node, double force) {
		if (node != null) {
			reset();
			isStarted = true;
			node.incPosition(new Vector3d(0, 0, force));
			initFrame = PerformanceMonitor.totalFrames;
		}
	}

	public int detect(QFSNode node) {
		nodeVelocity = node.getVelocity().length();
		if (isStarted) {
			if (node.getVelocity().length() > 0 && firstDetectionFrames == -1) {
				firstDetectionFrame = PerformanceMonitor.totalFrames;
				firstDetectionFrames = PerformanceMonitor.totalFrames - initFrame;
				return 1;
			}

			if (node.getVelocity().length() > detectionVelocity && isStarted) {
				secondDetectionFrame = PerformanceMonitor.totalFrames;
				secondDetectionFrames = secondDetectionFrame - initFrame;
				isStarted = false;
				return 2;
			}
		}
		return 0;
	}

	public double getNodeVelocity() {
		return nodeVelocity;
	}

	public Vector3i getIgnitorNode() {
		return ignitorNode;
	}

	public void setIgnitorNode(Vector3i ignitorNode) {
		this.ignitorNode = ignitorNode;
	}

	public Vector3i getDetectorNode() {
		return detectorNode;
	}

	public void setDetectorNode(Vector3i detectorNode) {
		this.detectorNode = detectorNode;
	}

	public int getFirstDetection() {
		return firstDetectionFrames;
	}

	public void setFirstDetection(int firstDetection) {
		this.firstDetectionFrames = firstDetection;
	}

	public int getCurrentFrame() {
		return initFrame;
	}

	public void setCurrentFrame(int currentFrame) {
		this.initFrame = currentFrame;
	}

	public double getDetectionVelocity() {
		return detectionVelocity;
	}

	public void setDetectionVelocity(double detectionVelocity) {
		if (detectionVelocity > 0)
			this.detectionVelocity = detectionVelocity;
	}

	public int getFirstDetectionFrames() {
		return firstDetectionFrames;
	}

	public void setFirstDetectionFrames(int firstDetectionFrames) {
		this.firstDetectionFrames = firstDetectionFrames;
	}

	public int getInitFrame() {
		return initFrame;
	}

	public void setInitFrame(int initFrame) {
		this.initFrame = initFrame;
	}

	public int getFirstDetectionFrame() {
		return firstDetectionFrame;
	}

	public void setFirstDetectionFrame(int firstDetectionFrame) {
		this.firstDetectionFrame = firstDetectionFrame;
	}

	public int getSecondDetectionFrame() {
		return secondDetectionFrame;
	}

	public void setSecondDetectionFrame(int secondDetectionFrame) {
		this.secondDetectionFrame = secondDetectionFrame;
	}

	public int getSecondDetectionFrames() {
		return secondDetectionFrames;
	}

	public void setSecondDetectionFrames(int secondDetectionFrames) {
		this.secondDetectionFrames = secondDetectionFrames;
	}

	@Override
	public void loadFromFile(QWini ini, String section, int index) {
		super.loadFromFile(ini, section, index);

		ignitorNode.x = ini.getInt("Detector", "ignitorNode.x", 1);
		ignitorNode.y = ini.getInt("Detector", "ignitorNode.y", 1);
		ignitorNode.z = ini.getInt("Detector", "ignitorNode.z", 1);
		detectorNode.x = ini.getInt("Detector", "detectorNode.x", 1);
		detectorNode.y = ini.getInt("Detector", "detectorNode.y", 1);
		detectorNode.z = ini.getInt("Detector", "detectorNode.z", 1);
		detectionVelocity = ini.getDouble("Detector", "detectVelocity", 0.00005);
	}

	@Override
	public void saveToFile(QWini ini, String section, int index) {
		super.saveToFile(ini, section, index);
		ini.put("Detector", "ignitorNode.x", ignitorNode.x);
		ini.put("Detector", "ignitorNode.y", ignitorNode.y);
		ini.put("Detector", "ignitorNode.z", ignitorNode.z);
		ini.put("Detector", "detectorNode.x", detectorNode.x);
		ini.put("Detector", "detectorNode.y", detectorNode.y);
		ini.put("Detector", "detectorNode.z", detectorNode.z);
		ini.put("Detector", "detectionVelocity", detectionVelocity);
	}

}
