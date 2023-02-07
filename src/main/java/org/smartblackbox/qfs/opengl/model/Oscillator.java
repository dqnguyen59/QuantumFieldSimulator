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

import org.ini4j.Wini;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.smartblackbox.qfs.opengl.utils.OscillatorType;
import org.smartblackbox.qfs.opengl.utils.Vector3b;
import org.smartblackbox.qfs.opengl.utils.VectorOscillatorType;
import org.smartblackbox.utils.AbstractSettings;
import org.smartblackbox.utils.ISettings;

public class Oscillator extends AbstractSettings implements ISettings {
	
	private String name = "";
	private Vector3b active = new Vector3b();
	private VectorOscillatorType oscillatorType = new VectorOscillatorType(); // Sin or Cos
	private Vector3d startAngle = new Vector3d(); // degree
	private Vector3d angle = new Vector3d(); // degree
	private Vector3d angleIncrement = new Vector3d(); // degree
	private Vector3d amplitude = new Vector3d();
	private Vector3d nodeInitPosition = new Vector3d();
	
	private Vector3d vOscillator = new Vector3d();
	public Vector3i nodeIndex = new Vector3i(-1, -1, -1);
	
	public Oscillator(String name, Vector3i nodeIndex) {
		this.name = name;
		this.nodeIndex = nodeIndex;
	}
	
	public void setNode(Vector3i index, Vector3d nodeInitPosition) {
		nodeIndex = index;
		this.nodeInitPosition.set(nodeInitPosition);
	}

	public Oscillator clone() {
		Oscillator oscillator = new Oscillator(name, nodeIndex);
		oscillator.active = new Vector3b(active);
		oscillator.oscillatorType = new VectorOscillatorType(oscillatorType);
		oscillator.startAngle = new Vector3d(startAngle);
		oscillator.angle = new Vector3d(angle);
		oscillator.angleIncrement = new Vector3d(angleIncrement);
		oscillator.amplitude = new Vector3d(amplitude);
		oscillator.vOscillator = new Vector3d(vOscillator);
		oscillator.nodeInitPosition = new Vector3d(nodeInitPosition);
		return oscillator;
	}
	
	/**
	 *  Perform the oscillation to the node.
	 * @return 
	 */
	public Vector3d oscillate() {
		vOscillator.set(0);
		
		if (active.x) {
			angle.x += angleIncrement.x;
			if (angle.x >= 360) angle.x = 0;
			else if (angle.x <= -360) angle.x = 0;

			if (oscillatorType.x == OscillatorType.sin)
				vOscillator.x = Math.sin(Math.toRadians(startAngle.x + angle.x));
			else if (oscillatorType.x == OscillatorType.cos)
				vOscillator.x = Math.cos(Math.toRadians(startAngle.x + angle.x));
			else if (oscillatorType.x == OscillatorType.square) {
				if (startAngle.x + angle.x >= 0 && startAngle.x + angle.x < 180)
					vOscillator.x = 1;
				else
					vOscillator.x = -1;
			}
		}

		if (active.y) {
			angle.y += angleIncrement.y;
			if (angle.y >= 360) angle.y = 0;
			else if (angle.y <= -360) angle.y = 0;
			
			if (oscillatorType.y == OscillatorType.sin)
				vOscillator.y = Math.sin(Math.toRadians(startAngle.y + angle.y));
			else if (oscillatorType.y == OscillatorType.cos)
				vOscillator.y = Math.cos(Math.toRadians(startAngle.y + angle.y));
			else if (oscillatorType.y == OscillatorType.square) {
				if (startAngle.y + angle.y >= 0 && startAngle.y + angle.y < 180)
					vOscillator.y = 1;
				else
					vOscillator.y = -1;
			}
		}

		if (active.z) {
			angle.z += angleIncrement.z;
			if (angle.z >= 360) angle.z = 0;
			else if (angle.z <= -360) angle.z = 0;
			
			if (oscillatorType.z == OscillatorType.sin)
				vOscillator.z = Math.sin(Math.toRadians(startAngle.z + angle.z));
			else if (oscillatorType.z == OscillatorType.cos)
				vOscillator.z = Math.cos(Math.toRadians(startAngle.z + angle.z));
			else if (oscillatorType.z == OscillatorType.square) {
				if (startAngle.z + angle.z >= 0 && startAngle.z + angle.z < 180)
					vOscillator.z = 1;
				else
					vOscillator.z = -1;
			}
		}
		
		if (active.x || active.y || active.z) {
			return vOscillator.mul(amplitude);
		}
		else return null;
	}

	public void reset() {
		vOscillator.set(0);
		angle.set(0);
	}
	
	public boolean isActive() {
		return active.x || active.y || active.z;
	}

