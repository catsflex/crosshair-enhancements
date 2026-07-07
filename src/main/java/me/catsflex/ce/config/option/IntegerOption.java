package me.catsflex.ce.config.option;

import com.google.gson.JsonObject;

public final class IntegerOption extends RangedConfigOption<Integer> {
	
	public IntegerOption(String key, int defaultValue, int min, int max) {
		super(key, defaultValue, min, max);
	}
	
	@Override
	public void read(JsonObject json) {
		if (!json.has(getKey())) return;
		
		set(json.get(getKey()).getAsInt());
	}
	
	@Override
	public void write(JsonObject json) {
		json.addProperty(getKey(), get());
	}
}
