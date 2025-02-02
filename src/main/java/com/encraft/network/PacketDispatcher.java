package com.encraft.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.encraft.dz.lib.Tags;
import com.encraft.network.client.SyncPlayerPropsMessage;
import com.encraft.network.server.OpenGuiMessage;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketDispatcher
{
	// a simple counter will allow us to get rid of 'magic' numbers used during packet registration
	private static byte packetId = 0;

	/**
	 * The SimpleNetworkWrapper instance is used both to register and send packets.
	 * Since I will be adding wrapper methods, this field is private, but you should
	 * make it public if you plan on using it directly.
	 */
	private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);

	/**
	 * Call this during pre-init or loading and register all of your packets (messages) here
	 */
	public static final void registerPackets() {
		// Packets handled on CLIENT
		registerMessage(SyncPlayerPropsMessage.class);

		// Packets handled on SERVER
		registerMessage(OpenGuiMessage.class);

		// If you don't want to make a 'registerMessage' method, you can do it directly:
		//PacketDispatcher.dispatcher.registerMessage(SyncPlayerPropsMessage.class, SyncPlayerPropsMessage.class, packetId++, Side.CLIENT);
		//PacketDispatcher.dispatcher.registerMessage(OpenGuiMessage.class, OpenGuiMessage.class, packetId++, Side.SERVER);

		// The following two packets are not used in this demo, but have been used in my other mods
		// I include them here simply for the sake of demonstrating packets that can be sent to both sides

		// Bidirectional packets:
		//registerMessage(PlaySoundPacket.class);
		//registerMessage(AttackTimePacket.class);
	}

	/**
	 * Registers an {@link AbstractMessage} to the appropriate side(s)
	 */
	private static final <T extends AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerMessage(Class<T> clazz) {
		// We can tell by the message class which side to register it on by using #isAssignableFrom (google it)

		// Also, one can see the convenience of using a static counter 'packetId' to keep
		// track of the current index, rather than hard-coding them all, plus it's one less
		// parameter to pass.
		if (AbstractMessage.AbstractClientMessage.class.isAssignableFrom(clazz)) {
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId++, Side.CLIENT);
		} else if (AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
		} else {
			// hopefully you didn't forget to extend the right class, or you will get registered on both sides
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId, Side.CLIENT);
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
		}
	}

	//========================================================//
	// The following methods are the 'wrapper' methods; again,
	// this just makes sending a message slightly more compact
	// and is purely a matter of stylistic preference
	//========================================================//

	/**
	 * Send this message to the specified player's client-side counterpart.
	 * See {@link SimpleNetworkWrapper#sendTo(IMessage, EntityPlayerMP)}
	 */
	public static final void sendTo(IMessage message, EntityPlayerMP player) {
		PacketDispatcher.dispatcher.sendTo(message, player);
	}

	/**
	 * Send this message to everyone.
	 * See {@link SimpleNetworkWrapper#sendToAll(IMessage)}
	 */
	public static void sendToAll(IMessage message) {
		PacketDispatcher.dispatcher.sendToAll(message);
	}

	/**
	 * Send this message to everyone within a certain range of a point.
	 * See {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static final void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		PacketDispatcher.dispatcher.sendToAllAround(message, point);
	}

	/**
	 * Sends a message to everyone within a certain range of the coordinates in the same dimension.
	 * Shortcut to {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static final void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
		PacketDispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
	}

	/**
	 * Sends a message to everyone within a certain range of the player provided.
	 * Shortcut to {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
		PacketDispatcher.sendToAllAround(message, player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, range);
	}

	/**
	 * Send this message to everyone within the supplied dimension.
	 * See {@link SimpleNetworkWrapper#sendToDimension(IMessage, int)}
	 */
	public static final void sendToDimension(IMessage message, int dimensionId) {
		PacketDispatcher.dispatcher.sendToDimension(message, dimensionId);
	}

	/**
	 * Send this message to the server.
	 * See {@link SimpleNetworkWrapper#sendToServer(IMessage)}
	 */
	public static final void sendToServer(IMessage message) {
		PacketDispatcher.dispatcher.sendToServer(message);
	}
}
