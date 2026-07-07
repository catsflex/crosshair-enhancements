package me.catsflex.ce.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public final class RenderUtil {
	private RenderUtil() {}
	
	public static void renderSprite(
		GuiGraphics guiGraphics, Identifier sprite,
		int screenX, int screenY,
		int renderWidth, int renderHeight,
		boolean shouldUseBlending, float opacity
	) {
		final var pipeline = getPipeline(shouldUseBlending);
		
		guiGraphics.blitSprite(pipeline, sprite, screenX, screenY, renderWidth, renderHeight, opacity);
	}
	
	public static void renderSprite(
		GuiGraphics guiGraphics, Identifier sprite,
		int sourceWidth, int sourceHeight,
		int cropX, int cropY,
		int screenX, int screenY,
		int renderWidth, int renderHeight,
		boolean shouldUseBlending, float opacity
	) {
		final var pipeline = getPipeline(shouldUseBlending);
		
		// Mojang forgot the overload. Get the color manually.
		final int colorWithOpacity = ARGB.white(opacity);
		
		guiGraphics.blitSprite(pipeline, sprite, sourceWidth, sourceHeight, cropX, cropY, screenX, screenY, renderWidth, renderHeight, colorWithOpacity);
	}
	
	private static RenderPipeline getPipeline(boolean shouldUseBlending) {
		return shouldUseBlending
			? RenderPipelines.CROSSHAIR
			: RenderPipelines.GUI_TEXTURED;
	}
}
