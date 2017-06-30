package io.github.skepter.forgedetector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_11_R1.PacketDataSerializer;
import net.minecraft.server.v1_11_R1.PacketPlayOutCustomPayload;

public class PacketSender {

	public static void sendCustomPayloadPacket(Player player, String channel, ByteBuf bytebuf) {
		try {
			
			PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload(channel, new PacketDataSerializer(bytebuf));
			
			//Use Reflection to get the packetdataserialiser stuff
			Field field = packet.getClass().getDeclaredField("b");
			field.setAccessible(true);
			PacketDataSerializer serializer = (PacketDataSerializer) field.get(packet);
			
			PacketContainer cPacket = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
			cPacket.getStrings().write(0, channel);
			cPacket.getModifier().write(1, serializer);
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, cPacket);
			
		} catch (InvocationTargetException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendRegisterPacket(Player player) {
		Bukkit.getLogger().info("Sending register packet");
		
		ByteBuf bytebuf = Unpooled.buffer();
		bytebuf.writeBytes("FML|HS FML FML|MP FML FORGE".getBytes());
		sendCustomPayloadPacket(player, "REGISTER", bytebuf);

		Bukkit.getLogger().info("Register packet sent!");
	}
	
	public static void sendServerHelloPacket(Player player){
		Bukkit.getLogger().info("Sending server hello packet...");
		
		ByteBuf bytebuf = Unpooled.wrappedBuffer(new byte[] {0, 2, 0, 0, 0, 0});
		sendCustomPayloadPacket(player, "FML|HS", bytebuf);
		
		Bukkit.getLogger().info("Server hello packet sent!");
	}
}