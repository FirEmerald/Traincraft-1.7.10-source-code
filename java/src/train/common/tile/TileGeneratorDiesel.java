/*******************************************************************************
 * Copyright (c) 2013 Spitfire4466. All rights reserved.
 * 
 * @name TrainCraft
 * @author Spitfire4466
 ******************************************************************************/

package src.train.common.tile;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import src.train.common.api.LiquidManager;
import src.train.common.api.LiquidManager.StandardTank;
import src.train.common.core.TrainModBlockUtil;
import src.train.common.core.handlers.PacketHandler;
import src.train.common.library.Info;
import src.train.common.networking.SetGeneratorLiquidPacket;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;

public class TileGeneratorDiesel extends TileEntity implements IEnergyProvider,IFluidHandler, IInventory, IEnergySource{

	private int facingMeta;
	public float energy;
	private boolean powered = false;
	public float extraEnergy;
	private int update;
	public float currentOutput = 0.0F;
	private static final float OUTPUT_MJ = 8.0F;
	private ForgeDirection direction = ForgeDirection.UNKNOWN;
	//private IPowerProvider provider;
	private int maxTank = 0;
	private StandardTank theTank;
	private IFluidTank[] tankArray = new IFluidTank[1];
	private FluidStack liquid;
	public int amount;
	public int liquidItemID;
	public Side side = FMLCommonHandler.instance().getEffectiveSide();
	private boolean producing = false;
	private int liquidItemIDClient;
	private int amountClient;
	public ItemStack dieselItemStacks[];
	/**
	 * IC2 variable
	 */
	public boolean addedToEnergyNet = false;
	public int IC2production = 0;

	public TileGeneratorDiesel() {
		facingMeta = this.blockMetadata;
		this.liquid = new FluidStack(FluidRegistry.WATER, 0);
		this.maxTank = 30000;
		this.theTank = LiquidManager.getInstance().new FilteredTank(maxTank, LiquidManager.getInstance().dieselFilter(), 1);
		tankArray[0] = theTank;
		dieselItemStacks = new ItemStack[2];
	}

