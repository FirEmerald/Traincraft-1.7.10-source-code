package src.train.common.tile;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;

import java.util.ArrayList;
import java.util.Random;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import src.train.client.core.handlers.ClientTickHandler;
import src.train.common.core.TrainModBlockUtil;
import src.train.common.core.handlers.PacketHandler;
import src.train.common.core.handlers.ServerTickHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class TileWindMill extends TileEntity implements IEnergySource, IEnergyProvider
{
	private int facingMeta;
	private int waterDirection;
	Material blockMaterial;
	private int updateTicks = 0;
	private static Random rand = new Random();
	public int windClient = 0;
	/**
	 * IC2 variable
	 */
	public boolean addedToEnergyNet = false;
	public int IC2production = (int) (MAX_GENERATE_WATTS * TrainModBlockUtil.TO_IC2_RATIO);

	/**
	 * UE variable Maximum amount of energy needed to generate electricity
	 */
	public static final int MAX_GENERATE_WATTS = 60;

	public TileWindMill() {
		facingMeta = this.blockMetadata;
	}

	public int getFacing() {
		return facingMeta;
	}

	public void setFacing(int facing) {
		this.facingMeta = facing;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		facingMeta = nbt.getByte("Orientation");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("Orientation", (byte) facingMeta);
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		return PacketHandler.getTEPPacket(this);
	}
    
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
    	NBTTagCompound tag = pkt.func_148857_g();
    	this.setFacing(tag.getByte("f"));
    	windClient = tag.getInteger("wind");
    }

	public void handlePacketDataFromServer(byte orientation, int wind) {
		facingMeta = orientation;
		if (orientation != -1)
			worldObj.setBlockMetadataWithNotify((int) xCoord, (int) yCoord, (int) zCoord, orientation, 2);
		this.windClient = wind;
	}

	public boolean isSimulating() {
		return !FMLCommonHandler.instance().getEffectiveSide().isClient();
	}

	@Override
	public void onChunkUnload() {
		if ((isSimulating()) && (this.addedToEnergyNet)) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToEnergyNet = false;
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		updateTicks++;
		/**
		 * IC2
		 */
		if (isSimulating() && !addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			this.addedToEnergyNet = true;
		}
		/**
		 * Remove any block on top of the wind mill
		 */
		if (updateTicks % 20 == 0 && !worldObj.isRemote) {
			if (!this.worldObj.isAirBlock(this.xCoord, this.yCoord + 1, this.zCoord)) {
				Block block = this.worldObj.getBlock(this.xCoord, this.yCoord + 1, this.zCoord);
				if (block != null) {
					ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(TrainModBlockUtil.getItemStackFromBlock(worldObj, this.xCoord, this.yCoord + 1, this.zCoord));
					for (ItemStack s : stacks) {
						EntityItem entityitem = new EntityItem(worldObj, this.xCoord, this.yCoord + 1, this.zCoord, s);
						float f3 = 0.05F;
						entityitem.motionX = (float) rand.nextGaussian() * f3;
						entityitem.motionY = (float) rand.nextGaussian() * f3 + 0.2F;
						entityitem.motionZ = (float) rand.nextGaussian() * f3;
						worldObj.spawnEntityInWorld(entityitem);
					}
				}
				this.worldObj.setBlockToAir(this.xCoord, this.yCoord + 1, this.zCoord);
			}
		}
		/**
		 * Calculate production using wind strength
		 */
		if (isSimulating() && (updateTicks % 128 == 0)) {
			IC2production = (int) (ServerTickHandler.windStrength + (((double) this.yCoord / 256) * 10));
			if (this.IC2production <= 0.0D)
				IC2production = 0;
			if (this.worldObj.isThundering())
				this.IC2production *= 3.5D;
			else if (this.worldObj.isRaining()) {
				this.IC2production *= 2.2D;
			}

			IC2production /= 4;
			//System.out.println(this.IC2production);
			if (IC2production > this.getMaxEnergyOutput())
				IC2production = this.getMaxEnergyOutput();
			TileEntity tile;
			if (this.getBlockMetadata()==1 || this.getBlockMetadata()==3)
			{
				tile = worldObj.getTileEntity(xCoord - 1, yCoord, zCoord);
				if (tile instanceof IEnergyReceiver) ((IEnergyReceiver) tile).receiveEnergy(ForgeDirection.EAST, IC2production * 5, false);
				tile = worldObj.getTileEntity(xCoord + 1, yCoord, zCoord);
				if (tile instanceof IEnergyReceiver) ((IEnergyReceiver) tile).receiveEnergy(ForgeDirection.WEST, IC2production * 5, false);
				tile = worldObj.getTileEntity(xCoord, yCoord, zCoord - 1);
				if (tile instanceof IEnergyReceiver) ((IEnergyReceiver) tile).receiveEnergy(ForgeDirection.SOUTH, IC2production * 5, false);
				tile = worldObj.getTileEntity(xCoord, yCoord, zCoord + 1);
				if (tile instanceof IEnergyReceiver) ((IEnergyReceiver) tile).receiveEnergy(ForgeDirection.NORTH, IC2production * 5, false);
				tile = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
				if (tile instanceof IEnergyReceiver) ((IEnergyReceiver) tile).receiveEnergy(ForgeDirection.UP, IC2production * 5, false);
				tile = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
				if (tile instanceof IEnergyReceiver) ((IEnergyReceiver) tile).receiveEnergy(ForgeDirection.DOWN, IC2production * 5, false);
			}
			PacketHandler.sendPacketToClients(PacketHandler.getTEPClient(this), worldObj, this.xCoord, this.yCoord, this.zCoord, 40D);
		}

	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	//@Override
	public int getMaxEnergyOutput() {
		return 10;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return true;
	}

	@Override
	public double getOfferedEnergy() {
		return this.IC2production;
	}

	@Override
	public void drawEnergy(double amount) {}
	@Override
	public boolean canConnectEnergy(ForgeDirection from)
	{
		return true;
	}
	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
	{
		return Math.min(maxExtract, getEnergyStored(from));
	}
	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		return this.IC2production * 5;
	}
	@Override
	public int getMaxEnergyStored(ForgeDirection from)
	{
		return Integer.MAX_VALUE;
	}
	@Override
	public int getSourceTier()
	{
		return 1;
	}
}
