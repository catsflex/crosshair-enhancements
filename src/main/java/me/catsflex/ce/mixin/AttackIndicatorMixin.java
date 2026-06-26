package me.catsflex.ce.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.catsflex.ce.config.ModConfig;
import me.catsflex.ce.util.RenderUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Gui.class)
public abstract class AttackIndicatorMixin {
	@Unique
	private static final float _MIN_CHARGE_THRESHOLD = 0.9F;
	@Unique
	private static final float _FULL_CHARGE = 1.0F;
	
	@ModifyExpressionValue(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getCurrentItemAttackStrengthDelay()F")
	)
	private float forceIndicatorForNonWeapons(float originalDelay) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get() || !config.hasIndicatorForNonWeapons.get()) return originalDelay;
		
		// A weird Minecraft quirk.
		// The game calculates the current item's attack delay in ticks using this formula: MAX_TPS / ATTACK_SPEED.
		// Then it checks whether the result is > 5.0F to render the full indicator.
		// For non-weapons the delay is: 20 / 4 = 5.
		// If player's attack speed attribute is modified, the game can either show an indicator for non-weapons OR
		// hide an indicator for weapons which is also a bug.
		// Return any value > 5.0F to trick the game.
		return Float.MAX_VALUE;
	}
	
	@ModifyExpressionValue(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F", ordinal = 0)
	)
	private float modifyIndicatorThreshold(float originalCharge) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get() || !config.shouldUseResponsiveIndicator.get()) return originalCharge;
		
		// Minecraft allows to perform full-charged hits,
		// when the weapon's charge has surpassed 90% (the minimum threshold).
		// A charge of 90% results in 84.8% of actual damage
		// based on the formula (0.2 + f * f * 0.8), where f = 0.9.
		// This fact is not well-covered on the Minecraft Wiki. The more you know.
		return originalCharge > _MIN_CHARGE_THRESHOLD ? _FULL_CHARGE : originalCharge;
	}
	
	@ModifyArg(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F", ordinal = 0)
	)
	private float smoothIndicatorCharge(float originalDeltaTicks, @Local(argsOnly = true) DeltaTracker deltaTracker) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get() || !config.shouldUseSmoothIndicator.get()) return originalDeltaTicks;
		
		// Force the game to use relevant between-tick time (a.k.a. delta/partial ticks) instead of hardcoded 0.0F.
		return deltaTracker.getGameTimeDeltaTicks();
	}
	
	// Renders both full indicator & indicator background.
	@WrapOperation(
		method = "renderCrosshair",
		at = {
			@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 1),
			@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 2)
		}
	)
	private void customIndicatorIcons(GuiGraphics guiGraphics, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get()) {
			original.call(guiGraphics, pipeline, sprite, x, y, width, height);
			return;
		}
		
		RenderUtil.renderSprite(guiGraphics, sprite, x, y, width, height, config.shouldIndicatorUseBlending.get(), config.indicatorOpacity.get());
	}
	
	@WrapOperation(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V")
	)
	private void customIndicatorProgress(
		GuiGraphics guiGraphics, RenderPipeline pipeline, Identifier sprite,
		int sourceWidth, int sourceHeight,
		int cropX, int cropY,
		int screenX, int screenY,
		int renderWidth, int renderHeight,
		Operation<Void> original
	) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get()) {
			original.call(guiGraphics, pipeline, sprite, sourceWidth, sourceHeight, cropX, cropY, screenX, screenY, renderWidth, renderHeight);
			return;
		}
		
		RenderUtil.renderSprite(guiGraphics, sprite, sourceWidth, sourceHeight, cropX, cropY, screenX, screenY, renderWidth, renderHeight, config.shouldIndicatorUseBlending.get(), config.indicatorOpacity.get());
	}
}
