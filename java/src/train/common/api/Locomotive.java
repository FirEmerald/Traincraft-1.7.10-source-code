package src.train.common.api;

import ic2.api.item.IElectricItem;
import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Keyboard;

import src.train.client.core.handlers.TCKeyHandler;
import src.train.common.Traincraft;
import src.train.common.core.HandleMaxAttachedCarts;
import src.train.common.core.handlers.ConfigHandler;
import src.train.common.core.handlers.FuelHandler;
import src.train.common.core.handlers.PacketHandler;
import src.train.common.library.EnumSounds;
import src.train.common.library.Info;
import cpw.mods.fml.common.FMLCommonHandler;

public abstract class Locomotive extends EntityRollingStock implements ISidedInventory {
	public int inventorySize;
	public double speedDivider = 3.6;
	public ItemStack locoInvent[];
	private int soundPosition = 0;
	public boolean parkingBrake = false;
	private int whistleDelay = 0;
	private int blowUpDelay = 0;
	private String lastRider = "";
	private Entity lastEntityRider;
	public int numCargoSlots;
	public int numCargoSlots1;
	public int numCargoSlots2;
	private boolean hasDrowned = false;
	protected boolean canCheckInvent = true;
	private int slotsFilled = 0;
	private int fuelUpdateTicks = 0;
	private boolean isLocoTurnedOn = false;

	/**
	 * state of the loco
	 */
	private String locoState = "";
	/**
	 * false if linked carts have no effect on the velocity of this cart. Use
	 * carefully, if you link two carts that can't be adjusted, it will behave
	 * as if they are not linked.
	 */
	protected boolean canBeAdjusted = false;

	/**
	 * These variables are used to display changes in the GUI
	 */
	public int currentNumCartsPulled = 0;
	public double currentMassPulled = 0;
	public double currentSpeedSlowDown = 0;
	public double currentAccelSlowDown = 0;
	public double currentBrakeSlowDown = 0;
	public double currentFuelConsumptionChange = 0;

	/**
	 * used internally inside each loco to set the fuel consumption
	 */
	protected int fuelRate;
	/**
	 * This is for the "can pull" feature It is used to avoid conflict with
	 * isCartLockDown @see EntityRollingStock line 422 This is set in @see
	 * TrainsOnClick
	 */
	public boolean canBePulled = false;

	public HandleMaxAttachedCarts maxAttached = new HandleMaxAttachedCarts();

	public Locomotive(World world) {
		super(world);
		setFuelConsumption(0);
		inventorySize = numCargoSlots + numCargoSlots2 + numCargoSlots1;
		dataWatcher.addObject(2, new Integer((int) 0));
		this.setDefaultMass(0);
		this.setCustomSpeed(getMaxSpeed());
		dataWatcher.addObject(3, new String(destination));
		dataWatcher.addObject(22, new String(locoState));
		dataWatcher.addObject(24, new Integer(fuelTrain));
		dataWatcher.addObject(25, new Integer((int) convertSpeed(Math.sqrt(Math.abs(motionX * motionX) + Math.abs(motionZ * motionZ)))));//convertSpeed((Math.abs(this.motionX) + Math.abs(this.motionZ))
		dataWatcher.addObject(26, new String(castToString(currentNumCartsPulled)));
		dataWatcher.addObject(27, new String(castToString(currentMassPulled)));
		dataWatcher.addObject(28, new String(castToString(Math.round(currentSpeedSlowDown))));
		dataWatcher.addObject(29, new String(castToString(currentAccelSlowDown)));
		dataWatcher.addObject(30, new String(castToString(currentBrakeSlowDown)));
		dataWatcher.addObject(31, new String(castToString(currentFuelConsumptionChange)));
		dataWatcher.addObject(15, new String(castToString(Math.round((getCustomSpeed() * 3.6)))));
		setAccel(0);
		setBrake(0);
		this.entityCollisionReduction = 0.99F;
		if(this instanceof SteamTrain)isLocoTurnedOn = true;
	}

	private String castToString(double str) {
		return "" + str;
	}

	@Override
	public boolean isPoweredCart() {
		return true;
	}

	@Override
	public boolean canBeRidden() {
		return true;
	}

	/**
	 * To disable linking altogether, return false here.
	 * 
	 * @return True if this cart is linkable.
	 */
	@Override
	public boolean isLinkable() {
		return false;
	}

