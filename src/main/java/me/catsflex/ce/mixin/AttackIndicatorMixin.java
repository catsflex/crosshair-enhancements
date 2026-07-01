package me.catsflex.ce.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.catsflex.ce.config.ModConfig;
import me.catsflex.ce.util.PlayerUtil;
import me.catsflex.ce.util.RenderUtil;
import me.catsflex.ce.util.WeaponChargeUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Gui.class)
public abstract class AttackIndicatorMixin {
	
	@ModifyExpressionValue(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getCurrentItemAttackStrengthDelay()F")
	)
	private float modifyFullIndicatorRenderCondition(float originalDelay) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get()) return originalDelay;
		
		// A weird Minecraft quirk.
		// The game calculates the current item's attack delay in ticks using this formula: MAX_TPS / ATTACK_SPEED.
		// Then it checks whether the result is > 5.0F to render the full indicator.
		// For non-weapons the delay is: 20 / 4 = 5.
		// If player's attack speed attribute is modified, the game can either show an indicator for non-weapons OR
		// hide an indicator for weapons which is also a bug.
		// Return any value > 5.0F to render the full indicator, otherwise it would be hidden.
		return WeaponChargeUtil.shouldRenderFullIndicator()
			? WeaponChargeUtil.FORCE_RENDER_VALUE
			: WeaponChargeUtil.FORCE_HIDE_VALUE;
	}
	
	@ModifyExpressionValue(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F", ordinal = 0)
	)
	private float applyResponsiveIndicator(float originalCharge) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get() || !config.shouldUseResponsiveIndicator.get()) return originalCharge;
		
		return WeaponChargeUtil.isChargedEnough(originalCharge)
			? WeaponChargeUtil.FULL_CHARGE
			: originalCharge;
	}
	
	@ModifyArg(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F", ordinal = 0)
	)
	private float smoothIndicatorAnimation(float originalDeltaTicks, @Local(argsOnly = true) DeltaTracker deltaTracker) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get() || !config.shouldUseSmoothIndicator.get()) return originalDeltaTicks;
		
		// Force the game to use the relevant partial tick (the fraction of the current tick)
		// instead of the hardcoded 0.0F to achieve perfectly smooth animation.
		// Pass 'true' to keep the animation smooth during tick freeze (via '/tick freeze' command).
		return deltaTracker.getGameTimeDeltaPartialTick(true);
	}
	
	@ModifyVariable(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;guiHeight()I", ordinal = 0),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F")
		)
	)
	private boolean applyFullIndicatorVisibility(boolean originalShouldShowFullIndicator, @Local(ordinal = 0) float charge) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get()) return originalShouldShowFullIndicator;
		
		return switch (config.fullIndicatorVisibility.get()) {
			case NEVER -> false;
			case TARGETED -> originalShouldShowFullIndicator;
			case ALWAYS_ON -> WeaponChargeUtil.isFullyCharged(charge) && WeaponChargeUtil.shouldRenderFullIndicator();
		};
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
		
		if (PlayerUtil.isMining()) return;
		
		RenderUtil.renderSprite(guiGraphics, sprite, x, y, width, height, config.shouldIndicatorUseBlending.get(), config.indicatorOpacity.get());
	}
	
	@WrapOperation(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V", ordinal = 0)
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
		
		if (PlayerUtil.isMining()) return;
		
		RenderUtil.renderSprite(guiGraphics, sprite, sourceWidth, sourceHeight, cropX, cropY, screenX, screenY, renderWidth, renderHeight, config.shouldIndicatorUseBlending.get(), config.indicatorOpacity.get());
	}
}
