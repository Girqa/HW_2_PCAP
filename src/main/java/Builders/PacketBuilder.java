package Builders;

import AdditionalClasses.PacketHelper;
import lombok.Setter;
import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class PacketBuilder {
    private PacketHelper helper = new PacketHelper("\\Device\\NPF_Loopback");
    byte[] ipDestinationBytes;
    byte[] ipSourceBytes;
    byte[] data;
    int port;

    @SneakyThrows
    public PacketBuilder() {
        ipDestinationBytes = InetAddress.getByName("255.255.255.255").getAddress();
        ipSourceBytes = InetAddress.getByName("127.0.0.1").getAddress();
    }
    @SneakyThrows
    public PacketBuilder setDestinationIP(String destinationIP) {
        ipDestinationBytes = InetAddress.getByName(destinationIP).getAddress();
        return this;
    }

    @SneakyThrows
    public PacketBuilder setSourceIP(String sourceIP) {
        ipSourceBytes = InetAddress.getByName(sourceIP).getAddress();
        return this;
    }
    public PacketBuilder setData(String data) {
        this.data = data.getBytes();
        return this;
    }

    public PacketBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public byte[] getPacket() {
        int totalLength = data.length + 28;
        byte [] packet = new byte[totalLength + 4];
        for (int i = 0, j = 7; i < 1; i++, j++) {
            packet[i] = longToBytes(0x02)[j];
        }
        //Header Length = 20 bytes
        for (int i = 4, j = 7; i < 5; i++, j++) packet[i] = longToBytes(69)[j];
        //Differentiated Services Field
        for (int i = 5 , j = 7; i < 6; i++, j++) packet[i] = longToBytes(0x00)[j];
        //Total Length
        for (int i = 6, j = 6; i < 8; i++, j++) packet[i] = longToBytes(totalLength)[j];
        //Identification - for fragmented packages
        for (int i = 8, j = 6; i < 10; i++, j++) packet[i] = longToBytes(33500)[j];
        //Flag, Fragment Offset - for fragmented packages
        for (int i = 10, j = 6; i < 12; i++, j++) packet[i] = longToBytes(0x00)[j];
        //Time to Live - max limit for moving through the network
        for (int i = 12, j = 7; i < 13; i++, j++) packet[i] = longToBytes(128)[j];
        //Protocol - UDP
        for (int i = 13 , j = 7; i < 14; i++, j++) packet[i] = longToBytes(17)[j];
        //Header Checksum, can be 0x00 if it is not calculated
        for (int i = 14, j = 6; i < 16; i++, j++) packet[i] = longToBytes(0x00)[j];

        for (int i = 16, j = 0; i < 20; i++, j++) packet[i] = ipSourceBytes[j];
        for (int i = 20, j = 0; i < 24; i++, j++) packet[i] = ipDestinationBytes[j];
        //Source port
        for (int i = 24, j = 6; i < 26; i++, j++) packet[i] = longToBytes(port)[j];
        //Destination port
        for (int i = 26, j = 6; i < 28; i++, j++) packet[i] = longToBytes(port)[j];
        //Length
        int length = totalLength - 20;
        for (int i = 28, j = 6; i < 30; i++, j++) packet[i] = longToBytes(length)[j];
        //Checksum, can be 0x00 if it is not calculated
        for (int i = 30, j = 6; i < 32; i++, j++) packet[i] = longToBytes(0x0000)[j];
        //Data
        System.arraycopy(data, 0, packet, 32, data.length);
        return packet;
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
}
