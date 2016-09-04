package src.train.client.core.handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import src.train.common.core.managers.TierRecipe;
import src.train.common.library.BlockData;
import src.train.common.library.ItemData;
import src.train.common.recipes.ShapedTrainRecipes;
import src.train.common.recipes.ShapelessTrainRecipe;

public class RecipeBookHandler {
	/**
	 * This is used to show if the recipe can also be crafted in vanilla workbench
	 */
	public static String[] vanillaWorkTableRecipes = new String[20];

	public RecipeBookHandler() {
		vanillaWorkTableRecipes[0] = BlockData.assemblyTableI.block.getUnlocalizedName();
		vanillaWorkTableRecipes[1] = BlockData.assemblyTableII.block.getUnlocalizedName();
		vanillaWorkTableRecipes[2] = BlockData.assemblyTableIII.block.getUnlocalizedName();
		vanillaWorkTableRecipes[3] = BlockData.distilIdle.block.getUnlocalizedName();
		vanillaWorkTableRecipes[4] = BlockData.openFurnaceIdle.block.getUnlocalizedName();
		vanillaWorkTableRecipes[5] = BlockData.trainWorkbench.block.getUnlocalizedName();
		vanillaWorkTableRecipes[6] = ItemData.overalls.item.getUnlocalizedName();
		vanillaWorkTableRecipes[7] = ItemData.jacket.item.getUnlocalizedName();
		vanillaWorkTableRecipes[8] = ItemData.hat.item.getUnlocalizedName();
		vanillaWorkTableRecipes[11] = ItemData.recipeBook.item.getUnlocalizedName();
		vanillaWorkTableRecipes[12] = BlockData.lantern.block.getUnlocalizedName();
		vanillaWorkTableRecipes[14] = ItemData.pants_driver_paintable.item.getUnlocalizedName();
		vanillaWorkTableRecipes[15] = ItemData.pants_ticketMan_paintable.item.getUnlocalizedName();
		vanillaWorkTableRecipes[16] = ItemData.hat_driver_paintable.item.getUnlocalizedName();
		vanillaWorkTableRecipes[17] = ItemData.hat_ticketMan_paintable.item.getUnlocalizedName();
		vanillaWorkTableRecipes[18] = ItemData.jacket_driver_paintable.item.getUnlocalizedName();
		vanillaWorkTableRecipes[19] = ItemData.jacket_ticketMan_paintable.item.getUnlocalizedName();
	}

	public static List workbenchListCleaner(List recipeList)
	{
		ArrayList outputList = new ArrayList();
		ArrayList cleanedList = new ArrayList();
		for (int i = 0; i < recipeList.size(); i++)
		{
			if (recipeList.get(i) instanceof ShapedTrainRecipes)
			{
				if (outputList != null)
				{
					if (!outputList.contains(((ShapedTrainRecipes) recipeList.get(i)).getRecipeOutput().getItem()))
					{
						cleanedList.add(recipeList.get(i));
					}
				}
				else
				{
					cleanedList.add(recipeList.get(i));
				}
				outputList.add(((ShapedTrainRecipes) recipeList.get(i)).getRecipeOutput().getItem());
			}
			if (recipeList.get(i) instanceof ShapelessTrainRecipe)
			{
				if (outputList != null)
				{
					if (!outputList.contains(((ShapelessTrainRecipe) recipeList.get(i)).getRecipeOutput().getItem()))
					{
						cleanedList.add(recipeList.get(i));
					}
				}
				else
				{
					cleanedList.add(recipeList.get(i));
				}
				outputList.add(((ShapelessTrainRecipe) recipeList.get(i)).getRecipeOutput().getItem());
			}
		}
		return cleanedList;
	}

	public static List assemblyListCleaner(List recipeList)
	{
		ArrayList outputList = new ArrayList();
		ArrayList cleanedList = new ArrayList();
		for (int i = 0; i < recipeList.size(); i++)
		{
			ItemStack output = ((TierRecipe) recipeList.get(i)).getOutput();
			if (outputList != null)
			{
				if (!outputList.contains(((TierRecipe) recipeList.get(i)).getOutput().getItem()))
				{
					cleanedList.add(recipeList.get(i));
				}
			}
			else
			{
				cleanedList.add(recipeList.get(i));
			}
			outputList.add(((TierRecipe) recipeList.get(i)).getOutput().getItem());
		}
		return cleanedList;
	}
}