	/**
	 * Returns true if this cart is a storage cart Some carts may have
	 * inventories but not be storage carts and some carts without inventories
	 * may be storage carts.
	 * 
	 * @return True if this cart should be classified as a storage cart.
	 */
	@Override
	public boolean isStorageCart() {
		return false;
	}

	protected int getCurrentMaxSpeed() {
		return (int) (dataWatcher.getWatchableObjectInt(2));
	}

	protected void setCurrentMaxSpeed(int maxSpeed) {
		if (!worldObj.isRemote) {
			dataWatcher.updateObject(2, maxSpeed);
		}
	}

	/**
	 * set the max speed in km/h if the param is 0 then the default speed is
	 * used
	 * 
	 * @param speed
	 */
	public void setCustomSpeed(double m) {
		if (m != 0) {
			setCurrentMaxSpeed((int) m);
			return;
		}
		setCurrentMaxSpeed((int) this.getMaxSpeed());
	}

	/**
	 * returns the absolute maximum speed of the given locomotive (speed in
	 * km/h) divided by 3.6 to get ms
	 * 
	 * @return double
	 */
	public double getMaxSpeed() {
		if (trainSpec != null) { return trainSpec.getMaxSpeed(); }
		return 50;
	}

	/**
	 * returns the current maximum speed of the given locomotive (speed in km/h)
	 * divided by 3.6 to get ms
	 * 
	 * @return double
	 */
	public double getCustomSpeed() {
		return getCurrentMaxSpeed() / speedDivider;
	}

	@Override
	public boolean canOverheat() {
		return getOverheatTime() > 0;
	}

	@Override
	public int getOverheatTime() {
		if (trainSpec != null) { return trainSpec.getHeatingTime(); }
		return 0;
	}

	/**
	 * set the fuel consumption rate for each loco if i is 0 then default
	 * consumption is used
	 * 
	 * @param i
	 * @return
	 */
	public int setFuelConsumption(int c) {
		if (c != 0) { return fuelRate = c; }
		if (trainSpec != null) { return fuelRate = trainSpec.getFuelConsumption(); }
		return 0;

	}

	/**
	 * returns the fuel consumption rate for each loco
	 * 
	 * @return int
	 */
	public int getFuelConsumption() {
		return fuelRate;
	}

	/**
	 * Return the power of the loco, used for cart pulling
	 * 
	 * @see HandleMaxAttachedCarts for calculations
	 */
	public int getPower() {
		if (trainSpec != null) { return trainSpec.getMHP(); }
		return 0;
	}

	/**
	 * Set acceleration rate if rate = 0, default value is used
	 * 
	 * @param rate
	 */
	public double setAccel(double rate) {
		if (rate != 0) {
			return accelerate = rate;
		}
		else {
			if (trainSpec != null) { return accelerate = trainSpec.getAccelerationRate(); }
			return 0.45;
		}
	}

	/**
	 * Set brake rate if rate = 0, default value is used
	 * 
	 * @param rate
	 */
	public double setBrake(double rate) {
		if (rate != 0) {
			return brake = rate;
		}
		else {
			if (trainSpec != null) { return brake = trainSpec.getBrakeRate(); }
			return 0.98;
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("canBeAdjusted", canBeAdjusted);
		nbttagcompound.setBoolean("canBePulled", canBePulled);
		nbttagcompound.setInteger("overheatLevel", (int) getOverheatLevel());
		nbttagcompound.setString("lastRider", lastRider);
		nbttagcompound.setString("destination", destination);
		nbttagcompound.setBoolean("parkingBrake", this.getParkingBrakeFromPacket());
		if(!(this instanceof SteamTrain))nbttagcompound.setBoolean("isLocoTurnedOn", isLocoTurnedOn);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		canBeAdjusted = nbttagcompound.getBoolean("canBeAdjusted");
		canBePulled = nbttagcompound.getBoolean("canBePulled");
		setOverheatLevel(nbttagcompound.getInteger("overheatLevel"));
		lastRider = nbttagcompound.getString("lastRider");
		destination = nbttagcompound.getString("destination");
		this.setParkingBrakeFromPacket(nbttagcompound.getBoolean("parkingBrake"));
		if(!(this instanceof SteamTrain))isLocoTurnedOn = nbttagcompound.getBoolean("isLocoTurnedOn");
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities
	 * when colliding.
	 */
	@Override
	public boolean canBePushed() {
		return false;
	}

	public void setCanBeAdjusted(boolean canBeAdj) {
		this.canBeAdjusted = canBeAdj;
	}

	/**
	 * gets packet from server and distribute for GUI handles motion
	 * 
	 * @param i
	 */
	@Override
	public void keyHandlerFromPacket(int i)
	{
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer)
		{
			PacketHandler.sendPacketToClients(PacketHandler.setTrainLockedToClient(riddenByEntity, this, locked), worldObj, (int) posX, (int) posY, (int) posZ, 5);
		}
		if (this.getTrainLockedFromPacket())
		{
			if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && !((EntityPlayer) this.riddenByEntity).getGameProfile().getId().equals(this.trainOwner)) { return; }
		}
		pressKey(i);
		//System.out.println(i);
		if (i == 8 && ConfigHandler.SOUNDS)
		{
			soundHorn();
		}
		if (i == 11)
		{

    		if (parkingBrake) parkingBrake = false;
    		else
    		{
    			if (getSpeed() < 10) parkingBrake = true;
    			else if (this.riddenByEntity instanceof ICommandSender) ((ICommandSender) this.riddenByEntity).addChatMessage(new ChatComponentText("Stop it before turning it off!"));
    		}
    		PacketHandler.sendPacketToClients(PacketHandler.setParkingBrake(riddenByEntity, this, parkingBrake, false), worldObj, (int) posX, (int) posY, (int) posZ, 5);
		}
	}

