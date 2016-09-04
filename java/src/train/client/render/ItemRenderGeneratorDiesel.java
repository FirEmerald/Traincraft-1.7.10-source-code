/*******************************************************************************
 * Copyright (c) 2013 Spitfire4466. All rights reserved.
 * 
 * @name TrainCraft
 * @author Spitfire4466
 ******************************************************************************/

package src.train.client.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import src.train.client.render.models.blocks.ModelGeneratorDiesel;
import src.train.common.library.Info;
import cpw.mods.fml.client.FMLClientHandler;

public class ItemRenderGeneratorDiesel implements IItemRenderer {

	private ModelGeneratorDiesel generator;

	public ItemRenderGeneratorDiesel() {
		generator = new ModelGeneratorDiesel(1.0f);
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch (type) {
		case ENTITY: {
			renderGenerator(0.0F, 0F, 0.0F, 0.0F);
			break;
		}
		case EQUIPPED: {
			renderGenerator(0F, 0.1F, 0F, 180.0F);
			break;
		}
		case EQUIPPED_FIRST_PERSON: {
			renderGenerator(0F, 0.1F, 0F, 180.0F);
			return;
		}
		case INVENTORY: {
			renderGenerator(0.5F, -0.1F, 0.5F, 0.0F);
			break;
		}
		default:
			break;
		}
	}

	private void renderGenerator(float f, float g, float h, float rotation) {
		Tessellator tesselator = Tessellator.instance;
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(Info.resourceLocation,Info.modelTexPrefix + "generator_diesel.png"));
		GL11.glPushMatrix();
		GL11.glTranslatef(f, g, h);
		//GL11.glRotatef(rotation, f, g, h);
		GL11.glScalef(0.7F, 0.7F, 0.7F);
		generator.render2(0.0625F);
		GL11.glPopMatrix();
	}
}
