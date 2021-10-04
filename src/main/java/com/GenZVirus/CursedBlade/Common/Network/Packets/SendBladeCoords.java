package com.GenZVirus.CursedBlade.Common.Network.Packets;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class SendBladeCoords {

	public int x, y, z;
	public String dimension;

	public SendBladeCoords(int x, int y, int z, String dimension) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimension = dimension;
	}

	public static void encode(SendBladeCoords pkt, PacketBuffer buf) {
		buf.writeInt(pkt.x);
		buf.writeInt(pkt.y);
		buf.writeInt(pkt.z);
		buf.writeString(pkt.dimension);
	}

	public static SendBladeCoords decode(PacketBuffer buf) {
		return new SendBladeCoords(buf.readInt(), buf.readInt(), buf.readInt(), buf.readString());
	}

	public static void handle(SendBladeCoords pkt, Supplier<NetworkEvent.Context> ctx) {

		ctx.get().enqueueWork(() -> {
			if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
				printMessage(pkt.x, pkt.y, pkt.z, pkt.dimension);
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public static void printMessage(int x, int y, int z, String dimension) {
		Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("Blade location X: \u00A74" + Integer.toString(x) + "\u00A7r Y: \u00A74" + Integer.toString(y) + "\u00A7r Z: \u00A74" + Integer.toString(z) + "\u00A7r Dimension: \u00A74" + dimension));
	}
	

}
