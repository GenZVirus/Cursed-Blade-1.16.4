package com.GenZVirus.CursedBlade.Common.Commands;

import javax.annotation.Nullable;

import com.GenZVirus.CursedBlade.CursedBlade.CursedBladeStats;
import com.GenZVirus.CursedBlade.Common.Config;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class SetCursedPlayer {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("set_cursed_player").requires(context -> {
			return Config.COMMON.testerMode.get();
		}).then(Commands.argument("target", EntityArgument.player()).executes(source -> {
			return setPlayer(EntityArgument.getPlayer(source, "target"));
		})));
	}

	public static void registerNullVarient(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("no_cursed_player").requires(context -> {
			return Config.COMMON.testerMode.get();
		}).executes(source -> {
			return setPlayer(null);
		}));
	}

	private static int setPlayer(@Nullable PlayerEntity player) {
		if (player instanceof ServerPlayerEntity) {
			CursedBladeStats.changeCursedPlayerTo((ServerPlayerEntity) player);
		}
		return 1;
	}

}
