package com.GenZVirus.CursedBlade.Events;

import java.util.Random;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Config;
import com.GenZVirus.CursedBlade.Common.Init.ItemInit;
import com.GenZVirus.CursedBlade.Common.Network.PacketHandlerCommon;
import com.GenZVirus.CursedBlade.Common.Network.Packets.SendCursedPlayerData;
import com.GenZVirus.CursedBlade.File.XMLFileJava;

import net.minecraft.entity.LivingEntity;
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
		if (event.getPlayer().getHeldItemMainhand().getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade) {
			if (((com.GenZVirus.CursedBlade.Common.Item.CursedBlade) event.getPlayer().getHeldItemMainhand().getItem()).getAttackDamage() >= Config.COMMON.whenToBreakBlocks.get()) {
				event.getWorld().destroyBlock(event.getPos(), false);
				event.getWorld().playSound(null, event.getPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (new Random().nextFloat() - new Random().nextFloat()) * 0.2F) * 0.7F);
			} else
				return;
		} else {
			return;
		}
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void CursedBladeDamage(LivingAttackEvent event) {
		if (event.getEntityLiving().world.isRemote) { return; }
		if (!(event.getSource().getTrueSource() instanceof PlayerEntity)) { return; }
		if (!(((PlayerEntity) event.getSource().getTrueSource()).getHeldItemMainhand().getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade))
			return;
		applyEffects(CursedBlade.CURSED_PLAYER.inventory.getStackInSlot(0), event.getEntityLiving(), CursedBlade.CURSED_PLAYER);
	}

	public static void applyEffects(ItemStack stack, LivingEntity target, PlayerEntity attacker) {

		// Apply absorption destruction

		if (target.getAbsorptionAmount() > 0 && Config.COMMON.destroyAbsorption.get() && com.GenZVirus.CursedBlade.CursedBlade.DESTROY_ABSORPTION)
			target.setAbsorptionAmount(0);

		// Apply life steal

		attacker.heal((float) (((com.GenZVirus.CursedBlade.Common.Item.CursedBlade) attacker.inventory.getStackInSlot(0).getItem()).getAttackDamage() * Config.COMMON.life_steal_ratio.get() * com.GenZVirus.CursedBlade.CursedBlade.LIFE_STEAL));

		// Apply shield destruction

		if (target.getActiveItemStack().getItem() instanceof ShieldItem && com.GenZVirus.CursedBlade.CursedBlade.DESTROY_SHIELDS) {
			target.getActiveItemStack().setDamage(0);
		}

		// Apply fire damage for 5 seconds
		if (com.GenZVirus.CursedBlade.CursedBlade.FIRE_ASPECT) {
			target.setFire(5);
		}

		// Apply poison for 5 seconds

		if (com.GenZVirus.CursedBlade.CursedBlade.POISON > 0) {
			target.addPotionEffect(new EffectInstance(Effects.POISON, 100, com.GenZVirus.CursedBlade.CursedBlade.POISON - 1));
		}

		// Apply wither for 5 seconds

		if (com.GenZVirus.CursedBlade.CursedBlade.WITHER > 0) {
			target.addPotionEffect(new EffectInstance(Effects.WITHER, 100, com.GenZVirus.CursedBlade.CursedBlade.WITHER - 1));
		}

	}

	@SubscribeEvent
	public static void onCursedPlayerKilledByPlayer(LivingDeathEvent event) {
		if (event.getEntityLiving().world.isRemote) { return; }
		if (!(event.getEntityLiving() instanceof PlayerEntity)) { return; }
		if (CursedBlade.CURSED_PLAYER == null) { return; }
		if (CursedBlade.CURSED_PLAYER.equals(event.getEntityLiving()) && event.getSource().getTrueSource() instanceof PlayerEntity) {
			CursedBlade.CURSED_PLAYER = (ServerPlayerEntity) event.getSource().getTrueSource();
			CursedBlade.PLAYER_UUID = event.getSource().getTrueSource().getUniqueID();
			XMLFileJava.save(CursedBlade.CURSED_PLAYER);
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
		if (player.getHeldItemMainhand().getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade) {
			XMLFileJava.addOne("KillCount");
			XMLFileJava.checkForUpgrades();
			((com.GenZVirus.CursedBlade.Common.Item.CursedBlade) player.getHeldItemMainhand().getItem()).reload();
			player.getAttributeManager().reapplyModifiers(player.getHeldItemMainhand().getAttributeModifiers(EquipmentSlotType.MAINHAND));
			PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(CursedBlade.KILL_COUNTER, CursedBlade.ATTACK_DAMAGE, CursedBlade.LIFE_STEAL, CursedBlade.DESTROY_ABSORPTION, CursedBlade.DESTROY_SHIELDS, CursedBlade.FIRE_ASPECT, CursedBlade.POISON, CursedBlade.WITHER, CursedBlade.STATUS), CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
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
			if (((PlayerEntity) event.getEntityLiving()).inventory.getStackInSlot(i).getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade) {
				((PlayerEntity) event.getEntityLiving()).inventory.removeStackFromSlot(i);
			}
		}
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void onItemToss(ItemTossEvent event) {
		if (event.getEntityItem().getItem().getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade) {
			event.getEntityItem().setNoPickupDelay();
			;
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
			if (!(event.getItem().getItem().getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade)) { return; }
			if (CursedBlade.PLAYER_UUID == null) {
				CursedBlade.PLAYER_UUID = player.getUniqueID();
				CursedBlade.CURSED_PLAYER = player;
				ItemStack stack = CursedBlade.CURSED_PLAYER.inventory.getStackInSlot(0);
				if (!CursedBlade.CURSED_PLAYER.inventory.addItemStackToInventory(stack)) {
					CursedBlade.CURSED_PLAYER.inventory.removeStackFromSlot(0);
					CursedBlade.CURSED_PLAYER.dropItem(stack, false);
				} else {
					CursedBlade.CURSED_PLAYER.inventory.removeStackFromSlot(0);
				}
				XMLFileJava.save(CursedBlade.CURSED_PLAYER);
				PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(CursedBlade.KILL_COUNTER, CursedBlade.ATTACK_DAMAGE, CursedBlade.LIFE_STEAL, CursedBlade.DESTROY_ABSORPTION, CursedBlade.DESTROY_SHIELDS, CursedBlade.FIRE_ASPECT, CursedBlade.POISON, CursedBlade.WITHER, CursedBlade.STATUS), CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
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
		if (!(CursedBlade.CURSED_PLAYER.inventory.getStackInSlot(0).getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade))
			return;
		if (mustReload && !CursedBlade.CURSED_PLAYER.inventory.getStackInSlot(0).isEmpty()) {
			mustReload = false;
			((com.GenZVirus.CursedBlade.Common.Item.CursedBlade) CursedBlade.CURSED_PLAYER.inventory.getStackInSlot(0).getItem()).reload();
		}
	}

	@SubscribeEvent
	public static void onCursedPlayerLogin(PlayerLoggedInEvent event) {
		if (event.getEntityLiving().world.isRemote)
			return;
		XMLFileJava.checkFileAndMake(event.getPlayer());
		XMLFileJava.loadUUID();
		if (CursedBlade.PLAYER_UUID == null)
			return;
		if (CursedBlade.PLAYER_UUID.equals(event.getPlayer().getUniqueID())) {
			CursedBlade.CURSED_PLAYER = (ServerPlayerEntity) event.getPlayer();
			boolean hasWeapon = false;
			for (int i = 0; i < ((PlayerEntity) event.getEntityLiving()).inventory.getSizeInventory(); i++) {
				if (((PlayerEntity) event.getEntityLiving()).inventory.getStackInSlot(i).getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade) {
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
			PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(CursedBlade.KILL_COUNTER, CursedBlade.ATTACK_DAMAGE, CursedBlade.LIFE_STEAL, CursedBlade.DESTROY_ABSORPTION, CursedBlade.DESTROY_SHIELDS, CursedBlade.FIRE_ASPECT, CursedBlade.POISON, CursedBlade.WITHER, CursedBlade.STATUS), CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
		mustReload = true;
	}

	@SubscribeEvent
	public static void onCursedPlayerLogout(PlayerLoggedOutEvent event) {
		if (event.getEntityLiving().world.isRemote)
			return;
		XMLFileJava.save(event.getPlayer());
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
			PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(CursedBlade.KILL_COUNTER, CursedBlade.ATTACK_DAMAGE, CursedBlade.LIFE_STEAL, CursedBlade.DESTROY_ABSORPTION, CursedBlade.DESTROY_SHIELDS, CursedBlade.FIRE_ASPECT, CursedBlade.POISON, CursedBlade.WITHER, CursedBlade.STATUS), CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	@SubscribeEvent
	public static void itemExperied(ItemExpireEvent event) {

	}

	@SubscribeEvent(receiveCanceled = true)
	public static void cursedPlayerItemFrame(EntityInteract event) {
		if (event.getTarget() instanceof ItemFrameEntity && event.getItemStack().getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade) {
			event.setCanceled(true);
		}
	}
}
