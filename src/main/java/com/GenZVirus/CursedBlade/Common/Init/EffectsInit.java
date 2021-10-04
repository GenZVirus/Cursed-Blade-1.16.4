package com.GenZVirus.CursedBlade.Common.Init;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Effects.CurseEffect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectsInit {

	public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, CursedBlade.MOD_ID);

	public static final RegistryObject<Effect> CURSED_BLADE = EFFECTS.register("cb_curse_effect", () -> new CurseEffect(EffectType.HARMFUL, 0));

}
