package com.GenZVirus.CursedBlade.Mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon.CursedDamage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

@Mixin(DamageSource.class)
public class MixinDamageSource {

	@Overwrite
	public static DamageSource causePlayerDamage(PlayerEntity player) {
		if (CursedBlade.CURSED_PLAYER == null) {
			return new EntityDamageSource("player", player);
		} else if (player.equals(CursedBlade.CURSED_PLAYER) && player.getHeldItemMainhand().getItem() instanceof CursedBladeWeapon) {
			return CursedDamage.CURSED_BLADE_DAMAGE;
		} else {
			return new EntityDamageSource("player", player);
		}
	}

}
