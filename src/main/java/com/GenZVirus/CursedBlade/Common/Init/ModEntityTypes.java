package com.GenZVirus.CursedBlade.Common.Init;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Entities.FlamesOfUndoingEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES,
			CursedBlade.MOD_ID);

	public static final RegistryObject<EntityType<FlamesOfUndoingEntity>> FLAMES_OF_UNDOING = ENTITY_TYPES.register("flamesofundoing",
			() -> EntityType.Builder.<FlamesOfUndoingEntity>create(FlamesOfUndoingEntity::new, EntityClassification.MISC)
					.size(0.1F, 0.1F)
					.build(new ResourceLocation(CursedBlade.MOD_ID, "flamesofundoing").toString()));
	
}
