package src.train.common.entity.rollingStock;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import src.train.common.Traincraft;
import src.train.common.api.AbstractTrains;
import src.train.common.api.AbstractWorkCart;
import src.train.common.library.GuiIDs;

public class EntityCabooseWorkCart extends AbstractWorkCart implements IInventory {

	public EntityCabooseWorkCart(World world) {
		super(world);
		initCabooseWorkCart();
	}

	public void initCabooseWorkCart() {
		furnaceItemStacks = new ItemStack[3];
		furnaceBurnTime = 0;
		currentItemBurnTime = 0;
		furnaceCookTime = 0;
	}

	public EntityCabooseWorkCart(World world, double d, double d1, double d2) {
		this(world);
		setPosition(d, d1 + yOffset, d2);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		prevPosX = d;
		prevPosY = d1;
		prevPosZ = d2;
	}

	@Override
	public void updateRiderPosition() {
		riddenByEntity.setPosition(posX, posY + getMountedYOffset() + riddenByEntity.getYOffset() + 0.15F, posZ);
	}

	@Override
	public void setDead() {
		super.setDead();
		isDead = true;
		if (worldObj.isRemote) {
			return;
		}
		label3: for (int i = 0; i < getSizeInventoryWork(); i++) {
			ItemStack itemstack = getStackInSlot(i);
			if (itemstack == null) {
				continue;
			}
			float f = rand.nextFloat() * 0.8F + 0.1F;
			float f1 = rand.nextFloat() * 0.8F + 0.1F;
			float f2 = rand.nextFloat() * 0.8F + 0.1F;
			do {
				if (itemstack.stackSize <= 0) {
					continue label3;
				}
				int j = rand.nextInt(21) + 10;
				if (j > itemstack.stackSize) {
					j = itemstack.stackSize;
				}
				itemstack.stackSize -= j;
				EntityItem entityitem = new EntityItem(worldObj, posX + f, posY + f1, posZ + f2, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));
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
	public void pressKey(int i) {
		if(locked && riddenByEntity != null && riddenByEntity instanceof EntityPlayer&& !((EntityPlayer)riddenByEntity).getGameProfile().getId().equals(this.trainOwner)){
			return;
		}
		if (i == 7 && riddenByEntity != null && riddenByEntity instanceof EntityPlayer) {
			((EntityPlayer) riddenByEntity).openGui(Traincraft.instance, GuiIDs.CRAFTING_CART, worldObj, (int) this.posX, (int) this.posY, (int) this.posZ);
		}
		if (i == 9 && riddenByEntity != null && riddenByEntity instanceof EntityPlayer) {
			((EntityPlayer) riddenByEntity).openGui(Traincraft.instance, GuiIDs.FURNACE_CART, worldObj, (int) this.posX, (int) this.posY, (int) this.posZ);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		updateBurning();
	}

	@Override
	public String getInventoryName() {
		return "Caboose";
	}

	@Override
	public boolean interactFirst(EntityPlayer entityplayer) {
		if ((super.interactFirst(entityplayer))) {
			return false;
		}
		if (!worldObj.isRemote) {
			ItemStack itemstack = entityplayer.inventory.getCurrentItem();
			if(lockThisCart(itemstack, entityplayer))return true;
			if (riddenByEntity != null && (riddenByEntity instanceof EntityPlayer) && riddenByEntity != entityplayer) {
				return true;
			}
			if (!worldObj.isRemote) {
				entityplayer.mountEntity(this);
			}
		}
		return true;
	}

	@Override
	public boolean isStorageCart() {
		return false;
	}

	@Override
	public boolean canBeRidden() {
		return true;
	}

	@Override
	public float getOptimalDistance(EntityMinecart cart) {
		return (1.45F);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (isDead) {
			return false;
		}
		return entityplayer.getDistanceSqToEntity(this) <= 124D;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}
}