package src.train.common.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import src.train.common.Traincraft;
import src.train.common.api.AbstractTrains;
import src.train.common.api.EntityRollingStock;
import src.train.common.api.Freight;
import src.train.common.api.LiquidTank;
import src.train.common.api.Tender;
import src.train.common.containers.ContainerDistil;
import src.train.common.containers.ContainerGeneratorDiesel;
import src.train.common.containers.ContainerOpenHearthFurnace;
import src.train.common.containers.ContainerTier;
import src.train.common.containers.ContainerTrainWorkbench;
import src.train.common.containers.ContainerWorkbenchCart;
import src.train.common.core.handlers.ServerTickHandler;
import src.train.common.core.util.MP3Player;
import src.train.common.entity.digger.EntityRotativeDigger;
import src.train.common.entity.rollingStock.EntityJukeBoxCart;
import src.train.common.entity.rollingStock.EntityTracksBuilder;
import src.train.common.entity.zeppelin.AbstractZeppelin;
import src.train.common.inventory.InventoryBuilder;
import src.train.common.inventory.InventoryForney;
import src.train.common.inventory.InventoryFreight;
import src.train.common.inventory.InventoryJukeBoxCart;
import src.train.common.inventory.InventoryLiquid;
import src.train.common.inventory.InventoryLoco;
import src.train.common.inventory.InventoryRotativeDigger;
import src.train.common.inventory.InventoryTender;
import src.train.common.inventory.InventoryWorkCart;
import src.train.common.inventory.InventoryZepp;
import src.train.common.library.GuiIDs;
import src.train.common.tile.TileBook;
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
import src.train.common.tile.TileTCRailGag;
import src.train.common.tile.TileTrainWbench;
import src.train.common.tile.TileWaterWheel;
import src.train.common.tile.TileWindMill;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy
{
	public static List<MP3Player> playerList = new ArrayList();
	
	public void registerKeyBindingHandler() {}

	public void registerRenderInformation() {}

	public void registerTileEntities() {
		FMLCommonHandler.instance().bus().register(new ServerTickHandler());
		GameRegistry.registerTileEntity(TileCrafterTierI.class, "TileCrafterTierI");
		GameRegistry.registerTileEntity(TileCrafterTierII.class, "TileCrafterTierII");
		GameRegistry.registerTileEntity(TileCrafterTierIII.class, "TileCrafterTierIII");
		GameRegistry.registerTileEntity(TileTrainWbench.class, "TileTrainWbench");
		GameRegistry.registerTileEntity(TileEntityDistil.class, "Tile Distil");
		GameRegistry.registerTileEntity(TileEntityOpenHearthFurnace.class, "Tile OpenHearthFurnace");
		GameRegistry.registerTileEntity(TileStopper.class, "TileStopper");
		GameRegistry.registerTileEntity(TileSignal.class, "TileSignal");
		GameRegistry.registerTileEntity(TileLantern.class, "tileLantern");
		GameRegistry.registerTileEntity(TileWaterWheel.class, "tileWaterWheel");
		GameRegistry.registerTileEntity(TileWindMill.class, "tileWindMill");
		GameRegistry.registerTileEntity(TileGeneratorDiesel.class, "tileGeneratorDiesel");
		GameRegistry.registerTileEntity(TileBook.class, "tileBook");
		GameRegistry.registerTileEntity(TileTCRailGag.class, "tileTCRailGag");
		GameRegistry.registerTileEntity(TileTCRail.class, "tileTCRail");
		GameRegistry.registerTileEntity(TileBridgePillar.class, "tileTCBridgePillar");
	}
	public void registerChunkHandler(Traincraft instance){
		ForgeChunkManager.setForcedChunkLoadingCallback(instance, new locoChunkloadCallback());
	}
	
	public class locoChunkloadCallback implements ForgeChunkManager.OrderedLoadingCallback {

		@Override
		public void ticketsLoaded(List<Ticket> tickets, World world) {
			for (Ticket ticket : tickets) {
				int locoID = ticket.getModData().getInteger("locoID");
				Entity entity = world.getEntityByID(locoID);
				if(entity!=null && entity instanceof AbstractTrains){
					((AbstractTrains)entity).forceChunkLoading(ticket);
				}
			}
		}

		@Override
		public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount) {
			List<Ticket> validTickets = Lists.newArrayList();
			for (Ticket ticket : tickets) {
				int locoID = ticket.getModData().getInteger("locoID");
				Entity entity = world.getEntityByID(locoID);
				if(entity!=null && entity instanceof AbstractTrains){
					validTickets.add(ticket);
				}
			}
			return validTickets;
		}
	}

	public int addArmor(String armor) {
		return 0;
	}

	public Minecraft getClientInstance() {
		return FMLClientHandler.instance().getClient();
	}
	public GuiScreen getCurrentScreen() {
		return null;
	}

	public void registerTextureFX() {}
	
	public void registerBookHandler() {}
    
	public void registerBlock(Block block) {
        GameRegistry.registerBlock(block, block.getUnlocalizedName().replace("tile.", ""));
	}
	
	public void registerBlock(Block block, Class<? extends ItemBlock> item) {
		GameRegistry.registerBlock(block, item, block.getUnlocalizedName().replace("tile.", ""));
    }

	public Minecraft getMinecraft() {
		return null;
	}

	public void registerVillagerSkin(int villagerId, String textureName) {}
	
	public static void killAllStreams() {
		for (MP3Player p : playerList) {
			p.stop();
		}
	}
	
	public static boolean checkJukeboxEntity(World world, int id) {
		if (world.getEntityByID(id)!=null) {
			return true;
		}
		return false;
	}
	
	public void isHoliday() {}

	public void doNEICheck(ItemStack item) {}
	
	public EntityPlayer getPlayer() {
		return null;
	}

	public EntityPlayer getPlayer(MessageContext ctx)
	{
		return ctx.getServerHandler().playerEntity;
	}

	public float getJukeboxVolume()
	{
		return 0;
	}
}