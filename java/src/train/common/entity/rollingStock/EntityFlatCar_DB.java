package src.train.common.entity.rollingStock;

import java.util.ArrayList;
import java.util.List;

import buildcraft.api.tools.IToolWrench;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import src.train.common.api.AbstractTrains;
import src.train.common.api.EntityRollingStock;
import src.train.common.api.IPassenger;

public class EntityFlatCar_DB extends EntityRollingStock implements IPassenger {

	public EntityFlatCar_DB(World world) {
		super(world);
	}

	public EntityFlatCar_DB(World world, double d, double d1, double d2) {
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
	public void updateRiderPosition() {
		riddenByEntity.setPosition(posX, posY + getMountedYOffset() + riddenByEntity.getYOffset() + 0.4, posZ);
	}

	@Override
	public void setDead() {
		super.setDead();
		isDead = true;
	}

	@Override
	public boolean interactFirst(EntityPlayer entityplayer) {
		playerEntity = entityplayer;
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
	public boolean canBeRidden() {
		return true;
	}

	@Override
	public boolean isStorageCart() {
		return false;
	}

	@Override
	public boolean isPoweredCart() {
		return false;
	}

	@Override
	public float getOptimalDistance(EntityMinecart cart) {
		float dist = 1.84F;
		return (dist);
	}
}