package src.train.common.networking;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import src.train.common.api.EntityRollingStock;
import src.train.common.api.Locomotive;
import src.train.common.entity.digger.EntityRotativeDigger;
import src.train.common.entity.zeppelin.AbstractZeppelin;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class KeyPacket implements IMessage
{
	private int key = 0;
	public KeyPacket() {}
	public KeyPacket(int key)
	{
		this.key = key;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		key = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(key);
	}
	
	public static class Handler implements IMessageHandler<KeyPacket, IMessage>
	{

		@Override
		public IMessage onMessage(KeyPacket message, MessageContext ctx)
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) message.handleServerSide(((NetHandlerPlayServer) ctx.netHandler).playerEntity);
			return null;
		}
		
	}
	
	public void handleServerSide(EntityPlayer player)
	{
		if (((EntityPlayer) player).ridingEntity != null && ((EntityPlayer) player).ridingEntity instanceof Locomotive) {
			((Locomotive) ((EntityPlayer) player).ridingEntity).keyHandlerFromPacket(key);
		}
		else if (((EntityPlayer) player).ridingEntity != null && ((EntityPlayer) player).ridingEntity instanceof EntityRollingStock) {
			((EntityRollingStock) ((EntityPlayer) player).ridingEntity).keyHandlerFromPacket(key);
		}
		else if (((EntityPlayer) player).ridingEntity != null && ((EntityPlayer) player).ridingEntity instanceof AbstractZeppelin) {
			((AbstractZeppelin) ((EntityPlayer) player).ridingEntity).pressKey(key);
		}
		else if (((EntityPlayer) player).ridingEntity != null && ((EntityPlayer) player).ridingEntity instanceof EntityRotativeDigger) {
			((EntityRotativeDigger) ((EntityPlayer) player).ridingEntity).pressKey(key);
		}
	}
}