/*******************************************************************************
 * Copyright (c) 2012 Mrbrutal. All rights reserved.
 * 
 * @name TrainCraft
 * @author Mrbrutal
 ******************************************************************************/

package src.train.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import src.train.common.items.ItemBlockGeneratorDiesel;
import src.train.common.items.ItemBlockGeneratorWaterWheel;
import src.train.common.items.ItemBlockGeneratorWindMill;
import src.train.common.items.ItemBlockOreTC;
import src.train.common.library.BlockData;
import cpw.mods.fml.common.registry.GameRegistry;

public class TCBlocks {

	public static void init() {
		loadBlocks();
		registerBlocks();
		nameBlocks();
		setHarvestLevels();
	}

	public static void loadBlocks() {
		BlockData.distilIdle.block = new BlockDistil(false).setHardness(3.5F).setStepSound(Block.soundTypeStone);
		BlockData.distilActive.block = new BlockDistil(true).setHardness(3.5F).setStepSound(Block.soundTypeStone).setLightLevel(0.8F);
		//BlockData.signal.block = new BlockSignal().setHardness(1.7F).setStepSound(Block.soundTypeMetal);

		BlockData.assemblyTableI.block = new BlockAssemblyTableI(Material.wood).setHardness(3.5F).setStepSound(Block.soundTypeWood);
		BlockData.assemblyTableII.block = new BlockAssemblyTableII(Material.rock).setHardness(3.5F).setStepSound(Block.soundTypeWood);
		BlockData.assemblyTableIII.block = new BlockAssemblyTableIII(Material.rock).setHardness(3.5F).setStepSound(Block.soundTypeWood);

		BlockData.trainWorkbench.block = new BlockTrainWorkbench().setHardness(1.7F).setStepSound(Block.soundTypeWood);
		BlockData.stopper.block = new BlockStopper().setHardness(1.7F).setStepSound(Block.soundTypeWood);

		BlockData.openFurnaceIdle.block = new BlockOpenHearthFurnace(false).setHardness(3.5F).setStepSound(Block.soundTypeStone);
		BlockData.openFurnaceActive.block = new BlockOpenHearthFurnace(true).setHardness(3.5F).setStepSound(Block.soundTypeStone);
		BlockData.oreTC.block = new BlockOreTC().setHardness(3.0F).setResistance(5F).setStepSound(Block.soundTypeStone);

		BlockData.lantern.block = new BlockLantern().setHardness(1.7F).setStepSound(Block.soundTypeMetal).setLightLevel(0.98F);
		BlockData.waterWheel.block = new BlockWaterWheel().setHardness(1.7F).setStepSound(Block.soundTypeWood);
		BlockData.windMill.block = new BlockWindMill().setHardness(1.7F).setStepSound(Block.soundTypeWood);
		BlockData.generatorDiesel.block = new BlockGeneratorDiesel().setHardness(1.7F).setStepSound(Block.soundTypeMetal);
		
		BlockData.tcRail.block = new BlockTCRail().setHardness(1.0F).setStepSound(Block.soundTypeMetal).setCreativeTab(null);
		BlockData.tcRailGag.block = new BlockTCRailGag().setHardness(1.0F).setStepSound(Block.soundTypeMetal).setCreativeTab(null);
		
		BlockData.bridgePillar.block = new BlockBridgePillar().setHardness(3.5F).setStepSound(Block.soundTypeWood);

		//BlockData.book.block = new BlockBook(BlockData.book.blockID);
	}

	public static void registerBlocks()
	{
		for (BlockData data : BlockData.values())
		{
			if (data.block != null)
			{
				if (data.hasItemBlock) GameRegistry.registerBlock(data.block, data.itemBlockClass, data.name());
				else GameRegistry.registerBlock(data.block, data.name());
			}
		}
	}

	public static void nameBlocks()
	{
		for (BlockData data : BlockData.values())
		{
			if (data.block != null) data.block.setBlockName(data.name());
		}
	}

	public static void setHarvestLevels()
	{
		BlockData.trainWorkbench.block.setHarvestLevel("axe", 0);
		BlockData.assemblyTableI.block.setHarvestLevel("axe", 0);
		BlockData.assemblyTableII.block.setHarvestLevel("axe", 0);
		BlockData.assemblyTableIII.block.setHarvestLevel("axe", 0);
		BlockData.waterWheel.block.setHarvestLevel("axe", 0);
		BlockData.windMill.block.setHarvestLevel("axe", 0);
		BlockData.bridgePillar.block.setHarvestLevel("axe", 0);

		Blocks.rail.setHarvestLevel("ItemStacked", 0);
		Blocks.detector_rail.setHarvestLevel("ItemStacked", 0);
		Blocks.golden_rail.setHarvestLevel("ItemStacked", 0);
	}
}