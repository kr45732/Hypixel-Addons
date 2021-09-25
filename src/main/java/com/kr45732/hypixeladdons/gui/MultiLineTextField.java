/*
 * Hypixel Addons - A quality of life mod for Hypixel
 * Copyright (c) 2021-2021 kr45732
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kr45732.hypixeladdons.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MultiLineTextField extends Gui {

	/** Reference of the minecraft instance for convenience **/
	private final Minecraft mc;
	/** The id of this textbox **/
	private final int id;
	/** X position of the textbox **/
	private final int x;
	/** Y position of the textbox **/
	private final int y;
	/** Width of the textbox **/
	private final int width;
	/** Calculated height of the textbox **/
	private int height;
	/** Calculated height of each line **/
	private final int lineHeight;
	/** Max lines the textbox can have **/
	private final int maxLines;
	/** Max length of a line **/
	private int maxLineLength = 100;
	/** If the textbox is focused **/
	private boolean isFocused = true;
	/** Cursor line number **/
	private int cursorLineNumber = 0;
	/** Cursor character number **/
	private int cursorCharNumber = 0;
	/** Selection line number **/
	private int selectionLineNumber = 0;
	/** Selection character number **/
	private int selectionCharNumber = 0;
	/** Used to determine when to make the cursor blink **/
	private int cursorCount = 0;
	/** Stores the lines of the textbox **/
	private final List<String> lines = new ArrayList<>();

	/**
	 * @param id The id of this textbox
	 * @param x X position of the textbox
	 * @param y Y position of the textbox
	 * @param width Width in pixels of the textbox
	 * @param maxLines Max lines the textbox can have
	 */
	public MultiLineTextField(int id, int x, int y, int width, int maxLines) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.maxLines = maxLines;

		this.mc = Minecraft.getMinecraft();
		this.lineHeight = mc.fontRendererObj.FONT_HEIGHT;
		this.height = maxLines * lineHeight;
		this.lines.add("");
	}

	/**
	 * Draw the textbox on the screen. This will draw the background, text, and cursor.
	 */
	public void drawTextBox() {
		try {
			// Background
			GuiScreen.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -6250336);

			// Lines
			int lineCount = 0; // Extra lines count if a line wrapped
			for (String line : lines) {
				mc.fontRendererObj.drawSplitString(line, this.x, this.y + lineCount * lineHeight, width, 0xFFFFFFFF);
				lineCount += mc.fontRendererObj.listFormattedStringToWidth(line, width).size();
			}

			if (isFocused) {
				if (hasSelection()) { // Selected text highlight
					int startLine = Math.min(cursorLineNumber, selectionLineNumber);
					int endLine = Math.max(cursorLineNumber, selectionLineNumber);

					if (startLine == endLine) {
						int startX =
							x +
							mc.fontRendererObj.getStringWidth(
								lines.get(cursorLineNumber).substring(0, Math.min(cursorCharNumber, selectionCharNumber))
							);
						int startY = y + Math.min(cursorLineNumber, selectionLineNumber) * lineHeight;
						int endX =
							x +
							mc.fontRendererObj.getStringWidth(
								lines.get(cursorLineNumber).substring(0, Math.max(cursorCharNumber, selectionCharNumber))
							);
						int endY = startY + lineHeight;

						drawHighlight(startX, startY, endX, endY);
					} else {
						for (int i = startLine; i <= endLine; i++) {
							int startX;
							int startY;
							int endX;
							int endY;
							if (i == startLine) {
								startX =
									x +
									mc.fontRendererObj.getStringWidth(
										lines.get(i).substring(0, Math.min(cursorCharNumber, selectionCharNumber))
									);
								startY = y + i * lineHeight;
								endX = x + mc.fontRendererObj.getStringWidth(lines.get(i));
								endY = startY + lineHeight;
							} else if (i == endLine) {
								startX = x;
								startY = y + i * lineHeight;
								endX =
									x +
									mc.fontRendererObj.getStringWidth(
										lines.get(i).substring(0, Math.max(cursorCharNumber, selectionCharNumber))
									);
								endY = startY + lineHeight;
							} else {
								startX = x;
								startY = y + i * lineHeight;
								endX = x + mc.fontRendererObj.getStringWidth(lines.get(i));
								endY = startY + lineHeight;
							}

							//							System.out.println(startX + " - " + startY + " - " + endX + " - " + endY);

							drawHighlight(startX, startY, endX, endY);
						}
					}
				} else if (cursorCount / 8 % 2 != 0) { // Blink (hide) every 8 cursorCount
					// Cursor (why are you so complicated)
					int totalLines = 1;

					for (int i = 0; i < cursorLineNumber; i++) {
						totalLines += mc.fontRendererObj.listFormattedStringToWidth(lines.get(i), width).size();
					}

					List<String> currentLineSplit = mc.fontRendererObj.listFormattedStringToWidth(lines.get(cursorLineNumber), width); // Get the wrapped text
					StringBuilder currentLineText = new StringBuilder(lines.get(cursorLineNumber).substring(0, cursorCharNumber)); // If the text wasn't wrapped, this would be the text to the left of the cursor
					if (currentLineSplit.size() > 1) { // If the text is wrapped
						int index = 0;
						for (int i = 0; i < currentLineSplit.size(); i++) {
							String splitText = currentLineSplit.get(i);
							index += splitText.length();

							if (index + 1 >= cursorCharNumber) { // The cursor would be on this line
								totalLines += i;
								index -= splitText.length();

								// Find the x offset (number of chars to the left of the cursor of the split line)
								currentLineText = new StringBuilder();
								for (int k = 0; k < splitText.length(); k++) {
									index++;
									currentLineText.append(splitText.charAt(k));

									if (index + 1 == cursorCharNumber) {
										break;
									}
								}
								break;
							}
						}
					}

					int left = x + mc.fontRendererObj.getStringWidth(currentLineText.toString());
					int top = y + totalLines * lineHeight;
					GuiScreen.drawRect(left, top, left + 1, top - lineHeight, 0xFF000000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean hasSelection() {
		return cursorLineNumber != selectionLineNumber || cursorCharNumber != selectionCharNumber;
	}

	/**
	 * Handles mouse clicks. Updates if the textbox is focused and if the cursor positions.
	 * @param mouseX The x position of where the mouse was clicked
	 * @param mouseY The y position of where the mouse was clicked
	 * @param mouseButton The mouse button that was clicked
	 */
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		try {
			setFocused(GuiUtils.isPointInRegion(x, y, width, height, mouseX, mouseY));

			if (isFocused && mouseButton == 0) { // Clicked in textbox
				int total = 0;
				for (int i = 0; i < lines.size(); i++) {
					total += mc.fontRendererObj.listFormattedStringToWidth(lines.get(i), width).size() * lineHeight;

					if (mouseY - y < total) {
						setCursorLine(MathHelper.clamp_int(i, 0, lines.size() - 1));
						break;
					}
				}

				String curLineText = lines.get(cursorLineNumber);
				total = 0;
				for (int i = 0; i < curLineText.length(); i++) {
					total += mc.fontRendererObj.getCharWidth(curLineText.charAt(i));

					if (mouseX - x < total) {
						setCursorChar(MathHelper.clamp_int(i, 0, lines.get(cursorLineNumber).length())); // Char position
						break;
					}
				}

				System.out.println(
					mouseX + " - " + mouseY + " - " + (mouseX - x) + " - " + (mouseY - y) + cursorLineNumber + " - " + cursorCharNumber
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles keyboard input from the user. This can update the text, mouse positions, and handle key combos.
	 * @param typedChar The character that was typed
	 * @param keyCode The LWJGL code for the key that was typed
	 */
	public void textboxKeyTyped(char typedChar, int keyCode) {
		try {
			if (!this.isFocused) {
				return;
			}

			System.out.println(keyCode + " - " + cursorLineNumber + " - " + cursorCharNumber + " - " + lines);

			if (GuiScreen.isKeyComboCtrlA(keyCode)) {
				setCursorLine(lines.size() - 1);
				setCursorChar(lines.get(cursorLineNumber).length());

				selectionLineNumber = 0;
				selectionCharNumber = 0;
			} else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
				GuiScreen.setClipboardString(getSelectedText());
			} else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
				String newText = trimToMaxLength(
					new StringBuilder(lines.get(cursorLineNumber))
						.insert(cursorCharNumber, ChatAllowedCharacters.filterAllowedCharacters(GuiScreen.getClipboardString()))
						.toString()
						.replaceAll("(?i)&(?=[0-9A-FK-OR])", "§")
				);
				lines.set(cursorLineNumber, newText);
				setCursorChar(cursorCharNumber + newText.length());
			} else {
				switch (keyCode) {
					case 14: // Back key
					case 211: // Delete key
						if (hasSelection()) { // Delete the selection
							int startLine = Math.min(cursorLineNumber, selectionLineNumber);
							int endLine = Math.max(cursorLineNumber, selectionLineNumber);
							int charMin = Math.min(cursorCharNumber, selectionCharNumber);
							int charMax = Math.max(cursorCharNumber, selectionCharNumber);

							if (startLine == endLine) { // Selection is on one line
								String newStr = new StringBuilder(lines.get(cursorLineNumber)).delete(charMin, charMax).toString();
								if (newStr.length() > 0) {
									lines.set(cursorLineNumber, newStr);
								} else {
									removeLine(cursorLineNumber);
								}
							} else { // Selection is on multiple lines
								for (int i = endLine; i > startLine - 1; i--) {
									String newStr;
									if (i == startLine) {
										newStr = lines.get(i).substring(0, charMin);
									} else if (i == endLine) {
										newStr = lines.get(i).substring(charMax);
									} else {
										newStr = "";
									}

									if (newStr.length() > 0) {
										lines.set(i, newStr);
									} else {
										removeLine(i);
									}
								}
							}
						} else if (cursorCharNumber == 0) { // Go to previous line
							if (cursorLineNumber > 0) { // Isn't the first line
								String thisLine = lines.get(cursorLineNumber);
								lines.remove(cursorLineNumber);
								setCursorLine(cursorLineNumber - 1);
								setCursorChar(lines.get(cursorLineNumber).length()); // Start at end of the previous line
								lines.set(cursorLineNumber, trimToMaxLength(lines.get(cursorLineNumber) + thisLine)); // Merge the text of this line onto the previous line
							}
						} else { // Delete a char on this line
							StringBuilder thisLine = new StringBuilder(lines.get(cursorLineNumber));

							if (cursorCharNumber - 2 >= 0 && thisLine.charAt(cursorCharNumber - 2) == '§') { // Undo color formatting codes if part of formatting code was deleted
								thisLine.setCharAt(cursorCharNumber - 2, '&');
							}

							lines.set(cursorLineNumber, trimToMaxLength(thisLine.deleteCharAt(cursorCharNumber - 1).toString()));
							setCursorChar(cursorCharNumber - 1);
						}
						break;
					case 203: // Left arrow
						if (cursorCharNumber == 0) { // Go to previous line
							if (cursorLineNumber > 0) {
								setCursorLine(cursorLineNumber - 1);
								setCursorChar(lines.get(cursorLineNumber).length());
							}
						} else { // Go left a char on this line
							setCursorChar(cursorCharNumber - 1);
						}
						break;
					case 205: // Right arrow
						if (cursorCharNumber >= lines.get(cursorLineNumber).length()) {
							if (cursorLineNumber < lines.size() - 1) { // Isn't last line
								setCursorLine(cursorLineNumber + 1);
								setCursorChar(0); // Go to start of the line
							}
						} else { // Go right a char on this line
							setCursorChar(cursorCharNumber + 1);
						}
						break;
					case 28: // Return
					case 156: // Numpad enter
						if (cursorLineNumber < maxLines - 1) { // Adding another line will not be more than max lines
							String thisLine = lines.get(cursorLineNumber);
							lines.set(cursorLineNumber, trimToMaxLength(thisLine.substring(0, cursorCharNumber))); // Move current line text after cursor to next line
							setCursorLine(cursorLineNumber + 1);
							lines.add(cursorLineNumber, trimToMaxLength(thisLine.substring(cursorCharNumber)));
							setCursorChar(0);
						}
						break;
					case 200: // Up arrow
						if (cursorLineNumber > 0) {
							setCursorLine(cursorLineNumber - 1);
							setCursorChar(MathHelper.clamp_int(cursorCharNumber, 0, lines.get(cursorLineNumber).length()));
						}
						break;
					case 208: // Down arrow
						if (cursorLineNumber < lines.size() - 1) { // Isn't last line
							setCursorLine(cursorLineNumber + 1);
							setCursorChar(MathHelper.clamp_int(cursorCharNumber, 0, lines.get(cursorLineNumber).length()));
						}
						break;
					default:
						if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
							StringBuilder thisLine = new StringBuilder(lines.get(cursorLineNumber)).insert(cursorCharNumber, typedChar);
							lines.set(cursorLineNumber, trimToMaxLength(thisLine.toString().replaceAll("(?i)&(?=[0-9A-FK-OR])", "§"))); // Convert &_ to §_
							setCursorChar(cursorCharNumber + 1);
							break;
						}
				}
			}

			updateHeight();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Increment the cursorCount variable that is used to determine when to blink the cursor. Should be called in GuiScreen#updateScreen().
	 */
	public void incrementCursorCount() {
		cursorCount++;
	}

	/**
	 * @return The id of this textbox
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param maxLineLength The max number of characters a line can be
	 */
	public void setMaxLineLength(int maxLineLength) {
		this.maxLineLength = maxLineLength;
	}

	/**
	 * @return The content of all lines where each line is separated by a new line
	 */
	public String getText() {
		return String.join("\n", lines).replaceAll("(?i)§(?=[0-9A-FK-OR])", "&");
	}

	public List<String> getLines() {
		return lines.stream().map(line -> line.replaceAll("(?i)§(?=[0-9A-FK-OR])", "&")).collect(Collectors.toList());
	}

	/**
	 * Sets the lines of the textbox and filters allowed characters
	 * @param text The new lines to set the text to
	 */
	public void setText(String[] text) {
		lines.clear();
		for (String line : text) {
			lines.add(ChatAllowedCharacters.filterAllowedCharacters(line).replaceAll("(?i)&(?=[0-9A-FK-OR])", "§"));
		}

		if (lines.size() == 0) {
			lines.add("");
		}

		updateHeight();
	}

	private void setFocused(boolean focused) {
		if (focused && !isFocused) {
			cursorCount = 0;
		}

		isFocused = focused;
	}

	private String trimToMaxLength(String text) {
		return text.length() > maxLineLength ? text.substring(0, maxLineLength) : text;
	}

	private void setCursorLine(int lineNumber) {
		cursorLineNumber = lineNumber;

		resetSelection();
	}

	private void setCursorChar(int charNumber) {
		cursorCharNumber = charNumber;

		resetSelection();
	}

	private void removeLine(int lineNumber) {
		lines.remove(lineNumber);
		setCursorLine(cursorLineNumber - 1);
		setCursorChar(0);
		if (lines.size() == 0) {
			setCursorLine(0);
			lines.add("");
		}
	}

	private void resetSelection() {
		selectionLineNumber = cursorLineNumber;
		selectionCharNumber = cursorCharNumber;
	}

	private String getSelectedText() {
		int startLine = Math.min(cursorLineNumber, selectionLineNumber);
		int endLine = Math.max(cursorLineNumber, selectionLineNumber);

		if (startLine == endLine) {
			return lines
				.get(cursorLineNumber)
				.substring(Math.min(cursorCharNumber, selectionCharNumber), Math.max(cursorCharNumber, selectionCharNumber));
		} else {
			String output = "";
			for (int i = startLine; i <= endLine; i++) {
				if (i == startLine) {
					output += lines.get(i).substring(Math.min(cursorCharNumber, selectionCharNumber));
				} else if (i == endLine) {
					output += "\n" + lines.get(i).substring(0, Math.max(cursorCharNumber, selectionCharNumber));
				} else {
					output += "\n" + lines.get(i);
				}
			}

			return output;
		}
	}

	private void drawHighlight(int startX, int startY, int endX, int endY) {
		if (startX < endX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		if (startY < endY) {
			int j = startY;
			startY = endY;
			endY = j;
		}

		if (endX > this.x + this.width) {
			endX = this.x + this.width;
		}

		if (startX > this.x + this.width) {
			startX = this.x + this.width;
		}

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(5387);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos(startX, endY, 0.0D).endVertex();
		worldrenderer.pos(endX, endY, 0.0D).endVertex();
		worldrenderer.pos(endX, startY, 0.0D).endVertex();
		worldrenderer.pos(startX, startY, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.disableColorLogic();
		GlStateManager.enableTexture2D();
	}

	private void updateHeight() {
		int extraLines = 0;
		for (String line : lines) {
			extraLines += Math.max(0, mc.fontRendererObj.listFormattedStringToWidth(line, width).size() - 1);
		}
		this.height = (maxLines + extraLines) * lineHeight;
	}
}
