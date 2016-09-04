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
import src.train.common.Traincraft;
import src.train.common.entity.rollingStock.EntityJukeBoxCart;

public class SetJukeBoxStreamingURLPacket implements IMessage
{
	private int entityID = 0;
	private String url = "";
	private boolean playing = false;
	public SetJukeBoxStreamingURLPacket() {}
	public SetJukeBoxStreamingURLPacket(EntityJukeBoxCart train, boolean playing, String url)
	{
		entityID = train.getEntityId();
		this.playing = playing;
		this.url = url;
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		entityID = ByteBufUtils.readVarInt(buf, 4);
		playing = buf.readBoolean();
		url = ByteBufUtils.readUTF8String(buf);
	}
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, entityID, 4);
		buf.writeBoolean(playing);
		ByteBufUtils.writeUTF8String(buf, url);
	}
	
	public static class Handler implements IMessageHandler<SetJukeBoxStreamingURLPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SetJukeBoxStreamingURLPacket message, MessageContext ctx)
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) message.handle(((NetHandlerPlayServer) ctx.netHandler).playerEntity);
			else message.handle(Traincraft.proxy.getPlayer());
			return null;
		}
	}

	public void handle(EntityPlayer player)
	{
		Entity entity = player.worldObj.getEntityByID(entityID);
		if (entity instanceof EntityJukeBoxCart)
		{
			((EntityJukeBoxCart) entity).recievePacket(url, playing);
		}
	}
}
