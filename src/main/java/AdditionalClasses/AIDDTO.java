package AdditionalClasses;

import jade.core.AID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AIDDTO {
    private String aidName;
    public AIDDTO(AID aid) {
        this.aidName = aid.getName();
    }
}
