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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import src.train.common.Traincraft;
import src.train.common.entity.zeppelin.AbstractZeppelin;

public class SetZeppelinRotationPacket implements IMessage
{
	private int entityID = 0;
	float yaw = 0, roll = 0;
	public SetZeppelinRotationPacket() {}
	public SetZeppelinRotationPacket(AbstractZeppelin entity, float yaw, float roll)
	{
		entityID = entity.getEntityId();
		this.yaw = yaw;
		this.roll = roll;
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		entityID = ByteBufUtils.readVarInt(buf, 4);
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		yaw = tag.getFloat("y");
		roll = tag.getFloat("r");
	}
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, entityID, 4);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setFloat("y", yaw);
		tag.setFloat("r", roll);
		ByteBufUtils.writeTag(buf, tag);
	}
	
	public static class Handler implements IMessageHandler<SetZeppelinRotationPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SetZeppelinRotationPacket message, MessageContext ctx)
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) message.handle(((NetHandlerPlayServer) ctx.netHandler).playerEntity);
			else message.handle(Traincraft.proxy.getPlayer());
			return null;
		}
	}

	public void handle(EntityPlayer player)
	{
		Entity entity = player.worldObj.getEntityByID(entityID);
		if (entity instanceof AbstractZeppelin)
		{
			((AbstractZeppelin) entity).rotationYawClient = yaw;
			((AbstractZeppelin) entity).roll = roll;
		}
	}
}
