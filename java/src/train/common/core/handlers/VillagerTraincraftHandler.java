package src.train.common.core.handlers;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import src.train.common.generation.ComponentVillageTrainstation;
import src.train.common.items.ItemRollingStock;
import src.train.common.library.ItemData;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public class VillagerTraincraftHandler implements IVillageCreationHandler, IVillageTradeHandler
{
	//private Random rand = new Random();
	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random)
	{
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Blocks.rail, random,20), Items.emerald));
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.emerald, random,20), new ItemStack(Blocks.rail)));
		
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Blocks.activator_rail, random,4), Items.emerald));
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.emerald, random,4), new ItemStack(Blocks.activator_rail)));
		
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Blocks.detector_rail, random,4), Items.emerald));
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.emerald, random,4), new ItemStack(Blocks.detector_rail)));
		
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Blocks.golden_rail, random,10), Items.emerald));
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.emerald, random,10), new ItemStack(Blocks.golden_rail)));
		
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Blocks.brick_block, random,10), Items.emerald));
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.emerald, random,10), new ItemStack(Blocks.brick_block)));
		
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.minecart, random,2), Items.emerald));
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.emerald, random,2), new ItemStack(Items.minecart)));
		
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.chest_minecart, random,4), Items.emerald));
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.emerald, random,4), new ItemStack(Items.chest_minecart)));
		
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.hopper_minecart, random,1), Items.emerald));
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.emerald, random,1), new ItemStack(Items.hopper_minecart)));
		
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.furnace_minecart, random,2), Items.emerald));
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.emerald, random,2), new ItemStack(Items.furnace_minecart)));
		
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.tnt_minecart, random,1), Items.emerald));
		recipeList.add(new MerchantRecipe(getRandomSizedStack(Items.emerald, random,1), new ItemStack(Items.tnt_minecart)));
		
		for(ItemData item : ItemData.values())
		{
			if(item!=null && item.item!=null)
			{
				if(item.item instanceof ItemRollingStock)
				{
					recipeList.add(new MerchantRecipe(new ItemStack(item.item), new ItemStack(Items.emerald,item.amountForEmerald)));
					recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald,item.amountForEmerald), item.item));
				}
				else if(item.amountForEmerald>0)
				{
					if(!(item.item instanceof ItemRollingStock) && item.amountForEmerald>0)
					{
						recipeList.add(new MerchantRecipe(new ItemStack(item.item,item.amountForEmerald), Items.emerald));
						recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald), new ItemStack(item.item,item.amountForEmerald)));
					}
				}
			}
		}
	}
	private ItemStack getRandomSizedStack(Item par0, Random random, int amount)
    {
        return new ItemStack(par0, random.nextInt(amount)+1, 0);
    }
	private ItemStack getRandomSizedStack(Block par0, Random random, int amount)
    {
        return new ItemStack(par0, random.nextInt(amount)+1, 0);
    }
	
	@Override
	public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int size)
	{
		 return new StructureVillagePieces.PieceWeight(ComponentVillageTrainstation.class, 15, MathHelper.getRandomIntegerInRange(random, 0 + size, 1 + size));
	}
	@Override
	public Class<?> getComponentClass() {
		return ComponentVillageTrainstation.class;
	}
	@Override
	public Object buildComponent(StructureVillagePieces.PieceWeight weight, StructureVillagePieces.Start start, List pieces, Random random, int p1, int p2, int p3, int p4, int p5)
	{
		return ComponentVillageTrainstation.buildComponent(start, pieces, random, p1, p2, p3, p4, p5);
	}
}
