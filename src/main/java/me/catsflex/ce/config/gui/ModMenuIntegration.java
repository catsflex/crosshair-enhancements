package me.catsflex.ce.config.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public final class ModMenuIntegration implements ModMenuApi {
	private static final String YACL_MOD_ID = "yet_another_config_lib_v3";
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return FabricLoader.getInstance().isModLoaded(YACL_MOD_ID)
			? YACLIntegration::createScreen
			: parent -> null;
	}
}
