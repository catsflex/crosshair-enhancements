package me.catsflex.ce.config.option;

import com.google.gson.JsonObject;

public class FloatOption extends ConfigOption {
	private float _currentValue;
	private final float _defaultValue;
	private final float _min;
	private final float _max;
	
	public FloatOption(String key, float defaultValue, float min, float max) {
		super(key);
		_currentValue = defaultValue;
		_defaultValue = defaultValue;
		_min = min;
		_max = max;
	}
	
	public float get() {
		return _currentValue;
	}
	
	public void set(float value) {
		_currentValue = Math.clamp(value, _min, _max);
	}
	
	public float getDefault() {
		return _defaultValue;
	}
	
	public float getMin() {
		return _min;
	}
	
	public float getMax() {
		return _max;
	}
	
	@Override
	public void read(JsonObject json) {
		if (!json.has(_key)) return;
		
		set(json.get(_key).getAsFloat());
	}
	
	@Override
	public void write(JsonObject json) {
		json.addProperty(_key, _currentValue);
	}
}
