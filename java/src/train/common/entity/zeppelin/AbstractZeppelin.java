package src.train.common.entity.zeppelin;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import src.train.client.core.handlers.TCKeyHandler;
import src.train.common.Traincraft;
import src.train.common.core.handlers.ConfigHandler;
import src.train.common.core.handlers.PacketHandler;
import src.train.common.library.GuiIDs;
import src.train.common.library.ItemData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractZeppelin extends Entity implements IInventory {
	protected ItemStack zeppInvent[];
	public int numCargoSlots;
	public int numCargoSlots1;
	public int numCargoSlots2;
	public int inventorySize;
	public int fuel;
	public boolean idle;
	public boolean altitude;

	public int boatCurrentDamage;
	public int boatTimeSinceHit;
	public int boatRockDirection;
	/*
	 * Unknown variables
	 */
	protected int field_9394_d;
	protected double field_9393_e;
	protected double field_9392_f;
	protected double field_9391_g;
	protected double field_9390_h;
	protected double field_9389_i;
	protected double field_9388_j;
	protected double field_9387_k;
	protected double field_9386_l;
	protected boolean field_70279_a;
	protected double field_70276_b;
	
	protected int boatPosRotationIncrements;
	protected double boatX;
	protected double boatY;
	protected double boatZ;
	protected double boatYaw;
	protected double boatPitch;
	@SideOnly(Side.CLIENT)
	protected double velocityX;
	@SideOnly(Side.CLIENT)
	protected double velocityY;
	@SideOnly(Side.CLIENT)
	protected double velocityZ;
	public double rotationYawClient;
	protected double updateTicks;
	public float pitch = 0F;
	public float roll = 0F;
	private int bombTimer;
	
	public AbstractZeppelin(World world) {
		super(world);
		boatCurrentDamage = 0;
		boatTimeSinceHit = 0;
		boatRockDirection = 1;
		preventEntitySpawning = true;
		setSize(2F, 0.8F);
		yOffset = height / 2.0F;
		numCargoSlots = 3;
		numCargoSlots1 = 3;
		numCargoSlots2 = 3;
		inventorySize = numCargoSlots + numCargoSlots2 + numCargoSlots1 + 5;
		zeppInvent = new ItemStack[inventorySize];
		idle = false;
		if (ConfigHandler.FLICKERING) {
			this.ignoreFrustumCheck = true;
		}
		this.dataWatcher.addObject(20, new Integer(fuel));
		this.field_70279_a = true;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void entityInit() {}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return entity.boundingBox;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		if (entity != entity.riddenByEntity) {
			double var2 = entity.posX - this.posX;
			double var4 = entity.posZ - this.posZ;
			double var6 = MathHelper.abs_max(var2, var4);

			if (var6 >= 0.009999999776482582D) {
				var6 = (double) MathHelper.sqrt_double(var6);
				var2 /= var6;
				var4 /= var6;
				double var8 = 1.0D / var6;

				if (var8 > 1.0D) {
					var8 = 1.0D;
				}

				var2 *= var8;
				var4 *= var8;
				var2 *= 0.05000000074505806D;
				var4 *= 0.05000000074505806D;
				var2 *= (double) (1.0F - this.entityCollisionReduction);
				var4 *= (double) (1.0F - this.entityCollisionReduction);
				this.addVelocity(-var2, 0.0D, -var4);
				entity.addVelocity(var2, 0.0D, var4);
			}
		}
		return;
	}

	public AbstractZeppelin(World world, double d, double d1, double d2) {
		this(world);
		setPosition(d, d1 + (double) yOffset, d2);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		prevPosX = d;
		prevPosY = d1;
		prevPosZ = d2;
	}

	@Override
	public double getMountedYOffset() {
		return (double) height * 0.0D - 0.30000001192092896D;
	}

	@Override
	public void setDead() {
		super.setDead();
		isDead = true;
		if (worldObj.isRemote) { return; }
		label0: for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack itemstack = getStackInSlot(i);
			if (itemstack == null) {
				continue;
			}
			float f = rand.nextFloat() * 0.8F + 0.1F;
			float f1 = rand.nextFloat() * 0.8F + 0.1F;
			float f2 = rand.nextFloat() * 0.8F + 0.1F;
			do {
				if (itemstack.stackSize <= 0) {
					continue label0;
				}
				int k = rand.nextInt(21) + 10;
				if (k > itemstack.stackSize) {
					k = itemstack.stackSize;
				}
				itemstack.stackSize -= k;
				EntityItem entityitem = new EntityItem(worldObj, posX + (double) f, posY + (double) f1, posZ + (double) f2, new ItemStack(itemstack.getItem(), k, itemstack.getItemDamage()));
				float f3 = 0.05F;
				entityitem.motionX = (float) rand.nextGaussian() * f3;
				entityitem.motionY = (float) rand.nextGaussian() * f3 + 0.2F;
				entityitem.motionZ = (float) rand.nextGaussian() * f3;
				worldObj.spawnEntityInWorld(entityitem);
			}
			while (true);
		}
	}

	public void pressKey(int i)
	{
		if (i == 0) {
			if (getFuel() > 0 && posY < 256) {
				/**
				 * up
				 */
				altitude = true;
				idle = false;
			}
		}

		if (i == 2)
		{
			/**
			 * down
			 */
			altitude = false;
			idle = false;
		}
		if (i == 3) {
			if (getFuel() > 0) {

			}
		}
		if (i == 1) {

		}
		/**
		 * idle key
		 */
		if (i == 6) {
			altitude = false;
			idle = true;
		}

		if (i == 7 && !worldObj.isRemote)
		{
			((EntityPlayer) riddenByEntity).openGui(Traincraft.instance, GuiIDs.ZEPPELIN, worldObj, this.getEntityId(), -1, (int) this.posZ);
		}
		if (i == 9)
		{
			if (this.riddenByEntity != null && (this.riddenByEntity instanceof EntityLivingBase)&&bombTimer<=0) {
				if(this.zeppInvent!=null && this.zeppInvent.length>0){
					for(int t=0;t<this.zeppInvent.length;t++){
						if(this.zeppInvent[t]!=null && this.zeppInvent[t].getItem()!=null && this.zeppInvent[t].getItem() == Item.getItemFromBlock(Blocks.tnt))
						{
							EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(this.worldObj, (double) ((float) posX), (double) ((float) posY -1F), (double) ((float) posZ), (EntityLivingBase) this.riddenByEntity);
							this.worldObj.spawnEntityInWorld(entitytntprimed);
							this.worldObj.playSoundAtEntity(entitytntprimed, "random.fuse", 1.0F, 1.0F);
							bombTimer=100;
							if(--this.zeppInvent[t].stackSize==0)this.zeppInvent[t]=null;
							return;
						}
					}
				}
			}
		}
	}

	public double speedXFromPitch(EntityPlayer player, double var3) {
		return (double) (-MathHelper.sin((player.rotationYaw) / 180.0F * (float) Math.PI) * var3 * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * var3);
	}

	public double speedZFromPitch(EntityPlayer player, double var3) {
		return (double) (MathHelper.cos((player.rotationYaw) / 180.0F * (float) Math.PI) * var3 * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * var3);
	}

	public double speedXFromPitch(Entity entity, double var3) {
		return (double) (-MathHelper.sin((entity.rotationYaw + 90) / 180.0F * (float) Math.PI) * var3 * MathHelper.cos(entity.rotationPitch / 180.0F * (float) Math.PI) * var3);
	}

	public double speedZFromPitch(Entity entity, double var3) {
		return (double) (MathHelper.cos((entity.rotationYaw + 90) / 180.0F * (float) Math.PI) * var3 * MathHelper.cos(entity.rotationPitch / 180.0F * (float) Math.PI) * var3);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void performHurtAnimation() {
		boatRockDirection = -boatRockDirection;
		boatTimeSinceHit = 10;
		boatCurrentDamage += boatCurrentDamage * 10;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
	 * posY, posZ, yaw, pitch
	 */
	@Override
	public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
		if (this.field_70279_a) {
			this.boatPosRotationIncrements = par9 + 5;
		}
		else {
			double var10 = par1 - this.posX;
			double var12 = par3 - this.posY;
			double var14 = par5 - this.posZ;
			double var16 = var10 * var10 + var12 * var12 + var14 * var14;

			if (var16 <= 1.0D) { return; }
			this.boatPosRotationIncrements = 3;
		}

		this.boatX = par1;
		this.boatY = par3;
		this.boatZ = par5;
		this.boatYaw = (double) par7;
		this.boatPitch = (double) par8;
		this.motionX = this.velocityX;
		this.motionY = this.velocityY;
		this.motionZ = this.velocityZ;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Sets the velocity to the args. Args: x, y, z
	 */
	@Override
	public void setVelocity(double par1, double par3, double par5) {
		this.velocityX = this.motionX = par1;
		this.velocityY = this.motionY = par3;
		this.velocityZ = this.motionZ = par5;
	}

	/**
	 * Used to setup more passengers seats!
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vec3 rotate(double x, double y, double z) {
		double cosYaw = Math.cos(this.getYaw() * 3.141593F / 180.0F);
		double sinYaw = Math.sin(this.getYaw() * 3.141593F / 180.0F);
		double cosPitch = Math.cos((this.getPitch()) * 3.141593F / 180.0F);
		double sinPitch = Math.sin((this.getPitch()) * 3.141593F / 180.0F);
		double cosRoll = Math.cos(0 * 3.141593F / 180.0F);// 0.01745
		double sinRoll = Math.sin(0 * 3.141593F / 180.0F);// 0

		double newX = (-x * cosRoll - y * sinRoll) * cosYaw + ((-x * sinRoll + y * cosRoll) * 0 + z * 0.01745) * sinYaw;
		double newY = -(((cosPitch - x) * -sinPitch));
		double newZ = (y * sinRoll - x * cosRoll) * sinYaw + ((-x * sinRoll + y * cosRoll) * 0 + z * 0.01745) * cosYaw;

		return Vec3.createVectorHelper(newX, newY, newZ);
	}

	public float getYaw() {
		return this.rotationYaw;
	}

	public float getPitch() {
		return this.rotationPitch;
	}

	@Override
	public void onUpdate()
	{
		this.fallDistance = 0F;
		super.onUpdate();
		updateTicks++;
		if(bombTimer>0)bombTimer--;
		if (boatTimeSinceHit > 0) {
			boatTimeSinceHit--;
		}
		if (boatCurrentDamage > 0) {
			boatCurrentDamage--;
		}

		if (riddenByEntity == null) {
			pitch = 0F;
		}

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		int i = 5;
		double d = 0.0D;
		for (int j = 0; j < i; j++) {
			double d4 = (boundingBox.minY + ((boundingBox.maxY - boundingBox.minY) * (double) (j + 0)) / (double) i) - 0.125D;
			double d8 = (boundingBox.minY + ((boundingBox.maxY - boundingBox.minY) * (double) (j + 1)) / (double) i) - 0.125D;
			AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(boundingBox.minX, d4, boundingBox.minZ, boundingBox.maxX, d8, boundingBox.maxZ);
			if (worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
				d += 1.0D / (double) i;
			}
		}
		double d13;
		double d11 = Math.sqrt(motionX * motionX + motionZ * motionZ);
		if (getFuel() > 0) {
			d13 = Math.cos(((double) rotationYaw * 3.1415926535897931D) / 180D);
			double d15 = Math.sin(((double) rotationYaw * 3.1415926535897931D) / 180D);
			double d18 = rand.nextFloat() * 2.0F - 1.0F;
			double d20 = (double) (rand.nextInt(2) * 2 - 1) * 0.69999999999999996D;
			if (rand.nextBoolean()) {
				double d21 = (posX - d13 * d18 * 0.80000000000000004D) + d15 * d20;
				double d23 = posZ - d15 * d18 * 0.80000000000000004D - d13 * d20;
				worldObj.spawnParticle("largesmoke", d21, posY - 0.125D, d23, motionX, motionY, motionZ);
			}
			else {
				double d22 = posX + d13 + d15 * d18 * 0.69999999999999996D;
				double d24 = (posZ + d15) - d13 * d18 * 0.69999999999999996D;
				worldObj.spawnParticle("largesmoke", d22, posY - 0.125D, d24, motionX, motionY, motionZ);
			}
		}
		if (getFuel() <= 0) {
			if (zeppInvent[0] != null && zeppInvent[0].getItem() == Items.coal) 
			{
				fuel = 1000;
				this.dataWatcher.updateObject(20, fuel);
				decrStackSize(0, 1);
			}
		}

		double var24 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		double var6;
		double var8;

		if (var24 > 0.26249999999999996D) {
			var6 = Math.cos((double) this.rotationYaw * Math.PI / 180.0D);
			var8 = Math.sin((double) this.rotationYaw * Math.PI / 180.0D);
		}

		double var12;
		double var26;

		if (this.worldObj.isRemote && this.field_70279_a) {
			if (this.boatPosRotationIncrements > 0) {
				var6 = this.posX + (this.boatX - this.posX) / (double) this.boatPosRotationIncrements;
				var8 = this.posY + (this.boatY - this.posY) / (double) this.boatPosRotationIncrements;
				var26 = this.posZ + (this.boatZ - this.posZ) / (double) this.boatPosRotationIncrements;
				var12 = MathHelper.wrapAngleTo180_double(this.boatYaw - (double) this.rotationYaw);
				rotationYaw = (float) rotationYawClient;
				this.rotationPitch = (float) ((double) this.rotationPitch + (this.boatPitch - (double) this.rotationPitch) / (double) this.boatPosRotationIncrements);
				--this.boatPosRotationIncrements;
				this.setPosition(var6, var8, var26);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			}
			else {
				var6 = this.posX + this.motionX;
				var8 = this.posY + this.motionY;
				var26 = this.posZ + this.motionZ;
				this.setPosition(var6, var8, var26);

				if (this.onGround) {
					this.motionX *= 0.5D;
					this.motionY *= 0.5D;
					this.motionZ *= 0.5D;
				}

				this.motionX *= 0.9900000095367432D;
				this.motionY *= 0.949999988079071D;
				this.motionZ *= 0.9900000095367432D;
			}
			return;
		}
		double d5;
		double speedMultiplier = 0.07;
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase) {
			d13 = (double) ((EntityLivingBase) this.riddenByEntity).moveForward;

			if (d13 > 0.0D) {
				d5 = -Math.sin((double) (this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F));
				d11 = Math.cos((double) (this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F));
				this.motionX += d5 * speedMultiplier * 0.05000000074505806D;
				this.motionZ += d11 * speedMultiplier * 0.05000000074505806D;
			}
		}

		if (rand.nextInt(4) == 0 && fuel > 0) {
			fuel = this.dataWatcher.getWatchableObjectInt(20);
			fuel--;
			this.dataWatcher.updateObject(20, fuel);
		}
		double d3 = d * 2D - 1.0D;
		motionY += 0.039999999105930328D * d3;
		if (altitude == true && posY < 256) {

			motionY = 0.051;

		}
		else if ((idle != true && altitude != true) || (posY > 256 && idle != true)) {
			motionY = -0.021;
		}
		else if (idle == true) {
			motionY = 0;
		}

		double d7 = 0.30000000000000002D;
		if (motionX < -d7) {
			motionX = -d7;
		}
		if (motionX > d7) {
			motionX = d7;
		}
		if (motionZ < -d7) {
			motionZ = -d7;
		}
		if (motionZ > d7) {
			motionZ = d7;
		}
		if (onGround) {
			motionX *= 0.5D;
			motionY *= 0.5D;
			motionZ *= 0.5D;
		}

		moveEntity(motionX, motionY, motionZ);

		if (isCollidedHorizontally && d11 > 0.24999999999999999D) {
			//if(!worldObj.isRemote) { setDead(); worldObj.createExplosion(this, posX,posY, posZ, 1F); dropItemWithOffset(ConfigHandler.ZepplinItem.shiftedIndex, 1, 0.0F);}
		}
		else {
			motionX *= 0.99000000953674316D;
			motionY *= 0.94999998807907104D;
			motionZ *= 0.99000000953674316D;
		}

		List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
		if (list != null && list.size() > 0) {
			for (int j1 = 0; j1 < list.size(); j1++) {
				Entity entity = (Entity) list.get(j1);
				if (entity != riddenByEntity && entity.canBePushed() && (entity instanceof AbstractZeppelin)) {
					entity.applyEntityCollision(this);
				}
			}
		}
		if (riddenByEntity != null && riddenByEntity.isDead) {
			riddenByEntity = null;
		}

		double rot = (double) this.rotationYaw;
		double div11 = this.prevPosX - this.posX;
		double div10 = this.prevPosZ - this.posZ;

		if ((div11 * div11) + (div10 * div10) > 0.001D) {
			rot = (double) ((float) (Math.atan2(div10, div11) * 180.0D / Math.PI));
		}

		double d12 = MathHelper.wrapAngleTo180_double(rot - (double) this.rotationYaw);

		if (d12 > 40.0D) {
			d12 = 40.0D;
		}

		if (d12 < -40.0D) {
			d12 = -40.0D;
		}
		this.rotationYaw = (float) ((double) this.rotationYaw + d12);
		this.setRotation(this.rotationYaw, this.rotationPitch);
		PacketHandler.sendPacketToClients(PacketHandler.setRotationPacketZeppelin(this, rotationYaw, roll), worldObj, (int) posX, (int) posY, (int) posZ, 400D);
	}

	@Override
	public void updateRiderPosition() {
		if (riddenByEntity == null) {
			return;
		}
		else {
			double d = Math.cos(((double) rotationYaw * 3.1415926535897931D) / 180D) * 0.60000000000000002D;
			double d1 = Math.sin(((double) rotationYaw * 3.1415926535897931D) / 180D) * 0.60000000000000002D;
			riddenByEntity.setPosition(posX + d, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ + d1);
			riddenByEntity.fallDistance = 0F;//no more damages on landing
			if (riddenByEntity instanceof EntityLiving) {
				pitch = ((EntityPlayer) riddenByEntity).rotationPitch * -1;
			}
			return;
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {

		nbttagcompound.setInteger("Fuel", getFuel());
		nbttagcompound.setBoolean("altitude", altitude);
		nbttagcompound.setBoolean("idle", idle);
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < zeppInvent.length; i++) {
			if (zeppInvent[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				zeppInvent[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("Items", nbttaglist);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.dataWatcher.updateObject(20, nbttagcompound.getInteger("Fuel"));
		this.altitude = nbttagcompound.getBoolean("altitude");
		this.idle = nbttagcompound.getBoolean("idle");
		NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 10);
		zeppInvent = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 0xff;
			if (j >= 0 && j < zeppInvent.length) {
				zeppInvent[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}

	@Override
	public float getShadowSize() {
		return 0.0F;
	}

	@Override
	public int getSizeInventory() {
		return inventorySize;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return zeppInvent[i];
	}

	/**
	 * works exactly like getStackInSlot, is only used upon closing GUIs
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (this.zeppInvent[par1] != null) {
			ItemStack var2 = this.zeppInvent[par1];
			this.zeppInvent[par1] = null;
			return var2;
		}
		else {
			return null;
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (zeppInvent[i] != null) {
			if (zeppInvent[i].stackSize <= j) {
				ItemStack itemstack = zeppInvent[i];
				zeppInvent[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = zeppInvent[i].splitStack(j);
			if (zeppInvent[i].stackSize == 0) {
				zeppInvent[i] = null;
			}
			return itemstack1;
		}
		else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		zeppInvent[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return "Zeppelin";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean isFuelled() {
		return getFuel() > 0;
	}

	public int c(int i) {
		return (getFuel() * i) / 1000;
	}

	@Override
	public void markDirty() {}

	boolean ImIn = false;
	ItemStack itemstack;

	@Override
	public boolean interactFirst(EntityPlayer entityplayer) {
		itemstack = entityplayer.inventory.getCurrentItem();

		if (riddenByEntity != null && (riddenByEntity instanceof EntityPlayer) && riddenByEntity != entityplayer) { return true; }
		if (!worldObj.isRemote) {
			entityplayer.mountEntity(this);
		}
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (isDead) { return false; }
		return entityplayer.getDistanceSqToEntity(this) <= 64D;
	}

	public int getFuel() {
		return (this.dataWatcher.getWatchableObjectInt(20));
	}

	@SideOnly(Side.CLIENT)
	public void func_70270_d(boolean par1) {
		this.field_70279_a = par1;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}
	
	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}
}