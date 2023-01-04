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
package org.smartblackbox.utils;

public class PerformanceMonitor {
	
	private static final double ONE_MILLION_NANO_SECONDS = 1000000.0;

	public enum Measurement {
		physics,
		updateMatrix,
		sendToGPU,
		totalCPURenderTime,
		gui2D,
		totalRenderTime,
		bdfgbdfbD,
	}
	
	private static long[] iMeasurements = new long[Measurement.values().length];
	private static long[] measurements = new long[Measurement.values().length];

	public static int frames = 0;

	public static int totalFrames = 0;

	public static double fps = 0.0;

	public static void start(Measurement measurement) {
		iMeasurements[measurement.ordinal()] = System.nanoTime();
	}
	
	public static void stop(Measurement measurement) {
		iMeasurements[measurement.ordinal()] = System.nanoTime() - iMeasurements[measurement.ordinal()];
		measurements[measurement.ordinal()] = iMeasurements[measurement.ordinal()];
	}
	
	public static double getMeasurement(Measurement measurement) {
		return measurements[measurement.ordinal()] / ONE_MILLION_NANO_SECONDS;
	}

	public static void reset(Measurement measurement) {
		measurements[measurement.ordinal()] = 0;
	}
}
