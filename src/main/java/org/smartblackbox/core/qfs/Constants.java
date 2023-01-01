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
package org.smartblackbox.core.qfs;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.nuklear.NkColor;

public class Constants {

	public static final String SEPARATOR = System.getProperty("file.separator");
	
	public static final double NANOSECOND = 1000000000;
	public static final double MILLISECOND = 1000000;
	public static final double UPDATE_RATE = 100; // ms
	public static final long KEYBOARD_INTERVAL = 50; // ms
	public static final int KEYBOARD_INTERVAL_REPEAT = 15; // ms
	
	public static final NkColor DEFAULT_BG_COLOR = NkColor.create()
            .r((byte) 0x50)
            .g((byte) 0x50)
            .b((byte) 0x50)
            .a((byte) 0xFF);

	public static final NkColor TRANSPARENT_BG_COLOR = NkColor.create()
            .r((byte) 0x70)
            .g((byte) 0x70)
            .b((byte) 0xB0)
            .a((byte) 0x70);

	public static final String TITLE = "Quantum Field Simulator";
	public static final String TEXTURE_FILE_PATH = "src/main/resources/textures/terrains/";

	public static final String DEFAULT_FONT_FILE = "fonts/fira-sans/FiraSans-Medium.ttf";
	public static final String DEFAULT_FONT_FILE_BOLD = "fonts/fira-sans/FiraSans-ExtraBold.ttf";
	public static final String DEFAULT_FONT_FILE_ITALIC = "fonts/fira-sans/FiraSans-MediumItalic.ttf";
	public static final int DEFAULT_FONT_SIZE = 18;

	public static final String PROJECT_FILE_EXT = ".qfs";
	public static final String PROJECT_FILE_PATH = "samples";

	public static final int STATUS_BAR_HEIGHT = 40;
	
	public static final int INITIAL_NUM_LIGHTS = 2;

	public static final float SPECULAR_POWER = 32f;
	
	public static final float CAMERA_MOVE_SPEED = 0.05f;
	public static final float MOUSE_SENSITIVITY = 0.2f;
	public static final float CAMERA_STEP = 0.05f;
	
	public static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Vector3f AMBIENT_LIGHT = new Vector3f(0.1f, 0.1f, 0.1f);

	public static final float EP_DEFAULT_ELESTICITY = 1.0f;
	public static final float EP_DEFAULT_DISTANCE = 1.0f;
	public static final float EP_DEFAULT_MOMENTUM = 0.066f;
	public static final float EP_DEFAULT_MOMENTUM_FADE = 0.83f;
	public static final float EP_DEFAULT_DELTA = 0.1f;
	public static final float EP_THRESHOLD = 0.00000f;
	public static final float PLANCK_LENGTH = 0.000000f;

	public static final int GLFW_CONTINUOUS_PRESS = 3;

	public static final float MAX_INTENSITY = 1000;

	public static final int DOUBLE_CLICK_INTERVAL = 200;

	public static final Float DEPTH_FADING_FACTOR = 0.008f;
	public static final Float TERRAIN_DEPTH_FADING_FACTOR = 0.0005f;

}
