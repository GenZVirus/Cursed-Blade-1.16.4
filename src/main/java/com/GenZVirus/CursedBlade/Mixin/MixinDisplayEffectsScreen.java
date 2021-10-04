package com.GenZVirus.CursedBlade.Mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Effects.CurseEffect;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@Mixin(DisplayEffectsScreen.class)
public abstract class MixinDisplayEffectsScreen {
	private ResourceLocation INVENTORY_BACKGROUND2 = new ResourceLocation("textures/gui/container/inventory.png");
	private ResourceLocation CB_CURSE_EFFECT_BACKGROUND = new ResourceLocation(CursedBlade.MOD_ID, "textures/gui/cb_curse_effect_background.png");

	@SuppressWarnings({ "deprecation", "rawtypes" })
	@Overwrite
	private void renderEffectBackground(MatrixStack matrixStack, int p_238810_2_, int p_238810_3_, Iterable<EffectInstance> effects) {
		((DisplayEffectsScreen)(Object)this).getMinecraft().getTextureManager().bindTexture(INVENTORY_BACKGROUND2);
		int i = ((DisplayEffectsScreen)(Object)this).getGuiTop();

		for (EffectInstance effectinstance : effects) {
			if (effectinstance.getPotion() instanceof CurseEffect) {
				((DisplayEffectsScreen)(Object)this).getMinecraft().getTextureManager().bindTexture(CB_CURSE_EFFECT_BACKGROUND);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				AbstractGui.blit(matrixStack, p_238810_2_, i, 0, 0, 120, 32, 120, 32);
				((DisplayEffectsScreen)(Object)this).getMinecraft().getTextureManager().bindTexture(INVENTORY_BACKGROUND2);
			} else {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				AbstractGui.blit(matrixStack, p_238810_2_, i, 0, 166, 140, 32, 256, 256);
			}
			i += p_238810_3_;
		}

	}
}
