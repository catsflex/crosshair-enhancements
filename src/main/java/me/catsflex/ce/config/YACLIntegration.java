package me.catsflex.ce.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import me.catsflex.ce.Main;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class YACLIntegration {
	private static final String _PREFIX = "config." + Main.MOD_ID;
	
	public static Screen createScreen(Screen parent) {
		var config = ModConfig.getInstance();
		var vanillaOptions = Minecraft.getInstance().options;
		
		var enabledOption = createBooleanTickBoxOption("enabled", ModConfig.DEFAULT_IS_ENABLED,
			() -> config.isEnabled, v -> config.isEnabled = v);
		
		var thirdPersonOption = createBooleanTickBoxOption("third-person", ModConfig.DEFAULT_SHOULD_SHOW_IN_THIRD_PERSON,
			() -> config.shouldShowInThirdPerson, v -> config.shouldShowInThirdPerson = v);
		var spectatorOption = createBooleanTickBoxOption("spectator", ModConfig.DEFAULT_SHOULD_SHOW_IN_SPECTATOR,
			() -> config.shouldShowInSpectator, v -> config.shouldShowInSpectator = v);
		var debugCrosshairOption = Option.<DebugScreenEntryStatus>createBuilder()
			.name(Component.translatable(getKey(KeyType.OPTION, "debug-crosshair") + ".name"))
			.description(OptionDescription.of(Component.translatable(getKey(KeyType.OPTION, "debug-crosshair") + ".description")))
			.binding(
				DebugScreenEntryStatus.IN_OVERLAY,
				() -> Minecraft.getInstance().debugEntries.getStatus(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR),
				newValue -> Minecraft.getInstance().debugEntries.setStatus(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR, newValue)
			)
			.controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(DebugScreenEntryStatus.class)
				.formatValue(name -> Component.translatable(getKey(KeyType.OPTION, "debug-crosshair") + "." + name.getSerializedName()))
			)
			.build();
		
		var crosshairBlendingOption = createBooleanTickBoxOption("crosshair-blending", ModConfig.DEFAULT_SHOULD_CROSSHAIR_USE_BLENDING,
			() -> config.shouldCrosshairUseBlending, v -> config.shouldCrosshairUseBlending = v);
		var crosshairOpacityOption = createFloatSliderOption("crosshair-opacity", ModConfig.DEFAULT_CROSSHAIR_OPACITY,
			ConfigValidator.LIMIT_OPACITY_MIN, ConfigValidator.LIMIT_OPACITY_MAX, 0.01F,
			() -> config.crosshairOpacity, v -> config.crosshairOpacity = v);
		
		var indicatorBlendingOption = createBooleanTickBoxOption("indicator-blending", ModConfig.DEFAULT_SHOULD_INDICATOR_USE_BLENDING,
			() -> config.shouldIndicatorUseBlending, v -> config.shouldIndicatorUseBlending = v);
		var indicatorOpacityOption = createFloatSliderOption("indicator-opacity", ModConfig.DEFAULT_INDICATOR_OPACITY,
			ConfigValidator.LIMIT_OPACITY_MIN, ConfigValidator.LIMIT_OPACITY_MAX, 0.01F,
			() -> config.indicatorOpacity, v -> config.indicatorOpacity = v);
		var indicatorForNonWeaponsOption = createBooleanTickBoxOption("indicator-for-non-weapons", ModConfig.DEFAULT_HAS_INDICATOR_FOR_NON_WEAPONS,
			() -> config.hasIndicatorForNonWeapons, v -> config.hasIndicatorForNonWeapons = v);
		var responsiveIndicatorOption = createBooleanTickBoxOption("responsive-indicator", ModConfig.DEFAULT_SHOULD_USE_RESPONSIVE_INDICATOR,
			() -> config.shouldUseResponsiveIndicator, v -> config.shouldUseResponsiveIndicator = v);
		var attackIndicatorOption = Option.<AttackIndicatorStatus>createBuilder()
			.name(Component.translatable("options.attackIndicator"))
			.description(OptionDescription.of(Component.translatable("config.crosshair-enhancements.option.attackIndicator.description")))
			.binding(
				AttackIndicatorStatus.CROSSHAIR,
				() -> vanillaOptions.attackIndicator().get(),
				v -> {
					vanillaOptions.attackIndicator().set(v);
					vanillaOptions.save();
				}
			)
			.controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(AttackIndicatorStatus.class)
				.formatValue(AttackIndicatorStatus::caption)
			)
			.build();
		
		return YetAnotherConfigLib.createBuilder().title(createTitle())
			
			.category(createCategory("general")
				
				.group(createGroup("main")
					.option(enabledOption)
					.build())
				
				.group(createGroup("common")
					.option(thirdPersonOption)
					.option(spectatorOption)
					.option(debugCrosshairOption)
					.build())
				
				.group(createGroup("crosshair")
					.option(crosshairBlendingOption)
					.option(crosshairOpacityOption)
					.build())
				
				.group(createGroup("attack-indicator")
					.option(indicatorBlendingOption)
					.option(indicatorOpacityOption)
					.option(indicatorForNonWeaponsOption)
					.option(responsiveIndicatorOption)
					.option(attackIndicatorOption)
					.build())
				
				.build())
			
			.save(config::save)
			.build()
			.generateScreen(parent);
	}
	
	private static Option<Float> createFloatSliderOption(String relativeKey, float defaultValue, float minValue, float maxValue, float step, Supplier<Float> getter, Consumer<Float> setter) {
		var key = getKey(KeyType.OPTION, relativeKey);
		
		return Option.<Float>createBuilder()
			.name(Component.translatable(key + ".name"))
			.description(OptionDescription.of(Component.translatable(key + ".description")))
			.binding(defaultValue, getter, setter)
			.controller(opt -> FloatSliderControllerBuilder.create(opt)
				.range(minValue, maxValue)
				.step(step)
				.formatValue(v -> Component.literal(Math.round(v * 100.0F) + "%"))
			)
			.build();
	}
	
	private static Option<Boolean> createBooleanTickBoxOption(String relativeKey, boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		var key = getKey(KeyType.OPTION, relativeKey);
		
		return Option.<Boolean>createBuilder()
			.name(Component.translatable(key + ".name"))
			.description(OptionDescription.of(Component.translatable(key + ".description")))
			.binding(defaultValue, getter, setter)
			.controller(TickBoxControllerBuilder::create)
			.build();
	}
	
	private static OptionGroup.Builder createGroup(String groupRelativeKey) {
		return OptionGroup.createBuilder().name(Component.translatable(getKey(KeyType.GROUP, groupRelativeKey)));
	}
	
	private static ConfigCategory.Builder createCategory(String categoryRelativeKey) {
		return ConfigCategory.createBuilder().name(Component.translatable(getKey(KeyType.CATEGORY, categoryRelativeKey)));
	}
	
	private static Component createTitle() {
		return Component.translatable(_PREFIX + ".title");
	}
	
	private static String getKey(KeyType type, String relativeKey) {
		return _PREFIX + "." + type.getValue() + "." + relativeKey;
	}
}
