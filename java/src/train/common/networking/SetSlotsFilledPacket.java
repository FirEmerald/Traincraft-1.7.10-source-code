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

public class SetSlotsFilledPacket implements IMessage
{
	private int entityID = 0;
	private int amount;
	public SetSlotsFilledPacket() {}
	public SetSlotsFilledPacket(Locomotive train, int amount)
	{
		entityID = train.getEntityId();
		this.amount = amount;
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		entityID = ByteBufUtils.readVarInt(buf, 4);
		amount = ByteBufUtils.readVarInt(buf, 5);
	}
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, entityID, 4);
		ByteBufUtils.writeVarInt(buf, amount, 5);
	}
	
	public static class Handler implements IMessageHandler<SetSlotsFilledPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SetSlotsFilledPacket message, MessageContext ctx)
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
			((Locomotive) entity).recieveSlotsFilled(amount);
		}
	}
}
