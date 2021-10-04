package com.GenZVirus.CursedBlade.Events;

import java.util.Random;
import java.util.UUID;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.CursedBlade.CursedBladeStats;
import com.GenZVirus.CursedBlade.Common.Config;
import com.GenZVirus.CursedBlade.Common.Effects.CurseEffect;
import com.GenZVirus.CursedBlade.Common.Init.EffectsInit;
import com.GenZVirus.CursedBlade.Common.Init.ItemInit;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon.CursedDamage;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon.CursedEffectDamage;
import com.GenZVirus.CursedBlade.Common.Network.PacketHandlerCommon;
import com.GenZVirus.CursedBlade.Common.Network.Packets.SendCursedPlayerData;
import com.GenZVirus.CursedBlade.File.XMLFileJava;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
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
	private static int timer = 0;
	private static int maxTime = 3456000;

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

	@SubscribeEvent
	public static void CursedBladeAttack(LivingAttackEvent event) {
		LivingEntity target = event.getEntityLiving();
		if (event.getEntityLiving().world.isRemote) {
			if(event.getSource().equals(CursedDamage.CURSED_BLADE_DAMAGE)) {
				Random rand = new Random();
				for (int i = 0; i < 50; i++) {
					float x = 1.0F - 2 * rand.nextFloat();
					float y = 1.0F - 2 * rand.nextFloat();
					float z = 1.0F - 2 * rand.nextFloat();
					target.world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, true, target.getPosX() + target.getWidth() * x * 2, target.getPosY() + target.getHeight() * y * 2, target.getPosZ() + target.getWidth() * z * 2,
							0, 0.01F, 0);
				}
			}
			return;
		}
		Random random = new Random();
		if (target.isPotionActive(EffectsInit.CURSED_BLADE.get()))
			if (random.nextInt(256 - target.getActivePotionEffect(EffectsInit.CURSED_BLADE.get()).getAmplifier()) == 0) {
				breakOrDead(event.getEntityLiving());
			}
		if (!(event.getSource().getTrueSource() instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
		if (!(player.getHeldItemMainhand().getItem() instanceof CursedBladeWeapon))
			return;
		if (event.getSource().equals(CursedDamage.CURSED_BLADE_DAMAGE)) {
			applyEffectsOnHit(player.inventory.getStackInSlot(0), target, player, true);
		}
	}

	private static boolean foundArmorPiece = false;

	private static void breakOrDead(LivingEntity entity) {
		foundArmorPiece = false;
		entity.getArmorInventoryList().forEach(itemStack -> {
			if (!itemStack.isEmpty()) {
				Item item = itemStack.getItem();
				itemStack.shrink(1);
				if (entity instanceof PlayerEntity) {
					((PlayerEntity) entity).addStat(Stats.ITEM_BROKEN.get(item));
					((PlayerEntity) entity).sendBreakAnimation(itemStack.getEquipmentSlot());
				}
				itemStack.setDamage(0);
				foundArmorPiece = true;
			}

		});
		if (!foundArmorPiece)
			CursedBladeWeapon.attemptToDamageEntity(entity, CursedEffectDamage.CURSED_EFFECT_DAMAGE, Float.MAX_VALUE);
	}

	@SubscribeEvent
	public static void CursedBladeDamage(LivingDamageEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (event.getSource().equals(CursedDamage.CURSED_BLADE_DAMAGE)) {
			float amount = CursedBladeStats.ATTACK_DAMAGE;
			if (event.getEntityLiving().isPotionActive(EffectsInit.CURSED_BLADE.get())) {
				amount += amount / 10.0F * entity.getActivePotionEffect(EffectsInit.CURSED_BLADE.get()).getAmplifier();
			}
			event.setAmount(amount);
		} else if (event.getEntityLiving().isPotionActive(EffectsInit.CURSED_BLADE.get())) {
			event.setAmount(event.getAmount() + event.getAmount() / 10.0F * entity.getActivePotionEffect(EffectsInit.CURSED_BLADE.get()).getAmplifier());
		}
	}

	public static void applyEffectsOnHit(ItemStack stack, LivingEntity target, PlayerEntity attacker, boolean sweep) {

		// Apply life steal

		attacker.heal((float) (((CursedBladeWeapon) attacker.inventory.getStackInSlot(0).getItem()).getAttackDamage() * Config.COMMON.life_steal_ratio.get() * CursedBladeStats.LIFE_STEAL));

		// Apply shield destruction

		if (target.getActiveItemStack().getItem() instanceof ShieldItem && CursedBladeStats.DESTROY_SHIELDS) {
			target.getActiveItemStack().setDamage(0);
		}

		applyEffects(stack, target, attacker);

		// Sweep

		if (sweep) {

			float f3 = 1.0F + 0.2F * ((CursedBladeWeapon) attacker.getHeldItemMainhand().getItem()).getAttackDamage();

			for (LivingEntity livingentity : attacker.world.getEntitiesWithinAABB(LivingEntity.class, target.getBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
				if (livingentity != attacker && livingentity != target && !attacker.isOnSameTeam(livingentity)
						&& (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).hasMarker()) && attacker.getDistanceSq(livingentity) < 9.0D) {
					livingentity.applyKnockback(0.4F, (double) MathHelper.sin(attacker.rotationYaw * ((float) Math.PI / 180F)),
							(double) (-MathHelper.cos(attacker.rotationYaw * ((float) Math.PI / 180F))));
					livingentity.attackEntityFrom(CursedDamage.CURSED_SWEEP_DAMAGE, f3);
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

		if (target.getAbsorptionAmount() > 0 && Config.COMMON.destroyAbsorption.get() && CursedBladeStats.DESTROY_ABSORPTION)
			target.setAbsorptionAmount(0);

		// Apply fire damage for 5 seconds
		if (CursedBladeStats.FIRE_ASPECT) {
			target.setFire(5);
		}

		// Apply poison for 5 seconds

		if (CursedBladeStats.POISON > 0) {
			target.addPotionEffect(new EffectInstance(Effects.POISON, 100, CursedBladeStats.POISON - 1));
		}

		// Apply wither for 5 seconds

		if (CursedBladeStats.WITHER > 0) {
			target.addPotionEffect(new EffectInstance(Effects.WITHER, 100, CursedBladeStats.WITHER - 1));
		}

		if (CursedBladeStats.HUNGER > 0) {
			target.addPotionEffect(new EffectInstance(Effects.HUNGER, 100, CursedBladeStats.HUNGER - 1));
		}

		if (CursedBladeStats.EXHAUST > 0) {
			target.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 100, CursedBladeStats.EXHAUST - 1));
			target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, CursedBladeStats.EXHAUST - 1));
			target.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 100, CursedBladeStats.EXHAUST - 1));
		}

		if (CursedBladeStats.STATUS.equals("Awakened")) {
			CurseEffect.applyCurse(target);
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
		if (CursedBlade.PLAYER_UUID == new UUID(0, 0)) {
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
			PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(CursedBladeStats.KILL_COUNTER, CursedBladeStats.ATTACK_DAMAGE, CursedBladeStats.LIFE_STEAL,
					CursedBladeStats.DESTROY_ABSORPTION, CursedBladeStats.DESTROY_SHIELDS, CursedBladeStats.FIRE_ASPECT, CursedBladeStats.POISON, CursedBladeStats.WITHER, CursedBladeStats.STATUS,
					CursedBladeStats.HUNGER, CursedBladeStats.EXHAUST), CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
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
			if (CursedBlade.PLAYER_UUID.equals(new UUID(0, 0))) {
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
				PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(CursedBladeStats.KILL_COUNTER, CursedBladeStats.ATTACK_DAMAGE, CursedBladeStats.LIFE_STEAL,
						CursedBladeStats.DESTROY_ABSORPTION, CursedBladeStats.DESTROY_SHIELDS, CursedBladeStats.FIRE_ASPECT, CursedBladeStats.POISON, CursedBladeStats.WITHER, CursedBladeStats.STATUS,
						CursedBladeStats.HUNGER, CursedBladeStats.EXHAUST), CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
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
			PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(CursedBladeStats.KILL_COUNTER, CursedBladeStats.ATTACK_DAMAGE, CursedBladeStats.LIFE_STEAL,
					CursedBladeStats.DESTROY_ABSORPTION, CursedBladeStats.DESTROY_SHIELDS, CursedBladeStats.FIRE_ASPECT, CursedBladeStats.POISON, CursedBladeStats.WITHER, CursedBladeStats.STATUS,
					CursedBladeStats.HUNGER, CursedBladeStats.EXHAUST), CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
			mustReload = true;
			timer = 0;
		} else {
			for (int i = 0; i < ((PlayerEntity) event.getEntityLiving()).inventory.getSizeInventory(); i++) {
				if (((PlayerEntity) event.getEntityLiving()).inventory.getStackInSlot(i).getItem() instanceof CursedBladeWeapon) {
					((PlayerEntity) event.getEntityLiving()).inventory.removeStackFromSlot(i);
					break;
				}
			}
		}

	}

	@SubscribeEvent
	public static void onCursedPlayerLogout(PlayerLoggedOutEvent event) {
		if (event.getEntityLiving().world.isRemote)
			return;
		XMLFileJava.save();
		if (CursedBlade.PLAYER_UUID == new UUID(0, 0))
			return;
		if (CursedBlade.PLAYER_UUID.equals(event.getPlayer().getUniqueID())) {
			CursedBlade.CURSED_PLAYER = null;
		}

	}

	@SubscribeEvent
	public static void onCursedPlayerRespawn(Clone event) {
		if (event.getEntityLiving().world.isRemote)
			return;
		if (CursedBlade.PLAYER_UUID == new UUID(0, 0))
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
			PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(CursedBladeStats.KILL_COUNTER, CursedBladeStats.ATTACK_DAMAGE, CursedBladeStats.LIFE_STEAL,
					CursedBladeStats.DESTROY_ABSORPTION, CursedBladeStats.DESTROY_SHIELDS, CursedBladeStats.FIRE_ASPECT, CursedBladeStats.POISON, CursedBladeStats.WITHER, CursedBladeStats.STATUS,
					CursedBladeStats.HUNGER, CursedBladeStats.EXHAUST), CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	@SubscribeEvent
	public static void itemExperied(ItemExpireEvent event) {

	}

	@SubscribeEvent
	public static void removeCursedPlayer(ServerTickEvent event) {
		if (timer == maxTime) {
			CursedBlade.PLAYER_UUID = new UUID(0, 0);
			XMLFileJava.save();
			timer = 0;
		} else if (CursedBlade.PLAYER_UUID != new UUID(0, 0) && CursedBlade.CURSED_PLAYER == null) {
			timer++;
		}
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
