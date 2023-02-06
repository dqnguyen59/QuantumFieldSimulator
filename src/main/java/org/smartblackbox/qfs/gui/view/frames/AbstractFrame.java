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
package org.smartblackbox.qfs.gui.view.frames;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkColorf;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkPluginFilter;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.smartblackbox.qfs.Constants;
import org.smartblackbox.qfs.gui.model.DialogFileModel;
import org.smartblackbox.qfs.gui.model.GroupModel;
import org.smartblackbox.qfs.gui.model.NuklearModel;
import org.smartblackbox.qfs.gui.model.Theme;
import org.smartblackbox.qfs.settings.AppSettings;
import org.smartblackbox.utils.Utils;

/**
 * 
 * @author dqnguyen
 *
 */
public abstract class AbstractFrame implements IFrame {

	private static final int EDIT_IS_INVISIBLE = 0;
	private static final int EDIT_HAS_FOCUS = 1;
	private static final int EDIT_IS_VISIBLE = 2;
	private static final int EDIT_GAIN_FOCUS = 5;
	private static final int EDIT_LOST_FOCUS = 10;
	private static final int EDIT_ENTER_PRESSED = 17;

	private static NkPluginFilter asciiFilter = NkPluginFilter.create(Nuklear::nnk_filter_ascii);
	private static NkPluginFilter decimalFilter = NkPluginFilter.create(Nuklear::nnk_filter_decimal);
	private static NkPluginFilter floatFilter = NkPluginFilter.create(Nuklear::nnk_filter_float);

	protected AppSettings appSettings = AppSettings.getInstance(); 

	protected long windowHandle;
	private String _hiddenChar = " ";
	private int lastWindowWidth;
	private int lastWindowHeight;
	private int lastHeight;
	private int lastWidth;
	protected boolean isDialog;
	protected int spacer1 = 10;
	protected int spacer2 = 20;
	protected int spacerRow = 1;
	protected int spacerRow2 = 10;
	protected int menuItemHeight = 15;
	protected int rowHeight = appSettings.getFontSize() + 4;
	protected int rowHeightEdit = appSettings.getFontSize() + 6;

	protected NkRect windowRect = null;
	protected int windowOptions = 0
			| Nuklear.NK_WINDOW_TITLE
			| Nuklear.NK_WINDOW_BORDER
			| Nuklear.NK_WINDOW_MOVABLE
			| Nuklear.NK_WINDOW_CLOSABLE
			| Nuklear.NK_WINDOW_NO_SCROLLBAR
			;
	private boolean windowSizeChanged;

	private String button_label_pressed = "";
	protected NuklearModel model;
	private StringBuffer editingText = new StringBuffer();
	private int focusCount;
	private int tabIndex;
	private int lastTabIndex;
	private boolean tabKeyPressed;
	private boolean focusChanged;
	
	private List<GroupModel> groupModelList = new ArrayList<>();
	public GroupModel currentGroup;
	private boolean isTabIndexEnd;
	private boolean isTabIndexBegin;
	
	private Stack<NkColor> stackTextColor = new Stack<>();
	
	public AbstractFrame(NuklearModel model) {
		this.model = model;
	}

	public void close() {
		model.close(this);
	}

	@Override
	public void render(long windowHandle, NkContext ctx) {
		this.windowHandle = windowHandle;
		windowSizeChanged = lastWindowWidth != appSettings.getWindowWidth() || lastWindowHeight != appSettings.getWindowHeight();
		if (windowSizeChanged) {
			update();
		}

		lastWindowWidth = appSettings.getWindowWidth();
		lastWindowHeight = appSettings.getWindowHeight();
	}

	/**
	 * Trick to force dimension change.
	 */
	private void update() {
		_hiddenChar = "" + (_hiddenChar.contains(" ")? ((char) 13) : " ");
	}
	
	/**
	 * 
	 * Bug: unable to change the frame position!!!
	 * 
	 * A workaround to align the frame to the right or to change its position.
	 * Add this hidden character to the title of a frame nk_begin().
	 * 
	 * @return alternating character between space " " and character #13
	 */
	protected String getAlternateChar() {
		return _hiddenChar;
	}

