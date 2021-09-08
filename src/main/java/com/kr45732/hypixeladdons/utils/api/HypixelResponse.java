/*
 * Hypixel Addons - A quality of life mod for Hypixel
 * Copyright (c) 2021 kr45732
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kr45732.hypixeladdons.utils.api;

import static com.kr45732.hypixeladdons.utils.Utils.higherDepth;

import com.google.gson.JsonElement;

public class HypixelResponse {

	public JsonElement response;
	public String failCause;

	public HypixelResponse(JsonElement response) {
		this.response = response;
	}

	public HypixelResponse(String failCase) {
		this.failCause = failCase;
	}

	public HypixelResponse() {
		this.failCause = "Unknown Fail Cause";
	}

	public boolean isNotValid() {
		return response == null;
	}

	public JsonElement get(String path) {
		if (isNotValid()) {
			return null;
		}

		return higherDepth(response, path);
	}
}