	/**
	 * All this is used in GUI only
	 * 
	 * @return
	 */
	public String getCurrentNumCartsPulled() {
		return (this.dataWatcher.getWatchableObjectString(26));
	}

	public String getCurrentMassPulled() {
		return (this.dataWatcher.getWatchableObjectString(27));
	}

	public String getCurrentSpeedSlowDown() {
		return (this.dataWatcher.getWatchableObjectString(28));
	}

	public String getCurrentAccelSlowDown() {
		return (this.dataWatcher.getWatchableObjectString(29));
	}

	public String getCurrentBrakeSlowDown() {
		return (this.dataWatcher.getWatchableObjectString(30));
	}

	public String getCurrentFuelConsumptionChange() {
		return (this.dataWatcher.getWatchableObjectString(31));
	}

	public String getCustomSpeedGUI() {
		return (this.dataWatcher.getWatchableObjectString(15));
	}

	public String getDestinationGUI() {
		if (worldObj.isRemote) { return (this.dataWatcher.getWatchableObjectString(3)); }
		return destination;
	}

	private double convertSpeed(double speed) {
		//System.out.println("X "+motionX +" Z "+motionZ);
		if (ConfigHandler.REAL_TRAIN_SPEED) {
			speed *= 2;// applying ratio
		}
		else {
			speed *= 6;
		}
		speed *= 10;
		speed *= 3.6;
		//speed *= 10;// convert in ms
		//speed *= 6;// applying ratio
		//speed *= 3.6;// convert in km/h
		return speed;
	}

	public void soundHorn()
	{
		for (EnumSounds sounds : EnumSounds.values())
		{
			if (sounds.getEntityClass() == this.getClass() && whistleDelay == 0)
			{
				worldObj.playSoundAtEntity(this, Info.resourceLocation + ":" + sounds.getHornString(), sounds.getHornVolume(), 1.0F);
				whistleDelay = 65;
			}
		}
	}

