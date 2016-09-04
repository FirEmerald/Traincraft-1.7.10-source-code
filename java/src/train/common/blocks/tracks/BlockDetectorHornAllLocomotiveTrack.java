/**
 * A track that detects all instance of Locomotive
 * 
 * @author Spitfire4466
 */
package src.train.common.blocks.tracks;

import mods.railcraft.api.tracks.ITrackEmitter;
import net.minecraft.entity.item.EntityMinecart;
import src.train.common.api.Locomotive;
import src.train.common.library.TrackIDs;

public class BlockDetectorHornAllLocomotiveTrack extends BlockDetectorTrack implements ITrackEmitter {

	@Override
	public TrackIDs getTrackType() {
		return TrackIDs.HORN_ALL_LOCOMOTIVES;
	}
	@Override
	public void onMinecartPass(EntityMinecart cart) {
		if (cart instanceof Locomotive) {
			((Locomotive)cart).soundHorn();
		}
	}

}