	@Override
	public void loadFromFile(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveToFile(String filename) {
	}

	@Override
	public void loadFromFile(Wini ini, String section, int index) {
		super.loadFromFile(ini, section, index);
		String s;
		name = ini.get(section + index, "name");
		nodeIndex.x = getInt(section + index, "nodeIndex.x", 0);
		nodeIndex.y = getInt(section + index, "nodeIndex.y", 0);
		nodeIndex.z = getInt(section + index, "nodeIndex.z", 0);
		nodeInitPosition.x = getDouble(section + index, "nodeInitPosition.x", 0);
		nodeInitPosition.y = getDouble(section + index, "nodeInitPosition.y", 0);
		nodeInitPosition.z = getDouble(section + index, "nodeInitPosition.z", 0);
		active.x = getBool(section + index, "active.x", false);
		active.y = getBool(section + index, "active.y", false);
		active.z = getBool(section + index, "active.z", false);
		oscillatorType.x = (s = getString(section + index, "OscillatorType.x", "")).isEmpty()? OscillatorType.sin : OscillatorType.valueOf(s);
		oscillatorType.y = (s = getString(section + index, "OscillatorType.y", "")).isEmpty()? OscillatorType.sin : OscillatorType.valueOf(s);
		oscillatorType.z = (s = getString(section + index, "OscillatorType.z", "")).isEmpty()? OscillatorType.sin : OscillatorType.valueOf(s);
		startAngle.x = getDouble(section + index, "startAngle.x", 0);
		startAngle.y = getDouble(section + index, "startAngle.y", 0);
		startAngle.z = getDouble(section + index, "startAngle.z", 0);
		angleIncrement.x = getDouble(section + index, "angleIncrement.x", 0);
		angleIncrement.y = getDouble(section + index, "angleIncrement.y", 0);
		angleIncrement.z = getDouble(section + index, "angleIncrement.z", 0);
		amplitude.x = getDouble(section + index, "amplitude.x", 0);
		amplitude.y = getDouble(section + index, "amplitude.y", 0);
		amplitude.z = getDouble(section + index, "amplitude.z", 0);
	}

	@Override
	public void saveToFile(Wini ini, String section, int index) {
		super.saveToFile(ini, section, index);
		put(section + index, "name", name);
		put(section + index, "nodeIndex.x", nodeIndex.x);
		put(section + index, "nodeIndex.y", nodeIndex.y);
		put(section + index, "nodeIndex.z", nodeIndex.z);
		put(section + index, "nodeInitPosition.x", nodeInitPosition.x);
		put(section + index, "nodeInitPosition.y", nodeInitPosition.y);
		put(section + index, "nodeInitPosition.z", nodeInitPosition.z);
		put(section + index, "active.x", active.x);
		put(section + index, "active.y", active.y);
		put(section + index, "active.z", active.z);
		put(section + index, "OscillatorType.x", oscillatorType.x == null? "" : oscillatorType.x.toString());
		put(section + index, "OscillatorType.y", oscillatorType.y == null? "" : oscillatorType.y.toString());
		put(section + index, "OscillatorType.z", oscillatorType.z == null? "" : oscillatorType.z.toString());
		put(section + index, "startAngle.x", startAngle.x);
		put(section + index, "startAngle.y", startAngle.y);
		put(section + index, "startAngle.z", startAngle.z);
		put(section + index, "angleIncrement.x", angleIncrement.x);
		put(section + index, "angleIncrement.y", angleIncrement.y);
		put(section + index, "angleIncrement.z", angleIncrement.z);
		put(section + index, "amplitude.x", amplitude.x);
		put(section + index, "amplitude.y", amplitude.y);
		put(section + index, "amplitude.z", amplitude.z);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector3b getActive() {
		return active;
	}

	public void setActive(Vector3b active) {
		this.active = active;
	}

	public VectorOscillatorType getOscillatorType() {
		return oscillatorType;
	}

	public void setOscillatorType(VectorOscillatorType oscillatorType) {
		this.oscillatorType = oscillatorType;
	}

	public Vector3d getStartangle() {
		return startAngle;
	}

	public void setStartangle(Vector3d startAngle) {
		this.startAngle = startAngle;
	}

	public Vector3d getAngle() {
		return angle;
	}

	public void setAngle(Vector3d angle) {
		this.angle = angle;
	}

	public Vector3d getAngleIncrement() {
		return angleIncrement;
	}

	public void setAngleIncrement(Vector3d angleIncrement) {
		this.angleIncrement = angleIncrement;
	}

	public Vector3d getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(Vector3d amplitude) {
		this.amplitude = amplitude;
	}

	public Vector3d getvOscillator() {
		return vOscillator;
	}

	public void setvOscillator(Vector3d vOscillator) {
		this.vOscillator = vOscillator;
	}

	public Vector3i getNodeIndex() {
		return nodeIndex;
	}

	public void setNodeIndex(Vector3i nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	public Vector3d getNodeInitPosition() {
		return nodeInitPosition;
	}

	public void setNodeInitPosition(Vector3d nodeInitPosition) {
		this.nodeInitPosition = nodeInitPosition;
	}
	
}
