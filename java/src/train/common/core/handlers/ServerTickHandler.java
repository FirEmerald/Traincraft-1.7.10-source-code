package src.train.common.core.handlers;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ServerTickHandler
{
	private int windTicker = 0;

	private static Random rand = new Random();
	public static int windStrength = 10 + rand.nextInt(10);
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START)
		{
			if (windTicker % 128 == 0)
			{
				updateWind(event.world);
			}
			windTicker += 1;
		}
	}

	private static void updateWind(World world) {
		int upChance = 10;
		int downChance = 10;
		if (windStrength > 20) {
			upChance -= windStrength - 20;
		}
		if (windStrength < 10) {
			downChance -= 10 - windStrength;
		}
		if (rand.nextInt(100) <= upChance) {
			windStrength += 1;
		}
		if (rand.nextInt(100) <= downChance) {
			windStrength -= 1;
		}

	}
}