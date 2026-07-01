package me.catsflex.ce.util;

import me.catsflex.ce.config.ModConfig;
import org.spongepowered.asm.mixin.Unique;

public abstract class WeaponChargeUtil {
	public static final float FORCE_HIDE_VALUE = Float.MIN_VALUE;
	public static final float FORCE_RENDER_VALUE = Float.MAX_VALUE;
	public static final float MIN_CHARGE_THRESHOLD = 0.9F;
	public static final float FULL_CHARGE = 1.0F;
	
	@Unique
	public static boolean isChargedEnough(float charge) {
		
		// Minecraft allows to perform full-charged hits,
		// when the weapon's charge has surpassed 90% (the minimum threshold).
		// A charge of 90% results in 84.8% of actual damage
		// based on the formula (0.2 + f * f * 0.8), where f = 0.9.
		// This fact is not well-covered on the Minecraft Wiki. The more you know.
		return charge > MIN_CHARGE_THRESHOLD;
	}
	
	@Unique
	public static boolean isFullyCharged(float charge) {
		return charge >= FULL_CHARGE;
	}
	
	public static boolean shouldRenderFullIndicator() {
		return PlayerUtil.isHoldingWeapon() || ModConfig.getInstance().shouldShowFullIndicatorForAllItems.get();
	}
}
