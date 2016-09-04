package src.train.common.items;

import java.util.List;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import src.train.common.Traincraft;
import src.train.common.library.Info;
import src.train.common.library.ItemData;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemWrench extends Item implements IToolWrench{

	public ItemWrench() {
		super();
		maxStackSize = 1;
		setCreativeTab(Traincraft.tcTab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(Info.modID.toLowerCase() + ":parts/" + ItemData.getIcon(this));
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		Block blockId = world.getBlock(x, y, z);
		if (blockId.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
			player.swingItem();
			return !world.isRemote;
		}
		return false;
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("\u00a77" + "Works same as a BC wrench.");
		par3List.add("\u00a77" + "Use it to change lantern color.");
		par3List.add("\u00a77" + "Use it to lock/unlock certain carts (passenger)");
		par3List.add("\u00a77" + "Use it to remove locked trains (OP only)");
	}

	@Override
	public boolean canWrench(EntityPlayer player, int x, int y, int z) {
		return true;
	}

	@Override
	public void wrenchUsed(EntityPlayer player, int x, int y, int z)
	{
	}

	@Override
	public boolean doesSneakBypassUse(World par2World, int par4, int par5, int par6, EntityPlayer player) {
		return true;
	}
}