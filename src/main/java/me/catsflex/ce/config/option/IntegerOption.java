package me.catsflex.ce.config.option;

import com.google.gson.JsonObject;

public class IntegerOption extends ConfigOption {
	private int _currentValue;
	private final int _defaultValue;
	private final int _min;
	private final int _max;
	
	public IntegerOption(String key, int defaultValue, int min, int max) {
		super(key);
		_currentValue = defaultValue;
		_defaultValue = defaultValue;
		_min = min;
		_max = max;
	}
	
	public int get() {
		return _currentValue;
	}
	
	public void set(int value) {
		_currentValue = Math.clamp(value, _min, _max);
	}
	
	public int getDefault() {
		return _defaultValue;
	}
	
	public int getMin() {
		return _min;
	}
	
	public int getMax() {
		return _max;
	}
	
	@Override
	public void read(JsonObject json) {
		if (!json.has(_key)) return;
		
		set(json.get(_key).getAsInt());
	}
	
	@Override
	public void write(JsonObject json) {
		json.addProperty(_key, _currentValue);
	}
}
