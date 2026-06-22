package me.catsflex.ce.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.catsflex.ce.config.ModConfig;
import me.catsflex.ce.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class CrosshairMixin {
	@Unique private static final int _CROSSHAIR_SIZE = 15;
	@Unique private static final float _HALF_CROSSHAIR_SIZE = _CROSSHAIR_SIZE / 2.0F;
	
	@Shadow @Final private static Identifier CROSSHAIR_SPRITE;
	
	@ModifyExpressionValue(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z")
	)
	private boolean bypassFirstPersonCheck(boolean isFirstPerson) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled) return isFirstPerson;
		
		// Make the game think we're ALWAYS in first person mode & render the crosshair.
		return isFirstPerson || config.shouldShowInThirdPerson;
	}
	
	@ModifyReturnValue(
		method = "canRenderCrosshairForSpectator",
		at = @At("RETURN")
	)
	private boolean bypassSpectatorCheck(boolean canRender) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled) return canRender;
		
		// Make the game think it should ALWAYS render the crosshair in spectator game mode.
		return canRender || config.shouldShowInSpectator;
	}
	
	@WrapOperation(
		method = "renderCrosshair",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 0)
	)
	private void customCrosshairRenderLogic(GuiGraphics guiGraphics, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled) {
			original.call(guiGraphics, pipeline, sprite, x, y, width, height);
			return;
		}
		
		guiGraphics.nextStratum();
		guiGraphics.pose().pushMatrix();
		
		correctlyCenterCrosshair(guiGraphics);
		RenderUtil.renderSprite(guiGraphics, CROSSHAIR_SPRITE, 0, 0, _CROSSHAIR_SIZE, _CROSSHAIR_SIZE, config.shouldCrosshairUseBlending, config.crosshairOpacity);
		
		guiGraphics.pose().popMatrix();
	}
	
	@Unique
	private void correctlyCenterCrosshair(GuiGraphics guiGraphics) {
		final float screenCenterX = guiGraphics.guiWidth() / 2.0F;
		final float screenCenterY = guiGraphics.guiHeight() / 2.0F;
		
		final float crosshairMinX = screenCenterX - _HALF_CROSSHAIR_SIZE;
		final float crosshairMinY = screenCenterY - _HALF_CROSSHAIR_SIZE;
		
		guiGraphics.pose().translate(crosshairMinX, crosshairMinY);
	}
}
