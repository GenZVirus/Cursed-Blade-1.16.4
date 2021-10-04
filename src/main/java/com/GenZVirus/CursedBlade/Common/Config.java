package com.GenZVirus.CursedBlade.Common;

import org.apache.commons.lang3.tuple.Pair;

import com.GenZVirus.CursedBlade.CursedBlade;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = CursedBlade.MOD_ID, bus = Bus.MOD)
public abstract class Config {

	public static class Client {

	}

	public static class Common {

		public final IntValue starting_attack_damage;
		public final DoubleValue damage_ratio;
		public final DoubleValue life_steal_ratio;
		public final BooleanValue destroyAbsorption;
		public final IntValue dropChance;
		public final IntValue whenToUpgrade;
		public final IntValue whenToBreakBlocks;
		public final BooleanValue testerMode;

		public Common(ForgeConfigSpec.Builder builder) {

			// Active Abilities

			builder.push("Cursed Blade");

			starting_attack_damage = builder.comment("Starting damage of the cursed blade").worldRestart().defineInRange("Starting Damage", 6, 0, Integer.MAX_VALUE);
			damage_ratio = builder.comment("The rate at which the damage scales per kill").worldRestart().defineInRange("Damage Ratio", 0.5D, 0.0D, 1.0D);
			dropChance = builder.comment("The drop chance is calculated like this: 1 / Drop Chance.").worldRestart().defineInRange("Drop Chance", 10000, 1, Integer.MAX_VALUE);
			whenToUpgrade = builder.comment("Upgrades are made every X amount of kills. Change the value to change when the upgrades are made.").worldRestart().defineInRange("When to Upgrade", 100, 1, 100000);
			life_steal_ratio  = builder.comment("The rate at which the life steal scales pe bonus upgrade").worldRestart().defineInRange("Life Steal Ratio", 0.1D, 0.0D, 100.0D);
			destroyAbsorption = builder.worldRestart().define("Destroy all absorption on hit", true);
			whenToBreakBlocks = builder.comment("After reaching the following amount of damage, the blade will destroy blocks when hitting them, but will not drop.").worldRestart().defineInRange("When to break blocks", 500, 1, 100000);
			testerMode = builder.worldRestart().comment("Set Tester Mode to true if you want to enable tester commands").define("Tester Mode", false);
			builder.pop();
		}

	}

	public static class Server {

		public Server(ForgeConfigSpec.Builder builder) {

		}

	}

//	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final ForgeConfigSpec COMMON_SPEC;
//	public static final ForgeConfigSpec SERVER_SPEC;
//	public static final Client CLIENT;
	public static final Common COMMON;
//	public static final Server SERVER;

	static {
//		final Pair<Client, ForgeConfigSpec> specPairClient = new ForgeConfigSpec.Builder().configure(Client::new);
//		CLIENT_SPEC = specPairClient.getRight();
//		CLIENT = specPairClient.getLeft();
		final Pair<Common, ForgeConfigSpec> specPairCommon = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPairCommon.getRight();
		COMMON = specPairCommon.getLeft();
//		final Pair<Server, ForgeConfigSpec> specPairServer = new ForgeConfigSpec.Builder().configure(Server::new);
//		SERVER_SPEC = specPairServer.getRight();
//		SERVER = specPairServer.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading event) {
//		((CursedBladeWeapon)ItemInit.CURSED_BLADE.get()).reload();
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfig.Reloading event) {

	}

}
