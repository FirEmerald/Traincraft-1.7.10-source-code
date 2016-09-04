package src.train.common.items;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import src.train.common.Traincraft;

public class ItemWaterTankBuckets extends ItemBucket { // implements IBucketHandler

	public ItemWaterTankBuckets(Block blockId, int icon) {
		super(blockId);
		this.maxStackSize = 16;
		setCreativeTab(Traincraft.tcTab);
	}

	public boolean canBeStoredInToolbox(ItemStack itemstack) {
		return true;
	}

	public ItemStack fillCustomBucket(World w, int i, int j, int k) {
		if (w.getBlock(i, j, k) == Blocks.water) {
			w.setBlockToAir(i, j, k);
			return new ItemStack(this);
		}
		return null;
	}

}