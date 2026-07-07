package me.catsflex.ce.config.option;

import com.google.gson.JsonObject;
import me.catsflex.ce.config.ModConfig;

public abstract class ConfigOption<T> {
	private final String key;
	private T currentValue;
	private final T defaultValue;
	
	public ConfigOption(String key, T defaultValue) {
		this.key = key;
		this.currentValue = defaultValue;
		this.defaultValue = defaultValue;
		
		ModConfig.registerOption(this);
	}
	
	public String getKey() {
		return key;
	}
	
	public T get() {
		return currentValue;
	}
	
	public void set(T value) {
		this.currentValue = value;
	}
	
	public T getDefault() {
		return defaultValue;
	}
	
	public abstract void read(JsonObject json);
	
	public abstract void write(JsonObject json);
}
