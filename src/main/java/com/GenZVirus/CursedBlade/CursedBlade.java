package com.GenZVirus.CursedBlade;

import java.io.File;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.GenZVirus.CursedBlade.Client.Entity.Renderer.FlamesOfUndoingRender;
import com.GenZVirus.CursedBlade.Common.Config;
import com.GenZVirus.CursedBlade.Common.Commands.ResetBladeStats;
import com.GenZVirus.CursedBlade.Common.Commands.SearchCursedPlayer;
import com.GenZVirus.CursedBlade.Common.Commands.SetCursedPlayer;
import com.GenZVirus.CursedBlade.Common.Commands.SetKillCount;
import com.GenZVirus.CursedBlade.Common.Commands.UpgradeCursedBlade;
import com.GenZVirus.CursedBlade.Common.Init.EffectsInit;
import com.GenZVirus.CursedBlade.Common.Init.ItemInit;
import com.GenZVirus.CursedBlade.Common.Init.ModEntityTypes;
import com.GenZVirus.CursedBlade.Common.Network.PacketHandlerCommon;
import com.GenZVirus.CursedBlade.Common.Network.Packets.SendCursedPlayerData;
import com.GenZVirus.CursedBlade.File.XMLFileJava;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("cursedblade")
public class CursedBlade {

	public static ServerPlayerEntity CURSED_PLAYER = null;
	public static UUID PLAYER_UUID = new UUID(0, 0);

	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	/*
	 * Mod id reference
	 */
	public static final String MOD_ID = "cursedblade";

	/*
	 * Instance of Broken Events
	 */
	public static CursedBlade instance;

	public CursedBlade() {
		File folder = new File("config/CursedBlade/");
		if (!folder.exists()) {
			try {
				folder.mkdir();
			} catch (Exception e) {
				LOGGER.debug("Failed to create config directory");
			}
		}
		ModLoadingContext.get().registerConfig(Type.COMMON, Config.COMMON_SPEC, "CursedBlade/Configs.toml");
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::doClientStuff);

		// Register custom items

		ItemInit.ITEMS.register(modEventBus);

		// Register custom effects
		EffectsInit.EFFECTS.register(modEventBus);
		LOGGER.info("Items loaded successfully");

		ModEntityTypes.ENTITY_TYPES.register(modEventBus);

		instance = this;
		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		// Initializing the PacketHandler

		PacketHandlerCommon.init();
		LOGGER.info("Packets loaded successfully");
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.FLAMES_OF_UNDOING.get(), FlamesOfUndoingRender::new);
	}

	@SuppressWarnings("resource")
	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		XMLFileJava.default_xmlFilePath = event.getServer().getWorld(World.OVERWORLD).getSavedData().folder.getPath() + "/CursedBlade/database.xml";
		CommandDispatcher<CommandSource> cmdDispatcher = event.getServer().getCommandManager().getDispatcher();
		SearchCursedPlayer.register(cmdDispatcher);
		SetKillCount.register(cmdDispatcher);
		SetCursedPlayer.register(cmdDispatcher);
		SetCursedPlayer.registerNullVarient(cmdDispatcher);
		UpgradeCursedBlade.register(cmdDispatcher);
		ResetBladeStats.register(cmdDispatcher);
	}

	@SubscribeEvent
	public void onServerClosing(FMLServerStoppingEvent event) {
		CURSED_PLAYER = null;
		PLAYER_UUID = new UUID(0, 0);
		CursedBladeStats.reset();
	}

	public static class CursedBladeStats {
		public static int KILL_COUNTER = 0;
		public static int ATTACK_DAMAGE = 0;
		public static String STATUS = "Dormant";

		public static int LIFE_STEAL = 0;
		public static boolean DESTROY_SHIELDS = false;
		public static boolean DESTROY_ABSORPTION = false;
		public static boolean FIRE_ASPECT = false;
		public static int POISON = 0;
		public static int WITHER = 0;
		public static int HUNGER = 0;
		public static int EXHAUST = 0;

		public static void reset() {
			KILL_COUNTER = 0;
			ATTACK_DAMAGE = 0;
			STATUS = "Dormant";

			LIFE_STEAL = 0;
			DESTROY_SHIELDS = false;
			DESTROY_ABSORPTION = false;
			FIRE_ASPECT = false;
			POISON = 0;
			WITHER = 0;
			HUNGER = 0;
			EXHAUST = 0;
		}
		
		public static void changeCursedPlayerTo(ServerPlayerEntity player) {
			if(CursedBlade.CURSED_PLAYER != null) {
				CursedBlade.CURSED_PLAYER = null;
				CursedBlade.PLAYER_UUID = new UUID(0, 0);
				XMLFileJava.save();
			}
			
			if(player == null) {
				CursedBlade.CURSED_PLAYER = null;
				CursedBlade.PLAYER_UUID = new UUID(0, 0);
				XMLFileJava.save();
			} else {
				CursedBlade.CURSED_PLAYER = player;
				CursedBlade.PLAYER_UUID = player.getUniqueID();
				XMLFileJava.save();
				PacketHandlerCommon.INSTANCE.sendTo(new SendCursedPlayerData(CursedBladeStats.KILL_COUNTER, CursedBladeStats.ATTACK_DAMAGE, CursedBladeStats.LIFE_STEAL,
						CursedBladeStats.DESTROY_ABSORPTION, CursedBladeStats.DESTROY_SHIELDS, CursedBladeStats.FIRE_ASPECT, CursedBladeStats.POISON, CursedBladeStats.WITHER, CursedBladeStats.STATUS,
						CursedBladeStats.HUNGER, CursedBladeStats.EXHAUST), CursedBlade.CURSED_PLAYER.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
			}
		}
	}
}
