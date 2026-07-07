package me.catsflex.ce.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.catsflex.ce.Main;
import me.catsflex.ce.config.custom.IndicatorVisibilityStatus;
import me.catsflex.ce.config.option.BooleanOption;
import me.catsflex.ce.config.option.ConfigOption;
import me.catsflex.ce.config.option.EnumOption;
import me.catsflex.ce.config.option.FloatOption;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ModConfig {
	
	// Config saving stuff.
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String CONFIG_NAME = Main.MOD_ID + ".json";
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME);
	private static final List<ConfigOption<?>> OPTIONS = new ArrayList<>();
	private static final ModConfig INSTANCE = new ModConfig();
	
	public final BooleanOption isEnabled = new BooleanOption("isEnabled", true);
	public final BooleanOption shouldShowInThirdPerson = new BooleanOption("shouldShowInThirdPerson", false);
	public final BooleanOption shouldShowInSpectator = new BooleanOption("shouldShowInSpectator", false);
	public final BooleanOption shouldShowWithHiddenHud = new BooleanOption("shouldShowWithHiddenHud", false);
	public final BooleanOption shouldCrosshairUseBlending = new BooleanOption("shouldCrosshairUseBlending", true);
	public final FloatOption crosshairOpacity = new FloatOption("crosshairOpacity", 1.0F, 0.0F, 1.0F);
	public final BooleanOption shouldIndicatorUseBlending = new BooleanOption("shouldIndicatorUseBlending", true);
	public final FloatOption indicatorOpacity = new FloatOption("indicatorOpacity", 1.0F, 0.0F, 1.0F);
	public final EnumOption<IndicatorVisibilityStatus> fullIndicatorVisibility = new EnumOption<>("fullIndicatorVisibility", IndicatorVisibilityStatus.TARGETED, IndicatorVisibilityStatus.class);
	public final BooleanOption shouldShowFullIndicatorForAllItems = new BooleanOption("shouldShowFullIndicatorForAllItems", false);
	public final BooleanOption shouldUseResponsiveIndicator = new BooleanOption("shouldUseResponsiveIndicator", false);
	public final BooleanOption shouldUseSmoothIndicator = new BooleanOption("shouldUseSmoothIndicator", false);
	
	private ModConfig() {}
	
	public static ModConfig getInstance() {
		return INSTANCE;
	}
	
	public static void registerOption(ConfigOption<?> option) {
		OPTIONS.add(option);
	}
	
	public void load() {
		if (!Files.exists(CONFIG_PATH)) {
			save();
			return;
		}
		
		try (var reader = Files.newBufferedReader(CONFIG_PATH)) {
			var element = JsonParser.parseReader(reader);
			if (!element.isJsonObject()) {
				throw new IllegalStateException("Config root is not a JSON object!");
			}
			
			var json = element.getAsJsonObject();
			for (var option : OPTIONS) {
				option.read(json);
			}
			
		} catch (Exception e) {
			Main.LOGGER.warn("Failed to load config, using defaults!", e);
			save();
		}
	}
	
	public void save() {
		var json = new JsonObject();
		
		for (var option : OPTIONS) {
			option.write(json);
		}
		
		try (var writer = Files.newBufferedWriter(CONFIG_PATH)) {
			GSON.toJson(json, writer);
		} catch (Exception e) {
			Main.LOGGER.warn("Failed to save config!", e);
		}
	}
}
