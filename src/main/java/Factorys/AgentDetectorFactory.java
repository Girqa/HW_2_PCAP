package Factorys;

import AdditionalClasses.PacketHelper;
import AdditionalClasses.PcapHelper;
import WorkingClasses.AgentDetector;
import jade.core.AID;

public class AgentDetectorFactory {
    private static final String communicationInterface = "\\Device\\NPF_Loopback";
    private static final int communicationPort = 4040;
    private static final int readPacketDelay = 50;
    private static final long deadAgentDelay = 200;
    public static AgentDetector newDetector(AID aid) {
        return getAgentDetector(aid, communicationPort);
    }

    public static AgentDetector newDetector(AID aid, int port) {
        return getAgentDetector(aid, port);
    }

    private static AgentDetector getAgentDetector(AID aid, int port) {
        AgentDetector detector = new AgentDetector();
        detector.setAgent(aid);
        detector.setCommunicationPort(port);
        detector.setPcap(new PcapHelper(communicationInterface, readPacketDelay));
        detector.setPacketHelper(new PacketHelper(communicationInterface));
        detector.setDeadAgentDelay(deadAgentDelay);
        detector.startDiscovering();
        detector.startSending();

        return detector;
    }
}
