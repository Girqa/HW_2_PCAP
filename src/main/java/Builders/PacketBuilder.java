package Builders;

import org.pcap4j.util.Inet4NetworkAddress;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PacketBuilder {
    private DatagramPacket packet;
    private int bufferSize = 128;
    private String localHost = "127.0.0.1";

    public PacketBuilder() {
        packet = new DatagramPacket(new byte[bufferSize], bufferSize);
    }

    public PacketBuilder setAddress(String address){
        try {
            packet.setAddress(InetAddress.getByName(address));
        } catch (UnknownHostException e) {
            try {
                packet.setAddress(InetAddress.getByName(localHost));
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        }
        return this;
    }

    public PacketBuilder setPort(int port) {
        packet.setPort(port);
        return this;
    }

    public PacketBuilder setData(String data) {
        packet.setData(data.getBytes());
        return this;
    }

    public byte[] getPacket() {
        return packet.getData();
    }
}
