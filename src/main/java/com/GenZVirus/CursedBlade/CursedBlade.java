package com.GenZVirus.CursedBlade;

import java.io.File;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.GenZVirus.CursedBlade.Client.Entity.Renderer.FlamesOfUndoingRender;
import com.GenZVirus.CursedBlade.Common.Config;
import com.GenZVirus.CursedBlade.Common.Init.ItemInit;
import com.GenZVirus.CursedBlade.Common.Init.ModEntityTypes;
import com.GenZVirus.CursedBlade.Common.Network.PacketHandlerCommon;
import com.GenZVirus.CursedBlade.File.XMLFileJava;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.dedicated.DedicatedServer;
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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("cursedblade")
public class CursedBlade {

	public static ServerPlayerEntity CURSED_PLAYER = null;
	public static UUID PLAYER_UUID;

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

	// Directly reference a log4j logger.
	@SuppressWarnings("unused")
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

		// Registering custom items

		ItemInit.ITEMS.register(modEventBus);
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

	}
}