	abstract public String getTitle();
	
	protected int getWindowWidth() {
		return appSettings.getWindowWidth();
	}

	protected int getWindowHeight() {
		return appSettings.getWindowHeight();
	}

	private void _createLayout(NkContext ctx, int x, int y, int width, int height, boolean centered) {
		if (lastHeight != height || lastWidth != height) {
			update();
			lastHeight = height;
			lastWidth = height;
		}
		
		if (centered) {
			if (windowRect == null) {
				x = (getWindowWidth() - width) / 2;
				y = (getWindowHeight() - height) / 2;			
			}
			else {
				x = (int) ((getWindowWidth() - windowRect.w()) / 2);
				y = (int) ((getWindowHeight() - windowRect.h()) / 2);
				windowRect.x(x);
				windowRect.y(y);
			}
		}
		
		// Create the rectangle that represents
		windowRect = NkRect.create();
		Nuklear.nk_rect(x, y, width, height, windowRect);
		
		if (model.getFocusedFrame() == this) {
			ctx.style().window().header().label_normal(Theme.colorHeaderTextActive);
			ctx.style().window().header().label_active(Theme.colorHeaderTextActive);
		}
		else {
			ctx.style().window().header().label_normal(Theme.colorHeaderText);
			ctx.style().window().header().label_active(Theme.colorHeaderText);
		}

		if (Nuklear.nk_begin(ctx, getTitle() + getAlternateChar(), windowRect, windowOptions)) {
			NkRect rect = NkRect.create();
			Nuklear.nk_window_get_content_region(ctx, rect);

			rect.x(rect.x() - 6);
			rect.w(rect.w() + 14);
			rect.y(rect.y() - 36);
			rect.h(rect.h() + 40);

			model.checkFocus(this, rect);

			ctx.style().window().header().label_normal(Theme.colorHeaderText);
			ctx.style().window().header().label_active(Theme.colorHeaderText);
			
			if (isDialog() && model.getDialogStack().getCurrent() != null) {
				model.getDialogStack().getCurrent().setWindowRect(rect);
			}
			
			if (Nuklear.nk_window_has_focus(ctx)) {
				// TODO: Make tab key work. Difficulty: very hard, maybe a modification of LWJGL source is required.
				
//				boolean shiftKeyDown = Nuklear.nk_input_is_key_down(ctx.input(), Nuklear.NK_KEY_SHIFT);
//				boolean tabKeyReleased = Nuklear.nk_input_is_key_released(ctx.input(), Nuklear.NK_KEY_TAB);
//				if (tabKeyReleased) {
//					System.out.println("Tab: " + tabKeyReleased + "; Shift: " + shiftKeyDown);
//					lastTabIndex = tabIndex;
//					if (shiftKeyDown)
//						tabIndex--;
//					else
//						tabIndex++;
//					if (tabIndex < 0) tabIndex = focusCount - 1;
//					else if (tabIndex >= focusCount) tabIndex = 0;
//					tabKeyPressed = true;
//				}
			}
			
			focusCount = 0;
			layout(ctx, x, y, width, height);
			
			tabKeyPressed = false;
			focusChanged = false;
		}
		else {
			close();
		}
		
		Nuklear.nk_end(ctx);
	}

	protected void createLayout(NkContext ctx, int x, int y, int width, int height) {
		_createLayout(ctx, x, y, width, height, false);
	}
	
	protected void createLayoutCentered(NkContext ctx, int width, int height) {
		_createLayout(ctx, 0, 0, width, height, true);
	}

	abstract protected void layout(NkContext ctx, int x, int y, int width, int height);

	public boolean isDialog() {
		return isDialog;
	}

	/**
	 * Change the text color and push the current color.
	 * Call {@link #popTextColor(NkContext)} to return to the previous color.
	 * 
	 * @param ctx
	 * @param color
	 */
	protected void pushTextColor(NkContext ctx, NkColor color) {
		stackTextColor.push(NkColor.create().set(ctx.style().text().color()));
		ctx.style().text().color(NkColor.create().set(color));
	}

