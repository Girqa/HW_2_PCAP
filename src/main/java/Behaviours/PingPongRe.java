package Behaviours;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PingPongRe extends AchieveREResponder {
    public PingPongRe(Agent a) {
        super(a,MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.MatchProtocol("ping")
        ));
    }

    /**
     * Можно ли навесить Proxy на поведение? Логирование громоздкое
     */
    @Override
    protected ACLMessage handleRequest(ACLMessage request) {
        ACLMessage reply = request.createReply();
        log.info("Agent {} got {} from {}",
                getAgent().getLocalName(),
                request.getContent(),
                request.getSender().getLocalName());
        reply.setPerformative(ACLMessage.INFORM);
        reply.setProtocol("pong");
        reply.setContent("Pong");
        return reply;
    }
}
