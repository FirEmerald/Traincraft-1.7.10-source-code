package src.train.common.entity.rollingStock;

import java.util.ArrayList;
import java.util.List;

import mods.railcraft.api.tracks.RailTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.BlockFluidFinite;
import src.train.common.Traincraft;
import src.train.common.api.EntityRollingStock;
import src.train.common.api.Freight;
import src.train.common.core.TrainModBlockUtil;
import src.train.common.core.handlers.BuilderOreHandler;
import src.train.common.core.handlers.FuelHandler;
import src.train.common.core.plugins.PluginRailcraft;
import src.train.common.library.BlockData;
import src.train.common.library.GuiIDs;
import src.train.common.library.ItemData;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class EntityTracksBuilder extends EntityRollingStock implements IInventory {
	protected boolean field_856_i;
	public int minecartType;
	public ItemStack item;
	private ItemStack BuilderInvent[];

	public int numBuilderSlots;
	public int numBuilderSlots1;
	public int numBuilderSlots2;
	public int numBuilderSlots3;
	public int inventoryBuilderSize;

	public double pushX;
	public double pushZ;

	public int MoreBuilderInvent;

	public int trackfuel;

	/** block under the tracks */
	//private int underBlock;
	private ItemStack underBlockStack;
	/** 2 blocks under the track */
	//private int underBlock2;
	private ItemStack underBlock2Stack;
	/** roof block on the right of the builder? */
	//private int upperBlock;
	private ItemStack upperBlockStack;
	/** roof block on the left of the builder? */
	//private int upperBlock1;
	private ItemStack upperBlock1Stack;
	/** roof block right over the track builder */
	//private int upperCenterBlock;
	private ItemStack upperCenterBlockStack;
	/** block for the building the tunnel like glass,... */
	//private int tunnelBlock;
	private ItemStack tunnelBlockStack;
	/** used to toggle tunnel on/off */
	public boolean tunnelActive;

	public double currentHeight;
	public int plannedHeight;

	EntityPlayer playerEntity;
	/** Affected by the button */
	public boolean followTracks;
	/** stack representing the tracks in the inventory */
	private ItemStack tracksStack;
	private int maxFuel = 5000;
	private int miningTickCounter = 0;

	public EntityTracksBuilder(World world) {
		super(world);
		initBuilder();
	}

	public void initBuilder() {
		numBuilderSlots = 8;
		numBuilderSlots1 = 8;
		numBuilderSlots2 = 8;
		numBuilderSlots3 = 8;
		MoreBuilderInvent = 20;
		inventoryBuilderSize = numBuilderSlots + numBuilderSlots2 + numBuilderSlots1 + numBuilderSlots3 + MoreBuilderInvent;
		BuilderInvent = new ItemStack[inventoryBuilderSize];
		trackfuel = 0;
		tunnelActive = false;
		followTracks = true;
		tracksStack = null;
		dataWatcher.addObject(24, Integer.valueOf((int) fuelTrain));
		dataWatcher.addObject(26, Integer.valueOf((int) plannedHeight));
		dataWatcher.addObject(27, Integer.valueOf((int) 1));
		dataWatcher.addObject(28, Integer.valueOf((int) 0));
	}

	public EntityTracksBuilder(World world, double d, double d1, double d2) {
		this(world);
		setPosition(d, d1 + (double) yOffset, d2);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		prevPosX = d;
		prevPosY = d1;
		prevPosZ = d2;
		currentHeight = posY;
		plannedHeight = (int) currentHeight;
		setPlannedHeight((int) plannedHeight);
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void setDead() {
		super.setDead();
		isDead = true;
		if (worldObj.isRemote) {
			return;
		}
		label1: for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack itemstack = getStackInSlot(i);
			if (itemstack == null) {
				continue;
			}
			float f = rand.nextFloat() * 0.8F + 0.1F;
			float f1 = rand.nextFloat() * 0.8F + 0.1F;
			float f2 = rand.nextFloat() * 0.8F + 0.1F;
			do {
				if (itemstack.stackSize <= 0) {
					continue label1;
				}
				int j = rand.nextInt(21) + 10;
				if (j > itemstack.stackSize) {
					j = itemstack.stackSize;
				}
				itemstack.stackSize -= j;
				EntityItem entityitem = new EntityItem(worldObj, posX + (double) f, posY + (double) f1, posZ + (double) f2, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));
				float f3 = 0.05F;
				entityitem.motionX = (float) rand.nextGaussian() * f3;
				entityitem.motionY = (float) rand.nextGaussian() * f3 + 0.2F;
				entityitem.motionZ = (float) rand.nextGaussian() * f3;
				worldObj.spawnEntityInWorld(entityitem);
			} while (true);
		}
		isDead = true;
	}
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		//System.out.println(this.pushX + ", " + this.pushZ);

		/* smoke+fuel */
		if (rand.nextInt(4) == 0) {
			Smoke();// creates smoke
		}
		//register current elevation = poY
		currentHeight = posY;
		if (worldObj.isRemote)
			return;

		tunnelBlockStack = new ItemStack(Blocks.glass, 1);
		if (getFuel() < maxFuel) {
			if (BuilderInvent[0] != null) {
				if ((FuelHandler.steamFuelLast(BuilderInvent[0])>0) && getFuel() + 300 < maxFuel) {
					fuelTrain += 300;
					decrStackInInvent(0, 1, 1);
				}
				if (BuilderInvent[0] != null && getFuel() + 600 < maxFuel) {
					fuelTrain += 600;
					decrStackInInvent(0, 1, 1);
				}
				if (BuilderInvent[0] != null && (PluginRailcraft.getItems().containsKey(PluginRailcraft.getNames()[1]) && (BuilderInvent[0].getItem() == PluginRailcraft.getItems().get(PluginRailcraft.getNames()[1]).getItem())) && getFuel() + 800 < maxFuel) {
					fuelTrain += 800;
					decrStackInInvent(0, 1, 1);
				}
				dataWatcher.updateObject(24, fuelTrain);
			}
		}
		moveStacks();
		updatePushForces();
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(posY);
		int k = MathHelper.floor_double(posZ);

		if (canDigg()) {
			updateState(true);
			this.digBuilder(i, j, k);
		}
		else {
			updateState(false);
			this.motionX = 0;
			this.motionZ = 0;
		}
	}

	private boolean canDigg() {
		return (checkForBallast() && checkForTracks() && getFuel() > 0);
	}
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("plannedHeight", plannedHeight);
		nbttagcompound.setBoolean("followTracks", followTracks);

		nbttagcompound.setDouble("PushX", pushX);
		nbttagcompound.setDouble("PushZ", pushZ);
		nbttagcompound.setShort("Trackfuel", (short) trackfuel);
		nbttagcompound.setShort("fuel", (short) getFuel());
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < BuilderInvent.length; i++) {
			if (BuilderInvent[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				BuilderInvent[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("Items", nbttaglist);
	}
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);

		pushX = nbttagcompound.getDouble("PushX");
		pushZ = nbttagcompound.getDouble("PushZ");
		if (nbttagcompound.getInteger("plannedHeight") < 4) {
			plannedHeight = (int) currentHeight;
			setPlannedHeight((int) plannedHeight);
		}
		else {
			setPlannedHeight(nbttagcompound.getInteger("plannedHeight"));
		}
		followTracks = nbttagcompound.getBoolean("followTracks");
		trackfuel = nbttagcompound.getShort("Trackfuel");
		fuelTrain = nbttagcompound.getShort("fuel");
		NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 10);
		BuilderInvent = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 0xff;
			if (j >= 0 && j < BuilderInvent.length) {
				BuilderInvent[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return inventoryBuilderSize;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return BuilderInvent[i];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (this.BuilderInvent[par1] != null) {
			ItemStack var2 = this.BuilderInvent[par1];
			this.BuilderInvent[par1] = null;
			return var2;
		}
		else {
			return null;
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (BuilderInvent[i] != null) {
			if (BuilderInvent[i].stackSize <= j) {
				ItemStack itemstack = BuilderInvent[i];
				BuilderInvent[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = BuilderInvent[i].splitStack(j);
			if (BuilderInvent[i].stackSize == 0) {
				BuilderInvent[i] = null;
			}
			return itemstack1;
		}
		else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		BuilderInvent[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return "Tracks Builder";
	}

	@Override
	public boolean interactFirst(EntityPlayer entityplayer) {
		playerEntity = entityplayer;
		if ((super.interactFirst(entityplayer))) {
			return false;
		}
		if (worldObj.isRemote) {
			return true;
		}

		entityplayer.openGui(Traincraft.instance, GuiIDs.BUILDER, worldObj, this.getEntityId(), -1, (int) this.posZ);
		pushZ = (posZ - entityplayer.posZ);
		pushX = (posX - entityplayer.posX);
		applyDragAndPushForces();
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	public int scaleMaxFuel(int i) {
		return (this.getFuel() * i) / maxFuel;
	}
	@Override
	protected void applyDragAndPushForces() {
		double d26 = MathHelper.sqrt_double(pushX * pushX + pushZ * pushZ);
		//System.out.println(pushX + ", " + pushZ + ": " + d26 + ": " + motionX + ", " + motionZ);
		if (d26 > 0.01D) {
			pushX /= d26;
			pushZ /= d26;
			double d28 = 0.029D;
			motionX *= 0.2D;
			motionY *= 0.0D;
			motionZ *= 0.2D;
			if (getFuel() > 0) {
				motionX += pushX * d28;
				motionZ += pushZ * d28;
			}
		}
		else {
			motionX *= 0.49999997615814209D;
			motionY *= 0.0D;
			motionZ *= 0.49999997615814209D;
		}
		motionX *= getDragAir();
		motionY *= 0.0D;
		motionZ *= getDragAir();
	}

	@Override
	protected void adjustSlopeVelocities(int metadata) {
		if (metadata == 2 || metadata == 3 || metadata == 4 || metadata == 5) {
			motionX *= 0.5;
			motionZ *= 0.5;
		}
	}

	protected void updatePushForces()
	{
		double push = MathHelper.sqrt_double(pushX * pushX + pushZ * pushZ);
		//if (push > 0)  System.out.println(pushX + ", " + pushZ + ": " + push + ": " + motionX + ", " + motionZ);
		if (push > 0 && motionX * motionX + motionZ * motionZ > 0)
		{
			pushX /= push;
			pushZ /= push;
			if (pushX * motionX + pushZ * motionZ < 0.0D)
			{
				pushX = 0.0D;
				pushZ = 0.0D;
			}
			else
			{
				pushX = motionX;
				pushZ = motionZ;
			}
		}
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		if (isDead) {
			return false;
		}
		return entityplayer.getDistanceSqToEntity(this) <= 64D;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (isDead) {
			return false;
		}
		return entityplayer.getDistanceSqToEntity(this) <= 64D;
	}

	@Override
	public float getOptimalDistance(EntityMinecart cart) {
		float dist = 1.6F;
		return (dist + 0.5F);
	}

	@Override
	public boolean canBeAdjusted(EntityMinecart cart) {
		return false;
	}

	public void Smoke() {
		if (getFuel() > 0) {
			fuelTrain--;
			if (!worldObj.isRemote) {
				dataWatcher.updateObject(24, fuelTrain);
			}
		}
	}

	public int getFuel() {
		if (worldObj.isRemote) {
			return (this.dataWatcher.getWatchableObjectInt(24));
		}
		return fuelTrain;
	}

	private void updateState(boolean state) {
		if (state)
			dataWatcher.updateObject(28, 1);
		if (!state)
			dataWatcher.updateObject(28, 0);
	}

	//render-state
	public boolean getState() {
		return this.dataWatcher.getWatchableObjectInt(28) == 1;
	}

	public void setPlannedHeightFromPacket(int packet) {
		int currentPlanned = getPlannedHeight();
		int request = packet + currentPlanned;
		if (request > 256) {
			request = 256;
		}
		if (request < 4) {
			request = 4;
		}
		setPlannedHeight(request);

	}

	public void setPlannedHeight(int plannedHeight) {
		//System.out.println("set watcher: "+ plannedHeight);
		dataWatcher.updateObject(26, plannedHeight);
		//System.out.println("watcher: "+this.dataWatcher.getWatchableObjectInt(26));
		this.plannedHeight = plannedHeight;
	}

	public int getPlannedHeight() {
		//System.out.println("get watcher: "+this.dataWatcher.getWatchableObjectInt(26) + " "+ worldObj.isRemote + " plannedHeight "+plannedHeight);
		return (this.dataWatcher.getWatchableObjectInt(26));
	}

	public void setFollowTracksFromPacket(int packet) {
		setFollowTracks(packet);
	}

	public void setFollowTracks(int follow) {
		dataWatcher.updateObject(27, follow);
		if (follow == 1)
			this.followTracks = true;
		if (follow == 0)
			this.followTracks = false;
	}

	public int getFollowTracks() {
		return (this.dataWatcher.getWatchableObjectInt(27));
	}

	public boolean isBlockInteresting(ItemStack i) {
		Item blockNow = i.getItem();
		boolean is = false;
		if (blockNow == Items.diamond) {
			is = true;
		}
		else if (blockNow == Items.gold_ingot) {
			is = true;
		}
		else if (blockNow == Items.iron_ingot) {
			is = true;
		}
		else if (blockNow == Items.dye) {
			is = true;
		}
		else if (blockNow == Items.coal) {
			is = true;
		}
		else if (blockNow == Items.redstone) {
			is = true;
		}
		else if (BuilderOreHandler.isOre(new ItemStack(blockNow))) {
			is = true;
		}
		else {
			is = false;
		}
		return is;
	}

	public void putInInvent(ItemStack itemdug) {
		boolean hasBeenPlaced = false;
		boolean noFreight = true;
		// ItemStack itemDug = (new ItemStack(blockNow, 1, 0));
		AxisAlignedBB box2 = null;
		box2 = boundingBox.expand(4.60000000298023224D, 4.60000000298023224D, 4.60000000298023224D);
		List lis = worldObj.getEntitiesWithinAABBExcludingEntity(this, box2);

		if (lis != null && lis.size() > 0) {
			for (int j1 = 0; j1 < lis.size(); j1++) {
				Entity entity = (Entity) lis.get(j1);
				if ((entity instanceof Freight) && (isAttached || isLinked()) && ((Freight) entity).cargoItems!=null && ((Freight) entity).cargoItems.length>0) {
					noFreight = false;
					for (int u = 0; u < ((Freight) entity).cargoItems.length; u++) {// checks the inventory
						ItemStack itemInside = ((IInventory) entity).getStackInSlot(u);
						if ((itemInside != null) && (((Freight) entity).cargoItems[u].stackSize != 0) && (((Freight) entity).cargoItems[u].getItem() == itemdug.getItem()) && (((Freight) entity).cargoItems[u].getItemDamage() == itemdug.getItemDamage()) && ((Freight) entity).cargoItems[u].stackSize != getInventoryStackLimit()) {// a stack is already in there and there is room
							((Freight) entity).cargoItems[u].stackSize += 1;
							hasBeenPlaced = true;
							u = ((Freight) entity).cargoItems.length;
							j1 = lis.size();
						}
						else if (itemInside == null) {// slot is empty
							if (((Freight) entity).cargoItems[u] == null) {
								((Freight) entity).cargoItems[u] = itemdug;
								hasBeenPlaced = true;
								u = ((Freight) entity).cargoItems.length;
								j1 = lis.size();
							}
						}
						else if ((!hasBeenPlaced) && (u == ((Freight) entity).cargoItems.length)) {// inventory is full, drop item
							noFreight = true;
						}
					}
				}
				else {
					noFreight = true;
				}
			}
		}
		else {
			noFreight = true;
		}
		if (noFreight) {
			for (int u = MoreBuilderInvent; u < BuilderInvent.length; u++) {// checks the inventory
				ItemStack itemInside = getStackInSlot(u);
				if ((itemInside != null) && (BuilderInvent[u].stackSize != 0) && (BuilderInvent[u].getItem() == itemdug.getItem()) && (BuilderInvent[u].getItemDamage() == itemdug.getItemDamage()) && BuilderInvent[u].stackSize != getInventoryStackLimit()) {// a stack is already in there and there is room
					BuilderInvent[u].stackSize += 1;
					hasBeenPlaced = true;
					u = BuilderInvent.length;
				}
				else if (itemInside == null) {// slot is empty
					if (BuilderInvent[u] == null) {
						BuilderInvent[u] = itemdug;
						hasBeenPlaced = true;
						u = BuilderInvent.length;
					}
				}
				else if ((!hasBeenPlaced) && (u == BuilderInvent.length - 1)) {// inventory is full, drop item
					// dropItemWithOffset(itemdug.itemID, 1, 1.0F);//has to be fixed
					entityDropItem(itemdug, 1.0F);
					u = BuilderInvent.length;
				}
			}
		}
	}

	/** can this block be used as ballast */
	private boolean canBeBallast(ItemStack stack) {
		/*
		 * if (stack != null && (stack.itemID == Block.planks.blockID || stack.itemID == Block.gravel.blockID || stack.itemID == Block.stone.blockID || stack.itemID == Block.brick.blockID || stack.itemID == Block.cobblestone.blockID || stack.itemID == Block.sandStone.blockID)) { return true; } */
		//return false;
		return canBeTunnel(stack);
	}

	/** can this block be used for the tunnel */
	private boolean canBeTunnel(ItemStack stack) {
		if (stack == null || stack.getItem() == null)
			return false;
		if (!(stack.getItem() instanceof ItemBlock))
			return false;
		if (stack.getItem() instanceof ItemBlock) {
			Block block = Block.getBlockFromItem(stack.getItem());
			if (block.hasTileEntity(stack.getItem().getMetadata(stack.getItemDamage())))
				return false;
			if(block.getRenderType()!=0)
				return false;
			/*if (block.isOpaqueCube())
				return true;*/
			return true;
		}
		return false;
	}

	/**
	 * Is the block in the inventory autorized to be put down? This doesn't seem to work properly, So I had another system going
	 */
	private boolean isAutorizedBlock(int i) {
		boolean is = false;
		if ((!UnAuthorizedBlock(i))) {
			is = true;
		}
		return is;
	}

	/* Is it a cable from industrial craft 2? */
	private boolean isCableOrPipe(ItemStack i)
	{
		return i.getItem().getClass().getSimpleName().equals("ItemPipe");
	}

	private boolean isOverheadLine(int i) {
		/* if(InitOverheadLine.lineItem==null){ return false; } return i==InitOverheadLine.lineItem.itemID; */
		return false;
	}

	/* Is it a forbidden block? */
	private boolean UnAuthorizedBlock(int i) 
	{
		return i == 57 || i == 42 || i == 41 || i == 81 || i == 56 || i == 21 || i == 16 || i == 15 || i == 14 || i == 116 || i == 114 || i == 113 || i == 111 || i == 109 || i == 108 || i == 107 || i == 106 || i == 31 || i == 30 || i == 26 || i == 18 || i == 7 || i == 6 || i == 37 || i == 38 || i == 39 || i == 40 || i == 43 || i == 51 || i == 52 || i == 53 || i == 55 || i == 57 || i == 63 || i == 64 || i == 67 || i == 68 || i == 70 || i == 71 || i == 72 || i == 77 || i == 78 || i == 81 || i == 83 || i == 92 || i == 95 || BuilderOreHandler.isOre(new ItemStack(Item.getItemById(i)));
	}

	/* Is it a forbidden block? */
	private boolean UnAuthorizedBlock(Block i) 
	{
		return UnAuthorizedBlock(Block.getIdFromBlock(i));
	}

	private void decrStackInInvent(int slot, int num, int random)
	{
		decrStackSize(slot, num);
	}

	/**
	 * checks the slots around the Track Builder drawing (in builder'gui)
	 * 
	 */
	private void checkBlock() {
		underBlock2Stack = null;
		underBlockStack = null;
		upperBlockStack = null;
		upperCenterBlockStack = null;
		upperBlock1Stack = null;
		for (int i = 1; i < 7; i++) {
			if (BuilderInvent[i] != null) {// && isAutorizedBlock(BuilderInvent[i].itemID)) {
				if (this.canBeBallast(BuilderInvent[i])) {
					if (i == 2) {
						//underBlock2 = BuilderInvent[2].itemID;
						underBlock2Stack = BuilderInvent[2].copy();
					}
				}
				else if (!this.canBeBallast(BuilderInvent[2])) {
					//underBlock2 = 0;
					underBlock2Stack = null;
				}
				if (this.canBeTunnel(BuilderInvent[i])) {
					if (i == 4) {
						//upperBlock = BuilderInvent[4].itemID;
						upperBlockStack = BuilderInvent[4].copy();
					}
					if (i == 5) {
						//upperCenterBlock = BuilderInvent[5].itemID;
						upperCenterBlockStack = BuilderInvent[5].copy();
					}
					if (i == 6) {
						//upperBlock1 = BuilderInvent[6].itemID;
						upperBlock1Stack = BuilderInvent[6].copy();
					}
				}
				else if (!this.canBeTunnel(BuilderInvent[i])) {

					if (i == 4) {
						//upperBlock = 0;
						upperBlockStack = new ItemStack(Blocks.stone);

					}
					if (i == 5) {
						//upperCenterBlock = 0;
						upperCenterBlockStack = new ItemStack(Blocks.stone);
					}
					if (i == 6) {
						//upperBlock1 = 0;
						upperBlock1Stack = new ItemStack(Blocks.stone);
					}
				}
			}
		}
		if (BuilderInvent[7] != null && canBeTunnel(BuilderInvent[7]) && BuilderInvent[7].stackSize>=12) {//&& isAutorizedBlock(BuilderInvent[7].itemID) && (BuilderInvent[7].itemID < 500)) {
			//tunnelBlock = BuilderInvent[7].itemID;
			tunnelBlockStack = BuilderInvent[7].copy();
			tunnelActive = true;
		}
		else {
			tunnelActive = false;
		}
	}

	private void moveStacks() {
		if (BuilderInvent[1] == null) {
			for (int i = 8; i <= 10; i++) {
				if (BuilderInvent[i] != null && (RailTools.isTrackItem(BuilderInvent[i]) || BuilderInvent[i].getItem() == Item.getItemFromBlock(Blocks.rail) || (BuilderInvent[i].getItem() == Item.getItemFromBlock(Blocks.golden_rail)) || (BuilderInvent[i].getItem() == Item.getItemFromBlock(Blocks.detector_rail)))) {
					BuilderInvent[1] = BuilderInvent[i].copy();
					decrStackSize(i, 64);
					break;
				}
			}
		}
		if (BuilderInvent[3] == null) {
			for (int i = 11; i <= 13; i++) {
				if (this.canBeBallast(BuilderInvent[i])) {
					BuilderInvent[3] = BuilderInvent[i].copy();
					decrStackSize(i, 64);
					break;
				}
			}
		}
		if (BuilderInvent[2] == null) {
			for (int i = 14; i <= 16; i++) {
				if (this.canBeBallast(BuilderInvent[i])) {
					BuilderInvent[2] = BuilderInvent[i].copy();
					decrStackSize(i, 64);
					break;
				}
			}
		}
		if (BuilderInvent[7] == null) {
			for (int i = 17; i <= 19; i++) {
				if (this.canBeTunnel(BuilderInvent[i])) {
					BuilderInvent[7] = BuilderInvent[i].copy();
					decrStackSize(i, 64);
					break;
				}
			}
		}

	}

	/* Gets the drops of the block then calls blockSpawner */
	public void getBlockList(World worldObj, int i, int j, int k) {
		if (!(worldObj.isAirBlock(i, j, k))) {
			ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(TrainModBlockUtil.getItemStackFromBlock(worldObj, (int) i, (int) j, (int) k));//underBlockStack.getItem().getMetadata(underBlockStack.getItemDamage())
			for (ItemStack s : stacks) {
				if((s.getItem() instanceof ItemBlock && (BlockRailBase.func_150051_a(Block.getBlockFromItem(s.getItem())))))return;
				if ((s.getItem() != Item.getItemFromBlock(Blocks.glass)) && (s.getItem() != tunnelBlockStack.getItem())) {// && (isBlockInteresting(s))) {// can't spawn rails or air blocks or glass blocks
					if ((worldObj.getBlock(i, j, k) != Block.getBlockFromItem(tunnelBlockStack.getItem()))) {
						putInInvent(s);
					}
				}
			}
		}
	}

	/**
	 * Perform block harvesting, drop the item, remove block and play sound.
	 * 
	 * @param pos
	 */
	private void harvestBlock_do(Vec3 pos) {
		if (pos == null) {
			return;
		}
		Block id = worldObj.getBlock((int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord);
		int meta = worldObj.getBlockMetadata((int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord);
		if (id != Blocks.air && id != null && !worldObj.isRemote) {
			this.playMiningEffect(pos, id);
		}

		if (!shouldIgnoreBlockForHarvesting(pos, id)) {
			if (id != Blocks.air && id != null) {
				//System.out.println("Removed block at:"+ (int) pos.xCoord +":"+ (int)pos.yCoord +":"+ (int)pos.zCoord);
				//worldObj.setBlockMetadataWithNotify((int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord, 0, 0);
				worldObj.setBlockToAir((int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord);
				worldObj.playAuxSFX(2001, (int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord, Block.getIdFromBlock(id) + (meta << 12));
				if (!worldObj.isRemote) {
					this.playMiningEffect(pos, id);
				}
			}
		}
	}

	/**
	 * Check if block is unharvestable
	 * 
	 * @param pos
	 * @param id block id
	 * @return is not harvested
	 */
	private boolean shouldIgnoreBlockForHarvesting(Vec3 pos, Block id) {
		if (id == null || id == Blocks.air || id == Blocks.bedrock || id == Blocks.fire || id == Blocks.portal || id == Blocks.end_portal || id instanceof BlockFluidClassic || id instanceof BlockFluidFinite || id == Block.getBlockById(55) || id == Block.getBlockById(70) || id == Block.getBlockById(72)) {
			return true;
		}
		return false;
	}

	/**
	 * Spawn breaking particles for blockparticles
	 * 
	 * @param pos position
	 * @param id2 index of the block in mining list
	 */
	private void playMiningEffect(Vec3 pos, Block id2) {
		miningTickCounter++;
		Block block = worldObj.getBlock((int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord);

		if (!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer()) {
			if (miningTickCounter % 8 == 0 && block != null && !worldObj.isRemote && Minecraft.getMinecraft() != null)
			{
				worldObj.playSound((int) pos.xCoord + 0.5F, (int) pos.yCoord + 0.5F, (int) pos.zCoord + 0.5F, block.stepSound.getBreakSound(), (block.stepSound.getVolume() + 1.0F) / 8F, block.stepSound.getPitch() * 0.5F, true);
			}
			if (miningTickCounter % 8 == 0 && block != Blocks.air && block != null && pos != null) {
				if (FMLClientHandler.instance().getClient() != null) {
					FMLClientHandler.instance().getClient().effectRenderer.addBlockHitEffects((int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord, getSideFromYaw());
				}
			}
		}
	}

	private int getSideFromYaw() {
		if (rotationYaw == 0) {
			return 5;
		}
		if (rotationYaw == 90) {
			return 3;
		}
		if (rotationYaw == 180) {
			return 4;
		}
		if (rotationYaw == 270) {
			return 2;
		}
		return 1;
	}

	public boolean noRoof(int i)
	{
		return i == 0 || i == 6 || i == 18 || i == 23 || i == 25 || i == 26 || i == 27 || i == 28 || i == 29 || i == 35 || i == 41 || i == 46 || i == 52 || i == 54 || i == 55 || i == 53 || i == 61 || i == 62 || i == 58 || i == 64 || i == 65 || i == 66 || i == 75 || i == 76 || i == 78 || i == 81 || i == 83 || i == 84 || i == 85 || i == 93 || i == 94 || i == 95 || i == 96 || i == 101 || i == 106 || i == 107 || i == 111 || i == 115 || i == 116 || i == 117 || i == 118 || i == 122 || i == Block.getIdFromBlock(Blocks.planks) || BuilderOreHandler.isOre(new ItemStack(Item.getItemById(i)));
	}

	public void putRoof(int i, int j, int k, int inv, World worldObj, ItemStack block) {
		if (!tunnelActive && block != null && (worldObj.getBlock(i, j, k) != Block.getBlockFromItem(block.getItem()))) {
			// worldObj.getBlockId(i-1,j+3,k) == Block.dirt.blockID || worldObj.getBlockId(i-1,j+3,k) == 8 || worldObj.getBlockId(i-1,j+3,k) == 9 || worldObj.getBlockId(i-1,j+3,k) == 10 || worldObj.getBlockId(i-1,j+3,k) == 11 || worldObj.getBlockId(i-1,j+3,k) == 12 || worldObj.getBlockId(i-1,j+3,k) == 13 || worldObj.getBlockId(i-1,j+3,k) == 1){
			getBlockList(worldObj, i, j, k);
			worldObj.setBlock(i, j, k, Block.getBlockFromItem(block.getItem()), block.getItem().getMetadata(block.getItemDamage()), 3);
			decrStackInInvent(inv, 1, 1);
		}
	}

	private boolean checkForBallast() {

		if (BuilderInvent[3] != null && canBeBallast(BuilderInvent[3])) {
			//underBlock = BuilderInvent[3].itemID;
			underBlockStack = BuilderInvent[3].copy();
			return true;
		}
		return false;
	}

	private boolean checkForTracks() {

		if (BuilderInvent[1] != null) {
			if (RailTools.isTrackItem(BuilderInvent[1]) && getFuel() > 0) {
				trackfuel = 1;
				tracksStack = BuilderInvent[1].copy();
				return true;

			}
			if ((BuilderInvent[1].getItem() == Items.iron_ingot && getFuel() > 0)) {
				trackfuel = 1;
				tracksStack = new ItemStack(Blocks.rail);
				return true;
			}
			if ((BuilderInvent[1].getItem() == ItemData.steel.item) && getFuel() > 0) {
				trackfuel = 1;
				tracksStack = new ItemStack(Blocks.rail);
				return true;
			}
			if ((BuilderInvent[1].getItem() == Item.getItemFromBlock(Blocks.rail) || (BuilderInvent[1].getItem() == Item.getItemFromBlock(Blocks.golden_rail)) || (BuilderInvent[1].getItem() == Item.getItemFromBlock(Blocks.detector_rail)))) {
				if (getFuel() > 0) {
					tracksStack = BuilderInvent[1].copy();
					trackfuel = 1;
					return true;
				}
			}
		}
		else {
			trackfuel = 0;
			return false;
		}
		return false;
	}

	/**
	 * north = 0 west = 3 east = 1 south = 2
	 * 
	 * @return orientation
	 */
	private int getFacing() {
		if (!worldObj.isRemote) {
			rotation = (float) ((Math.atan2(d7, d6) * 180D) / Math.PI);
		}
		else {
			rotation = (float) ((Math.atan2(0 - motionX, 0 - motionZ) * 180D) / Math.PI);
		}
		int var6 = MathHelper.floor_double((double) (rotation * 4.0F / 360.0F) + 0.5D) & 3;
		return var6;
	}

	/** Compares the currentHeight with given height in GUI */
	private int checkForHeight() {
		if ((int) currentHeight < getPlannedHeight()) {
			return 1;
		}
		if ((int) currentHeight > getPlannedHeight()) {
			return -1;
		}
		return 0;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (worldObj.isRemote) {
			return true;
		}
		if(canBeDestroyedByPlayer(damagesource))return true;
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
			boolean flag = damagesource.getEntity() instanceof EntityPlayer && ((EntityPlayer)damagesource.getEntity()).capabilities.isCreativeMode;
			if(!flag) {
				dropCartAsItem();
			}
		}
		return true;
	}

	/**
	 * Gets if we can place a torch on a block.
	 */
	private boolean canPlaceTorchOn(World par1World, int par2, int par3, int par4) {
		if (par1World.doesBlockHaveSolidTopSurface(par1World, par2, par3, par4)) {
			return true;
		}
		else {
			Block var5 = par1World.getBlock(par2, par3, par4);
			return (var5 != Blocks.air && var5 != null && var5.canPlaceTorchOnTop(par1World, par2, par3, par4));
		}
	}

	private void digOnXAxis(int i, int j, int k, int d, int iX, int hY) {
		Vec3 vec = null;

		getBlockList(worldObj, i + d, j + hY, k);
		getBlockList(worldObj, i + d, j + hY + 1, k);
		getBlockList(worldObj, i + d, j + hY + 2, k);
		getBlockList(worldObj, i + d, j + hY, k - 1);
		getBlockList(worldObj, i + d, j + hY, k + 1);
		getBlockList(worldObj, i + d, j + hY + 1, k - 1);
		getBlockList(worldObj, i + d, j + hY + 1, k + 1);
		getBlockList(worldObj, i + d, j + hY + 2, k - 1);
		getBlockList(worldObj, i + d, j + hY + 2, k + 1);
		if ((BlockRailBase.func_150051_a(worldObj.getBlock(i + d, j + hY, k - 1)) || worldObj.getBlock(i + d, j + hY, k - 1)==BlockData.tcRail.block || worldObj.getBlock(i + d, j + hY, k - 1)==BlockData.tcRailGag.block)&& followTracks) {
		}
		else {
			//worldObj.setBlockMetadataWithNotify(i + d, j+ hY, k - 1, 0);
			vec = Vec3.createVectorHelper(i + d, j + hY, k - 1);
			this.harvestBlock_do(vec);
		}

		if ((BlockRailBase.func_150051_a(worldObj.getBlock(i + d, j + hY, k + 1)) || worldObj.getBlock(i + d, j + hY, k + 1)==BlockData.tcRail.block || worldObj.getBlock(i + d, j + hY, k + 1)==BlockData.tcRailGag.block )&& followTracks) {
		}
		else {
			//worldObj.setBlockMetadataWithNotify(i + d, j+ hY, k + 1, 0);
			vec = Vec3.createVectorHelper(i + d, j + hY, k + 1);
			this.harvestBlock_do(vec);
		}

		if ((BlockRailBase.func_150051_a((worldObj.getBlock(i + d, j + hY, k))) || worldObj.getBlock(i + d, j + hY, k)==BlockData.tcRail.block || worldObj.getBlock(i + d, j + hY, k)==BlockData.tcRailGag.block)&& followTracks) {
		}
		else {
			//worldObj.setBlockMetadataWithNotify(i + d, j+ hY, k, 0);
			vec = Vec3.createVectorHelper(i + d, j + hY, k);
			this.harvestBlock_do(vec);
		}

		vec = Vec3.createVectorHelper(i + d, j + hY + 1, k);
		this.harvestBlock_do(vec);
		vec = Vec3.createVectorHelper(i + d, j + hY + 2, k);
		this.harvestBlock_do(vec);
		vec = Vec3.createVectorHelper(i + d, j + hY + 1, k - 1);
		this.harvestBlock_do(vec);
		vec = Vec3.createVectorHelper(i + d, j + hY + 2, k + 1);
		this.harvestBlock_do(vec);
		vec = Vec3.createVectorHelper(i + d, j + hY + 1, k + 1);
		this.harvestBlock_do(vec);
		vec = Vec3.createVectorHelper(i + d, j + hY + 2, k - 1);
		this.harvestBlock_do(vec);

		if (tunnelActive) {// puts dirt then glass (default) in a tunnel shape for underwater tunnel

			getBlockList(worldObj, i + (3 * iX), j + hY + 3, k);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 3, k);
			getBlockList(worldObj, i + (3 * iX), j + hY + 1, k);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 1, k);
			getBlockList(worldObj, i + (3 * iX), j + hY + 2, k);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 2, k);
			getBlockList(worldObj, i + (3 * iX), j + hY + 1, k - 1);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 1, k - 1);
			getBlockList(worldObj, i + (3 * iX), j + hY + 1, k + 1);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 1, k + 1);
			getBlockList(worldObj, i + (3 * iX), j + hY + 2, k + 1);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 2, k + 1);
			getBlockList(worldObj, i + (3 * iX), j + hY + 2, k - 1);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 2, k - 1);
			getBlockList(worldObj, i + (3 * iX), j + hY + 3, k + 1);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 3, k + 1);
			getBlockList(worldObj, i + (3 * iX), j + hY + 3, k - 1);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 3, k - 1);
			getBlockList(worldObj, i + (3 * iX), j + hY + 3, k + 2);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 3, k + 2);
			getBlockList(worldObj, i + (3 * iX), j + hY + 3, k - 2);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 3, k - 2);
			getBlockList(worldObj, i + (3 * iX), j + hY + 2, k + 2);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 2, k + 2);
			getBlockList(worldObj, i + (3 * iX), j + hY + 2, k - 2);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 2, k - 2);
			getBlockList(worldObj, i + (3 * iX), j + hY + 1, k + 2);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 1, k + 2);
			getBlockList(worldObj, i + (3 * iX), j + hY + 1, k - 2);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 1, k - 2);
			getBlockList(worldObj, i + (3 * iX), j + hY, k + 2);
			worldObj.setBlockToAir(i + (3 * iX), j + hY, k + 2);
			getBlockList(worldObj, i + (3 * iX), j + hY, k - 2);
			worldObj.setBlockToAir(i + (3 * iX), j + hY, k - 2);
			getBlockList(worldObj, i + (3 * iX), j + hY, k + 1);
			worldObj.setBlockToAir(i + (3 * iX), j + hY, k + 1);
			getBlockList(worldObj, i + (3 * iX), j + hY, k - 1);
			worldObj.setBlockToAir(i + (3 * iX), j + hY, k - 1);

			getBlockList(worldObj, i + (3 * iX), j + hY + 3, k + 2);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 3, k + 2);
			getBlockList(worldObj, i + (3 * iX), j + hY + 3, k - 2);
			worldObj.setBlockToAir(i + (3 * iX), j + hY + 3, k - 2);

			getBlockList(worldObj, i + 2 * iX, j + hY + 4, k + 1);
			worldObj.setBlockToAir(i + 2 * iX, j + hY + 4, k + 1);
			getBlockList(worldObj, i + 2 * iX, j + hY + 4, k - 1);
			worldObj.setBlockToAir(i + 2 * iX, j + hY + 4, k - 1);
			getBlockList(worldObj, i + 2 * iX, j + hY + 4, k);
			worldObj.setBlockToAir(i + 2 * iX, j + hY + 4, k);
			getBlockList(worldObj, i + 2 * iX, j + hY + 4, k + 2);
			worldObj.setBlockToAir(i + 2 * iX, j + hY + 4, k + 2);
			getBlockList(worldObj, i + 2 * iX, j + hY + 4, k - 2);
			worldObj.setBlockToAir(i + 2 * iX, j + hY + 4, k - 2);

			worldObj.setBlock(i + (2 * iX), j + hY + 3, k, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY + 1, k, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY + 2, k, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY + 1, k - 1, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY + 1, k + 1, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY + 2, k + 1, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY + 2, k - 1, Blocks.glass);

			worldObj.setBlock(i + (2 * iX), j + hY + 3, k + 1, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY + 3, k - 1, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY + 3, k + 2, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3);
			worldObj.setBlock(i + (2 * iX), j + hY + 3, k - 2, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3);
			worldObj.setBlock(i + (2 * iX), j + hY + 2, k + 2, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY + 2, k - 2, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY + 1, k + 2, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY + 1, k - 2, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY, k + 2, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY, k - 2, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY, k + 1, Blocks.glass);
			worldObj.setBlock(i + (2 * iX), j + hY, k - 1, Blocks.glass);
			// worldObj.setBlock(i,j,k,Blocks.glass);

			worldObj.setBlockToAir(i + iX, j + hY + 3, k + 1);//removes excess of glass
			worldObj.setBlockToAir(i + iX, j + hY + 3, k - 1);
			worldObj.setBlockToAir(i + iX, j + hY + 3, k);
			
			if(worldObj.setBlock(i + iX, j + hY + 4, k + 1, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + iX, j + hY + 4, k - 1, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + iX, j + hY + 4, k + 2, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + iX, j + hY + 4, k - 2, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + iX, j + hY + 2, k + 2, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + iX, j + hY + 2, k - 2, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + iX, j + hY + 1, k + 2, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + iX, j + hY + 1, k - 2, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + iX, j + hY, k + 2, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + iX, j + hY, k - 2, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + iX, j + hY - 1, k + 1, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + iX, j + hY - 1, k - 1, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			
			if (upperCenterBlockStack != null && Block.getBlockFromItem(upperCenterBlockStack.getItem()) != Blocks.air && worldObj.getBlock(i + iX, j + hY + 4, k) != Block.getBlockFromItem(upperCenterBlockStack.getItem())) {
				worldObj.setBlock(i + iX, j + hY + 4, k, Block.getBlockFromItem(upperCenterBlockStack.getItem()), upperCenterBlockStack.getItem().getMetadata(upperCenterBlockStack.getItemDamage()), 3);
				decrStackInInvent(5, 1, 1);
			}
			if (upperCenterBlockStack == null)
				worldObj.setBlock(i + iX, j + hY + 4, k, Block.getBlockById(1));
			worldObj.setBlockToAir(i + iX, j + hY, k - 1);
			worldObj.setBlockToAir(i + iX, j + hY, k + 1);
			worldObj.setBlockToAir(i + iX, j + hY + 1, k);
			worldObj.setBlockToAir(i + iX, j + hY + 2, k);

			if (hY < 0 && !BlockRailBase.func_150051_a(worldObj.getBlock(i + iX, j + hY, k))) {//code is different when going down
				worldObj.setBlockToAir(i + iX, j + hY, k);
			}
			else {
				worldObj.setBlockToAir(i + (2 * iX), j + hY, k);//-2 or it constantly removes tracks
			}
			worldObj.setBlockToAir(i + iX, j + hY + 1, k - 1);
			worldObj.setBlockToAir(i + iX, j + hY + 2, k + 1);
			worldObj.setBlockToAir(i + iX, j + hY + 1, k + 1);
			worldObj.setBlockToAir(i + iX, j + hY + 2, k - 1);
		}
	}

	private void digOnZAxis(int i, int j, int k, int d, int kZ, int hY) {
		Vec3 vec = null;
		getBlockList(worldObj, i, j + hY, k + d);
		getBlockList(worldObj, i, j + hY + 1, k + d);
		getBlockList(worldObj, i, j + hY + 2, k + d);
		getBlockList(worldObj, i - 1, j + hY, k + d);
		getBlockList(worldObj, i + 1, j + hY, k + d);
		getBlockList(worldObj, i - 1, j + hY + 1, k + d);
		getBlockList(worldObj, i + 1, j + hY + 2, k + d);
		getBlockList(worldObj, i + 1, j + hY + 1, k + d);
		getBlockList(worldObj, i - 1, j + hY + 2, k + d);

		if ((BlockRailBase.func_150051_a(worldObj.getBlock(i - 1, j + hY, k + d)) || worldObj.getBlock(i - 1, j + hY, k + d)==BlockData.tcRail.block || worldObj.getBlock(i - 1, j + hY, k + d)==BlockData.tcRailGag.block) && followTracks) {
		}
		else {
			//worldObj.setBlockMetadataWithNotify(i - 1, j+ hY, k + d, 0, -1);
			worldObj.setBlockToAir(i - 1, j + hY, k + d);
			vec = Vec3.createVectorHelper(i - 1, j + hY, k + d);
			this.harvestBlock_do(vec);
		}

		if ((BlockRailBase.func_150051_a(worldObj.getBlock(i + 1, j + hY, k + d)) || worldObj.getBlock(i + 1, j + hY, k + d)==BlockData.tcRail.block || worldObj.getBlock(i + 1, j + hY, k + d)==BlockData.tcRailGag.block) && followTracks) {
		}
		else {
			//worldObj.setBlockMetadataWithNotify(i + 1, j+ hY, k + d, 0, -1);
			worldObj.setBlockToAir(i + 1, j + hY, k + d);
			vec = Vec3.createVectorHelper(i + 1, j + hY, k + d);
			this.harvestBlock_do(vec);
		}

		if ((BlockRailBase.func_150051_a(worldObj.getBlock(i, j + hY, k + d)) || worldObj.getBlock(i, j + hY, k + d)==BlockData.tcRail.block || worldObj.getBlock(i, j + hY, k + d)==BlockData.tcRailGag.block) && followTracks) {
		}
		else {
			//worldObj.setBlockMetadataWithNotify(i, j+ hY, k + d, 0, -1);
			worldObj.setBlockToAir(i, j + hY, k + d);
			vec = Vec3.createVectorHelper(i, j + hY, k + d);
			this.harvestBlock_do(vec);
		}
		vec = Vec3.createVectorHelper(i, j + hY + 1, k + d);
		this.harvestBlock_do(vec);
		vec = Vec3.createVectorHelper(i, j + hY + 2, k + d);
		this.harvestBlock_do(vec);
		vec = Vec3.createVectorHelper(i - 1, j + hY + 1, k + d);
		this.harvestBlock_do(vec);
		vec = Vec3.createVectorHelper(i + 1, j + hY + 2, k + d);
		this.harvestBlock_do(vec);
		vec = Vec3.createVectorHelper(i + 1, j + hY + 1, k + d);
		this.harvestBlock_do(vec);
		vec = Vec3.createVectorHelper(i - 1, j + hY + 2, k + d);
		this.harvestBlock_do(vec);

		if (tunnelActive) {
			getBlockList(worldObj, i, j + hY + 3, k + (3 * kZ));
			worldObj.setBlockToAir(i, j + hY + 3, k + (3 * kZ));
			getBlockList(worldObj, i, j + hY + 1, k + (3 * kZ));
			worldObj.setBlockToAir(i, j + hY + 1, k + (3 * kZ));
			getBlockList(worldObj, i, j + hY + 2, k + (3 * kZ));
			worldObj.setBlockToAir(i, j + hY + 2, k + (3 * kZ));
			getBlockList(worldObj, i - 1, j + hY + 1, k + (3 * kZ));
			worldObj.setBlockToAir(i - 1, j + hY + 1, k + (3 * kZ));
			getBlockList(worldObj, i + 1, j + hY + 1, k + (3 * kZ));
			worldObj.setBlockToAir(i + 1, j + hY + 1, k + (3 * kZ));
			getBlockList(worldObj, i + 1, j + hY + 2, k + (3 * kZ));
			worldObj.setBlockToAir(i + 1, j + hY + 2, k + (3 * kZ));
			getBlockList(worldObj, i - 1, j + hY + 2, k + (3 * kZ));
			worldObj.setBlockToAir(i - 1, j + hY + 2, k + (3 * kZ));
			getBlockList(worldObj, i + 1, j + hY + 3, k + (3 * kZ));
			worldObj.setBlockToAir(i + 1, j + hY + 3, k + (3 * kZ));
			getBlockList(worldObj, i - 1, j + hY + 3, k + (3 * kZ));
			worldObj.setBlockToAir(i - 1, j + hY + 3, k + (3 * kZ));
			getBlockList(worldObj, i + 2, j + hY + 3, k + (3 * kZ));
			worldObj.setBlockToAir(i + 2, j + hY + 3, k + (3 * kZ));
			getBlockList(worldObj, i - 2, j + hY + 3, k + (3 * kZ));
			worldObj.setBlockToAir(i - 2, j + hY + 3, k + (3 * kZ));
			getBlockList(worldObj, i + 2, j + hY + 2, k + (3 * kZ));
			worldObj.setBlockToAir(i + 2, j + hY + 2, k + (3 * kZ));
			getBlockList(worldObj, i - 2, j + hY + 2, k + (3 * kZ));
			worldObj.setBlockToAir(i - 2, j + hY + 2, k + (3 * kZ));
			getBlockList(worldObj, i + 2, j + hY + 1, k + (3 * kZ));
			worldObj.setBlockToAir(i + 2, j + hY + 1, k + (3 * kZ));
			getBlockList(worldObj, i - 2, j + hY + 1, k + (3 * kZ));
			worldObj.setBlockToAir(i - 2, j + hY + 1, k + (3 * kZ));
			getBlockList(worldObj, i + 2, j + hY, k + (3 * kZ));
			worldObj.setBlockToAir(i + 2, j + hY, k + (3 * kZ));
			getBlockList(worldObj, i - 2, j + hY, k + (3 * kZ));
			worldObj.setBlockToAir(i - 2, j + hY, k + (3 * kZ));
			getBlockList(worldObj, i + 1, j + hY, k + (3 * kZ));
			worldObj.setBlockToAir(i + 1, j + hY, k + (3 * kZ));
			getBlockList(worldObj, i - 1, j + hY, k + (3 * kZ));
			worldObj.setBlockToAir(i - 1, j + hY, k + (3 * kZ));

			getBlockList(worldObj, i + 2, j + hY + 3, k + (3 * kZ));
			worldObj.setBlockToAir(i + 2, j + hY + 3, k + (3 * kZ));
			getBlockList(worldObj, i - 2, j + hY + 3, k + (3 * kZ));
			worldObj.setBlockToAir(i - 2, j + hY + 3, k + (3 * kZ));

			getBlockList(worldObj, i + 1, j + hY + 4, k + 2 * kZ);
			worldObj.setBlockToAir(i + 1, j + hY + 4, k + 2 * kZ);
			getBlockList(worldObj, i - 1, j + hY + 4, k + 2 * kZ);
			worldObj.setBlockToAir(i - 1, j + hY + 4, k + 2 * kZ);
			getBlockList(worldObj, i, j + hY + 4, k + 2 * kZ);
			worldObj.setBlockToAir(i, j + hY + 4, k + 2 * kZ);
			getBlockList(worldObj, i + 2, j + hY + 4, k + 2 * kZ);
			worldObj.setBlockToAir(i + 2, j + hY + 4, k + 2 * kZ);
			getBlockList(worldObj, i - 2, j + hY + 4, k + 2 * kZ);
			worldObj.setBlockToAir(i - 2, j + hY + 4, k + 2 * kZ);

			worldObj.setBlock(i, j + hY + 3, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i, j + hY + 1, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i, j + hY + 2, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i - 1, j + hY + 1, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i + 1, j + hY + 1, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i + 1, j + hY + 2, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i - 1, j + hY + 2, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i + 1, j + hY + 3, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i - 1, j + hY + 3, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i + 2, j + hY + 3, k + (2 * kZ), Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3);
			worldObj.setBlock(i - 2, j + hY + 3, k + (2 * kZ), Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3);
			worldObj.setBlock(i + 2, j + hY + 2, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i - 2, j + hY + 2, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i + 2, j + hY + 1, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i - 2, j + hY + 1, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i + 2, j + hY, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i - 2, j + hY, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i + 1, j + hY, k + (2 * kZ), Blocks.glass);
			worldObj.setBlock(i - 1, j + hY, k + (2 * kZ), Blocks.glass);

			// tunnel
			worldObj.setBlockToAir(i + 1, j + hY + 3, k + kZ);
			worldObj.setBlockToAir(i - 1, j + hY + 3, k + kZ);
			worldObj.setBlockToAir(i, j + hY + 3, k + kZ);

			if(worldObj.setBlock(i + 1, j + hY + 4, k + kZ, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i - 1, j + hY + 4, k + kZ, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + 2, j + hY + 4, k + kZ, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i - 2, j + hY + 4, k + kZ, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + 2, j + hY + 2, k + kZ, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i - 2, j + hY + 2, k + kZ, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + 2, j + hY + 1, k + kZ, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i - 2, j + hY + 1, k + kZ, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + 2, j + hY, k + kZ, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i - 2, j + hY, k + kZ, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i + 1, j + hY - 1, k + kZ, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if(worldObj.setBlock(i - 1, j + hY - 1, k + kZ, Block.getBlockFromItem(tunnelBlockStack.getItem()), tunnelBlockStack.getItem().getMetadata(tunnelBlockStack.getItemDamage()), 3))decrStackSize(7,1);
			if (upperCenterBlockStack != null && Block.getBlockFromItem(upperCenterBlockStack.getItem()) != Blocks.air && worldObj.getBlock(i, j + hY + 4, k + kZ) != Block.getBlockFromItem(upperCenterBlockStack.getItem())) {
				worldObj.setBlock(i, j + hY + 4, k + kZ, Block.getBlockFromItem(upperCenterBlockStack.getItem()), upperCenterBlockStack.getItem().getMetadata(upperCenterBlockStack.getItemDamage()), 3);
				decrStackInInvent(5, 1, 1);
			}
			if (upperCenterBlockStack == null)
				worldObj.setBlock(i, j + hY + 4, k + kZ, Block.getBlockById(1));
			worldObj.setBlockToAir(i - 1, j + hY, k + kZ);
			worldObj.setBlockToAir(i + 1, j + hY, k + kZ);
			if (hY < 0 && !BlockRailBase.func_150051_a(worldObj.getBlock(i, j + hY, k + kZ))) {
				worldObj.setBlockToAir(i, j + hY, k + kZ);
			}
			else {
				worldObj.setBlockToAir(i, j + hY, k + (2 * kZ));
			}
			worldObj.setBlockToAir(i, j + hY + 1, k + kZ);
			worldObj.setBlockToAir(i, j + hY + 2, k + kZ);
			worldObj.setBlockToAir(i - 1, j + hY + 1, k + kZ);
			worldObj.setBlockToAir(i + 1, j + hY + 2, k + kZ);
			worldObj.setBlockToAir(i + 1, j + hY + 1, k + kZ);
			worldObj.setBlockToAir(i - 1, j + hY + 2, k + kZ);
		}
	}

	/**
	 * This handles the Builder's digging process
	 */
	private void digBuilder(int i, int j, int k) {
		if (worldObj.isRemote) {
			return;
		}
		/** +1/-1 on X axis, used to know where to dig */
		int iX = 0;
		/** +1/-1 on Z axis, used to know where to dig */
		int kZ = 0;
		/** +1/-1 on Y axis (up/down) */
		int hY = 0;

		Block north = worldObj.getBlock(i + 1, j + hY, k);
		Block south = worldObj.getBlock(i - 1, j + hY, k);
		Block east = worldObj.getBlock(i, j + hY, k + 1);
		Block west = worldObj.getBlock(i, j + hY, k - 1);

		//directions to compare with getFacing()
		int northDir = 0;
		int southDir = 2;
		int eastDir = 1;
		int westDir = 3;

		int block1 = 0;
		int block2 = 0;
		int block3 = 0;
		int block4 = 0;
		int block5 = 0;
		int block6 = 0;
		int block7 = 0;
		int block8 = 0;
		int block9 = 0;

		//checks the blocks to lay down
		checkBlock();
		//checks the tracks in slot 1
		checkForTracks();
		//checks for ballast in slot 3
		checkForBallast();
		//checks the height, if it is supposed to continue up/down/flat
		hY = checkForHeight();

		if (trackfuel >= 1) {// is fueled
			if (north != Blocks.bedrock && motionX > 0 && getFacing() == southDir) {
				iX = 1;//dug blocks are (i+iX,j,k)
				kZ = 0;
			}
			else if (south != Blocks.bedrock && motionX < 0 && getFacing() == northDir) {
				iX = -1;//dug blocks are (i-iX,j,k)
				kZ = 0;
			}
			else if (west != Blocks.bedrock && motionZ < 0 && getFacing() == eastDir) {
				kZ = -1;//dug blocks are (i,j,k-1)
				iX = 0;
			}
			else if (east != Blocks.bedrock && motionZ > 0 && getFacing() == westDir) {
				kZ = 1;//dug blocks are (i,j,k+1)
				iX = 0;
			}
			else {
				return;
			}

			//builder is going flat or up
			if (hY > -1 && underBlockStack != null && worldObj.getBlock(i + iX, j - 1 + hY, k + kZ) != Block.getBlockFromItem(underBlockStack.getItem()) && worldObj.getBlock(i + iX, j - 1 + hY, k + kZ) != Block.getBlockFromItem(tracksStack.getItem())) {
				getBlockList(worldObj, i + iX, j - 1 + hY, k + kZ);
				worldObj.setBlock(i + iX, j - 1 + hY, k + kZ, Block.getBlockFromItem(underBlockStack.getItem()), underBlockStack.getItem().getMetadata(underBlockStack.getItemDamage()), 3);
				decrStackInInvent(3, 1, 1);// decr underblock
			}

			//builder is going down, different code is required
			if (hY < 0 && underBlockStack != null && worldObj.getBlock(i, j - 1 + hY, k) != Block.getBlockFromItem(underBlockStack.getItem()) && worldObj.getBlock(i, j - 1 + hY, k) != Block.getBlockFromItem(tracksStack.getItem())) {
				getBlockList(worldObj, i, j - 1 + hY, k);
				worldObj.setBlock(i, j - 1 + hY, k, Block.getBlockFromItem(underBlockStack.getItem()), underBlockStack.getItem().getMetadata(underBlockStack.getItemDamage()), 3);// changes the block under the builder
				decrStackInInvent(3, 1, 1);// decr underblock
			}

			//placing the block (not the one right under the track but below)
			if (underBlock2Stack != null && worldObj.getBlock(i + iX, j - 2 + hY, k + kZ) != Block.getBlockFromItem(underBlock2Stack.getItem())) {
				getBlockList(worldObj, i + iX, j - 2 + hY, k + kZ);
				worldObj.setBlock(i + iX, j - 2 + hY, k + kZ, Block.getBlockFromItem(underBlock2Stack.getItem()), underBlockStack.getItem().getMetadata(underBlockStack.getItemDamage()), 3);// changes the second block under
				decrStackInInvent(2, 1, 1);//decr underblock2
			}

			int d = 0;
			int roofYOffset = 0;
			if (hY != 0) {
				roofYOffset = 1;
			}
			//if the builder is digging on x axis
			if (iX != 0) {
				// if there is gravel, sand, water, lava,... in the ceiling then change the block
				putRoof(i + iX, j + 3 + roofYOffset + hY, k, 5, worldObj, upperCenterBlockStack);
				putRoof(i + iX, j + 3 + roofYOffset + hY, k - 1, 4, worldObj, upperBlockStack);
				putRoof(i + iX, j + 3 + roofYOffset + hY, k + 1, 6, worldObj, upperBlock1Stack);
				d = iX;

				//if the builder is digging on z axis
			}
			else if (kZ != 0) {
				putRoof(i, j + 3 + roofYOffset + hY, k + kZ, 5, worldObj, upperCenterBlockStack);
				putRoof(i - 1, j + 3 + roofYOffset + hY, k + kZ, 4, worldObj, upperBlockStack);
				putRoof(i + 1, j + 3 + roofYOffset + hY, k + kZ, 6, worldObj, upperBlock1Stack);
				d = kZ;
			}
			//when tunnel is active, blocks are dug further in front to let space for glass 
			if (tunnelActive) {
				d = 3 * d;
			}
			//if the builder is digging on x axis
			if (iX != 0) {
				digOnXAxis(i, j, k, d, iX, hY);
			}
			else if (kZ != 0) {
				digOnZAxis(i, j, k, d, kZ, hY);
			}

			//torchPlacer(i, j, k, iX, kZ);

			if (hY == 0 && !BlockRailBase.func_150051_a(worldObj.getBlock(i, j + hY, k)) && !BlockRailBase.func_150051_a(worldObj.getBlock(i, j, k)) && Blocks.rail.canPlaceBlockAt(worldObj, i, j + hY, k)) {
				checkForTracks();
				trackfuel--;

				if (!worldObj.isRemote) {
					decrStackInInvent(1, 1, 1);
				}
				RailTools.placeRailAt(tracksStack.copy(), worldObj, i, j + hY, k);
			}
			else if (hY < 0 && worldObj.isAirBlock(i, j + hY, k) && !worldObj.isAirBlock(i, j + hY - 1, k) && Blocks.rail.canPlaceBlockAt(worldObj, i, j + hY, k)) {
				checkForTracks();
				trackfuel--;

				if (!worldObj.isRemote) {
					decrStackInInvent(1, 1, 1);
				}
				RailTools.placeRailAt(tracksStack.copy(), worldObj, i, j + hY, k);

				// builder goes up
			}
			else if (hY > 0 && !BlockRailBase.func_150051_a(worldObj.getBlock(i + iX, j + hY, k + kZ)) && !BlockRailBase.func_150051_a(worldObj.getBlock(i + iX, j + hY + 1, k + kZ)) && !BlockRailBase.func_150051_a(worldObj.getBlock(i + iX, j, k + kZ)) && !BlockRailBase.func_150051_a(worldObj.getBlock(i, j + hY, k)) && !BlockRailBase.func_150051_a(worldObj.getBlock(i, j - hY, k)) && Blocks.rail.canPlaceBlockAt(worldObj, i + iX, j + hY, k + kZ)) {
				checkForTracks();
				trackfuel--;
				if (!worldObj.isRemote) {
					decrStackInInvent(1, 1, 1);
				}
				RailTools.placeRailAt(tracksStack.copy(), worldObj, i + iX, j + hY, k + kZ);
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public void markDirty()
	{
		
	}
}