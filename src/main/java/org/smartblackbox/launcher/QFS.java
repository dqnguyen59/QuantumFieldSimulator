package org.smartblackbox.launcher;

import org.smartblackbox.core.utils.Utils;

public class QFS {
	public static void main(String[] args) {
		System.out.println("Quantum Field Simulator Launcher");

		try {
			Utils.runCommand("java -jar -XstartOnFirstThread QuantumFieldSimulator.jar");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
