package src.train.common.core.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import src.train.common.Traincraft;
import src.train.common.api.AbstractTrains;
import src.train.common.mysql.mysqlLogger;

public class RollingStockStatsEventHandler {

	/**
	 * Stores the distance a player has done on this train
	 */
	private ArrayList<playerDistance> plDist = new ArrayList<playerDistance>();
	private AbstractTrains train;
	private mysqlLogger logger;

	public RollingStockStatsEventHandler(AbstractTrains train) {
		this.train = train;
		logger = new mysqlLogger();
	}

	public RollingStockStatsEventHandler() {
		logger = new mysqlLogger();
	}

	/**
	 * Calculates the distance this train has done if a player rides it, also calculates
	 */
	/* Ready for stats implement, unused for now** */
	public void trainDistance() {
		double xDist = train.prevPosX - train.posX;
		double yDist = train.prevPosY - train.posY;
		double zDist = train.prevPosZ - train.posZ;
		int var7 = Math.round(MathHelper.sqrt_double(xDist * xDist + yDist * yDist + zDist * zDist) * 100);
		train.trainDistanceTraveled += (var7);
		//System.out.println(trainDistanceTraveled);
		if (train.riddenByEntity != null && train.riddenByEntity instanceof EntityPlayer) {
			for (playerDistance d : plDist) {
				if (((EntityPlayer) train.riddenByEntity).getGameProfile().getId().equals(d.uuid))
				{
					d.setDistance(d.getDistance() + (var7));
					//System.out.println(d.getDistance()+" "+((EntityPlayer)riddenByEntity).username);
					return;
				}
			}
			plDist.add(new playerDistance(((EntityPlayer) train.riddenByEntity).getGameProfile().getId(), (double) (var7)));
		}
	}

	/**
	 * just an object to register the distance a player has done
	 * 
	 */
	private class playerDistance {
		private UUID uuid;
		private double distance;

		private playerDistance(UUID uuid, double distance) {
			this.uuid = uuid;
			this.distance = distance;
		}

		protected double getDistance() {
			return distance;
		}

		protected UUID getUUID() {
			return uuid;
		}

		protected void setDistance(double distance) {
			this.distance = distance;
		}
	}

	private String castToString(double str) {
		return "" + str;
	}

	/**
	 * trainDestroy event, on train destroyed by player
	 * 
	 * @param trainID
	 * @param trainName
	 * @param trainType
	 * @param trainCreator
	 * @param trainDestroyer
	 * @param posX
	 * @param posY
	 * @param posZ
	 */
	public void trainDestroy(int trainID, String trainName, String trainType, UUID trainCreator, UUID trainDestroyer, String pos) {
		Map<String, String> destroy = new HashMap<String, String>();
		if (trainID < 1)
			return;
		destroy.put("uID", new String(castToString(trainID)));
		destroy.put("itemID", trainName);
		destroy.put("trainType", trainType);
		destroy.put("trainCreator", trainCreator.toString());
		destroy.put("trainDestroyer", trainDestroyer.toString());
		destroy.put("location", pos);
		if (Traincraft.mysqlLoggerEnabled && ConfigHandler.MYSQL_ENABLE)
			logger.logEvent("trainDestroy", destroy);
		//logger.logEvent("trainDestroy", destroy);
	}

	/**
	 * trainExplode event, on locomotive explosion
	 * 
	 * @param trainID
	 * @param trainName
	 * @param trainType
	 * @param trainCreator
	 * @param posX
	 * @param posY
	 * @param posZ
	 */
	public void trainExplode(int trainID, String trainName, String trainType, UUID trainCreator, String pos) {
		Map<String, String> explode = new HashMap<String, String>();
		if (trainID < 1)
			return;
		explode.put("uID", new String(castToString(trainID)));
		explode.put("itemID", trainName);
		explode.put("trainType", trainType);
		explode.put("trainCreator", trainCreator.toString());
		explode.put("location", pos);
		if (Traincraft.mysqlLoggerEnabled && ConfigHandler.MYSQL_ENABLE)
			logger.logEvent("trainExplode", explode);
		//logger.logEvent("trainExplode", explode);
	}

	/**
	 * trainCreate event, on item creation
	 * 
	 * @param trainID
	 * @param trainName
	 * @param trainType
	 * @param uuid
	 * @param posX
	 * @param posY
	 * @param posZ
	 */
	public void trainCreate(int trainID, String trainName, String trainType, UUID uuid, String pos) {
		Map<String, String> create = new HashMap<String, String>();
		if (trainID < 1)
			return;
		create.put("uID", new String(castToString(trainID)));
		create.put("itemID", trainName);
		create.put("trainType", trainType);
		create.put("trainCreator", uuid.toString());
		create.put("location", pos);
		if (Traincraft.mysqlLoggerEnabled && ConfigHandler.MYSQL_ENABLE)
			logger.logEvent("trainCreate", create);
		//logger.logEvent("trainCreate", create);
	}

	/**
	 * trainPlace event, on entity placement
	 * 
	 * @param trainID
	 * @param trainName
	 * @param trainType
	 * @param trainOwner
	 * @param posX
	 * @param posY
	 * @param posZ
	 */
	public void trainPlace(int trainID, String trainName, String trainType, UUID trainOwner, UUID trainOwner2, String pos) {
		Map<String, String> place = new HashMap<String, String>();
		if (trainID < 1)
			return;
		place.put("uID", new String(castToString(trainID)));
		place.put("itemID", trainName);
		place.put("trainType", trainType);
		place.put("trainCreator", trainOwner.toString());
		place.put("trainPlacer", trainOwner2.toString());
		place.put("location", pos);
		if (Traincraft.mysqlLoggerEnabled && ConfigHandler.MYSQL_ENABLE)
			logger.logEvent("trainPlace", place);
		//logger.logEvent("trainPlace", place);
	}

	/**
	 * trainPaint event, when a player paints a train with a dye
	 * 
	 * @param trainID
	 * @param trainName
	 * @param trainType
	 * @param trainCreator
	 * @param color
	 * @param posX
	 * @param posY
	 * @param posZ
	 */
	public void trainPaint(int trainID, String trainName, String trainType, UUID trainCreator, UUID trainInteracter, String color, String pos) {
		Map<String, String> paint = new HashMap<String, String>();
		if (trainID < 1)
			return;
		paint.put("uID", new String(castToString(trainID)));
		paint.put("itemID", trainName);
		paint.put("trainType", trainType);
		paint.put("trainCreator", trainCreator.toString());
		paint.put("color", color);
		paint.put("location", pos);
		paint.put("trainInteracter", trainInteracter.toString());
		if (Traincraft.mysqlLoggerEnabled && ConfigHandler.MYSQL_ENABLE)
			logger.logEvent("trainColor", paint);
		//logger.logEvent("trainColor", paint);
	}
}
