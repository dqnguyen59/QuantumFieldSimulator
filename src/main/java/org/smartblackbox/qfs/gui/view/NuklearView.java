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
package org.smartblackbox.qfs.gui.view;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.nuklear.NkAllocator;
import org.lwjgl.nuklear.NkBuffer;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkConvertConfig;
import org.lwjgl.nuklear.NkDrawCommand;
import org.lwjgl.nuklear.NkDrawNullTexture;
import org.lwjgl.nuklear.NkDrawVertexLayoutElement;
import org.lwjgl.nuklear.NkMouse;
import org.lwjgl.nuklear.NkUserFont;
import org.lwjgl.nuklear.NkUserFontGlyph;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL14C;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.KHRDebug;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.gui.model.Theme;
import org.smartblackbox.qfs.settings.AppSettings;
import org.smartblackbox.utils.Utils;

/**
 * This class is a modified version to fit the needs of this project.
 * The original versioncan be found at https://www.thecodingfox.com/nuklear-usage-guide-lwjgl.
 * 
 * @author dqnguyen
 *
 */
public class NuklearView {

	private static final int BUFFER_INITIAL_SIZE = 4 * 1024;
	private static final int MAX_VERTEX_BUFFER = 512 * 1024;
	private static final int MAX_ELEMENT_BUFFER = 128 * 1024;
	private static final NkAllocator ALLOCATOR;
	private static final NkDrawVertexLayoutElement.Buffer VERTEX_LAYOUT;

	static {
		ALLOCATOR = NkAllocator.create()
				.alloc((handle, old, size) -> MemoryUtil.nmemAllocChecked(size))
				.mfree((handle, ptr) -> MemoryUtil.nmemFree(ptr));

		VERTEX_LAYOUT = NkDrawVertexLayoutElement.create(4)
				.position(0).attribute(Nuklear.NK_VERTEX_POSITION).format(Nuklear.NK_FORMAT_FLOAT).offset(0)
				.position(1).attribute(Nuklear.NK_VERTEX_TEXCOORD).format(Nuklear.NK_FORMAT_FLOAT).offset(8)
				.position(2).attribute(Nuklear.NK_VERTEX_COLOR).format(Nuklear.NK_FORMAT_R8G8B8A8).offset(16)
				.position(3).attribute(Nuklear.NK_VERTEX_ATTRIBUTE_COUNT).format(Nuklear.NK_FORMAT_COUNT).offset(0)
				.flip();
	}

	private AppSettings appSettings = AppSettings.getInstance();
	
	private NuklearModel nuklearModel;
	private long windowHandle;
	private NkContext context = NkContext.create();
	private NkBuffer cmds = NkBuffer.create();
	private NkDrawNullTexture null_texture = NkDrawNullTexture.create();
	private final ByteBuffer ttf;
	private final ByteBuffer ttfItalic;
	private final ByteBuffer ttfBold;

	private int vbo, vao, ebo;
	private int prog;
	private int vert_shdr;
	private int frag_shdr;
	private int uniform_tex;
	private int uniform_proj;
	private GLCapabilities caps;

