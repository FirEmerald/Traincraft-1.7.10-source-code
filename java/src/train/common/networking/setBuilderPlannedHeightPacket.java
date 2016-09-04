package src.train.common.networking;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import src.train.common.entity.rollingStock.EntityTracksBuilder;

public class setBuilderPlannedHeightPacket implements IMessage
{
	private int entityID = 0;
	private int height = 0;
	private boolean type = false;
	public setBuilderPlannedHeightPacket() {}
	public setBuilderPlannedHeightPacket(EntityTracksBuilder builder, int height, boolean type)
	{
		entityID = builder.getEntityId();
		this.height = height;
		this.type = type;
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		entityID = ByteBufUtils.readVarInt(buf, 4);
		height = ByteBufUtils.readVarInt(buf, 5);
		type = buf.readBoolean();
	}
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, entityID, 4);
		ByteBufUtils.writeVarInt(buf, height, 5);
		buf.writeBoolean(type);
	}
	
	public static class Handler implements IMessageHandler<setBuilderPlannedHeightPacket, IMessage>
	{
		@Override
		public IMessage onMessage(setBuilderPlannedHeightPacket message, MessageContext ctx)
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			{
	            EntityPlayer player = ((NetHandlerPlayServer) ctx.netHandler).playerEntity;
	            message.handleServerSide(player);
	        }
			return null;
		}
	}

	public void handleServerSide(EntityPlayer player)
	{
		Entity entity = player.worldObj.getEntityByID(entityID);
		if (entity instanceof EntityTracksBuilder)
		{
			if (type) ((EntityTracksBuilder) entity).setPlannedHeightFromPacket(height);
			else ((EntityTracksBuilder) entity).setFollowTracks(height);
		}
	}
}
