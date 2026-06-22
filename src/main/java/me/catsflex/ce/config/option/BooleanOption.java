package me.catsflex.ce.config.option;

import com.google.gson.JsonObject;

public class BooleanOption extends ConfigOption {
	private boolean _currentValue;
	private final boolean _defaultValue;
	
	public BooleanOption(String key, boolean defaultValue) {
		super(key);
		_currentValue = defaultValue;
		_defaultValue = defaultValue;
	}
	
	public boolean get() {
		return _currentValue;
	}
	
	public void set(boolean value) {
		_currentValue = value;
	}
	
	public boolean getDefault() {
		return _defaultValue;
	}
	
	@Override
	public void read(JsonObject json) {
		if (!json.has(_key)) return;
		
		set(json.get(_key).getAsBoolean());
	}
	
	@Override
	public void write(JsonObject json) {
		json.addProperty(_key, _currentValue);
	}
}
