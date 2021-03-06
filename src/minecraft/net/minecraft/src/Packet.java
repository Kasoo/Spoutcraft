package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.IntHashMap;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet0KeepAlive;
import net.minecraft.src.Packet100OpenWindow;
import net.minecraft.src.Packet101CloseWindow;
import net.minecraft.src.Packet102WindowClick;
import net.minecraft.src.Packet103SetSlot;
import net.minecraft.src.Packet104WindowItems;
import net.minecraft.src.Packet105UpdateProgressbar;
import net.minecraft.src.Packet106Transaction;
import net.minecraft.src.Packet107CreativeSetSlot;
import net.minecraft.src.Packet108EnchantItem;
import net.minecraft.src.Packet10Flying;
import net.minecraft.src.Packet11PlayerPosition;
import net.minecraft.src.Packet12PlayerLook;
import net.minecraft.src.Packet130UpdateSign;
import net.minecraft.src.Packet131MapData;
import net.minecraft.src.Packet132TileEntityData;
import net.minecraft.src.Packet13PlayerLookMove;
import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet15Place;
import net.minecraft.src.Packet16BlockItemSwitch;
import net.minecraft.src.Packet17Sleep;
import net.minecraft.src.Packet18Animation;
import net.minecraft.src.Packet19EntityAction;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet200Statistic;
import net.minecraft.src.Packet201PlayerInfo;
import net.minecraft.src.Packet20NamedEntitySpawn;
import net.minecraft.src.Packet21PickupSpawn;
import net.minecraft.src.Packet22Collect;
import net.minecraft.src.Packet23VehicleSpawn;
import net.minecraft.src.Packet24MobSpawn;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Packet254ServerPing;
import net.minecraft.src.Packet255KickDisconnect;
import net.minecraft.src.Packet25EntityPainting;
import net.minecraft.src.Packet26EntityExpOrb;
import net.minecraft.src.Packet28EntityVelocity;
import net.minecraft.src.Packet29DestroyEntity;
import net.minecraft.src.Packet2Handshake;
import net.minecraft.src.Packet30Entity;
import net.minecraft.src.Packet31RelEntityMove;
import net.minecraft.src.Packet32EntityLook;
import net.minecraft.src.Packet33RelEntityMoveLook;
import net.minecraft.src.Packet34EntityTeleport;
import net.minecraft.src.Packet35EntityHeadRotation;
import net.minecraft.src.Packet38EntityStatus;
import net.minecraft.src.Packet39AttachEntity;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.Packet40EntityMetadata;
import net.minecraft.src.Packet41EntityEffect;
import net.minecraft.src.Packet42RemoveEntityEffect;
import net.minecraft.src.Packet43Experience;
import net.minecraft.src.Packet4UpdateTime;
import net.minecraft.src.Packet50PreChunk;
import net.minecraft.src.Packet51MapChunk;
import net.minecraft.src.Packet52MultiBlockChange;
import net.minecraft.src.Packet53BlockChange;
import net.minecraft.src.Packet54PlayNoteBlock;
import net.minecraft.src.Packet5PlayerInventory;
import net.minecraft.src.Packet60Explosion;
import net.minecraft.src.Packet61DoorChange;
import net.minecraft.src.Packet6SpawnPosition;
import net.minecraft.src.Packet70Bed;
import net.minecraft.src.Packet71Weather;
import net.minecraft.src.Packet7UseEntity;
import net.minecraft.src.Packet8UpdateHealth;
import net.minecraft.src.Packet9Respawn;
import net.minecraft.src.PacketCount;

public abstract class Packet {

	public static IntHashMap packetIdToClassMap = new IntHashMap();
	private static Map packetClassToIdMap = new HashMap();
	private static Set clientPacketIdList = new HashSet();
	private static Set serverPacketIdList = new HashSet();
	public final long creationTimeMillis = System.currentTimeMillis();
	public static long field_48158_m;
	public static long field_48156_n;
	public static long field_48157_o;
	public static long field_48155_p;
	public boolean isChunkDataPacket = false;

	public static void addIdClassMapping(int par0, boolean par1, boolean par2, Class par3Class) { // Spout default -> public
		if (packetIdToClassMap.containsItem(par0)) {
			throw new IllegalArgumentException("Duplicate packet id:" + par0);
		} else if (packetClassToIdMap.containsKey(par3Class)) {
			throw new IllegalArgumentException("Duplicate packet class:" + par3Class);
		} else {
			packetIdToClassMap.addKey(par0, par3Class);
			packetClassToIdMap.put(par3Class, Integer.valueOf(par0));
			if (par1) {
				clientPacketIdList.add(Integer.valueOf(par0));
			}

			if (par2) {
				serverPacketIdList.add(Integer.valueOf(par0));
			}

		}
	}

	public static Packet getNewPacket(int par0) {
		try {
			Class var1 = (Class)packetIdToClassMap.lookup(par0);
			return var1 == null?null:(Packet)var1.newInstance();
		} catch (Exception var2) {
			var2.printStackTrace();
			System.out.println("Skipping packet with id " + par0);
			return null;
		}
	}

