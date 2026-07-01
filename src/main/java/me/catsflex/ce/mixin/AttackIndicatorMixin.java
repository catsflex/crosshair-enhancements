package me.catsflex.ce.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.catsflex.ce.config.ModConfig;
import me.catsflex.ce.util.RenderUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Gui.class)
public abstract class AttackIndicatorMixin {
	@Unique private static final float _SOMETHING_BIGGER_THAN_5 = Float.MAX_VALUE;
	@Unique private static final float _MIN_CHARGE_THRESHOLD = 0.9F;
	@Unique private static final float _FULL_CHARGE = 1.0F;
	
	@ModifyExpressionValue(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getCurrentItemAttackStrengthDelay()F")
	)
	private float forceIndicatorForNonWeapons(float originalDelay) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get()) return originalDelay;
		
		// A weird Minecraft quirk.
		// The game calculates the current item's attack delay in ticks using this formula: MAX_TPS / ATTACK_SPEED.
		// Then it checks whether the result is > 5.0F to render the full indicator.
		// For non-weapons the delay is: 20 / 4 = 5.
		// If player's attack speed attribute is modified, the game can either show an indicator for non-weapons OR
		// hide an indicator for weapons which is also a bug.
		// Return any value > 5.0F to trick the game.
		if (isHoldingWeapon()) return _SOMETHING_BIGGER_THAN_5;
		
		if (!config.shouldShowFullIndicatorForAllItems.get()) return originalDelay;
		
		return _SOMETHING_BIGGER_THAN_5;
	}
	
	@ModifyExpressionValue(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F", ordinal = 0)
	)
	private float modifyIndicatorThreshold(float originalCharge) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get() || !config.shouldUseResponsiveIndicator.get()) return originalCharge;
		
		return isChargedEnough(originalCharge) ? _FULL_CHARGE : originalCharge;
	}
	
	@ModifyArg(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F", ordinal = 0)
	)
	private float smoothIndicatorCharge(float originalDeltaTicks, @Local(argsOnly = true) DeltaTracker deltaTracker) {
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
	private boolean applyFullIndicatorVisibility(boolean shouldShowFullIndicator, @Local(ordinal = 0) float charge) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get()) return shouldShowFullIndicator;
		
		return switch (config.fullIndicatorVisibility.get()) {
			case NEVER -> false;
			case TARGETED -> shouldShowFullIndicator;
			case ALWAYS_ON -> {
				boolean shouldShowForItem = config.shouldShowFullIndicatorForAllItems.get() || isHoldingWeapon();
				
				// Current charge's value is correctly adjusted when using 'Responsive Indicator' option.
				yield isFullyCharged(charge) && shouldShowForItem;
			}
		};
	}
	
	// A better approach for checking whether the current item is a weapon or not.
	@Unique
	private boolean isHoldingWeapon() {
		final var player = Minecraft.getInstance().player;
		if (player == null) return false;
		
		final var mainHandItem = player.getMainHandItem();
		if (mainHandItem.isEmpty()) return false;
		
		final var modifiers = mainHandItem.get(DataComponents.ATTRIBUTE_MODIFIERS);
		if (modifiers == null) return false;
		
		for (final var modifier : modifiers.modifiers()) {
			final var attribute = modifier.attribute();
			
			// If an item has a modified attribute,
			// it is *probably* a weapon or an item that is supposed to be used as one.
			if (attribute.equals(Attributes.ATTACK_DAMAGE) || attribute.equals(Attributes.ATTACK_SPEED))
				return true;
		}
		
		return false;
	}
	
	@Unique
	private boolean isChargedEnough(float charge) {
		
		// Minecraft allows to perform full-charged hits,
		// when the weapon's charge has surpassed 90% (the minimum threshold).
		// A charge of 90% results in 84.8% of actual damage
		// based on the formula (0.2 + f * f * 0.8), where f = 0.9.
		// This fact is not well-covered on the Minecraft Wiki. The more you know.
		return charge > _MIN_CHARGE_THRESHOLD;
	}
	
	@Unique
	private boolean isFullyCharged(float charge) {
		return charge >= _FULL_CHARGE;
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
		
		if (isMining()) return;
		
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
		
		if (isMining()) return;
		
		RenderUtil.renderSprite(guiGraphics, sprite, sourceWidth, sourceHeight, cropX, cropY, screenX, screenY, renderWidth, renderHeight, config.shouldIndicatorUseBlending.get(), config.indicatorOpacity.get());
	}
	
	@Unique
	private boolean isMining() {
		final var client = Minecraft.getInstance();
		if (client.player == null) return false;
		
		return client.options.keyAttack.isDown() &&
			client.hitResult != null &&
			client.hitResult.getType() == HitResult.Type.BLOCK;
	}
}
