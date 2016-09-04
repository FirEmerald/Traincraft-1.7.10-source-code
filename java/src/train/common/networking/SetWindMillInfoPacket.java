package src.train.common.networking;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import src.train.common.Traincraft;
import src.train.common.core.handlers.ServerTickHandler;
import src.train.common.tile.TileWindMill;

public class SetWindMillInfoPacket implements IMessage
{
	private int x = 0, y = 0, z = 0, wind = 0;
	private byte meta = 0;
	public SetWindMillInfoPacket() {}
	public SetWindMillInfoPacket(TileWindMill tile)
	{
		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
		this.meta = (byte) tile.getFacing();
		wind = ServerTickHandler.windStrength;
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = ByteBufUtils.readVarInt(buf, 5);
		y = ByteBufUtils.readVarInt(buf, 5);
		z = ByteBufUtils.readVarInt(buf, 5);
		meta = buf.readByte();
		wind = ByteBufUtils.readVarInt(buf, 5);
	}
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, x, 5);
		ByteBufUtils.writeVarInt(buf, y, 5);
		ByteBufUtils.writeVarInt(buf, z, 5);
		buf.writeByte(meta);
		ByteBufUtils.writeVarInt(buf, wind, 5);
	}
	
	public static class Handler implements IMessageHandler<SetWindMillInfoPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SetWindMillInfoPacket message, MessageContext ctx)
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) message.handle(((NetHandlerPlayServer) ctx.netHandler).playerEntity);
			else message.handle(Traincraft.proxy.getPlayer());
			return null;
		}
	}

	public void handle(EntityPlayer player)
	{
		TileEntity tile = player.worldObj.getTileEntity(x, y, z);
		if (tile instanceof TileWindMill)
		{
			((TileWindMill) tile).setFacing(meta);
			((TileWindMill) tile).windClient = wind;
		}
	}
}
