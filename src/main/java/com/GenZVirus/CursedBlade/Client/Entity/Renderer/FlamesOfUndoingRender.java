package com.GenZVirus.CursedBlade.Client.Entity.Renderer;

import java.util.Random;

import com.GenZVirus.CursedBlade.Common.Entities.FlamesOfUndoingEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;

public class FlamesOfUndoingRender extends EntityRenderer<FlamesOfUndoingEntity> {

	public FlamesOfUndoingRender(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void render(FlamesOfUndoingEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

		double offset1,offset2,offset3;

		double x = entityIn.getPosX();
		double y = entityIn.getPosY() + 1.0D;
		double z = entityIn.getPosZ();

		Random rand = new Random();
		
		for (int i = 0; i < 30; i++) {
			offset1 = 1 - rand.nextDouble() * 2;
			offset2 = 1 - rand.nextDouble() * 2;
			offset3 = 1 - rand.nextDouble() * 2;
			entityIn.world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, true, x + offset1, y + offset2, z + offset3, 0, 0, 0);
		}
	}

	@Override
	public ResourceLocation getEntityTexture(FlamesOfUndoingEntity entity) {
		return null;
	}

}
