package com.GenZVirus.CursedBlade.Common.Network;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Network.Packets.SendBladeCoords;
import com.GenZVirus.CursedBlade.Common.Network.Packets.SendCursedPlayerData;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandlerCommon {

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(CursedBlade.MOD_ID, "spell"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void init() {
		int id = 0;

		INSTANCE.messageBuilder(SendCursedPlayerData.class, id++).encoder(SendCursedPlayerData::encode).decoder(SendCursedPlayerData::decode).consumer(SendCursedPlayerData::handle).add();
		INSTANCE.messageBuilder(SendBladeCoords.class, id++).encoder(SendBladeCoords::encode).decoder(SendBladeCoords::decode).consumer(SendBladeCoords::handle).add();
	}
}
