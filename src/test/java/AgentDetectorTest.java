import Factorys.AgentDetectorFactory;
import WorkingClasses.AgentDetector;
import jade.core.AID;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AgentDetectorTest {
    @Test
    void testTwoDetectors() {
        AgentDetector detector1 = AgentDetectorFactory.
                newDetector(new AID("Agent1", true));
        AgentDetector detector2 = AgentDetectorFactory
                .newDetector(new AID("Agent2", true));

        doPause(200L);

        assertEquals(1, detector1.getActiveAgents().size());
        assertEquals(1, detector2.getActiveAgents().size());
    }
    @Test
    void stoppingOneDetector() {
        AgentDetector detector1 = AgentDetectorFactory
                .newDetector(new AID("Agent1", true), 1020);
        AgentDetector detector2 = AgentDetectorFactory.
                newDetector(new AID("Agent2", true), 1020);

        doPause(200L);

        assertEquals(1, detector1.getActiveAgents().size());
        assertEquals(1, detector2.getActiveAgents().size());

        detector2.stopSending();

        doPause(300L);

        assertEquals(0, detector1.getActiveAgents().size());
        assertEquals(1, detector2.getActiveAgents().size());
    }
    @Test
    void testManyDetectors() {
        int detectorsAmount = 4;
        List<AgentDetector> detectors = getFewDetectorsOnPort(detectorsAmount, 2020);

        doPause(1000L);

        for (AgentDetector detector: detectors) {
            assertEquals(
                    detectorsAmount - 1,
                    detector.getActiveAgents().size());
        }

    }

    private List<AgentDetector> getFewDetectorsOnPort(int N, int port) {
        List<AgentDetector> detectors = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            detectors.add(
                    AgentDetectorFactory.newDetector(new AID("Agent "+(i+1)+"@192.168.56.1:1099/JADE", true), port)
            );
        }
        return detectors;
    }

    private void doPause(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
