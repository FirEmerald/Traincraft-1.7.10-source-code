package src.train.common.recipes;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import src.train.common.library.ItemData;

public class OpenHearthFurnaceRecipes {

	private static final OpenHearthFurnaceRecipes smeltingBase = new OpenHearthFurnaceRecipes();
	private Map<Item, ItemStack> smeltingListResult1;
	private Map<Item, Item> smeltingListResult2;
	private Map<Item, Integer> smeltingListCookTime;
	private Map<Item, Float> experienceList = new HashMap<Item, Float>();

	public static final OpenHearthFurnaceRecipes smelting() {
		return smeltingBase;
	}

	private OpenHearthFurnaceRecipes()
	{
		smeltingListResult1 = new HashMap<Item, ItemStack>();
		smeltingListResult2 = new HashMap<Item, Item>();
		smeltingListCookTime = new HashMap<Item, Integer>();
		addSmelting(Items.iron_ingot, ItemData.graphite.item, new ItemStack(ItemData.steel.item), 2F, 1000);
		//addSmelting(ItemData.graphite.itemID,Item.ingotIron.shiftedIndex, new ItemStack(ItemData.steel.item),2F);
	}

	public void addSmelting(Item i, Item j, ItemStack itemstack, float exp, int cookTime) {
		smeltingListResult1.put(i, itemstack);
		smeltingListResult1.put(j, itemstack);
		smeltingListResult2.put(i, j);
		smeltingListResult2.put(j, i);
		this.experienceList.put(i, Float.valueOf(exp));
		this.experienceList.put(j, Float.valueOf(exp));
		smeltingListCookTime.put(i, cookTime);
		smeltingListCookTime.put(j, cookTime);
	}

	public float getExperience(Item item) {
		return this.experienceList.containsKey(item) ? ((Float) this.experienceList.get(item)).floatValue() : 0.0F;
	}

	public ItemStack getSmeltingResultFromItem1(Item item) {
		return (ItemStack) smeltingListResult1.get(item);
	}

	public int getCookTime(ItemStack i, ItemStack j) {
		if (i != null && j != null) {
			int resultFrom1 = 1000;
			int resultFrom2 = 1000;
			if (smeltingListCookTime.containsKey(i.getItem()))
				resultFrom1 = (Integer) smeltingListCookTime.get(i.getItem());
			if (smeltingListCookTime.containsKey(j.getItem()))
				resultFrom2 = (Integer) smeltingListCookTime.get(j.getItem());
			return resultFrom1 != 0 ? resultFrom1 : resultFrom2;
		}
		return 600;
	}

	public boolean areItemPartOfRecipe(ItemStack i, ItemStack j) {
		ItemStack resultFrom1 = (ItemStack) smeltingListResult1.get(i.getItem());
		ItemStack resultFrom2 = (ItemStack) smeltingListResult1.get(j.getItem());
		if (resultFrom1 == null || resultFrom2 == null) {
			return false;
		}
		if (resultFrom1.areItemStacksEqual(resultFrom1, resultFrom2)) {
			return true;
		}
		return false;
	}

	public Map<Item, ItemStack> getSmeltingList() {
		return smeltingListResult1;
	}
	public Map<Item, Item> getSmeltingList2() {
		return smeltingListResult2;
	}
}
