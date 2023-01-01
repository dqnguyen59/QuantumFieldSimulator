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

import org.ini4j.Wini;
import org.smartblackbox.core.utils.AbstractSettings;
import org.smartblackbox.core.utils.ISettings;

public class SlitWallSettings extends AbstractSettings implements ISettings {

	public enum SlitDirection {
		x,
		y,
		z,
		;
		
		private static String[] strEnums;

		public static String[] getValues() {
			if (strEnums == null) {
				SlitDirection[] v = values();
				strEnums = new String[v.length];
				for (int i = 0; i < v.length; i++) {
					strEnums[i] = v[i].name();
				}
			}
			return strEnums;
		}
	}
	
	private boolean active = false;
	public SlitDirection direction = SlitDirection.x;
	private int numSlits = 2;
	private int slitWidth = 3;
	private int slitHeight = 20;
	private int slitDistance = 17;
	private float position = 0.25f;
	private boolean isChanged;
	private boolean isUpdated;
	
	public SlitWallSettings() {
		super();
	}

	public SlitWallSettings(SlitWallSettings slitWallSettings) {
		super();
		set(slitWallSettings);
	}

	public void set(SlitWallSettings wall) {
		active = wall.active;
		direction = wall.direction;
		numSlits = wall.numSlits;
		slitWidth = wall.slitWidth;
		slitHeight = wall.slitHeight;
		slitDistance = wall.slitDistance;
		position = wall.position;
		isChanged = wall.isChanged;
		isUpdated = wall.isUpdated;
	}
	
	@Override
	public AbstractSettings clone() {
		return new SlitWallSettings(this);
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean value) {
		isChanged = value;
	}
	
	public boolean isUpdated() {
		return isUpdated;
	}

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;
			isChanged = true;
		}
	}

	public SlitDirection getDirection() {
		return direction;
	}
	
	public void setDirection(SlitDirection direction) {
		if (this.direction != direction) {
			this.direction = direction;
			isChanged = true;
		}
	}
	
	public int getNumSlits() {
		return numSlits;
	}
	
	public void setNumSlits(int numSlits) {
		if (this.numSlits != numSlits) {
			this.numSlits = numSlits;
			isChanged = true;
		}
	}
	
	public int getSlitWidth() {
		return slitWidth;
	}
	
	public void setSlitWidth(int slitWidth) {
		if (this.slitWidth != slitWidth) {
			this.slitWidth = slitWidth;
			isChanged = true;
		}
	}
	
	public int getSlitHeight() {
		return slitHeight;
	}
	
	public void setSlitHeight(int slitHeight) {
		if (this.slitHeight != slitHeight) {
			this.slitHeight = slitHeight;
			isChanged = true;
		}
	}
	
	public int getSlitDistance() {
		return slitDistance;
	}
	
	public void setSlitDistance(int slitDistance) {
		if (this.slitDistance != slitDistance) {
			this.slitDistance = slitDistance;
			isChanged = true;
		}
	}

	public float getPosition() {
		return position;
	}

	public void setPosition(float position) {
		if (this.position != position) {
			this.position = position;
			isChanged = true;
		}
	}

	public void reset() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				isChanged = true;
				isUpdated = true;
			}
		}).start();
	}

	@Override
	public void loadFromFile(Wini ini, String section, int index) {
		String s;
		direction = ((s = ini.get(section, "direction")) == null? SlitDirection.x : SlitDirection.valueOf(s));
		active = ((s = ini.get(section, "active")) == null? false : Boolean.parseBoolean(s));
		numSlits = ((s = ini.get(section, "numSlits")) == null? 2 : Integer.parseInt(s));
		slitWidth = ((s = ini.get(section, "slitWidth")) == null? 3 : Integer.parseInt(s));
		slitHeight = ((s = ini.get(section, "slitHeight")) == null? 10 : Integer.parseInt(s));
		slitDistance = ((s = ini.get(section, "slitDistance")) == null? 51 : Integer.parseInt(s));
		position = ((s = ini.get(section, "position")) == null? 0.25f : Float.parseFloat(s));
		reset();
	}

	@Override
	public void saveToFile(Wini ini, String section, int index) {
		ini.put(section, "active", active);
		ini.put(section, "direction", direction == null? "" : direction.toString());
		ini.put(section, "numSlits", numSlits);
		ini.put(section, "slitWidth", slitWidth);
		ini.put(section, "slitHeight", slitHeight);
		ini.put(section, "slitDistance", slitDistance);
		ini.put(section, "position", position);
	}

}
