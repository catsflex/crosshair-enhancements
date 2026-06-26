package me.catsflex.ce.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.catsflex.ce.config.ModConfig;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	
	@ModifyExpressionValue(
		method = "renderLevel",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z")
	)
	private boolean bypassFirstPersonCheck(boolean isFirstPerson) {
		final var config = ModConfig.getInstance();
		if (!config.isEnabled.get()) return isFirstPerson;
		
		// Same logic as during crosshair rendering.
		return isFirstPerson || config.shouldShowInThirdPerson.get();
	}
}
