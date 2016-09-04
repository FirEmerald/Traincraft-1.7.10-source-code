package src.train.common.library;

import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;

public enum AchievementIDs {
	steel(new Item[] { ItemData.steel.item }),
	stake(new Item[] { ItemData.stake.item }),
	dieselFuel(new Item[] { ItemData.diesel.item }),
	electMotor(new Item[] { ItemData.electmotor.item }),
	dieselEngine(new Item[] { ItemData.dieselengine.item }),
	firebox(new Item[] { ItemData.firebox.item, ItemData.ironFirebox.item }),
	zeppelin(new Item[] { ItemData.zeppelin.item,ItemData.airship.item }),
	smallSteam(new Item[] { ItemData.minecartLoco3.item }),
	normalSteam(new Item[] { ItemData.minecartPower.item, ItemData.minecartLocoBR01_DB.item, ItemData.minecartLocoBR80_DB.item, ItemData.minecartLocoEr.item, ItemData.minecartLocoForneyRed.item, ItemData.minecartLocomogulBlue.item, }),
	passenger(new Item[] { ItemData.minecartPassenger5.item, ItemData.minecartPassengerBlue.item, ItemData.minecartPassenger2.item, ItemData.minecartCaboose.item, ItemData.minecartCaboose3.item, ItemData.minecartPassenger7.item, ItemData.minecartPassenger8_1class_DB.item, ItemData.minecartPassenger9_2class_DB.item }),
	freight(new Item[] { ItemData.minecartChest.item, ItemData.minecartGrain.item, ItemData.minecartWood.item, ItemData.minecartFreightCart2.item, ItemData.minecartOpenWagon.item, ItemData.minecartBoxCartUS.item, ItemData.minecartFreightCartSmall.item, ItemData.minecartFreightCartUS.item, ItemData.minecartFreightCenterBeam_Empty.item, ItemData.minecartFreightCenterBeam_Wood_1.item, ItemData.minecartFreightCenterBeam_Wood_2.item, ItemData.minecartFreightClosed.item, ItemData.minecartFreightClosed.item, ItemData.minecartFreightGondola_DB.item, ItemData.minecartFreightHopperUS.item, ItemData.minecartFreightOpen2.item, ItemData.minecartFreightTrailer.item, ItemData.minecartFreightWagon_DB.item, ItemData.minecartFreightWellcar.item, ItemData.minecartFreightWood2.item, ItemData.minecartOpenWagon.item }),
	liquid(new Item[] { ItemData.minecartWatertransp.item, ItemData.minecartTankWagon.item, ItemData.minecartTankWagon2.item, ItemData.minecartTankWagon_DB.item, ItemData.minecartTankWagonUS.item }),
	tender(new Item[] { ItemData.minecartTender.item, ItemData.minecartTenderBR01_DB.item, ItemData.minecartTenderEr.item, ItemData.minecartSteamRedTender.item }),
	diesel(new Item[] { ItemData.minecartGP7Red.item, ItemData.minecartShunter.item, ItemData.minecartChmE3.item, ItemData.minecartCD742.item, ItemData.minecartKof_DB.item, ItemData.minecartLocoSD70.item, ItemData.minecartV60_DB.item }),
	tram(new Item[] { ItemData.minecartTramWood.item, ItemData.minecartTramWood.item }),
	fast(new Item[] { ItemData.minecartVL10.item, ItemData.minecartBR_E69.item }),
	//heavySteam(new Item[] { ItemData.minecartHeavySteam }),//TODO put it back once Heavy Steam is back
	workCart(new Item[] { ItemData.minecartWork.item, ItemData.minecartCabooseWork.item }),
	builder(new Item[] { ItemData.minecartBuilder.item }),
	jukebox(new Item[] { ItemData.minecartJukeBoxCart.item }),

	openHearth(new Item[] { Item.getItemFromBlock(BlockData.openFurnaceIdle.block) }),
	trainWB(new Item[] { Item.getItemFromBlock(BlockData.trainWorkbench.block) }),
	assemblyTable(new Item[] { Item.getItemFromBlock(BlockData.assemblyTableI.block), Item.getItemFromBlock(BlockData.assemblyTableII.block), Item.getItemFromBlock(BlockData.assemblyTableIII.block) }),
	engineer(new Item[] { ItemData.overalls.item, ItemData.hat.item, ItemData.jacket.item }),
	woodenParts(new Item[] { ItemData.woodenBogie.item, ItemData.woodenCab.item, ItemData.woodenFrame.item, ItemData.seats.item }),
	ironParts(new Item[] { ItemData.ironBogie.item, ItemData.ironBoiler.item, ItemData.ironCab.item, ItemData.ironChimney.item, ItemData.ironFirebox.item, ItemData.ironFrame.item }),
	steelParts(new Item[] { ItemData.bogie.item, ItemData.steelcab.item, ItemData.steelchimney.item, ItemData.steelframe.item, ItemData.boiler.item, ItemData.firebox.item }),
	plastic(new Item[] { ItemData.rawPlastic.item }),
	fineCopperWire(new Item[] { ItemData.copperWireFine.item }),
	electronicCircuit(new Item[] { ItemData.electronicCircuit.item }),
	generator(new Item[] { ItemData.generator.item }),
	cherepanov(new Item[] { ItemData.minecartLocoCherepanov.item }),
	minetrain(new Item[] { ItemData.minecartLocoMineTrain.item }),
	flatCart(new Item[] { ItemData.minecartFlatCart.item, ItemData.minecartFlatCart_DB.item, ItemData.minecartFlatCartLogs_DB.item, ItemData.minecartFlatCartRail_DB.item, ItemData.minecartFlatCartSU.item, ItemData.minecartFlatCartUS.item, ItemData.minecartFlatCartWoodUS.item }),
	mail(new Item[] { ItemData.minecartMailWagon_DB.item }),
	caboose(new Item[] { ItemData.minecartCaboose.item, ItemData.minecartCaboose3.item, ItemData.minecartCabooseWork.item }),
	stockCar(new Item[] { ItemData.minecartStockCar.item }),
	distilationTower(new Item[] { Item.getItemFromBlock(BlockData.distilIdle.block) });

	public Achievement achievement;
	protected Item[] items;

	public Item[] getItems() {
		return items;
	}

	AchievementIDs(Item[] items) {
		this.items = items;
	}
}