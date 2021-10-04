package com.GenZVirus.CursedBlade.Common.Commands;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Network.PacketHandlerCommon;
import com.GenZVirus.CursedBlade.Common.Network.Packets.SendBladeCoords;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.fml.network.NetworkDirection;

public class SearchCursedPlayer {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("search_blade").requires(context -> {
			return context.hasPermissionLevel(0);
		}).executes(source -> {
			return search(source.getSource());
		}));
	}

	private static int search(CommandSource source) {
		try {
			if (!source.asPlayer().world.isRemote() && CursedBlade.CURSED_PLAYER != null) {
				String dimension = CursedBlade.CURSED_PLAYER.world.getDimensionKey().getLocation().getPath().replace('_', ' ');
				PacketHandlerCommon.INSTANCE.sendTo(
						new SendBladeCoords((int) CursedBlade.CURSED_PLAYER.getPosX(), 
											(int) CursedBlade.CURSED_PLAYER.getPosY(), 
											(int) CursedBlade.CURSED_PLAYER.getPosZ(),
											(dimension.substring(0, 1).toUpperCase() + dimension.substring(1))),
											source.asPlayer().connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
			}
		} catch (CommandSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

}