	/**
	 * Set the text color to the saved (pushed) color. See {@link #pushTextColor(NkContext, NkColor)}
	 * 
	 * @param ctx
	 */
	protected void popTextColor(NkContext ctx) {
		ctx.style().text().color(stackTextColor.pop());
	}
	
	protected boolean nk_group_begin(NkContext ctx, MemoryStack stack, GroupModel groupModel, int option) {
		if (groupModelList.indexOf(groupModel) == -1)
			groupModelList.add(groupModel);
		
		groupModel.setBufGroupName(stack.calloc(256));
		MemoryUtil.memASCII(groupModel.getName(), false, groupModel.getBufGroupName());

		currentGroup = groupModel;
		boolean result = Nuklear.nk_group_begin(ctx, groupModel.getBufGroupName(), option);
		if (result) {
			IntBuffer bufScrollX = stack.callocInt(1);
			IntBuffer bufScrollY = stack.callocInt(1);
			Nuklear.nk_group_get_scroll(ctx, groupModel.getBufGroupName(), bufScrollX, bufScrollY);
			currentGroup.setScrollX(bufScrollX.get());
			currentGroup.setScrollY(bufScrollY.get());
		}
		return result;
	}

	protected void nk_group_end(NkContext ctx) {
		currentGroup.group_end();
		Nuklear.nk_group_end(ctx);
	}

	protected void nk_spacer(NkContext ctx, int spacer, int i) {
		Nuklear.nk_layout_row_dynamic(ctx, spacer , i);
		Nuklear.nk_spacer(ctx);
	}

	protected boolean nk_menu_item_label(NkContext ctx, String label) {
		boolean isReleased = false;

		float savedRouding = ctx.style().button().rounding();
		float savedBorder = ctx.style().button().border();
		NkColor savedColor = NkColor.create().set(ctx.style().button().normal().data().color());
		
		ctx.style().button().rounding(0);
		ctx.style().button().normal().data().color(Theme.colorBackground);
		ctx.style().button().border(0);
		
		if (label.equals(button_label_pressed) && (isReleased = !Nuklear.nk_input_is_mouse_down(ctx.input(), Nuklear.NK_BUTTON_LEFT))) {
			button_label_pressed = "";
		}
		
		ctx.style().button().text_alignment(Nuklear.NK_TEXT_ALIGN_MIDDLE | Nuklear.NK_TEXT_ALIGN_LEFT);
		if (Nuklear.nk_button_label(ctx, label)) {
			button_label_pressed = label;
		}
		
		ctx.style().button().rounding(savedRouding);
		ctx.style().button().border(savedBorder);
		ctx.style().button().normal().data().color(savedColor);
		return isReleased;
	}

	/**
	 * The current Nuklear.nk_button_label does not have release button.
	 * As a workaround, use this method to replace it. 
	 * 
	 * @param ctx
	 * @param label must be unique!
	 * @return true if mouse button is released.
	 */
	protected boolean nk_button_label(NkContext ctx, String label, int align) {
		boolean isReleased = false;

		if (label.equals(button_label_pressed) && (isReleased = !Nuklear.nk_input_is_mouse_down(ctx.input(), Nuklear.NK_BUTTON_LEFT))) {
			button_label_pressed = "";
		}

		ctx.style().button().text_alignment(Nuklear.NK_TEXT_ALIGN_MIDDLE | align);
		if (Nuklear.nk_button_label(ctx, label)) {
			button_label_pressed = label;
		}
		
		return isReleased;
	}

	protected void nk_label_value(NkContext ctx, String label, String value, double leftCol, double rightCol, int alignLabel, int alignValue) {
		Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, rowHeight, 2);
		
