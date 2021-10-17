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

package com.kr45732.hypixeladdons.utils.structs;

public class SkillsStruct {

	private final String name;
	private final int currentLevel;
	private final int maxLevel;
	private final long totalExp;
	private final long expCurrent;
	private final long expForNext;
	private final double progressToNext;

	public SkillsStruct(String name, int currentLevel, int maxLevel, long totalExp, long expCurrent, long expForNext, double progressToNext) {
		this.name = name;
		this.currentLevel = currentLevel;
		this.maxLevel = maxLevel;
		this.totalExp = totalExp;
		this.expCurrent = expCurrent;
		this.expForNext = expForNext;
		this.progressToNext = progressToNext;
	}

	public String getName() {
		return name;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public long getTotalExp() {
		return totalExp;
	}

	public long getExpCurrent() {
		return expCurrent;
	}

	public long getExpForNext() {
		return expForNext;
	}

	public double getProgressToNext() {
		return progressToNext;
	}

	public boolean isMaxed() {
		return currentLevel == maxLevel;
	}

	public double getProgressLevel() {
		return currentLevel + progressToNext;
	}
}
