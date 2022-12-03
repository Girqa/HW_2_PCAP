import Factorys.AgentDetectorFactory;
import WorkingClasses.AgentDetector;
import jade.core.AID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        AID aid1 = new AID("Agent1", true);
        AID aid2 = new AID("Agent2", true);
        AID aid3 = new AID("Agent3", true);
        AID aid4 = new AID("Agent4", true);

        AgentDetector detector1 = AgentDetectorFactory.newDetector(aid1);
        AgentDetector detector2 = AgentDetectorFactory.newDetector(aid2);
        AgentDetector detector3 = AgentDetectorFactory.newDetector(aid3);
        AgentDetector detector4 = AgentDetectorFactory.newDetector(aid4);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        detector1.stopSending();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(detector1.getActiveAgents());
        System.out.println(detector2.getActiveAgents());
        System.out.println(detector3.getActiveAgents());
        System.out.println(detector4.getActiveAgents());
    }
}
