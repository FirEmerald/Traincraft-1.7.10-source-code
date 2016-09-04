/**
 * A track that detects all instance of SteamTrain
 * 
 * @author Spitfire4466
 */
package src.train.common.blocks.tracks;

import mods.railcraft.api.tracks.ITrackEmitter;
import net.minecraft.entity.item.EntityMinecart;
import src.train.common.api.SteamTrain;
import src.train.common.library.TrackIDs;

public class BlockDetectorSteamLocomotiveTrack extends BlockDetectorTrack implements ITrackEmitter {

	@Override
	public TrackIDs getTrackType() {
		return TrackIDs.DETECTOR_STEAM_LOCOMOTIVES;
	}
	@Override
	public void onMinecartPass(EntityMinecart cart) {
		if (cart instanceof SteamTrain) {
			setTrackPowering();
		}
	}
}