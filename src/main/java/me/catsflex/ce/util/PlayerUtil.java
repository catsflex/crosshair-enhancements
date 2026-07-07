package me.catsflex.ce.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public final class PlayerUtil {
	private PlayerUtil() {}
	
	// A better approach for checking whether the current item is a weapon or not.
	public static boolean isHoldingWeapon() {
		final var player = Minecraft.getInstance().player;
		if (player == null) return false;
		
		final var mainHandItem = player.getMainHandItem();
		if (mainHandItem.isEmpty()) return false;
		
		final var modifiers = mainHandItem.get(DataComponents.ATTRIBUTE_MODIFIERS);
		if (modifiers == null) return false;
		
		for (final var modifier : modifiers.modifiers()) {
			final var attribute = modifier.attribute();
			
			// If an item has a modified attribute,
			// it is *probably* a weapon or an item that is supposed to be used as one.
			if (attribute.equals(Attributes.ATTACK_DAMAGE) || attribute.equals(Attributes.ATTACK_SPEED))
				return true;
		}
		
		return false;
	}
	
	public static boolean isLookingAtEntity(@Nullable final HitResult hitResult) {
		return hitResult != null && hitResult.getType() == HitResult.Type.ENTITY;
	}
	
	public static boolean isLookingAtBlock(@Nullable final HitResult hitResult) {
		return hitResult != null && hitResult.getType() == HitResult.Type.BLOCK;
	}
	
	public static boolean isMining() {
		final var client = Minecraft.getInstance();
		if (client.player == null) return false;
		
		return client.options.keyAttack.isDown() && isLookingAtBlock(client.hitResult);
	}
}
