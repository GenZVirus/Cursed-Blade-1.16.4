package com.GenZVirus.CursedBlade.Events;

import java.util.List;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.CursedBlade.CursedBladeStats;
import com.GenZVirus.CursedBlade.Common.Config;
import com.GenZVirus.CursedBlade.Common.RenderCursedPlayerPose;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseReleasedEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = CursedBlade.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ItemToolTipEventsHandler {

	public static Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public static void renderBlock(RenderHandEvent event) {
		if (event.getItemStack().getItem() instanceof CursedBladeWeapon && mc.gameSettings.keyBindUseItem.isKeyDown()) {
			event.getMatrixStack().rotate(new Quaternion(-60, 0, 0, true));
			event.getMatrixStack().translate(0, 0.6D, 0);
		}
	}
	
	@SubscribeEvent(receiveCanceled = true)
	public static void renderPlayer(RenderPlayerEvent event) {
		if(event.getPlayer().getActiveItemStack().getItem() instanceof CursedBladeWeapon) {
			event.setCanceled(true);
			RenderCursedPlayerPose.renderPlayer((AbstractClientPlayerEntity) event.getPlayer(), event.getPlayer().rotationYaw, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), event.getLight(), event.getRenderer().getEntityModel(), event.getRenderer().layerRenderers);
		}
	}

	@SubscribeEvent
	public static void ToolTip(ItemTooltipEvent event) {
		if (event.getPlayer() == null)
			return;
		if (!(event.getItemStack().getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon))
			return;

		List<ITextComponent> text = Lists.newArrayList();

		// Add Name
		text.add(new TranslationTextComponent("\u00A74" + "???"));

		// Add new line
		text.add(new TranslationTextComponent(""));

		if (CursedBladeStats.STATUS.equals("Dormant")) {
			// Add new line
			text.add(new TranslationTextComponent("\u00A7o" + "Status: " + "\u00A7r\u00A77" + CursedBladeStats.STATUS));
		} else {
			// Add new line
			text.add(new TranslationTextComponent("\u00A7o" + "Status: " + "\u00A7r\u00A7e" + CursedBladeStats.STATUS));

		}

		// Add new line
		text.add(new TranslationTextComponent(""));

		// Add Kill Count
		text.add(new TranslationTextComponent("\u00A78" + "Kill Counter: " + CursedBladeStats.KILL_COUNTER));

		// Add new line
		text.add(new TranslationTextComponent(""));

		if (CursedBladeStats.FIRE_ASPECT) {
			// Add fire aspect
			text.add(new TranslationTextComponent("\u00A76" + "Fire Aspect"));
		}

		if (CursedBladeStats.DESTROY_ABSORPTION) {
			// Add destroy absorption
			text.add(new TranslationTextComponent("\u00A71" + "Destroy Absorption"));
		}

		if (CursedBladeStats.DESTROY_SHIELDS) {
			// Add destroy shields
			text.add(new TranslationTextComponent("\u00A7f" + "Shield Breaker"));
		}

		if (CursedBladeStats.LIFE_STEAL > 0) {
			// Add life steal
			text.add(new TranslationTextComponent("\u00A7c" + "Life Steal: " + (CursedBladeStats.LIFE_STEAL * Config.COMMON.life_steal_ratio.get()) + "%"));
		}

		if (CursedBladeStats.POISON > 0) {
			// Add Poison
			text.add(new TranslationTextComponent("\u00A7a" + "Poison " + CursedBladeStats.POISON));
		}

		if (CursedBladeStats.WITHER > 0) {
			// Add Wither
			text.add(new TranslationTextComponent("\u00A78" + "Wither " + CursedBladeStats.WITHER));
		}
		
		if (CursedBladeStats.HUNGER > 0) {
			// Add Hunger
			text.add(new TranslationTextComponent("\u00A72" + "Hunger " + CursedBladeStats.HUNGER));
		}

		if (CursedBladeStats.EXHAUST > 0) {
			// Add Wither
			text.add(new TranslationTextComponent("\u00A74" + "Exhaust " + CursedBladeStats.EXHAUST));
		}

		// Add Damage
		text.add(new TranslationTextComponent("\u00A7c" + "Attack Damage: " + (CursedBladeStats.ATTACK_DAMAGE + 1)));

		event.getToolTip().clear();
		event.getToolTip().addAll(text);
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void mouseclick(MouseClickedEvent.Pre event) {
		if (mc.player == null)
			return;
		if (mc.player.isCreative())
			return;
		if (event.getGui() instanceof ContainerScreen) {
			if (((ContainerScreen<?>) event.getGui()).getSlotUnderMouse() != null) {
				if (((ContainerScreen<?>) event.getGui()).getSlotUnderMouse().getStack().getItem() instanceof CursedBladeWeapon) {
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void mouseclick(MouseReleasedEvent.Pre event) {
		if (mc.player == null)
			return;
		if (mc.player.isCreative())
			return;
		if (event.getGui() instanceof ContainerScreen) {
			if (((ContainerScreen<?>) event.getGui()).getSlotUnderMouse() != null) {
				if (((ContainerScreen<?>) event.getGui()).getSlotUnderMouse().getStack().getItem() instanceof CursedBladeWeapon) {
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void keyPressed(KeyboardKeyPressedEvent.Pre event) {
		if (mc.player == null)
			return;
		if (mc.player.isCreative())
			return;
		if (event.getGui() instanceof ContainerScreen) {
			if (((ContainerScreen<?>) event.getGui()).getSlotUnderMouse() != null) {
				if (((ContainerScreen<?>) event.getGui()).getSlotUnderMouse().getStack().getItem() instanceof CursedBladeWeapon) {
					event.setCanceled(true);
				}
				if (event.getKeyCode() == 49) {
					event.setCanceled(true);
				}
			}
		}
	}
}
