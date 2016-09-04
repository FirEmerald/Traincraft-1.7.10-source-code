package src.train.common.core.handlers;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class BuilderOreHandler {
	public static boolean isOre(ItemStack id) {
		ArrayList<ItemStack> oreList = OreDictionary.getOres(OreDictionary.getOreID(id));
		if (oreList != null && oreList.size() > 0) {
			return true;
		}
		return false;

	}
}