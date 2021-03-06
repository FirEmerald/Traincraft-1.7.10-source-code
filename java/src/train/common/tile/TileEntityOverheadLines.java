package src.train.common.tile;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import src.train.common.blocks.tracks.BlockEnergyTrack;
import src.train.common.core.handlers.OverheadLinesEnergyNetHandler;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.FMLCommonHandler;


/**
 * @author Spitfire4466 <trains-and-zeppelin.wikispaces.com>
 */
public class TileEntityOverheadLines extends TileEntity implements IEnergySink, IEnergyReceiver
{ 

	public double energy = 0;
	public int maxEnergy=1024;
	public int tier = 1; 
	public int output; 
	private int facing = 0;
	public static OverheadLinesEnergyNetHandler network = new OverheadLinesEnergyNetHandler();
	private int updateTicks = 0;
	public boolean isProvider = false;
	
	public boolean Up = false; 
	public boolean Down = false; 
	public boolean Left = false; 
	public boolean Right = false; 
	public boolean Back = false; 
	public boolean Front = false;

	public boolean addedToEnergyNet=false;

	public TileEntityOverheadLines() {

	} 

	public boolean isAddedToEnergyNet() { 
		return this.addedToEnergyNet; 
	}

	public int getFacing() { 
		return this.facing; 
	} 
	public void setFacing(int facing) { 
		this.facing = facing; 
	}
	public void updateEntity() { 
		if (worldObj.isRemote) {
			return;
		}
		updateTicks++;
		if ((!this.addedToEnergyNet)) {
			network.addToNetwork(this);
			this.addedToEnergyNet = true;
			/**
			 * IC2
			 */
			if (isSimulating()&&!addedToEnergyNet) {
				//MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			}
		}
		if (updateTicks % 200 == 0) {
			network.cleanNetwork();
		}
		if (updateTicks % 5 == 0 && network != null && network.getNetwork() != null && network.getNetwork().size() > 0 && this.energy >= (this.getMaxEnergy() - 1)) {
			double output = Math.min(this.output, this.energy);
			if (output > 0) {
				this.energy = (short) (this.energy - sendEnergy(output));
			}
		}
/*
		List lis3 = this.worldObj.getEntitiesWithinAABB(ElectricTrain.class, AxisAlignedBB.getBoundingBoxFromPool((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 1), (double)(this.zCoord + 1)).expand(1.0D, 4.0D, 1.0D));

		if (lis3 != null && lis3.size() > 0) {

			for (int j1 = 0; j1 < lis3.size(); j1++) { Entity entity = (Entity)lis3.get(j1);

			if ((entity instanceof IElectricTrain)) { if ((this.energy > 0) && (((EntityRollingStock)entity).fuelTrain)<400 ) { double transfered = this.energy0.05; (((EntityRollingStock)entity).fuelTrain)+=transfered; this.energy-=transfered2; } } } }

		List lis1 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBoxFromPool((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 0.1), (double)(this.yCoord+0.1), (double)(this.zCoord + 0.1)).expand(1.2D, 1.2D, 1.2D));
*/
	}
	public double getDistanceSq(double par1, double par3, double par5) {
		double var7 = this.xCoord - par1;
		double var9 = this.yCoord - par3;
		if (Math.abs(var9) == 1)
			var9 = 0;
		double var11 = this.zCoord - par5;
		return Math.sqrt(var7 * var7 + var11 * var11);
	}
	public int sendEnergy(double send) {
		for (int i = 0; i < network.getNetwork().size(); i++) {
			if (network.getNetwork().get(i) != null && !network.getNetwork().get(i).equals(this)) {
				int x2 = ((BlockEnergyTrack) network.getNetwork().get(i)).getX();
				int y2 = ((BlockEnergyTrack) network.getNetwork().get(i)).getY();
				int z2 = ((BlockEnergyTrack) network.getNetwork().get(i)).getZ();
				if (!((BlockEnergyTrack) network.getNetwork().get(i)).isProvider && this.isProvider && ((TileEntityOverheadLines) network.getNetwork().get(i)).getEnergy() < ((TileEntityOverheadLines) network.getNetwork().get(i)).getMaxEnergy()) {//if(isBeside(x2,y2,z2)){
					if (getDistanceSq(x2, y2, z2) <= 50){
						double transfered = ((TileEntityOverheadLines) network.getNetwork().get(i)).getMaxEnergy() - ((TileEntityOverheadLines) network.getNetwork().get(i)).getEnergy();
						transfered = Math.min(this.output, transfered);
						((TileEntityOverheadLines) network.getNetwork().get(i)).setEnergy(transfered + ((TileEntityOverheadLines) network.getNetwork().get(i)).getEnergy());
						if (transfered > 0)
							return (int) transfered;
						}
				}
			}
		}
		return 0;
	}
	
	private void setEnergy(double d)
	{
		energy = d;
	}

	private double getEnergy()
	{
		return energy;
	}

	public boolean isSimulating()
	{
		return !FMLCommonHandler.instance().getEffectiveSide().isClient();
	}
	@Override
	public void onChunkUnload()
	{
		if ((isSimulating()) && (this.addedToEnergyNet)) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToEnergyNet = false;
		}
	}
	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound); 
		par1NBTTagCompound.setDouble("energy", energy); 
		par1NBTTagCompound.setInteger("facing", facing); 
		par1NBTTagCompound.setBoolean("Up",Up); 
		par1NBTTagCompound.setBoolean("Down",Down); 
		par1NBTTagCompound.setBoolean("Left",Left); 
		par1NBTTagCompound.setBoolean("Right",Right);
		par1NBTTagCompound.setBoolean("Back",Back); 
		par1NBTTagCompound.setBoolean("Front",Front);
	}
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound); 
		energy=par1NBTTagCompound.getDouble("energy"); 
		facing=par1NBTTagCompound.getInteger("facing"); 
		Up=par1NBTTagCompound.getBoolean("Up"); 
		Down=par1NBTTagCompound.getBoolean("Down"); 
		Left=par1NBTTagCompound.getBoolean("Left"); 
		Right=par1NBTTagCompound.getBoolean("Right");
		Back=par1NBTTagCompound.getBoolean("Back"); 
		Front=par1NBTTagCompound.getBoolean("Front");
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage)
	{
		double reject = amount - Math.min(amount, getMaxSafeInput());
		this.energy+=Math.min(amount, getMaxSafeInput());
		isProvider = true;
		if(this.energy>this.getMaxEnergy())
		{
			reject += this.energy-this.getMaxEnergy();
			this.energy=this.getMaxEnergy();
		}
		return reject;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
	{
		int energyReceived = Math.min(this.getMaxEnergyStored(from) - this.getEnergyStored(from), Math.min(200, maxReceive));
		if (!simulate)
		{
			energy += energyReceived;
		}
		return energyReceived;
	}

	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		return (int) (this.energy * 5);
	}

	public int getMaxEnergy()
	{
		return this.maxEnergy;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from)
	{
		return this.maxEnergy * 5;
	}
	
	public int getMaxSafeInput()
	{
		return 1024;
	}
	
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
	{
		return true;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from)
	{
		return true;
	}

	@Override
	public double getDemandedEnergy()
	{
		return Math.min(this.getMaxEnergy() - this.energy, getMaxSafeInput());
	}

	@Override
	public int getSinkTier()
	{
		return 2;
	}
}