	public NuklearView(NuklearModel nuklearModel) {
		this.nuklearModel = nuklearModel;
		try {
			ttf = Utils.resourceToByteBuffer(appSettings.getFontFile(), 512 * 1024);
			ttfItalic = Utils.resourceToByteBuffer(appSettings.getFontFileItalic(), 512 * 1024);
			ttfBold = Utils.resourceToByteBuffer(appSettings.getFontFileBold(), 512 * 1024);
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public NkContext getContext() {
		return context;
	}

	public void setContext(NkContext context) {
		this.context = context;
	}

	public long createWindow() {
		GLFWErrorCallback.createPrint().set();
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize glfw");
		}

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		if (Platform.get() == Platform.MACOSX) {
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
		}
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);

		int WINDOW_WIDTH  = 860;
		int WINDOW_HEIGHT = 640;

		long windowHandle = GLFW.glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "GLFW Nuklear Demo", MemoryUtil.NULL, MemoryUtil.NULL);
		if (windowHandle == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		GLFW.glfwMakeContextCurrent(windowHandle);
		caps = GL.createCapabilities();

		if (caps.OpenGL43) {
			GL43.glDebugMessageControl(GL43.GL_DEBUG_SOURCE_API, GL43.GL_DEBUG_TYPE_OTHER, GL43.GL_DEBUG_SEVERITY_NOTIFICATION, (IntBuffer)null, false);
		} else if (caps.GL_KHR_debug) {
			KHRDebug.glDebugMessageControl(
					KHRDebug.GL_DEBUG_SOURCE_API,
					KHRDebug.GL_DEBUG_TYPE_OTHER,
					KHRDebug.GL_DEBUG_SEVERITY_NOTIFICATION,
					(IntBuffer)null,
					false
					);
		} else if (caps.GL_ARB_debug_output) {
			ARBDebugOutput.glDebugMessageControlARB(ARBDebugOutput.GL_DEBUG_SOURCE_API_ARB, ARBDebugOutput.GL_DEBUG_TYPE_OTHER_ARB, ARBDebugOutput.GL_DEBUG_SEVERITY_LOW_ARB, (IntBuffer)null, false);
		}

		return windowHandle;
	}

	public void init(long windowHandle) {

		this.windowHandle = windowHandle;

		Nuklear.nk_init(context, ALLOCATOR, null);
		context.clip()
		.copy((handle, text, len) -> {
			if (len == 0) {
				return;
			}

			try (MemoryStack stack = MemoryStack.stackPush()) {
				ByteBuffer str = stack.malloc(len + 1);
				MemoryUtil.memCopy(text, MemoryUtil.memAddress(str), len);
				str.put(len, (byte)0);

				GLFW.glfwSetClipboardString(windowHandle, str);
			}
		})
		.paste((handle, edit) -> {
			long text = GLFW.nglfwGetClipboardString(windowHandle);
			if (text != MemoryUtil.NULL) {
				Nuklear.nnk_textedit_paste(edit, text, Nuklear.nnk_strlen(text));
			}
		});

		setupContext();

		initFont(nuklearModel.getDefaultFont(), ttf, appSettings.getFontSize());
		initFont(nuklearModel.getDefaultFontBold(), ttfBold, appSettings.getFontSize());
		initFont(nuklearModel.getDefaultFontItalic(), ttfItalic, appSettings.getFontSize());

		Nuklear.nk_style_set_font(context, nuklearModel.getDefaultFont());
	}

	private void initFont(NkUserFont font, ByteBuffer ttf, int fontSize) {
		int BITMAP_W = 1024;
		int BITMAP_H = 1024;

		int FONT_HEIGHT = fontSize;
		int fontTexID   = GL11C.glGenTextures();

		STBTTFontinfo          fontInfo = STBTTFontinfo.create();
		STBTTPackedchar.Buffer cdata    = STBTTPackedchar.create(95);

		float scale;
		float descent;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			STBTruetype.stbtt_InitFont(fontInfo, ttf);
			scale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT);

			IntBuffer d = stack.mallocInt(1);
			STBTruetype.stbtt_GetFontVMetrics(fontInfo, null, d, null);
			descent = d.get(0) * scale;

			ByteBuffer bitmap = MemoryUtil.memAlloc(BITMAP_W * BITMAP_H);

