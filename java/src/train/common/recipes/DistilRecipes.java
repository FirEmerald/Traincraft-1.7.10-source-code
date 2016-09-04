package src.train.common.recipes;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import src.train.common.core.handlers.ConfigHandler;
import src.train.common.library.BlockData;
import src.train.common.library.ItemData;

public class DistilRecipes {

	private static final DistilRecipes smeltingBase = new DistilRecipes();
	private Map<Item, ItemStack> smeltingList;
	private Map<Item, Float> experienceList = new HashMap<Item, Float>();
	private Map<Item, Float> plasticChanceList = new HashMap<Item, Float>();
	private Map<Item, ItemStack> plasticList = new HashMap<Item, ItemStack>();

	public static final DistilRecipes smelting() {
		return smeltingBase;
	}

	private DistilRecipes()
	{
		smeltingList = new HashMap<Item, ItemStack>();
		//TODO test if copper works also
		if (ConfigHandler.ORE_GEN)
		{
			addSmelting(BlockData.oreTC.block, new ItemStack(ItemData.diesel.item, 2), 0.5F, 1, new ItemStack(ItemData.rawPlastic.item, 1));
		}
		addSmelting(Items.reeds, new ItemStack(ItemData.diesel.item), 0.2F, 4, new ItemStack(ItemData.rawPlastic.item, 1));
		addSmelting(Blocks.leaves, new ItemStack(ItemData.diesel.item), 0.2F, 6, new ItemStack(ItemData.rawPlastic.item, 2));
		addSmelting(Blocks.leaves2, new ItemStack(ItemData.diesel.item), 0.2F, 6, new ItemStack(ItemData.rawPlastic.item, 2));
		addSmelting(ItemData.diesel.item, new ItemStack(ItemData.diesel.item), 1F, 2, new ItemStack(ItemData.rawPlastic.item, 1));
		addSmelting(Items.wheat, new ItemStack(ItemData.diesel.item), 0.2F, 4, new ItemStack(ItemData.rawPlastic.item, 1));
	}
	/**
	 * 
	 * @param i: Input block id
	 * @param itemstack: Output
	 * @param exp: Experience
	 * @param plasticChance used as follow: Math.random(plasticChance)==0
	 * @param plasticSktack: the plastic output and output size
	 */
	public void addSmelting(Item item, ItemStack itemstack, float exp, int plasticChance, ItemStack plasticStack)
	{
		smeltingList.put(item, itemstack);
		plasticList.put(item, plasticStack);
		this.experienceList.put(plasticStack.getItem(), Float.valueOf(exp));
		this.plasticChanceList.put(item, Float.valueOf(plasticChance));
	}
	public void addSmelting(Block block, ItemStack itemstack, float exp, int plasticChance, ItemStack plasticStack)
	{
		addSmelting(Item.getItemFromBlock(block), itemstack, exp, plasticChance, plasticStack);
	}

	public float getExperience(Item item)
	{
		return this.experienceList.containsKey(item) ? ((Float) this.experienceList.get(item)).floatValue() : 0.0F;
	}

	public int getPlasticChance(Item item)
	{
		if (this.plasticChanceList.containsKey(item))
		{
			return (int) ((Float) this.plasticChanceList.get(item)).floatValue();
		}
		return 0;
	}

	public ItemStack getSmeltingResult(Item item)
	{
		return (ItemStack) smeltingList.get(item);
	}

	public ItemStack getPlasticResult(Item item)
	{
		return (ItemStack) plasticList.get(item);
	}

	public Map<Item, ItemStack> getSmeltingList()
	{
		return smeltingList;
	}
}
