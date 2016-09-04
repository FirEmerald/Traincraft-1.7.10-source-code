/*******************************************************************************
 * Copyright (c) 2012 Mrbrutal. All rights reserved.
 * 
 * @name TrainCraft
 * @author Mrbrutal
 ******************************************************************************/

package src.train.common.core.handlers;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import src.train.common.inventory.TrainCraftingManager;
import src.train.common.library.BlockData;
import src.train.common.library.ItemData;
import src.train.common.recipes.RecipesArmorDyes;
import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeHandler
{
	public static void initBlockRecipes()
	{
		TrainCraftingManager.instance.getRecipeList().add(new RecipesArmorDyes());
		/* Assembly tables */
		GameRegistry.addRecipe(new ItemStack(BlockData.assemblyTableI.block, 1), new Object[] { "IPI", "S S", "SPS", Character.valueOf('I'), Items.iron_ingot, Character.valueOf('P'), Blocks.piston, Character.valueOf('S'), Blocks.stone });
		GameRegistry.addRecipe(new ItemStack(BlockData.assemblyTableII.block, 1), new Object[] { "GPG", "O O", "OPO", Character.valueOf('G'), Items.gold_ingot, Character.valueOf('P'), Blocks.piston, Character.valueOf('O'), Blocks.obsidian });
		GameRegistry.addRecipe(new ItemStack(BlockData.assemblyTableIII.block, 1), new Object[] { "GPG", "DLD", "OPO", Character.valueOf('G'), Items.gold_ingot, Character.valueOf('P'), Blocks.piston, Character.valueOf('D'), Items.diamond, Character.valueOf('L'), Blocks.glowstone, Character.valueOf('O'), Blocks.obsidian });
		addDictRecipe(new ItemStack(BlockData.trainWorkbench.block, 1), new Object[] { "###", "IFI", "###", Character.valueOf('#'), "plankWood", Character.valueOf('F'), Blocks.furnace, Character.valueOf('I'), Items.iron_ingot });
		addDictRecipe(new ItemStack(BlockData.distilIdle.block, 1), new Object[] { "###", "#F#", "###", Character.valueOf('#'), "ingotSteel", Character.valueOf('F'), ItemData.firebox.item });

		/* Open Hearth Furnace */
		GameRegistry.addRecipe(new ItemStack(BlockData.openFurnaceIdle.block, 1), new Object[] { "#L#", "#B#", "#I#", Character.valueOf('#'), Blocks.nether_brick, Character.valueOf('L'), Items.lava_bucket, Character.valueOf('B'), Items.bucket, Character.valueOf('I'), Blocks.iron_block });

		/* Lantern */
		GameRegistry.addRecipe(new ItemStack(BlockData.lantern.block, 4), new Object[] { "III", "PTP", "III", Character.valueOf('I'), Items.iron_ingot, Character.valueOf('P'), Blocks.glass_pane, Character.valueOf('T'),Blocks.torch });
		
		/* Clothes */
		GameRegistry.addRecipe(new ItemStack(ItemData.overalls.item, 1), new Object[] { " # ", "X$X", "X$X", Character.valueOf('X'), new ItemStack(Items.dye, 1, 4), Character.valueOf('$'), Items.leather_leggings, Character.valueOf('#'), new ItemStack(Items.dye, 1, 1) });
		GameRegistry.addRecipe(new ItemStack(ItemData.jacket.item, 1), new Object[] { "X X", "X$X", "X#X", Character.valueOf('X'), new ItemStack(Items.dye, 1, 14), Character.valueOf('$'), Items.leather_chestplate, Character.valueOf('#'), Items.string });
		GameRegistry.addRecipe(new ItemStack(ItemData.hat.item, 1), new Object[] { " X ", "X$X", "#X#", Character.valueOf('X'), new ItemStack(Items.dye, 1, 4), Character.valueOf('$'), Items.leather_helmet, Character.valueOf('#'), Items.string });

		/* Driver Clothes*/
		GameRegistry.addRecipe(new ItemStack(ItemData.pants_driver_paintable.item, 1), new Object[] { "XXX", "XLX", "X$X", Character.valueOf('L'), Items.leather_leggings,Character.valueOf('$'), new ItemStack(Items.dye, 1, 4), Character.valueOf('X'), Items.string});
		GameRegistry.addRecipe(new ItemStack(ItemData.jacket_driver_paintable.item, 1), new Object[] { "X X", "XRX", "XPX", Character.valueOf('X'), new ItemStack(Items.dye, 1, 4), Character.valueOf('P'), Items.leather_chestplate,Character.valueOf('R'),  new ItemStack(Items.dye, 1, 1) });
		GameRegistry.addRecipe(new ItemStack(ItemData.hat_driver_paintable.item, 1), new Object[] {"#$#", "# #", Character.valueOf('$'), new ItemStack(Items.dye, 1, 4), Character.valueOf('#'), Items.string });
		
		/* Ticket Man Clothes */
		GameRegistry.addRecipe(new ItemStack(ItemData.pants_ticketMan_paintable.item, 1), new Object[] { "XXX", "XLX", "X$X", Character.valueOf('L'), Items.leather_leggings,Character.valueOf('$'), new ItemStack(Items.dye, 1, 8), Character.valueOf('X'), Items.string});
		GameRegistry.addRecipe(new ItemStack(ItemData.jacket_ticketMan_paintable.item, 1), new Object[] { "X X", "XPX", "X#X", Character.valueOf('P'), Items.leather_chestplate, Character.valueOf('#'), new ItemStack(Items.dye, 1, 4), Character.valueOf('X'), Items.string});
		GameRegistry.addRecipe(new ItemStack(ItemData.hat_ticketMan_paintable.item, 1), new Object[] {"#$#", "# #", Character.valueOf('$'), new ItemStack(Items.dye, 1, 0), Character.valueOf('#'), Items.string });
		
		/* Recipe book */
		GameRegistry.addRecipe(new ItemStack(ItemData.recipeBook.item, 1), new Object[] { "TTT", "TBT", "TTT", Character.valueOf('T'), Blocks.rail, Character.valueOf('B'), Items.book });

		/*Buffer*/
		addDictRecipe(new ItemStack(BlockData.stopper.block, 1), new Object[] { "WWW", "I I", "RRR", Character.valueOf('W'), "plankWood", Character.valueOf('R'), Blocks.rail, Character.valueOf('I'), Items.iron_ingot});
		
		GameRegistry.addRecipe(new ItemStack(BlockData.oreTC.block, 1,3), new Object[] { "GXG", Character.valueOf('G'), Blocks.gravel, Character.valueOf('X'), Items.clay_ball});
		
	}

	public static void initItemRecipes() {

		// Always do this " X " instead of this "X", and do not put "" empty brackets

		/* I placed it here because workbench should be one of the first recipe shown in the recipe book */
		ArrayList<ItemStack> planks = OreDictionary.getOres("plankWood");
		if (planks != null && planks.size() >= 0) {
			for (ItemStack plank : planks) {
				TrainCraftingManager.instance.addRecipe(new ItemStack(BlockData.trainWorkbench.block, 1), new Object[] { "###", "IFI", "###", Character.valueOf('#'), plank, Character.valueOf('F'), Blocks.furnace, Character.valueOf('I'), Items.iron_ingot});
			}
		}
		
		/* Recipe book */
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.recipeBook.item, 1), new Object[] { "TTT", "TBT", "TTT", Character.valueOf('T'), Blocks.rail, Character.valueOf('B'), Items.book });

		/* Chunk Loader Activator */
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.chunkLoaderActivator.item, 1), new Object[] { "  P", " S ", "S  ", Character.valueOf('S'), Items.blaze_rod, Character.valueOf('P'), Items.ender_pearl });

		/* Assembly tables */
		TrainCraftingManager.instance.addRecipe(new ItemStack(BlockData.assemblyTableI.block, 1), new Object[] { "IPI", "S S", "SPS", Character.valueOf('I'), Items.iron_ingot, Character.valueOf('P'), Blocks.piston, Character.valueOf('S'), Blocks.stone });
		TrainCraftingManager.instance.addRecipe(new ItemStack(BlockData.assemblyTableII.block, 1), new Object[] { "GPG", "O O", "OPO", Character.valueOf('G'), Items.gold_ingot, Character.valueOf('P'), Blocks.piston, Character.valueOf('O'), Blocks.obsidian });
		TrainCraftingManager.instance.addRecipe(new ItemStack(BlockData.assemblyTableIII.block, 1), new Object[] { "GPG", "DLD", "OPO", Character.valueOf('G'), Items.gold_ingot, Character.valueOf('P'), Blocks.piston, Character.valueOf('D'), Items.diamond, Character.valueOf('L'), Blocks.glowstone, Character.valueOf('O'), Blocks.obsidian });

		/* Open Hearth Furnace */
		TrainCraftingManager.instance.addRecipe(new ItemStack(BlockData.openFurnaceIdle.block, 1), new Object[] { "#L#", "#B#", "#I#", Character.valueOf('#'), Blocks.nether_brick, Character.valueOf('L'), Items.lava_bucket, Character.valueOf('B'), Items.bucket, Character.valueOf('I'), Blocks.iron_block });

		/* Lantern */
		TrainCraftingManager.instance.addRecipe(new ItemStack(BlockData.lantern.block, 4), new Object[] { "III", "PTP", "III", Character.valueOf('I'), Items.iron_ingot, Character.valueOf('P'), Blocks.glass_pane, Character.valueOf('T'),Blocks.torch });
	
		/* Clothes */
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.overalls.item, 1), new Object[] { " # ", "X$X", "X X", Character.valueOf('X'), new ItemStack(Items.dye, 1, 4), Character.valueOf('$'), Items.leather_leggings, Character.valueOf('#'), new ItemStack(Items.dye, 1, 1) });
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.jacket.item, 1), new Object[] { "X X", "X$X", "X#X", Character.valueOf('X'), new ItemStack(Items.dye, 1, 14), Character.valueOf('$'), Items.leather_chestplate, Character.valueOf('#'), Items.string });
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.hat.item, 1), new Object[] { " X ", "X$X", "#X#", Character.valueOf('X'), new ItemStack(Items.dye, 1, 4), Character.valueOf('$'), Items.leather_helmet, Character.valueOf('#'), Items.string });
		
		/* Driver Clothes*/
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.pants_driver_paintable.item, 1), new Object[] { "XXX", "XLX", "X$X", Character.valueOf('L'), Items.leather_leggings,Character.valueOf('$'), new ItemStack(Items.dye, 1, 4), Character.valueOf('X'), Items.string});
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.jacket_driver_paintable.item, 1), new Object[] { "X X", "XRX", "XPX", Character.valueOf('X'), new ItemStack(Items.dye, 1, 4), Character.valueOf('P'), Items.leather_chestplate,Character.valueOf('R'),  new ItemStack(Items.dye, 1, 1) });
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.hat_driver_paintable.item, 1), new Object[] {"#$#", "# #", Character.valueOf('$'), new ItemStack(Items.dye, 1, 4), Character.valueOf('#'), Items.string });
		
		/* Ticket Man Clothes */
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.pants_ticketMan_paintable.item, 1), new Object[] { "XXX", "XLX", "X$X", Character.valueOf('L'), Items.leather_leggings,Character.valueOf('$'), new ItemStack(Items.dye, 1, 8), Character.valueOf('X'), Items.string});
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.jacket_ticketMan_paintable.item, 1), new Object[] { "X X", "XPX", "X#X", Character.valueOf('P'), Items.leather_chestplate, Character.valueOf('#'), new ItemStack(Items.dye, 1, 4), Character.valueOf('X'), Items.string});
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.hat_ticketMan_paintable.item, 1), new Object[] {"#$#", "# #", Character.valueOf('$'), new ItemStack(Items.dye, 1, 0), Character.valueOf('#'), Items.string });
		
		
		ArrayList<ItemStack> plastics = OreDictionary.getOres("dustPlastic");
		ArrayList<ItemStack> copper = OreDictionary.getOres("ingotCopper");
		if (plastics != null && plastics.size() >= 0) {
			for (ItemStack plastic : plastics) {
				/* Empty canister */
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.emptyCanister.item, 4), new Object[] { "PPP", "P P", "PPP", Character.valueOf('P'), plastic});
				/* Electronic circuit */
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.electronicCircuit.item, 1), new Object[] { "XXX", "RPR", "XXX", Character.valueOf('X'), ItemData.copperWireFine.item, Character.valueOf('P'), plastic, Character.valueOf('R'), Items.redstone });
				/* Composite Material*/
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.reinforcedPlastic.item, 16), new Object[] { "LPL", "PLP", "GPG", Character.valueOf('G'), Blocks.glass_pane, Character.valueOf('P'), ItemData.graphite.item, Character.valueOf('L'), plastic});
				
				if (copper != null && copper.size() >= 0) {
					for (ItemStack copp : copper) {
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.copperWireFine.item, 6), new Object[] { "XXX", "XPX", "XXX", Character.valueOf('X'), copp, Character.valueOf('P'), plastic });
					}
				}	
			}
		}
		
		/* Composite Suit */
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.boots_suit_paintable.item, 1), new Object[] {" D ","X X", "XFX", Character.valueOf('F'), Items.feather, Character.valueOf('D'), Items.diamond, Character.valueOf('X'), ItemData.reinforcedPlates.item});
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.pants_suit_paintable.item, 1), new Object[] { "XDX", "X$X", "X X", Character.valueOf('$'), Items.fire_charge, Character.valueOf('X'), ItemData.reinforcedPlates.item,Character.valueOf('D'), Items.diamond});
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.jacket_suit_paintable.item, 1), new Object[] { "X X", "XDX", "XAX", Character.valueOf('A'), Items.golden_apple, Character.valueOf('X'), ItemData.reinforcedPlates.item,Character.valueOf('D'), Blocks.diamond_block});
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.helmet_suit_paintable.item, 1), new Object[] {"#D#", "# #", Character.valueOf('D'), Blocks.diamond_block, Character.valueOf('#'), ItemData.reinforcedPlates.item });

		/* Trains parts */

		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.generator.item, 1), new Object[] { " ##", "E$$", " ##", Character.valueOf('#'), ItemData.copperWireFine.item, Character.valueOf('E'), ItemData.electronicCircuit.item, Character.valueOf('$'), Items.iron_ingot });// generator
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.controls.item, 1), new Object[] { "#X#", "#E#", "$$$", Character.valueOf('#'), Blocks.lever, Character.valueOf('X'), Blocks.stone_button, Character.valueOf('$'), Items.iron_ingot, Character.valueOf('E'), ItemData.electronicCircuit.item });// train controls
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.dieselengine.item, 2), new Object[] { "###", "XXX", "CCC", Character.valueOf('#'), ItemData.piston.item, Character.valueOf('X'), ItemData.cylinder.item, Character.valueOf('C'), ItemData.camshaft.item });// diesel engine 
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.electmotor.item, 1), new Object[] { "I#I", "#E#", "I#I", Character.valueOf('#'), ItemData.copperWireFine.item, Character.valueOf('I'), Items.iron_ingot, Character.valueOf('E'), ItemData.electronicCircuit.item });// Electric motor 
		ArrayList<ItemStack> dustCoal = OreDictionary.getOres("dustCoal");
		if (dustCoal != null && dustCoal.size() >= 0) {
			for (int t = 0; t < dustCoal.size(); t++) {
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.graphite.item, 2), new Object[] { "###", "#X#", "###", Character.valueOf('#'), dustCoal.get(t), Character.valueOf('X'), Items.clay_ball });// Graphite
			}
		}
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.ironBoiler.item, 2), new Object[] { "###", "XXX", "###", Character.valueOf('#'), Items.iron_ingot, Character.valueOf('X'), Items.water_bucket });// iron Boiler 
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.ironFirebox.item, 2), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Items.iron_ingot, Character.valueOf('X'), Items.flint_and_steel });// iron Firebox  
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.ironChimney.item, 2), new Object[] { "# #", "# #", "# #", Character.valueOf('#'), Items.iron_ingot });
		
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.coaldust.item, 4), new Object[] { "###","   ","   ", Character.valueOf('#'), Items.coal });
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.coaldust.item, 4), new Object[] { "   ","###","   ", Character.valueOf('#'), Items.coal });
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.coaldust.item, 4), new Object[] { "   ","   ","###", Character.valueOf('#'), Items.coal });
		
		//TrainCraftingManager.instance.addShapelessRecipe(new ItemStack(ItemData.coaldust.item, 4), new Object[] { Items.coal, Items.coal, Items.coal, Items.coal });// coal dust
		
		//TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.signal.item, 2), new Object[] { "#", "X", "X", Character.valueOf('X'), ItemData.steel.item, Character.valueOf('#'), Items.redstone });
		/* diesel generator */
		TrainCraftingManager.instance.addRecipe(new ItemStack(BlockData.generatorDiesel.block, 1), new Object[] { "C  ", "DE ", Character.valueOf('C'), ItemData.steelchimney.item, Character.valueOf('D'), ItemData.dieselengine.item, Character.valueOf('E'), ItemData.electronicCircuit.item });
		
		/* Zepplin parts and zeppelin item */
		if (ConfigHandler.ENABLE_ZEPPELIN) {
			TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.balloon.item, 1), new Object[] { "###", "# #", "###", Character.valueOf('#'), Blocks.wool });// Balloon  
			TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.steamengine.item, 1), new Object[] { "C  ", "BF ", Character.valueOf('C'), ItemData.steelchimney.item, Character.valueOf('B'), ItemData.boiler.item, Character.valueOf('F'), ItemData.firebox.item });// Small steam engine 
			TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.airship.item, 1), new Object[] { "B B", "SES", "POP", Character.valueOf('B'), ItemData.balloon.item, Character.valueOf('S'), Items.stick, Character.valueOf('E'), ItemData.steamengine.item, Character.valueOf('P'), ItemData.propeller.item, Character.valueOf('O'), Items.boat });
			TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.zeppelin.item, 1), new Object[] { "BBB", "SES", "POP", Character.valueOf('B'), ItemData.balloon.item, Character.valueOf('S'), ItemData.propeller.item, Character.valueOf('E'), ItemData.controls.item, Character.valueOf('P'), ItemData.electmotor.item, Character.valueOf('O'), ItemData.seats.item });
		}
		
		ArrayList<ItemStack> steel = OreDictionary.getOres("ingotSteel");
		ArrayList<ItemStack> s1 = OreDictionary.getOres("plankWood");
		ArrayList<ItemStack> s2 = OreDictionary.getOres("logWood");
		if (s1 != null && s1.size() >= 0) {
			for (int i = 0; i < s1.size(); i++) {
				if (steel != null && steel.size() >= 0) {
					for (int t = 0; t < steel.size(); t++) {
						
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.reinforcedPlates.item, 1), new Object[] { "RRR", "SSS", "CCC", Character.valueOf('R'), ItemData.reinforcedPlastic.item, Character.valueOf('S'), steel.get(t), Character.valueOf('C'), Items.clay_ball});
						
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.composite_wrench.item, 1), new Object[] {"S S", " R "," R ", Character.valueOf('R'), ItemData.reinforcedPlastic.item, Character.valueOf('S'),steel.get(t) });
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.steelcab.item, 2), new Object[] { "###", "X X", "XXX", Character.valueOf('X'), steel.get(t), Character.valueOf('#'), s1.get(i) });// Steel cab
						TrainCraftingManager.instance.addRecipe(new ItemStack(BlockData.distilIdle.block, 1), new Object[] { "###", "#F#", "###", Character.valueOf('#'), steel.get(t), Character.valueOf('F'), ItemData.firebox.item });
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.transformer.item, 1), new Object[] { "# #", "XEX", "###", Character.valueOf('#'), steel.get(t), Character.valueOf('E'), ItemData.electronicCircuit.item, Character.valueOf('X'), Items.redstone });// transformer
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.minecartLocoMineTrain.item, 1), new Object[] { "X#X", "XXX", Character.valueOf('X'), steel.get(t), Character.valueOf('#'), ItemData.electmotor.item });
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.minecartLocoMineTrain.item, 1), new Object[] { "   ", "X#X", "XXX", Character.valueOf('X'), steel.get(t), Character.valueOf('#'), ItemData.electmotor.item });
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.minecartMineTrain.item, 1), new Object[] { "X#X", "XXX", Character.valueOf('X'), steel.get(t), Character.valueOf('#'), Blocks.chest });
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.minecartMineTrain.item, 1), new Object[] { "   ", "X#X", "XXX", Character.valueOf('X'), steel.get(t), Character.valueOf('#'), Blocks.chest });

						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.boiler.item, 2), new Object[] { "###", "XXX", "###", Character.valueOf('#'), steel.get(t), Character.valueOf('X'), Items.water_bucket });// Boiler 
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.firebox.item, 2), new Object[] { "###", "#X#", "###", Character.valueOf('#'), steel.get(t), Character.valueOf('X'), Items.flint_and_steel });// Firebox 
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.bogie.item, 4), new Object[] { " # ", "#X#", " # ", Character.valueOf('#'), steel.get(t), Character.valueOf('X'), Items.iron_ingot });// Bogie 
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.steelframe.item, 2), new Object[] { "# #", "AAA", Character.valueOf('A'), steel.get(t), Character.valueOf('#'), Items.iron_ingot });// Steel Frame 
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.steelframe.item, 2), new Object[] { "   ", "# #", "AAA", Character.valueOf('A'), steel.get(t), Character.valueOf('#'), Items.iron_ingot });// Steel Frame  
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.steelchimney.item, 2), new Object[] { "# #", "# #", "# #", Character.valueOf('#'), steel.get(t) });// Bogie 
						TrainCraftingManager.instance.addRecipe(new ItemStack(Items.flint_and_steel, 2), new Object[] { "* ", " #", Character.valueOf('*'), steel.get(t), Character.valueOf('#'), Items.flint });

						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.stake.item, 1), new Object[] { "   ", "IFI", "   ", Character.valueOf('I'), steel.get(t), Character.valueOf('F'), Items.iron_ingot });
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.stake.item, 1), new Object[] { "IFI", "   ", "   ", Character.valueOf('I'), steel.get(t), Character.valueOf('F'), Items.iron_ingot });
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.stake.item, 1), new Object[] { "   ", "   ", "IFI", Character.valueOf('I'), steel.get(t), Character.valueOf('F'), Items.iron_ingot });

						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.transmition.item, 1), new Object[] { " # ", "#X#", " # ", Character.valueOf('#'), steel.get(t), Character.valueOf('X'), ItemData.diesel.item });// transmition
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.piston.item, 3), new Object[] { " # ", " X ", Character.valueOf('#'), steel.get(t), Character.valueOf('X'), Items.stick });// piston
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.camshaft.item, 3), new Object[] { "###", "   ", "   ", Character.valueOf('#'), steel.get(t) });// camshaft 
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.camshaft.item, 3), new Object[] { "   ", "###", "   ", Character.valueOf('#'), steel.get(t) });// camshaft
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.camshaft.item, 3), new Object[] { "   ", "   ", "###", Character.valueOf('#'), steel.get(t) });// camshaft
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.cylinder.item, 3), new Object[] { "# #", "# #", "###", Character.valueOf('#'), steel.get(t) });// cylinder 
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.propeller.item, 2), new Object[] { " # ", "#X#", " # ", Character.valueOf('#'), s1.get(i), Character.valueOf('X'), Items.iron_ingot });// Propeller
						
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailSmallStraight.item, 32), new Object[] { "I I", "SPS", "I I", Character.valueOf('P'), s1.get(i), Character.valueOf('I'), Items.iron_ingot, Character.valueOf('S'), steel.get(t) });// small straight track
					}
				}
				if (s2 != null && s2.size() >= 0) {
					for (int j = 0; j < s2.size(); j++) {
						/* Water Wheel */
						TrainCraftingManager.instance.addRecipe(new ItemStack(BlockData.waterWheel.block, 1), new Object[] { " P ", "PGP", " P ", Character.valueOf('P'), s2.get(j),Character.valueOf('G'), ItemData.generator.item});
						/* Wind mill */
						TrainCraftingManager.instance.addRecipe(new ItemStack(BlockData.windMill.block, 1), new Object[] { " R ", " G ", "B B",Character.valueOf('G'), ItemData.generator.item, Character.valueOf('B'), Items.iron_ingot, Character.valueOf('R'), ItemData.propeller.item});
						
						TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.woodenBogie.item, 4), new Object[] { " # ", "#X#", " # ", Character.valueOf('#'), s1.get(i), Character.valueOf('X'), s2.get(j) });// wooden Bogie
					}
				}
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.seats.item, 1), new Object[] { "#  ", "## ", "XXX", Character.valueOf('#'), s1.get(i), Character.valueOf('X'), Items.iron_ingot });// transformer
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.woodenFrame.item, 2), new Object[] { "# #", "AAA", Character.valueOf('A'), s1.get(i), Character.valueOf('#'), s1.get(i) });// wooden Frame
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.woodenFrame.item, 2), new Object[] { "   ", "# #", "AAA", Character.valueOf('A'), s1.get(i), Character.valueOf('#'), s1.get(i) });// wooden Frame
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.woodenCab.item, 2), new Object[] { "###", "X X", "XXX", Character.valueOf('X'), s1.get(i), Character.valueOf('#'), s1.get(i) });// wooden cab
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.ironBogie.item, 4), new Object[] { " # ", "#X#", " # ", Character.valueOf('#'), Items.iron_ingot, Character.valueOf('X'), s1.get(i) });// iron Bogie
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.ironFrame.item, 2), new Object[] { "# #", "AAA", Character.valueOf('A'), Items.iron_ingot, Character.valueOf('#'), s1.get(i) });// iron Frame
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.ironFrame.item, 2), new Object[] { "   ", "# #", "AAA", Character.valueOf('A'), Items.iron_ingot, Character.valueOf('#'), s1.get(i) });// iron Frame
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.ironCab.item, 2), new Object[] { "###", "X X", "XXX", Character.valueOf('X'), Items.iron_ingot, Character.valueOf('#'), s1.get(i) });// iron cab
				TrainCraftingManager.instance.addRecipe(new ItemStack(BlockData.stopper.block, 1), new Object[] { "WWW", "I I", "RRR", Character.valueOf('W'), s1.get(i), Character.valueOf('R'), Blocks.rail, Character.valueOf('I'), Items.iron_ingot});// stopper
				
				TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailLargeSlopeWood.item, 2), new Object[] {"  S", " SW","SWW", Character.valueOf('S'), ItemData.tcRailSmallStraight.item,Character.valueOf('W'), s1.get(i) });// straight slopes
			}
		}
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailMediumTurn.item, 2), new Object[] {"SS ", "S  ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// medium turn
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailMediumTurn.item, 2), new Object[] {"   ","SS ", "S  ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// medium turn
		
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailLargeTurn.item, 2), new Object[] {"SSS","SS ", "S  ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// large turn
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailVeryLargeTurn.item, 2), new Object[] {" L ","L  ","   ", Character.valueOf('L'), ItemData.tcRailLargeTurn.item });// very large turn
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailVeryLargeTurn.item, 2), new Object[] {"   "," L ","L  ", Character.valueOf('L'), ItemData.tcRailLargeTurn.item });// very large turn
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailMediumStraight.item, 2), new Object[] {"S  ", "S  ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// medium straight
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailMediumStraight.item, 2), new Object[] {" S ", " S ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// medium straight
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailMediumStraight.item, 2), new Object[] {"  S", "  S", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// medium straight
		
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailMediumStraight.item, 2), new Object[] {"   ","S  ", "S  ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// medium straight
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailMediumStraight.item, 2), new Object[] {"   "," S ", " S ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// medium straight
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailMediumStraight.item, 2), new Object[] {"   ","  S", "  S", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// medium straight
		
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailLongStraight.item, 3), new Object[] {" S ", " S "," S ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// long straight
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailLongStraight.item, 3), new Object[] {"S  ", "S  ","S  ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// long straight
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailLongStraight.item, 3), new Object[] {"  S", "  S","  S", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// long straight
		
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailMediumSwitch.item, 2), new Object[] {"S  ", "SRS", "S  ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item, Character.valueOf('R'), ItemData.tcRailMediumTurn.item });// switch
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailLargeSwitch.item, 2), new Object[] {"S  ", "SRS", "S  ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item, Character.valueOf('R'), ItemData.tcRailLargeTurn.item });// switch
		
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailMediumParallelSwitch.item, 2), new Object[] {"S S", "SRR", "SR ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item, Character.valueOf('R'), ItemData.tcRailMediumTurn.item });// switch
		
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailTwoWaysCrossing.item, 4), new Object[] {" S ", "SSS"," S ", Character.valueOf('S'), ItemData.tcRailSmallStraight.item });// two ways crossing
		
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailLargeSlopeGravel.item, 2), new Object[] {"  S", " SG","SGG", Character.valueOf('S'), ItemData.tcRailSmallStraight.item,Character.valueOf('G'), Blocks.gravel });// straight slopes
		TrainCraftingManager.instance.addRecipe(new ItemStack(ItemData.tcRailLargeSlopeBallast.item, 2), new Object[] {"  S", " SB","SBB", Character.valueOf('S'), ItemData.tcRailSmallStraight.item,Character.valueOf('B'), new ItemStack(BlockData.oreTC.block, 1,3) });// straight slopes
		
		TrainCraftingManager.instance.addRecipe(new ItemStack(BlockData.bridgePillar.block, 4), new Object[] {"SSS", "S S","SSS", Character.valueOf('S'), Items.stick});// bridge pillar
		
		/* Smelting recipes */
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(BlockData.oreTC.block, 1, 0), OreDictionary.getOres("ingotCopper").get(0), 0.7f);
	}

	public static void addDictRecipe(ItemStack stack, Object... obj) {
		ShapedOreRecipe recipe = new ShapedOreRecipe(stack, obj);
		GameRegistry.addRecipe(recipe);
	}
}