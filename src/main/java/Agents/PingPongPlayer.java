package Agents;

import Behaviours.PingPongIni;
import Behaviours.PingPongRe;
import Factorys.AgentDetectorFactory;
import WorkingClasses.AgentDetector;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PingPongPlayer extends Agent {
    private AgentDetector detector;
    @Override
    protected void setup() {
        detector = AgentDetectorFactory.newDetector(getAID(), 8080);
        addBehaviour(new TickerBehaviour(this, 1000L) {
            @Override
            protected void onTick() {
                addBehaviour(new PingPongIni(getAgent(), detector));
            }
        });
        addBehaviour(new PingPongRe(this));

        // Subscribers test
        if (getLocalName().endsWith("1")) {
            detector.subscribeOnChange(
                    msg -> log.info("Subscriber of {} received: {}", this.getLocalName(), msg)
            );
        }
    }

    @Override
    public void doDelete() {
        super.doDelete();
        detector.stop();
    }
}
