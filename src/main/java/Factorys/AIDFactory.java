package Factorys;

import AdditionalClasses.ParsingProvider;
import jade.core.AID;

public class AIDFactory {
    public static AID createAID(String name) {
        AID aid = new AID();
        aid.setName(name);
        return aid;
    }

    public static String describeAIDWithJSON(AID aid) {
        return ParsingProvider.toJson(aid.getName());
    }
}
