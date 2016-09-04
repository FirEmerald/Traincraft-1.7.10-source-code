package src.train.common.tile;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.common.MinecraftForge;
import src.train.common.Traincraft;
import src.train.common.core.TrainModBlockUtil;
import src.train.common.core.handlers.PacketHandler;
import src.train.common.library.Info;
import src.train.common.networking.setWaterWheelMetaPacket;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class TileWaterWheel extends TileEntity/*TileEntityElectrical*/ implements IEnergySource, IEnergyProvider
{
	private int facingMeta;
	private int waterDirection;
	Material blockMaterial;
	private int updateTicks=0;

	/**
	 * IC2 variable
	 */
	public boolean addedToEnergyNet = false;
	public int production = (int)(MAX_GENERATE_WATTS*TrainModBlockUtil.TO_IC2_RATIO);

	/**
	 * UE variable
	 * Maximum amount of energy needed to generate electricity
	 */
	public static final int MAX_GENERATE_WATTS = 120;

	/**
	 * UE variable
	 * Amount of heat the coal generator needs before generating electricity.
	 */
	public static final int MIN_GENERATE_WATTS = 100;
	/**
	 * UE variable
	 * Per second
	 */
	public double prevGenerateWatts, generateWatts = 0;
	/**
	 * UE variable
	 */
	/*public IConductor connectedElectricUnit = null;

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return direction == ForgeDirection.getOrientation(connectDirection())||direction == ForgeDirection.getOrientation(connectDirection()).getOpposite();
	}*/
	private int connectDirection(){
		int l = worldObj.getBlockMetadata((int)xCoord, (int)yCoord, (int)zCoord);
		if(l==3||l==1){
			return 4;
		}else{
			return 3;
		}

	}
	public TileWaterWheel()
	{
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
		this.generateWatts = nbt.getDouble("generateRate");
	}
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("Orientation", (byte) facingMeta);
		nbt.setDouble("generateRate", this.generateWatts);
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
		if(orientation!=-1)worldObj.setBlockMetadataWithNotify((int)xCoord, (int)yCoord, (int)zCoord, orientation,2);
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
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		updateTicks++;
		if (isSimulating()&&!addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			this.addedToEnergyNet = true;
		}
		if(!isSimulating()){
			double dir=0;
			Block blockXP = worldObj.getBlock((int)xCoord+1, (int)yCoord, (int)zCoord);
			Block blockXN = worldObj.getBlock((int)xCoord-1, (int)yCoord, (int)zCoord);
			Block blockZP = worldObj.getBlock((int)xCoord, (int)yCoord, (int)zCoord+1);
			Block blockZN = worldObj.getBlock((int)xCoord, (int)yCoord, (int)zCoord-1);
			Block blockTop = worldObj.getBlock((int)xCoord, (int)yCoord+1, (int)zCoord);
			Block blockBottom = worldObj.getBlock((int)xCoord, (int)yCoord-1, (int)zCoord);


			if(blockXP instanceof BlockFluidBase && this.worldObj.getBlock(this.xCoord+1, this.yCoord, this.zCoord).getMaterial().isLiquid() && worldObj.getBlockMetadata((int)xCoord+1, (int)yCoord, (int)zCoord)!= 0){
				if(this.getBlockMetadata()!=2)worldObj.setBlockMetadataWithNotify((int)xCoord, (int)yCoord, (int)zCoord, 2,2);
				blockMaterial = this.worldObj.getBlock(this.xCoord+1, this.yCoord, this.zCoord).getMaterial();
				dir = -1;
				if(blockMaterial!=blockMaterial.lava)setWaterDir((int)dir);

			}else if(blockXN instanceof BlockFluidBase && this.worldObj.getBlock(this.xCoord-1, this.yCoord, this.zCoord).getMaterial().isLiquid() && worldObj.getBlockMetadata((int)xCoord-1, (int)yCoord, (int)zCoord)!= 0){
				if(this.getBlockMetadata()!=0)worldObj.setBlockMetadataWithNotify((int)xCoord, (int)yCoord, (int)zCoord, 0,2);
				blockMaterial = this.worldObj.getBlock(this.xCoord-1, this.yCoord, this.zCoord).getMaterial();
				dir = 1;
				if(blockMaterial!=blockMaterial.lava)setWaterDir((int)dir);
			}else if(blockZN instanceof BlockFluidBase && this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord-1).getMaterial().isLiquid() && worldObj.getBlockMetadata((int)xCoord, (int)yCoord, (int)zCoord-1)!= 0){
				if(this.getBlockMetadata()!=1)worldObj.setBlockMetadataWithNotify((int)xCoord, (int)yCoord, (int)zCoord, 1,2);
				blockMaterial = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord-1).getMaterial();
				dir = -3;
				if(blockMaterial!=blockMaterial.lava)setWaterDir((int)dir);
			}else if(blockZP instanceof BlockFluidBase && this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord+1).getMaterial().isLiquid() && worldObj.getBlockMetadata((int)xCoord, (int)yCoord, (int)zCoord+1)!= 0){
				if(this.getBlockMetadata()!=3)worldObj.setBlockMetadataWithNotify((int)xCoord, (int)yCoord, (int)zCoord, 3,2);
				blockMaterial = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord+1).getMaterial();
				dir = 0;
				if(blockMaterial!=blockMaterial.lava)setWaterDir((int)dir);
			}else if(blockTop instanceof BlockFluidBase && this.worldObj.getBlock(this.xCoord, this.yCoord+1, this.zCoord).getMaterial().isLiquid()&&worldObj.getBlockMetadata((int)xCoord, (int)yCoord+1, (int)zCoord)!= 0){
				blockMaterial = this.worldObj.getBlock(this.xCoord, this.yCoord+1, this.zCoord).getMaterial();
				dir = ((BlockFluidBase)blockTop).getFlowDirection(this.worldObj,this.xCoord, this.yCoord+1, this.zCoord);
				if(blockMaterial!=blockMaterial.lava)setWaterDir((int)dir);
			}else if(blockBottom instanceof BlockFluidBase && this.worldObj.getBlock(this.xCoord, this.yCoord-1, this.zCoord).getMaterial().isLiquid() &&worldObj.getBlockMetadata((int)xCoord, (int)yCoord-1, (int)zCoord)!= 0){
				blockMaterial = this.worldObj.getBlock(this.xCoord, this.yCoord-1, this.zCoord).getMaterial();
				dir = ((BlockFluidBase)blockBottom).getFlowDirection(this.worldObj,this.xCoord, this.yCoord-1, this.zCoord);
				if((int)dir == -3||(int)dir==-2){
					dir=0;
				}else if((int)dir == 0){
					dir=-3;
				}else if((int)dir==-1){
					dir=1;
				}else if((int)dir==1){
					dir=-1;
				}
				if(blockMaterial!=blockMaterial.lava)setWaterDir((int)dir);
			}

			else{
				setWaterDir((int)-1001);
			}

			if(getWaterDir()==0 && this.getBlockMetadata() !=3){
				worldObj.setBlockMetadataWithNotify((int)xCoord, (int)yCoord, (int)zCoord, 3,2);
				sendPacketToServer(getTEPClient(3));
				//System.out.println("3");
			}
			if((getWaterDir()==-3 || getWaterDir()==-2) && this.getBlockMetadata() !=1){
				worldObj.setBlockMetadataWithNotify((int)xCoord, (int)yCoord, (int)zCoord, 1,2);
				sendPacketToServer(getTEPClient(1));
				//System.out.println("1");
			}
			if(getWaterDir()==-1 && this.getBlockMetadata() !=2){
				worldObj.setBlockMetadataWithNotify((int)xCoord, (int)yCoord, (int)zCoord, 2,2);
				sendPacketToServer(getTEPClient(2));
				//System.out.println("2");
			}
			if(getWaterDir()==1 && this.getBlockMetadata() !=0){
				worldObj.setBlockMetadataWithNotify((int)xCoord, (int)yCoord, (int)zCoord, 0,3);
				sendPacketToServer(getTEPClient(0));
				//System.out.println("0");
			}
			TileEntity tile;
			if (this.getBlockMetadata()==1 || this.getBlockMetadata()==3)
			{
				tile = worldObj.getTileEntity(xCoord - 1, yCoord, zCoord);
				if (tile instanceof IEnergyReceiver) ((IEnergyReceiver) tile).receiveEnergy(ForgeDirection.EAST, production * 5, false);
				tile = worldObj.getTileEntity(xCoord + 1, yCoord, zCoord);
				if (tile instanceof IEnergyReceiver) ((IEnergyReceiver) tile).receiveEnergy(ForgeDirection.WEST, production * 5, false);
			}
			if (this.getBlockMetadata()==2 || this.getBlockMetadata()==4)
			{
				tile = worldObj.getTileEntity(xCoord, yCoord, zCoord - 1);
				if (tile instanceof IEnergyReceiver) ((IEnergyReceiver) tile).receiveEnergy(ForgeDirection.SOUTH, production * 5, false);
				tile = worldObj.getTileEntity(xCoord, yCoord, zCoord + 1);
				if (tile instanceof IEnergyReceiver) ((IEnergyReceiver) tile).receiveEnergy(ForgeDirection.NORTH, production * 5, false);
			}
			if(this.updateTicks%60==0){
				sendPacketToServer(getTEPClient(this.getBlockMetadata()));
			}
		}
		if(worldObj.isRemote)return;

		this.prevGenerateWatts = this.generateWatts;
		/*ForgeDirection outputDirection = ForgeDirection.getOrientation(connectDirection());
		ForgeDirection outputDirection1 = ForgeDirection.getOrientation(connectDirection()).getOpposite();
		TileEntity outputTile = VectorHelper.getConnectorFromSide(this.worldObj, new Vector3(this.xCoord, this.yCoord, this.zCoord), outputDirection);
		TileEntity outputTile1 = VectorHelper.getConnectorFromSide(this.worldObj, new Vector3(this.xCoord, this.yCoord, this.zCoord), outputDirection1);

		if(outputTile!=null){
			this.produceUE(outputDirection);
		}else
		if(outputTile1!=null){
			this.produceUE(outputDirection1);
		}else*/
		/**
		 * No UE network
		 * The try output IC2 power
		 */
		//if(network==null && isSimulating()){
		if(isSimulating()){
			//if(getWaterDir()>-1001)sendEnergy(this.production);
		}

	}
	private void sendPacketToServer(IMessage tcPayloadPacket)
	{
		List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, getBlockType().getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord, zCoord).expand(10, 10, 10));//AxisAlignedBB.getAABBPool().getAABB((double)((float)this.xCoord - f), (double)this.yCoord, (double)((float)this.zCoord - f), (double)((float)(this.xCoord + 1) + f), (double)((float)(this.yCoord + 1) + f), (double)((float)(this.zCoord + 1) + f)));
		
		if (list != null && list.size() > 0) {
			for (int j1 = 0; j1 < list.size(); j1++) {
				Entity entity = (Entity) list.get(j1);
				if (entity instanceof EntityPlayer) {
					if (entity instanceof EntityPlayerMP) {
						EntityPlayerMP playerMP = (EntityPlayerMP) entity;
						Traincraft.network.sendTo(tcPayloadPacket, playerMP);
					}
				}
			}
		}
	}
	private IMessage getTEPClient(int meta)
	{
		return new setWaterWheelMetaPacket(this, (byte) meta);
	}

	private void setWaterDir(int i){
		waterDirection=i;
	}
	public int getWaterDir(){
		return waterDirection;
	}
	@Override
	public boolean canUpdate() {
		return true;
	}
	/**
	 * IC2
	 * @param send
	 * @return
	 */
	/*public int sendEnergy(int send)
	{
		EnergyTileSourceEvent event = new EnergyTileSourceEvent(this, send);
		MinecraftForge.EVENT_BUS.post(event);
		return event.amount;
	}*/
	/**
	 * IC2
	 * @param receiver
	 * @param direction
	 * @return
	 */
	/*@Override
	public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
		if((this.getBlockMetadata()==1||this.getBlockMetadata()==3) && (direction == direction.XN||direction == direction.XP)){
			return true;
		}else if((this.getBlockMetadata()==0||this.getBlockMetadata()==2) && (direction == direction.ZN||direction == direction.ZP)){
			return true;
		}
		return false;
	}*/
	/**
	 * IC2
	 * @return
	 */
	/*@Override
	public boolean isAddedToEnergyNet() {
		return this.addedToEnergyNet;
	}*/
	/**
	 * IC2
	 * @return
	 */
	//@Override
	public int getMaxEnergyOutput() {
		return this.production;
	}
	/*@Override
	public float getRequest(ForgeDirection direction) {
		return 0;
	}
	@Override
	public float getProvide(ForgeDirection direction) {
		return 0;
	}
	@Override
	public float getMaxEnergyStored() {
		return 0;
	}*/
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		if((this.getBlockMetadata()==1||this.getBlockMetadata()==3) && (direction == direction.WEST||direction == direction.EAST)){
			return true;
		}else if((this.getBlockMetadata()==0||this.getBlockMetadata()==2) && (direction == direction.NORTH||direction == direction.SOUTH)){
			return true;
		}
		return false;
	}
	@Override
	public double getOfferedEnergy() {
		if(getWaterDir()>-1001)return this.production;
		return 0;
	}
	@Override
	public void drawEnergy(double amount) {
	}
	@Override
	public boolean canConnectEnergy(ForgeDirection from)
	{
		if((this.getBlockMetadata()==1||this.getBlockMetadata()==3) && (from == ForgeDirection.WEST||from == ForgeDirection.EAST)){
			return true;
		}else if((this.getBlockMetadata()==0||this.getBlockMetadata()==2) && (from == ForgeDirection.NORTH||from == ForgeDirection.SOUTH)){
			return true;
		}
		return false;
	}
	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
	{
		return Math.min(maxExtract, getEnergyStored(from));
	}
	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		if(getWaterDir()>-1001)return this.production * 5;
		return 0;
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
