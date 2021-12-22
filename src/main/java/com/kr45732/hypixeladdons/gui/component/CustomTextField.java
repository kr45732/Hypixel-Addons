/*
 * Hypixel Addons - A customizable quality of life mod for Hypixel
 * Copyright (c) 2021 kr45732
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

/*
 * Hypixel Addons - A customizable quality of life mod for Hypixel
 * Copyright (c) 2021 kr45732
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

package com.kr45732.hypixeladdons.gui.component;

import com.kr45732.hypixeladdons.utils.structs.WrappedText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kr45732.hypixeladdons.utils.Utils.*;

@SideOnly(Side.CLIENT)
public class CustomTextField extends Gui {
    private static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int margin;
    private boolean isFocused;
    protected String text;
    private int topVisibleLine;
    private int bottomVisibleLine;
    private final int maxVisibleLines;
    private final int wrapWidth;
    protected int cursorPos;
    private int cursorCounter;
    protected int selectionPos;

    private int maxLines = -1;
    protected int maxLineLength = -1;

    public CustomTextField(int x, int y, int width, int height, int margin) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.margin = margin;

        text = "";
        maxVisibleLines = MathHelper.floor_double((height - (margin * 2.0)) / fontRenderer.FONT_HEIGHT) - 1;
        wrapWidth = width - (margin * 2);
        selectionPos = -1;
    }

    public void setMaxLines(int maxLines){
        this.maxLines = maxLines;
    }

    public void setMaxLineLength(int maxLineLength){
        this.maxLineLength = maxLineLength;
    }

    /**
     * Draw the textbox on the screen. This will draw the background, text, cursor, and scrollbar.
     */
    public void drawTextBox() {
        drawRect(x, y, x + width, y + height, 0xF2181c25);
        renderVisibleText();
        renderCursor();
        renderScrollBar();
    }

    /**
     * Handles mouse clicks. Updates if the textbox is focused and the cursor's position.
     * @param mouseX The x position of where the mouse was clicked
     * @param mouseY The y position of where the mouse was clicked
     * @param mouseButton The mouse button that was clicked
     */
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean isWithinBounds = isWithinBounds(mouseX, mouseY);
        setFocused(isWithinBounds);

        if (isFocused && isWithinBounds) {
            if (mouseButton == 0) {
                int relativeMouseX = mouseX - x - margin;
                int relativeMouseY = mouseY - y - margin;
                int yPos = MathHelper.clamp_int((relativeMouseY / fontRenderer.FONT_HEIGHT) + topVisibleLine, 0, getFinalLineIndex());
                int xPos = fontRenderer.trimStringToWidth(getLine(yPos), relativeMouseX, false).length();

                setCursorPos(countCharacters(yPos) + xPos);
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        boolean isWithinBounds = isWithinBounds(mouseX, mouseY);
        setFocused(isWithinBounds);

        if (isFocused && isWithinBounds) {
            if (state == 0) {
                int relativeMouseX = mouseX - x - margin;
                int relativeMouseY = mouseY - y - margin;
                int yPos = MathHelper.clamp_int((relativeMouseY / fontRenderer.FONT_HEIGHT) + topVisibleLine, 0, getFinalLineIndex());
                int xPos = fontRenderer.trimStringToWidth(getLine(yPos), relativeMouseX, false).length();

                int pos = MathHelper.clamp_int(countCharacters(yPos) + xPos, 0, text.length());
                if (pos != cursorPos) {
                    selectionPos = cursorPos;
                    setCursorPos(pos);
                } else {
                    selectionPos = -1;
                }
            }
        }
    }

    /**
     * Handles mouse being scrolled.
     * @param direction The direction being scrolled. Less than 0 is scrolling down and greater than 0 is scrolling up.
     */
    public void mouseScrolled(double direction) {
        if (direction < 0) {
            incrementVisibleLines();
        } else if (direction > 0) {
            decrementVisibleLines();
        }
    }

    /**
     * Handles keyboard input from the user. This can update the text, mouse's position, and handle key combos.
     * @param typedChar The character that was typed
     * @param keyCode The <a href="https://gist.github.com/Mumfrey/5cfc3b7e14fef91b6fa56470dc05218a">LWJGL code</a> for the key that was typed
     */
    public void textboxKeyTyped(char typedChar, int keyCode) {
        if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(getSelectedText());
        } else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            if (getSelectionDifference() != 0) {
                GuiScreen.setClipboardString(getSelectedText());
                deleteSelectedText();
            }
        } else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            insert(GuiScreen.getClipboardString());
        } else if (isKeyComboCtrlBack(keyCode)) {
            deletePrevWord();
        } else {
            switch (keyCode) {
                case 14:  // Backspace
                    if (getSelectionDifference() != 0) {
                        deleteSelectedText();
                    } else {
                        deletePrev();
                    }
                    break;
                case 211: // Delete
                    if (getSelectionDifference() != 0) {
                        deleteSelectedText();
                    } else {
                        deleteNext();
                    }
                    break;
                case 15:  // Tab
                    insert("    ");
                    break;
                case 156: // Return
                case 28:  // Enter
                    if(maxLines == -1 || text.split("\n").length + 1 <= maxLines) {
                        insert("\n");
                    }
                    break;
                case 199:  // Home
                    updateSelectionPos();
                    setCursorPos(0);
                    break;
                case 207:  // End
                    updateSelectionPos();
                    setCursorPos(text.length());
                    break;
                case 200:  // Up
                    updateSelectionPos();
                    moveUp();
                    break;
                case 208:  // Down
                    updateSelectionPos();
                    moveDown();
                    break;
                case 203:  // Left
                    boolean moveLeft = true;
                    if (GuiScreen.isShiftKeyDown()) {
                        if (selectionPos < 0) {
                            selectionPos = cursorPos;
                        }
                    } else {
                        if (selectionPos > -1) {
                            setCursorPos(getSelectionStart());
                            moveLeft = false;
                        }
                        selectionPos = -1;
                    }

                    if (moveLeft) {
                        moveLeft();
                    }
                    break;
                case 205:  // Right
                    boolean moveRight = true;
                    if (GuiScreen.isShiftKeyDown()) {
                        if (selectionPos < 0) {
                            selectionPos = cursorPos;
                        }
                    } else {
                        if (selectionPos > -1) {
                            setCursorPos(getSelectionEnd());
                            moveRight = false;
                        }
                        selectionPos = -1;
                    }

                    if (moveRight) {
                        moveRight();
                    }
                    break;
                default:
                    if (isFocused()) {
                        if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                            insert(Character.toString(typedChar));
                            updateVisibleLines();
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Increment the cursorCount variable that is used to determine when to blink the cursor. Should be called in {@link  net.minecraft.client.gui.GuiScreen#updateScreen()}.
     */
    public void incrementCursorCount() {
        cursorCounter++;
    }

    public String getText() {
        return text;
    }

    public void setText(String newText) {
        text = newText;
        updateVisibleLines();
    }

    private List<String> toLines() {
        return wrapToWidth(text, wrapWidth);
    }

    private List<WrappedText> toLinesWithIndication() {
        return wrapToWidthWithIndication(text, wrapWidth);
    }

    private String getLine(int line) {
        return line >= 0 && line < toLines().size() ? toLines().get(line) : getFinalLine();
    }

    private String getFinalLine() {
        return getLine(getFinalLineIndex());
    }

    private String getCurrentLine() {
        return getLine(getCursorY());
    }

    private List<String> getVisibleLines() {
        List<String> lines = toLines();
        List<String> visibleLines = new ArrayList<>();
        for (int i = topVisibleLine; i <= bottomVisibleLine; i++) {
            if (i < lines.size()) {
                visibleLines.add(lines.get(i));
            }
        }

        return visibleLines;
    }

    private int getFinalLineIndex() {
        return toLines().size() - 1;
    }

    private boolean cursorIsValid() {
        int yPos = getCursorY();
        return yPos >= topVisibleLine && yPos <= bottomVisibleLine;
    }

    private int getRenderSafeCursorY() {
        return getCursorY() - topVisibleLine;
    }

    private int getCursorWidth() {
        String line = getCurrentLine();
        return fontRenderer.getStringWidth(line.substring(0, MathHelper.clamp_int(getCursorX(), 0, line.length())));
    }

    private boolean isWithinBounds(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    protected boolean atBeginningOfNote() {
        return cursorPos == 0;
    }

    protected boolean atEndOfNote() {
        return cursorPos >= text.length();
    }

    private int getVisibleLineCount() {
        return bottomVisibleLine - topVisibleLine + 1;
    }

    private void updateVisibleLines() {
        while (getVisibleLineCount() <= maxVisibleLines && bottomVisibleLine < getFinalLineIndex()) {
            bottomVisibleLine++;
        }
    }

    private boolean needsScrollBar() {
        return toLines().size() > getVisibleLineCount();
    }

    private boolean isFocused() {
        return isFocused;
    }

    private void setFocused(boolean focused) {
        if (focused && !isFocused) {
            cursorCounter = 0;
        }

        isFocused = focused;
    }

    private boolean isKeyComboCtrlBack(int keyCode) {
        return keyCode == 14 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    protected void insert(String newText) {
        newText = newText.replace("\t", "    ").replace("\r", "").replace("\f","");
        String finalText = text.substring(0, cursorPos) + newText + text.substring(cursorPos);
        if(maxLineLength != -1){
            if(Arrays.stream(finalText.split("\n")).anyMatch(line -> line.length() > maxLineLength)){
                return;
            }
        }

        deleteSelectedText();
        setText(finalText);
        moveCursorPosBy(newText.length());
    }

    protected void deleteNext() {
        String currentText = text;
        if (!atEndOfNote() && !currentText.isEmpty()) {
            StringBuilder sb = new StringBuilder(currentText);
            sb.deleteCharAt(cursorPos);
            setText(sb.toString());
            selectionPos--;
        }
    }

    protected void deletePrev() {
        String currentText = text;
        if (!atBeginningOfNote() && !currentText.isEmpty()) {
            StringBuilder sb = new StringBuilder(currentText);
            sb.deleteCharAt(cursorPos - 1);
            setText(sb.toString());
            moveLeft();
        }
    }

    private void deletePrevWord() {
        if (!atBeginningOfNote()) {
            char prev = text.charAt(cursorPos - 1);
            if (prev == ' ') {
                while (prev == ' ') {
                    deletePrev();
                    if (atBeginningOfNote()) {
                        return;
                    }
                    prev = text.charAt(cursorPos - 1);
                }
            } else {
                while (prev != ' ') {
                    deletePrev();
                    if (atBeginningOfNote()) {
                        return;
                    }
                    prev = text.charAt(cursorPos - 1);
                }
            }
        }
    }

    protected void deleteSelectedText() {
        while (getSelectionDifference() > 0) {
            deletePrev();
        }

        while (getSelectionDifference() < 0) {
            deleteNext();
        }

        selectionPos = -1;
    }

    private void incrementVisibleLines() {
        if (bottomVisibleLine < getFinalLineIndex()) {
            topVisibleLine++;
            bottomVisibleLine++;
        }
    }

    private void decrementVisibleLines() {
        if (topVisibleLine > 0) {
            topVisibleLine--;
            bottomVisibleLine--;
        }
    }

    private int countCharacters(int maxLineIndex) {
        List<WrappedText> wrappedLines = toLinesWithIndication();
        int count = 0;
        for (int i = 0; i < maxLineIndex; i++) {
            WrappedText wrappedLine = wrappedLines.get(i);
            count += wrappedLine.getText().length();
            if (!wrappedLine.isWrapped()) {
                count++;
            }
        }

        return count;
    }

    private int getCursorX(int pos) {
        List<WrappedText> wrappedLines = toLinesWithIndication();
        int yPos = getCursorY();
        boolean currentLineIsWrapped = false;
        int count = 0;
        for (int i = 0; i <= yPos; i++) {
            if (i < wrappedLines.size()) {
                WrappedText wrappedLine = wrappedLines.get(i);
                if (i < yPos) {
                    count += wrappedLine.getText().length();
                    if (!wrappedLine.isWrapped()) {
                        count++;
                    }
                }

                if (wrappedLine.isWrapped()) {
                    if (i == yPos && i > 0) {
                        currentLineIsWrapped = true;
                    }
                }
            }
        }

        if (currentLineIsWrapped) {
            count--;
        }

        return pos - count;
    }

    private int getCursorX() {
        return getCursorX(cursorPos);
    }

    private int getCursorY(int pos) {
        List<WrappedText> wrappedLines = toLinesWithIndication();
        int count = 0;
        for (int i = 0; i < wrappedLines.size(); i++) {
            WrappedText wrappedLine = wrappedLines.get(i);
            count += wrappedLine.getText().length();
            if (!wrappedLine.isWrapped()) {
                count++;
            }

            if (count > pos) {
                return i;
            }
        }

        return getFinalLineIndex();
    }

    private int getCursorY() {
        return getCursorY(cursorPos);
    }

    private int getSelectionDifference() {
        return selectionPos > -1 ? cursorPos - selectionPos : 0;
    }

    private boolean hasSelectionOnLine(int line) {
        if (selectionPos > -1) {
            List<WrappedText> wrappedLines = toLinesWithIndication();
            int count = 0;
            for (int i = 0; i <= line; i++) {
                WrappedText wrappedLine = wrappedLines.get(i);
                for (int j = 0; j < wrappedLine.getText().length(); j++) {
                    count++;
                    if (line == i && isInSelection(count)) {
                        return true;
                    }
                }

                if (!wrappedLine.isWrapped()) {
                    count++;
                }
            }
        }

        return false;
    }

    private void setCursorPos(int pos) {
        cursorPos = MathHelper.clamp_int(pos, 0, text.length());
        if (getCursorY() > bottomVisibleLine) {
            incrementVisibleLines();
        } else if (getCursorY() < topVisibleLine) {
            decrementVisibleLines();
        }
    }

    protected void moveCursorPosBy(int amount) {
        setCursorPos(cursorPos + amount);
    }

    private void moveRight() {
        if (!atEndOfNote()) {
            moveCursorPosBy(1);
        }
    }

    protected void moveLeft() {
        if (!atBeginningOfNote()) {
            moveCursorPosBy(-1);
        }
    }

    private void moveUp() {
        int width = getCursorWidth();
        int yPos = getCursorY();
        while (cursorPos > 0 && (getCursorY() == yPos || getCursorWidth() > width)) {
            moveLeft();
        }
    }

    private void moveDown() {
        int width = getCursorWidth();
        int yPos = getCursorY();
        while (cursorPos < text.length() && (getCursorY() == yPos || getCursorWidth() < width)) {
            moveRight();
        }
    }

    private void updateSelectionPos() {
        if (GuiScreen.isShiftKeyDown()) {
            if (selectionPos < 0) {
                selectionPos = cursorPos;
            }
        } else {
            selectionPos = -1;
        }
    }

    private boolean isInSelection(int pos) {
        if (selectionPos > -1) {
            return pos >= getSelectionStart() && pos <= getSelectionEnd();
        }

        return false;
    }

    protected int getSelectionStart() {
        if (selectionPos > -1) {
            if (selectionPos > cursorPos) {
                return cursorPos;
            } else if (cursorPos > selectionPos) {
                return selectionPos;
            }
        }

        return -1;
    }

    protected int getSelectionEnd() {
        if (selectionPos > -1) {
            if (selectionPos > cursorPos) {
                return selectionPos;
            } else if (cursorPos > selectionPos) {
                return cursorPos;
            }
        }

        return -1;
    }

    protected String getSelectedText() {
        if (getSelectionStart() >= 0 && getSelectionEnd() >= 0) {
            return text.substring(getSelectionStart(), getSelectionEnd());
        }

        return "";
    }

    private void drawSelectionBox(int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int temp = startX;
            startX = endX;
            endX = temp;
        }

        if (startY < endY) {
            int temp = startY;
            startY = endY;
            endY = temp;
        }

        if (endX > x + width) {
            endX = x + width;
        }

        if (startX > x + width) {
            startX = x + width;
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

    private void renderSelectionBox(int y, int renderY, String line) {
        if (hasSelectionOnLine(y)) {
            String absoluteLine = getLine(y);
            int count = 0;
            List<WrappedText> wrappedLines = toLinesWithIndication();
            for (int i = 0; i < y; i++) {
                WrappedText wrappedLine = wrappedLines.get(i);
                count += wrappedLine.getText().length();
                if (!wrappedLine.isWrapped()) {
                    count++;
                }
            }

            if (wrappedLines.get(y).isWrapped()) {
                count--;
            }

            int start = getSelectionStart() - count;
            if (start < 0) {
                start = 0;
            }

            int end = getSelectionEnd() - count;
            if (end > line.length()) {
                end = line.length();
            }

            if (start >= end) {
                selectionPos = -1;
            } else {
                String selection = absoluteLine.substring(start, end);
                int startX = x + margin + fontRenderer.getStringWidth(absoluteLine.substring(0, start));
                int endX = startX + fontRenderer.getStringWidth(selection);
                drawSelectionBox(startX, renderY, endX, renderY + fontRenderer.FONT_HEIGHT);
            }
        }
    }

    private void renderVisibleText() {
        int renderY = y + margin;
        int yPos = topVisibleLine;
        for (String line : getVisibleLines()) {
            fontRenderer.drawStringWithShadow(line, x + margin, renderY, 14737632);
            renderSelectionBox(yPos, renderY, line);

            renderY += fontRenderer.FONT_HEIGHT;
            yPos++;
        }
    }

    private void renderCursor() {
        boolean shouldDisplayCursor = isFocused && cursorCounter / 6 % 2 == 0 && cursorIsValid();
        if (shouldDisplayCursor) {
            String line = getCurrentLine();
            int renderCursorX = x + margin + fontRenderer.getStringWidth(line.substring(0, MathHelper.clamp_int(getCursorX(), 0, line.length())));
            int renderCursorY = y + margin + (getRenderSafeCursorY() * fontRenderer.FONT_HEIGHT);

            drawRect(renderCursorX, renderCursorY - 1, renderCursorX + 1, renderCursorY + fontRenderer.FONT_HEIGHT + 1, -3092272);
        }
    }

    private void renderScrollBar() {
        if (needsScrollBar()) {
            List<String> lines = toLines();
            int effectiveHeight = height - (margin / 2);
            int scrollBarHeight = MathHelper.floor_double(effectiveHeight * ((double) getVisibleLineCount() / lines.size()));
            int scrollBarTop = y + (margin / 4) + MathHelper.floor_double(((double) topVisibleLine / lines.size()) * effectiveHeight);

            int diff = (scrollBarTop + scrollBarHeight) - (y + height);
            if (diff > 0) {
                scrollBarTop -= diff;
            }

            drawRect(x + width - (margin * 3 / 4), scrollBarTop, x + width - (margin / 4), scrollBarTop + scrollBarHeight, -3092272);
        }
    }
}
