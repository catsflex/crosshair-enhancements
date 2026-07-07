package me.catsflex.ce.config.option;

import com.google.gson.JsonObject;

public final class BooleanOption extends ConfigOption<Boolean> {
	
	public BooleanOption(String key, boolean defaultValue) {
		super(key, defaultValue);
	}
	
	@Override
	public void read(JsonObject json) {
		if (!json.has(getKey())) return;
		
		set(json.get(getKey()).getAsBoolean());
	}
	
	@Override
	public void write(JsonObject json) {
		json.addProperty(getKey(), get());
	}
}
