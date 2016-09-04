package src.train.common.tile;

import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import src.train.common.core.handlers.PacketHandler;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileTCRailGag extends TileEntity {
	protected Random rand = new Random();
	protected Side side;
	public int originX;
	public int originY;
	public int originZ;
	public String type;
	public float bbHeight = 0.125f;

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		originX = nbt.getInteger("originX");
		originY = nbt.getInteger("originY");
		originZ = nbt.getInteger("originZ");
		bbHeight = nbt.getFloat("bbHeight");
		type = nbt.getString("type");
		super.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("originX", originX);
		nbt.setInteger("originY", originY);
		nbt.setInteger("originZ", originZ);
		nbt.setFloat("bbHeight", bbHeight);
		nbt.setString("type", type);
		super.writeToNBT(nbt);
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		return PacketHandler.getTEPPacket(this);
	}
    
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
    	NBTTagCompound tag = pkt.func_148857_g();
    	this.type = tag.getString("type");
    	this.bbHeight = (float) tag.getInteger("bbHeight") / 1000;
    }

	public void handlePacketDataFromServer(String type, int bbHeight) {
		this.type = type;
		this.bbHeight = (((float) bbHeight) / 1000);
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
	}
}