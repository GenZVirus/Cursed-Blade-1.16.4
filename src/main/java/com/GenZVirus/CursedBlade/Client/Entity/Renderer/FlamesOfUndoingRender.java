package com.GenZVirus.CursedBlade.Client.Entity.Renderer;

import java.util.Random;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Entities.FlamesOfUndoingEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;

public class FlamesOfUndoingRender extends EntityRenderer<FlamesOfUndoingEntity> {

	private final ModelRenderer bb_main;
	private final ResourceLocation texture = new ResourceLocation(CursedBlade.MOD_ID, "textures/items/cb_beam.png");

	public FlamesOfUndoingRender(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		bb_main = new ModelRenderer(16, 16, 0, 0);
		bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
		bb_main.setTextureOffset(0, 0).addBox(0, 0, 0.0F, 0.0F, 1.0F, 1600.0F, 1.0F, false);
	}

	@Override
	public void render(FlamesOfUndoingEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		double offset = 1.0D;
		double pitch = entityIn.getShooter().getPitchYaw().x;
		double yaw = entityIn.getShooter().getPitchYaw().y;
		double pitchRadian = pitch * (Math.PI / 180); // X rotation
		double yawRadian = yaw * (Math.PI / 180); // Y rotation
		double newPosX = offset * -Math.sin(yawRadian) * Math.cos(pitchRadian);
		double newPosY = offset * -Math.sin(pitchRadian);
		double newPosZ = offset * Math.cos(yawRadian) * Math.cos(pitchRadian);
		Random rand = new Random();
		for (int i = 0; i < 5; i++) {
			int randOffset = rand.nextInt(100);
			entityIn.world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, true, entityIn.getPosX() + newPosX * randOffset, entityIn.getPosY() + newPosY * randOffset,
					entityIn.getPosZ() + newPosZ * randOffset, 0, 0.01F, 0);
		}
		bb_main.rotateAngleX = (float) (entityIn.rotationPitch * Math.PI / 180.0F);
		bb_main.rotateAngleY = -(float) (entityIn.rotationYaw * Math.PI / 180.0F);
		bb_main.render(matrixStackIn, bufferIn.getBuffer(RenderType.getBeaconBeam(texture, false)), 16777215, 0);
	}

	@Override
	public ResourceLocation getEntityTexture(FlamesOfUndoingEntity entity) {
		return null;
	}

}
