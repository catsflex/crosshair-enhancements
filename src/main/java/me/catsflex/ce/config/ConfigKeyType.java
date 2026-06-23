package me.catsflex.ce.config;

import me.catsflex.ce.Main;

public enum ConfigKeyType {
	CATEGORY("category"),
	GROUP("group"),
	OPTION("option"),
	DEBUG_OVERLAY_OPTION("debugOverlayOption"),
	VANILLA_OPTION("vanillaOption");
	
	private static final String _PREFIX = "config." + Main.MOD_ID;
	private final String _value;
	
	ConfigKeyType(String value) {
		_value = value;
	}
	
	public String buildKey(String relativeKey) {
		return _PREFIX + "." + _value + "." + relativeKey;
	}
	
	public static String getTitleKey() {
		return _PREFIX + ".title";
	}
}
