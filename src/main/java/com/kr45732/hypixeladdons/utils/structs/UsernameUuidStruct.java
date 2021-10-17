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

public class UsernameUuidStruct {

	private String username;
	private String uuid;
	private String failCause;

	public UsernameUuidStruct(String username, String uuid) {
		this.username = username;
		this.uuid = uuid;
	}

	public UsernameUuidStruct(String failCause) {
		this.failCause = failCause;
	}

	public UsernameUuidStruct() {
		this.failCause = "Unknown fail cause";
	}

	public String getUsername() {
		return username;
	}

	public String getUuid() {
		return uuid;
	}

	public String getFailCause() {
		return failCause;
	}

	public boolean isNotValid() {
		return username == null || uuid == null;
	}

	@Override
	public String toString() {
		return "UsernameUuidStruct{" + "username='" + username + '\'' + ", uuid='" + uuid + '\'' + '}';
	}
}
