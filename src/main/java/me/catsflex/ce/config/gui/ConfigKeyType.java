package me.catsflex.ce.config.gui;

import me.catsflex.ce.Main;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.util.StringRepresentable;

public enum ConfigKeyType {
	CATEGORY("category"),
	GROUP("group"),
	OPTION("option"),
	DEBUG_OVERLAY_OPTION("debugOverlayOption"),
	VANILLA_OPTION("vanillaOption");
	
	private static final String PREFIX = "config." + Main.MOD_ID;
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String TITLE = "title";
	private static final String ENUM = "enum";
	private final String type;
	
	ConfigKeyType(String type) {
		this.type = type;
	}
	
	public String buildKey(String relativeKey) {
		return PREFIX + "." + type + "." + relativeKey;
	}
	
	public String buildNameKey(String relativeKey) {
		return buildKey(relativeKey) + "." + NAME;
	}
	
	public String buildDescriptionKey(String relativeKey) {
		return buildKey(relativeKey) + "." + DESCRIPTION;
	}
	
	public static String getTitleKey() {
		return PREFIX + "." + TITLE;
	}
	
	public static <T extends Enum<T>> String getEnumKey(T value) {
		var enumClassName = getUnobfuscatedEnumName(value);
		var valueName = (value instanceof StringRepresentable sr)
			? sr.getSerializedName()
			: value.name();
		
		return PREFIX + "." + ENUM + "." + enumClassName + "." + valueName;
	}
	
	private static <T extends Enum<T>> String getUnobfuscatedEnumName(T value) {
		if (value instanceof DebugScreenEntryStatus) return "DebugScreenEntryStatus";
		
		return value.getDeclaringClass().getSimpleName();
	}
}
