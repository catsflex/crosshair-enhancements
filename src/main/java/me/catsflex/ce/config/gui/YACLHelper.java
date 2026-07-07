package me.catsflex.ce.config.gui;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.*;
import me.catsflex.ce.config.option.*;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class YACLHelper {
	private YACLHelper() {}
	
	private static final BooleanSupplier ALWAYS_AVAILABLE = () -> true;
	
	public static void dependAvailabilityOn(Option<?> target, Option<Boolean> condition) {
		target.setAvailable(condition.pendingValue());
		
		// Add an event listener to update the target option's availability in real time.
		condition.addEventListener((option, value) -> {
			target.setAvailable(option.pendingValue());
		});
	}
	
	// Base settings for all options.
	private static <T> Option.Builder<T> createBase(ConfigKeyType type, ConfigOption<T> option, BooleanSupplier isAvailable) {
		return Option.<T>createBuilder()
			.name(Component.translatable(type.buildNameKey(option.getKey())))
			.description(OptionDescription.of(Component.translatable(type.buildDescriptionKey(option.getKey()))))
			.binding(option.getDefault(), option::get, option::set)
			.available(isAvailable.getAsBoolean());
	}
	
	private static <T> Option.Builder<T> createBase(ConfigKeyType type, ConfigOption<T> option) {
		return createBase(type, option, ALWAYS_AVAILABLE);
	}
	
	// Tick box option (for booleans).
	public static Option<Boolean> tickBoxOption(BooleanOption option, BooleanSupplier isAvailable) {
		return YACLHelper.createBase(ConfigKeyType.OPTION, option, isAvailable)
			.controller(TickBoxControllerBuilder::create)
			.build();
	}
	
	public static Option<Boolean> tickBoxOption(BooleanOption option) {
		return tickBoxOption(option, ALWAYS_AVAILABLE);
	}
	
	// Color picker option.
	public static Option<Color> colorPickerOption(ColorOption option, boolean hasAlpha, BooleanSupplier isAvailable) {
		return YACLHelper.createBase(ConfigKeyType.OPTION, option, isAvailable)
			.controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(hasAlpha))
			.build();
	}
	
	public static Option<Color> colorPickerOption(ColorOption option, boolean hasAlpha) {
		return colorPickerOption(option, hasAlpha, ALWAYS_AVAILABLE);
	}
	
	// Integer slider option.
	public static Option<Integer> integerSliderOption(IntegerOption option, int step, BooleanSupplier isAvailable) {
		return YACLHelper.createBase(ConfigKeyType.OPTION, option, isAvailable)
			.controller(opt -> IntegerSliderControllerBuilder.create(opt)
				.range(option.getMin(), option.getMax())
				.step(step)
			)
			.build();
	}
	
	public static Option<Integer> integerSliderOption(IntegerOption option, int step) {
		return integerSliderOption(option, step, ALWAYS_AVAILABLE);
	}
	
	// Float slider option.
	public static Option<Float> floatSliderOption(FloatOption option, float step, BooleanSupplier isAvailable) {
		return YACLHelper.createBase(ConfigKeyType.OPTION, option, isAvailable)
			.controller(opt -> FloatSliderControllerBuilder.create(opt)
				.range(option.getMin(), option.getMax())
				.step(step)
			)
			.build();
	}
	
	public static Option<Float> floatSliderOption(FloatOption option, float step) {
		return floatSliderOption(option, step, ALWAYS_AVAILABLE);
	}
	
	// Enum option.
	public static <T extends Enum<T>> Option<T> enumOption(EnumOption<T> option, ValueFormatter<T> formatter, BooleanSupplier isAvailable) {
		return YACLHelper.createBase(ConfigKeyType.OPTION, option, isAvailable)
			.controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(option.getEnumClass())
				.formatValue(formatter)
			)
			.build();
	}
	
	public static <T extends Enum<T>> Option<T> enumOption(EnumOption<T> option, ValueFormatter<T> formatter) {
		return enumOption(option, formatter, ALWAYS_AVAILABLE);
	}
	
	// Vanilla enum base settings for vanilla bindings.
	private static <T extends Enum<T>> Option<T> createVanillaEnumBase(
		String fullKey,
		String fullDescKey,
		T defaultValue,
		Supplier<T> getter,
		Consumer<T> setter,
		Class<T> enumClass,
		ValueFormatter<T> formatter,
		BooleanSupplier isAvailable
	) {
		return Option.<T>createBuilder()
			.name(Component.translatable(fullKey))
			.description(OptionDescription.of(Component.translatable(fullDescKey)))
			.binding(defaultValue, getter, setter)
			.available(isAvailable.getAsBoolean())
			.controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(enumClass)
				.formatValue(formatter)
			)
			.build();
	}
	
	// Vanilla enum option.
	public static <T extends Enum<T>> Option<T> vanillaEnumOption(
		String vanillaNameKey,
		String relativeKey,
		T defaultValue,
		Supplier<T> getter,
		Consumer<T> setter,
		Class<T> enumClass,
		ValueFormatter<T> formatter
	) {
		final var descriptionKey = ConfigKeyType.VANILLA_OPTION.buildDescriptionKey(relativeKey);
		
		return createVanillaEnumBase(vanillaNameKey, descriptionKey, defaultValue, getter, setter, enumClass, formatter, ALWAYS_AVAILABLE);
	}
	
	// Debug overlay option.
	public static Option<DebugScreenEntryStatus> debugOverlayOption(
		String relativeKey,
		DebugScreenEntryStatus defaultValue,
		Supplier<DebugScreenEntryStatus> getter,
		Consumer<DebugScreenEntryStatus> setter
	) {
		return createVanillaEnumBase(
			ConfigKeyType.DEBUG_OVERLAY_OPTION.buildNameKey(relativeKey),
			ConfigKeyType.DEBUG_OVERLAY_OPTION.buildDescriptionKey(relativeKey),
			defaultValue,
			getter,
			setter,
			DebugScreenEntryStatus.class,
			status -> Component.translatable(ConfigKeyType.getEnumKey(status)),
			ALWAYS_AVAILABLE
		);
	}
	
	public static Component createTitle() {
		return Component.translatable(ConfigKeyType.getTitleKey());
	}
	
	public static ConfigCategory.Builder createCategory(String categoryRelativeKey) {
		return ConfigCategory.createBuilder().name(Component.translatable(ConfigKeyType.CATEGORY.buildKey(categoryRelativeKey)));
	}
	
	public static OptionGroup.Builder createGroup(String groupRelativeKey) {
		return OptionGroup.createBuilder().name(Component.translatable(ConfigKeyType.GROUP.buildKey(groupRelativeKey)));
	}
}
