package WorkingClasses;

import AdditionalClasses.PacketHelper;
import AdditionalClasses.ParsingProvider;
import Factorys.AIDFactory;
import jade.core.AID;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.PacketListener;
import org.pcap4j.packet.Packet;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class MyPacketListener implements PacketListener {
    private Map<AID, Date> agents;
    private PacketHelper packetHelper;
    private AID agent;
    private List<Listener> subscribers;

    public MyPacketListener(Map<AID, Date> agents,
                            PacketHelper packetHelper,
                            AID agent,
                            List<Listener> subscribers) {
        this.agents = agents;
        this.packetHelper = packetHelper;
        this.agent = agent;
        this.subscribers = subscribers;
    }

    @Override
    public void gotPacket(Packet packet) {
        if (packet != null) {
            AID receivedAID = parsePacket(packet);
            if (!receivedAID.equals(agent)) {
                if (!agents.containsKey(receivedAID)) {
                    informSubscribers("Added: " + receivedAID.toString());
                }
                agents.put(receivedAID, new Date());
            }
            log.trace("Agent {} got AID {}", agent.getName(), receivedAID.getName());
        }
    }

    private AID parsePacket(Packet packet) {
        byte[] data = packet.getRawData();
        String jsonContent = packetHelper.parse(data);
        return AIDFactory.createAID(ParsingProvider.fromJson(jsonContent, String.class));
    }

    private void informSubscribers(String msg) {
        for (Listener subscriber: subscribers) {
            subscriber.listen(msg);
        }
    }
}
