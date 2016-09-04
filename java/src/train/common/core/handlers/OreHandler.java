/*******************************************************************************
 * Copyright (c) 2012 Mrbrutal. All rights reserved.
 * 
 * @name TrainCraft
 * @author Mrbrutal
 ******************************************************************************/

package src.train.common.core.handlers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import src.train.common.library.BlockData;
import src.train.common.library.ItemData;

public class OreHandler {

	public static void registerOres() {
		
		OreDictionary.registerOre("oreCopper", new ItemStack(BlockData.oreTC.block, 1, 0));
		OreDictionary.registerOre("oreOilsands", new ItemStack(BlockData.oreTC.block, 1, 1));
		OreDictionary.registerOre("orePetroleum", new ItemStack(BlockData.oreTC.block, 1, 2));

		OreDictionary.registerOre("ingotCopper", new ItemStack(ItemData.ingotCopper.item));

		OreDictionary.registerOre("ingotSteel", new ItemStack(ItemData.steel.item));
		
		OreDictionary.registerOre("dustPlastic", new ItemStack(ItemData.rawPlastic.item));
		
		OreDictionary.registerOre("dustCoal", new ItemStack(ItemData.coaldust.item));
	}
}
