/*******************************************************************************
 * Copyright (c) 2013 Mrbrutal. All rights reserved.
 * 
 * @name Traincraft
 * @author Mrbrutal
 ******************************************************************************/

package src.train.client.render;

import src.train.client.render.models.blocks.ModelTCBook;
import src.train.common.tile.TileBook;
import src.train.common.tile.TileWindMill;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class RenderTCBook extends TileEntitySpecialRenderer {

	private ModelTCBook book = new ModelTCBook();
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float tick) {
		book.render((TileBook) tileentity, x, y, z);
	}
}