	public final int getPacketId() {
		return ((Integer)packetClassToIdMap.get(this.getClass())).intValue();
	}

	public static Packet readPacket(DataInputStream par0DataInputStream, boolean par1) throws IOException {
		boolean var2 = false;
		Packet var3 = null;

		int var6;
		try {
			var6 = par0DataInputStream.read();
			if (var6 == -1) {
				return null;
			}

			if (par1 && !serverPacketIdList.contains(Integer.valueOf(var6)) || !par1 && !clientPacketIdList.contains(Integer.valueOf(var6))) {
				throw new IOException("Bad packet id " + var6);
			}

			var3 = getNewPacket(var6);
			if (var3 == null) {
				throw new IOException("Bad packet id " + var6);
			}

			var3.readPacketData(par0DataInputStream);
			++field_48158_m;
			field_48156_n += (long)var3.getPacketSize();
		} catch (EOFException var5) {
			System.out.println("Reached end of stream");
			return null;
		}

		PacketCount.countPacket(var6, (long)var3.getPacketSize());
		++field_48158_m;
		field_48156_n += (long)var3.getPacketSize();
		return var3;
	}

	public static void writePacket(Packet par0Packet, DataOutputStream par1DataOutputStream) throws IOException {
		par1DataOutputStream.write(par0Packet.getPacketId());
		par0Packet.writePacketData(par1DataOutputStream);
		++field_48157_o;
		field_48155_p += (long)par0Packet.getPacketSize();
	}

	public static void writeString(String par0Str, DataOutputStream par1DataOutputStream) throws IOException {
		if (par0Str.length() > 32767) {
			throw new IOException("String too big");
		} else {
			par1DataOutputStream.writeShort(par0Str.length());
			par1DataOutputStream.writeChars(par0Str);
		}
	}

	public static String readString(DataInputStream par0DataInputStream, int par1) throws IOException {
		short var2 = par0DataInputStream.readShort();
		if (var2 > par1) {
			throw new IOException("Received string length longer than maximum allowed (" + var2 + " > " + par1 + ")");
		} else if (var2 < 0) {
			throw new IOException("Received string length is less than zero! Weird string!");
		} else {
			StringBuilder var3 = new StringBuilder();

			for (int var4 = 0; var4 < var2; ++var4) {
				var3.append(par0DataInputStream.readChar());
			}

			return var3.toString();
		}
	}

	public abstract void readPacketData(DataInputStream var1) throws IOException;

	public abstract void writePacketData(DataOutputStream var1) throws IOException;

	public abstract void processPacket(NetHandler var1);

	public abstract int getPacketSize();

	protected ItemStack readItemStack(DataInputStream par1DataInputStream) throws IOException {
		ItemStack var2 = null;
		short var3 = par1DataInputStream.readShort();
		if (var3 >= 0) {
			byte var4 = par1DataInputStream.readByte();
			short var5 = par1DataInputStream.readShort();
			var2 = new ItemStack(var3, var4, var5);
			if (Item.itemsList[var3].isDamageable() || Item.itemsList[var3].func_46056_k()) {
				var2.stackTagCompound = this.readNBTTagCompound(par1DataInputStream);
			}
		}

		return var2;
	}

	protected void writeItemStack(ItemStack par1ItemStack, DataOutputStream par2DataOutputStream) throws IOException {
		if (par1ItemStack == null) {
			par2DataOutputStream.writeShort(-1);
		} else {
			par2DataOutputStream.writeShort(par1ItemStack.itemID);
			par2DataOutputStream.writeByte(par1ItemStack.stackSize);
			par2DataOutputStream.writeShort(par1ItemStack.getItemDamage());
			if (par1ItemStack.getItem().isDamageable() || par1ItemStack.getItem().func_46056_k()) {
				this.writeNBTTagCompound(par1ItemStack.stackTagCompound, par2DataOutputStream);
			}
		}

	}

	protected NBTTagCompound readNBTTagCompound(DataInputStream par1DataInputStream) throws IOException {
		short var2 = par1DataInputStream.readShort();
		if (var2 < 0) {
			return null;
		} else {
			byte[] var3 = new byte[var2];
			par1DataInputStream.readFully(var3);
			return CompressedStreamTools.decompress(var3);
		}
	}

	protected void writeNBTTagCompound(NBTTagCompound par1NBTTagCompound, DataOutputStream par2DataOutputStream) throws IOException {
		if (par1NBTTagCompound == null) {
			par2DataOutputStream.writeShort(-1);
		} else {
			byte[] var3 = CompressedStreamTools.compress(par1NBTTagCompound);
			par2DataOutputStream.writeShort((short)var3.length);
			par2DataOutputStream.write(var3);
		}

	}

