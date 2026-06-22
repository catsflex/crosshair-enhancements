package me.catsflex.ce.config.option;

import com.google.gson.JsonObject;
import me.catsflex.ce.config.ModConfig;

public abstract class ConfigOption {
	protected final String _key;
	
	public ConfigOption(String key) {
		_key = key;
		ModConfig.registerOption(this);
	}
	
	public String getKey() {
		return _key;
	}
	
	public abstract void read(JsonObject json);
	
	public abstract void write(JsonObject json);
}
