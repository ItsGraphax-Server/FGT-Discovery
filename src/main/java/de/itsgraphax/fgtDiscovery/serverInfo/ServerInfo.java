package de.itsgraphax.fgtDiscovery.serverInfo;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import de.itsgraphax.fgtDiscovery.util.PacketHelper;
import de.itsgraphax.fgtDiscovery.util.ServerConnectionData;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class ServerInfo {
    @SerializedName("description")
    private Motd motd;
    @SerializedName("max")
    private int maxPlayers;
    @SerializedName("online")
    private int players;
    @SerializedName("sample")
    private List<PlayerSample> sample;

    public Motd motd() {
        return motd;
    }

    public int maxPlayers() {
        return maxPlayers;
    }

    public Integer players() {
        return players;
    }

    public List<PlayerSample> sample() {
        return sample;
    }


    public static ServerInfo fromRawJson(String rawJson) {
        Type type = new TypeToken<@NotNull ServerInfo>() {
        }.getType();

        JsonObject root = JsonParser.parseString(rawJson).getAsJsonObject();
        JsonObject user = root.getAsJsonObject("players");
        for (String key : user.keySet()) {
            root.add(key, user.get(key));
        }

        return GsonComponentSerializer.gson().serializer().fromJson(
                root,
                type
        );
    }

    /**
     * requests "Server Info" with the "Status" packet from another server and returns it.
     * This is adapted from <a href="https://gist.github.com/zh32/7190955">here</a>
     * (see also <a href="https://minecraft.wiki/w/Java_Edition_protocol/Server_List_Ping">mc wiki link</a>
     *
     * @throws IOException if something with the connection doesn't work
     */
    public static ServerInfo request(ServerConnectionData connectionData) throws IOException {
        try (Socket socket = new Socket(connectionData.ip(), connectionData.port())) {
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inStream = new DataInputStream(socket.getInputStream());


            ByteArrayOutputStream handshakeBytes = new ByteArrayOutputStream();
            DataOutputStream handshakeStream = new DataOutputStream(handshakeBytes); // temp


            PacketHelper.writeVarInt(handshakeStream, 0x00);
            PacketHelper.writeVarInt(handshakeStream, 775);

            byte[] ipBytes = connectionData.ip().getBytes(StandardCharsets.UTF_8);
            PacketHelper.writeVarInt(handshakeStream, ipBytes.length);
            handshakeStream.write(ipBytes);

            handshakeStream.writeShort(connectionData.port());

            PacketHelper.writeVarInt(handshakeStream, 1);

            byte[] handshakePacket = handshakeBytes.toByteArray();
            PacketHelper.writeVarInt(outStream, handshakePacket.length);
            outStream.write(handshakePacket);

            PacketHelper.writeVarInt(outStream, 1);
            PacketHelper.writeVarInt(outStream, 0x00);

            /*int inPacktLength =*/
            PacketHelper.readVarInt(inStream);
            /*int inPacketId =*/
            PacketHelper.readVarInt(inStream);

            int inDataLength = PacketHelper.readVarInt(inStream);
            byte[] inDataBytes = new byte[inDataLength];
            inStream.readFully(inDataBytes);

            String inData = new String(inDataBytes);

            return fromRawJson(inData);
        }
    }
}
