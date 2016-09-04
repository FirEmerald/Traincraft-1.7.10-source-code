package src.train.common.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import src.train.common.api.LiquidManager.StandardTank;
import buildcraft.api.tools.IToolWrench;

public class LiquidTank extends EntityRollingStock implements IFluidHandler, IInventory {
	private FluidStack liquid;
	private int capacity;
	protected ItemStack cargoItems[];
	private int update = 8;
	private StandardTank theTank;
	private IFluidTank[] tankArray = new IFluidTank[1];

	/**
	 * 
	 * @param world
	 * @param liquidId
	 * @param quantity
	 * @param capacity
	 */
	public LiquidTank(World world, int liquidId, int quantity, int capacity) {
		this(world, liquidId, quantity, capacity, null, false);
	}

	public LiquidTank(World world, int liquidId, int quantity, int capacity, FluidStack filter) {
		this(world, liquidId, quantity, capacity, filter, false);
	}
	
	public LiquidTank(World world, int liquidId, int quantity, int capacity, FluidStack filter, boolean reverseSort) {
		super(world);
		Fluid fluid = FluidRegistry.getFluid(liquidId);
		if (fluid == null) fluid = FluidRegistry.WATER;
		this.liquid = new FluidStack(fluid, quantity);
		this.capacity = capacity;
		if(filter == null)
			this.theTank = LiquidManager.getInstance().new StandardTank(capacity);
		if(filter != null)
			this.theTank = LiquidManager.getInstance().new FilteredTank(capacity, filter);
		if(filter != null && reverseSort)
			this.theTank = LiquidManager.getInstance().new ReverseFilteredTank(capacity, filter);
		tankArray[0] = theTank;
		dataWatcher.addObject(4, new Integer(0));
		dataWatcher.addObject(22, new String(""));
	}

	private LiquidTank(FluidStack liquid, int capacity, World world, FluidStack filter, boolean reverseSort) {
		super(world);
		this.liquid = liquid;
		this.capacity = capacity;
		if(filter == null)
			this.theTank = LiquidManager.getInstance().new StandardTank(capacity);
		if(filter != null)
			this.theTank = LiquidManager.getInstance().new FilteredTank(capacity, filter);
		if(filter != null && reverseSort)
			this.theTank = LiquidManager.getInstance().new ReverseFilteredTank(capacity, filter);
		tankArray[0] = theTank;
		dataWatcher.addObject(4, new Integer(0));
		dataWatcher.addObject(22, new String(""));

	}

	public int getAmount() {
		return (this.dataWatcher.getWatchableObjectInt(18));
	}

	public int getLiquidItemID() {
		return (this.dataWatcher.getWatchableObjectInt(4));
	}

	public String getLiquidName() {
		return (this.dataWatcher.getWatchableObjectString(22));
	}

	public StandardTank getTank() {
		return theTank;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		this.theTank.writeToNBT(nbttagcompound);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		this.theTank.readFromNBT(nbttagcompound);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (worldObj.isRemote)
			return;
		if (theTank != null && theTank.getFluid() != null) {
			this.dataWatcher.updateObject(18, theTank.getFluid().amount);
			this.dataWatcher.updateObject(4, theTank.getFluid().getFluidID());
			if (theTank.getFluid().getFluid() != null)
				this.dataWatcher.updateObject(22, theTank.getFluid().getFluid().getName());
			handleMass();
		}
		else if (theTank != null && theTank.getFluid() == null) {
			this.dataWatcher.updateObject(18, 0);
			this.dataWatcher.updateObject(4, 0);
			this.dataWatcher.updateObject(22, "");
		}
	}

	/**
	 * Handle mass depending on liquid amount
	 */
	protected void handleMass() {
		if (this.updateTicks % 10 != 0 && theTank.getFluid().amount > 0) {
			this.mass = this.getDefaultMass();
			double preciseAmount = theTank.getFluid().amount;
			mass += (preciseAmount / 10000);//1 bucket = 1 kilo
		}
	}

