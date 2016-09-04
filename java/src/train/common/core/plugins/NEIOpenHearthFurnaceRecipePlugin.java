package src.train.common.core.plugins;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import src.train.client.gui.GuiOpenHearthFurnace;
import src.train.common.recipes.OpenHearthFurnaceRecipes;
import src.train.common.recipes.ShapedTrainRecipes;
import codechicken.nei.ItemList;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.ShapedRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import codechicken.nei.recipe.FurnaceRecipeHandler.FuelPair;
import codechicken.nei.recipe.FurnaceRecipeHandler.SmeltingPair;
import codechicken.nei.recipe.TemplateRecipeHandler.CachedRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler.RecipeTransferRect;

public class NEIOpenHearthFurnaceRecipePlugin extends ShapedRecipeHandler
{
	NEIOpenHearthFurnaceRecipePlugin.CachedShapedRecipe getShape(ItemStack ingredient1, ItemStack ingredient2, ItemStack result)
	{
		NEIOpenHearthFurnaceRecipePlugin.CachedShapedRecipe shape = new NEIOpenHearthFurnaceRecipePlugin.CachedShapedRecipe(0, 0, null, result);

		if (ingredient1 != null) {
			PositionedStack stack = new PositionedStack(ingredient1, 30, 6);
			stack.setMaxSize(1);
			shape.ingredients.add(stack);
			stack = null;
		}
		if (ingredient2 != null) {
			PositionedStack stack = new PositionedStack(ingredient2, 51, 6);
			stack.setMaxSize(1);
			shape.ingredients.add(stack);
		}

		shape.result.relx = 111;
		shape.result.rely = 24;
		return shape;
	}
	public class CachedShapedRecipe extends CachedRecipe
    {
        public ArrayList<PositionedStack> ingredients;
        public PositionedStack result;
        
        public CachedShapedRecipe(int width, int height, Object[] items, ItemStack out)
        {
            result = new PositionedStack(out, 119, 24);
            ingredients = new ArrayList<PositionedStack>();
            setIngredients(width, height, items);
        }
        
        public CachedShapedRecipe(ShapedRecipes recipe)
        {
            this(recipe.recipeWidth, recipe.recipeHeight, recipe.recipeItems, recipe.getRecipeOutput());
        }
        
        /**
         * @param width
         * @param height
         * @param items an ItemStack[] or ItemStack[][]
         */
        public void setIngredients(int width, int height, Object[] items)
        {
            for(int x = 0; x < width; x++)
            {
                for(int y = 0; y < height; y++)
                {
                    if(items[y*width+x] == null)
                        continue;
                    
                    PositionedStack stack = new PositionedStack(items[y*width+x], 25+x*18, 6+y*18, false);
                    stack.setMaxSize(1);
                    ingredients.add(stack);
                }
            }
        }
        
        @Override
        public List<PositionedStack> getIngredients()
        {
            return getCycledIngredients(cycleticks / 20, ingredients);
        }
        
        public PositionedStack getResult()
        {
            return result;
        }
        
