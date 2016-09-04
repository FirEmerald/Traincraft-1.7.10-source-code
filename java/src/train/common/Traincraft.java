package src.train.common;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.logging.Logger;

import com.mojang.authlib.GameProfile;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import src.train.common.api.LiquidManager;
import src.train.common.blocks.TCBlocks;
import src.train.common.core.CommonProxy;
import src.train.common.core.CreativeTabTraincraft;
import src.train.common.core.TrainModCore;
import src.train.common.core.handlers.AchievementHandler;
import src.train.common.core.handlers.ChunkHandler;
import src.train.common.core.handlers.ConfigHandler;
import src.train.common.core.handlers.CraftingHandler;
import src.train.common.core.handlers.EntityHandler;
import src.train.common.core.handlers.FuelHandler;
import src.train.common.core.handlers.OreHandler;
import src.train.common.core.handlers.PacketHandler;
import src.train.common.core.handlers.RecipeHandler;
import src.train.common.core.handlers.TCGuiHandler;
import src.train.common.core.handlers.VillagerTraincraftHandler;
import src.train.common.generation.ComponentVillageTrainstation;
import src.train.common.generation.WorldGenWorld;
import src.train.common.items.TCItems;
import src.train.common.library.Info;
import src.train.common.mysql.mysqlLogInterface;
import src.train.common.mysql.mysqlLogger;
import src.train.common.networking.*;
import src.train.common.recipes.AssemblyTableRecipes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = Info.modID, name = Info.modName, version = Info.modVersion)
//@NetworkMod(clientSideRequired = true, serverSideRequired = true, versionBounds = "[" + Info.modVersion + "]", channels = { Info.channel }, packetHandler = PacketHandler.class, connectionHandler = KeyServerHandler.class)
public class Traincraft {

	/* TrainCraft instance */
	@Instance(Info.modID)
	public static Traincraft instance;

	/* TrainCraft proxy files */
	@SidedProxy(clientSide = "src.train.client.core.ClientProxy", serverSide = "src.train.common.core.CommonProxy")
	public static CommonProxy proxy;

	/* TrainCraft Logger */
	public static Logger tcLog = Logger.getLogger(Info.modID);

	/* Creative tab for Traincraft */
	public static CreativeTabs tcTab;

	public ArmorMaterial armor = EnumHelper.addArmorMaterial("Armor", 5, new int[] { 1, 3, 2, 1 }, 25);
	public ArmorMaterial armorCloth = EnumHelper.addArmorMaterial("TCcloth", 5, new int[] {1, 3, 2, 1}, 25);
	public ArmorMaterial armorCompositeSuit = EnumHelper.addArmorMaterial("TCsuit", 70, new int[] {5, 12, 8, 5}, 50);
	public static int trainArmor;
	public static int trainCloth;
	public static int trainCompositeSuit;

	private mysqlLogInterface logMysql = new mysqlLogger();
	public static boolean mysqlLoggerEnabled;

	public static SimpleNetworkWrapper network;
	
    public static GameProfile fakeProfile = new GameProfile(UUID.nameUUIDFromBytes("Traincraft".getBytes()), "[TrainCraft]");
    public static WeakReference<FakePlayer> fakePlayer = new WeakReference<FakePlayer>(null);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		network = NetworkRegistry.INSTANCE.newSimpleChannel("TrainMod");
		network.registerMessage(KeyPacket.Handler.class, KeyPacket.class, 0, Side.CLIENT);
		network.registerMessage(KeyPacket.Handler.class, KeyPacket.class, 1, Side.SERVER);
		network.registerMessage(setBuilderPlannedHeightPacket.Handler.class, setBuilderPlannedHeightPacket.class, 2, Side.CLIENT);
		network.registerMessage(setBuilderPlannedHeightPacket.Handler.class, setBuilderPlannedHeightPacket.class, 3, Side.SERVER);
		network.registerMessage(SetDistilLiquidPacket.Handler.class, SetDistilLiquidPacket.class, 4, Side.CLIENT);
		network.registerMessage(SetDistilLiquidPacket.Handler.class, SetDistilLiquidPacket.class, 5, Side.SERVER);
		network.registerMessage(SetGeneratorLiquidPacket.Handler.class, SetGeneratorLiquidPacket.class, 6, Side.CLIENT);
		network.registerMessage(SetGeneratorLiquidPacket.Handler.class, SetGeneratorLiquidPacket.class, 7, Side.SERVER);
		network.registerMessage(SetJukeBoxStreamingURLPacket.Handler.class, SetJukeBoxStreamingURLPacket.class, 8, Side.CLIENT);
		network.registerMessage(SetJukeBoxStreamingURLPacket.Handler.class, SetJukeBoxStreamingURLPacket.class, 9, Side.SERVER);
		network.registerMessage(SetLocoTurnedOnPacket.Handler.class, SetLocoTurnedOnPacket.class, 10, Side.CLIENT);
		network.registerMessage(SetLocoTurnedOnPacket.Handler.class, SetLocoTurnedOnPacket.class, 11, Side.SERVER);
		network.registerMessage(SetParkingBrakePacket.Handler.class, SetParkingBrakePacket.class, 12, Side.CLIENT);
		network.registerMessage(SetParkingBrakePacket.Handler.class, SetParkingBrakePacket.class, 13, Side.SERVER);
		network.registerMessage(SetRotationPacket.Handler.class, SetRotationPacket.class, 14, Side.CLIENT);
		network.registerMessage(SetRotationPacket.Handler.class, SetRotationPacket.class, 15, Side.SERVER);
		network.registerMessage(SetSlotsFilledPacket.Handler.class, SetSlotsFilledPacket.class, 16, Side.CLIENT);
		network.registerMessage(SetSlotsFilledPacket.Handler.class, SetSlotsFilledPacket.class, 17, Side.SERVER);
		network.registerMessage(SetTCRailInfoPacket.Handler.class, SetTCRailInfoPacket.class, 18, Side.CLIENT);
		network.registerMessage(SetTCRailInfoPacket.Handler.class, SetTCRailInfoPacket.class, 19, Side.SERVER);
		network.registerMessage(setTileColorPacket.Handler.class, setTileColorPacket.class, 20, Side.CLIENT);
		network.registerMessage(setTileColorPacket.Handler.class, setTileColorPacket.class, 21, Side.SERVER);
		network.registerMessage(setTrainLockedPacket.Handler.class, setTrainLockedPacket.class, 22, Side.CLIENT);
		network.registerMessage(setTrainLockedPacket.Handler.class, setTrainLockedPacket.class, 23, Side.SERVER);
		network.registerMessage(setWaterWheelMetaPacket.Handler.class, setWaterWheelMetaPacket.class, 24, Side.CLIENT);
		network.registerMessage(setWaterWheelMetaPacket.Handler.class, setWaterWheelMetaPacket.class, 25, Side.SERVER);
		network.registerMessage(SetWindMillInfoPacket.Handler.class, SetWindMillInfoPacket.class, 26, Side.CLIENT);
		network.registerMessage(SetWindMillInfoPacket.Handler.class, SetWindMillInfoPacket.class, 27, Side.SERVER);
		network.registerMessage(SetZeppelinRotationPacket.Handler.class, SetZeppelinRotationPacket.class, 28, Side.CLIENT);
		network.registerMessage(SetZeppelinRotationPacket.Handler.class, SetZeppelinRotationPacket.class, 29, Side.SERVER);
		//ForgeChunkManager.setForcedChunkLoadingCallback(instance, ChunkHandler.getInstance());
		MinecraftForge.EVENT_BUS.register(ChunkHandler.getInstance());