	public ItemStack checkInvent(ItemStack itemstack) {
		ItemStack result = null;
		if (worldObj.isRemote)
			return itemstack;
		this.update += 1;
		if (this.update % 8 == 0 && itemstack != null) {
			ItemStack emptyItem = itemstack.getItem().getContainerItem(itemstack);
			if(cargoItems[1] == null) {
				result = LiquidManager.getInstance().processContainer(this, 0, theTank, itemstack, 0);
			}
			else if(emptyItem != null) {
				if(cargoItems[1] != null && emptyItem.getItem() == cargoItems[1].getItem()) {
    				if(cargoItems[1].stackSize+1 < cargoItems[1].getMaxStackSize()) {
    					result = LiquidManager.getInstance().processContainer(this, 0, theTank, itemstack, 0);
    				}
				}
			}
			else {
				if(cargoItems[1] != null && itemstack.getItem() == cargoItems[1].getItem()) {
    				if(cargoItems[1].stackSize+1 <= cargoItems[1].getMaxStackSize()) {
    					result = LiquidManager.getInstance().processContainer(this, 0, theTank, itemstack, 0);
    				}
				}
			}

			if (result != null) {
				if(cargoItems[1] == null) {
					cargoItems[1] = result;
				}
				else if(cargoItems[1].getItem() == result.getItem()) {
					cargoItems[1].stackSize += 1;
				}
			}
		}
		return itemstack;
	}

	public void setLiquid(FluidStack liquid) {
		this.liquid = liquid;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getCapacity() {
		return this.capacity;
	}

	private int placeInSpecialInvent(ItemStack itemstack1, int i, boolean doAdd) {
		if (cargoItems[i] == null) {
			if (doAdd)
				cargoItems[i] = itemstack1;
			return itemstack1.stackSize;
		}
		else if (cargoItems[i] != null && cargoItems[i].getItem() == itemstack1.getItem() && itemstack1.isStackable() && (!itemstack1.getHasSubtypes() || cargoItems[i].getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(cargoItems[i], itemstack1)) {

			int var9 = cargoItems[i].stackSize + itemstack1.stackSize;
			if (var9 <= itemstack1.getMaxStackSize()) {
				if (doAdd)
					cargoItems[i].stackSize = var9;
				return var9;
			}
			else if (cargoItems[i].stackSize < itemstack1.getMaxStackSize()) {
				if (doAdd)
					cargoItems[i].stackSize = cargoItems[i].getMaxStackSize();
				return Math.abs(cargoItems[i].getMaxStackSize() - cargoItems[i].stackSize - itemstack1.stackSize);
			}
		}
		return itemstack1.stackSize;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return cargoItems[i];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (this.cargoItems[par1] != null) {
			ItemStack var2 = this.cargoItems[par1];
			this.cargoItems[par1] = null;
			return var2;
		}
		else {
			return null;
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (cargoItems[i] != null) {
			if (cargoItems[i].stackSize <= j) {
				ItemStack itemstack = cargoItems[i];
				cargoItems[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = cargoItems[i].splitStack(j);
			if (cargoItems[i].stackSize == 0) {
				cargoItems[i] = null;
			}
			return itemstack1;
		}
		else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		cargoItems[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public void markDirty() {}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		return cargoItems.length;
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (worldObj.isRemote) {
			return true;
		}
		if (canBeDestroyedByPlayer(damagesource))
			return true;
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

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	public FluidStack getFluid() {
		return theTank.getFluid();
	}

	public int getFluidAmount() {
		return theTank.getFluidAmount();
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return theTank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource == null || !resource.isFluidEqual(theTank.getFluid())) {
			return null;
		}
		return theTank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return theTank.drain(maxDrain, doDrain);
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
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { theTank.getInfo() };
	}
}
