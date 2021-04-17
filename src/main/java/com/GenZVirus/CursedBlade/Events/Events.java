package com.GenZVirus.CursedBlade.Events;

import java.util.Random;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Config;
import com.GenZVirus.CursedBlade.Common.Init.ItemInit;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon.CursedDamage;
import com.GenZVirus.CursedBlade.Common.Network.PacketHandlerCommon;
import com.GenZVirus.CursedBlade.Common.Network.Packets.SendCursedPlayerData;
import com.GenZVirus.CursedBlade.File.XMLFileJava;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkDirection;

@Mod.EventBusSubscriber(modid = CursedBlade.MOD_ID, bus = Bus.FORGE)
public class Events {

	public static boolean mustReload = false;

	@SubscribeEvent
	public static void onBlockHit(LeftClickBlock event) {
		if (event.getWorld().isRemote())
			return;
		if (event.getPlayer().getHeldItemMainhand().getItem() instanceof CursedBladeWeapon) {
			if (((CursedBladeWeapon) event.getPlayer().getHeldItemMainhand().getItem()).getAttackDamage() >= Config.COMMON.whenToBreakBlocks.get()) {
				event.getWorld().destroyBlock(event.getPos(), false);
				event.getWorld().playSound(null, event.getPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F,
						(1.0F + (new Random().nextFloat() - new Random().nextFloat()) * 0.2F) * 0.7F);
			} else
				return;
		} else {
			return;
		}
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void CursedBladeAttack(LivingAttackEvent event) {
		if (event.getEntityLiving().world.isRemote) {
			return;
		}
		if (!(event.getSource().getTrueSource() instanceof PlayerEntity)) {
			return;
		}
		if (!(((PlayerEntity) event.getSource().getTrueSource()).getHeldItemMainhand().getItem() instanceof CursedBladeWeapon))
			return;
		PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
		if (!event.getSource().equals(CursedDamage.CURSED_DAMAGE)) {
			event.setCanceled(true);
			applyEffectsOnHit(player.inventory.getStackInSlot(0), event.getEntityLiving(), player);
			event.getEntityLiving().attackEntityFrom(CursedBladeWeapon.CursedDamage.CURSED_DAMAGE, ((CursedBladeWeapon) player.getHeldItemMainhand().getItem()).getAttackDamage());
		}
	}

	public static void applyEffectsOnHit(ItemStack stack, LivingEntity target, PlayerEntity attacker) {

		// Apply life steal

		attacker.heal((float) (((CursedBladeWeapon) attacker.inventory.getStackInSlot(0).getItem()).getAttackDamage() * Config.COMMON.life_steal_ratio.get() * CursedBlade.LIFE_STEAL));

		// Apply shield destruction

		if (target.getActiveItemStack().getItem() instanceof ShieldItem && CursedBlade.DESTROY_SHIELDS) {
			target.getActiveItemStack().setDamage(0);
		}

		applyEffects(stack, target, attacker);

		// Sweep

		if (attacker instanceof PlayerEntity) {

			float f3 = 1.0F + 0.2F * ((CursedBladeWeapon) attacker.getHeldItemMainhand().getItem()).getAttackDamage();

			for (LivingEntity livingentity : attacker.world.getEntitiesWithinAABB(LivingEntity.class, target.getBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
				if (livingentity != attacker && livingentity != target && !attacker.isOnSameTeam(livingentity)
						&& (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).hasMarker()) && attacker.getDistanceSq(livingentity) < 9.0D) {
					livingentity.applyKnockback(0.4F, (double) MathHelper.sin(attacker.rotationYaw * ((float) Math.PI / 180F)),
							(double) (-MathHelper.cos(attacker.rotationYaw * ((float) Math.PI / 180F))));
					livingentity.attackEntityFrom(CursedDamage.CURSED_DAMAGE, f3);
				}
			}
			attacker.world.playSound((PlayerEntity) null, attacker.getPosX(), attacker.getPosY(), attacker.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);
			((PlayerEntity) attacker).spawnSweepParticles();
		}
		stack.damageItem(1, attacker, (entity) -> {
			entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
		});

	}

	public static void applyEffects(ItemStack stack, LivingEntity target, PlayerEntity attacker) {
		// Apply absorption destruction

		if (target.getAbsorptionAmount() > 0 && Config.COMMON.destroyAbsorption.get() && CursedBlade.DESTROY_ABSORPTION)
			target.setAbsorptionAmount(0);

		// Apply fire damage for 5 seconds
		if (CursedBlade.FIRE_ASPECT) {
			target.setFire(5);
		}

		// Apply poison for 5 seconds

		if (CursedBlade.POISON > 0) {
			target.addPotionEffect(new EffectInstance(Effects.POISON, 100, CursedBlade.POISON - 1));
		}

		// Apply wither for 5 seconds

		if (CursedBlade.WITHER > 0) {
			target.addPotionEffect(new EffectInstance(Effects.WITHER, 100, CursedBlade.WITHER - 1));
		}

		if (CursedBlade.HUNGER > 0) {
			target.addPotionEffect(new EffectInstance(Effects.HUNGER, 100, CursedBlade.HUNGER - 1));
		}

		if (CursedBlade.EXHAUST > 0) {
			target.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 100, CursedBlade.EXHAUST - 1));
			target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, CursedBlade.EXHAUST - 1));
			target.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 100, CursedBlade.EXHAUST - 1));
		}

	}

	@SubscribeEvent
	public static void onCursedPlayerKilledByPlayer(LivingDeathEvent event) {
		if (event.getEntityLiving().world.isRemote) {
			return;
		}
		if (!(event.getEntityLiving() instanceof PlayerEntity)) {
			return;
		}
		if (CursedBlade.CURSED_PLAYER == null) {
			return;
		}
		if (CursedBlade.CURSED_PLAYER.equals(event.getEntityLiving()) && event.getSource().getTrueSource() instanceof PlayerEntity) {
			CursedBlade.CURSED_PLAYER = (ServerPlayerEntity) event.getSource().getTrueSource();
			CursedBlade.PLAYER_UUID = event.getSource().getTrueSource().getUniqueID();
			XMLFileJava.save();
		}
	}

	@SubscribeEvent
	public static void spawnCursedBlade(LivingDeathEvent event) {
		if (event.getEntityLiving().world.isRemote)
			return;
		if (event.getEntityLiving() instanceof PlayerEntity)
			return;
		if (CursedBlade.PLAYER_UUID == null) {
			Random rand = new Random();
			if (rand.nextInt(Config.COMMON.dropChance.get()) == 0) {
				event.getEntityLiving().entityDropItem(new ItemStack(ItemInit.CURSED_BLADE.get()));
			}
		}
	}

	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getEntityLiving().world.isRemote)
			return;
		if (!(event.getSource().getTrueSource() instanceof PlayerEntity))
			return;
		PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
		if (player.getHeldItemMainhand().getItem() instanceof CursedBladeWeapon) {
			XMLFileJava.addOne("KillCount");
			XMLFileJava.checkForUpgrades();
			((CursedBladeWeapon) player.getHeldItemMainhand().getItem()).reload();
			player.getAttributeManager().reapplyModifiers(player.getHeldItemMainhand().getAttributeModifiers(EquipmentSlotType.MAINHAND));
			PacketHandlerCommon.INSTANCE.sendTo(
					new SendCursedPlayerData(CursedBlade.KILL_COUNTER, CursedBlade.ATTACK_DAMAGE, CursedBlade.LIFE_STEAL, CursedBlade.DESTROY_ABSORPTION, CursedBlade.DESTROY_SHIELDS,
							CursedBlade.FIRE_ASPECT, CursedBlade.POISON, CursedBlade.WITHER, CursedBlade.STATUS, CursedBlade.HUNGER, CursedBlade.EXHAUST),
					CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	@SubscribeEvent
	public static void onCursedPlayerDeath(LivingDeathEvent event) {
		if (event.getEntityLiving().world.isRemote)
			return;
		if (!(event.getEntityLiving() instanceof PlayerEntity))
			return;
		if (event.getSource().getTrueSource() instanceof PlayerEntity)
			return;
		for (int i = 0; i < ((PlayerEntity) event.getEntityLiving()).inventory.getSizeInventory(); i++) {
			if (((PlayerEntity) event.getEntityLiving()).inventory.getStackInSlot(i).getItem() instanceof CursedBladeWeapon) {
				((PlayerEntity) event.getEntityLiving()).inventory.removeStackFromSlot(i);
			}
		}
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void onItemToss(ItemTossEvent event) {
		if (event.getEntityItem().getItem().getItem() instanceof CursedBladeWeapon) {
			event.getEntityItem().setNoPickupDelay();
			mustReload = true;
		}
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void onItemPickUp(EntityItemPickupEvent event) {
		if (event.getEntityLiving().world.isRemote)
			return;
		if (!(event.getEntityLiving() instanceof ServerPlayerEntity)) {
			event.setCanceled(true);
		} else {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
			if (!(event.getItem().getItem().getItem() instanceof CursedBladeWeapon)) {
				return;
			}
			if (CursedBlade.PLAYER_UUID == null) {
				CursedBlade.PLAYER_UUID = player.getUniqueID();
				CursedBlade.CURSED_PLAYER = player;
				ItemStack stack = player.inventory.getStackInSlot(0);
				if (!player.inventory.addItemStackToInventory(stack)) {
					player.inventory.removeStackFromSlot(0);
					player.dropItem(stack, false);
				} else {
					player.inventory.removeStackFromSlot(0);
				}
				XMLFileJava.save();
				PacketHandlerCommon.INSTANCE.sendTo(
						new SendCursedPlayerData(CursedBlade.KILL_COUNTER, CursedBlade.ATTACK_DAMAGE, CursedBlade.LIFE_STEAL, CursedBlade.DESTROY_ABSORPTION, CursedBlade.DESTROY_SHIELDS,
								CursedBlade.FIRE_ASPECT, CursedBlade.POISON, CursedBlade.WITHER, CursedBlade.STATUS, CursedBlade.HUNGER, CursedBlade.EXHAUST),
						CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
				mustReload = true;
			} else if (!CursedBlade.PLAYER_UUID.equals(player.getUniqueID())) {
				event.setCanceled(true);
			} else {
				mustReload = true;
				ItemStack stack = CursedBlade.CURSED_PLAYER.inventory.getStackInSlot(0);
				if (stack.getItem().equals(ItemInit.CURSED_BLADE.get())) {
					event.setCanceled(true);
					event.getItem().remove();
					return;
				}
				if (!CursedBlade.CURSED_PLAYER.inventory.addItemStackToInventory(stack)) {
					CursedBlade.CURSED_PLAYER.inventory.removeStackFromSlot(0);
					CursedBlade.CURSED_PLAYER.dropItem(stack, false);
				} else {
					CursedBlade.CURSED_PLAYER.inventory.removeStackFromSlot(0);
				}
			}
		}
	}

	@SubscribeEvent
	public static void mustReload(ServerTickEvent event) {
		if (CursedBlade.CURSED_PLAYER == null)
			return;
		if (!(CursedBlade.CURSED_PLAYER.inventory.getStackInSlot(0).getItem() instanceof CursedBladeWeapon))
			return;
		if (mustReload && !CursedBlade.CURSED_PLAYER.inventory.getStackInSlot(0).isEmpty()) {
			mustReload = false;
			((CursedBladeWeapon) CursedBlade.CURSED_PLAYER.inventory.getStackInSlot(0).getItem()).reload();
		}
	}

	@SubscribeEvent
	public static void onCursedPlayerLogin(PlayerLoggedInEvent event) {
		if (event.getEntityLiving().world.isRemote)
			return;
		XMLFileJava.checkFileAndMake();
		XMLFileJava.loadUUID();
		if (CursedBlade.PLAYER_UUID == null)
			return;
		if (CursedBlade.PLAYER_UUID.equals(event.getPlayer().getUniqueID())) {
			CursedBlade.CURSED_PLAYER = (ServerPlayerEntity) event.getPlayer();
			boolean hasWeapon = false;
			for (int i = 0; i < ((PlayerEntity) event.getEntityLiving()).inventory.getSizeInventory(); i++) {
				if (((PlayerEntity) event.getEntityLiving()).inventory.getStackInSlot(i).getItem() instanceof CursedBladeWeapon) {
					hasWeapon = true;
					break;
				}
			}
			if (!hasWeapon) {
				ItemStack stack = CursedBlade.CURSED_PLAYER.inventory.getStackInSlot(0);
				if (!CursedBlade.CURSED_PLAYER.inventory.addItemStackToInventory(stack)) {
					CursedBlade.CURSED_PLAYER.inventory.removeStackFromSlot(0);
					CursedBlade.CURSED_PLAYER.dropItem(stack, false);
				} else {
					CursedBlade.CURSED_PLAYER.inventory.removeStackFromSlot(0);
				}
				CursedBlade.CURSED_PLAYER.inventory.addItemStackToInventory(new ItemStack(ItemInit.CURSED_BLADE.get()));
			}
			XMLFileJava.load(event.getPlayer());
			PacketHandlerCommon.INSTANCE.sendTo(
					new SendCursedPlayerData(CursedBlade.KILL_COUNTER, CursedBlade.ATTACK_DAMAGE, CursedBlade.LIFE_STEAL, CursedBlade.DESTROY_ABSORPTION, CursedBlade.DESTROY_SHIELDS,
							CursedBlade.FIRE_ASPECT, CursedBlade.POISON, CursedBlade.WITHER, CursedBlade.STATUS, CursedBlade.HUNGER, CursedBlade.EXHAUST),
					CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
		mustReload = true;
	}

	@SubscribeEvent
	public static void onCursedPlayerLogout(PlayerLoggedOutEvent event) {
		if (event.getEntityLiving().world.isRemote)
			return;
		XMLFileJava.save();
		if (CursedBlade.PLAYER_UUID == null)
			return;
		if (CursedBlade.PLAYER_UUID.equals(event.getPlayer().getUniqueID())) {
			CursedBlade.CURSED_PLAYER = null;
		}

	}

	@SubscribeEvent
	public static void onCursedPlayerRespawn(Clone event) {
		if (event.getEntityLiving().world.isRemote)
			return;
		if (CursedBlade.PLAYER_UUID == null)
			return;
		if (CursedBlade.PLAYER_UUID.equals(event.getPlayer().getUniqueID())) {
			CursedBlade.CURSED_PLAYER = (ServerPlayerEntity) event.getPlayer();
			if (event.getPlayer().inventory.getStackInSlot(0).isEmpty()) {
				event.getPlayer().inventory.addItemStackToInventory(new ItemStack(ItemInit.CURSED_BLADE.get()));
			} else {
				ItemStack stack = event.getPlayer().inventory.getStackInSlot(0).copy();
				event.getPlayer().inventory.removeStackFromSlot(0);
				event.getPlayer().inventory.addItemStackToInventory(new ItemStack(ItemInit.CURSED_BLADE.get()));
				if (!event.getPlayer().inventory.addItemStackToInventory(stack)) {
					event.getPlayer().dropItem(stack, false);
				}
			}
			PacketHandlerCommon.INSTANCE.sendTo(
					new SendCursedPlayerData(CursedBlade.KILL_COUNTER, CursedBlade.ATTACK_DAMAGE, CursedBlade.LIFE_STEAL, CursedBlade.DESTROY_ABSORPTION, CursedBlade.DESTROY_SHIELDS,
							CursedBlade.FIRE_ASPECT, CursedBlade.POISON, CursedBlade.WITHER, CursedBlade.STATUS, CursedBlade.HUNGER, CursedBlade.EXHAUST),
					CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	@SubscribeEvent
	public static void itemExperied(ItemExpireEvent event) {

	}

	@SubscribeEvent(receiveCanceled = true)
	public static void cursedPlayerItemFrame(EntityInteract event) {
		if (event.getTarget() instanceof ItemFrameEntity && event.getItemStack().getItem() instanceof CursedBladeWeapon) {
			event.setCanceled(true);
		}
		if (event.getTarget() instanceof LivingEntity && event.getItemStack().getItem() instanceof CursedBladeWeapon) {
			applyEffects(event.getItemStack(), (LivingEntity) event.getTarget(), event.getPlayer());
		}
	}
}
