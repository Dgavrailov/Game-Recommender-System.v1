import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class ClientAgent extends Agent {
    private String genre;
    private String platform;
    private boolean multiplayer;
    private String publisher;
    private Label recommendationsLabel;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length == 5) {
            genre = (String) args[0];
            platform = (String) args[1];
            multiplayer = (boolean) args[2];
            publisher = (String) args[3];
            recommendationsLabel = (Label) args[4];

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(getAID("GameRecommender"));
            msg.setContent(genre + "," + platform + "," + multiplayer + "," + publisher);
            send(msg);
        } if (args != null && args.length == 2) {
            genre = (String) args[0];
            recommendationsLabel = (Label) args[1];

            ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
            msg2.addReceiver(getAID("GameRecommender"));
            msg2.setContent(genre);
            send(msg2);
        }

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    Platform.runLater(() -> recommendationsLabel.setText(content));
                    doDelete();
                } else {
                    block();
                }
            }
        });
    }
}