		Nuklear.nk_layout_row_push(ctx, (float) leftCol);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, label, alignLabel);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());
		
		Nuklear.nk_layout_row_push(ctx, (float) rightCol);
		Nuklear.nk_label(ctx, value, alignValue);
		
		Nuklear.nk_layout_row_end(ctx);
	}
	
	protected void nk_label_value(NkContext ctx, String label, String value, double leftCol, double rightCol) {
		Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, rowHeight, 2);
		
		Nuklear.nk_layout_row_push(ctx, (float) leftCol);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, label, Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());
		
		Nuklear.nk_layout_row_push(ctx, (float) rightCol);
		Nuklear.nk_label(ctx, value, Nuklear.NK_TEXT_LEFT);
		
		Nuklear.nk_layout_row_end(ctx);
	}
	
	protected boolean nk_button_label(NkContext ctx, String label) {
		return nk_button_label(ctx, label, Nuklear.NK_TEXT_ALIGN_CENTERED);
	}

	protected boolean nk_check_label(NkContext ctx, String label, boolean value, double leftCol, double rightCol) {
		Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, rowHeightEdit, 2);
		
		Nuklear.nk_layout_row_push(ctx, (float) leftCol);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, label, Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());
		
		Nuklear.nk_layout_row_push(ctx, (float) rightCol);
		value = Nuklear.nk_check_label(ctx, "", value);
		
		Nuklear.nk_layout_row_end(ctx);
		return value;
	}

