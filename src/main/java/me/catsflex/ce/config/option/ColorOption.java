package me.catsflex.ce.config.option;

import com.google.gson.JsonObject;

import java.awt.*;

public final class ColorOption extends ConfigOption<Color> {
	
	// Optimization purposes.
	private int currentARGBValue;
	private final int defaultARGBValue;
	
	public ColorOption(String key, Color defaultValue) {
		super(key, defaultValue);
		
		final int argb = defaultValue.getRGB();
		this.currentARGBValue = argb;
		this.defaultARGBValue = argb;
	}
	
	public int getAsInt() {
		return currentARGBValue;
	}
	
	public int getDefaultAsInt() {
		return defaultARGBValue;
	}
	
	@Override
	public void set(Color value) {
		super.set(value);
		
		currentARGBValue = value.getRGB();
	}
	
	@Override
	public void read(JsonObject json) {
		if (!json.has(getKey())) return;
		
		// Skip the '#' character.
		final var hex = json.get(getKey()).getAsString().substring(1);
		
		try {
			final int argb = Integer.parseUnsignedInt(hex, 16);
			set(new Color(argb, true));
		} catch (NumberFormatException e) {
			// Ignore the incorrect value.
		}
	}
	
	@Override
	public void write(JsonObject json) {
		json.addProperty(getKey(), String.format("#%08X", getAsInt()));
	}
}
