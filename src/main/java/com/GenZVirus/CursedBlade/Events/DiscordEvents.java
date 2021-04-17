package com.GenZVirus.CursedBlade.Events;

import java.net.URI;
import java.net.URISyntaxException;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = CursedBlade.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class DiscordEvents {

	public static Minecraft mc = Minecraft.getInstance();
	public static ResourceLocation discord = new ResourceLocation(CursedBlade.MOD_ID, "textures/discord/discord.png");
	public static ResourceLocation discord_background = new ResourceLocation(CursedBlade.MOD_ID, "textures/discord/discord_background.png");

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void Discord(DrawScreenEvent.Post event) {
		if (ModList.get().isLoaded("betterux") || ModList.get().isLoaded("mobplusplus")) {
			return;
		}
		if (event.getGui() instanceof OptionsScreen && mc.world != null) {
			mc.getTextureManager().bindTexture(discord);
			RenderSystem.scalef(0.1F, 0.1F, 0.1F);
			RenderSystem.enableBlend();
			int posX = 6;
			int posY = mc.getMainWindow().getScaledHeight() - 28;
			AbstractGui.blit(new MatrixStack(), posX * 10, posY * 10, 0, 0, 0, 160, 160, 160, 160);
			RenderSystem.disableBlend();
			RenderSystem.scalef(10.0F, 10.0F, 10.0F);
			mc.fontRenderer.drawString(new MatrixStack(), "GenZVirus", posX + 28, posY + 4, 0xFFFFFFFF);
		}
	}

	@SubscribeEvent
	public static void DiscordBackground(DrawScreenEvent.Pre event) {
		if (event.getGui() instanceof InventoryScreen)
			System.out.println(DimensionType.getDimensionFolder(World.OVERWORLD, mc.getSaveLoader().getSavesDir().toFile()));
		if (ModList.get().isLoaded("betterux") || ModList.get().isLoaded("mobplusplus")) {
			return;
		}
		if (event.getGui() instanceof OptionsScreen && mc.world != null) {
			int posX = 0;
			int posY = mc.getMainWindow().getScaledHeight() - 40;
			mc.getTextureManager().bindTexture(discord_background);
			RenderSystem.enableBlend();
			AbstractGui.blit(new MatrixStack(), posX, posY, 0, 0, 0, 120, 40, 40, 120);
			RenderSystem.disableBlend();
		}
	}

	@SubscribeEvent
	public static void MenuOptions(GuiScreenEvent.InitGuiEvent.Post event) {
		if (ModList.get().isLoaded("betterux") || ModList.get().isLoaded("mobplusplus")) {
			return;
		}
		if (event.getGui() instanceof OptionsScreen && mc.world != null) {
			event.addWidget(new Button(4, mc.getMainWindow().getScaledHeight() - 30, 20, 20, new TranslationTextComponent(" "), (x) -> {
				mc.displayGuiScreen(new ConfirmOpenLinkScreen(DiscordEvents::confirmLink, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.com/invite/ty6gQaD").getValue(), false));
			}));
		}
	}

	public static void confirmLink(boolean p_confirmLink_1_) {
		if (p_confirmLink_1_) {
			try {
				openLink(new URI(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.com/invite/ty6gQaD").getValue()));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		mc.displayGuiScreen((Screen) null);
	}

	private static void openLink(URI p_openLink_1_) {
		Util.getOSType().openURI(p_openLink_1_);
	}

}
