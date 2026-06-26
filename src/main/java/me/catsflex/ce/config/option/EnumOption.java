package me.catsflex.ce.config.option;

import com.google.gson.JsonObject;

public class EnumOption<T extends Enum<T>> extends ConfigOption {
	private T _currentValue;
	private final T _defaultValue;
	private final Class<T> _enumClass;
	
	public EnumOption(String key, T defaultValue, Class<T> enumClass) {
		super(key);
		_currentValue = defaultValue;
		_defaultValue = defaultValue;
		_enumClass = enumClass;
	}
	
	public T get() {
		return _currentValue;
	}
	
	public void set(T value) {
		_currentValue = value;
	}
	
	public T getDefault() {
		return _defaultValue;
	}
	
	public Class<T> getEnumClass() {
		return _enumClass;
	}
	
	@Override
	public void read(JsonObject json) {
		if (!json.has(_key)) return;
		
		try {
			set(Enum.valueOf(_enumClass, json.get(_key).getAsString()));
		} catch (IllegalArgumentException e) {
			// Ignore the incorrect value.
		}
	}
	
	@Override
	public void write(JsonObject json) {
		json.addProperty(_key, _currentValue.name());
	}
}
