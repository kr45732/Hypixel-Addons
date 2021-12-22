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

package com.kr45732.hypixeladdons.utils.config;

import static com.kr45732.hypixeladdons.utils.Utils.higherDepth;

import com.google.gson.*;
import com.kr45732.hypixeladdons.HypixelAddons;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

	public static final String baseConfigPath = "config/" + HypixelAddons.MOD_ID + "/";
	private static final String configFilePath = baseConfigPath + "config.json";
	private final Gson gson;
	private JsonObject configuration;

	public Configuration() {
		File baseDir = new File(baseConfigPath);
		if(!baseDir.exists()){
			new File(baseConfigPath).mkdirs();
		}

		gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			configuration = new JsonParser().parse(new FileReader(configFilePath)).getAsJsonObject();
			HypixelAddons.INSTANCE.getLogger().info("Successfully loaded existing configuration");
		} catch (FileNotFoundException e) {
			HypixelAddons.INSTANCE.getLogger().error("Unable to get existing configuration. Creating a new configuration", e);

			try {
				File file = new File(configFilePath);
				file.createNewFile();
				try (FileWriter writer = new FileWriter(file)) {
					writer.write("{}");
					writer.flush();
				}

				configuration = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
			} catch (IOException ex) {
				HypixelAddons.INSTANCE.getLogger().error("Unable to create a new configuration file", ex);
			}
		} catch (IllegalStateException e) {
			HypixelAddons.INSTANCE.getLogger().error("Existing configuration is not formatted properly. Remaking configuration", e);
			try {
				File file = new File(configFilePath);
				file.delete();
				file.createNewFile();
				try (FileWriter writer = new FileWriter(file)) {
					writer.write("{}");
					writer.flush();
				}

				configuration = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
			} catch (IOException ex) {
				HypixelAddons.INSTANCE.getLogger().error("Unable to reset configuration", ex);
			}
		}
	}

	/* Initialize */
	public boolean initialize(String category, String key, boolean defaultValue) {
		if (hasKey(category, key)) {
			return get(category, key, defaultValue);
		} else {
			write(category, key, defaultValue);
			return defaultValue;
		}
	}

	public String initialize(String category, String key, String defaultValue) {
		if (hasKey(category, key)) {
			return get(category, key, defaultValue);
		} else {
			write(category, key, defaultValue);
			return defaultValue;
		}
	}

	public int initialize(String category, String key, int defaultValue) {
		if (hasKey(category, key)) {
			return get(category, key, defaultValue);
		} else {
			write(category, key, defaultValue);
			return defaultValue;
		}
	}

	public double initialize(String category, String key, double defaultValue) {
		if (hasKey(category, key)) {
			return get(category, key, defaultValue);
		} else {
			write(category, key, defaultValue);
			return defaultValue;
		}
	}

	/* Get */
	public boolean get(String category, String key, boolean defaultValue) {
		try {
			if (higherDepth(configuration, category + "." + key) != null) {
				return higherDepth(configuration, category + "." + key).getAsBoolean();
			}
		} catch (Exception e) {
			HypixelAddons.INSTANCE.getLogger().error("Exception when reading a boolean", e);
		}

		return defaultValue;
	}

	public String get(String category, String key, String defaultValue) {
		try {
			if (higherDepth(configuration, category + "." + key) != null) {
				return higherDepth(configuration, category + "." + key).getAsString();
			}
		} catch (Exception e) {
			HypixelAddons.INSTANCE.getLogger().error("Exception when reading a string", e);
		}
		return defaultValue;
	}

	public int get(String category, String key, int defaultValue) {
		try {
			if (higherDepth(configuration, category + "." + key) != null) {
				return higherDepth(configuration, category + "." + key).getAsInt();
			}
		} catch (Exception e) {
			HypixelAddons.INSTANCE.getLogger().error("Exception when reading an integer", e);
		}
		return defaultValue;
	}

	public double get(String category, String key, double defaultValue) {
		try {
			if (higherDepth(configuration, category + "." + key) != null) {
				return higherDepth(configuration, category + "." + key).getAsDouble();
			}
		} catch (Exception e) {
			HypixelAddons.INSTANCE.getLogger().error("Exception when reading a double", e);
		}
		return defaultValue;
	}

	public List<String> get(String category, String key, List<String> defaultValue) {
		try {
			if (higherDepth(configuration, category + "." + key) != null) {
				List<String> output = new ArrayList<>();
				for (JsonElement str : higherDepth(configuration, category + "." + key).getAsJsonArray()) {
					output.add(str.getAsString());
				}
				return output;
			}
		} catch (Exception e) {
			HypixelAddons.INSTANCE.getLogger().error("Exception when reading a string list", e);
		}
		return defaultValue;
	}

	/* Write */
	public void write(String category, String key, boolean value) {
		try {
			JsonObject categoryJson = higherDepth(configuration, category, new JsonObject());
			if (categoryJson.has(key) && categoryJson.get(key).getAsBoolean() == value) {
				return;
			}
			categoryJson.addProperty(key, value);
			configuration.add(category, categoryJson);
			save();
		} catch (Exception e) {
			HypixelAddons.INSTANCE.getLogger().error("Exception when writing a boolean", e);
		}
	}

	public void write(String category, String key, String value) {
		try {
			JsonObject categoryJson = higherDepth(configuration, category, new JsonObject());
			if (categoryJson.has(key) && categoryJson.get(key).getAsString().equals(value)) {
				return;
			}
			categoryJson.addProperty(key, value);
			configuration.add(category, categoryJson);
			save();
		} catch (Exception e) {
			HypixelAddons.INSTANCE.getLogger().error("Exception when writing a string", e);
		}
	}

	public void write(String category, String key, int value) {
		try {
			JsonObject categoryJson = higherDepth(configuration, category, new JsonObject());
			if (categoryJson.has(key) && categoryJson.get(key).getAsInt() == value) {
				return;
			}
			categoryJson.addProperty(key, value);
			configuration.add(category, categoryJson);
			save();
		} catch (Exception e) {
			HypixelAddons.INSTANCE.getLogger().error("Exception when writing an integer", e);
		}
	}

	public void write(String category, String key, double value) {
		try {
			JsonObject categoryJson = higherDepth(configuration, category, new JsonObject());
			if (categoryJson.has(key) && categoryJson.get(key).getAsDouble() == value) {
				return;
			}
			categoryJson.addProperty(key, value);
			configuration.add(category, categoryJson);
			save();
		} catch (Exception e) {
			HypixelAddons.INSTANCE.getLogger().error("Exception when writing a double", e);
		}
	}

	/* Helper methods */
	private synchronized void save() throws FileNotFoundException {
		try (FileWriter writer = new FileWriter(configFilePath)) {
			gson.toJson(configuration, writer);
			writer.flush();
		} catch (IOException e) {
			HypixelAddons.INSTANCE.getLogger().error("Error saving configuration", e);
		}
		configuration = new JsonParser().parse(new FileReader(configFilePath)).getAsJsonObject();
	}

	private boolean hasKey(String category, String key) {
		try {
			return configuration.has(category) && higherDepth(configuration, category + "." + key) != null;
		} catch (Exception e) {
			HypixelAddons.INSTANCE.getLogger().error("Exception when reading configuration", e);
		}
		return false;
	}
}
