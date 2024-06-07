import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.util.Set;

public class GameRecommendationAgent extends Agent {
    private OntologyLoader ontologyLoader;

    @Override
    protected void setup() {
        System.out.println("Agent " + getLocalName() + " started successfully.");

        try {
            ontologyLoader = new OntologyLoader("Files/GameRec_v3.owx");
        } catch (OWLOntologyCreationException e) {
            System.out.println("Problem s agent 007");
            e.printStackTrace();
            doDelete();
        }

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    String[] preferences = content.split(",");
                    if (preferences.length == 4) {
                        String genre = preferences[0].trim();
                        String platform = preferences[1].trim();
                        boolean multiplayer = Boolean.parseBoolean(preferences[2].trim());
                        String publisher = preferences[3].trim();
                        recommendGames(genre, platform, multiplayer, publisher, msg);
                    } else if (preferences.length == 1) {
                        String genre = preferences[0].trim();
                        recommendGamesByGenre(genre, msg);
                    } else {
                        reply(msg, "Invalid preferences format. Please use 'genre, platform, multiplayer, publisher'.");
                    }
                } else {
                    block();
                }
            }
        });
    }
    private void recommendGamesByGenre(String genre, ACLMessage originalMsg) {
        Set<OWLNamedIndividual> games = ontologyLoader.getGamesByGenre(genre);
        StringBuilder recommendations = new StringBuilder("Recommended " + genre + " games:" + "\n");
        for (OWLNamedIndividual game : games) {
            recommendations.append(" - ").append(ontologyLoader.getShortForm(game.getIRI())).append("\n");
        }

        reply(originalMsg, recommendations.toString());
    }
    private void recommendGames(String genre, String platform, boolean multiplayer, String publisher, ACLMessage originalMsg) {
        Set<OWLNamedIndividual> games = ontologyLoader.getGamesByCriteria(genre, platform, multiplayer, publisher);
        StringBuilder recommendations = new StringBuilder("Recommended games by your preferences: " + "\n");
        for (OWLNamedIndividual game : games) {
            recommendations.append(" - ").append(ontologyLoader.getShortForm(game.getIRI())).append("\n");
        }

        reply(originalMsg, recommendations.toString());
    }

    private void reply(ACLMessage originalMsg, String content) {
        ACLMessage reply = originalMsg.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(content);
        send(reply);
    }
}