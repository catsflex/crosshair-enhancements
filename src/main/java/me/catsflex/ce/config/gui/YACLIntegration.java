package me.catsflex.ce.config.gui;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import me.catsflex.ce.config.ModConfig;
import me.catsflex.ce.config.custom.IndicatorVisibilityStatus;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.screens.Screen;

public final class YACLIntegration {
	private YACLIntegration() {}
	
	public static Screen createScreen(Screen parent) {
		final var config = ModConfig.getInstance();
		
		return YetAnotherConfigLib.createBuilder().title(YACLHelper.createTitle())
			
			.category(YACLHelper.createCategory("general")
				
				.group(YACLHelper.createGroup("main")
					.option(YACLHelper.tickBoxOption(config.isEnabled))
					.build())
				
				.group(YACLHelper.createGroup("common")
					.option(YACLHelper.tickBoxOption(config.shouldShowInThirdPerson))
					.option(YACLHelper.tickBoxOption(config.shouldShowInSpectator))
					.option(YACLHelper.tickBoxOption(config.shouldShowWithHiddenHud))
					.option(createDebugCrosshairOption())
					.build())
				
				.group(YACLHelper.createGroup("crosshair")
					.option(YACLHelper.tickBoxOption(config.shouldCrosshairUseBlending))
					.option(YACLHelper.floatSliderOption(config.crosshairOpacity, 0.01F))
					.build())
				
				.group(YACLHelper.createGroup("attackIndicator")
					.option(YACLHelper.tickBoxOption(config.shouldIndicatorUseBlending))
					.option(YACLHelper.floatSliderOption(config.indicatorOpacity, 0.01F))
					.option(createAttackIndicatorOption())
					.option(YACLHelper.enumOption(config.fullIndicatorVisibility, IndicatorVisibilityStatus::getComponent))
					.option(YACLHelper.tickBoxOption(config.shouldShowFullIndicatorForAllItems))
					.option(YACLHelper.tickBoxOption(config.shouldUseResponsiveIndicator))
					.option(YACLHelper.tickBoxOption(config.shouldUseSmoothIndicator))
					.build())
				
				.build())
			
			.save(config::save)
			.build()
			.generateScreen(parent);
	}
	
	private static Option<AttackIndicatorStatus> createAttackIndicatorOption() {
		final var vanillaOptions = Minecraft.getInstance().options;
		
		return YACLHelper.vanillaEnumOption(
			"options.attackIndicator",
			"attackIndicator",
			AttackIndicatorStatus.CROSSHAIR,
			() -> vanillaOptions.attackIndicator().get(),
			v -> {
				vanillaOptions.attackIndicator().set(v);
				vanillaOptions.save();
			},
			AttackIndicatorStatus.class,
			AttackIndicatorStatus::caption
		);
	}
	
	private static Option<DebugScreenEntryStatus> createDebugCrosshairOption() {
		return YACLHelper.debugOverlayOption(
			"debugCrosshair",
			DebugScreenEntryStatus.IN_OVERLAY,
			() -> Minecraft.getInstance().debugEntries.getStatus(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR),
			newValue -> Minecraft.getInstance().debugEntries.setStatus(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR, newValue)
		);
	}
}
