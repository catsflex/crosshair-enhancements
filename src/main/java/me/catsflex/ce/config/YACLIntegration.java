package me.catsflex.ce.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import me.catsflex.ce.config.option.BooleanOption;
import me.catsflex.ce.config.option.ColorOption;
import me.catsflex.ce.config.option.FloatOption;
import me.catsflex.ce.config.option.IntegerOption;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class YACLIntegration {
	
	public static Screen createScreen(Screen parent) {
		final var config = ModConfig.getInstance();
		final var vanillaOptions = Minecraft.getInstance().options;
		
		var attackIndicatorOption = createVanillaEnumOption(
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
		
		var debugCrosshairOption = createDebugOverlayOption(
			"debugCrosshair",
			DebugScreenEntryStatus.IN_OVERLAY,
			() -> Minecraft.getInstance().debugEntries.getStatus(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR),
			newValue -> Minecraft.getInstance().debugEntries.setStatus(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR, newValue)
		);
		
		return YetAnotherConfigLib.createBuilder().title(createTitle())
			
			.category(createCategory("general")
				
				.group(createGroup("main")
					.option(createBooleanTickBoxOption(config.isEnabled))
					.build())
				
				.group(createGroup("common")
					.option(createBooleanTickBoxOption(config.shouldShowInThirdPerson))
					.option(createBooleanTickBoxOption(config.shouldShowInSpectator))
					.option(debugCrosshairOption)
					.build())
				
				.group(createGroup("crosshair")
					.option(createBooleanTickBoxOption(config.shouldCrosshairUseBlending))
					.option(createFloatSliderOption(config.crosshairOpacity, 0.01F))
					.build())
				
				.group(createGroup("attack-indicator")
					.option(createBooleanTickBoxOption(config.shouldIndicatorUseBlending))
					.option(createFloatSliderOption(config.indicatorOpacity, 0.01F))
					.option(createBooleanTickBoxOption(config.hasIndicatorForNonWeapons))
					.option(createBooleanTickBoxOption(config.shouldUseResponsiveIndicator))
					.option(attackIndicatorOption)
					.build())
				
				.build())
			
			.save(config::save)
			.build()
			.generateScreen(parent);
	}
	
	private static Option<Integer> createIntegerSliderOption(IntegerOption option, int step) {
		final var key = ConfigKeyType.OPTION.buildKey(option.getKey());
		
		return Option.<Integer>createBuilder()
			.name(Component.translatable(key + ".name"))
			.description(OptionDescription.of(Component.translatable(key + ".description")))
			.binding(option.getDefault(), option::get, option::set)
			.controller(opt -> IntegerSliderControllerBuilder.create(opt)
				.range(option.getMin(), option.getMax())
				.step(step)
			)
			.build();
	}
	
	private static Option<Float> createFloatSliderOption(FloatOption option, float step) {
		final var key = ConfigKeyType.OPTION.buildKey(option.getKey());
		
		return Option.<Float>createBuilder()
			.name(Component.translatable(key + ".name"))
			.description(OptionDescription.of(Component.translatable(key + ".description")))
			.binding(option.getDefault(), option::get, option::set)
			.controller(opt -> FloatSliderControllerBuilder.create(opt)
				.range(option.getMin(), option.getMax())
				.step(step)
			)
			.build();
	}
	
	private static Option<Color> createColorOption(ColorOption option, boolean hasAlpha) {
		final var key = ConfigKeyType.OPTION.buildKey(option.getKey());
		
		return Option.<Color>createBuilder()
			.name(Component.translatable(key + ".name"))
			.description(OptionDescription.of(Component.translatable(key + ".description")))
			.binding(option.getDefault(), option::get, option::set)
			.controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(hasAlpha))
			.build();
	}
	
	private static Option<Boolean> createBooleanTickBoxOption(BooleanOption option) {
		final var key = ConfigKeyType.OPTION.buildKey(option.getKey());
		
		return Option.<Boolean>createBuilder()
			.name(Component.translatable(key + ".name"))
			.description(OptionDescription.of(Component.translatable(key + ".description")))
			.binding(option.getDefault(), option::get, option::set)
			.controller(TickBoxControllerBuilder::create)
			.build();
	}
	
	private static Option<DebugScreenEntryStatus> createDebugOverlayOption(String relativeKey, DebugScreenEntryStatus defaultValue, Supplier<DebugScreenEntryStatus> getter, Consumer<DebugScreenEntryStatus> setter) {
		final var key = ConfigKeyType.DEBUG_OVERLAY_OPTION.buildKey(relativeKey);
		
		return Option.<DebugScreenEntryStatus>createBuilder()
			.name(Component.translatable(key + ".name"))
			.description(OptionDescription.of(Component.translatable(key + ".description")))
			.binding(defaultValue, getter, setter)
			.controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(DebugScreenEntryStatus.class)
				.formatValue(name -> Component.translatable(key + "." + name.getSerializedName()))
			)
			.build();
	}
	
	private static <T extends Enum<T>> Option<T> createVanillaEnumOption(String vanillaNameKey, String relativeKey, T defaultValue, Supplier<T> getter, Consumer<T> setter, Class<T> enumClass, ValueFormatter<T> valueFormatter) {
		final var descriptionKey = ConfigKeyType.VANILLA_OPTION.buildKey(relativeKey) + ".description";
		
		return Option.<T>createBuilder()
			.name(Component.translatable(vanillaNameKey))
			.description(OptionDescription.of(Component.translatable(descriptionKey)))
			.binding(defaultValue, getter, setter)
			.controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(enumClass)
				.formatValue(valueFormatter)
			)
			.build();
	}
	
	private static OptionGroup.Builder createGroup(String groupRelativeKey) {
		return OptionGroup.createBuilder().name(Component.translatable(ConfigKeyType.GROUP.buildKey(groupRelativeKey)));
	}
	
	private static ConfigCategory.Builder createCategory(String categoryRelativeKey) {
		return ConfigCategory.createBuilder().name(Component.translatable(ConfigKeyType.CATEGORY.buildKey(categoryRelativeKey)));
	}
	
	private static Component createTitle() {
		return Component.translatable(ConfigKeyType.getTitleKey());
	}
}
