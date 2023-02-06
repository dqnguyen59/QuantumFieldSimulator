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
package org.smartblackbox.qfs.opengl.view;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.smartblackbox.qfs.settings.AppSettings;
import org.smartblackbox.qfs.settings.QFSProject;

public class GLWindow {

	private AppSettings qfsSettings = AppSettings.getInstance();
	
	private QFSProject qfsProject = QFSProject.getInstance();

	private final String title;
	private int left, top, width, height;
	private long windowHandle;
	private boolean resize;

	public GLWindow(String title) {
		this.title = title;
		
		this.left = qfsSettings.getWindowLeft();
		this.top = qfsSettings.getWindowTop();
		this.width = qfsSettings.getWindowWidth();
		this.height = qfsSettings.getWindowHeight();
	}

	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

		// Anti-Aliasing
		GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 16);
		
		boolean maximized = qfsSettings.getMaximized() == 1;

		if (maximized) {
			GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GL11.GL_TRUE);
		}

		windowHandle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);

		if (windowHandle == MemoryUtil.NULL) {
			throw new RuntimeException("Error: Failed to create GLFW window!");
		}

		GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
			this.width = width;
			this.height = height;
			this.setResize(true);
		});

		GLFW.glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
				GLFW.glfwSetWindowShouldClose(window, true);
		});

		if (maximized) {
			GLFW.glfwMaximizeWindow(windowHandle);
		}
		else {
			GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			
			if (left != -9999 && top != -9999)
				GLFW.glfwSetWindowPos(windowHandle, left, top);
			else
				GLFW.glfwSetWindowPos(windowHandle, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
		}

		GLFW.glfwMakeContextCurrent(windowHandle);

		GLFW.glfwSwapInterval(qfsProject.getGlSwapInterval());
		
		GLFW.glfwShowWindow(windowHandle);
		
		GL.createCapabilities();
        GL11.glClearColor(0.0f,  0.0f, 0.0f, 0.0f);
		
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	public void update() {
		GLFW.glfwSwapBuffers(windowHandle);
	}
	
	public void cleanUp() {
		GLFW.glfwDestroyWindow(windowHandle);
		windowHandle = 0;
	}
	
	public void setClearColor(float r, float g, float b, float a) {
		GL11.glClearColor(r, g, b, a);
	}
	
	public boolean isKeyPressed(int keyCode) {
		return GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS;
	}
	
	public boolean windowShouldClose() {
		return GLFW.glfwWindowShouldClose(windowHandle);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(windowHandle, title);
	}
	
	public boolean isResize() {
		return resize;
	}

	public void setResize(boolean resize) {
		this.resize = resize;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public long getWindowHandle() {
		return windowHandle;
	}
	
}
