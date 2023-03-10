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
package org.smartblackbox.qfs.opengl.view.renderer;

import org.smartblackbox.qfs.opengl.model.Camera;
import org.smartblackbox.qfs.opengl.model.ObjFileModel;
import org.smartblackbox.qfs.opengl.model.Scene;

public interface IRenderer {

	public void init() throws Exception;
	
	public void render(Camera camera, Scene scene);
	
	public void bind(ObjFileModel model);
	
	public void unbind();
	
	public void cleanup();
	
}
