package me.catsflex.ce.config;

public class ConfigValidator {
	
	public static final float LIMIT_OPACITY_MIN = 0.0F;
	public static final float LIMIT_OPACITY_MAX = 1.0F;
	
	public static boolean validate(ModConfig config) {
		boolean wasModified = false;
		
		wasModified |= validateCrosshairOpacity(config);
		wasModified |= validateIndicatorOpacity(config);
		
		return wasModified;
	}
	
	private static boolean validateCrosshairOpacity(ModConfig config) {
		float clamped = Math.clamp(config.crosshairOpacity, LIMIT_OPACITY_MIN, LIMIT_OPACITY_MAX);
		if (config.crosshairOpacity == clamped) return false;
		
		config.crosshairOpacity = clamped;
		return true;
	}
	
	private static boolean validateIndicatorOpacity(ModConfig config) {
		float clamped = Math.clamp(config.indicatorOpacity, LIMIT_OPACITY_MIN, LIMIT_OPACITY_MAX);
		if (config.indicatorOpacity == clamped) return false;
		
		config.indicatorOpacity = clamped;
		return true;
	}
}
