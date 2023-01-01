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
package org.smartblackbox.core.qfs.gui.model;

import java.nio.ByteBuffer;

import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

public class Theme {
	public static NkColor white = createColor(255, 255, 255, 255);
	public static NkColor black = createColor(0, 0, 0, 255);
	public static NkColor grey01 = createColor(0X10, 0X10, 0X10, 255);
	public static NkColor grey02 = createColor(0X20, 0X20, 0X20, 255);
	public static NkColor grey03 = createColor(0X30, 0X30, 0X30, 255);
	public static NkColor grey04 = createColor(0X40, 0X40, 0X40, 255);
	public static NkColor grey05 = createColor(0X50, 0X50, 0X50, 255);
	public static NkColor grey06 = createColor(0X60, 0X60, 0X60, 255);
	public static NkColor grey07 = createColor(0X70, 0X70, 0X70, 255);
	public static NkColor grey08 = createColor(0X80, 0X80, 0X80, 255);
	public static NkColor grey09 = createColor(0X90, 0X90, 0X90, 255);
	public static NkColor grey10 = createColor(0XA0, 0XA0, 0XA0, 255);
	public static NkColor grey11 = createColor(0XB0, 0XB0, 0XB0, 255);
	public static NkColor grey12 = createColor(0XC0, 0XC0, 0XC0, 255);
	public static NkColor grey13 = createColor(0XD0, 0XD0, 0XD0, 255);
	public static NkColor grey14 = createColor(0XE0, 0XE0, 0XE0, 255);
	public static NkColor blue1 = createColor(0x48, 0x58, 0xFF, 255);
	public static NkColor blue2 = createColor(0x66, 0x78, 0xA5, 255);
	public static NkColor blue3 = createColor(94, 120, 200, 255);
	public static NkColor blue4 = createColor(148, 255, 255, 255);
	public static NkColor red = createColor(255, 0, 0, 255);
	public static NkColor green = createColor(0, 255, 0, 255);
	public static NkColor yellow = createColor(255, 255, 0, 255);
	public static NkColor blue = createColor(0, 0, 255, 255);

	public static NkColor colorBackground = grey02;
	public static NkColor colorText = grey12;
	public static NkColor colorWindow = black;
	public static NkColor colorHeader = grey05;
	public static NkColor colorHeaderText = grey12;
	public static NkColor colorHeaderTextActive = blue4;
	public static NkColor colorBorder = grey03;
	public static NkColor colorButton = grey04;
	public static NkColor colorButtonHover = blue3;
	public static NkColor colorButtonActive = grey08;
	public static NkColor colorToggle = grey05;
	public static NkColor colorToggleHover = grey07;
	public static NkColor colorToggleCursor = grey12;
	public static NkColor colorSelect = grey03;
	public static NkColor colorSelected = grey05;
	public static NkColor colorSelectActive = white;
	public static NkColor colorSlider = grey12;
	public static NkColor colorSliderCursor = blue2;
	public static NkColor colorSliderCursorHover = blue3;
	public static NkColor colorSliderCursorActive = black;
	public static NkColor colorProperty = grey04;
	public static NkColor colorEdit = grey03;
	public static NkColor colorEditCursor = black;
	public static NkColor colorCombo = grey04;
	public static NkColor colorChart = grey06;
	public static NkColor colorChartColor = grey01;
	public static NkColor colorChartColorHighLight = red;
	public static NkColor colorScrollbar = grey04;
	public static NkColor colorScrollbarCursor = grey08;
	public static NkColor colorScrollbarCursorHover = grey10;
	public static NkColor colorScrollbarCursorActive = grey12;
	public static NkColor colorTabHeader = grey11;
	public static NkColor groupBorderColor = grey05;

	public static void init(NkContext context) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			int size = NkColor.SIZEOF * Nuklear.NK_COLOR_COUNT;
			ByteBuffer buffer = stack.calloc(size);
			NkColor.Buffer colors = new NkColor.Buffer(buffer);
			colors.put(Nuklear.NK_COLOR_TEXT, colorText);
			colors.put(Nuklear.NK_COLOR_WINDOW, colorBackground);
			colors.put(Nuklear.NK_COLOR_HEADER, colorHeader);
			colors.put(Nuklear.NK_COLOR_BORDER, colorBorder);
			colors.put(Nuklear.NK_COLOR_BUTTON, colorButton);
			colors.put(Nuklear.NK_COLOR_BUTTON_HOVER, colorButtonHover);
			colors.put(Nuklear.NK_COLOR_BUTTON_ACTIVE, colorButtonActive);
			colors.put(Nuklear.NK_COLOR_TOGGLE, colorToggle);
			colors.put(Nuklear.NK_COLOR_TOGGLE_HOVER, colorToggleHover);
			colors.put(Nuklear.NK_COLOR_TOGGLE_CURSOR, colorToggleCursor);
			colors.put(Nuklear.NK_COLOR_SELECT, colorSelect);
			colors.put(Nuklear.NK_COLOR_SELECT_ACTIVE, colorSelectActive);
			colors.put(Nuklear.NK_COLOR_SLIDER, colorSlider);
			colors.put(Nuklear.NK_COLOR_SLIDER_CURSOR, colorSliderCursor);
			colors.put(Nuklear.NK_COLOR_SLIDER_CURSOR_HOVER, colorSliderCursorHover);
			colors.put(Nuklear.NK_COLOR_SLIDER_CURSOR_ACTIVE, colorSliderCursorHover);
			colors.put(Nuklear.NK_COLOR_PROPERTY, colorProperty);
			colors.put(Nuklear.NK_COLOR_EDIT, colorEdit);
			colors.put(Nuklear.NK_COLOR_EDIT_CURSOR, colorEditCursor);
			colors.put(Nuklear.NK_COLOR_COMBO, colorCombo);
			colors.put(Nuklear.NK_COLOR_CHART, colorChart);
			colors.put(Nuklear.NK_COLOR_CHART_COLOR, colorChartColor);
			colors.put(Nuklear.NK_COLOR_CHART_COLOR_HIGHLIGHT, colorChartColorHighLight);
			colors.put(Nuklear.NK_COLOR_SCROLLBAR, colorScrollbar);
			colors.put(Nuklear.NK_COLOR_SCROLLBAR_CURSOR, colorScrollbarCursor);
			colors.put(Nuklear.NK_COLOR_SCROLLBAR_CURSOR_HOVER, colorScrollbarCursorHover);
			colors.put(Nuklear.NK_COLOR_SCROLLBAR_CURSOR_ACTIVE, colorScrollbarCursorActive);
			colors.put(Nuklear.NK_COLOR_TAB_HEADER, colorTabHeader);
	        Nuklear.nk_style_from_table(context, colors);

			context.style().window().group_border_color(Theme.groupBorderColor);

			NkVec2 padding = context.style().window().group_padding();
			padding.x(4);
			padding.y(4);
		
			context.style().selectable().hover().data().color(Theme.blue2);
			context.style().selectable().hover_active().data().color(Theme.grey13);
			context.style().selectable().normal_active().data().color(Theme.grey13);
			context.style().selectable().pressed_active().data().color(Theme.grey13);
			context.style().selectable().text_hover(Theme.white);
			context.style().selectable().text_normal_active(Theme.blue1);
			context.style().selectable().text_hover_active(Theme.blue1);
			context.style().selectable().text_pressed(Theme.blue1);
			context.style().selectable().text_pressed_active(Theme.blue1);
		}
	}

	public static NkColor createColor(int r, int g, int b, int a) {
		return NkColor.create().r((byte)r).g((byte)g).b((byte)b).a((byte)a);
	}	

}
