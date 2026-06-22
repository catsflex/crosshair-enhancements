package me.catsflex.ce.config.option;

import com.google.gson.JsonObject;

import java.awt.*;

public class ColorOption extends ConfigOption {
	private Color _currentValue;
	private final Color _defaultValue;
	
	public ColorOption(String key, Color defaultValue) {
		super(key);
		_currentValue = defaultValue;
		_defaultValue = defaultValue;
	}
	
	public Color get() {
		return _currentValue;
	}
	
	public void set(Color value) {
		_currentValue = value;
	}
	
	public Color getDefault() {
		return _defaultValue;
	}
	
	@Override
	public void read(JsonObject json) {
		if (!json.has(_key)) return;
		
		// Skip the '#' character.
		var hex = json.get(_key).getAsString().substring(1);
		
		try {
			int argb = Integer.parseUnsignedInt(hex, 16);
			set(new Color(argb, true));
		} catch (NumberFormatException e) {
			// Ignore the incorrect value.
		}
	}
	
	@Override
	public void write(JsonObject json) {
		json.addProperty(_key, String.format("#%08X", _currentValue.getRGB()));
	}
}
