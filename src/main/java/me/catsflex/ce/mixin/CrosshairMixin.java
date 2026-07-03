package me.catsflex.ce.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.catsflex.ce.config.ModConfig;
import me.catsflex.ce.util.PlayerUtil;
import me.catsflex.ce.util.RenderUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.class)
public abstract class CrosshairMixin {
	@Unique private static final int _CROSSHAIR_SIZE = 15;
	@Unique private static final float _HALF_CROSSHAIR_SIZE = _CROSSHAIR_SIZE / 2.0F;
	
	@Shadow @Final private static Identifier CROSSHAIR_SPRITE;
	@Shadow @Final private Minecraft minecraft;
	
	@Shadow
	protected abstract void renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
	
	@ModifyExpressionValue(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z")
	)
	private boolean allowCrosshairInThirdPerson(boolean originalIsFirstPerson) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get() || !config.shouldShowInThirdPerson.get()) return originalIsFirstPerson;
		
		return true;
	}
	
	@Inject(
		method = "canRenderCrosshairForSpectator",
		at = @At("HEAD"),
		cancellable = true
	)
	private void allowCrosshairInSpectator(HitResult hitResult, CallbackInfoReturnable<Boolean> cir) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get()) return;
		
		if (!config.shouldShowInSpectator.get() && !PlayerUtil.isLookingAtEntity(hitResult)) return;
		
		cir.setReturnValue(true);
	}
	
	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Gui;renderSleepOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"
		)
	)
	private void allowCrosshairWithHiddenHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get() || !config.shouldShowWithHiddenHud.get()) return;
		
		if (!minecraft.options.hideGui) return;
		
		renderCrosshair(guiGraphics, deltaTracker);
	}
	
	@WrapOperation(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 0)
	)
	private void customCrosshairRenderLogic(GuiGraphics guiGraphics, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get()) {
			original.call(guiGraphics, pipeline, sprite, x, y, width, height);
			return;
		}
		
		guiGraphics.pose().pushMatrix();
		
		correctlyCenterCrosshair(guiGraphics);
		RenderUtil.renderSprite(guiGraphics, CROSSHAIR_SPRITE, 0, 0, _CROSSHAIR_SIZE, _CROSSHAIR_SIZE, config.shouldCrosshairUseBlending.get(), config.crosshairOpacity.get());
		
		guiGraphics.pose().popMatrix();
	}
	
	@Unique
	private static void correctlyCenterCrosshair(GuiGraphics guiGraphics) {
		final float screenCenterX = guiGraphics.guiWidth() / 2.0F;
		final float screenCenterY = guiGraphics.guiHeight() / 2.0F;
		
		final float crosshairMinX = screenCenterX - _HALF_CROSSHAIR_SIZE;
		final float crosshairMinY = screenCenterY - _HALF_CROSSHAIR_SIZE;
		
		guiGraphics.pose().translate(crosshairMinX, crosshairMinY);
	}
}
