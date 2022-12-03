package AdditionalClasses;

import lombok.SneakyThrows;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class PacketHelper {
    private String iface;

    public PacketHelper(String interfaceName) {
        this.iface = interfaceName;
    }

    /**
     * parses raw packet data into string value of payload
     * @param data - all data from UDP packet
     * @return string value of payload
     */
    public String parse(byte[] data){
        if (data.length < 14) return null;
        int offset = (iface.equals("\\Device\\NPF_Loopback") ? 4 /*local*/ : 14 /*ethernet*/) + 20 /*ipv4*/ + 8 /* udp */;

        byte[]dataByte = new byte[data.length-offset];
        System.arraycopy(data,offset,dataByte,0,dataByte.length);
        return new String(dataByte);
    }

    /**
     * creates packet data (byte array) of given String value
     * @param data - string value to decode into packet
     * @param portToSend - port to send data vid UDP
     * @return byte arr of packet to send vid UDP
     */
    @SneakyThrows
    public byte[] collectPacket(String data, int portToSend) {
        byte[] platformDescBytes = data.getBytes();
        int dataLength = platformDescBytes.length;
        int totalLength = dataLength + 28;

        byte [] packet = new byte[totalLength + (iface.equals("\\Device\\NPF_Loopback") ? 4 : 14)];
        /* send to all*/
        byte[] ipDestinationBytes = InetAddress.getByName("255.255.255.255").getAddress();
        byte[] ipSourceBytes = InetAddress.getByName("127.0.0.1").getAddress();
//        byte[] ipSourceBytes = InetAddress.getByName(ipSource).getAddress();
        int addCount = 0;

        /* set NPF_Loopback as iface to use. WORKS ONLY FOR WINDOWS*/
        for (int i = 0, j = 7; i < 1; i++, j++) packet[i] = longToBytes(0x02)[j];

        //Header Length = 20 bytes
        for (int i = 4 + addCount, j = 7; i < 5 + addCount; i++, j++) packet[i] = longToBytes(69)[j];
        //Differentiated Services Field
        for (int i = 5 + addCount, j = 7; i < 6 + addCount; i++, j++) packet[i] = longToBytes(0x00)[j];
        //Total Length
        for (int i = 6 + addCount, j = 6; i < 8 + addCount; i++, j++) packet[i] = longToBytes(totalLength)[j];
        //Identification - for fragmented packages
        for (int i = 8 + addCount, j = 6; i < 10 + addCount; i++, j++) packet[i] = longToBytes(33500)[j];
        //Flag, Fragment Offset - for fragmented packages
        for (int i = 10 + addCount, j = 6; i < 12 + addCount; i++, j++) packet[i] = longToBytes(0x00)[j];
        //Time to Live - max limit for moving through the network
        for (int i = 12 + addCount, j = 7; i < 13 + addCount; i++, j++) packet[i] = longToBytes(128)[j];
        //Protocol - UDP
        for (int i = 13 + addCount, j = 7; i < 14 + addCount; i++, j++) packet[i] = longToBytes(17)[j];
        //Header Checksum, can be 0x00 if it is not calculated
        for (int i = 14 + addCount, j = 6; i < 16 + addCount; i++, j++) packet[i] = longToBytes(0x00)[j];

        for (int i = 16 + addCount, j = 0; i < 20 + addCount; i++, j++) packet[i] = ipSourceBytes[j];
        for (int i = 20 + addCount, j = 0; i < 24 + addCount; i++, j++) packet[i] = ipDestinationBytes[j];
        //Source port
        for (int i = 24 + addCount, j = 6; i < 26 + addCount; i++, j++) packet[i] = longToBytes(portToSend)[j];
        //Destination port
        for (int i = 26 + addCount, j = 6; i < 28 + addCount; i++, j++) packet[i] = longToBytes(portToSend)[j];
        //Length
        int length = totalLength - 20;
        for (int i = 28 + addCount, j = 6; i < 30 + addCount; i++, j++) packet[i] = longToBytes(length)[j];
        //Checksum, can be 0x00 if it is not calculated
        for (int i = 30 + addCount, j = 6; i < 32 + addCount; i++, j++) packet[i] = longToBytes(0x0000)[j];
        //Data
        System.arraycopy(platformDescBytes, 0, packet, 32 + addCount, platformDescBytes.length);
        return packet;
    }


    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

}