		/* Log */
		tcLog.info("Starting Traincraft " + Info.modVersion + "!");

		/* Config handler */
		ConfigHandler.init(new File(event.getModConfigurationDirectory(), Info.modName + ".cfg"));

		/* Register the KeyBinding Handler */
		proxy.registerKeyBindingHandler();

		/* Tile Entities */
		proxy.registerTileEntities();

		/* Rendering registration */
		proxy.registerRenderInformation();
		trainArmor = proxy.addArmor("armor");
		trainCloth = proxy.addArmor("Paintable");
		trainCompositeSuit = proxy.addArmor("CompositeSuit");
		
		/* Tab for creative items/blocks */
		tcTab = new CreativeTabTraincraft(CreativeTabs.getNextID(), "Traincraft");

		/* Ore generation */
		GameRegistry.registerWorldGenerator(new WorldGenWorld(), 1);
		MapGenStructureIO.func_143031_a(ComponentVillageTrainstation.class, "Trainstation");

		/* Track registration */
		TrainModCore.RegisterNewTracks();
		
		/*Fuel registration*/
		GameRegistry.registerFuelHandler(new FuelHandler());
		
		TCBlocks.init();
		TCItems.init();
		
		/* Register entities */
		EntityHandler.init();

		AchievementHandler.load();
		AchievementPage.registerAchievementPage(AchievementHandler.tmPage);
		
		/* Check holidays */
		proxy.isHoliday();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {

		//proxy.getCape();
		
		/* GUI handler initiation */
    	NetworkRegistry.INSTANCE.registerGuiHandler(instance, new TCGuiHandler());
    	FMLCommonHandler.instance().bus().register(new CraftingHandler());

		/* Ore dictionary */
		OreHandler.registerOres();

		/* Recipes */
		RecipeHandler.initBlockRecipes();
		RecipeHandler.initItemRecipes();
		AssemblyTableRecipes.recipes();

		/* Register the liquids */
		LiquidManager.getInstance().registerLiquids();
		
		/* Liquid FX */
		proxy.registerTextureFX();

		/* Try to load mysql */
		if (ConfigHandler.MYSQL_ENABLE)
			mysqlLoggerEnabled = logMysql.enableLogger();
		
		/*Trainman Villager*/
		VillagerRegistry.instance().registerVillagerId(ConfigHandler.TRAINCRAFT_VILLAGER_ID);
		VillagerTraincraftHandler villageHandler = new VillagerTraincraftHandler();
		VillagerRegistry.instance().registerVillageCreationHandler(villageHandler);
	    proxy.registerVillagerSkin(ConfigHandler.TRAINCRAFT_VILLAGER_ID, "station_chief.png");
	    VillagerRegistry.instance().registerVillageTradeHandler(ConfigHandler.TRAINCRAFT_VILLAGER_ID, villageHandler);
	    
	    proxy.registerBookHandler();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		proxy.registerChunkHandler(instance);
	}

	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent event) {
		TrainModCore.ModsLoaded();
		LiquidManager.getLiquidsFromDictionnary();
	}
	
	@EventHandler
	public void serverStop(FMLServerStoppedEvent event) {
		 proxy.killAllStreams();
	}
	
	public static FakePlayer getFakePlayer(WorldServer world, double x, double y, double z)
	{
		FakePlayer player = fakePlayer.get();
		if (player == null)
		{
			player = FakePlayerFactory.get(world, fakeProfile);
			fakePlayer = new WeakReference<FakePlayer>(player);
		}
		else player.worldObj = world;
		player.posX = x;
		player.posY = y;
		player.posZ = z;
		return player;
	}
}