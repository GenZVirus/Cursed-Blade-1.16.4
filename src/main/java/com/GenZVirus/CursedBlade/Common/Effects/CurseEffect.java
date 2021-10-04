package com.GenZVirus.CursedBlade.Common.Effects;

import com.GenZVirus.CursedBlade.Common.Init.EffectsInit;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon.CursedEffectDamage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.stats.Stats;

public class CurseEffect extends Effect {

	public CurseEffect(EffectType typeIn, int liquidColorIn) {
		super(typeIn, liquidColorIn);
	}

	boolean somethingBroke;
	@Override
	public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		somethingBroke = false;
		entityLivingBaseIn.getArmorInventoryList().forEach(itemStack -> {
			if (!itemStack.isEmpty()) {
				Item item = itemStack.getItem();
				itemStack.shrink(1);
				if (entityLivingBaseIn instanceof PlayerEntity) {
					((PlayerEntity) entityLivingBaseIn).addStat(Stats.ITEM_BROKEN.get(item));
					((PlayerEntity) entityLivingBaseIn).sendBreakAnimation(itemStack.getEquipmentSlot());
				}
				itemStack.setDamage(0);
				somethingBroke=true;
			}
		});
		if(somethingBroke) return;
		CursedBladeWeapon.attemptToDamageEntity(entityLivingBaseIn, CursedEffectDamage.CURSED_EFFECT_DAMAGE, Float.MAX_VALUE);

	}
	
	public static void applyCurse(LivingEntity entity) {
		int seconds = 3600;
		EffectInstance effectInstance = new EffectInstance(EffectsInit.CURSED_BLADE.get(), seconds * 20, 0);
		if(!entity.isPotionActive(effectInstance.getPotion())) {
			entity.activePotionsMap.put(effectInstance.getPotion(), effectInstance);
		} else {
			entity.getActivePotionEffect(EffectsInit.CURSED_BLADE.get()).combine(new EffectInstance(EffectsInit.CURSED_BLADE.get(), seconds * 20, entity.getActivePotionEffect(EffectsInit.CURSED_BLADE.get()).getAmplifier() + 1));
		}
	}
}
