package Behaviours;

import WorkingClasses.AgentDetector;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

@Slf4j
public class PingPongIni extends AchieveREInitiator {
    private AgentDetector detector;
    public PingPongIni(Agent a, AgentDetector detector) {
        super(a, new ACLMessage(ACLMessage.REQUEST));
        this.detector = detector;
    }

    @Override
    protected Vector prepareRequests(ACLMessage request) {
        List<AID> players = detector.getActiveAgents();
        players.forEach(player -> request.addReceiver(player));
        request.setContent("Ping");
        request.setProtocol("ping");
        log.info("Agent {} sends Ping to {}",
                getAgent().getLocalName(),
                players.stream().map(AID::getLocalName).collect(Collectors.toList()));
        return super.prepareRequests(request);
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        log.info("Agent {} got {} from {}",
                getAgent().getLocalName(),
                inform.getContent(),
                inform.getSender().getLocalName());
        super.handleInform(inform);
    }
}
