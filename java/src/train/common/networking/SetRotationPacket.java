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
import src.train.common.api.EntityRollingStock;

public class SetRotationPacket implements IMessage
{
	private int entityID = 0;
	float rotationYawServer = 0, realRotation = 0, anglePitch = 0;
	boolean isInReverse = false, shouldSetPosY = false;
	double posY = 0;
	public SetRotationPacket() {}
	public SetRotationPacket(EntityRollingStock train, float rotationYawServer, float realRotation, boolean isInReverse, float anglePitch, double posY, boolean shouldSetPosY)
	{
		entityID = train.getEntityId();
		this.rotationYawServer = rotationYawServer;
		this.realRotation = realRotation;
		this.isInReverse = isInReverse;
		this.anglePitch = anglePitch;
		this.posY = posY;
		this.shouldSetPosY = shouldSetPosY;
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		entityID = ByteBufUtils.readVarInt(buf, 4);
		isInReverse = buf.readBoolean();
		shouldSetPosY = buf.readBoolean();
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		rotationYawServer = tag.getFloat("rY");
		realRotation = tag.getFloat("rR");
		anglePitch = tag.getFloat("aP");
		posY = tag.getDouble("pY");
	}
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, entityID, 4);
		buf.writeBoolean(isInReverse);
		buf.writeBoolean(shouldSetPosY);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setFloat("rY", rotationYawServer);
		tag.setFloat("rR", realRotation);
		tag.setFloat("aP", anglePitch);
		tag.setDouble("pY", posY);
		ByteBufUtils.writeTag(buf, tag);
	}
	
	public static class Handler implements IMessageHandler<SetRotationPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SetRotationPacket message, MessageContext ctx)
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) message.handle(((NetHandlerPlayServer) ctx.netHandler).playerEntity);
			else message.handle(Traincraft.proxy.getPlayer());
			return null;
		}
	}

	public void handle(EntityPlayer player)
	{
		Entity entity = player.worldObj.getEntityByID(entityID);
		if (entity instanceof EntityRollingStock)
		{
			((EntityRollingStock) entity).rotationYawClient = rotationYawServer;
			((EntityRollingStock) entity).rotationYawClientReal = realRotation;
			((EntityRollingStock) entity).isClientInReverse = isInReverse;
			((EntityRollingStock) entity).anglePitchClient = anglePitch;
			((EntityRollingStock) entity).setYFromServer(posY, shouldSetPosY);
		}
	}
}
