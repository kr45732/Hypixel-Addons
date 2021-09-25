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

package com.kr45732.hypixeladdons.utils.structs;

public class UsernameUuidStruct {

	public String playerUsername;
	public String playerUuid;
	public String failCause;

	public UsernameUuidStruct(String playerUsername, String playerUuid) {
		this.playerUsername = playerUsername;
		this.playerUuid = playerUuid;
	}

	public UsernameUuidStruct(String failCause) {
		this.failCause = failCause;
	}

	public UsernameUuidStruct() {
		this.failCause = "Unknown Fail Cause";
	}

	public boolean isNotValid() {
		return playerUsername == null || playerUuid == null;
	}

	@Override
	public String toString() {
		return "UsernameUuidStruct{" + "username='" + playerUsername + '\'' + ", uuid='" + playerUuid + '\'' + '}';
	}
}
