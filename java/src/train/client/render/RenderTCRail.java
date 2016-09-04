package src.train.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import src.train.client.render.models.blocks.ModelLargeSlopeTCTrack;
import src.train.client.render.models.blocks.ModelLeftSwitchTCTrack;
import src.train.client.render.models.blocks.ModelLeftTurnTCTrack;
import src.train.client.render.models.blocks.ModelMediumStraightTCTrack;
import src.train.client.render.models.blocks.ModelRightSwitchTCTrack;
import src.train.client.render.models.blocks.ModelRightTurnTCTrack;
import src.train.client.render.models.blocks.ModelSmallStraightTCTrack;
import src.train.client.render.models.blocks.ModelTwoWaysCrossingTCTrack;
import src.train.common.items.ItemTCRail.TrackTypes;
import src.train.common.tile.TileTCRail;

public class RenderTCRail extends TileEntitySpecialRenderer {

	private ModelSmallStraightTCTrack modelSmallStraight = new ModelSmallStraightTCTrack();
	private ModelMediumStraightTCTrack modelMediumStraight = new ModelMediumStraightTCTrack();
	private ModelRightTurnTCTrack modelRightTurn = new ModelRightTurnTCTrack();
	private ModelLeftTurnTCTrack modelLeftTurn = new ModelLeftTurnTCTrack();
	private ModelRightSwitchTCTrack modelRightSwitchTurn = new ModelRightSwitchTCTrack();
	private ModelLeftSwitchTCTrack modelLeftSwitchTurn = new ModelLeftSwitchTCTrack();
	private ModelTwoWaysCrossingTCTrack modelTwoWaysCrossing = new ModelTwoWaysCrossingTCTrack();
	private ModelLargeSlopeTCTrack modelLargeSlope = new ModelLargeSlopeTCTrack();

	public RenderTCRail() {
		
	}

	@Override
	public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float var8) {
		if(var1 instanceof TileTCRail){
			//System.out.println(((TileTCRail)var1).getType());
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.SMALL_STRAIGHT.getLabel())){
				modelSmallStraight.render((TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.TWO_WAYS_CROSSING.getLabel())){
				modelTwoWaysCrossing.render((TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.MEDIUM_STRAIGHT.getLabel())){
				modelMediumStraight.render((TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.LARGE_SLOPE_WOOD.getLabel())){
				modelLargeSlope.render("wood",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.LARGE_SLOPE_GRAVEL.getLabel())){
				modelLargeSlope.render("gravel",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.LARGE_SLOPE_BALLAST.getLabel())){
				modelLargeSlope.render("ballast",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.MEDIUM_RIGHT_TURN.getLabel())){
				modelRightTurn.render("medium",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.MEDIUM_LEFT_TURN.getLabel())){
				modelLeftTurn.render("medium",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.LARGE_RIGHT_TURN.getLabel())){
				modelRightTurn.render("large",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.VERY_LARGE_RIGHT_TURN.getLabel())){
				modelRightTurn.render("very_large",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.LARGE_LEFT_TURN.getLabel())){
				modelLeftTurn.render("large",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.VERY_LARGE_LEFT_TURN.getLabel())){
				modelLeftTurn.render("very_large",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.MEDIUM_RIGHT_SWITCH.getLabel())){
				modelRightSwitchTurn.render("medium",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.MEDIUM_LEFT_SWITCH.getLabel())){
				modelLeftSwitchTurn.render("medium",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.LARGE_RIGHT_SWITCH.getLabel())){
				modelRightSwitchTurn.render("large_90",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.LARGE_LEFT_SWITCH.getLabel())){
				modelLeftSwitchTurn.render("large_90",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.MEDIUM_RIGHT_PARALLEL_SWITCH.getLabel())){
				modelRightSwitchTurn.render("medium_parallel",(TileTCRail) var1, x, y, z);
			}
			if(((TileTCRail)var1).hasModel && ((TileTCRail)var1).getType()!=null && ((TileTCRail)var1).getType().equals(TrackTypes.MEDIUM_LEFT_PARALLEL_SWITCH.getLabel())){
				modelLeftSwitchTurn.render("medium_parallel",(TileTCRail) var1, x, y, z);
			}
		}
	}
}
