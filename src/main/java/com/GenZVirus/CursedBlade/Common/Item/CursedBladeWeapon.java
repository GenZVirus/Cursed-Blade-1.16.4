package com.GenZVirus.CursedBlade.Common.Item;

import java.util.List;

import com.GenZVirus.CursedBlade.CursedBlade;
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
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.item.UseAction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

public class CursedBladeWeapon extends TieredItem {

	private float attackDamage;
	/** Modifiers applied when the item is in the mainhand of a user. */
	private Multimap<Attribute, AttributeModifier> attributeModifiers;

	public CursedBladeWeapon(IItemTier tier, Item.Properties builderIn) {
		super(tier, builderIn);
		this.attackDamage = 0;
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
		if (com.GenZVirus.CursedBlade.CursedBlade.CURSED_PLAYER != null)
			XMLFileJava.load(com.GenZVirus.CursedBlade.CursedBlade.CURSED_PLAYER);
		this.attackDamage = (float) com.GenZVirus.CursedBlade.CursedBlade.ATTACK_DAMAGE;
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
		if (com.GenZVirus.CursedBlade.CursedBlade.CURSED_PLAYER == null)
			return super.onEntityItemUpdate(stack, entity);
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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (com.GenZVirus.CursedBlade.CursedBlade.STATUS.equals("Awakened")) {
			AxisAlignedBB CUBE_BOX = VoxelShapes.fullCube().getBoundingBox();
			AxisAlignedBB aabb = CUBE_BOX.offset(playerIn.getPositionVec()).grow(100);
			List<Entity> list = playerIn.world.getEntitiesWithinAABBExcludingEntity(playerIn, aabb);
			int size = 0;
			for (Entity entity : list) {
				if (entity instanceof LivingEntity) {
					size++;
					((LivingEntity) entity).clearActivePotions();
					if (entity instanceof PlayerEntity) {
						((PlayerEntity) entity).inventory.clear();
					}
					if (entity instanceof EnderDragonEntity) {
						((EnderDragonEntity) entity).attackEntityPartFrom(((EnderDragonEntity) entity).dragonPartHead,
								new CursedDamage("CursedBlade").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute(), Float.MAX_VALUE);
					} else {
						((LivingEntity) entity).attackEntityFrom(new CursedDamage("CursedBlade").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute(), Float.MAX_VALUE);
					}
				}
			}
			if (size > 0 && !worldIn.isRemote) {
				XMLFileJava.add("KillCount", size);
				XMLFileJava.load(com.GenZVirus.CursedBlade.CursedBlade.CURSED_PLAYER);
				PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(com.GenZVirus.CursedBlade.CursedBlade.KILL_COUNTER, com.GenZVirus.CursedBlade.CursedBlade.ATTACK_DAMAGE,
						com.GenZVirus.CursedBlade.CursedBlade.LIFE_STEAL, com.GenZVirus.CursedBlade.CursedBlade.DESTROY_ABSORPTION, com.GenZVirus.CursedBlade.CursedBlade.DESTROY_SHIELDS,
						com.GenZVirus.CursedBlade.CursedBlade.FIRE_ASPECT, com.GenZVirus.CursedBlade.CursedBlade.POISON, com.GenZVirus.CursedBlade.CursedBlade.WITHER,
						com.GenZVirus.CursedBlade.CursedBlade.STATUS, CursedBlade.HUNGER, CursedBlade.EXHAUST), com.GenZVirus.CursedBlade.CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);

			}
		}
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return ActionResult.resultConsume(itemstack);
	}

	public static class CursedDamage extends DamageSource {

		public static final DamageSource CURSED_DAMAGE = new CursedDamage("CursedBlade").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();

		public CursedDamage(String damageTypeIn) {
			super(damageTypeIn);
		}

		@Override
		public Entity getTrueSource() {
			return com.GenZVirus.CursedBlade.CursedBlade.CURSED_PLAYER;
		}
	}

}
