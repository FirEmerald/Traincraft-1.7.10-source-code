/*******************************************************************************
 * Copyright (c) 2012 Mrbrutal. All rights reserved.
 * 
 * @name TrainCraft
 * @author Mrbrutal
 ******************************************************************************/

package src.train.client.render.models.blocks;

import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import src.train.client.render.CustomModelRenderer;
import src.train.common.library.Info;

public class ModelStopper extends ModelBase {
	
	private IModelCustom track;

	public CustomModelRenderer box;
	public CustomModelRenderer box0;
	public CustomModelRenderer box1;
	public CustomModelRenderer box10;
	public CustomModelRenderer box2;
	public CustomModelRenderer box3;
	public CustomModelRenderer box35;
	public CustomModelRenderer box4;
	public CustomModelRenderer box5;

	private float scale;

	public ModelStopper(float scale) {
		
		track =  AdvancedModelLoader.loadModel(new ResourceLocation(Info.resourceLocation, Info.modelPrefix + "track_normal.obj"));
		
		box = new CustomModelRenderer(43, 4, 64, 64);
		box.addBox(0F, 0F, 0F, 2, 15, 1, scale);
		box.setPosition(-8F, 0F, 4F);
		box.rotateAngleZ = -0.8028514559173916F;

		box0 = new CustomModelRenderer(36, 4, 64, 64);
		box0.addBox(0F, 0F, 0F, 2, 15, 1, scale);
		box0.setPosition(-8F, 0F, -5F);
		box0.rotateAngleZ = -0.8028514559173916F;

		box1 = new CustomModelRenderer(57, 4, 64, 64);
		box1.addBox(-2F, 0F, 0F, 2, 9, 1, scale);
		box1.setPosition(8F, 0F, -5F);
		box1.rotateAngleZ = -5.759586531581287F;

		box10 = new CustomModelRenderer(5, 5, 64, 64);
		box10.addBox(0F, 0F, 0F, 2, 4, 16, scale);
		box10.setPosition(4F, 7F, -8F);

		box2 = new CustomModelRenderer(50, 4, 64, 64);
		box2.addBox(-2F, 0F, 0F, 2, 9, 1, scale);
		box2.setPosition(8F, 0F, 4F);
		box2.rotateAngleZ = -5.759586531581287F;

		box3 = new CustomModelRenderer(26, 7, 64, 64);
		box3.addBox(0F, 0F, 0F, 1, 3, 3, scale);
		box3.setPosition(4F, 11F, -5F);

		box35 = new CustomModelRenderer(6, 5, 64, 64);
		box35.addBox(0F, 0F, 0F, 2, 12, 1, scale);
		box35.setPosition(2F, 0F, 5F);

		box4 = new CustomModelRenderer(0, 27, 64, 64);
		box4.addBox(0F, 0F, 0F, 16, 1, 16, scale);
		box4.setPosition(-8F, 0F, -8F);

		box5 = new CustomModelRenderer(13, 5, 64, 64);
		box5.addBox(0F, 0F, 0F, 2, 12, 1, scale);
		box5.setPosition(2F, 0F, -6F);
	}

	public void render(float f5, int meta) {
		if (meta != -1) {
			switch (meta) {
			case 0:
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			case 1:
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			case 2:
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			case 3:
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			}
		}
		box.render(f5);
		box0.render(f5);
		box1.render(f5);
		box10.render(f5);
		box2.render(f5);
		box3.render(f5);
		box35.render(f5);
		//box4.render(f5);
		box5.render(f5);
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) 0, (float) 0, (float) 0);
		GL11.glRotatef(90, 0, 1, 0);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(Info.resourceLocation, Info.modelTexPrefix + "track_normal.png"));
		GL11.glColor3f(1, 1, 1);
		track.renderAll();
		GL11.glPopMatrix();
	}

	public void render2(float f5) {
		track.renderAll();
		box.render(f5);
		box0.render(f5);
		box1.render(f5);
		box10.render(f5);
		box2.render(f5);
		box3.render(f5);
		box35.render(f5);
		//box4.render(f5);
		box5.render(f5);
	}
}
