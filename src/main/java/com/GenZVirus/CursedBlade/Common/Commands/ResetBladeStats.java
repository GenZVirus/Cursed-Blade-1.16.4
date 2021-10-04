package com.GenZVirus.CursedBlade.Common.Commands;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.CursedBlade.CursedBladeStats;
import com.GenZVirus.CursedBlade.Common.Config;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon;
import com.GenZVirus.CursedBlade.Common.Network.PacketHandlerCommon;
import com.GenZVirus.CursedBlade.Common.Network.Packets.SendCursedPlayerData;
import com.GenZVirus.CursedBlade.File.XMLFileJava;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.fml.network.NetworkDirection;

public class ResetBladeStats {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("reset_blade_stats").requires(context -> {
			return Config.COMMON.testerMode.get();
		}).executes(source -> {
			return reset();
		}));
	}

	private static int reset() {
		CursedBladeStats.reset();
		XMLFileJava.save();
		((CursedBladeWeapon) CursedBlade.CURSED_PLAYER.inventory.getStackInSlot(0).getItem()).reload();
		PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(CursedBladeStats.KILL_COUNTER, CursedBladeStats.ATTACK_DAMAGE, CursedBladeStats.LIFE_STEAL, CursedBladeStats.DESTROY_ABSORPTION,
				CursedBladeStats.DESTROY_SHIELDS, CursedBladeStats.FIRE_ASPECT, CursedBladeStats.POISON, CursedBladeStats.WITHER, CursedBladeStats.STATUS, CursedBladeStats.HUNGER,
				CursedBladeStats.EXHAUST), CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		return 1;
	}

}
