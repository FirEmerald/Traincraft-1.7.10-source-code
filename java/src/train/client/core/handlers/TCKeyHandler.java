package src.train.client.core.handlers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.server.MinecraftServer;
import src.train.common.Traincraft;
import src.train.common.networking.KeyPacket;

public class TCKeyHandler
{
	public static KeyBinding horn;
	public static KeyBinding inventory;
	public static KeyBinding up;
	public static KeyBinding down;
	public static KeyBinding idle;
	public static KeyBinding furnace;
	public TCKeyHandler()
	{
		horn = new KeyBinding("key.tc.horn", Keyboard.KEY_H, "key.categories.traincraft");
		ClientRegistry.registerKeyBinding(horn);
		inventory = new KeyBinding("key.tc.inventory", Keyboard.KEY_R, "key.categories.traincraft");
		ClientRegistry.registerKeyBinding(inventory);
		up = new KeyBinding("key.tc.up", Keyboard.KEY_Y, "key.categories.traincraft");
		ClientRegistry.registerKeyBinding(up);
		down = new KeyBinding("key.tc.down", Keyboard.KEY_X, "key.categories.traincraft");
		ClientRegistry.registerKeyBinding(down);
		idle = new KeyBinding("key.tc.idle", Keyboard.KEY_C, "key.categories.traincraft");
		ClientRegistry.registerKeyBinding(idle);
		furnace = new KeyBinding("key.tc.furnace", Keyboard.KEY_F, "key.categories.traincraft");
		ClientRegistry.registerKeyBinding(furnace);
	}
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		if (up.getIsKeyPressed()) sendKeyControlsPacket(0);
		if (down.getIsKeyPressed()) sendKeyControlsPacket(2);
		if (idle.isPressed()) sendKeyControlsPacket(6);
		if (inventory.isPressed()) sendKeyControlsPacket(7);
		if (horn.isPressed()) sendKeyControlsPacket(8);
		if (furnace.isPressed()) sendKeyControlsPacket(9);
		if (Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed()) sendKeyControlsPacket(11);
	}
	
	public static void sendKeyControlsPacket(int key)
	{
		Traincraft.network.sendToServer(new KeyPacket(key));
	}
}