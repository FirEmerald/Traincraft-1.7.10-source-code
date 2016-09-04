/**
 * @author Spitfire4466
 */
package src.train.common.blocks.tracks;

import net.minecraft.util.IIcon;
import src.train.common.library.TrackIDs;

public class BlockVanillaSnowyTrack extends TrackBaseTraincraft {

	@Override
	public TrackIDs getTrackType() {
		return TrackIDs.VANILLA_SNOWY_TRACK;
	}
	@Override
	public IIcon getIcon() {
		int meta = this.tileEntity.getBlockMetadata();
		if (meta >= 6) {
			return getIcon(1);
		}
		return getIcon(0);
	}
	@Override
	public boolean isFlexibleRail() {
		return true;
	}
}