package me.catsflex.ce.config.option;

import com.google.gson.JsonObject;

public final class EnumOption<T extends Enum<T>> extends ConfigOption<T> {
	private final Class<T> enumClass;
	
	public EnumOption(String key, T defaultValue, Class<T> enumClass) {
		super(key, defaultValue);
		
		this.enumClass = enumClass;
	}
	
	public Class<T> getEnumClass() {
		return enumClass;
	}
	
	@Override
	public void read(JsonObject json) {
		if (!json.has(getKey())) return;
		
		try {
			set(Enum.valueOf(enumClass, json.get(getKey()).getAsString()));
		} catch (IllegalArgumentException e) {
			// Ignore the incorrect value.
		}
	}
	
	@Override
	public void write(JsonObject json) {
		json.addProperty(getKey(), get().name());
	}
}
