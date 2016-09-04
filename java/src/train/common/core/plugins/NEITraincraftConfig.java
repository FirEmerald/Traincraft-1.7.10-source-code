package src.train.common.core.plugins;

import src.train.common.Traincraft;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import cpw.mods.fml.common.Mod;

public class NEITraincraftConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {
		API.registerRecipeHandler(new NEITraincraftWorkbenchRecipePlugin());
        API.registerUsageHandler(new NEITraincraftWorkbenchRecipePlugin());
        API.registerRecipeHandler(new NEIAssemblyTableRecipePlugin());
        API.registerUsageHandler(new NEIAssemblyTableRecipePlugin());
        API.registerRecipeHandler(new NEIOpenHearthFurnaceRecipePlugin());
        API.registerUsageHandler(new NEIOpenHearthFurnaceRecipePlugin());
        API.registerRecipeHandler(new NEIDistillationTowerRecipePlugin());
        API.registerUsageHandler(new NEIDistillationTowerRecipePlugin());
	}

	@Override
    public String getName() {
            return Traincraft.class.getAnnotation(Mod.class).name();
    }

    @Override
    public String getVersion() {
            return Traincraft.class.getAnnotation(Mod.class).version();
    }    
}