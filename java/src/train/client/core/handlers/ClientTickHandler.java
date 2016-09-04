package src.train.client.core.handlers;

import java.util.EnumSet;

import org.lwjgl.Sys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import src.train.client.gui.HUDloco;
import src.train.common.Traincraft;
import src.train.common.api.Locomotive;
import src.train.common.core.TrainModCore;
import src.train.common.library.Info;
import src.train.common.library.BlockData;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ClientTickHandler
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static boolean isHidden = false;
	private HUDloco locoHUD = null;
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START)
		{
    		if(!isHidden)
    		{
    			if(mc.theWorld != null && mc != null && mc.theWorld.playerEntities != null)
    			{
    				Traincraft.proxy.doNEICheck(new ItemStack(BlockData.tcRail.block));
        			Traincraft.proxy.doNEICheck(new ItemStack(BlockData.tcRailGag.block));
    				isHidden = true;
    			}
    		}
		}
	}

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END)
		{
			onRenderTick();
		}
	}

	public void onRenderTick()
	{
		if (mc.thePlayer != null && mc.thePlayer.ridingEntity != null && mc.thePlayer.ridingEntity instanceof Locomotive && mc.isGuiEnabled() && mc.currentScreen == null)
		{
			if (locoHUD == null) locoHUD = new HUDloco(Traincraft.proxy.getClientInstance());
			locoHUD.renderSkillHUD((Locomotive) mc.thePlayer.ridingEntity);
		}
	}
}