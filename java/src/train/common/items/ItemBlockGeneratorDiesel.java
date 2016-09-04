/*******************************************************************************
 * Copyright (c) 2013 Spitfire4466. All rights reserved.
 * 
 * @name TrainCraft
 * @author Spitfire4466
 ******************************************************************************/

package src.train.common.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockGeneratorDiesel extends ItemBlock {

	public ItemBlockGeneratorDiesel(Block id) {
		super(id);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("\u00a77" + "BC: 8MJ/tick");
		par3List.add("\u00a77" + "IC2: 20EU/tick");
		par3List.add("\u00a77" + "Don't place 2 together.");
	}

	/**
	 * Return an item rarity from EnumRarity
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return EnumRarity.rare;
	}
}
