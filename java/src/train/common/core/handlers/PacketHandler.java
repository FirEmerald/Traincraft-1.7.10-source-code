/*******************************************************************************
 * Copyright (c) 2012 Mrbrutal. All rights reserved.
 * 
 * @name TrainCraft
 * @author Mrbrutal
 ******************************************************************************/

package src.train.common.core.handlers;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import src.train.common.Traincraft;
import src.train.common.api.AbstractTrains;
import src.train.common.api.EntityRollingStock;
import src.train.common.api.Locomotive;
import src.train.common.entity.rollingStock.EntityJukeBoxCart;
import src.train.common.entity.rollingStock.EntityTracksBuilder;
import src.train.common.entity.zeppelin.AbstractZeppelin;
import src.train.common.items.ItemRecipeBook;
import src.train.common.library.Info;
import src.train.common.networking.SetDistilLiquidPacket;
import src.train.common.networking.SetJukeBoxStreamingURLPacket;
import src.train.common.networking.SetLocoTurnedOnPacket;
import src.train.common.networking.SetParkingBrakePacket;
import src.train.common.networking.SetRotationPacket;
import src.train.common.networking.SetSlotsFilledPacket;
import src.train.common.networking.SetTCRailInfoPacket;
import src.train.common.networking.SetWindMillInfoPacket;
import src.train.common.networking.SetZeppelinRotationPacket;
import src.train.common.networking.setBuilderPlannedHeightPacket;
import src.train.common.networking.setTrainLockedPacket;
import src.train.common.tile.TileBook;
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

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketHandler
{

	protected RollingStockStatsEventHandler statsEventHandler = new RollingStockStatsEventHandler();

	public static IMessage getTEPClient(TileEntity te)
	{
		if (te instanceof TileWindMill) return new SetWindMillInfoPacket((TileWindMill) te);
		if (te instanceof TileTCRail) return new SetTCRailInfoPacket((TileTCRail) te);
		return null;
	}

	public static S35PacketUpdateTileEntity getTEPPacket(TileEntity te)
	{
		NBTTagCompound tag = new NBTTagCompound();
		if (te != null)
		{
			if (te instanceof TileTrainWbench)
			{
				TileTrainWbench tem = (TileTrainWbench) te;
				tag.setByte("f", (byte) tem.getFacing().ordinal());
			}
			if (te instanceof TileCrafterTierIII)
			{
				TileCrafterTierIII tem = (TileCrafterTierIII) te;
				tag.setByte("f", (byte) tem.getFacing().ordinal());
			}
			if (te instanceof TileCrafterTierII)
			{
				TileCrafterTierII tem = (TileCrafterTierII) te;
				tag.setByte("f", (byte) tem.getFacing().ordinal());
			}
			if (te instanceof TileCrafterTierI)
			{
				TileCrafterTierI tem = (TileCrafterTierI) te;
				tag.setByte("f", (byte) tem.getFacing().ordinal());
			}
			if (te instanceof TileEntityDistil)
			{
				TileEntityDistil tem = (TileEntityDistil) te;
				tag.setByte("f", (byte) tem.getFacing().ordinal());
				tag.setShort("cookTime", (short) tem.distilCookTime);
				tag.setShort("burnTime", (short) tem.distilBurnTime);
				tag.setShort("amount", (short) tem.amount);
				tag.setShort("liquid", (short) tem.liquidItemID);
			}
			if (te instanceof TileEntityOpenHearthFurnace)
			{
				TileEntityOpenHearthFurnace tem = (TileEntityOpenHearthFurnace) te;
				tag.setByte("f", (byte) tem.getFacing().ordinal());
				tag.setShort("cookTime", (short) tem.furnaceCookTime);
				tag.setShort("burnTime", (short) tem.furnaceBurnTime);
			}
			if (te instanceof TileStopper)
			{
				TileStopper tem = (TileStopper) te;
				tag.setByte("f", (byte) tem.getFacing());
			}
			if (te instanceof TileBook)
			{
				TileBook tem = (TileBook) te;
				tag.setByte("f", (byte) tem.getFacing());
			}
			if (te instanceof TileSignal)
			{
				TileSignal tem = (TileSignal) te;
				tag.setByte("f", (byte) tem.getFacing());
			}
			if (te instanceof TileLantern)
			{
				TileLantern tem = (TileLantern) te;
				tag.setInteger("color", tem.randomColor);
			}
			if (te instanceof TileWaterWheel)
			{
				TileWaterWheel tem = (TileWaterWheel) te;
				tag.setByte("f", (byte) tem.getFacing());
			}
			if (te instanceof TileWindMill)
			{
				TileWindMill tem = (TileWindMill) te;
				tag.setByte("f", (byte) tem.getFacing());
				tag.setInteger("wind", ServerTickHandler.windStrength);
			}
			if (te instanceof TileGeneratorDiesel)
			{
				TileGeneratorDiesel tem = (TileGeneratorDiesel) te;
				tag.setByte("f", (byte) tem.getFacing());
			}
			if (te instanceof TileTCRail)
			{
				TileTCRail tem = (TileTCRail) te;
				tag.setByte("f", (byte) tem.getFacing());
				tag.setString("type", tem.getType());
				tag.setBoolean("hasModel", tem.hasModel);
				tag.setBoolean("state", tem.getSwitchState());
				tag.setInteger("item", Item.getIdFromItem(tem.idDrop));
			}
			if (te instanceof TileTCRailGag)
			{
				TileTCRailGag tem = (TileTCRailGag) te;
				tag.setString("type", tem.type);
				tag.setInteger("bbHeight", (int) (tem.bbHeight * 1000));
			}
			return new S35PacketUpdateTileEntity(te.xCoord, te.yCoord, te.zCoord, 3, tag);
		}
		return null;
	}

	public static IMessage setDistilLiquid(TileEntity te)
	{
		if (te != null && te instanceof TileEntityDistil)
		{
			return new SetDistilLiquidPacket((TileEntityDistil) te);
		}
		return null;
	}

	/**
	 * RollingStock rotation packet sent to client
	 * 
	 * @param entity
	 * @param rotationYawServer
	 * @param realRotation
	 * @param anglePitch
	 * @return
	 */
	public static IMessage setRotationPacket(Entity entity, float rotationYawServer, float realRotation, boolean isInReverse, float anglePitch, double posY, boolean shouldSetPosY)
	{
		if (entity != null && entity instanceof EntityRollingStock) return new SetRotationPacket((EntityRollingStock) entity, rotationYawServer, realRotation, isInReverse, anglePitch, posY, shouldSetPosY);
		return null;
	}

	/**
	 * Zeppelin rotation packet sent to client
	 * 
	 * @param entity
	 * @param rotationYawServer
	 * @param realRotation
	 * @param anglePitch
	 * @return
	 */
	public static IMessage setRotationPacketZeppelin(Entity entity, float rotationYawServer, float roll)
	{
		if (entity != null && entity instanceof AbstractZeppelin) return new SetZeppelinRotationPacket((AbstractZeppelin) entity, rotationYawServer, roll);
		return null;
	}

	public static void sendPacketToClients(IMessage packet, World worldObj, int x, int y, int z, double range)
	{
		try
		{
			for (EntityPlayerMP player : (List<EntityPlayerMP>) worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.getBoundingBox(x - range, y - range, z - range, x + range, y + range, z + range)))
			{
				if (player.getDistanceSq(x, y, z) <= range * range) Traincraft.network.sendTo(packet, player);
			}
		}
		catch (Exception e)
		{
			System.out.println("Sending packet to client failed.");
			e.printStackTrace();
		}
	}

	public static IMessage setParkingBrake(Entity player, Entity entity, boolean set, boolean toServer)
	{
		IMessage packet = null;
		if (entity != null && entity instanceof Locomotive) packet = new SetParkingBrakePacket((Locomotive) entity, set);
		if (toServer)
		{
			Traincraft.network.sendToServer(packet);
		}
		return packet;
	}

	public static IMessage setLocoTurnedOn(Entity player, Entity entity, boolean set, boolean toServer)
	{
		IMessage packet = null;
		if (entity != null && entity instanceof Locomotive) packet = new SetLocoTurnedOnPacket((Locomotive) entity, set);
		if (toServer)
		{
			Traincraft.network.sendToServer(packet);
		}
		return packet;
	}

	public static IMessage setTrainLocked(Entity player, Entity entity, boolean set)
	{
		IMessage packet = null;
		if (entity != null && entity instanceof AbstractTrains) packet = new setTrainLockedPacket((AbstractTrains) entity, set);
		if (player instanceof EntityClientPlayerMP)
		{
			Traincraft.network.sendToServer(packet);
		}
		return packet;
	}

	public static IMessage setTrainLockedToClient(Entity player, Entity entity, boolean set)
	{
		if (entity instanceof AbstractTrains) return new setTrainLockedPacket((AbstractTrains) entity, set);
		return null;
	}

	public static IMessage setBuilderPlannedHeight(Entity player, Entity entity, int set, boolean packetID)
	{
		IMessage packet = null;
		if (entity != null && entity instanceof EntityTracksBuilder) packet = new setBuilderPlannedHeightPacket((EntityTracksBuilder) entity, set, packetID);
		if (player instanceof EntityClientPlayerMP)
		{
			Traincraft.network.sendToServer(packet);
		}
		return packet;
	}
	/*
	public static IMessage setBookPage(Entity player, int page, int recipe)
	{
		ByteBuf buf = Unpooled.buffer();
		int name = 6;
		buf.writeInt(player.getEntityId());//.getID());
		buf.writeInt(page);
		buf.writeInt(recipe);
		IMessage packet = new TCPayloadPacket(name, buf);
		if (player instanceof EntityClientPlayerMP)
		{
			Traincraft.network.sendToServer(packet);
		}
		return packet;
	}
	*/
	public static IMessage setJukeBoxStreamingUrl(EntityPlayer player, Entity entity, String url, boolean isPlaying)
	{
		IMessage packet = null;
		if (entity != null && entity instanceof EntityJukeBoxCart) packet = new SetJukeBoxStreamingURLPacket((EntityJukeBoxCart) entity, isPlaying, url);
		if (player instanceof EntityClientPlayerMP)
		{
			Traincraft.network.sendToServer(packet);
		}
		return packet;
	}

	public static IMessage setSlotsFilledPacket(Entity entity, int slotsFilled)
	{
		if (entity instanceof Locomotive) return new SetSlotsFilledPacket((Locomotive) entity, slotsFilled);
		return null;
	}
}
