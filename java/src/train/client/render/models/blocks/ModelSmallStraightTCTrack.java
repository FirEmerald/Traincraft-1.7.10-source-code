package src.train.client.render.models.blocks;

import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import src.train.common.library.Info;
import src.train.common.tile.TileTCRail;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSmallStraightTCTrack extends ModelBase {
	private IModelCustom modelSmallStraight;

	public ModelSmallStraightTCTrack() {
		modelSmallStraight = AdvancedModelLoader.loadModel(new ResourceLocation(Info.resourceLocation, Info.modelPrefix + "track_normal.obj"));
	}

	public void render() {
		modelSmallStraight.renderAll();
	}

	public void render(TileTCRail tcRail, double x, double y, double z) {
		// Push a blank matrix onto the stack
		GL11.glPushMatrix();

		// Move the object into the correct position on the block (because the OBJ's origin is the center of the object)
		GL11.glTranslatef((float) x + 0.5f, (float) y, (float) z + 0.5f);

		// Bind the texture, so that OpenGL properly textures our block.
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(Info.resourceLocation, Info.modelTexPrefix + "track_normal.png"));
		GL11.glColor3f(1, 1, 1);
		//GL11.glScalef(0.5f, 0.5f, 0.5f);
		int facing = tcRail.getWorldObj().getBlockMetadata((int) tcRail.xCoord, (int) tcRail.yCoord, (int) tcRail.zCoord);

		if (facing == 3) {
			GL11.glRotatef(90, 0, 1, 0);
		}
		if (facing == 1) {
			GL11.glRotatef(90, 0, 1, 0);
		}

		this.render();

		// Pop this matrix from the stack.
		GL11.glPopMatrix();
	}

}
