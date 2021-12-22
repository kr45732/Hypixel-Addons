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

import java.util.Arrays;

public class MOTDTextField extends CustomTextField {
    public MOTDTextField(int x, int y, int width, int height, int margin) {
        super(x, y, width, height, margin);
    }

    @Override
    protected String getSelectedText() {
        if (getSelectionStart() >= 0 && getSelectionEnd() >= 0) {
            return text.substring(getSelectionStart(), getSelectionEnd()).replaceAll("(?i)§(?=[0-9A-FK-OR])", "&");
        }

        return "";
    }

    @Override
    public String getText() {
        return text.replaceAll("(?i)§(?=[0-9A-FK-OR])", "&");
    }

    @Override
    protected void insert(String newText) {
        newText = newText.replace("\t", "    ").replace("\r", "").replace("\f","");
        String finalText = text.substring(0, cursorPos) + newText + text.substring(cursorPos);
        if(maxLineLength != -1){
            if(Arrays.stream(finalText.split("\n")).anyMatch(line -> line.length() > maxLineLength)){
                return;
            }
        }
        finalText = finalText.replaceAll("(?i)&(?=[0-9A-FK-OR])", "§");

        deleteSelectedText();
        setText(finalText);
        moveCursorPosBy(newText.length());
    }

    @Override
    protected void deleteNext() {
        String currentText = text;
        if (!atEndOfNote() && !currentText.isEmpty()) {
            StringBuilder sb = new StringBuilder(currentText);
            if (cursorPos >= 1 && sb.charAt(cursorPos - 1) == '§') {
                sb.setCharAt(cursorPos - 1, '&');
            }
            sb.deleteCharAt(cursorPos);
            setText(sb.toString());
            selectionPos--;
        }
    }

    @Override
    protected void deletePrev() {
        String currentText = text;
        if (!atBeginningOfNote() && !currentText.isEmpty()) {
            StringBuilder sb = new StringBuilder(currentText);
            if (cursorPos >= 2 && sb.charAt(cursorPos - 2) == '§') {
                sb.setCharAt(cursorPos - 2, '&');
            }
            sb.deleteCharAt(cursorPos - 1);
            setText(sb.toString());
            moveLeft();
        }
    }
}
