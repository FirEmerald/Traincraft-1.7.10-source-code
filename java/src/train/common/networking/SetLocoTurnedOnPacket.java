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
import src.train.common.api.Locomotive;

public class SetLocoTurnedOnPacket implements IMessage
{
	private int entityID = 0;
	private boolean on = false;
	public SetLocoTurnedOnPacket() {}
	public SetLocoTurnedOnPacket(Locomotive train, boolean on)
	{
		entityID = train.getEntityId();
		this.on = on;
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		entityID = ByteBufUtils.readVarInt(buf, 4);
		on = buf.readBoolean();
	}
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, entityID, 4);
		buf.writeBoolean(on);
	}
	
	public static class Handler implements IMessageHandler<SetLocoTurnedOnPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SetLocoTurnedOnPacket message, MessageContext ctx)
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) message.handle(((NetHandlerPlayServer) ctx.netHandler).playerEntity);
			else message.handle(Traincraft.proxy.getPlayer());
			return null;
		}
	}

	public void handle(EntityPlayer player)
	{
		Entity entity = player.worldObj.getEntityByID(entityID);
		if (entity instanceof Locomotive)
		{
			((Locomotive) entity).setLocoTurnedOn(on, false, false, 0);
		}
	}
}
