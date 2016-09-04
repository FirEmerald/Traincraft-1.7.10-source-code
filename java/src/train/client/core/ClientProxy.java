package src.train.client.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import src.train.client.core.handlers.ClientTickHandler;
import src.train.client.core.handlers.TCKeyHandler;
import src.train.client.core.handlers.RecipeBookHandler;
import src.train.client.core.helpers.HolidayHelper;
import src.train.client.gui.GuiBuilder;
import src.train.client.gui.GuiCrafterTier;
import src.train.client.gui.GuiCraftingCart;
import src.train.client.gui.GuiDistil;
import src.train.client.gui.GuiForney;
import src.train.client.gui.GuiFreight;
import src.train.client.gui.GuiFurnaceCart;
import src.train.client.gui.GuiGeneratorDiesel;
import src.train.client.gui.GuiJukebox;
import src.train.client.gui.GuiLantern;
import src.train.client.gui.GuiLiquid;
import src.train.client.gui.GuiLoco2;
import src.train.client.gui.GuiOpenHearthFurnace;
import src.train.client.gui.GuiRecipeBook;
import src.train.client.gui.GuiTender;
import src.train.client.gui.GuiTrainCraftingBlock;
import src.train.client.gui.GuiZepp;
import src.train.client.render.ItemRenderBridgePillar;
import src.train.client.render.ItemRenderGeneratorDiesel;
import src.train.client.render.ItemRenderLantern;
import src.train.client.render.ItemRenderSignal;
import src.train.client.render.ItemRenderStopper;
import src.train.client.render.ItemRenderWaterWheel;
import src.train.client.render.ItemRenderWindMill;
import src.train.client.render.RenderBogie;
import src.train.client.render.RenderBridgePillar;
import src.train.client.render.RenderGeneratorDiesel;
import src.train.client.render.RenderLantern;
import src.train.client.render.RenderRollingStock;
import src.train.client.render.RenderRotativeDigger;
import src.train.client.render.RenderRotativeWheel;
import src.train.client.render.RenderSignal;
import src.train.client.render.RenderStopper;
import src.train.client.render.RenderTCRail;
import src.train.client.render.RenderWaterWheel;
import src.train.client.render.RenderWindMill;
import src.train.client.render.RenderZeppelins;
import src.train.common.Traincraft;
import src.train.common.api.EntityBogie;
import src.train.common.api.EntityRollingStock;
import src.train.common.core.CommonProxy;
import src.train.common.core.handlers.ConfigHandler;
import src.train.common.entity.digger.EntityRotativeDigger;
import src.train.common.entity.digger.EntityRotativeWheel;
import src.train.common.entity.rollingStock.EntityJukeBoxCart;
import src.train.common.entity.zeppelin.EntityZeppelinOneBalloon;
import src.train.common.entity.zeppelin.EntityZeppelinTwoBalloons;
import src.train.common.library.BlockData;
import src.train.common.library.GuiIDs;
import src.train.common.library.Info;
import src.train.common.tile.TileBridgePillar;
import src.train.common.tile.TileCrafterTierI;
import src.train.common.tile.TileCrafterTierII;
import src.train.common.tile.TileCrafterTierIII;
import src.train.common.tile.TileEntityDistil;
import src.train.common.tile.TileEntityOpenHearthFurnace;
import src.train.common.tile.TileGeneratorDiesel;
import src.train.common.tile.TileLantern;
import src.train.common.tile.TileSignal;
import src.train.common.tile.TileStopper;
import src.train.common.tile.TileTCRail;
import src.train.common.tile.TileTrainWbench;
import src.train.common.tile.TileWaterWheel;
import src.train.common.tile.TileWindMill;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

	@Override
	public void isHoliday() {
		HolidayHelper helper = new HolidayHelper();
		helper.setDaemon(true);
		helper.start();
	}
	
	@Override
	public void registerKeyBindingHandler()
	{
		FMLCommonHandler.instance().bus().register(new TCKeyHandler());
	}

	@Override
	public void registerRenderInformation() {
		FMLCommonHandler.instance().bus().register(new ClientTickHandler());

		RenderingRegistry.registerEntityRenderingHandler(EntityRollingStock.class, new RenderRollingStock());
		RenderingRegistry.registerEntityRenderingHandler(EntityZeppelinTwoBalloons.class, new RenderZeppelins());
		RenderingRegistry.registerEntityRenderingHandler(EntityZeppelinOneBalloon.class, new RenderZeppelins());
		RenderingRegistry.registerEntityRenderingHandler(EntityRotativeDigger.class, new RenderRotativeDigger());
		RenderingRegistry.registerEntityRenderingHandler(EntityRotativeWheel.class, new RenderRotativeWheel());
		//bogies
		RenderingRegistry.registerEntityRenderingHandler(EntityBogie.class, new RenderBogie());

		ClientRegistry.bindTileEntitySpecialRenderer(TileStopper.class, new RenderStopper());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockData.stopper.block), new ItemRenderStopper());
		
		//ClientRegistry.bindTileEntitySpecialRenderer(TileBook.class, new RenderTCBook());
		//MinecraftForgeClient.registerItemRenderer(BlockIDs.book.blockID, new ItemRenderBook());

		ClientRegistry.bindTileEntitySpecialRenderer(TileSignal.class, new RenderSignal());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockData.signal.block), new ItemRenderSignal());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileLantern.class, new RenderLantern());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockData.lantern.block), new ItemRenderLantern());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileWaterWheel.class, new RenderWaterWheel());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockData.waterWheel.block), new ItemRenderWaterWheel());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileWindMill.class, new RenderWindMill());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockData.windMill.block), new ItemRenderWindMill());

		ClientRegistry.bindTileEntitySpecialRenderer(TileGeneratorDiesel.class, new RenderGeneratorDiesel());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockData.generatorDiesel.block), new ItemRenderGeneratorDiesel());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileTCRail.class, new RenderTCRail());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileBridgePillar.class, new RenderBridgePillar());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockData.bridgePillar.block), new ItemRenderBridgePillar());
	}

	@Override
	public int addArmor(String armor) {
		return RenderingRegistry.addNewArmourRendererPrefix(armor);
	}

	@Override
	public GuiScreen getCurrentScreen() {
		return Minecraft.getMinecraft().currentScreen;
	}
	@Override
	public void registerVillagerSkin(int villagerId, String textureName) {
		VillagerRegistry.instance().registerVillagerSkin(villagerId, new ResourceLocation(Info.resourceLocation,Info.villagerPrefix + textureName));
	}
	
	@Override
	public void registerBookHandler() {
		RecipeBookHandler recipeBookHandler = new RecipeBookHandler();
	}

	@Override
	public Minecraft getMinecraft() {
		return Minecraft.getMinecraft();
	}
	
	@Override
	public EntityPlayer getPlayer() {
		return getMinecraft().thePlayer;
	}
	
	@Override
	public void doNEICheck(ItemStack item)
	{
		for (ModContainer mod : Loader.instance().getModList())
		{
            if (mod.getModId().equals("NotEnoughItems"))
            {
                codechicken.nei.api.API.hideItem(item);
                return;
            }
		}
		if (Minecraft.getMinecraft().thePlayer != null)
		{
            Iterator modsIT = Loader.instance().getModList().iterator();
            ModContainer modc;
            while (modsIT.hasNext())
            {
                modc = (ModContainer) modsIT.next();
                if ("Not Enough Items".equals(modc.getName().trim()))
                {
                    codechicken.nei.api.API.hideItem(item);
                    return;
                }
            }
        }
	}
	
	@Override
	public EntityPlayer getPlayer(MessageContext ctx)
	{
		return getPlayer();
	}
	
	@Override
	public float getJukeboxVolume()
	{
		return Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.RECORDS) * Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER);
	}
}