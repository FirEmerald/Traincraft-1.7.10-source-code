/*******************************************************************************
 * Copyright (c) 2012 Mrbrutal. All rights reserved.
 * 
 * @name TrainCraft
 * @author Mrbrutal
 ******************************************************************************/

package src.train.common.items;

import cpw.mods.fml.common.registry.GameRegistry;
import src.train.common.Traincraft;
import src.train.common.library.Info;
import src.train.common.library.ItemData;

public class TCItems {

	public static void init() {
		loadItems();
		nameItems();
	}

	private static void loadItems() {
		for (ItemData items : ItemData.values()) {
			if (items.className != null) {
				if (items.className.equals("ItemTrain")) {
					items.item = new ItemTrain();
				}
				else if (items.className.equals("ItemRollingStock")) {
					items.item = new ItemRollingStock(items.iconName);
				}
				else if (items.className.equals("ItemRotativeDigger")) {
					items.item = new ItemRotativeDigger();
				}
				else if (items.className.equals("ItemContainer")) {
					items.item = new ItemContainer();
				}
			}
		}
		//ItemData.signal.item = new ItemSignal(ItemData.signal.itemID, BlockIDs.activeSignal.block).setIconIndex(ItemData.signal.iconIndex);
		ItemData.chunkLoaderActivator.item = new ItemChunkLoaderActivator();
		ItemData.recipeBook.item = new ItemRecipeBook();
		
		ItemData.stake.item = new ItemStacked(200);
		ItemData.airship.item = new ItemZeppelins(0);
		ItemData.zeppelin.item = new ItemZeppelins(1);
		ItemData.overalls.item = new ItemTCArmor(Traincraft.instance.armor, Traincraft.trainArmor, 2,0);
		ItemData.jacket.item = new ItemTCArmor(Traincraft.instance.armor, Traincraft.trainArmor, 1,0);
		ItemData.hat.item = new ItemTCArmor(Traincraft.instance.armor, Traincraft.trainArmor, 0,0);
		
		/**Paintable armors:*/
		ItemData.pants_ticketMan_paintable.item = new ItemTCArmor(Traincraft.instance.armorCloth, Traincraft.trainCloth, 2,0xdedede);
		ItemData.jacket_ticketMan_paintable.item = new ItemTCArmor(Traincraft.instance.armorCloth, Traincraft.trainCloth, 1,0x002cdb);
		ItemData.hat_ticketMan_paintable.item = new ItemTCArmor(Traincraft.instance.armorCloth, Traincraft.trainCloth, 0,0x9fafb5);
		
		ItemData.pants_driver_paintable.item = new ItemTCArmor(Traincraft.instance.armorCloth, Traincraft.trainCloth, 2,0x1535d4);
		ItemData.jacket_driver_paintable.item = new ItemTCArmor(Traincraft.instance.armorCloth, Traincraft.trainCloth, 1,0x1469d9);
		ItemData.hat_driver_paintable.item = new ItemTCArmor(Traincraft.instance.armorCloth, Traincraft.trainCloth, 0,0x1469d9);
	
		ItemData.boots_suit_paintable.item = new ItemTCCompositeSuit(Traincraft.instance.armorCompositeSuit, Traincraft.trainCompositeSuit, 3,0x1535d4);
		ItemData.pants_suit_paintable.item = new ItemTCCompositeSuit(Traincraft.instance.armorCompositeSuit, Traincraft.trainCompositeSuit, 2,0x1535d4);
		ItemData.jacket_suit_paintable.item = new ItemTCCompositeSuit(Traincraft.instance.armorCompositeSuit, Traincraft.trainCompositeSuit, 1,0x1469d9);
		ItemData.helmet_suit_paintable.item = new ItemTCCompositeSuit(Traincraft.instance.armorCompositeSuit, Traincraft.trainCompositeSuit, 0,0x1469d9);
		
		ItemData.composite_wrench.item = new ItemWrench();
		
		/**Tracks*/
		ItemData.tcRailMediumTurn.item = new ItemTCRail(ItemTCRail.TrackTypes.MEDIUM_TURN);
		ItemData.tcRailLargeTurn.item = new ItemTCRail(ItemTCRail.TrackTypes.LARGE_TURN);
		ItemData.tcRailVeryLargeTurn.item = new ItemTCRail(ItemTCRail.TrackTypes.VERY_LARGE_TURN);
		ItemData.tcRailLongStraight.item = new ItemTCRail(ItemTCRail.TrackTypes.LONG_STRAIGHT);
		ItemData.tcRailMediumStraight.item = new ItemTCRail(ItemTCRail.TrackTypes.MEDIUM_STRAIGHT);
		ItemData.tcRailSmallStraight.item = new ItemTCRail(ItemTCRail.TrackTypes.SMALL_STRAIGHT);
		ItemData.tcRailMediumSwitch.item = new ItemTCRail(ItemTCRail.TrackTypes.MEDIUM_SWITCH);
		ItemData.tcRailLargeSwitch.item = new ItemTCRail(ItemTCRail.TrackTypes.LARGE_SWITCH);
		ItemData.tcRailMediumParallelSwitch.item = new ItemTCRail(ItemTCRail.TrackTypes.MEDIUM_PARALLEL_SWITCH);
		
		ItemData.tcRailTwoWaysCrossing.item = new ItemTCRail(ItemTCRail.TrackTypes.TWO_WAYS_CROSSING);
		ItemData.tcRailLargeSlopeWood.item = new ItemTCRail(ItemTCRail.TrackTypes.LARGE_SLOPE_WOOD);
		ItemData.tcRailLargeSlopeGravel.item = new ItemTCRail(ItemTCRail.TrackTypes.LARGE_SLOPE_GRAVEL);
		ItemData.tcRailLargeSlopeBallast.item = new ItemTCRail(ItemTCRail.TrackTypes.LARGE_SLOPE_BALLAST);
		
	}

	private static void nameItems() {
		for (ItemData items : ItemData.values()) {
			if (items.item != null) {
				items.item.setUnlocalizedName(items.name());
				GameRegistry.registerItem(items.item, Info.modID.toLowerCase() + ":" + items.name());
			}
		}
	}
}