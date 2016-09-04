package src.train.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import src.train.common.api.AbstractTrains;
import src.train.common.api.DieselTrain;
import src.train.common.api.ElectricTrain;
import src.train.common.api.LiquidManager;
import src.train.common.api.Locomotive;
import src.train.common.api.SteamTrain;
import src.train.common.inventory.InventoryLoco;
import src.train.common.library.Info;

public class GuiLoco2 extends GuiContainer {

	private String texture = Info.guiPrefix + "customButton.png";
	private int textureX = 0;
	private int textureY = 46;
	private int textureSizeX = 40;
	private int textureSizeY = 13;
	private int buttonPosX = 0;
	private int buttonPosY = 0;
	private GuiButton buttonLock;

	private Locomotive loco;
	private InventoryLoco invLoco;
	private float yaw;
	private float roll;
	private boolean rollDown;

	public GuiLoco2(InventoryPlayer inventoryplayer, Entity entityminecart) {
		super(new InventoryLoco(inventoryplayer, (Locomotive) entityminecart));
		loco = (Locomotive) entityminecart;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		if (!((Locomotive) loco).parkingBrake) {
			if (loco instanceof SteamTrain) {
				textureX = 41;
				textureY = 13;
				textureSizeX = 40;
				textureSizeY = 13;
			}
			else {
				textureX = 126;
				textureY = 13;
				textureSizeX = 43;
				textureSizeY = 13;
			}
			buttonPosX = 43;
			buttonPosY = -13;
			buttonList.add(new GuiCustomButton(2, ((width - xSize) / 2) + buttonPosX - 12, ((height - ySize) / 2) + buttonPosY, textureSizeX, textureSizeY, "", texture, textureX, textureY));//Brake: Off
		}
		else if (((Locomotive) loco).parkingBrake) {
			if (loco instanceof SteamTrain) {
				textureX = 0;
				textureY = 13;
				textureSizeX = 40;
				textureSizeY = 13;
			}
			else {
				textureX = 82;
				textureY = 13;
				textureSizeX = 43;
				textureSizeY = 13;
			}
			buttonPosX = 0;
			buttonPosY = -13;
			buttonList.add(new GuiCustomButton(2, ((width - xSize) / 2) + buttonPosX, ((height - ySize) / 2) + buttonPosY, textureSizeX, textureSizeY, "", texture, textureX, textureY));//Brake: On 
		}
		int var1 = (this.width - xSize) / 2;
		int var2 = (this.height - ySize) / 2;
		if (!((Locomotive) loco).locked) {
			this.buttonList.add(this.buttonLock = new GuiButton(3, var1 + 108, var2 - 10, 67, 10, "Unlocked"));
		}
		else {
			this.buttonList.add(this.buttonLock = new GuiButton(3, var1 + 108, var2 - 10, 67, 10, "Locked"));
		}
		if (!(loco instanceof SteamTrain)) {
			if (((Locomotive) loco).isLocoTurnedOn()) {
				this.buttonList.add(this.buttonLock = new GuiButton(4, var1 + 108, var2 - 22, 67, 12, "Stop Engine"));
			}
			else {
				this.buttonList.add(this.buttonLock = new GuiButton(4, var1 + 108, var2 - 22, 67, 12, "Start Engine"));
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if (guibutton.id == 2) {
			if ((!((Locomotive) loco).getParkingBrakeFromPacket()) && ((Locomotive) loco).getSpeed() < 10) {
				((Locomotive) loco).sendParkingBrakePacket(true);
				((Locomotive) loco).parkingBrake = true;
				guibutton.displayString = "Brake: On";
				this.initGui();
			}
			else if (((Locomotive) loco).getSpeed() < 10) {
				((Locomotive) loco).sendParkingBrakePacket(false);
				((Locomotive) loco).parkingBrake = false;
				guibutton.displayString = "Brake: Off";
				this.initGui();
			}
		}
		if (guibutton.id == 3) {
			if (((AbstractTrains) loco).riddenByEntity != null && ((AbstractTrains) loco).riddenByEntity instanceof EntityPlayer && ((EntityPlayer) ((AbstractTrains) loco).riddenByEntity).equals(((AbstractTrains) loco).trainOwner)) {
				if ((!((AbstractTrains) loco).locked)) {
					((AbstractTrains) loco).sendTrainLockedPacket((EntityPlayer) ((AbstractTrains) loco).riddenByEntity, true);
					((AbstractTrains) loco).locked = true;
					guibutton.displayString = "Locked";
					this.initGui();
				}
				else {
					((AbstractTrains) loco).sendTrainLockedPacket((EntityPlayer) ((AbstractTrains) loco).riddenByEntity, false);
					((AbstractTrains) loco).locked = false;
					guibutton.displayString = "UnLocked";
					this.initGui();
				}
			}
			else if (((AbstractTrains) loco).riddenByEntity != null && ((AbstractTrains) loco).riddenByEntity instanceof EntityPlayer) {
				((EntityPlayer) ((AbstractTrains) loco).riddenByEntity).addChatMessage(new ChatComponentText("You are not the owner"));
			}
		}
		if (guibutton.id == 4) {
			if (loco.isLocoTurnedOn()) {
				if(loco.getSpeed() <= 1){
					loco.setLocoTurnedOn(false, true, false,10);
					guibutton.displayString = "Start Engine";
				}else{
					((EntityPlayer)loco.riddenByEntity).addChatMessage(new ChatComponentText("Stop before turning it Off!"));
				}
			}
			else {
				loco.setLocoTurnedOn(true, true, false,10);
				guibutton.displayString = "Stop Engine";
			}
		}
	}

	@Override
	protected void drawCreativeTabHoveringText(String str, int t, int g) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		//int liqui = (dieselInventory.getLiquidAmount() * 50) / dieselInventory.getTankCapacity();
		String state = "";
		if (((Locomotive) loco).locked) state = "Locked";
		if (!((Locomotive) loco).locked) state = "Unlocked";

		int textWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth("the GUI, change speed, destroy it.");
		int startX = 90;
		int startY = 5;

		int i4 = 0xf0100010;
		int h = 8;
		int w = textWidth;
		drawGradientRect(startX - 3, startY - 4, startX + textWidth + 3, startY + 8 + 4 + 40, i4, i4);
		drawGradientRect(startX - 4, startY - 3, startX + textWidth + 4, startY + 8 + 3 + 40, i4, i4);
		int colour1 = 0x505000ff;
		int colour2 = (colour1 & 0xfefefe) >> 1 | colour1 & 0xff000000;
		drawGradientRect(startX - 3, startY - 3, startX + textWidth + 3, startY + 8 + 3 + 40, colour1, colour2);
		drawGradientRect(startX - 2, startY - 2, startX + textWidth + 2, startY + 8 + 2 + 40, i4, i4);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(str, startX, startY, -1);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("only its owner can open", startX, startY + 10, -1);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("the GUI, change speed, destroy it.", startX, startY + 20, -1);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Current state: " + state, startX, startY + 30, -1);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Owner: " + ((AbstractTrains) loco).trainOwner.toString(), startX, startY + 40, -1);
	}

