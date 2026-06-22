package me.catsflex.ce.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.catsflex.ce.Main;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
	
	// Default values.
	public static final boolean DEFAULT_IS_ENABLED = true;
	public static final boolean DEFAULT_SHOULD_SHOW_IN_THIRD_PERSON = false;
	public static final boolean DEFAULT_SHOULD_SHOW_IN_SPECTATOR = false;
	public static final boolean DEFAULT_SHOULD_CROSSHAIR_USE_BLENDING = true;
	public static final float DEFAULT_CROSSHAIR_OPACITY = 1.0F;
	public static final boolean DEFAULT_SHOULD_INDICATOR_USE_BLENDING = true;
	public static final float DEFAULT_INDICATOR_OPACITY = 1.0F;
	public static final boolean DEFAULT_HAS_INDICATOR_FOR_NON_WEAPONS = false;
	public static final boolean DEFAULT_SHOULD_USE_RESPONSIVE_INDICATOR = false;
	
	// Current values.
	public boolean isEnabled = DEFAULT_IS_ENABLED;
	public boolean shouldShowInThirdPerson = DEFAULT_SHOULD_SHOW_IN_THIRD_PERSON;
	public boolean shouldShowInSpectator = DEFAULT_SHOULD_SHOW_IN_SPECTATOR;
	public boolean shouldCrosshairUseBlending = DEFAULT_SHOULD_CROSSHAIR_USE_BLENDING;
	public float crosshairOpacity = DEFAULT_CROSSHAIR_OPACITY;
	public boolean shouldIndicatorUseBlending = DEFAULT_SHOULD_INDICATOR_USE_BLENDING;
	public float indicatorOpacity = DEFAULT_INDICATOR_OPACITY;
	public boolean hasIndicatorForNonWeapons = DEFAULT_HAS_INDICATOR_FOR_NON_WEAPONS;
	public boolean shouldUseResponsiveIndicator = DEFAULT_SHOULD_USE_RESPONSIVE_INDICATOR;
	
	// Config saving stuff.
	// Registering a color adapter is mandatory, as the mod glitches out otherwise while interacting with colors.
	private static final Gson _GSON = new GsonBuilder()
		.registerTypeAdapter(Color.class, new ColorAdapter())
		.setPrettyPrinting().create();
	private static final String _CONFIG_NAME = Main.MOD_ID + ".json";
	private static final Path _CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(_CONFIG_NAME);
	
	private static ModConfig _instance;
	
	public static ModConfig getInstance() {
		if (_instance == null) {
			_instance = load();
		}
		return _instance;
	}
	
	private void validate() {
		boolean wasModified = ConfigValidator.validate(this);
		if (!wasModified) return;
		
		save();
		Main.LOGGER.info("Fixed invalid values in config!");
	}
	
	public static ModConfig load() {
		if (Files.exists(_CONFIG_PATH)) {
			try (var reader = Files.newBufferedReader(_CONFIG_PATH)) {
				var loaded = _GSON.fromJson(reader, ModConfig.class);
				if (loaded != null) {
					loaded.validate();
					return loaded;
				}
			} catch (Exception e) {
				Main.LOGGER.warn("Failed to load config, using defaults!", e);
			}
		}
		
		var config = new ModConfig();
		config.save();
		return config;
	}
	
	public void save() {
		try (var writer = Files.newBufferedWriter(_CONFIG_PATH)) {
			_GSON.toJson(this, writer);
		} catch (Exception e) {
			Main.LOGGER.warn("Failed to save config!", e);
		}
	}
}
