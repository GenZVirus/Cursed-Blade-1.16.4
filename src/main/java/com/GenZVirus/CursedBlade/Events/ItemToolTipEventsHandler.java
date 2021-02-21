package com.GenZVirus.CursedBlade.Events;

import java.util.List;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Config;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseReleasedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = CursedBlade.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ItemToolTipEventsHandler {
	
	public static Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public static void heavyTitaniumArmorSet(ItemTooltipEvent event) {
		if (event.getPlayer() == null)
			return;
		if (!(event.getItemStack().getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade))
			return;

		List<ITextComponent> text = Lists.newArrayList();

		// Add Name
		text.add(new TranslationTextComponent("\u00A74" + "???"));

		// Add new line
		text.add(new TranslationTextComponent(""));

		if (CursedBlade.STATUS.equals("Dormant")) {
			// Add new line
			text.add(new TranslationTextComponent("\u00A7o" + "Status: " + "\u00A7r\u00A77" + CursedBlade.STATUS));
		} else {
			// Add new line
			text.add(new TranslationTextComponent("\u00A7o" + "Status: " + "\u00A7r\u00A7e" + CursedBlade.STATUS));

		}

		// Add new line
		text.add(new TranslationTextComponent(""));

		// Add Kill Count
		text.add(new TranslationTextComponent("\u00A78" + "Kill Counter: " + CursedBlade.KILL_COUNTER));

		// Add new line
		text.add(new TranslationTextComponent(""));

		if (CursedBlade.FIRE_ASPECT) {
			// Add fire aspect
			text.add(new TranslationTextComponent("\u00A76" + "Fire Aspect"));
		}

		if (CursedBlade.DESTROY_ABSORPTION) {
			// Add destroy absorption
			text.add(new TranslationTextComponent("\u00A71" + "Destroy Absorption"));
		}

		if (CursedBlade.DESTROY_SHIELDS) {
			// Add destroy shields
			text.add(new TranslationTextComponent("\u00A7f" + "Shield Breaker"));
		}

		if (CursedBlade.LIFE_STEAL > 0) {
			// Add life steal
			text.add(new TranslationTextComponent("\u00A7c" + "Life Steal: " + (CursedBlade.LIFE_STEAL * Config.COMMON.life_steal_ratio.get()) + "%"));
		}

		if (CursedBlade.POISON > 0) {
			// Add Poison
			text.add(new TranslationTextComponent("\u00A72" + "Poison " + CursedBlade.POISON));
		}

		if (CursedBlade.WITHER > 0) {
			// Add Wither
			text.add(new TranslationTextComponent("\u00A78" + "Wither " + CursedBlade.WITHER));
		}

		// Add Damage
		text.add(new TranslationTextComponent("\u00A7c" + "Attack Damage: " + (CursedBlade.ATTACK_DAMAGE + 1)));

		event.getToolTip().clear();
		event.getToolTip().addAll(text);
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void mouseclick(MouseClickedEvent.Pre event) {
		if(mc.player == null) return;
		if(mc.player.isCreative()) return;
		if (event.getGui() instanceof ContainerScreen) {
			if (((ContainerScreen<?>) event.getGui()).getSlotUnderMouse() != null) {
				if (((ContainerScreen<?>) event.getGui()).getSlotUnderMouse().getStack().getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade) {
					event.setCanceled(true);
				}
			}
		}
	}
	@SubscribeEvent(receiveCanceled = true)
	public static void mouseclick(MouseReleasedEvent.Pre event) {
		if(mc.player == null) return;
		if(mc.player.isCreative()) return;
		if (event.getGui() instanceof ContainerScreen) {
			if (((ContainerScreen<?>) event.getGui()).getSlotUnderMouse() != null) {
				if (((ContainerScreen<?>) event.getGui()).getSlotUnderMouse().getStack().getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade) {
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void keyPressed(KeyboardKeyPressedEvent.Pre event) {
		if(mc.player == null) return;
		if(mc.player.isCreative()) return;
		if (event.getGui() instanceof ContainerScreen) {
			if (((ContainerScreen<?>) event.getGui()).getSlotUnderMouse() != null) {
				if (((ContainerScreen<?>) event.getGui()).getSlotUnderMouse().getStack().getItem() instanceof com.GenZVirus.CursedBlade.Common.Item.CursedBlade) {
					event.setCanceled(true);
				}
				if (event.getKeyCode() == 49) {
					event.setCanceled(true);
				}
			}
		}
	}
}
