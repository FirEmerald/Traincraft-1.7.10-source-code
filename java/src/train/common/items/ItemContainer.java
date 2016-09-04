/*******************************************************************************
 * Copyright (c) 2014 Mrbrutal. All rights reserved.
 * 
 * @name Traincraft
 * @author Mrbrutal
 ******************************************************************************/

package src.train.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import src.train.common.Traincraft;
import src.train.common.library.Info;
import src.train.common.library.ItemData;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemContainer extends Item {

	public ItemContainer() {
		super();
		maxStackSize = 64;
		setCreativeTab(Traincraft.tcTab);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(Info.modID.toLowerCase() + ":parts/" + ItemData.getIcon(this));
	}
	
	@Override
	public Item getContainerItem() {
		return ItemData.emptyCanister.item;
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		return new ItemStack(ItemData.emptyCanister.item);
	}

}
