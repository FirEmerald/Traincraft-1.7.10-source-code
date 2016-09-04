package src.train.common.core.handlers;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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
import src.train.common.tile.TileCrafterTierI;
import src.train.common.tile.TileCrafterTierII;
import src.train.common.tile.TileCrafterTierIII;
import src.train.common.tile.TileEntityDistil;
import src.train.common.tile.TileEntityOpenHearthFurnace;
import src.train.common.tile.TileGeneratorDiesel;
import src.train.common.tile.TileLantern;
import src.train.common.tile.TileTrainWbench;

public class TCGuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		EntityPlayer riddenByEntity = null;
		Entity entity = null;
		entity = player.ridingEntity;
		if (player.ridingEntity != null) riddenByEntity = (EntityPlayer) entity.riddenByEntity;
		Entity entity1 = null;
		if (y == -1) entity1 = world.getEntityByID(x);
		switch (ID)
		{
		case (GuiIDs.CRAFTER_TIER_I):
			return te != null && te instanceof TileCrafterTierI ? new ContainerTier(player.inventory, (TileCrafterTierI) te) : null;
		case (GuiIDs.CRAFTER_TIER_II):
			return te != null && te instanceof TileCrafterTierII ? new ContainerTier(player.inventory, (TileCrafterTierII) te) : null;
		case (GuiIDs.CRAFTER_TIER_III):
			return te != null && te instanceof TileCrafterTierIII ? new ContainerTier(player.inventory, (TileCrafterTierIII) te) : null;
		case (GuiIDs.DISTIL):
			return te != null && te instanceof TileEntityDistil ? new ContainerDistil(player.inventory, (TileEntityDistil) te) : null;
		case (GuiIDs.GENERATOR_DIESEL):
			return te != null && te instanceof TileGeneratorDiesel ? new ContainerGeneratorDiesel(player.inventory, (TileGeneratorDiesel) te) : null;
		case (GuiIDs.OPEN_HEARTH_FURNACE):
			return te != null && te instanceof TileEntityOpenHearthFurnace ? new ContainerOpenHearthFurnace(player.inventory, (TileEntityOpenHearthFurnace) te) : null;
		case (GuiIDs.TRAIN_WORKBENCH):
			return te != null && te instanceof TileTrainWbench ? new ContainerTrainWorkbench(player.inventory, player.worldObj, (TileTrainWbench) te) : null;
		case (GuiIDs.LOCO):
			return riddenByEntity != null && entity != null ? new InventoryLoco(riddenByEntity.inventory, (EntityRollingStock) entity1) : null;
		case (GuiIDs.FORNEY):
			return riddenByEntity != null && entity != null ? new InventoryForney(player.inventory, (EntityRollingStock) entity1) : null;
		case (GuiIDs.CRAFTING_CART):
			return new ContainerWorkbenchCart(player.inventory, player.worldObj);
		case (GuiIDs.FURNACE_CART):
			return riddenByEntity != null && entity != null ? new InventoryWorkCart(player.inventory, entity1) : null;
		case (GuiIDs.ZEPPELIN):
			return riddenByEntity != null && entity != null ? new InventoryZepp(player.inventory, (AbstractZeppelin) entity1) : null;
		case (GuiIDs.DIGGER):
			return riddenByEntity != null && entity != null ? new InventoryRotativeDigger(player.inventory, (EntityRotativeDigger) entity1) : null;
			/* Stationary entities while player is not riding. */
		case (GuiIDs.FREIGHT):
			//System.out.println("Freight: " + ID + " | " + entity1.getEntityName() + " | " + x + ":" + y + ":" + z);
			return entity1 != null && entity1 instanceof Freight ? new InventoryFreight(player.inventory, (Freight) entity1) : null;
		case (GuiIDs.JUKEBOX):
			return entity1 != null && entity1 instanceof EntityJukeBoxCart ? new InventoryJukeBoxCart(player.inventory, (EntityJukeBoxCart) entity1) : null;
		case (GuiIDs.TENDER):
			return entity1 != null && entity1 instanceof Tender ? new InventoryTender(player.inventory, (Tender) entity1) : null;
		case (GuiIDs.BUILDER):
			return entity1 != null && entity1 instanceof EntityTracksBuilder ? new InventoryBuilder(player.inventory, (EntityTracksBuilder) entity1) : null;
		case (GuiIDs.LIQUID):
			return entity1 != null && entity1 instanceof LiquidTank ? new InventoryLiquid(player.inventory, (LiquidTank) entity1) : null;
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		EntityPlayer riddenByEntity = null;
		Entity entity = player.ridingEntity;
		if (player.ridingEntity != null) riddenByEntity = (EntityPlayer) entity.riddenByEntity;
		Entity entity1 = null;
		if (y == -1) entity1 = world.getEntityByID(x);
		switch (ID)
		{
		case (GuiIDs.CRAFTER_TIER_I):
			return te != null && te instanceof TileCrafterTierI ? new GuiCrafterTier(player.inventory, (TileCrafterTierI) te) : null;
		case (GuiIDs.CRAFTER_TIER_II):
			return te != null && te instanceof TileCrafterTierII ? new GuiCrafterTier(player.inventory, (TileCrafterTierII) te) : null;
		case (GuiIDs.CRAFTER_TIER_III):
			return te != null && te instanceof TileCrafterTierIII ? new GuiCrafterTier(player.inventory, (TileCrafterTierIII) te) : null;
		case (GuiIDs.DISTIL):
			return te != null && te instanceof TileEntityDistil ? new GuiDistil(player.inventory, (TileEntityDistil) te) : null;
		case (GuiIDs.GENERATOR_DIESEL):
			return te != null && te instanceof TileGeneratorDiesel ? new GuiGeneratorDiesel(player.inventory, (TileGeneratorDiesel) te) : null;
		case (GuiIDs.OPEN_HEARTH_FURNACE):
			return te != null && te instanceof TileEntityOpenHearthFurnace ? new GuiOpenHearthFurnace(player.inventory, (TileEntityOpenHearthFurnace) te) : null;
		case GuiIDs.TRAIN_WORKBENCH:
			return te != null && te instanceof TileTrainWbench ? new GuiTrainCraftingBlock(player.inventory, player.worldObj, (TileTrainWbench) te) : null;
		case (GuiIDs.LOCO):
			return riddenByEntity != null && entity != null ? new GuiLoco2(riddenByEntity.inventory, entity) : null;
		case (GuiIDs.FORNEY):
			return riddenByEntity != null && entity != null ? new GuiForney(riddenByEntity.inventory, entity) : null;
		case (GuiIDs.CRAFTING_CART):
			return riddenByEntity != null && entity != null ? new GuiCraftingCart(riddenByEntity.inventory, world) : null;
		case (GuiIDs.FURNACE_CART):
			return riddenByEntity != null && entity != null ? new GuiFurnaceCart(riddenByEntity.inventory, entity) : null;
		case (GuiIDs.ZEPPELIN):
			return riddenByEntity != null && entity != null ? new GuiZepp(riddenByEntity.inventory, entity) : null;
		case (GuiIDs.DIGGER):
			return riddenByEntity != null && entity != null ? new GuiBuilder(player, riddenByEntity.inventory, entity) : null;
			//Stationary entities while player is not riding. 
		case (GuiIDs.FREIGHT):
			return entity1 != null ? new GuiFreight(player,player.inventory, entity1) : null;
		case (GuiIDs.TENDER):
			return entity1 != null ? new GuiTender(player,player.inventory, entity1) : null;
		case (GuiIDs.BUILDER):
			return entity1 != null ? new GuiBuilder(player,player.inventory, entity1) : null;
		case (GuiIDs.LIQUID):
			return entity1 != null ? new GuiLiquid(player,player.inventory, entity1) : null;
		case (GuiIDs.RECIPE_BOOK):
			return new GuiRecipeBook(player, player.getCurrentEquippedItem());
		/*case (GuiIDs.RECIPE_BOOK2):
			return te != null && te instanceof TileBook ? new GuiRecipeBook2(player, player.getCurrentEquippedItem()) : new GuiRecipeBook2(player, player.getCurrentEquippedItem());*/
		case (GuiIDs.LANTERN):
			return new GuiLantern(player, (TileLantern)te);
		case (GuiIDs.JUKEBOX):
			return entity1 != null ? new GuiJukebox(player,(EntityJukeBoxCart)entity1) : null;
		default:
			return null;
		}
	}
}
