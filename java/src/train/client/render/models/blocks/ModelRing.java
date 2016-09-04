/*******************************************************************************
 * Copyright (c) 2013 Mrbrutal. All rights reserved.
 * 
 * @name Traincraft
 * @author Mrbrutal
 ******************************************************************************/

package src.train.client.render.models.blocks;

import org.lwjgl.opengl.GL11;

import src.train.common.library.Info;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class ModelRing extends ModelBase  {
private IModelCustom ring;
	
	public ModelRing() {
		ring = new AdvancedModelLoader().loadModel(new ResourceLocation(Info.resourceLocation, Info.modelPrefix + "ring.obj"));
	}
	
	public void render() {
		ring.renderAll();
	}
	
	public void render(int color) {
		//GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPushMatrix();
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(Info.resourceLocation, Info.modelTexPrefix + "ring.png"));
		render();
		GL11.glPopMatrix();
	}
}
