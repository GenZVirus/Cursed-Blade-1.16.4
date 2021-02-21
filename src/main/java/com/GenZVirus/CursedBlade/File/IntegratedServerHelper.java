package com.GenZVirus.CursedBlade.File;

import net.minecraft.client.Minecraft;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IntegratedServerHelper {

	public static void setIntegratedServerAddress() {
		if (Minecraft.getInstance().getIntegratedServer() != null)
			XMLFileJava.default_xmlFilePath = "saves/" + ((ServerWorldInfo) Minecraft.getInstance().getIntegratedServer().getWorlds().iterator().next().getWorldInfo()).getWorldName() + "/CursedBlade/database.xml";
	
	}
	
}
