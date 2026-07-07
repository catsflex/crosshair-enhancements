package me.catsflex.ce.config.option;

import com.google.gson.JsonObject;

public final class FloatOption extends RangedConfigOption<Float> {
	
	public FloatOption(String key, float defaultValue, float min, float max) {
		super(key, defaultValue, min, max);
	}
	
	@Override
	public void read(JsonObject json) {
		if (!json.has(getKey())) return;
		
		set(json.get(getKey()).getAsFloat());
	}
	
	@Override
	public void write(JsonObject json) {
		json.addProperty(getKey(), get());
	}
}