	public int getFacing() {
		return facingMeta;
	}
	public void setFacing(int facing) {
		this.facingMeta = facing;
		direction = ForgeDirection.getOrientation(facing);
	}
	@Override
	public void onChunkUnload() {
		if ((isSimulating()) && (this.addedToEnergyNet)) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToEnergyNet = false;
		}
	}
	/**
	 * IC2 max output
	 * @return
	 */
	public int getMaxEnergyOutput() {
		return 10;
	}
	/**
	 * IC2
	 */
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return true;
	}
	/**
	 * IC2
	 */
	@Override
	public double getOfferedEnergy() {
		return this.IC2production;
	}
	/**
	 * IC2
	 */
	@Override
	public void drawEnergy(double amount)
	{
		extractEnergy((float)amount, true);
	}
	
	@Override
	public void updateEntity(){
		/**
		 * IC2
		 */
		if (isSimulating() && !addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			this.addedToEnergyNet = true;
		}
		
		if(!worldObj.isRemote){
			IC2production = (int) (currentOutput*2.5);
			
			ArrayList<TileEntity> tile = new ArrayList<TileEntity>();
			tile.add(0, worldObj.getTileEntity(xCoord+1, yCoord, zCoord));
			tile.add(1,worldObj.getTileEntity(xCoord-1, yCoord, zCoord));
			tile.add(2,worldObj.getTileEntity(xCoord, yCoord-1, zCoord));
			tile.add(3,worldObj.getTileEntity(xCoord, yCoord+1, zCoord));
			tile.add(4,worldObj.getTileEntity(xCoord, yCoord, zCoord-1));
			tile.add(5,worldObj.getTileEntity(xCoord, yCoord, zCoord+1));
			for(int t = 0;t<tile.size();t++)
			{
				if(tile.get(t) !=null && tile.get(t) instanceof IEnergyReceiver)
				{
					IEnergyReceiver receptor = (IEnergyReceiver)tile.get(t);
					ForgeDirection dir = ForgeDirection.UNKNOWN;
					if(t==0)dir = ForgeDirection.EAST;
					if(t==1)dir = ForgeDirection.WEST;
					if(t==2)dir = ForgeDirection.DOWN;
					if(t==3)dir = ForgeDirection.UP;
					if(t==4)dir = ForgeDirection.NORTH;
					if(t==5)dir = ForgeDirection.SOUTH;
					
					if (isPoweredTile((TileEntity) receptor, dir))
					{
						IEnergyReceiver receptor1 = ((IEnergyReceiver) tile);

						float extracted = getPowerToExtract((TileEntity) receptor,dir);
						if (extracted > 0)
						{
							extracted = (float) receptor1.receiveEnergy(dir.getOpposite(), (int) (extracted * 5), false);
							extractEnergy(extracted, true); // Comment out for constant power
							//currentOutput = extractEnergy(0, needed, true); // Uncomment for constant power
							this.IC2production=0;//if a bc pipe is drawing energy, do not output IC2
						}
					}
				}
			}
			burn();
		}
	}
	private float getPowerToExtract(TileEntity tile,ForgeDirection dir)
	{
		IEnergyReceiver receptor = ((IEnergyReceiver) tile);
		return (float) receptor.receiveEnergy(dir, maxEnergyReceived() * 5, true) / 5; // Comment out for constant power
		//		return extractEnergy(0, getActualOutput(), false); // Uncomment for constant power
	}
	public boolean isPoweredTile(TileEntity tile, ForgeDirection side)
	{
		if (tile instanceof IEnergyReceiver) return ((IEnergyReceiver) tile).canConnectEnergy(side.getOpposite());
		return false;
	}
	public void burn()
	{
		this.update += 1;

		if (this.update % 8 == 0) {
			if (dieselItemStacks[0] != null){
				ItemStack result = LiquidManager.getInstance().processContainer(this, 0, theTank, dieselItemStacks[0], 0);
				if (result != null && placeInInvent(result, 1, false)) {
					placeInInvent(result, 1, true);
				}
			}
			if(this.theTank.getFluid()!=null){
				amount = this.theTank.getFluid().amount;
				this.liquidItemID = this.theTank.getFluid().getFluidID();
			}else{
				amount = 0;
				this.liquidItemID=0;
			}
			if(side.isServer())PacketHandler.sendPacketToClients(this.setGeneratorLiquid(), this.worldObj, xCoord, yCoord, zCoord, 12.0D);	
		}

		float output = 0.0F;
		if (isPowered() && this.theTank.getFluid()!=null && this.theTank.getFluid().amount>dieselUsedPerTick()&&this.energy<this.maxEnergy()) {
			if (this.update % 8 == 0)setIsProducing(true);
			output = getMaxOutputMJ();
			addEnergy(getMaxOutputMJ());
			theTank.drain(dieselUsedPerTick(), true);
		}else{
			if (this.update % 8 == 0)setIsProducing(false);
		}
		this.currentOutput = ((this.currentOutput * 74.0F + output) / 75.0F);
	}
	public IMessage setGeneratorLiquid()
	{
		return new SetGeneratorLiquidPacket(this);
	}
	public void handlePacketDataFromServer(boolean isProducing,short amount, short liquidID) {
		this.setIsProducing(isProducing);
		this.amountClient = (int) amount;
		liquidItemIDClient = liquidID;
	}
	@Override
	public void readFromNBT(NBTTagCompound nbtTag) {
		super.readFromNBT(nbtTag);
		facingMeta = nbtTag.getByte("Orientation");
		this.energy = nbtTag.getFloat("Energy");
		this.direction = ForgeDirection.getOrientation(nbtTag.getInteger("direction"));
		this.theTank.readFromNBT(nbtTag);
		this.powered = nbtTag.getBoolean("powered");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTag) {
		super.writeToNBT(nbtTag);
		nbtTag.setByte("Orientation", (byte) facingMeta);
		nbtTag.setFloat("Energy", energy);
		nbtTag.setInteger("direction", facingMeta);
		this.theTank.writeToNBT(nbtTag);
		nbtTag.setBoolean("powered", this.powered);
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
    }

	public void handlePacketDataFromServer(byte orientation) {
		facingMeta = orientation;
	}
	public static boolean isNotHost(World world) {
		return world.isRemote;
	}
	public float getEnergy() {
		return this.energy;
	}
	public float getCurrentOutput() {
		return this.currentOutput;
	}
	public boolean isPowered(){
		return powered;
	}
	public void setIsPowered(boolean power){
		powered = power;
	}
	public boolean isProducing(){
		return producing ;
	}
	public void setIsProducing(boolean producing){
		this.producing = producing;
	}
	public void addEnergy(float addition) {
		this.energy += addition;

		if (this.energy > maxEnergy())
			this.energy = maxEnergy();
	}

	public void subtractEnergy(float subtraction)
	{
		this.energy -= subtraction;
		if (this.energy < 0.0F)
			this.energy = 0.0F;
	}
	public float extractEnergy(float max, boolean doExtract)
	{

		float combinedMax = maxEnergyExtracted() + this.extraEnergy * 0.5F;
		float actualMax;
		if (max > combinedMax)
			actualMax = combinedMax;
		else
			actualMax = max;
		float extracted;
		if (this.energy >= actualMax) {
			extracted = actualMax;
			if (doExtract) {
				this.energy -= actualMax;
				this.extraEnergy -= Math.min(actualMax, this.extraEnergy);
			}
		} else {
			extracted = this.energy;
			if (doExtract) {
				this.energy = 0.0F;
				this.extraEnergy = 0.0F;
			}
		}

		return extracted;
	}
	public boolean isSimulating() {
		return !FMLCommonHandler.instance().getEffectiveSide().isClient();
	}
	public float getMaxOutputMJ()
	{
		return this.OUTPUT_MJ;
	}

	public int dieselUsedPerTick()
	{
		return 1;
	}

	public int maxEnergy()
	{
		return 30000;
	}

	public int maxEnergyReceived()
	{
		return 1200;
	}

	public int maxEnergyExtracted()
	{
		return 160;
	}

	/**
	 * used by the GUI
	 * @return
	 */
	public int getLiquidAmount() {
		return (amountClient);
	}

	/**
	 * used by the GUI
	 * @return int
	 */
	public int getLiquidItemIDClient() {
		return liquidItemIDClient;
	}

	public StandardTank getTank() {
		return theTank;
	}
	public int getTankCapacity() {
		return maxTank;
	}

	private boolean placeInInvent(ItemStack itemstack1, int i, boolean doAdd) {
		if (dieselItemStacks[i] == null) {
			if (doAdd)
				dieselItemStacks[i] = itemstack1;
			return true;
		}
		else if (dieselItemStacks[i] != null && dieselItemStacks[i].getItem() == itemstack1.getItem() && itemstack1.isStackable() && (!itemstack1.getHasSubtypes() || dieselItemStacks[i].getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(dieselItemStacks[i], itemstack1)) {
			int var9 = dieselItemStacks[i].stackSize + itemstack1.stackSize;
			if (var9 <= itemstack1.getMaxStackSize()) {
				if (doAdd)
					dieselItemStacks[i].stackSize = var9;

			}
			else if (dieselItemStacks[i].stackSize < itemstack1.getMaxStackSize()) {
				if (doAdd)
					dieselItemStacks[i].stackSize += 1;
			}
			return true;
		}
		return false;

	}
	@Override
	public int getSizeInventory() {
		return dieselItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return dieselItemStacks[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (dieselItemStacks[i] != null) {
			if (dieselItemStacks[i].stackSize <= j) {
				ItemStack itemstack = dieselItemStacks[i];
				dieselItemStacks[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = dieselItemStacks[i].splitStack(j);
			if (dieselItemStacks[i].stackSize == 0) {
				dieselItemStacks[i] = null;
			}
			return itemstack1;
		}
		else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (this.dieselItemStacks[par1] != null) {
			ItemStack var2 = this.dieselItemStacks[par1];
			this.dieselItemStacks[par1] = null;
			return var2;
		}
		else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		dieselItemStacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return "Diesel Generator";
	}
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (worldObj == null) {
			return true;
		}
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		}
		return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}
	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public World getWorldObj() {
		return this.worldObj;
	}

	public FluidStack getFluid()
	{
		return theTank.getFluid();
	}

	public int getFluidAmount()
	{
		return theTank.getFluidAmount();
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return theTank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if (resource == null || !resource.isFluidEqual(theTank.getFluid()))
		{
			return null;
		}
		return theTank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return theTank.drain(maxDrain, doDrain);
	}

	public int getCapacity() {
		return this.maxTank;
	}
	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		return new FluidTankInfo[] { theTank.getInfo() };
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from)
	{
		return true;
	}

	@Override
	public int getSourceTier()
	{
		return 2;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
	{

		int energyExtracted = Math.min(this.getEnergyStored(from), Math.min(800, maxExtract));
		if (!simulate)
		{
			energy -= energyExtracted;
		}
		return energyExtracted;
	}

	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		return (int) (this.energy / 5);
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from)
	{
		return (int) (this.energy / 5);
	}

}
