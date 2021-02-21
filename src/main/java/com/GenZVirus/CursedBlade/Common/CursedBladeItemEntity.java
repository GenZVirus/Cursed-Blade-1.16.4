package com.GenZVirus.CursedBlade.Common;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CursedBladeItemEntity extends ItemEntity{

	public CursedBladeItemEntity(World worldIn, double x, double y, double z, ItemStack stack) {
		super(worldIn, x, y, z, stack);
	}
	
	@Override
	public float getItemHover(float partialTicks) {
		return 0;
	}

}
