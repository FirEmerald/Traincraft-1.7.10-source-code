package src.train.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import src.train.common.api.AbstractTrains;
import src.train.common.core.handlers.PacketHandler;
import src.train.common.entity.rollingStock.EntityTracksBuilder;
import src.train.common.inventory.InventoryBuilder;
import src.train.common.inventory.InventoryLoco;
import src.train.common.library.Info;

public class GuiBuilder extends GuiContainer {

	private EntityTracksBuilder builder;
	private InventoryLoco invLoco;
	private int requestedHeight;
	private EntityPlayer player;
	private GuiButton buttonLock;

	public GuiBuilder(EntityPlayer player, InventoryPlayer inventoryplayer, Entity entityminecart) {
		super(new InventoryBuilder(inventoryplayer, (EntityTracksBuilder) entityminecart));
		xSize = 255;
		ySize = 193;
		builder = (EntityTracksBuilder) entityminecart;
		requestedHeight = (int) builder.getPlannedHeight();
		this.player = player;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		if (((EntityTracksBuilder) builder).getFollowTracks() == 1) {
			buttonList.add(new GuiButton(1, ((width - xSize) / 2) + 3, ((height - ySize) / 2) - 20, 80, 20, StatCollector.translateToLocal("builder.follow.name")));
		}
		if (((EntityTracksBuilder) builder).getFollowTracks() == 0) {
			buttonList.add(new GuiButton(1, ((width - xSize) / 2) + 3, ((height - ySize) / 2) - 20, 80, 20, StatCollector.translateToLocal("builder.remove.name")));
		}
		buttonList.add(new GuiButton(2, ((width - xSize) / 2) + 85, ((height - ySize) / 2) - 40, 30, 20, StatCollector.translateToLocal("builder.up.name")));
		buttonList.add(new GuiButton(3, ((width - xSize) / 2) + 85, ((height - ySize) / 2) - 20, 30, 20, StatCollector.translateToLocal("builder.down.name")));

		int var1 = (this.width - xSize) / 2;
		int var2 = (this.height - ySize) / 2;
		if (!((AbstractTrains) builder).locked) {
			this.buttonList.add(this.buttonLock = new GuiButton(4, var1 + 3, var2 - 30, 51, 10, StatCollector.translateToLocal("train.unlocked.name")));
		}
		else {
			this.buttonList.add(this.buttonLock = new GuiButton(4, var1 + 3, var2 - 30, 43, 10, StatCollector.translateToLocal("train.locked.name")));
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		fontRendererObj.drawString("Builder", 4, 8, 0x404040);
		fontRendererObj.drawString("Inventory", 113, 100, 0x404040);
		fontRendererObj.drawString("Doesn't work", 4, 160, 0x404040);
		fontRendererObj.drawString("with new tracks", 4, 170, 0x404040);
		fontRendererObj.drawString("yet", 4, 180, 0x404040);

		fontRendererObj.drawString(StatCollector.translateToLocal("builder.currElev.name") + ": " + (int) builder.currentHeight, 120, -25, 0xFFFFFF);
		fontRendererObj.drawString(StatCollector.translateToLocal("builder.reqElev.name") + ": " + (int) builder.getPlannedHeight(), 120, -10, 0xFFFFFF);
		if (intersectsWith(i, j)) {
			drawCreativeTabHoveringText("When a builder is locked,", i, j);
		}
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if (guibutton.id == 1) {
			if (((EntityTracksBuilder) builder).getFollowTracks() == 1)
			{
				sendPacket(0, false);
				guibutton.displayString = StatCollector.translateToLocal("builder.remove.name");
			}
			else
			{
				sendPacket(1, false);
				guibutton.displayString = StatCollector.translateToLocal("builder.follow.name");
			}
		}
		if (guibutton.id == 2)
		{
			sendPacket(1, true);
		}
		if (guibutton.id == 3)
		{
			sendPacket(-1, true);
		}

		if (guibutton.id == 4) {
			if (player != null && player instanceof EntityPlayer && player.getGameProfile().getId().equals(((AbstractTrains) builder).trainOwner)) {
				if ((!((AbstractTrains) builder).locked)) {
					AxisAlignedBB box = ((EntityTracksBuilder) builder).boundingBox.expand(5, 5, 5);
					List lis3 = ((EntityTracksBuilder) builder).worldObj.getEntitiesWithinAABBExcludingEntity(builder, box);
					if (lis3 != null && lis3.size() > 0) {
						for (int j1 = 0; j1 < lis3.size(); j1++) {
							Entity entity = (Entity) lis3.get(j1);
							if (entity instanceof EntityPlayer) {
								((AbstractTrains) builder).sendTrainLockedPacket((EntityPlayer) entity, true);
							}
						}
					}

					((AbstractTrains) builder).locked = true;
					guibutton.displayString = StatCollector.translateToLocal("train.locked.name");
					this.initGui();
				}
				else {
					AxisAlignedBB box = ((EntityTracksBuilder) builder).boundingBox.expand(5, 5, 5);
					List lis3 = ((EntityTracksBuilder) builder).worldObj.getEntitiesWithinAABBExcludingEntity(builder, box);
					if (lis3 != null && lis3.size() > 0) {
						for (int j1 = 0; j1 < lis3.size(); j1++) {
							Entity entity = (Entity) lis3.get(j1);
							if (entity instanceof EntityPlayer) {
								((AbstractTrains) builder).sendTrainLockedPacket((EntityPlayer) entity, false);
							}
						}
					}
					((AbstractTrains) builder).locked = false;
					guibutton.displayString = StatCollector.translateToLocal("train.unlocked.name");
					this.initGui();
				}
			}
			else if (player != null && player instanceof EntityPlayer) {
				player.addChatMessage(new ChatComponentTranslation("train.owner.name"));
			}
		}
	}

	@Override
	protected void drawCreativeTabHoveringText(String str, int t, int g) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String state = "";
		if (((AbstractTrains) builder).locked)
			state = "Locked";
		if (!((AbstractTrains) builder).locked)
			state = "Unlocked";

		int textWidth = fontRendererObj.getStringWidth("the GUI, change speed, destroy it.");
		int startX = 10;
		int startY = -10;

		int i4 = 0xf0100010;
		int h = 8;
		int w = textWidth;
		drawGradientRect(startX - 3, startY - 4, startX + textWidth + 3, startY + 8 + 4 + 40, i4, i4);
		drawGradientRect(startX - 4, startY - 3, startX + textWidth + 4, startY + 8 + 3 + 40, i4, i4);
		int colour1 = 0x505000ff;
		int colour2 = (colour1 & 0xfefefe) >> 1 | colour1 & 0xff000000;
		drawGradientRect(startX - 3, startY - 3, startX + textWidth + 3, startY + 8 + 3 + 40, colour1, colour2);
		drawGradientRect(startX - 2, startY - 2, startX + textWidth + 2, startY + 8 + 2 + 40, i4, i4);
		fontRendererObj.drawStringWithShadow(str, startX, startY, -1);
		fontRendererObj.drawStringWithShadow("only its owner can open", startX, startY + 10, -1);
		fontRendererObj.drawStringWithShadow("the GUI and destroy it.", startX, startY + 20, -1);
		fontRendererObj.drawStringWithShadow("Current state: " + state, startX, startY + 30, -1);
		fontRendererObj.drawStringWithShadow("Owner: " + ((AbstractTrains) builder).trainOwner.toString(), startX, startY + 40, -1);
	}

