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

import org.smartblackbox.utils.AbstractSettings;
import org.smartblackbox.utils.QWini;

public class RainModel extends AbstractSettings  {
	
	private boolean isActive = false;
	private double force = -25.0;
	private int iterations = 2;

	public RainModel() {
	}
	
	public RainModel(RainModel model) {
		set(model);
	}
	
	public void set(RainModel model) {
		setActive(model.isActive);
		setForce(model.force);
		setIterations(model.iterations);
	}
	
	@Override
	public AbstractSettings clone() {
		return new RainModel(this);
	}

	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public double getForce() {
		return force;
	}
	
	public void setForce(double force) {
		this.force = force;
	}
	
	public int getIterations() {
		return iterations;
	}
	
	public void setIterations(int rainIterations) {
		this.iterations = rainIterations;
	}

	@Override
	public void loadFromFile(QWini ini, String section, int index) {
		super.loadFromFile(ini, section, index);
		
		isActive = ini.getBool(section, "active", false);
		force = ini.getDouble(section, "force", -25);
		iterations = ini.getInt(section, "iterations", 2);
	}

	@Override
	public void saveToFile(QWini ini, String section, int index) {
		super.saveToFile(ini, section, index);
		ini.put(section, "active", isActive);
		ini.put(section, "force", force);
		ini.put(section, "iterations", iterations);
	}

	
}
