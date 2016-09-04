package src.train.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import src.train.common.Traincraft;
import src.train.common.items.ItemTCRail;
import src.train.common.items.ItemTCRail.TrackTypes;
import src.train.common.library.BlockData;
import src.train.common.library.Info;
import src.train.common.library.ItemData;
import src.train.common.tile.TileTCRail;
import src.train.common.tile.TileTCRailGag;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTCRail extends Block {
	private IIcon texture;

	public BlockTCRail() {
		super(Material.iron);
		setCreativeTab(Traincraft.tcTab);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
	{
		Block block = par1World.getBlock(par2, par3, par4);
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
		TileTCRail tileEntity = (TileTCRail) world.getTileEntity(x, y, z);
		if (tileEntity != null) {
			return new ItemStack(tileEntity.idDrop);
		}
		return null;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block par5, int par6) {
		TileTCRail tileEntity = (TileTCRail) world.getTileEntity(i, j, k);
		if (tileEntity != null && tileEntity.isLinkedToRail) {
			world.setBlockToAir(tileEntity.linkedX, tileEntity.linkedY, tileEntity.linkedZ);
			world.removeTileEntity(tileEntity.linkedX, tileEntity.linkedY, tileEntity.linkedZ);
		}
		if (tileEntity != null && tileEntity.idDrop != null && !world.isRemote) {
			EntityPlayer player = Traincraft.proxy.getPlayer();
			boolean flag = player instanceof EntityPlayer && ((EntityPlayer)player).capabilities.isCreativeMode;
			if(!flag) {
				this.dropBlockAsItem(world, i, j, k, new ItemStack(tileEntity.idDrop, 1, 0));
			}
		}
		world.removeTileEntity(i, j, k);
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block par5) {
		TileEntity tile = world.getTileEntity(i, j, k);
		if (tile == null || !(tile instanceof TileTCRail))
			return;

		TileTCRail tileEntity = (TileTCRail) world.getTileEntity(i, j, k);
		if (tileEntity != null && tileEntity.isLinkedToRail) {
			if (world.isAirBlock(tileEntity.linkedX, tileEntity.linkedY, tileEntity.linkedZ)) {
				world.setBlockToAir(i, j, k);
				world.removeTileEntity(i, j, k);
			}
		}
		if (!World.doesBlockHaveSolidTopSurface(world, i, j - 1, k) && world.getBlock(i, j-1, k) != BlockData.bridgePillar.block) {
			world.setBlockToAir(i, j, k);
			world.removeTileEntity(i, j, k);
		}
		if (tileEntity != null && !world.isRemote) {
			boolean flag = world.isBlockIndirectlyGettingPowered(i, j, k);
			boolean switchState = tileEntity.getSwitchState();
			if (tileEntity.previousRedstoneState != flag) {
				tileEntity.changeSwitchState(world, tileEntity, i, j, k);
				tileEntity.previousRedstoneState = flag;
			}
		}
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileTCRail();
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int par6, float par7, float par8, float par9) {
		TileEntity te = world.getTileEntity(i, j, k);
		int l = world.getBlockMetadata(i, j, k);
		if (!world.isRemote && te != null && (te instanceof TileTCRail)) {
			if (player != null && player.inventory != null && player.inventory.getCurrentItem() != null && (player.inventory.getCurrentItem().getItem() instanceof IToolWrench) && ((TileTCRail) te).getType() != null && ((TileTCRail) te).getType().equals(TrackTypes.SMALL_STRAIGHT.getLabel())) {
				l++;
				if (l > 3)
					l = 0;
				world.setBlockMetadataWithNotify(i, j, k, l, 2);
				((TileTCRail) te).hasRotated = true;
				return true;
			}
			//((TileTCRail)te).printInfo();
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		texture = iconRegister.registerIcon(Info.modID.toLowerCase() + ":tracks/rail_normal_turned");
	}

	@Override
	public IIcon getIcon(int i, int j) {
		return texture;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		return null;
	}
}
