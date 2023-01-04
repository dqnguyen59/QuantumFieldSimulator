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

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.smartblackbox.qfs.opengl.model.Material;
import org.smartblackbox.qfs.opengl.model.lights.DirectionalLight;
import org.smartblackbox.qfs.opengl.model.lights.Light;
import org.smartblackbox.utils.Utils;

public class Shader {
	
	private final int programID;
	private int vertexShaderID, fragmentShaderID;
	private final Map<String, Integer> uniforms;
	
	public Shader() throws Exception {
		programID = GL20.glCreateProgram();
		
		if (programID == 0)
			throw new Exception("Error: Could not create shader"); 
		
		uniforms = new HashMap<>();
	}
	
	public void createUniform(String uniformName) throws Exception {
		int uniformLocation = GL20.glGetUniformLocation(programID,  uniformName);
		if (uniformLocation < 0)
			throw new Exception("Could not find uniform " + uniformName);
		uniforms.put(uniformName, uniformLocation);		
	}
	
	public void createMaterialUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".ambient");
		createUniform(uniformName + ".diffuse");
		createUniform(uniformName + ".specular");
		createUniform(uniformName + ".hasTexture");
		createUniform(uniformName + ".shininess");
	}
	
	public void createDirectionalLightUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".color");
		createUniform(uniformName + ".direction");
		createUniform(uniformName + ".intensity");
	}
	
	public void createLightUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".color");
		createUniform(uniformName + ".position");
		createUniform(uniformName + ".intensity");
		createUniform(uniformName + ".constant");
		createUniform(uniformName + ".linear");
		createUniform(uniformName + ".exponent");
		createUniform(uniformName + ".coneDirection");
		createUniform(uniformName + ".cutOff");
		createUniform(uniformName + ".isSpotLight");
	}
	
	public void createLightListUniform(String uniformName, int size) throws Exception {
		for (int i = 0; i < size; i++) {
			createLightUniform(uniformName + "[" + i + "]");
		}
	}

	public void setUniform(String uniformName, Matrix4f value) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			GL20.glUniformMatrix4fv(uniforms.get(uniformName), false,
					value.get(stack.mallocFloat(16)));
		}
	}
	
	public void setUniform(String uniformName, Vector4f value) {
		GL20.glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
	}
	
	public void setUniform(String uniformName, Vector3f value) {
		GL20.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
	}
	
	public void setUniform(String uniformName, Vector3d value) {
		GL20.glUniform3f(uniforms.get(uniformName), (float) value.x, (float) value.y, (float) value.z);
	}
	
	public void setUniform(String uniformName, boolean value) {
		float res = value? 1 : 0;
	
		GL20.glUniform1f(uniforms.get(uniformName), res);
	}
	
	public void setUniform(String uniformName, float value) {
		GL20.glUniform1f(uniforms.get(uniformName), value);
	}
	
	public void setUniform(String uniformName, double value) {
		GL20.glUniform1f(uniforms.get(uniformName), (float) value);
	}
	
	public void setUniform(String uniformName, int value) {
		GL20.glUniform1i(uniforms.get(uniformName), value);
	}
	
	public void setUniform(String uniformName, Material material) {
		setUniform(uniformName + ".ambient", material.getAmbientIntensityColor());
		setUniform(uniformName + ".diffuse", material.getDiffuseIntensityColor());
		setUniform(uniformName + ".specular", material.getSpecularIntensityColor());
		setUniform(uniformName + ".hasTexture", material.hasTexture() ? 1 : 0);
		setUniform(uniformName + ".shininess", material.getShininess());
	}
	
	public void setUniform(String uniformName, DirectionalLight light) {
		setUniform(uniformName + ".color", light.getColor());
		setUniform(uniformName + ".direction", light.getDirection());
		setUniform(uniformName + ".intensity", light.getIntensity());
	}
	
	public void setUniform(String uniformName, Light light) {
		setUniform(uniformName + ".color", light.getColor());
		setUniform(uniformName + ".position", light.getPosition());
		setUniform(uniformName + ".intensity", light.getIntensity());
		setUniform(uniformName + ".constant", light.getConstant());
		setUniform(uniformName + ".linear", light.getLinear());
		setUniform(uniformName + ".exponent", light.getExponent());
		setUniform(uniformName + ".coneDirection", light.getDirection());
		setUniform(uniformName + ".cutOff", light.getCutOffRad());
		setUniform(uniformName + ".isSpotLight", light.isSpotLight());
	}
	
	public void setUniform(String uniformName, Light[] lights) {
		int numLights = lights != null ? lights.length : 0;
		
		for (int i = 0; i < numLights; i++) {
			setUniform(uniformName, lights[i], i);
		}
	}
	
	public void setUniform(String uniformName, Light light, int pos) {
		setUniform(uniformName + "[" + pos + "]", light);
	}


	
	public void createVertextShader(String shaderCode) throws Exception {
		GL20.glDeleteShader(vertexShaderID);
		vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
	}

	public void createFragmentShader(String shaderCode) throws Exception {
		GL20.glDeleteShader(fragmentShaderID);
		fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
	}
	
	public int createShader(String shaderCode, int shaderType) throws Exception {
		int shaderID = GL20.glCreateShader(shaderType);
		if (shaderID == 0)
			throw new Exception("Error: unable to create shader. Type: " + shaderType);
		
		GL20.glShaderSource(shaderID, shaderCode);
		GL20.glCompileShader(shaderID);
		
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0)
			throw new Exception("Error: unable to compile shader code. Type: " + shaderType +
					"; Info: " + GL20.glGetShaderInfoLog(shaderID, 1024));
		
		GL20.glAttachShader(programID, shaderID);
		
		return shaderID;
	}
	
	public void link() throws Exception {
		GL20.glLinkProgram(programID);
		if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0)
			throw new Exception("Error: unable to linking shader code; Info: " + 
					GL20.glGetProgramInfoLog(programID, 1024));
		
		if (vertexShaderID != 0)
			GL20.glDetachShader(programID, vertexShaderID);

		if (fragmentShaderID != 0)
			GL20.glDetachShader(programID, fragmentShaderID);
		
		if (!Utils.isMac()) {
			validateProgram();
		}
	}
	
	public void validateProgram() throws Exception {
		GL20.glValidateProgram(programID);
		
		if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0)
			throw new Exception("Error: unable to validate shader code: " +
					GL20.glGetProgramInfoLog(programID, 1024));
	}
	
	public void bind() {
		GL20.glUseProgram(programID);
	}
	
	public void unbind() {
		GL20.glUseProgram(0);
	}

	public void cleanup() {
		unbind();
		
		if (programID != 0)
			GL20.glDeleteProgram(programID);
	}
	
}
