package com.GenZVirus.CursedBlade.Common.Network.Packets;

import java.util.function.Supplier;

import com.GenZVirus.CursedBlade.CursedBlade;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class SendCursedPlayerData {

	public int kill_counter;
	public int attack_damage;
	public int life_steal;
	public boolean destroy_absorption;
	public boolean destroy_shields;
	public boolean fire_aspect;
	public int poison;
	public int wither;
	public String status;
	public int hunger;
	public int exhaust;

	public SendCursedPlayerData(int kill_counter, int attack_damage, int life_steal, boolean destroy_absorption, boolean destroy_shields, boolean fire_aspect, int poison, int wither, String status, int hunger, int exhaust) {
		this.kill_counter = kill_counter;
		this.attack_damage = attack_damage;
		this.life_steal = life_steal;
		this.destroy_absorption = destroy_absorption;
		this.destroy_shields = destroy_shields;
		this.fire_aspect = fire_aspect;
		this.poison = poison;
		this.wither = wither;
		this.status = status;
		this.hunger = hunger;
		this.exhaust = exhaust;
	}

	public static void encode(SendCursedPlayerData pkt, PacketBuffer buf) {
		buf.writeInt(pkt.kill_counter);
		buf.writeInt(pkt.attack_damage);
		buf.writeInt(pkt.life_steal);
		buf.writeBoolean(pkt.destroy_absorption);
		buf.writeBoolean(pkt.destroy_shields);
		buf.writeBoolean(pkt.fire_aspect);
		buf.writeInt(pkt.poison);
		buf.writeInt(pkt.wither);
		buf.writeString(pkt.status);
		buf.writeInt(pkt.hunger);
		buf.writeInt(pkt.exhaust);
	}

	public static SendCursedPlayerData decode(PacketBuffer buf) {
		return new SendCursedPlayerData(buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readInt(), buf.readInt(), buf.readString(), buf.readInt(), buf.readInt());
	}

	public static void handle(SendCursedPlayerData pkt, Supplier<NetworkEvent.Context> ctx) {

		ctx.get().enqueueWork(() -> {
			if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
				CursedBlade.KILL_COUNTER = pkt.kill_counter;
				CursedBlade.ATTACK_DAMAGE = pkt.attack_damage;
				CursedBlade.LIFE_STEAL = pkt.life_steal;
				CursedBlade.DESTROY_ABSORPTION = pkt.destroy_absorption;
				CursedBlade.DESTROY_SHIELDS = pkt.destroy_shields;
				CursedBlade.FIRE_ASPECT = pkt.fire_aspect;
				CursedBlade.POISON = pkt.poison;
				CursedBlade.WITHER = pkt.wither;
				CursedBlade.STATUS = pkt.status;
				CursedBlade.HUNGER = pkt.hunger;
				CursedBlade.EXHAUST = pkt.exhaust;
			}
		});
		ctx.get().setPacketHandled(true);
	}

}
