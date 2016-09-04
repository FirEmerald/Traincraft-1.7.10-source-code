/*******************************************************************************
 * Copyright (c) 2012 Mrbrutal & DV8FromTheWorld. All rights reserved.
 * 
 * @name TrainCraft
 * @author Mrbrutal & DV8FromTheWorld
 ******************************************************************************/

package src.train.common.core.handlers;

import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import src.train.common.library.AchievementIDs;
import src.train.common.library.BlockData;
import src.train.common.library.ItemData;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AchievementHandler {

	public static AchievementPage tmPage;

	public static void load() {
		AchievementIDs.trainWB.achievement = new Achievement("achievement.tc.trainWB", "trainWB", 0, 0, BlockData.trainWorkbench.block, (Achievement) null).registerStat();
		AchievementIDs.woodenParts.achievement = new Achievement("achievement.tc.woodenParts", "woodenParts", -2, 2, ItemData.woodenBogie.item, AchievementIDs.trainWB.achievement).registerStat();
		AchievementIDs.ironParts.achievement = new Achievement("achievement.tc.ironParts", "ironParts", 0, 2, ItemData.ironBogie.item, AchievementIDs.trainWB.achievement).registerStat();
		AchievementIDs.steelParts.achievement = new Achievement("achievement.tc.steelParts", "steelParts", 2, 2, ItemData.bogie.item, AchievementIDs.trainWB.achievement).registerStat();
		AchievementIDs.firebox.achievement = new Achievement("achievement.tc.firebox", "firebox", 0, 4, ItemData.firebox.item, AchievementIDs.ironParts.achievement).registerStat();
		AchievementIDs.zeppelin.achievement = new Achievement("achievement.tc.zeppelin", "zeppelin", 2, 4, ItemData.airship.item, AchievementIDs.firebox.achievement).registerStat();
		AchievementIDs.smallSteam.achievement = new Achievement("achievement.tc.smallSteam", "smallSteam", -2, 6, ItemData.minecartLoco3.item, AchievementIDs.firebox.achievement).registerStat();
		AchievementIDs.normalSteam.achievement = new Achievement("achievement.tc.normalSteam", "normalSteam", 0, 6, ItemData.minecartPower.item, AchievementIDs.firebox.achievement).registerStat();
		//AchievementIDs.heavySteam.achievement = new Achievement(20109, "heavySteam", 2, 6, ItemData.minecartHeavySteam.item, AchievementIDs.firebox.achievement).registerStat();
		//TODO put it back once Heavy Steam is back

		AchievementIDs.openHearth.achievement = new Achievement("achievement.tc.openHearth", "openHearth", -4, 0, BlockData.openFurnaceActive.block, (Achievement) null).registerStat();
		AchievementIDs.steel.achievement = new Achievement("achievement.tc.steel", "steel", -4, 2, ItemData.steel.item, AchievementIDs.openHearth.achievement).registerStat();
		AchievementIDs.stake.achievement = new Achievement("achievement.tc.stake", "stake", -4, 4, ItemData.stake.item, AchievementIDs.steel.achievement).registerStat();
		AchievementIDs.dieselEngine.achievement = new Achievement("achievement.tc.dieselEngine", "dieselEngine", -6, 4, ItemData.dieselengine.item, AchievementIDs.steel.achievement).registerStat();
		AchievementIDs.diesel.achievement = new Achievement("achievement.tc.dieselLoco", "dieselLoco", -6, 6, ItemData.minecartCD742.item, AchievementIDs.dieselEngine.achievement).registerStat();

		AchievementIDs.distilationTower.achievement = new Achievement("achievement.tc.distilationTower", "distilationTower", -10, 0, BlockData.distilActive.block, (Achievement) null).registerStat();
		AchievementIDs.dieselFuel.achievement = new Achievement("achievement.tc.dieselFuel", "dieselFuel", -8, 2, ItemData.diesel.item, AchievementIDs.distilationTower.achievement).registerStat();
		AchievementIDs.plastic.achievement = new Achievement("achievement.tc.plastic", "plastic", -12, 2, ItemData.rawPlastic.item, AchievementIDs.distilationTower.achievement).registerStat();
		AchievementIDs.fineCopperWire.achievement = new Achievement("achievement.tc.fineCopperWire", "fineCopperWire", -12, 4, ItemData.copperWireFine.item, AchievementIDs.plastic.achievement).registerStat();
		AchievementIDs.electronicCircuit.achievement = new Achievement("achievement.tc.electronicCircuit", "electronicCircuit", -12, 6, ItemData.electronicCircuit.item, AchievementIDs.fineCopperWire.achievement).registerStat();
		AchievementIDs.generator.achievement = new Achievement("achievement.tc.generator", "generator", -14, 8, ItemData.generator.item, AchievementIDs.electronicCircuit.achievement).registerStat();
		AchievementIDs.electMotor.achievement = new Achievement("achievement.tc.electMotor", "electMotor", -10, 8, ItemData.electmotor.item, AchievementIDs.electronicCircuit.achievement).registerStat();
		AchievementIDs.tram.achievement = new Achievement("achievement.tc.tram", "tram", -12, 10, ItemData.minecartTramWood.item, AchievementIDs.electMotor.achievement).registerStat();
		AchievementIDs.fast.achievement = new Achievement("achievement.tc.fast", "fast", -8, 10, ItemData.minecartVL10.item, AchievementIDs.electMotor.achievement).registerStat();

		AchievementIDs.engineer.achievement = new Achievement("achievement.tc.engineer", "engineer", -2, -2, ItemData.overalls.item, (Achievement) null).registerStat();

		AchievementIDs.assemblyTable.achievement = new Achievement("achievement.tc.assemblyTable", "assemblyTable", 6, 0, BlockData.assemblyTableI.block, (Achievement) null).registerStat();
		AchievementIDs.passenger.achievement = new Achievement("achievement.tc.passenger", "passenger", 5, 2, ItemData.minecartPassenger2.item, AchievementIDs.assemblyTable.achievement).registerStat();
		AchievementIDs.freight.achievement = new Achievement("achievement.tc.freight", "freight", 7, 2, ItemData.minecartChest.item, AchievementIDs.assemblyTable.achievement).registerStat();
		AchievementIDs.liquid.achievement = new Achievement("achievement.tc.liquid", "liquid", 5, 4, ItemData.minecartWatertransp.item, AchievementIDs.assemblyTable.achievement).registerStat();
		AchievementIDs.tender.achievement = new Achievement("achievement.tc.tender", "tender", 7, 4, ItemData.minecartTender.item, AchievementIDs.assemblyTable.achievement).registerStat();
		AchievementIDs.workCart.achievement = new Achievement("achievement.tc.workCart", "workCart", 5, 6, ItemData.minecartWork.item, AchievementIDs.assemblyTable.achievement).registerStat().setSpecial();
		AchievementIDs.builder.achievement = new Achievement("achievement.tc.builder", "builder", 7, 6, ItemData.minecartBuilder.item, AchievementIDs.assemblyTable.achievement).registerStat();
		AchievementIDs.jukebox.achievement = new Achievement("achievement.tc.jukebox", "jukebox", 5, 8, ItemData.minecartJukeBoxCart.item, AchievementIDs.assemblyTable.achievement).registerStat().setSpecial();
		AchievementIDs.minetrain.achievement = new Achievement("achievement.tc.minetrain", "minetrain", 7, 8, ItemData.minecartMineTrain.item, AchievementIDs.assemblyTable.achievement).registerStat();
		AchievementIDs.cherepanov.achievement = new Achievement("achievement.tc.cherepanov", "cherepanov", 5, 10, ItemData.minecartLocoCherepanov.item, AchievementIDs.assemblyTable.achievement).registerStat();
		AchievementIDs.mail.achievement = new Achievement("achievement.tc.mail", "mail", 7, 10, ItemData.minecartMailWagon_DB.item, AchievementIDs.assemblyTable.achievement).registerStat();
		AchievementIDs.stockCar.achievement = new Achievement("achievement.tc.stockcar", "stockcar", 5, 12, ItemData.minecartStockCar.item, AchievementIDs.assemblyTable.achievement).registerStat();
		AchievementIDs.caboose.achievement = new Achievement("achievement.tc.caboose", "caboose", 7, 12, ItemData.minecartCaboose.item, AchievementIDs.assemblyTable.achievement).registerStat();
		AchievementIDs.flatCart.achievement = new Achievement("achievement.tc.flatcart", "flatcart", 5, 14, ItemData.minecartFlatCartRail_DB.item, AchievementIDs.assemblyTable.achievement).registerStat();
		//TODO put this: AchievementIDs.heavySteam.achievement  back once Heavy Steam is back
		Achievement ach[] = new Achievement[] { AchievementIDs.steel.achievement, AchievementIDs.stake.achievement, AchievementIDs.dieselFuel.achievement, AchievementIDs.electMotor.achievement, AchievementIDs.dieselEngine.achievement, AchievementIDs.firebox.achievement, AchievementIDs.zeppelin.achievement, AchievementIDs.smallSteam.achievement, AchievementIDs.normalSteam.achievement, AchievementIDs.passenger.achievement, AchievementIDs.freight.achievement, AchievementIDs.liquid.achievement, AchievementIDs.tender.achievement, AchievementIDs.diesel.achievement, AchievementIDs.tram.achievement, AchievementIDs.fast.achievement, AchievementIDs.workCart.achievement, AchievementIDs.builder.achievement, AchievementIDs.jukebox.achievement, AchievementIDs.openHearth.achievement, AchievementIDs.engineer.achievement, AchievementIDs.distilationTower.achievement, AchievementIDs.plastic.achievement, AchievementIDs.fineCopperWire.achievement, AchievementIDs.electronicCircuit.achievement, AchievementIDs.generator.achievement, AchievementIDs.woodenParts.achievement, AchievementIDs.ironParts.achievement, AchievementIDs.steelParts.achievement, AchievementIDs.trainWB.achievement, AchievementIDs.assemblyTable.achievement, AchievementIDs.mail.achievement, AchievementIDs.minetrain.achievement, AchievementIDs.cherepanov.achievement, AchievementIDs.flatCart.achievement, AchievementIDs.stockCar.achievement, AchievementIDs.caboose.achievement };

		tmPage = new AchievementPage("TrainCraft", ach);
	}
}