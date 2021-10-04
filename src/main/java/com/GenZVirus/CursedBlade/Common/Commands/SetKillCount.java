package com.GenZVirus.CursedBlade.Common.Commands;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.CursedBlade.CursedBladeStats;
import com.GenZVirus.CursedBlade.Common.Config;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon;
import com.GenZVirus.CursedBlade.Common.Network.PacketHandlerCommon;
import com.GenZVirus.CursedBlade.Common.Network.Packets.SendCursedPlayerData;
import com.GenZVirus.CursedBlade.File.XMLFileJava;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.fml.network.NetworkDirection;

public class SetKillCount {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("set_kill_count").requires(context -> {
			return Config.COMMON.testerMode.get();
		}).then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE - 1)).executes(source -> {
			return setKillCount(IntegerArgumentType.getInteger(source, "amount"));
		})));
	}

	private static int setKillCount(int amount) {
		CursedBladeStats.KILL_COUNTER = amount;
		XMLFileJava.save();
		((CursedBladeWeapon)CursedBlade.CURSED_PLAYER.inventory.getStackInSlot(0).getItem()).reload();
		PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(CursedBladeStats.KILL_COUNTER, CursedBladeStats.ATTACK_DAMAGE, CursedBladeStats.LIFE_STEAL,
				CursedBladeStats.DESTROY_ABSORPTION, CursedBladeStats.DESTROY_SHIELDS, CursedBladeStats.FIRE_ASPECT, CursedBladeStats.POISON, CursedBladeStats.WITHER, CursedBladeStats.STATUS,
				CursedBladeStats.HUNGER, CursedBladeStats.EXHAUST), CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		return 1;
	}

}