	static {
		addIdClassMapping(0, true, true, Packet0KeepAlive.class);
		addIdClassMapping(1, true, true, Packet1Login.class);
		addIdClassMapping(2, true, true, Packet2Handshake.class);
		addIdClassMapping(3, true, true, Packet3Chat.class);
		addIdClassMapping(4, true, false, Packet4UpdateTime.class);
		addIdClassMapping(5, true, false, Packet5PlayerInventory.class);
		addIdClassMapping(6, true, false, Packet6SpawnPosition.class);
		addIdClassMapping(7, false, true, Packet7UseEntity.class);
		addIdClassMapping(8, true, false, Packet8UpdateHealth.class);
		addIdClassMapping(9, true, true, Packet9Respawn.class);
		addIdClassMapping(10, true, true, Packet10Flying.class);
		addIdClassMapping(11, true, true, Packet11PlayerPosition.class);
		addIdClassMapping(12, true, true, Packet12PlayerLook.class);
		addIdClassMapping(13, true, true, Packet13PlayerLookMove.class);
		addIdClassMapping(14, false, true, Packet14BlockDig.class);
		addIdClassMapping(15, false, true, Packet15Place.class);
		addIdClassMapping(16, false, true, Packet16BlockItemSwitch.class);
		addIdClassMapping(17, true, false, Packet17Sleep.class);
		addIdClassMapping(18, true, true, Packet18Animation.class);
		addIdClassMapping(19, false, true, Packet19EntityAction.class);
		addIdClassMapping(20, true, false, Packet20NamedEntitySpawn.class);
		addIdClassMapping(21, true, false, Packet21PickupSpawn.class);
		addIdClassMapping(22, true, false, Packet22Collect.class);
		addIdClassMapping(23, true, false, Packet23VehicleSpawn.class);
		addIdClassMapping(24, true, false, Packet24MobSpawn.class);
		addIdClassMapping(25, true, false, Packet25EntityPainting.class);
		addIdClassMapping(26, true, false, Packet26EntityExpOrb.class);
		addIdClassMapping(28, true, false, Packet28EntityVelocity.class);
		addIdClassMapping(29, true, false, Packet29DestroyEntity.class);
		addIdClassMapping(30, true, false, Packet30Entity.class);
		addIdClassMapping(31, true, false, Packet31RelEntityMove.class);
		addIdClassMapping(32, true, false, Packet32EntityLook.class);
		addIdClassMapping(33, true, false, Packet33RelEntityMoveLook.class);
		addIdClassMapping(34, true, false, Packet34EntityTeleport.class);
		addIdClassMapping(35, true, false, Packet35EntityHeadRotation.class);
		addIdClassMapping(38, true, false, Packet38EntityStatus.class);
		addIdClassMapping(39, true, false, Packet39AttachEntity.class);
		addIdClassMapping(40, true, false, Packet40EntityMetadata.class);
		addIdClassMapping(41, true, false, Packet41EntityEffect.class);
		addIdClassMapping(42, true, false, Packet42RemoveEntityEffect.class);
		addIdClassMapping(43, true, false, Packet43Experience.class);
		addIdClassMapping(50, true, false, Packet50PreChunk.class);
		addIdClassMapping(51, true, false, Packet51MapChunk.class);
		addIdClassMapping(52, true, false, Packet52MultiBlockChange.class);
		addIdClassMapping(53, true, false, Packet53BlockChange.class);
		addIdClassMapping(54, true, false, Packet54PlayNoteBlock.class);
		addIdClassMapping(60, true, false, Packet60Explosion.class);
		addIdClassMapping(61, true, false, Packet61DoorChange.class);
		addIdClassMapping(70, true, false, Packet70Bed.class);
		addIdClassMapping(71, true, false, Packet71Weather.class);
		addIdClassMapping(100, true, false, Packet100OpenWindow.class);
		addIdClassMapping(101, true, true, Packet101CloseWindow.class);
		addIdClassMapping(102, false, true, Packet102WindowClick.class);
		addIdClassMapping(103, true, false, Packet103SetSlot.class);
		addIdClassMapping(104, true, false, Packet104WindowItems.class);
		addIdClassMapping(105, true, false, Packet105UpdateProgressbar.class);
		addIdClassMapping(106, true, true, Packet106Transaction.class);
		addIdClassMapping(107, true, true, Packet107CreativeSetSlot.class);
		addIdClassMapping(108, false, true, Packet108EnchantItem.class);
		addIdClassMapping(130, true, true, Packet130UpdateSign.class);
		addIdClassMapping(131, true, false, Packet131MapData.class);
		addIdClassMapping(132, true, false, Packet132TileEntityData.class);
		addIdClassMapping(200, true, false, Packet200Statistic.class);
		addIdClassMapping(201, true, false, Packet201PlayerInfo.class);
		addIdClassMapping(250, true, true, Packet250CustomPayload.class);
		addIdClassMapping(254, false, true, Packet254ServerPing.class);
		addIdClassMapping(255, true, true, Packet255KickDisconnect.class);
	}
}
