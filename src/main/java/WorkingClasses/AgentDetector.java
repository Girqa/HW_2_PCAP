package WorkingClasses;

import AdditionalClasses.PacketHelper;
import AdditionalClasses.ParsingProvider;
import AdditionalClasses.PcapHelper;
import Factorys.AIDFactory;
import jade.core.AID;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.PacketListener;

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
    private ScheduledExecutorService ses;
    private ScheduledFuture<?> discoveringTask;
    private ScheduledFuture<?> cleaningTask;
    private ScheduledFuture<?> sendingTask;
    private Map<AID, Date> agents = new ConcurrentHashMap();
    private List<Listener> subscribers = new ArrayList<>();

    public AgentDetector() {
        ses = Executors.newScheduledThreadPool(3);
    }

    public void startDiscovering()  {
        if (discoveringTask == null) {
            PacketListener listener = new MyPacketListener(agents, packetHelper, agent, subscribers);
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
            sendingTask = ses.scheduleWithFixedDelay(() -> pcap.sendPacket(getAgentPacket()),
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

    private byte[] getAgentPacket() {
        String content = AIDFactory.describeAIDWithJSON(agent);
        return packetHelper.collectPacket(content, communicationPort);
    }
}
