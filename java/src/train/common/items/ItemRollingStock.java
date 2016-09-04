package src.train.common.items;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.IMinecart;
import mods.railcraft.api.core.items.IMinecartItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import src.train.common.Traincraft;
import src.train.common.api.AbstractTrains;
import src.train.common.api.DieselTrain;
import src.train.common.api.ElectricTrain;
import src.train.common.api.EntityRollingStock;
import src.train.common.api.Locomotive;
import src.train.common.api.SteamTrain;
import src.train.common.api.Tender;
import src.train.common.core.handlers.ConfigHandler;
import src.train.common.core.handlers.RollingStockStatsEventHandler;
import src.train.common.entity.rollingStock.EntityTracksBuilder;
import src.train.common.items.ItemTCRail.TrackTypes;
import src.train.common.library.BlockData;
import src.train.common.library.EnumTrains;
import src.train.common.library.Info;
import src.train.common.library.ItemData;
import src.train.common.tile.TileTCRail;
import src.train.common.tile.TileTCRailGag;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRollingStock extends ItemMinecart implements IMinecart, IMinecartItem {

	private String trainName;
	private String trainCreator = "000000000000-0000-0000-0000-00000000";
	private int trainColor = -1;
	private RollingStockStatsEventHandler statsEvent = new RollingStockStatsEventHandler();
	String iconName;

	public ItemRollingStock(String iconName) {
		super(1);
		maxStackSize = 1;
		trainName = this.getUnlocalizedName();
		setCreativeTab(Traincraft.tcTab);
		this.iconName = iconName;
	}

	public int setNewUniqueID(ItemStack stack, EntityPlayer player, int numberOfTrains) {
		NBTTagCompound var3 = stack.getTagCompound();
		if (numberOfTrains <= 0) {
			numberOfTrains = AbstractTrains.uniqueIDs++;
		}
		else {
			AbstractTrains.uniqueIDs = numberOfTrains++;
		}
		if (var3 == null) {
			var3 = new NBTTagCompound();
			stack.setTagCompound(var3);
		}
		stack.getTagCompound().setInteger("uniqueID", numberOfTrains);
		stack.getTagCompound().setString("trainCreator", player.getGameProfile().getId().toString());
		return numberOfTrains;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		if (par1ItemStack.hasTagCompound()) {
			NBTTagCompound var5 = par1ItemStack.getTagCompound();
			int id = var5.getInteger("uniqueID");
			trainCreator = var5.getString("trainCreator");
			/*if (id > 0)
				par3List.add("\u00a77" + "ID: " + id);*/
			if (trainCreator.length() > 0)
				par3List.add("\u00a77" + "Creator: " + trainCreator);
			int color = var5.getInteger("trainColor");
			if (var5.hasKey("trainColor") && color <= 16)
				par3List.add("\u00a77" + "Color: " + AbstractTrains.getColorAsString(color));

		}
		double mass = getMass();
		int power = getMHP();
		int maxSpeed = getMaxSpeed();
		String[] additionnalInfo = getAdditionnalInfo();
		if (getTrainType().length() > 0)
			par3List.add("\u00a77" + "Type: " + getTrainType());
		if (power > 0)
			par3List.add("\u00a77" + "Power: " + power +" Mhp");
		if (mass > 0)
			par3List.add("\u00a77" + "Mass: " + (mass * 10));
		if (maxSpeed > 0)
			par3List.add("\u00a77" + "Max Speed: " + maxSpeed);
		if(getCargoCapacity()>0){
			par3List.add("\u00a77" + "Slots: "+getCargoCapacity());
		}
		if(additionnalInfo!=null && additionnalInfo.length>0 && additionnalInfo[0].length()>0){
			for(int i=0;i<additionnalInfo.length;i++){
				if(additionnalInfo[i].length()>0){
					par3List.add("\u00a77" + additionnalInfo[i]);
				}
			}
		}
	}
	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return EnumRarity.rare;
	}

	public String getTrainType() {
		for(EnumTrains trains : EnumTrains.values()){
			if(trains.getItem() == this){
				return trains.getTrainType();
			}
		}
		return "";
	}
	public double getMass() {
		for(EnumTrains trains : EnumTrains.values()){
			if(trains.getItem() == this){
				return trains.getMass();
			}
		}
		return 0;
	}
	public int getMaxSpeed() {
		for(EnumTrains trains : EnumTrains.values()){
			if(trains.getItem() == this){
				return trains.getMaxSpeed();
			}
		}
		return 0;
	}
	public int getMHP() {
		for(EnumTrains trains : EnumTrains.values()){
			if(trains.getItem() == this){
				return trains.getMHP();
			}
		}
		return 0;
	}
	public String[] getAdditionnalInfo() {
		for(EnumTrains trains : EnumTrains.values()){
			if(trains.getItem() == this){
				return trains.getAdditionnalTooltip();
			}
		}
		return null;
	}
	public int getCargoCapacity() {
		for(EnumTrains trains : EnumTrains.values()){
			if(trains.getItem() == this){
				return trains.getCargoCapacity();
			}
		}
		return 0;
	}
	public String getTrainName() {
		return trainName;
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		Block var11 = par3World.getBlock(par4, par5, par6);
		int meta = par3World.getBlockMetadata(par4, par5, par6);
		TileEntity tileentity = par3World.getTileEntity(par4, par5, par6);
		//System.out.println(meta);
		if (par3World.isRemote) {
			return false;
		}
		if(tileentity!=null && tileentity instanceof TileTCRail){
			TileTCRail tile = (TileTCRail) tileentity;
			if(tile!=null && tile.getType().equals(TrackTypes.MEDIUM_STRAIGHT.getLabel())||tile.getType().equals(TrackTypes.SMALL_STRAIGHT.getLabel())){
				this.placeCart(par2EntityPlayer, par1ItemStack, par3World, par4, par5, par6);
				return true;
			}
			par2EntityPlayer.addChatMessage(new ChatComponentText("Place me on a straight piece of track !"));
			return false;
		}else
		if(tileentity!=null && tileentity instanceof TileTCRailGag){
			TileTCRailGag tileGag = (TileTCRailGag) tileentity;
			if(tileGag!=null){
				TileTCRail tile = (TileTCRail) par3World.getTileEntity(tileGag.originX, tileGag.originY, tileGag.originZ);
				if(tile!=null && tile.getType().equals(TrackTypes.MEDIUM_STRAIGHT.getLabel())){
					this.placeCart(par2EntityPlayer, par1ItemStack, par3World, par4, par5, par6);
					return true;
				}
				par2EntityPlayer.addChatMessage(new ChatComponentText("Place me on a straight piece of track !"));
			}
			return false;
		}else
		if (BlockRailBase.func_150051_a(var11) && (meta < 2 || meta > 5)) {
			this.placeCart(par2EntityPlayer, par1ItemStack, par3World, par4, par5, par6);
			return true;
		}
		else {
			return false;
		}
	}

	public EntityMinecart placeCart(EntityPlayer player, ItemStack itemstack, World world, int i, int j, int k) {
		EntityRollingStock rollingStock = null;
		for(EnumTrains train : EnumTrains.values()){
			if(train.getItem() == itemstack.getItem()){
				rollingStock = (EntityRollingStock) train.getEntity(world, (float) i + 0.5F, (float) j + 0.5F, (float) k + 0.5F);
		    
				if(train.getColors()!=null){
					rollingStock.setColor(rollingStock.getColorFromString(train.getColors()[0]));
				}
			}
		}
		if (rollingStock != null && rollingStock instanceof EntityRollingStock) {
			if (!world.isRemote) {
				
				if((rollingStock instanceof SteamTrain && !ConfigHandler.ENABLE_STEAM) || (rollingStock instanceof ElectricTrain && !ConfigHandler.ENABLE_ELECTRIC) || (rollingStock instanceof DieselTrain && !ConfigHandler.ENABLE_DIESEL) || (rollingStock instanceof EntityTracksBuilder && !ConfigHandler.ENABLE_BUILDER) ||(rollingStock instanceof Tender && !ConfigHandler.ENABLE_TENDER)){
					if(player != null)player.addChatMessage(new ChatComponentText("This type of train has been deactivated by the OP"));
					rollingStock.setDead();
					return rollingStock;
				}
				
				int dir = 0;
				int meta = world.getBlockMetadata(i, j, k);
				//System.out.println(meta);
				if (player != null)
					dir = MathHelper.floor_double((double) ((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
				//180 = 3 = EAST 
				//0 = 0 = SOUTH
				//90 = 1 = WEST
				// -180 = 2 = NORTH
				//System.out.println(meta + " " +dir);
				if (dir == 2) {
					rollingStock.rotationYaw = 0;
					if(meta==0){
						rollingStock.serverRealRotation = -90;
					}else{
						rollingStock.serverRealRotation = 180;
					}
					if(world.getBlock(i, j, k)==BlockData.tcRail.block || world.getBlock(i, j, k)==BlockData.tcRailGag.block){
						if(meta==0 || meta == 2){
							rollingStock.rotationYaw = 90;
						}else{
							rollingStock.rotationYaw = 0;
						}
					}
					if(rollingStock instanceof Locomotive){
						if((world.getBlock(i, j, k-1)==BlockData.tcRail.block || world.getBlock(i, j, k-1)==BlockData.tcRailGag.block || BlockRailBase.func_150051_a(world.getBlock(i, j, k-1)))&&(world.getBlock(i, j, k-2)==BlockData.tcRail.block || world.getBlock(i, j, k-2)==BlockData.tcRailGag.block || BlockRailBase.func_150051_a(world.getBlock(i, j, k-2)))){
							if(meta==0 || meta == 2){
								rollingStock.serverRealRotation = -90;
							}
						}else{
							player.addChatMessage(new ChatComponentText("Place me on a straight piece of track!"));
							rollingStock.setDead();
							return rollingStock;
						}
					}
				}
				if (dir == 1) {
					rollingStock.rotationYaw = 90;
					if(meta==1){
						rollingStock.serverRealRotation = 180;
					}else{
						rollingStock.serverRealRotation = 90;
					}
					if(world.getBlock(i, j, k)==BlockData.tcRail.block || world.getBlock(i, j, k)==BlockData.tcRailGag.block){
						if(meta==1 || meta == 3){
							rollingStock.rotationYaw = 0;
						}else{
							rollingStock.rotationYaw = 90;
						}
					}
					if(rollingStock instanceof Locomotive){
						if((world.getBlock(i-1, j, k)==BlockData.tcRail.block || world.getBlock(i-1, j, k)==BlockData.tcRailGag.block || BlockRailBase.func_150051_a(world.getBlock(i-1, j, k)))&&(world.getBlock(i-2, j, k)==BlockData.tcRail.block || world.getBlock(i-2, j, k)==BlockData.tcRailGag.block || BlockRailBase.func_150051_a(world.getBlock(i-2, j, k)))){
							if(meta==1 || meta == 3){
								rollingStock.serverRealRotation = 180;
							}
						}else{
							player.addChatMessage(new ChatComponentText("Place me on a straight piece of track!"));
							rollingStock.setDead();
							return rollingStock;
						}
					}
				}
				if (dir == 0) {
					rollingStock.rotationYaw = -180;
					if(meta == 0){
						rollingStock.serverRealRotation = 90;
					}else{
						rollingStock.serverRealRotation = 180;
					}
					if(world.getBlock(i, j, k)==BlockData.tcRail.block || world.getBlock(i, j, k)==BlockData.tcRailGag.block){
						if(meta==0 || meta == 2){
							rollingStock.rotationYaw = -90;
						}else{
							rollingStock.rotationYaw = 0;
						}
					}
					if(rollingStock instanceof Locomotive){
						if((world.getBlock(i, j, k+1)==BlockData.tcRail.block || world.getBlock(i, j, k+1)==BlockData.tcRailGag.block || BlockRailBase.func_150051_a(world.getBlock(i, j, k+1)))&&(world.getBlock(i, j, k+2)==BlockData.tcRail.block || world.getBlock(i, j, k+2)==BlockData.tcRailGag.block || BlockRailBase.func_150051_a(world.getBlock(i, j, k+2)))){
							if(meta == 0 || meta == 2){
								rollingStock.serverRealRotation = 90;
							}
						}else{
							player.addChatMessage(new ChatComponentText("Place me on a straight piece of track!"));
							rollingStock.setDead();
							return rollingStock;
						}
					}
				}
				if (dir == 3) {
					rollingStock.rotationYaw = 180;
					if(meta==1){
						rollingStock.serverRealRotation = 0;
					}else{
						rollingStock.serverRealRotation = 90;
					}
					if(world.getBlock(i, j, k)==BlockData.tcRail.block || world.getBlock(i, j, k)==BlockData.tcRailGag.block){
						if(meta==1 || meta == 3){
							rollingStock.rotationYaw = 180;
						}else{
							rollingStock.rotationYaw = 90;
						}
					}
					if(rollingStock instanceof Locomotive){
						if((world.getBlock(i+1, j, k)==BlockData.tcRail.block || world.getBlock(i+1, j, k)==BlockData.tcRailGag.block || BlockRailBase.func_150051_a(world.getBlock(i+1, j, k)))&&(world.getBlock(i+2, j, k)==BlockData.tcRail.block || world.getBlock(i+2, j, k)==BlockData.tcRailGag.block || BlockRailBase.func_150051_a(world.getBlock(i+2, j, k)))){
							if(meta == 1 || meta == 3){
								rollingStock.serverRealRotation = 0;
							}
						}else{
							player.addChatMessage(new ChatComponentText("Place me on a straight piece of track!"));
							rollingStock.setDead();
							return rollingStock;
						}
					}
				}

				rollingStock.trainType = ((ItemRollingStock) itemstack.getItem()).getTrainType();
				rollingStock.trainName = ((ItemRollingStock) itemstack.getItem()).getItemStackDisplayName(itemstack);
				if (player != null){
					rollingStock.trainOwner = player.getGameProfile().getId();
					CartTools.setCartOwner(rollingStock, player);//set the owner for RC compatibility
				}
				rollingStock.mass = getMass();

				int uniID = -1;
				if (itemstack.hasTagCompound()) {
					NBTTagCompound var5 = itemstack.getTagCompound();
					uniID = var5.getInteger("uniqueID");
					if (uniID != -1)
						rollingStock.uniqueID = uniID;
					if (uniID != -1)
						rollingStock.getEntityData().setInteger("uniqueID", uniID);
					trainCreator = var5.getString("trainCreator");
					trainColor = var5.getInteger("trainColor");
					if (var5.hasKey("trainColor"))
						rollingStock.setColor(trainColor);
					rollingStock.trainCreator = UUID.fromString(trainCreator);
				}
				if (player != null)
					rollingStock.setInformation(((ItemRollingStock) itemstack.getItem()).getTrainType(), player.getGameProfile().getId(), UUID.fromString(trainCreator), ((ItemRollingStock) itemstack.getItem()).getItemStackDisplayName(itemstack), uniID);
				if (player == null)
					rollingStock.setInformation(((ItemRollingStock) itemstack.getItem()).getTrainType(), UUID.fromString("000000000000-0000-0000-0000-00000000"), UUID.fromString(trainCreator), ((ItemRollingStock) itemstack.getItem()).getItemStackDisplayName(itemstack), uniID);

				if (ConfigHandler.SHOW_POSSIBLE_COLORS && rollingStock.acceptedColors != null && rollingStock.acceptedColors.size() > 0) {
					String concatColors = ": ";
					for (int t = 0; t < rollingStock.acceptedColors.size(); t++) {
						if (!rollingStock.getColorAsString(rollingStock.acceptedColors.get(t)).equals("Empty") && !rollingStock.getColorAsString(rollingStock.acceptedColors.get(t)).equals("Full"))
							concatColors = concatColors.concat(rollingStock.getColorAsString(rollingStock.acceptedColors.get(t)) + ", ");
					}
					if (concatColors.length() > 4) {
						if (player != null) {
							player.addChatMessage(new ChatComponentText("Possible colors" + concatColors));
							player.addChatMessage(new ChatComponentText("To paint, click me with the right (vanilla) dye"));
						}
					}
				}
				world.spawnEntityInWorld(rollingStock);
				if (player != null)
					statsEvent.trainPlace(rollingStock.getUniqueTrainID(), ((ItemRollingStock) itemstack.getItem()).getItemStackDisplayName(itemstack), ((ItemRollingStock) itemstack.getItem()).getTrainType(), UUID.fromString(trainCreator), player.getGameProfile().getId(), new String((int) i + ";" + (int) j + ";" + (int) k));
			}
			--itemstack.stackSize;
		}
		return rollingStock;
	}

	@Override
	public boolean canBePlacedByNonPlayer(ItemStack cart) {
		return true; 
	}
	  
	 @Override
	 public EntityMinecart placeCart(GameProfile owner, ItemStack cart, World world, int i, int j, int k) { 
		 return placeCart((EntityPlayer) null, cart, world, i, j, k);
	}

	@Override
	public boolean doesCartMatchFilter(ItemStack stack, EntityMinecart cart) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(Info.modID.toLowerCase() + ":trains/" + iconName);
	}
}