	public boolean intersectsWith(int mouseX, int mouseY) {
		//System.out.println(mouseX+" "+mouseY);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		if (mouseX >= j + 124 && mouseX <= j + 174 && mouseY >= k - 10 && mouseY <= k) { return true; }
		return false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Minecraft.getMinecraft().fontRenderer.drawString(loco.getInventoryName(), 39, 7, 0x000000);
		Minecraft.getMinecraft().fontRenderer.drawString(loco.getInventoryName(), 41, 5, 0x000000);
		Minecraft.getMinecraft().fontRenderer.drawString(loco.getInventoryName(), 39, 5, 0x000000);
		Minecraft.getMinecraft().fontRenderer.drawString(loco.getInventoryName(), 41, 7, 0x000000);

		Minecraft.getMinecraft().fontRenderer.drawString(loco.getInventoryName(), 39, 6, 0x000000);
		Minecraft.getMinecraft().fontRenderer.drawString(loco.getInventoryName(), 41, 6, 0x000000);
		Minecraft.getMinecraft().fontRenderer.drawString(loco.getInventoryName(), 40, 7, 0x000000);
		Minecraft.getMinecraft().fontRenderer.drawString(loco.getInventoryName(), 40, 5, 0x000000);
		Minecraft.getMinecraft().fontRenderer.drawString(loco.getInventoryName(), 40, 6, 0xd3a900);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		if (intersectsWith(i, j)) {
			drawCreativeTabHoveringText("When a locomotive is locked,", i, j);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int t, int g) {
		String i = Info.guiPrefix + "gui_loco.png";

		if (loco instanceof ElectricTrain) {
			i = Info.guiPrefix + "gui_tram.png";
		}
		if (loco instanceof SteamTrain) {
			i = Info.guiPrefix + "gui_loco_steam.png";
		}
		if (loco instanceof DieselTrain) {
			i = Info.guiPrefix + "gui_loco_diesel.png";
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(new ResourceLocation(Info.modID.toLowerCase(), i));
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

		if (loco instanceof SteamTrain) {
			int load = (((SteamTrain) loco).getWater());
			int lo = Math.abs(((load * 50) / (((SteamTrain) loco).getCartTankCapacity())));

			if (((SteamTrain) loco).getLiquidItemID() == LiquidManager.WATER_FILTER.getFluidID()) {
				drawTexturedModalRect(j + 143, (k + 68) - lo, 190, 69 - lo, 18, lo + 1);
			}
			if (loco.getIsFuelled()) {
				int l = loco.getFuelDiv(12);
				drawTexturedModalRect(j + 8, (k + 36 + 12) - l, 176, 12 - l, 14, l + 2);
			}
		}
		else if (loco instanceof DieselTrain) {
			int load = (((DieselTrain) loco).getDiesel());
			int lo2 = Math.abs(((load * 50) / (((DieselTrain) loco).getCartTankCapacity())));
			drawTexturedModalRect(j + 143, (k + 68) - lo2, 192, 120 - lo2, 18, lo2);
			if (loco.getIsFuelled()) {
				int l = loco.getFuelDiv(12);
				drawTexturedModalRect(j + 10, (k + 36 + 13) - l, 178, 12 - l, 14, l + 2);
			}
		}
		else {
			for (int i1 = loco.numCargoSlots; i1 < 5; i1++) {
				drawTexturedModalRect(j + 79 + 18 * i1, k + 17, 190, 0, 18, 18);
			}
			for (int j1 = loco.numCargoSlots1; j1 < 5; j1++) {
				drawTexturedModalRect(j + 79 + 18 * j1, k + 35, 190, 0, 18, 18);
			}
			for (int k1 = loco.numCargoSlots2; k1 < 5; k1++) {
				drawTexturedModalRect(j + 79 + 18 * k1, k + 53, 190, 0, 18, 18);
			}
			if (loco.getIsFuelled()) {
				int l = loco.getFuelDiv(12);
				drawTexturedModalRect(j + 8, (k + 36 + 12) - l, 176, 12 - l, 14, l + 2);
			}
		}

		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Carts pulled: " + loco.getCurrentNumCartsPulled(), 1, 10, 0xFFFFFF);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Mass pulled: " + loco.getCurrentMassPulled(), 1, 20, 0xFFFFFF);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Speed reduction: " + loco.getCurrentSpeedSlowDown(), 1, 30, 0xFFFFFF);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Accel reduction: " + loco.getCurrentAccelSlowDown(), 1, 40, 0xFFFFFF);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Brake reduction: " + loco.getCurrentBrakeSlowDown(), 1, 50, 0xFFFFFF);
		if (loco instanceof DieselTrain) {
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Fuel consumption: 1 every " + (loco.getFuelConsumption() / 5) + " ticks", 1, 60, 0xFFFFFF);
		}
		else {
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Fuel consumption: 1 every " + loco.getFuelConsumption() + " ticks", 1, 60, 0xFFFFFF);
		}
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Fuel: " + loco.getFuel(), 1, 70, 0xFFFFFF);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Power: " + loco.getPower() + " Mhp", 1, 80, 0xFFFFFF);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("State: " + loco.getState(), 1, 90, 0xFFFFFF);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Heat level: " + loco.getOverheatLevel(), 1, 100, 0xFFFFFF);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Maximum Speed: " + (loco.getCustomSpeedGUI()), 1, 110, 0xFFFFFF);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Destination: " + (loco.getDestinationGUI()), 1, 120, 0xFFFFFF);
	}
}