	public boolean intersectsWith(int mouseX, int mouseY) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		if (mouseX >= j + 3 && mouseX <= j + 55 && mouseY >= k - 30 && mouseY <= k - 20) {
			return true;
		}
		return false;
	}

	private void sendPacket(int packet, boolean packetID)
	{
		AxisAlignedBB box = ((EntityTracksBuilder) builder).boundingBox.expand(5, 5, 5);
		List lis3 = ((EntityTracksBuilder) builder).worldObj.getEntitiesWithinAABBExcludingEntity(builder, box);
		if (lis3 != null && lis3.size() > 0)
		{
			for (int j1 = 0; j1 < lis3.size(); j1++)
			{
				Entity entity = (Entity) lis3.get(j1);
				if (entity instanceof EntityPlayer)
				{
					PacketHandler.setBuilderPlannedHeight(entity, builder, packet, packetID);
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int t, int g) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(new ResourceLocation(Info.resourceLocation, Info.guiPrefix + "gui_builder2.png"));
		int j = ((width - xSize) / 2);
		int k = (height - ySize) / 2;
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		if (builder.getFuel() > 0) {
			int l = builder.scaleMaxFuel(12);
			drawTexturedModalRect(j + 8, (k + 36 + 12) - l, 0, 250 - l, 14, l + 2);
		}
		for (int i1 = builder.numBuilderSlots; i1 < 5; i1++) {
			drawTexturedModalRect(j + 105 + 18 * i1, k + 6, 14, 166, 18, 18);
		}
		for (int j1 = builder.numBuilderSlots1; j1 < 5; j1++) {
			drawTexturedModalRect(j + 105 + 18 * j1, k + 24, 14, 166, 18, 18);//
		}
		for (int k1 = builder.numBuilderSlots2; k1 < 5; k1++) {
			drawTexturedModalRect(j + 105 + 18 * k1, k + 42, 14, 166, 18, 18);
		}
		for (int k1 = builder.numBuilderSlots3; k1 < 5; k1++) {
			drawTexturedModalRect(j + 105 + 18 * k1, k + 60, 14, 166, 18, 18);
		}
	}
}