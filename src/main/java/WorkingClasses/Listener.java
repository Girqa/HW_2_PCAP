package WorkingClasses;

import jade.core.AID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Listener {
    public void listen(String msg){
        log.debug("Subscriber has received '{}'", msg);
    }
}