        public void computeVisuals()
        {
            for(PositionedStack p : ingredients)
                p.generatePermutations();
            
            result.generatePermutations();
        }
        @Override
		public PositionedStack getOtherStack() {
			return afuels.get((cycleticks / 48) % afuels.size()).stack;
		}
    }
	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiOpenHearthFurnace.class;
	}

	@Override
	public String getRecipeName() {
		return "Open Hearth Furnace";
	}

	@Override
	public void loadTransferRects() {
		//transferRects.add(new RecipeTransferRect(new Rectangle(50, 23, 18, 18), "fuel"));
		transferRects.add(new RecipeTransferRect(new Rectangle(74, 23, 24, 18), "open hearth furnace"));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("open hearth furnace") && getClass() == NEIOpenHearthFurnaceRecipePlugin.class) {
			HashMap<Item, ItemStack> recipes = (HashMap<Item, ItemStack>) OpenHearthFurnaceRecipes.smelting().getSmeltingList();
			HashMap<Item, Item> recipesIngredient = (HashMap<Item, Item>) OpenHearthFurnaceRecipes.smelting().getSmeltingList2();

			for (Entry<Item, ItemStack> recipe : recipes.entrySet()) {
				ItemStack item = recipe.getValue();
				ItemStack item2 = new ItemStack(recipesIngredient.get(recipe.getKey()), 1, -1);
				arecipes.add(getShape(new ItemStack(recipe.getKey(), 1, -1), item2, item));
			}
		}
		else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		HashMap<Item, ItemStack> recipes = (HashMap<Item, ItemStack>) OpenHearthFurnaceRecipes.smelting().getSmeltingList();
		HashMap<Item, Item> recipesIngredient = (HashMap<Item, Item>) OpenHearthFurnaceRecipes.smelting().getSmeltingList2();

		for (Entry<Item, ItemStack> recipe : recipes.entrySet()) {
			ItemStack item = recipe.getValue();
			if (NEIServerUtils.areStacksSameType(item, result)) {
				ItemStack item2 = new ItemStack(recipesIngredient.get(recipe.getKey()), 1, -1);
				arecipes.add(getShape(new ItemStack(recipe.getKey(), 1, -1), item2, result));
			}
		}

	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if (inputId.equals("fuel") && getClass() == NEIOpenHearthFurnaceRecipePlugin.class) {
			loadCraftingRecipes("open hearth furnace");
		}
		else {
			super.loadUsageRecipes(inputId, ingredients);
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		HashMap<Item, ItemStack> recipes = (HashMap<Item, ItemStack>) OpenHearthFurnaceRecipes.smelting().getSmeltingList();
		HashMap<Item, Item> recipesIngredient = (HashMap<Item, Item>) OpenHearthFurnaceRecipes.smelting().getSmeltingList2();

		for (Entry<Item, ItemStack> recipe : recipes.entrySet()) {
			ItemStack item = recipe.getValue();
			if (ingredient.getItem() == recipe.getKey()) {
				ItemStack item2 = new ItemStack(recipesIngredient.get(recipe.getKey()), 1, -1);
				arecipes.add(getShape(ingredient, item2, item));
			}
		}
	}

	@Override
	public String getGuiTexture() {
		return "tc:textures/gui/gui_open_hearth_furnace.png";
	}

	public static class FuelPair {
		public FuelPair(ItemStack ingred, int burnTime) {
			this.stack = new PositionedStack(ingred, 42, 42, false);
			this.burnTime = burnTime;
		}

		public PositionedStack stack;
		public int burnTime;
	}

	public static ArrayList<FuelPair> afuels;
	public static TreeSet<Integer> efuels;

	@Override
	public TemplateRecipeHandler newInstance() {
		if (afuels == null) findFuels();
		return super.newInstance();
	}

	@Override
	public void drawExtras(int recipe) {
		drawProgressBar(51, 25, 176, 0, 14, 14, 48, 7);
		drawProgressBar(31, 25, 176, 0, 14, 14, 48, 7);
		drawProgressBar(74, 23, 176, 14, 24, 16, 48, 0);
	}

	private static void removeFuels() {
		efuels = new TreeSet<Integer>();
		efuels.add(Block.getIdFromBlock(Blocks.brown_mushroom_block));
		efuels.add(Block.getIdFromBlock(Blocks.red_mushroom_block));
		efuels.add(Block.getIdFromBlock(Blocks.standing_sign));
		efuels.add(Block.getIdFromBlock(Blocks.wall_sign));
		efuels.add(Block.getIdFromBlock(Blocks.wooden_door));
		efuels.add(Block.getIdFromBlock(Blocks.trapped_chest));
	}

	private static void findFuels() {
		afuels = new ArrayList<FuelPair>();
		for (ItemStack item : ItemList.items) {
			if (!efuels.contains(Item.getIdFromItem(item.getItem()))) {
				int burnTime = TileEntityFurnace.getItemBurnTime(item);
				if (burnTime > 0) afuels.add(new FuelPair(item.copy(), burnTime));
			}
		}
	}

	static {
		removeFuels();
	}
}
