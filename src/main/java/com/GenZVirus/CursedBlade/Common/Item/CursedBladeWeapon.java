package com.GenZVirus.CursedBlade.Common.Item;

import java.util.UUID;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.CursedBlade.CursedBladeStats;
import com.GenZVirus.CursedBlade.Common.Effects.CurseEffect;
import com.GenZVirus.CursedBlade.Common.Entities.FlamesOfUndoingEntity;
import com.GenZVirus.CursedBlade.Common.Network.PacketHandlerCommon;
import com.GenZVirus.CursedBlade.Common.Network.Packets.SendCursedPlayerData;
import com.GenZVirus.CursedBlade.File.XMLFileJava;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.TieredItem;
import net.minecraft.item.UseAction;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

public class CursedBladeWeapon extends TieredItem {

	private int timer = 0;
	private float attackDamage;
	/** Modifiers applied when the item is in the mainhand of a user. */
	private Multimap<Attribute, AttributeModifier> attributeModifiers;
	private static FlamesOfUndoingEntity CURSED_BEAM;

	public CursedBladeWeapon(IItemTier tier, Item.Properties builderIn) {
		super(tier, builderIn);
		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.attackDamage, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 0, AttributeModifier.Operation.ADDITION));
		this.attributeModifiers = builder.build();
	}

	@Override
	public IItemTier getTier() {
		return super.getTier();
	}

	public void reload() {
		XMLFileJava.loadDamage();
		if (com.GenZVirus.CursedBlade.CursedBlade.CURSED_PLAYER != null)
			XMLFileJava.load(com.GenZVirus.CursedBlade.CursedBlade.CURSED_PLAYER);
		this.attackDamage = (float) CursedBladeStats.ATTACK_DAMAGE;
		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.attackDamage, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 0, AttributeModifier.Operation.ADDITION));
		this.attributeModifiers = builder.build();
	}

	public float getAttackDamage() {
		return this.attackDamage + 1;
	}

	public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		return !player.isCreative();
	}

	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (state.isIn(Blocks.COBWEB)) {
			return 15.0F;
		} else {
			Material material = state.getMaterial();
			return material != Material.PLANTS && material != Material.TALL_PLANTS && material != Material.CORAL && !state.isIn(BlockTags.LEAVES) && material != Material.GOURD ? 1.0F : 1.5F;
		}
	}

	/**
	 * Current implementations of this method in child classes do not use the entry
	 * argument beside ev. They just raise the damage on the stack.
	 */
	public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return true;
	}

	/**
	 * Called when a Block is destroyed using this Item. Return true to trigger the
	 * "Use Item" statistic.
	 */
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
		if (state.getBlockHardness(worldIn, pos) != 0.0F) {
			stack.damageItem(2, entityLiving, (entity) -> {
				entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
			});
		}

		return true;
	}

	/**
	 * Check whether this Item can harvest the given Block
	 */
	public boolean canHarvestBlock(BlockState blockIn) {
		return blockIn.isIn(Blocks.COBWEB);
	}

	/**
	 * Gets a map of item attribute modifiers, used by ItemSword to increase hit
	 * damage.
	 */
	@SuppressWarnings("deprecation")
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
		return equipmentSlot == EquipmentSlotType.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(equipmentSlot);
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false;
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
//		entity.setNoDespawn();
		if (com.GenZVirus.CursedBlade.CursedBlade.CURSED_PLAYER == null) {
			if (timer == 0) {
				try {
					PlayerList playerList = entity.getServer().getPlayerList();
					if (!playerList.getPlayers().isEmpty()) {
						UUID uuid = playerList.getPlayers().get(0).getUniqueID();
						String dimension = entity.world.getDimensionKey().getLocation().getPath().replace('_', ' ');
						dimension = dimension.substring(0, 1).toUpperCase() + dimension.substring(1);
						TranslationTextComponent text = new TranslationTextComponent("Blade location X: \u00A74" + Integer.toString((int) entity.getPosX()) + "\u00A7r Y: \u00A74"
								+ Integer.toString((int) entity.getPosY()) + "\u00A7r Z: \u00A74" + Integer.toString((int) entity.getPosZ()) + "\u00A7r Dimension: \u00A74" + dimension);
						playerList.func_232641_a_(text, ChatType.SYSTEM, uuid);
						timer = 1200;
					}
				} catch (Exception e) {
				}
			} else {
				timer--;
			}
			return super.onEntityItemUpdate(stack, entity);
		}
		double x = com.GenZVirus.CursedBlade.CursedBlade.CURSED_PLAYER.getPosX();
		double y = com.GenZVirus.CursedBlade.CursedBlade.CURSED_PLAYER.getPosY();
		double z = com.GenZVirus.CursedBlade.CursedBlade.CURSED_PLAYER.getPosZ();
		entity.setPositionAndUpdate(x, y, z);
		return super.onEntityItemUpdate(stack, entity);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	@Override
	public boolean isImmuneToFire() {
		return true;
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote()) {
			if (CursedBladeStats.KILL_COUNTER >= 1000 && CursedBladeStats.STATUS.equals("Dormant")) {
				CursedBladeStats.STATUS = "Awakened";
				PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(CursedBladeStats.KILL_COUNTER, CursedBladeStats.ATTACK_DAMAGE, CursedBladeStats.LIFE_STEAL,
						CursedBladeStats.DESTROY_ABSORPTION, CursedBladeStats.DESTROY_SHIELDS, CursedBladeStats.FIRE_ASPECT, CursedBladeStats.POISON, CursedBladeStats.WITHER, CursedBladeStats.STATUS,
						CursedBladeStats.HUNGER, CursedBladeStats.EXHAUST), CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
			}
		}
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		return super.onItemUse(context);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (CursedBladeStats.STATUS.equals("Awakened")) {
			double offset = 1.0D;
			double pitch = playerIn.getPitchYaw().x;
			double yaw = playerIn.getPitchYaw().y;
			double pitchRadian = pitch * (Math.PI / 180); // X rotation
			double yawRadian = yaw * (Math.PI / 180); // Y rotation
			double newPosX = offset * -Math.sin(yawRadian) * Math.cos(pitchRadian);
			double newPosY = offset * -Math.sin(pitchRadian);
			double newPosZ = offset * Math.cos(yawRadian) * Math.cos(pitchRadian);
			CURSED_BEAM = new FlamesOfUndoingEntity(playerIn.world, playerIn, newPosX, newPosY, newPosZ);
			CURSED_BEAM.rotationPitch = (float) pitch;
			CURSED_BEAM.rotationYaw = (float) yaw;
			CURSED_BEAM.setRawPosition(playerIn.getPosX(), 1.0D + playerIn.getPosY(), playerIn.getPosZ());
			CURSED_BEAM.setDamage(this.attackDamage);
			CURSED_BEAM.setShooter(playerIn);
			playerIn.world.addEntity(CURSED_BEAM);
		}
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return ActionResult.resultConsume(itemstack);
	}

	static int timer1 = 20;

	public static void attemptToDamageEntity(Entity entity, DamageSource source, float amount) {
		if (!entity.attackEntityFrom(source, amount)) {
			if (entity instanceof EnderDragonPartEntity) {
				((EnderDragonPartEntity) entity).attackEntityFrom(source, amount);
			} else if (timer1 == 0) {
				timer1 = 20;
				entity.onKillCommand();
			} else {
				if (entity instanceof LivingEntity) {
					LivingEntity target = (LivingEntity) entity;
					target.setHealth(target.getHealth() - amount);
				}
				timer1--;
			}
		} else {
			timer1 = 20;
		}
		if (entity instanceof LivingEntity) {
			CurseEffect.applyCurse((LivingEntity) entity);
		}
	}

	public static class CursedDamage extends EntityDamageSource {

		public static final DamageSource CURSED_BLADE_DAMAGE = new CursedDamage("CursedBladeDamage", null).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
		public static final DamageSource CURSED_SWEEP_DAMAGE = new CursedDamage("CursedSweepDamage", null).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();

		public CursedDamage(String damageTypeIn, Entity entity) {
			super(damageTypeIn, entity);
		}

		@Override
		public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn) {
			ItemStack itemstack = this.damageSourceEntity instanceof LivingEntity ? ((LivingEntity) this.damageSourceEntity).getHeldItemMainhand() : ItemStack.EMPTY;
			String s = "death.attack." + this.damageType;
			return !itemstack.isEmpty() && itemstack.hasDisplayName()
					? new TranslationTextComponent(s + ".item", entityLivingBaseIn.getDisplayName(), this.damageSourceEntity.getDisplayName(), itemstack.getTextComponent())
					: new TranslationTextComponent(s, entityLivingBaseIn.getDisplayName(), CursedBlade.CURSED_PLAYER.getDisplayName());

		}

		@Override
		public Entity getTrueSource() {
			return CursedBlade.CURSED_PLAYER;
		}
	}

	public static class CursedBeamDamage extends CursedDamage {

		public static final DamageSource CURSED_BEAM_DAMAGE = new CursedDamage("CursedBeamDamage", null).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();

		public CursedBeamDamage(String damageTypeIn, Entity entity) {
			super(damageTypeIn, entity);
		}

		@Override
		public Entity getTrueSource() {
			return CURSED_BEAM;
		}
	}

	public static class CursedEffectDamage extends DamageSource {

		public static final DamageSource CURSED_EFFECT_DAMAGE = new CursedEffectDamage("CursedEffectDamage").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();

		public CursedEffectDamage(String damageTypeIn) {
			super(damageTypeIn);
		}

		@Override
		public Entity getTrueSource() {
			return CursedBlade.CURSED_PLAYER;
		}
	}

}