	@Override
	public void onUpdate()
	{
		if (!worldObj.isRemote)
		{
	        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase)
	        {
	        	EntityLivingBase entity = (EntityLivingBase)this.riddenByEntity;
	        	if (entity.moveForward > 0)
	        	{
					if (getFuel() > 0 && this.isLocoTurnedOn())
					{
						int dir = MathHelper.floor_double((double) ((entity.rotationYaw * 4F) / 360F) + 0.5D) & 3;
						if (dir == 2) motionZ -= entity.moveForward * 0.0025 * this.accelerate;
						if (dir == 1) motionX -= entity.moveForward * 0.0025 * this.accelerate;
						if (dir == 0) motionZ += entity.moveForward * 0.0025 * this.accelerate;
						if (dir == 3) motionX += entity.moveForward * 0.0025 * this.accelerate;
					}
	        	}
	        	else if (entity.moveForward < 0)
	        	{
					motionX *= brake * -entity.moveForward;
					motionZ *= brake * -entity.moveForward;
	        	}
	        }
			if (updateTicks % 50 == 0) {
				PacketHandler.sendPacketToClients(PacketHandler.setParkingBrake(riddenByEntity, this, parkingBrake, false), worldObj, (int) posX, (int) posY, (int) posZ, 50);
				this.setLocoTurnedOn(isLocoTurnedOn, false, true,500);//sending to client
			}

			if (updateTicks % 20 == 0) maxAttached.PullPhysic(this);
			/**
			 * Can't use datawatcher here. Locomotives use them all already
			 * Check inventory The packet never arrives if it is sent when the
			 * entity reads its NBT (player hasn't been initialised probably)
			 */
			if (updateTicks % 200 == 0) {
				this.slotsFilled = 0;
				for (int i = 0; i < getSizeInventory(); i++) {
					ItemStack itemstack = getStackInSlot(i);
					if (itemstack != null) {
						slotsFilled++;
					}
				}
				PacketHandler.sendPacketToClients(PacketHandler.setSlotsFilledPacket(this, slotsFilled), worldObj, (int) posX, (int) posY, (int) posZ, 150);
			}
			/**
			 * Fuel consumption
			 */
			int consumption = ((Locomotive) this).getFuelConsumption();
			if (this instanceof DieselTrain) consumption /= 5;
			if (fuelUpdateTicks >= consumption) {
				fuelUpdateTicks = 0;
				updateFuelTrain();
			}
			fuelUpdateTicks++;

			if (!this.isLocoTurnedOn()) {
				motionX *= brake;
				motionZ *= brake;
			}

		}
		if (whistleDelay > 0) {
			whistleDelay--;
		}
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer) {
			this.lastRider = ((EntityPlayer) this.riddenByEntity).getGameProfile().getId().toString();
			this.lastEntityRider = (this.riddenByEntity);
		}

		if (!this.worldObj.isRemote && this.getParkingBrakeFromPacket() && getState() != "broken") {
			motionX *= 0.0;
			motionZ *= 0.0;
		}
		if (ConfigHandler.SOUNDS) {
			for (EnumSounds sounds : EnumSounds.values()) {
				if (sounds.getEntityClass() != null && sounds.getEntityClass().equals(this.getClass())) {
					if (getFuel() > 0 && this.isLocoTurnedOn()) {
						double speed = Math.sqrt(motionX * motionX + motionZ * motionZ);
						if (speed > -0.001D && speed < 0.01D && soundPosition == 0) {
							worldObj.playSoundAtEntity(this, Info.resourceLocation + ":" + sounds.getIdleString(), sounds.getIdleVolume(), 0.001F);
							soundPosition = (int) sounds.getIdleSoundLenght();
						}
						if (sounds.getSoundChangeWithSpeed()) {
							if (speed > 0.01D && speed < 0.06D && soundPosition == 0) {
								worldObj.playSoundAtEntity(this, Info.resourceLocation + ":" + sounds.getRunString(), sounds.getRunVolume(), 0.1F);
								soundPosition = (int) sounds.getRunSoundLenght();
							}
							else if (speed > 0.06D && speed < 0.2D && soundPosition == 0) {
								worldObj.playSoundAtEntity(this, Info.resourceLocation + ":" + sounds.getRunString(), sounds.getRunVolume(), 0.4F);
								soundPosition = (int) sounds.getRunSoundLenght() / 2;
							}
							else if (speed > 0.2D && soundPosition == 0) {
								worldObj.playSoundAtEntity(this, Info.resourceLocation + ":" + sounds.getRunString(), sounds.getRunVolume(), 0.5F);
								soundPosition = (int) sounds.getRunSoundLenght() / 3;
							}
						}
						else {
							if (speed > 0.01D && soundPosition == 0) {
								worldObj.playSoundAtEntity(this, Info.resourceLocation + ":" + sounds.getRunString(), sounds.getRunVolume(), 0.4F);
								soundPosition = (int) sounds.getRunSoundLenght();
							}
						}
						if (soundPosition > 0) {
							soundPosition--;
						}
					}
					break;
				}
			}
		}
		if (getState() == "cold") {
			this.extinguish();
			if (getCurrentMaxSpeed() >= (getMaxSpeed() * 0.6)) {
				motionX *= 0.0;
				motionZ *= 0.0;
			}
		}
		if (getState() == "warm") {
			this.extinguish();
			if (getCurrentMaxSpeed() >= (getMaxSpeed() * 0.7)) {
				motionX *= 0.94;
				motionZ *= 0.94;
			}
		}
		if (getState() == "hot") {
			this.extinguish();
			if (getCurrentMaxSpeed() <= (getMaxSpeed())) {}
		}
		if (getState() == "very hot") {}
		if (getState() == "too hot") {
			motionX *= 0.95;
			motionZ *= 0.95;
			worldObj.spawnParticle("largesmoke", posX, posY + 0.3, posZ, 0.0D, 0.0D, 0.0D);
		}
		if (getState() == "broken") {
			setFire(8);
			((Locomotive) this).setCustomSpeed(0);// set speed to normal
			((Locomotive) this).setAccel(0.000001);// simulate a break down
			((Locomotive) this).setBrake(1);
			this.motionX *= 0.97;// slowly slows down
			this.motionZ *= 0.97;
			worldObj.spawnParticle("largesmoke", posX, posY + 0.3, posZ, 0.0D, 0.0D, 0.0D);
			worldObj.spawnParticle("largesmoke", posX, posY + 0.3, posZ, 0.0D, 0.0D, 0.0D);
			blowUpDelay++;
			if (blowUpDelay > 80) {
				if (!worldObj.isRemote) {
					//worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 0.5F, true);
					worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 0.5F, false);
					this.setDead();
				}
				if (!worldObj.isRemote && FMLCommonHandler.instance().getMinecraftServerInstance() != null && this.lastEntityRider != null && this.lastEntityRider instanceof EntityPlayer) {
					FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendChatMsg(new ChatComponentText(((EntityPlayer) this.lastEntityRider).getGameProfile().getName() + " blew " + this.trainOwner + "'s locomotive"));
				}
				if (!worldObj.isRemote) statsEventHandler.trainExplode(this.uniqueID, this.trainName, this.trainType, this.trainCreator, new String((int) posX + ";" + (int) posY + ";" + (int) posZ));
				// if (!worldObj.isRemote)PacketHandler.sendPacketToClients(PacketHandler.sendStatsToServer(7,this.uniqueID, this.trainName, this.trainType, this.trainOwner,"", (int) posX, (int) posY, (int) posZ),this.worldObj, (int)posX,(int)posY,(int)posZ, 12.0D);
			}
		}

		super.onUpdate();
		if (!worldObj.isRemote) {
			//System.out.println(motionX +" "+motionZ);
			dataWatcher.updateObject(25, (int) convertSpeed(Math.sqrt(motionX * motionX + motionZ * motionZ)));
			dataWatcher.updateObject(24, fuelTrain);
			dataWatcher.updateObject(20, overheatLevel);
			dataWatcher.updateObject(22, locoState);
			dataWatcher.updateObject(3, destination);
			dataWatcher.updateObject(26, (castToString(currentNumCartsPulled)));
			dataWatcher.updateObject(27, (castToString((currentMassPulled)) + " tons"));
			dataWatcher.updateObject(28, (castToString((int) currentSpeedSlowDown) + " km/h"));
			dataWatcher.updateObject(29, (castToString((double) (Math.round(currentAccelSlowDown * 1000)) / 1000)));
			dataWatcher.updateObject(30, (castToString((double) (Math.round(currentBrakeSlowDown * 1000)) / 1000)));
			dataWatcher.updateObject(31, ("1c/" + castToString((int) (currentFuelConsumptionChange)) + " per tick"));
			dataWatcher.updateObject(15, castToString((int) (getCustomSpeed() * 3.6)) + " km/h");
			//System.out.println();
			if (this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.2000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this) && this.updateTicks % 4 == 0) {
				if (!hasDrowned && !worldObj.isRemote && FMLCommonHandler.instance().getMinecraftServerInstance() != null && this.lastEntityRider != null && this.lastEntityRider instanceof EntityPlayer) {
					FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendChatMsg(new ChatComponentText(((EntityPlayer) this.lastEntityRider).getGameProfile().getName() + " drowned " + this.trainOwner + "'s locomotive"));
				}
				//this.attackEntityFrom(DamageSource.generic, 100);
				((Locomotive) this).setCustomSpeed(0);// set speed to normal
				((Locomotive) this).setAccel(0.000001);// simulate a break down
				((Locomotive) this).setBrake(1);
				this.motionX *= 0.97;// slowly slows down
				this.motionZ *= 0.97;
				this.fuelTrain = 0;
				this.hasDrowned = true;
				this.canCheckInvent = false;
				blowUpDelay++;
				if (blowUpDelay > 20) {
					this.attackEntityFrom(DamageSource.drown, 100);
				}
			}/*
			 * else{ this.canCheckInvent=true; this.hasDrowned=false; }
			 */
		}
	}

	@Override
	protected void applyDragAndPushForces() {
		motionX *= getDragAir();
		motionY *= 0.0D;
		motionZ *= getDragAir();
	}

	/**
	 * Carts should return their drag factor here
	 * 
	 * @return The drag rate.
	 */
	@Override
	public double getDragAir() {
		return 1D;
	}

	/**
	 * Added for SMP
	 * 
	 * @return true if on, false if off
	 */
	public boolean getParkingBrakeFromPacket() {
		return parkingBrake;
	}

	public void sendParkingBrakePacket(boolean packet) {
		PacketHandler.setParkingBrake(this.riddenByEntity, this, packet, true);
	}

	/**
	 * Added for SMP
	 * 
	 * @param set
	 *            set 0 if parking break is false, 1 if true
	 */
	public void setParkingBrakeFromPacket(boolean set) {
		parkingBrake = set;
	}

	/**
	 * added for SMP, used by the HUD
	 * 
	 * @return
	 */
	public double getSpeed() {
		return dataWatcher.getWatchableObjectInt(25);
	}

	/**
	 * added for SMP, used by the HUD
	 * 
	 * @return
	 */
	@Override
	public int getOverheatLevel() {
		return (this.dataWatcher.getWatchableObjectInt(20));
	}

	/**
	 * returns the state of the loco state is the consequence of overheating
	 * 
	 * @return cold warm hot very hot too hot broken
	 */
	public String getState() {
		return (this.dataWatcher.getWatchableObjectString(22));
	}

	/**
	 * set the state of the loco
	 * 
	 * @param state
	 *            cold warm hot very hot too hot broken
	 */
	public void setState(String state) {
		locoState = state;
		this.dataWatcher.updateObject(22, state);
	}

	/**
	 * added for SMP, used by the HUD
	 * 
	 * @return
	 */
	public int getFuel() {
		if (worldObj.isRemote) { return (this.dataWatcher.getWatchableObjectInt(24)); }
		return fuelTrain;
	}

	/**
	 * Is it fuelled? used in GUI
	 */
	public boolean getIsFuelled() {
		if (worldObj.isRemote) { return (this.dataWatcher.getWatchableObjectInt(24)) > 0; }
		return (this.fuelTrain > 0);
	}

	/** Used for the gui */
	public int getFuelDiv(int i) {
		if (worldObj.isRemote) { return ((this.dataWatcher.getWatchableObjectInt(24) * i) / 1200); }
		return (this.fuelTrain * i) / 1200;
	}

	/**
	 * This code applies fuel consumption.
	 */
	protected void updateFuelTrain() {
		if (fuelTrain < 0) {
			motionX *= 0.8;
			motionZ *= 0.8;
		}
		else {
			if (this.isLocoTurnedOn()) fuelTrain--;
		}
	}

	public void setLocoTurnedOn(boolean set, boolean toServer, boolean toClient, double distance) {
		this.isLocoTurnedOn = set;
		if (toServer) PacketHandler.setLocoTurnedOn(this.riddenByEntity, this, set, true);
		if (toClient) PacketHandler.sendPacketToClients(PacketHandler.setLocoTurnedOn(riddenByEntity, this, set, false), worldObj, (int) posX, (int) posY, (int) posZ, distance);
	}

	public boolean isLocoTurnedOn() {
		return this.isLocoTurnedOn;
	}

	private int placeInSpecialInvent(ItemStack itemstack1, int i, boolean doAdd) {
		if (locoInvent[i] == null) {
			if (doAdd) locoInvent[i] = itemstack1;
			return itemstack1.stackSize;
		}
		else if (locoInvent[i] != null && locoInvent[i].getItem() == itemstack1.getItem() && itemstack1.isStackable() && (!itemstack1.getHasSubtypes() || locoInvent[i].getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(locoInvent[i], itemstack1)) {

			int var9 = locoInvent[i].stackSize + itemstack1.stackSize;
			if (var9 <= itemstack1.getMaxStackSize()) {
				if (doAdd) locoInvent[i].stackSize = var9;
				return var9;
			}
			else if (locoInvent[i].stackSize < itemstack1.getMaxStackSize()) {
				if (doAdd) locoInvent[i].stackSize = locoInvent[i].getMaxStackSize();
				return Math.abs(locoInvent[i].getMaxStackSize() - locoInvent[i].stackSize - itemstack1.stackSize);

			}
		}
		return itemstack1.stackSize;

	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (worldObj.isRemote) { return true; }
		if (canBeDestroyedByPlayer(damagesource)) return true;
		super.attackEntityFrom(damagesource, i);
		setRollingDirection(-getRollingDirection());
		setRollingAmplitude(10);
		setBeenAttacked();
		setDamage(getDamage() + i * 10);
		if (getDamage() > 40) {
			if (riddenByEntity != null) {
				riddenByEntity.mountEntity(this);
			}
			this.setDead();
			boolean flag = damagesource.getEntity() instanceof EntityPlayer && ((EntityPlayer) damagesource.getEntity()).capabilities.isCreativeMode;
			if (!flag) {
				dropCartAsItem();
			}
		}
		return true;
	}

	/** RC routing integration */
	@Override
	public boolean setDestination(ItemStack ticket) {
		if (ticket != null) {
			destination = getTicketDestination(ticket);
			return true;
		}
		return false;
	}

	/* IInventory implements */
	@Override
	public ItemStack getStackInSlot(int i) {
		return locoInvent[i];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (this.locoInvent[par1] != null) {
			ItemStack var2 = this.locoInvent[par1];
			this.locoInvent[par1] = null;
			return var2;
		}
		else {
			return null;
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (locoInvent[i] != null) {
			if (locoInvent[i].stackSize <= j) {
				ItemStack itemstack = locoInvent[i];
				locoInvent[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = locoInvent[i].splitStack(j);
			if (locoInvent[i].stackSize == 0) {
				locoInvent[i] = null;
			}
			return itemstack1;

		}
		else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		locoInvent[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public void markDirty() {
		if (!worldObj.isRemote) {
			this.slotsFilled = 0;
			for (int i = 0; i < getSizeInventory(); i++) {
				ItemStack itemstack = getStackInSlot(i);
				if (itemstack != null) {
					slotsFilled++;
				}
			}
			PacketHandler.sendPacketToClients(PacketHandler.setSlotsFilledPacket(this, slotsFilled), worldObj, (int) posX, (int) posY, (int) posZ, 150);
		}
	}

	public int getAmmountOfCargo() {
		return slotsFilled;
	}

	public void recieveSlotsFilled(int amount) {
		this.slotsFilled = amount;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_)
	{
		int j = this.getSizeInventory();
		int[] slots = new int[j];
		for (int i = 0; i < j; i++) slots[i] = i;
		return slots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side)
	{
		if (stack == null) { return false; }
		//FuelHandler.steamFuelLast(itemstack) > 0 || LiquidManager.getInstance().isDieselLocoFuel(itemstack)||(itemstack.getItem().shiftedIndex==Item.redstone.shiftedIndex) || (itemstack.getItem() instanceof IElectricItem)
		//LiquidManager.getInstance().isContainer(itemstack1)&&loco instanceof SteamTrain
		if (this instanceof SteamTrain) {
			//System.out.println("is fuel? "+(FuelHandler.steamFuelLast(stack) > 0) + "return "+placeInSpecialInvent(stack,0,false));
			if (FuelHandler.steamFuelLast(stack) > 0) return true;
			if (LiquidManager.getInstance().isContainer(stack)) return true;
		}
		if (this instanceof DieselTrain) {
			//System.out.println("is diesel? "+(LiquidManager.getInstance().isDieselLocoFuel(stack)) + "return "+placeInSpecialInvent(stack,0,false));
			if (LiquidManager.getInstance().isDieselLocoFuel(stack)) return true;
		}
		if (this instanceof ElectricTrain) {
			if ((stack.getItem() == Items.redstone) || (stack.getItem() instanceof IElectricItem) || (stack.getItem() instanceof IEnergyContainerItem)) return true;
		}
		return false;

	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_)
	{
		return false;
	}
	
	public int inventorySize()
	{
		return inventorySize;
	}
}