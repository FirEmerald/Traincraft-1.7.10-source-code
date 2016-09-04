package src.train.common.inventory;

import cofh.api.energy.IEnergyContainerItem;
import ic2.api.item.IElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import src.train.common.api.EntityRollingStock;
import src.train.common.api.LiquidManager;
import src.train.common.api.Locomotive;
import src.train.common.api.SteamTrain;
import src.train.common.core.handlers.FuelHandler;
import src.train.common.slots.SpecialSlots;

public class InventoryLoco extends Container {
	private Locomotive loco;
	private InventoryPlayer player;
	private int inventorySize;

	private SpecialSlots specialSlots = SpecialSlots.getInstance();

	public InventoryLoco(InventoryPlayer iinventory, EntityRollingStock entityminecart)
	{
		inventorySize = ((Locomotive) entityminecart).inventorySize;
		player = iinventory;
		loco = (Locomotive) entityminecart;
		int i = 1;
		addSlotToContainer(specialSlots.new SlotFuel(loco, 0, 8, 53));
		if (loco instanceof SteamTrain)
		{
			addSlotToContainer(specialSlots.new SlotLiquid(loco, 1, 32, 53));
			i = 2;
		}
		for (int j = 0; j < loco.numCargoSlots; j++)
		{
			addSlotToContainer(new Slot((IInventory) entityminecart, i, 80 + j * 18, 18));
			i++;
		}
		for (int k = 0; k < loco.numCargoSlots1; k++)
		{
			addSlotToContainer(new Slot((IInventory) entityminecart, i, 80 + k * 18, 36));
			i++;
		}
		for (int l = 0; l < loco.numCargoSlots2; l++)
		{
			addSlotToContainer(new Slot((IInventory) entityminecart, i, 80 + l * 18, 54));
			i++;
		}
		for (int i1 = 0; i1 < 3; i1++)
		{
			for (int k1 = 0; k1 < 9; k1++)
			{
				addSlotToContainer(new Slot(iinventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 84 + i1 * 18));
			}
		}
		for (int j1 = 0; j1 < 9; j1++)
		{
			addSlotToContainer(new Slot(iinventory, j1, 8 + j1 * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		if (loco.isDead) {
			return false;
		}
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i < inventorySize) {
				if (!mergeItemStack(itemstack1, inventorySize, inventorySlots.size(), true)) {
					return null;
				}
			}
			else if (i > inventorySize) {
				if (FuelHandler.steamFuelLast(itemstack) > 0 || LiquidManager.getInstance().isDieselLocoFuel(itemstack) || (itemstack.getItem() == Items.redstone) || (itemstack.getItem() instanceof IElectricItem) || (itemstack.getItem() instanceof IEnergyContainerItem)) {
					if (!mergeItemStack(itemstack1, 0, 1, false)) {
						return null;
					}
				}
				else if (LiquidManager.getInstance().isContainer(itemstack1) && loco instanceof SteamTrain) {
					if (!mergeItemStack(itemstack1, 1, 2, false)) {
						return null;
					}
				}
				else if (!mergeItemStack(itemstack1, 2, inventorySize, false)) {
					return null;
				}
			}
			else if (!mergeItemStack(itemstack1, 2, inventorySize, false)) {
				return null;
			}
			if (itemstack1.stackSize == 0) {
				slot.putStack(null);
			}
			else {
				slot.onSlotChanged();
			}
		}
		return itemstack;
	}
}