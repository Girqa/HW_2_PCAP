package WorkingClasses;

import AdditionalClasses.PacketHelper;
import AdditionalClasses.ParsingProvider;
import AdditionalClasses.PcapHelper;
import Builders.PacketBuilder;
import Interfaces.Listener;
import jade.core.AID;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.PacketListener;
import org.pcap4j.packet.Packet;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class AgentDetector {
    @Setter
    private PcapHelper pcap;
    @Setter
    private PacketHelper packetHelper;
    @Setter
    private int communicationPort;
    @Setter
    private AID agent;
    @Setter
    private long deadAgentDelay;
    private final ScheduledExecutorService ses;
    private ScheduledFuture<?> discoveringTask;
    private ScheduledFuture<?> cleaningTask;
    private ScheduledFuture<?> sendingTask;
    private final Map<AID, Date> agents;
    private final List<Listener> subscribers = new ArrayList<>();
    private byte[] packet;

    public AgentDetector() {
        ses = Executors.newScheduledThreadPool(3);
        agents = new ConcurrentHashMap<>();
        packet = getAgentPacket();
    }

    public void startDiscovering()  {
        if (discoveringTask == null) {
            PacketListener listener = new MyPackageListener();
            discoveringTask = pcap.startPacketsCapturing(communicationPort, listener, ses);
            cleaningTask = ses.scheduleWithFixedDelay(this::deadAgentRemoving,
                    100,
                    100,
                    TimeUnit.MILLISECONDS);
            log.info("Agent {} started discovering.", agent.getName());
        } else {
            log.warn("Unable command: Discovering has been started before.");
        }
    }

    public void startSending() {
        if (sendingTask == null) {
            log.info("{} started sending.", agent.getName());
            sendingTask = ses.scheduleWithFixedDelay(() -> pcap.sendPacket(packet),
                    0, 50, TimeUnit.MILLISECONDS);
        } else {
            log.warn("Unable command: Sending has been started before.");
        }
    }

    private void deadAgentRemoving() {
        Date curDate = new Date();
        for (Map.Entry<AID, Date> entry: agents.entrySet()) {
            if (curDate.getTime() - entry.getValue().getTime() > deadAgentDelay) {
                log.info("{} was deleted from list of {}",
                        entry.getKey().getLocalName(),
                        agent.getLocalName());

                log.info("Delay of sending package for {} - {}", entry.getKey().getName(),
                        curDate.getTime() - entry.getValue().getTime());

                informSubscribers(entry.getKey().getLocalName()
                        +" was deleted from list of "
                        +agent.getLocalName());

                agents.remove(entry.getKey());
            }
        }
    }

    public List<AID> getActiveAgents() {
        deadAgentRemoving();
        return new ArrayList<>(agents.keySet());
    }

    public void subscribeOnChange(Listener subscriber) {
        subscribers.add(subscriber);
    }

    public void stopSending() {
        log.info("Agent {} stopped sending.", agent.getName());
        sendingTask.cancel(true);
        sendingTask = null;
    }

    public void stop() {
        log.info("Detector of {} was stopped", agent.getLocalName());
        cleaningTask.cancel(true);
        sendingTask.cancel(true);
        discoveringTask.cancel(true);
    }

    private byte[] getAgentPacket() {
        String content = ParsingProvider.toJson(agent);
        return new PacketBuilder()
                .setData(content)
                .setPort(communicationPort)
                .setSourceIP("127.0.0.1")
                .setDestinationIP("255.255.255.255")
                .getPacket();
    }
    private void informSubscribers(String msg) {
        for (Listener subscriber: subscribers) {
            subscriber.listen(msg);
        }
    }
    private class MyPackageListener implements PacketListener {
        @Override
        public void gotPacket(Packet packet) {
            if (packet != null) {
                AID receivedAID = parsePacket(packet);
                if (!receivedAID.equals(agent)) {
                    if (!agents.containsKey(receivedAID)) {
                        informSubscribers("Added: " + receivedAID);
                    }
                    agents.put(receivedAID, new Date());
                }
                log.debug("Agent {} got AID {}", agent.getName(), receivedAID.getName());
            }
        }

        private AID parsePacket(Packet packet) {
            byte[] data = packet.getRawData();
            String jsonContent = packetHelper.parse(data);
            return ParsingProvider.fromJson(jsonContent, AID.class);
        }
    }
}
