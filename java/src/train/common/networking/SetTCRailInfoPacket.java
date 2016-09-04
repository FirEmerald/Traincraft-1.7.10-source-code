package src.train.common.networking;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import src.train.common.Traincraft;
import src.train.common.tile.TileTCRail;

public class SetTCRailInfoPacket implements IMessage
{
	private int x = 0, y = 0, z = 0, idDrop = 0;
	private byte facing = 0;
	private boolean switchState = false, model = false;
	private String type = "";
	public SetTCRailInfoPacket() {}
	public SetTCRailInfoPacket(TileTCRail tile)
	{
		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
		facing = (byte) tile.getFacing();
		switchState = tile.getSwitchState();
		model = tile.hasModel;
		type = tile.getType();
		idDrop = Item.getIdFromItem(tile.idDrop);
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = ByteBufUtils.readVarInt(buf, 5);
		y = ByteBufUtils.readVarInt(buf, 5);
		z = ByteBufUtils.readVarInt(buf, 5);
		facing = buf.readByte();
		switchState = buf.readBoolean();
		model = buf.readBoolean();
		type = ByteBufUtils.readUTF8String(buf);
		idDrop = ByteBufUtils.readVarInt(buf, 5);
	}
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, x, 5);
		ByteBufUtils.writeVarInt(buf, y, 5);
		ByteBufUtils.writeVarInt(buf, z, 5);
		buf.writeByte(facing);
		buf.writeBoolean(switchState);
		buf.writeBoolean(model);
		ByteBufUtils.writeUTF8String(buf, type);
		ByteBufUtils.writeVarInt(buf, idDrop, 5);
	}
	
	public static class Handler implements IMessageHandler<SetTCRailInfoPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SetTCRailInfoPacket message, MessageContext ctx)
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) message.handle(((NetHandlerPlayServer) ctx.netHandler).playerEntity);
			else message.handle(Traincraft.proxy.getPlayer());
			return null;
		}
	}

	public void handle(EntityPlayer player)
	{
		TileEntity tile = player.worldObj.getTileEntity(x, y, z);
		if (tile instanceof TileTCRail) ((TileTCRail) tile).handlePacketDataFromServer(facing, type, model, switchState, idDrop);
	}
}