			STBTTPackContext pc = STBTTPackContext.malloc(stack);
			STBTruetype.stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, MemoryUtil.NULL);
			STBTruetype.stbtt_PackSetOversampling(pc, 4, 4);
			STBTruetype.stbtt_PackFontRange(pc, ttf, 0, FONT_HEIGHT, 32, cdata);
			STBTruetype.stbtt_PackEnd(pc);

			// Convert R8 to RGBA8
			ByteBuffer texture = MemoryUtil.memAlloc(BITMAP_W * BITMAP_H * 4);
			for (int i = 0; i < bitmap.capacity(); i++) {
				texture.putInt((bitmap.get(i) << 24) | 0x00FFFFFF);
			}
			texture.flip();

			GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, fontTexID);
			GL11C.glTexImage2D(GL11C.GL_TEXTURE_2D, 0, GL11C.GL_RGBA8, BITMAP_W, BITMAP_H, 0, GL11C.GL_RGBA, GL12C.GL_UNSIGNED_INT_8_8_8_8_REV, texture);
			GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_LINEAR);
			GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_LINEAR);

			MemoryUtil.memFree(texture);
			MemoryUtil.memFree(bitmap);
		}

		font
		.width((handle, h, text, len) -> {
			float text_width = 0;
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer unicode = stack.mallocInt(1);

				int glyph_len = Nuklear.nnk_utf_decode(text, MemoryUtil.memAddress(unicode), len);
				int text_len  = glyph_len;

				if (glyph_len == 0) {
					return 0;
				}

				IntBuffer advance = stack.mallocInt(1);
				while (text_len <= len && glyph_len != 0) {
					if (unicode.get(0) == Nuklear.NK_UTF_INVALID) {
						break;
					}

					/* query currently drawn glyph information */
					STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, unicode.get(0), advance, null);
					text_width += advance.get(0) * scale;

					/* offset next glyph */
					glyph_len = Nuklear.nnk_utf_decode(text + text_len, MemoryUtil.memAddress(unicode), len - text_len);
					text_len += glyph_len;
				}
			}
			return text_width;
		})
		.height(FONT_HEIGHT)
		.query((handle, font_height, glyph, codepoint, next_codepoint) -> {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				FloatBuffer x = stack.floats(0.0f);
				FloatBuffer y = stack.floats(0.0f);

				STBTTAlignedQuad q       = STBTTAlignedQuad.malloc(stack);
				IntBuffer        advance = stack.mallocInt(1);

				STBTruetype.stbtt_GetPackedQuad(cdata, BITMAP_W, BITMAP_H, codepoint - 32, x, y, q, false);
				STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, codepoint, advance, null);

				NkUserFontGlyph ufg = NkUserFontGlyph.create(glyph);

				ufg.width(q.x1() - q.x0());
				ufg.height(q.y1() - q.y0());
				ufg.offset().set(q.x0(), q.y0() + (FONT_HEIGHT + descent));
				ufg.xadvance(advance.get(0) * scale);
				ufg.uv(0).set(q.s0(), q.t0());
				ufg.uv(1).set(q.s1(), q.t1());
			}
		})
		.texture(it -> it.id(fontTexID));
	}
	
	public void initTheme() {
		Theme.init(context);
	}

	public void render() {
		if (windowHandle == 0) return;

		newFrame();
		initTheme();

		nuklearModel.render(windowHandle, context);

		render(Nuklear.NK_ANTI_ALIASING_ON, MAX_VERTEX_BUFFER, MAX_ELEMENT_BUFFER);
	}

	public void cleanUp() {
		shutdown();

		Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
	}

	private void setupContext() {
		String NK_SHADER_VERSION = Platform.get() == Platform.MACOSX ? "#version 150\n" : "#version 300 es\n";
		String vertex_shader =
				NK_SHADER_VERSION +
				"uniform mat4 ProjMtx;\n" +
				"in vec2 Position;\n" +
				"in vec2 TexCoord;\n" +
				"in vec4 Color;\n" +
				"out vec2 Frag_UV;\n" +
				"out vec4 Frag_Color;\n" +
				"void main() {\n" +
				"   Frag_UV = TexCoord;\n" +
				"   Frag_Color = Color;\n" +
				"   gl_Position = ProjMtx * vec4(Position.xy, 0, 1);\n" +
				"}\n";
		String fragment_shader =
				NK_SHADER_VERSION +
				"precision mediump float;\n" +
				"uniform sampler2D Texture;\n" +
				"in vec2 Frag_UV;\n" +
				"in vec4 Frag_Color;\n" +
				"out vec4 Out_Color;\n" +
				"void main(){\n" +
				"   Out_Color = Frag_Color * texture(Texture, Frag_UV.st);\n" +
				"}\n";

		Nuklear.nk_buffer_init(cmds, ALLOCATOR, BUFFER_INITIAL_SIZE);
		prog = GL20C.glCreateProgram();
		vert_shdr = GL20C.glCreateShader(GL20C.GL_VERTEX_SHADER);
		frag_shdr = GL20C.glCreateShader(GL20C.GL_FRAGMENT_SHADER);
		GL20C.glShaderSource(vert_shdr, vertex_shader);
		GL20C.glShaderSource(frag_shdr, fragment_shader);
		GL20C.glCompileShader(vert_shdr);
		GL20C.glCompileShader(frag_shdr);
		if (GL20C.glGetShaderi(vert_shdr, GL20C.GL_COMPILE_STATUS) != GL11C.GL_TRUE) {
			throw new IllegalStateException();
		}
		if (GL20C.glGetShaderi(frag_shdr, GL20C.GL_COMPILE_STATUS) != GL11C.GL_TRUE) {
			throw new IllegalStateException();
		}
		GL20C.glAttachShader(prog, vert_shdr);
		GL20C.glAttachShader(prog, frag_shdr);
		GL20C.glLinkProgram(prog);
		if (GL20C.glGetProgrami(prog, GL20C.GL_LINK_STATUS) != GL11C.GL_TRUE) {
			throw new IllegalStateException();
		}

		uniform_tex = GL20C.glGetUniformLocation(prog, "Texture");
		uniform_proj = GL20C.glGetUniformLocation(prog, "ProjMtx");
		int attrib_pos = GL20C.glGetAttribLocation(prog, "Position");
		int attrib_uv  = GL20C.glGetAttribLocation(prog, "TexCoord");
		int attrib_col = GL20C.glGetAttribLocation(prog, "Color");

		// buffer setup
		vbo = GL15C.glGenBuffers();
		ebo = GL15C.glGenBuffers();
		vao = GL30C.glGenVertexArrays();

		GL30C.glBindVertexArray(vao);
		GL15C.glBindBuffer(GL15C.GL_ARRAY_BUFFER, vbo);
		GL15C.glBindBuffer(GL15C.GL_ELEMENT_ARRAY_BUFFER, ebo);

		GL20C.glEnableVertexAttribArray(attrib_pos);
		GL20C.glEnableVertexAttribArray(attrib_uv);
		GL20C.glEnableVertexAttribArray(attrib_col);

		GL20C.glVertexAttribPointer(attrib_pos, 2, GL11C.GL_FLOAT, false, 20, 0);
		GL20C.glVertexAttribPointer(attrib_uv, 2, GL11C.GL_FLOAT, false, 20, 8);
		GL20C.glVertexAttribPointer(attrib_col, 4, GL11C.GL_UNSIGNED_BYTE, true, 20, 16);

		// null texture setup
		int nullTexID = GL11C.glGenTextures();

		null_texture.texture().id(nullTexID);
		null_texture.uv().set(0.5f, 0.5f);

		GL11C.	glBindTexture(GL11C.GL_TEXTURE_2D, nullTexID);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			GL11C.glTexImage2D(GL11C.GL_TEXTURE_2D, 0, GL11C.GL_RGBA8, 1, 1, 0, GL11C.GL_RGBA, GL12C.GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(0xFFFFFFFF));
		}
		GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_NEAREST);
		GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_NEAREST);

		GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, 0);
		GL15C.glBindBuffer(GL15C.GL_ARRAY_BUFFER, 0);
		GL15C.glBindBuffer(GL15C.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30C.glBindVertexArray(0);
	}

	private void newFrame() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer l = stack.mallocInt(1);
			IntBuffer t = stack.mallocInt(1);
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);

			GLFW.glfwGetWindowPos(windowHandle, l, t);
			GLFW.glfwGetWindowSize(windowHandle, w, h);
			int maximized = GLFW.glfwGetWindowAttrib(windowHandle, GLFW.GLFW_MAXIMIZED);
			
			if (appSettings.getWindowLeft() != l.get(0)
					|| appSettings.getWindowTop() != t.get(0)
					|| appSettings.getWindowWidth() != w.get(0)
					|| appSettings.getWindowHeight() != h.get(0)
					|| appSettings.getMaximized() != maximized
				) {
				appSettings.setWindowLeft(l.get(0));
				appSettings.setWindowTop(t.get(0));
				appSettings.setWindowWidth(w.get(0));
				appSettings.setWindowHeight(h.get(0));
				appSettings.setMaximized(maximized);
				appSettings.saveToFile();
			}
			
			appSettings.setWindowWidth(w.get(0));
			appSettings.setWindowHeight(h.get(0));

			GLFW.glfwGetFramebufferSize(windowHandle, w, h);
			appSettings.setDisplayWidth(w.get(0));
			appSettings.setDisplayHeight(h.get(0));
		}
	}

	private void render(int AA, int max_vertex_buffer, int max_element_buffer) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// setup global state
			GL11C.glEnable(GL11.GL_BLEND);
			GL14C.glBlendEquation(GL14C.GL_FUNC_ADD);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11C.glDisable(GL11C.GL_CULL_FACE);
			GL11C.glDisable(GL11.GL_DEPTH_TEST);
			GL11C.glEnable(GL11C.GL_SCISSOR_TEST);
			GL13C.glActiveTexture(GL13C.GL_TEXTURE0);

			// setup program
			GL20C.glUseProgram(prog);
			GL20C.glUniform1i(uniform_tex, 0);
			GL20C.glUniformMatrix4fv(uniform_proj, false, stack.floats(
					2.0f / appSettings.getWindowWidth(), 0.0f, 0.0f, 0.0f,
					0.0f, -2.0f / appSettings.getWindowHeight(), 0.0f, 0.0f,
					0.0f, 0.0f, -1.0f, 0.0f,
					-1.0f, 1.0f, 0.0f, 1.0f
					));
			GL11C.glViewport(0, 0, appSettings.getDisplayWidth(), appSettings.getDisplayHeight());
		}

		// convert from command queue into draw list and draw to screen

		// allocate vertex and element buffer
		GL30C.glBindVertexArray(vao);
		GL15C.glBindBuffer(GL15C.GL_ARRAY_BUFFER, vbo);
		GL15C.glBindBuffer(GL15C.GL_ELEMENT_ARRAY_BUFFER, ebo);

		GL15C.glBufferData(GL15C.GL_ARRAY_BUFFER, max_vertex_buffer, GL15C.GL_STREAM_DRAW);
		GL15C.glBufferData(GL15C.GL_ELEMENT_ARRAY_BUFFER, max_element_buffer, GL15C.GL_STREAM_DRAW);

		// load draw vertices & elements directly into vertex + element buffer
		ByteBuffer vertices = Objects.requireNonNull(GL15C.glMapBuffer(GL15C.GL_ARRAY_BUFFER, GL15C.GL_WRITE_ONLY, max_vertex_buffer, null));
		ByteBuffer elements = Objects.requireNonNull(GL15C.glMapBuffer(GL15C.GL_ELEMENT_ARRAY_BUFFER, GL15C.GL_WRITE_ONLY, max_element_buffer, null));
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// fill convert configuration
			NkConvertConfig config = NkConvertConfig.calloc(stack)
					.vertex_layout(VERTEX_LAYOUT)
					.vertex_size(20)
					.vertex_alignment(4)
					.null_texture(null_texture)
					.circle_segment_count(22)
					.curve_segment_count(22)
					.arc_segment_count(22)
					.global_alpha(1.0f)
					.shape_AA(AA)
					.line_AA(AA);

			// setup buffers to load vertices and elements
			NkBuffer vbuf = NkBuffer.malloc(stack);
			NkBuffer ebuf = NkBuffer.malloc(stack);

			Nuklear.nk_buffer_init_fixed(vbuf, vertices/*, max_vertex_buffer*/);
			Nuklear.nk_buffer_init_fixed(ebuf, elements/*, max_element_buffer*/);
			Nuklear.nk_convert(context, cmds, vbuf, ebuf, config);
		}
		GL15C.glUnmapBuffer(GL15C.GL_ELEMENT_ARRAY_BUFFER);
		GL15C.glUnmapBuffer(GL15C.GL_ARRAY_BUFFER);

		// iterate over and execute each draw command
		float fb_scale_x = (float)appSettings.getDisplayWidth() / (float)appSettings.getWindowWidth();
		float fb_scale_y = (float)appSettings.getDisplayHeight() / (float)appSettings.getWindowHeight();

		long offset = MemoryUtil.NULL;
		for (NkDrawCommand cmd = Nuklear.nk__draw_begin(context, cmds); cmd != null; cmd = Nuklear.nk__draw_next(cmd, cmds, context)) {
			if (cmd.elem_count() == 0) {
				continue;
			}
			GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, cmd.texture().id());
			GL11C.glScissor(
					(int)(cmd.clip_rect().x() * fb_scale_x),
					(int)((appSettings.getWindowHeight() - (int)(cmd.clip_rect().y() + cmd.clip_rect().h())) * fb_scale_y),
					(int)(cmd.clip_rect().w() * fb_scale_x),
					(int)(cmd.clip_rect().h() * fb_scale_y)
					);
			GL11C.	glDrawElements(GL11C.GL_TRIANGLES, cmd.elem_count(), GL11C.GL_UNSIGNED_SHORT, offset);
			offset += cmd.elem_count() * 2;
		}
		
		Nuklear.nk_clear(context);
		Nuklear.nk_buffer_clear(cmds);

		// default OpenGL state
		GL20C.glUseProgram(0);
		GL15C.glBindBuffer(GL15C.GL_ARRAY_BUFFER, 0);
		GL15C.glBindBuffer(GL15C.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30C.glBindVertexArray(0);
		GL11C.glDisable(GL11C.GL_SCISSOR_TEST);
	}

	private void destroy() {
		GL20C.glDetachShader(prog, vert_shdr);
		GL20C.glDetachShader(prog, frag_shdr);
		GL20C.glDeleteShader(vert_shdr);
		GL20C.glDeleteShader(frag_shdr);
		GL20C.glDeleteProgram(prog);
		GL11C.glDeleteTextures(nuklearModel.getDefaultFont().texture().id());
		GL11C.glDeleteTextures(nuklearModel.getDefaultFontBold().texture().id());
		GL11C.glDeleteTextures(nuklearModel.getDefaultFontItalic().texture().id());
		GL11C.glDeleteTextures(null_texture.texture().id());
		GL15C.glDeleteBuffers(vbo);
		GL15C.glDeleteBuffers(ebo);
		Nuklear.nk_buffer_free(cmds);

		GL.setCapabilities(null);
	}

	private void shutdown() {
		Objects.requireNonNull(context.clip().copy()).free();
		Objects.requireNonNull(context.clip().paste()).free();
		Nuklear.nk_free(context);
		destroy();
		Objects.requireNonNull(nuklearModel.getDefaultFont().query()).free();
		Objects.requireNonNull(nuklearModel.getDefaultFont().width()).free();
		Objects.requireNonNull(nuklearModel.getDefaultFontBold().query()).free();
		Objects.requireNonNull(nuklearModel.getDefaultFontBold().width()).free();
		Objects.requireNonNull(nuklearModel.getDefaultFontItalic().query()).free();
		Objects.requireNonNull(nuklearModel.getDefaultFontItalic().width()).free();

		Objects.requireNonNull(ALLOCATOR.alloc()).free();
		Objects.requireNonNull(ALLOCATOR.mfree()).free();
	}

	public void beginInput() {
		Nuklear.nk_input_begin(context);
	}

	public void processInput() {
		NkMouse mouse = context.input().mouse();

		if (mouse.grab()) {
			//GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
		} else if (mouse.grabbed()) {
			float prevX = mouse.prev().x();
			float prevY = mouse.prev().y();
			GLFW.glfwSetCursorPos(windowHandle, prevX, prevY);
			mouse.pos().x(prevX);
			mouse.pos().y(prevY);
		} else if (mouse.ungrab()) {
			//GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		}
	}

	public void endInput() {
		Nuklear.nk_input_end(context);
	}

}