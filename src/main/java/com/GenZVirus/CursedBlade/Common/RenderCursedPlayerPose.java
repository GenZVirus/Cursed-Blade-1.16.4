package com.GenZVirus.CursedBlade.Common;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;

public class RenderCursedPlayerPose {

	public static void renderPlayer(AbstractClientPlayerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn,
			BipedModel<AbstractClientPlayerEntity> model, List<LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>> layerRenderers) {
		matrixStackIn.push();
		model.swingProgress = entityIn.getSwingProgress(partialTicks);
		boolean shouldSit = entityIn.isPassenger() && (entityIn.getRidingEntity() != null && entityIn.getRidingEntity().shouldRiderSit());
		model.isSitting = shouldSit;
		model.isChild = entityIn.isChild();
		float f = MathHelper.interpolateAngle(partialTicks, entityIn.prevRenderYawOffset, entityIn.renderYawOffset);
		float f1 = MathHelper.interpolateAngle(partialTicks, entityIn.prevRotationYawHead, entityIn.rotationYawHead);
		float f2 = f1 - f;
		if (shouldSit && entityIn.getRidingEntity() instanceof LivingEntity) {
			LivingEntity livingentity = (LivingEntity) entityIn.getRidingEntity();
			f = MathHelper.interpolateAngle(partialTicks, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
			f2 = f1 - f;
			float f3 = MathHelper.wrapDegrees(f2);
			if (f3 < -85.0F) {
				f3 = -85.0F;
			}

			if (f3 >= 85.0F) {
				f3 = 85.0F;
			}

			f = f1 - f3;
			if (f3 * f3 > 2500.0F) {
				f += f3 * 0.2F;
			}

			f2 = f1 - f;
		}

		float f6 = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);
		if (entityIn.getPose() == Pose.SLEEPING) {
			Direction direction = entityIn.getBedDirection();
			if (direction != null) {
				float f4 = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
				matrixStackIn.translate((double) ((float) (-direction.getXOffset()) * f4), 0.0D, (double) ((float) (-direction.getZOffset()) * f4));
			}
		}

		float f7 = entityIn.ticksExisted + partialTicks;
		applyRotations(entityIn, matrixStackIn, f7, f, partialTicks);
		matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
		matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
		matrixStackIn.translate(0.0D, (double) -1.501F, 0.0D);
		float f8 = 0.0F;
		float f5 = 0.0F;
		if (!shouldSit && entityIn.isAlive()) {
			f8 = MathHelper.lerp(partialTicks, entityIn.prevLimbSwingAmount, entityIn.limbSwingAmount);
			f5 = entityIn.limbSwing - entityIn.limbSwingAmount * (1.0F - partialTicks);
			if (entityIn.isChild()) {
				f5 *= 3.0F;
			}

			if (f8 > 1.0F) {
				f8 = 1.0F;
			}
		}

		model.setLivingAnimations(entityIn, f5, f8, partialTicks);
		model.setRotationAngles(entityIn, f5, f8, f7, f2, f6);
		Minecraft minecraft = Minecraft.getInstance();
		boolean flag = !entityIn.isInvisible();
		boolean flag1 = !flag && !entityIn.isInvisibleToPlayer(minecraft.player);
		boolean flag2 = minecraft.isEntityGlowing(entityIn);
		matrixStackIn.rotate(new Quaternion(0.0F, -90.0F, 0.0F, true));
//		model.bipedRightArm.rotateAngleY = (float) (Math.PI / 2);
		model.bipedRightArm.rotateAngleY = (float) (Math.PI / 2);
		model.bipedHead.rotateAngleY = (float) (Math.PI / 2);
		RenderType rendertype = func_230496_a_(entityIn, flag, flag1, flag2, model);
		if (rendertype != null) {
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
			int i = getPackedOverlay(entityIn, 0);
			model.render(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
		}

		if (!entityIn.isSpectator()) {
			for (LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> layerrenderer : layerRenderers) {
//				if (layerrenderer instanceof HeldItemLayer<?, ?>) {
//					matrixStackIn.rotate(new Quaternion(0.0F, 0.0F, -90.0F, true));
//					matrixStackIn.rotate(new Quaternion(0.0F, 90.0F, 0.0F, true));
//					matrixStackIn.translate(0.8D, -0.9D, -0.15D);
//					layerrenderer.getEntityModel().render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
//					matrixStackIn.translate(-0.8D, 0.9D, 0.15D);
//					matrixStackIn.rotate(new Quaternion(0.0F, -90.0F, 0.0F, true));
//					matrixStackIn.rotate(new Quaternion(0.0F, 0.0F, 90.0F, true));
//				} else {
					layerrenderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
//				}
			}
		}

		matrixStackIn.pop();
	}

	public static int getPackedOverlay(LivingEntity livingEntityIn, float uIn) {
		return OverlayTexture.getPackedUV(OverlayTexture.getU(uIn), OverlayTexture.getV(livingEntityIn.hurtTime > 0 || livingEntityIn.deathTime > 0));
	}

	@Nullable
	protected static RenderType func_230496_a_(AbstractClientPlayerEntity entityIn, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_, BipedModel<AbstractClientPlayerEntity> model) {
		ResourceLocation resourcelocation = entityIn.getLocationSkin();
		if (p_230496_3_) {
			return RenderType.getItemEntityTranslucentCull(resourcelocation);
		} else if (p_230496_2_) {
			return model.getRenderType(resourcelocation);
		} else {
			return p_230496_4_ ? RenderType.getOutline(resourcelocation) : null;
		}
	}

	private static float getFacingAngle(Direction facingIn) {
		switch (facingIn) {
		case SOUTH:
			return 90.0F;
		case WEST:
			return 0.0F;
		case NORTH:
			return 270.0F;
		case EAST:
			return 180.0F;
		default:
			return 0.0F;
		}
	}

	protected static void applyRotations(AbstractClientPlayerEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
		Pose pose = entityLiving.getPose();
		if (pose != Pose.SLEEPING) {
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
		}

		if (entityLiving.deathTime > 0) {
			float f = ((float) entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
			f = MathHelper.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}

			matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f * 90.0F));
		} else if (entityLiving.isSpinAttacking()) {
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0F - entityLiving.rotationPitch));
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(((float) entityLiving.ticksExisted + partialTicks) * -75.0F));
		} else if (pose == Pose.SLEEPING) {
			Direction direction = entityLiving.getBedDirection();
			float f1 = direction != null ? getFacingAngle(direction) : rotationYaw;
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f1));
			matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0F));
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(270.0F));
		} else if (entityLiving.hasCustomName() || entityLiving instanceof PlayerEntity) {
			String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName().getString());
			if (("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof PlayerEntity) || ((PlayerEntity) entityLiving).isWearing(PlayerModelPart.CAPE))) {
				matrixStackIn.translate(0.0D, (double) (entityLiving.getHeight() + 0.1F), 0.0D);
				matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
			}
		}

	}

}