//	protected boolean nk_check_label(NkContext ctx, String label, boolean value) {
//		Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, rowHeightEdit, 2);
//		Nuklear.nk_label(ctx, label, Nuklear.NK_TEXT_LEFT);
//		value = Nuklear.nk_check_label(ctx, "", value);
//		Nuklear.nk_layout_row_end(ctx);
//		return value;
//	}

	/////////////////////////////////////////////////////////////////////////
	/// LABEL EDIT STRING ///////////////////////////////////////////////////

	protected String editString(NkContext ctx, MemoryStack stack, StringBuffer stringBuffer, String value, NkPluginFilter filter) {
		// TODO: experimental for now. 'mode' should be optional instead of hard coded.
		int mode = Nuklear.NK_EDIT_DEFAULT
				//| Nuklear.NK_EDIT_READ_ONLY
				| Nuklear.NK_EDIT_AUTO_SELECT
				| Nuklear.NK_EDIT_SIG_ENTER
				| Nuklear.NK_EDIT_ALLOW_TAB
				//| Nuklear.NK_EDIT_NO_CURSOR
				| Nuklear.NK_EDIT_SELECTABLE
				| Nuklear.NK_EDIT_CLIPBOARD
				//| Nuklear.NK_EDIT_CTRL_ENTER_NEWLINE
				//| Nuklear.NK_EDIT_NO_HORIZONTAL_SCROLL
				| Nuklear.NK_EDIT_ALWAYS_INSERT_MODE
				//| Nuklear.NK_EDIT_MULTILINE
				//| Nuklear.NK_EDIT_GOTO_END_ON_ACTIVATE
				;
		
		ByteBuffer buffer = stack.calloc(256);
		String text = stringBuffer.length() == 0? value : stringBuffer.toString();
		int length = MemoryUtil.memASCII(text, false, buffer);
		IntBuffer len = stack.ints(length);
		
		// If tab key is pressed, then focus the edit field where tabIndex == focusCount.
		if ((tabKeyPressed || currentGroup != null && currentGroup.is_scroll_changed(ctx, stack)) && tabIndex == focusCount) {
			Nuklear.nk_edit_focus(ctx, mode);
			Nuklear.nk_input_key(ctx, Nuklear.NK_KEY_TEXT_SELECT_ALL, true);
		}
		
		int res = Nuklear.nk_edit_string(ctx, mode, buffer, len, 255, filter);

		// Make sure the focus of the current edit field is lost when a tab key is pressed.
		if ((focusChanged ||tabKeyPressed) && lastTabIndex == focusCount) {
			res = EDIT_LOST_FOCUS;
			focusChanged = false;
		}

		int direction = Nuklear.nk_input_is_key_down(ctx.input(), Nuklear.NK_KEY_SHIFT)? -1 : 1;
		
		switch (res) {
		case EDIT_IS_INVISIBLE:
			if ((tabKeyPressed || currentGroup != null && currentGroup.isScrolling()) && tabIndex == focusCount) {
				if (isTabIndexEnd) {
					Nuklear.nk_group_set_scroll(ctx, currentGroup.getBufGroupName(), 0, 0);
					currentGroup.setScrollY(0);
					currentGroup.setLastScrollY(0);
					isTabIndexEnd = false;
				}
				else if (isTabIndexBegin) {
					Nuklear.nk_group_set_scroll(ctx, currentGroup.getBufGroupName(), 0, 9999999);
					currentGroup.setScrollY(9999999);
					currentGroup.setLastScrollY(9999999);
					isTabIndexBegin = false;
				}
				else {
					Nuklear.nk_group_set_scroll(ctx, currentGroup.getBufGroupName(), 0, currentGroup.getScrollY() + 5 * direction);
					currentGroup.setTargetScrollY(currentGroup.getLastScrollY() + 35 * direction);
					currentGroup.setScrolling(true);
				}
			}
			break;
		case EDIT_HAS_FOCUS:
			if (stringBuffer != null) {
				if (stringBuffer.length() == 0)
					stringBuffer.append(text);
				else {
					stringBuffer.setLength(0);
					stringBuffer.append(MemoryUtil.memASCII(buffer, len.get(0)));
				}
			}
			if (tabIndex == focusCount && currentGroup != null && currentGroup.isScrolling()) {
				Nuklear.nk_group_set_scroll(ctx, currentGroup.getBufGroupName(), 0, currentGroup.getScrollY() + 5 * direction);
				if (currentGroup.isScrollYTargetReached()) {
					currentGroup.setScrolling(false);
					if (tabIndex == currentGroup.getMinFocusIndex())
						isTabIndexBegin = true;
					if (tabIndex == currentGroup.getMaxFocusIndex())
						isTabIndexEnd = true;
				}
			}
			break;
		case EDIT_GAIN_FOCUS:
			if (!tabKeyPressed) {
				lastTabIndex = tabIndex; 
				tabIndex = focusCount;
			}
			focusChanged = true;
			break;
		case EDIT_IS_VISIBLE:
			break;
		case EDIT_LOST_FOCUS:
		case EDIT_ENTER_PRESSED:
			stringBuffer.setLength(0);
			value = MemoryUtil.memASCII(buffer, len.get(0));
			break;
		}

		if (currentGroup != null) {
			currentGroup.setMinFocusIndex(focusCount);
			currentGroup.setMaxFocusIndex(focusCount);
		}
		
		focusCount++;
		return value;		
	}

	protected String nk_label_edit(NkContext ctx, MemoryStack stack, String label, StringBuffer stringBuffer, String value, double leftCol, double rightCol) {
		Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, rowHeightEdit, 2);
		
		Nuklear.nk_layout_row_push(ctx, (float) leftCol);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, label, Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());
		
		Nuklear.nk_layout_row_push(ctx, (float) rightCol);
		value = editString(ctx, stack, stringBuffer, value, asciiFilter);
		
		Nuklear.nk_layout_row_end(ctx);
		return value;
	}

	/// LABEL EDIT STRING ///////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	/// LABEL EDIT INTEGER //////////////////////////////////////////////////

	protected int editInt(NkContext ctx, MemoryStack stack, StringBuffer stringBuffer, int value) {
		DecimalFormat format = appSettings.getFormatInt();
		String currentValue = format.format(value);
		String newValue = editString(ctx, stack, stringBuffer, currentValue, decimalFilter);
		if (!newValue.equalsIgnoreCase(currentValue)) {
			try {
				value = format.parse(newValue).intValue();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	protected int nk_label_edit(NkContext ctx, MemoryStack stack, String label, StringBuffer stringBuffer, int value, double leftCol, double rightCol) {
		Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, rowHeightEdit, 2);
		
		Nuklear.nk_layout_row_push(ctx, (float) leftCol);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, label, Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());

		Nuklear.nk_layout_row_push(ctx, (float) rightCol);
		value = editInt(ctx, stack, stringBuffer, value);
		
		Nuklear.nk_layout_row_end(ctx);
		return value;
	}

	/// LABEL EDIT INTEGER //////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	/// LABEL EDIT DOUBLE ///////////////////////////////////////////////////

	protected double editDouble(NkContext ctx, MemoryStack stack, StringBuffer stringBuffer, double value, DecimalFormat format) {
		String currentValue = format.format(value);
		String newValue = editString(ctx, stack, stringBuffer, currentValue, floatFilter);
		if (!newValue.equalsIgnoreCase(currentValue)) {
			try {
				value = format.parse(newValue).doubleValue();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	protected double editDouble(NkContext ctx, MemoryStack stack, double value) {
		return editDouble(ctx, stack, editingText, value, appSettings.getFormatDefault());
	}

	protected float editFloat(NkContext ctx, MemoryStack stack, StringBuffer stringBuffer, float value) {
		return (float) editDouble(ctx, stack, stringBuffer, value, appSettings.getFormatDefault());
	}

	protected double nk_label_edit(NkContext ctx, MemoryStack stack, String label, StringBuffer buffer, double value, DecimalFormat format) {
		Nuklear.nk_layout_row_dynamic(ctx, rowHeightEdit, 1);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, label, Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());
		return editDouble(ctx, stack, buffer, value, format);
	}

	protected double nk_label_edit(NkContext ctx, MemoryStack stack, String label, StringBuffer buffer, double value) {
		return nk_label_edit(ctx, stack, label, buffer, value, appSettings.getFormatDefault());
	}

	protected double nk_label_edit(NkContext ctx, MemoryStack stack, String label, StringBuffer buffer,
			double value, DecimalFormat format, double leftCol, double rightCol) {
		Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, rowHeightEdit, 2);
		
		Nuklear.nk_layout_row_push(ctx, (float) leftCol);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, label, Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());
		
		Nuklear.nk_layout_row_push(ctx, (float) rightCol);
		value = editDouble(ctx, stack, buffer, value, format);
		
		Nuklear.nk_layout_row_end(ctx);
		return value;
	}

	protected double nk_label_edit(NkContext ctx, MemoryStack stack, String label, StringBuffer buffer, double value, double leftCol, double rightCol) {
		return nk_label_edit(ctx, stack, label, buffer, value, appSettings.getFormatDefault(), leftCol, rightCol);
	}

	/// LABEL EDIT DOUBLE ///////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	/// LABEL EDIT FLOAT ////////////////////////////////////////////////////

	public float nk_label_edit(NkContext ctx, MemoryStack stack, String label, StringBuffer buffer, float value, DecimalFormat format) {
		return (float) nk_label_edit(ctx, stack, label, buffer, (double) value, format);
	}

	public float nk_label_edit(NkContext ctx, MemoryStack stack, String label, StringBuffer buffer, float value) {
		return nk_label_edit(ctx, stack, label, buffer, value, appSettings.getFormatDefault());
	}

	public float nk_label_edit(NkContext ctx, MemoryStack stack, String label, StringBuffer buffer, float value, double leftCol, double rightCol) {
		return (float) nk_label_edit(ctx, stack, label, buffer, (double) value, leftCol, rightCol);
	}

	/// LABEL EDIT FLOAT ////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////

	public String nk_option(NkContext ctx, String[] labels, String value, int columns) {
		boolean isChanged = false;
		Nuklear.nk_layout_row_dynamic(ctx, spacerRow, 1);
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, columns);

		for (String label : labels) {
			if (Nuklear.nk_option_label(ctx, label, value.equalsIgnoreCase(label))) {
				if (!isChanged && !label.equalsIgnoreCase(value)) {
					isChanged = true;
					value = label;
				}
			}
		}

		return value;
	}

	public String nk_option(NkContext ctx, String[] values, String value) {
		return nk_option(ctx, values, value, values.length);
	}

	protected String nk_label_combo_options(NkContext ctx, MemoryStack stack, String label, String[] values, String value, double leftCol, double rightCol) {
		Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, rowHeightEdit, 2);
		
		Nuklear.nk_layout_row_push(ctx, (float) leftCol);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, label, Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());
		
		Nuklear.nk_layout_row_push(ctx, (float) rightCol);
		value = nk_combo_options(ctx, stack, values, value);
		
		Nuklear.nk_layout_row_end(ctx);
		return value;
	}

	protected String nk_label_Options(NkContext ctx, String label, String[] values, String value, double leftCol, double rightCol, int rightColumns) {
		Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, rowHeightEdit, 2);
		
		Nuklear.nk_layout_row_push(ctx, (float) leftCol);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, label, Nuklear.NK_TEXT_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());
		
		boolean isChanged = false;
		for (String s : values) {
			Nuklear.nk_layout_row_push(ctx, (float) rightCol / rightColumns);
			if (Nuklear.nk_option_label(ctx, s, value.equalsIgnoreCase(s))) {
				if (!isChanged && !s.equalsIgnoreCase(value)) {
					isChanged = true;
					value = s;
				}
			}
		}
		
		Nuklear.nk_layout_row_end(ctx);
		return value;
	}

	protected double nk_slider(NkContext ctx, double min, double value, double max, double step) {
		float[] valueA = {(float) value};
		Nuklear.nk_layout_row_dynamic(ctx, spacerRow, 1);
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
		if (Nuklear.nk_slider_float(ctx, (float) min, valueA, (float) max, (float) step))
			return valueA[0];
		else
			return value;
	}

	protected float nk_slider(NkContext ctx, double min, float value, double max, double step) {
		return (float) nk_slider(ctx, min, (double) value, max, step);
	}

	protected void tooltip(NkContext ctx, String text) {
		if (Nuklear.nk_widget_is_hovered(ctx))
			Nuklear.nk_tooltip(ctx, text);
	}

	protected Vector3f nk_combo_color_picker(NkContext ctx, MemoryStack stack, Vector3f color) {
		NkColorf c = NkColorf.create();
		c.set(color.x, color.y, color.z, 1.0f);

		if (Nuklear.nk_combo_begin_color(ctx,
				Nuklear.nk_rgb_cf(c, NkColor.malloc(stack)),
				NkVec2.malloc(stack).set(Nuklear.nk_widget_width(ctx), 400))) {
			Nuklear.nk_layout_row_dynamic(ctx, 120, 1);
			Nuklear.nk_color_picker(ctx, c, Nuklear.NK_RGB);
			Nuklear.nk_layout_row_dynamic(ctx, 25, 1);
			c.r(Nuklear.nk_propertyf(ctx, "#R:", 0, c.r(), 1.0f, 0.01f, 0.005f))
			.g(Nuklear.nk_propertyf(ctx, "#G:", 0, c.g(), 1.0f, 0.01f, 0.005f))
			.b(Nuklear.nk_propertyf(ctx, "#B:", 0, c.b(), 1.0f, 0.01f, 0.005f));
			color.set(c.r(), c.g(), c.b());
			Nuklear.nk_combo_end(ctx);
		}
		return color;
	}

	protected Vector3f nk_combo_color_picker(NkContext ctx, MemoryStack stack, String label, Vector3f color, double leftCol, double rightCol) {
		Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, rowHeight, 2);
		Nuklear.nk_layout_row_push(ctx, (float) leftCol);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, label, Nuklear.NK_TEXT_ALIGN_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());
		Nuklear.nk_layout_row_push(ctx, (float) rightCol);
		color = nk_combo_color_picker(ctx, stack, color);
		Nuklear.nk_layout_row_end(ctx);
		return color;
	}

	protected Vector4f nk_combo_color_picker(NkContext ctx, MemoryStack stack, Vector4f color) {
		NkColorf c = NkColorf.create();
		c.set(color.x, color.y, color.z, color.w);

		if (Nuklear.nk_combo_begin_color(ctx,
				Nuklear.nk_rgb_cf(c, NkColor.malloc(stack)),
				NkVec2.malloc(stack).set(Nuklear.nk_widget_width(ctx), 400))) {
			Nuklear.nk_layout_row_dynamic(ctx, 120, 1);
			Nuklear.nk_color_picker(ctx, c, Nuklear.NK_RGBA);
			Nuklear.nk_layout_row_dynamic(ctx, 25, 1);
			c.r(Nuklear.nk_propertyf(ctx, "#R:", 0, c.r(), 1.0f, 0.01f, 0.005f))
			.g(Nuklear.nk_propertyf(ctx, "#G:", 0, c.g(), 1.0f, 0.01f, 0.005f))
			.b(Nuklear.nk_propertyf(ctx, "#B:", 0, c.b(), 1.0f, 0.01f, 0.005f))
			.a(Nuklear.nk_propertyf(ctx, "#A:", 0, c.a(), 1.0f, 0.01f, 0.005f));
			color.set(c.r(), c.g(), c.b(), c.a());
			Nuklear.nk_combo_end(ctx);
		}
		return color;
	}

	protected Vector4f nk_combo_color_picker(NkContext ctx, MemoryStack stack, String label, Vector4f color, double leftCol, double rightCol) {
		Nuklear.nk_layout_row_begin(ctx, Nuklear.NK_DYNAMIC, rowHeight, 2);
		Nuklear.nk_layout_row_push(ctx, (float) leftCol);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFontBold());
		Nuklear.nk_label(ctx, label, Nuklear.NK_TEXT_ALIGN_LEFT);
		Nuklear.nk_style_set_font(ctx, model.getDefaultFont());
		Nuklear.nk_layout_row_push(ctx, (float) rightCol);
		color = nk_combo_color_picker(ctx, stack, color);
		Nuklear.nk_layout_row_end(ctx);
		return color;
	}

	protected String nk_combo_options(NkContext ctx, MemoryStack stack, String[] list, String currentValue) {
		if (Nuklear.nk_combo_begin_label(ctx, currentValue,
				NkVec2.malloc(stack).set(Nuklear.nk_widget_width(ctx), 400))) {

			String newValue = nk_option(ctx, list, currentValue, 1);

			if (!newValue.equalsIgnoreCase(currentValue)) {
				currentValue = newValue; 
				Nuklear.nk_combo_close(ctx);
			}

			Nuklear.nk_combo_end(ctx);
		}
		return currentValue;
	}

	public String getSelectedFile() {
		return ((DialogFileModel) model.getDialogStack().getCurrent()).getSelectedFileName();
	}
    
	public void setSelectedFile(String fileName) {
		((DialogFileModel) model.getDialogStack().getCurrent()).setSelectedFileName(fileName);
	}

	public boolean nk_file_list_view(NkContext ctx, String path, String projectFileExt, DialogFileModel dlgFileModel) {
		boolean isDoubleClicked = false;
		
		if (Nuklear.nk_group_begin(ctx, "ScrollableFiles", 0)) {

			Nuklear.nk_layout_row_dynamic(ctx, rowHeight, 1);
			List<File> files = Utils.getFiles(path, Constants.PROJECT_FILE_EXT);
			
			if (dlgFileModel.getSelectedFileName().isEmpty() && files.size() > 0)
				dlgFileModel.setSelectedFileName(files.get(0).getName()); 
			
			for (File file : files) {
				boolean selected = dlgFileModel.getSelectedFileName().equalsIgnoreCase(file.getName());
				boolean widgetMouseClickDown = Nuklear.nk_widget_is_mouse_clicked(ctx, Nuklear.NK_BUTTON_LEFT);
				if (Nuklear.nk_select_label(ctx, file.getName(), Nuklear.NK_TEXT_ALIGN_LEFT, selected)) {
					dlgFileModel.setSelectedFileName(file.getName());
					dlgFileModel.setSelectedFilePath(path + Constants.SEPARATOR + file.getName());
					if (widgetMouseClickDown && model.isDoubleClicked()) {
						isDoubleClicked = true;
					}
				}
			}
			Nuklear.nk_group_end(ctx);
		}
		return isDoubleClicked;
	}

	public void nk_layout_row_dynamic(NkContext ctx, int rowHeight, int cols) {
		Nuklear.nk_layout_row_dynamic(ctx, rowHeight, cols);
	}

	public void nk_spacer(NkContext ctx, int spaceHeight) {
		Nuklear.nk_layout_row_dynamic(ctx, spaceHeight, 1);
		Nuklear.nk_spacer(ctx);
	}

}
