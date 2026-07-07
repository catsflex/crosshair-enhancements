package me.catsflex.ce.config.custom;

import me.catsflex.ce.config.gui.ConfigKeyType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum IndicatorVisibilityStatus implements StringRepresentable {
	ALWAYS_ON("alwaysOn"),
	TARGETED("targeted"),
	NEVER("never");
	
	private final String status;
	
	IndicatorVisibilityStatus(String status) {
		this.status = status;
	}
	
	@Override
	public @NonNull String getSerializedName() {
		return status;
	}
	
	public Component getComponent() {
		return Component.translatable(ConfigKeyType.getEnumKey(this));
	}
}
