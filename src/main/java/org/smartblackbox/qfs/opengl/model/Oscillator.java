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
	private Vector3d startangle = new Vector3d(); // degree
	private Vector3d angle = new Vector3d(); // degree
	private Vector3d angleIncrement = new Vector3d(); // degree
	private Vector3d amplitude = new Vector3d();
	
	private Vector3d vOscillator = new Vector3d();
	public Vector3i nodeIndex = new Vector3i(-1, -1, -1);
	
	public Oscillator(String name, Vector3i nodeIndex) {
		this.name = name;
		this.nodeIndex = nodeIndex;
	}
	
	public void setNode(Vector3i index) {
		nodeIndex = index;
	}

	public Oscillator clone() {
		Oscillator oscillator = new Oscillator(name, nodeIndex);
		oscillator.active = new Vector3b(active);
		oscillator.oscillatorType = new VectorOscillatorType(oscillatorType);
		oscillator.startangle = new Vector3d(startangle);
		oscillator.angle = new Vector3d(angle);
		oscillator.angleIncrement = new Vector3d(angleIncrement);
		oscillator.amplitude = new Vector3d(amplitude);
		oscillator.vOscillator = new Vector3d(vOscillator);
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
			if (angle.x > 360) angle.x = angle.x - 360;
			else if (angle.x < -360) angle.x = angle.x + 360;

			if (oscillatorType.x == OscillatorType.sin)
				vOscillator.x = Math.sin(Math.toRadians(startangle.x + angle.x));
			else if (oscillatorType.x == OscillatorType.cos)
				vOscillator.x = Math.cos(Math.toRadians(startangle.x + angle.x));
		}

		if (active.y) {
			angle.y += angleIncrement.y;
			if (angle.y > 360) angle.y = angle.y - 360;
			else if (angle.y < -360) angle.y = angle.y + 360;
			
			if (oscillatorType.y == OscillatorType.sin)
				vOscillator.y = Math.sin(Math.toRadians(startangle.y + angle.y));
			else if (oscillatorType.y == OscillatorType.cos)
				vOscillator.y = Math.cos(Math.toRadians(startangle.y + angle.y));
		}

		if (active.z) {
			angle.z += angleIncrement.z;
			if (angle.z > 360) angle.z = angle.z - 360;
			else if (angle.z < -360) angle.z = angle.z + 360;
			
			if (oscillatorType.z == OscillatorType.sin)
				vOscillator.z = Math.sin(Math.toRadians(startangle.z + angle.z));
			else if (oscillatorType.z == OscillatorType.cos)
				vOscillator.z = Math.cos(Math.toRadians(startangle.z + angle.z));
		}
		
		if (active.x || active.y || active.z) {
			return vOscillator.mul(amplitude);
		}
		else return null;
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
		String s;
		name = ini.get(section + index, "name");
		nodeIndex.x = ((s = ini.get(section + index, "nodeIndex.x")).isEmpty()? 0 : Integer.parseInt(s));
		nodeIndex.y = ((s = ini.get(section + index, "nodeIndex.y")).isEmpty()? 0 : Integer.parseInt(s));
		nodeIndex.z = ((s = ini.get(section + index, "nodeIndex.z")).isEmpty()? 0 : Integer.parseInt(s));
		active.x = ((s = ini.get(section + index, "active.x")).isEmpty()? false : Boolean.parseBoolean(s));
		active.y = ((s = ini.get(section + index, "active.y")).isEmpty()? false : Boolean.parseBoolean(s));
		active.z = ((s = ini.get(section + index, "active.z")).isEmpty()? false : Boolean.parseBoolean(s));
		oscillatorType.x = ((s = ini.get(section + index, "OscillatorType.x")).isEmpty()? OscillatorType.sin : OscillatorType.valueOf(s));
		oscillatorType.y = ((s = ini.get(section + index, "OscillatorType.y")).isEmpty()? OscillatorType.sin : OscillatorType.valueOf(s));
		oscillatorType.z = ((s = ini.get(section + index, "OscillatorType.z")).isEmpty()? OscillatorType.sin : OscillatorType.valueOf(s));
		startangle.x = ((s = ini.get(section + index, "startangle.x")).isEmpty()? 0 : Double.parseDouble(s));
		startangle.y = ((s = ini.get(section + index, "startangle.y")).isEmpty()? 0 : Double.parseDouble(s));
		startangle.z = ((s = ini.get(section + index, "startangle.z")).isEmpty()? 0 : Double.parseDouble(s));
		angleIncrement.x = ((s = ini.get(section + index, "angleIncrement.x")).isEmpty()? 0 : Double.parseDouble(s));
		angleIncrement.y = ((s = ini.get(section + index, "angleIncrement.y")).isEmpty()? 0 : Double.parseDouble(s));
		angleIncrement.z = ((s = ini.get(section + index, "angleIncrement.z")).isEmpty()? 0 : Double.parseDouble(s));
		amplitude.x = ((s = ini.get(section + index, "amplitude.x")).isEmpty()? 0 : Double.parseDouble(s));
		amplitude.y = ((s = ini.get(section + index, "amplitude.y")).isEmpty()? 0 : Double.parseDouble(s));
		amplitude.z = ((s = ini.get(section + index, "amplitude.z")).isEmpty()? 0 : Double.parseDouble(s));
	}

	@Override
	public void saveToFile(Wini ini, String section, int index) {
		ini.put(section + index, "name", name);
		ini.put(section + index, "nodeIndex.x", nodeIndex.x);
		ini.put(section + index, "nodeIndex.y", nodeIndex.y);
		ini.put(section + index, "nodeIndex.z", nodeIndex.z);
		ini.put(section + index, "active.x", active.x);
		ini.put(section + index, "active.y", active.y);
		ini.put(section + index, "active.z", active.z);
		ini.put(section + index, "OscillatorType.x", oscillatorType.x == null? "" : oscillatorType.x.toString());
		ini.put(section + index, "OscillatorType.y", oscillatorType.y == null? "" : oscillatorType.y.toString());
		ini.put(section + index, "OscillatorType.z", oscillatorType.z == null? "" : oscillatorType.z.toString());
		ini.put(section + index, "startangle.x", startangle.x);
		ini.put(section + index, "startangle.y", startangle.y);
		ini.put(section + index, "startangle.z", startangle.z);
		ini.put(section + index, "angleIncrement.x", angleIncrement.x);
		ini.put(section + index, "angleIncrement.y", angleIncrement.y);
		ini.put(section + index, "angleIncrement.z", angleIncrement.z);
		ini.put(section + index, "amplitude.x", amplitude.x);
		ini.put(section + index, "amplitude.y", amplitude.y);
		ini.put(section + index, "amplitude.z", amplitude.z);
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
		return startangle;
	}

	public void setStartangle(Vector3d startangle) {
		this.startangle = startangle;
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

	
	
}
