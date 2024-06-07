import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class GameRecApp extends Application {

    private AgentContainer agentContainer;

    //Create + config of GUI
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Game Recommendation App");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(15));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPrefSize(600, 700);


        Label headerLabel = new Label("Game Recommendation System");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setConstraints(headerLabel, 0, 0, 2, 1);

        Label genreLabel = new Label("Genre:");
        ComboBox<String> genreComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "Shooter", "RPG", "Sports", "Racing", "Adventure", "Fighting", "Card"));
        GridPane.setHalignment(genreLabel, HPos.RIGHT);
        GridPane.setValignment(genreLabel, VPos.CENTER);
        GridPane.setConstraints(genreLabel, 0, 1);
        GridPane.setConstraints(genreComboBox, 1, 1);

        Label platformLabel = new Label("Platform:");
        ComboBox<String> platformComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "PC", "Xbox", "Play_Station", "Android"));
        GridPane.setHalignment(platformLabel, HPos.RIGHT);
        GridPane.setValignment(platformLabel, VPos.CENTER);
        GridPane.setConstraints(platformLabel, 0, 2);
        GridPane.setConstraints(platformComboBox, 1, 2);

        Label multiplayerLabel = new Label("Multiplayer:");
        ComboBox<String> multiplayerComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "true", "false"));
        GridPane.setHalignment(multiplayerLabel, HPos.RIGHT);
        GridPane.setValignment(multiplayerLabel, VPos.CENTER);
        GridPane.setConstraints(multiplayerLabel, 0, 3);
        GridPane.setConstraints(multiplayerComboBox, 1, 3);

        Label publisherLabel = new Label("Publisher:");
        ComboBox<String> publisherComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "Blizzard", "EA_Games", "Midway_Games"));
        GridPane.setHalignment(publisherLabel, HPos.RIGHT);
        GridPane.setValignment(publisherLabel, VPos.CENTER);
        GridPane.setConstraints(publisherLabel, 0, 4);
        GridPane.setConstraints(publisherComboBox, 1, 4);

        Button recommendButton = new Button("Recommend");
        GridPane.setHalignment(recommendButton, HPos.CENTER);
        GridPane.setConstraints(recommendButton, 0, 5, 2, 1);

        Label recommendationsLabel = new Label();
        recommendationsLabel.setWrapText(false);
        GridPane.setHalignment(recommendationsLabel, HPos.CENTER);
        GridPane.setConstraints(recommendationsLabel, 0, 6, 2, 1);

        Button recommendByGenreButton = new Button("By Genre");
        GridPane.setHalignment(recommendByGenreButton, HPos.CENTER);
        GridPane.setConstraints(recommendByGenreButton, 0, 7, 2, 1);

        Label recommendationsByGenreLabel = new Label();
        recommendationsByGenreLabel.setWrapText(false);
        GridPane.setHalignment(recommendationsByGenreLabel, HPos.CENTER);
        GridPane.setConstraints(recommendationsByGenreLabel, 0, 8, 2, 1);

        Button restartButton = new Button("Reset");
        GridPane.setHalignment(restartButton, HPos.RIGHT);
        GridPane.setConstraints(restartButton, 0, 9, 2, 1);

        gridPane.getChildren().addAll(headerLabel, genreLabel, genreComboBox, platformLabel, platformComboBox,
                multiplayerLabel, multiplayerComboBox, publisherLabel, publisherComboBox, recommendButton,
                recommendationsLabel, recommendByGenreButton, recommendationsByGenreLabel, restartButton);

        recommendByGenreButton.setOnAction(l -> {
            String genre = genreComboBox.getValue();
            getRecommendationsByGenre(genre, recommendationsByGenreLabel);
        });

        recommendButton.setOnAction(e -> {
            String genre = genreComboBox.getValue();
            String platform = platformComboBox.getValue();
            boolean multiplayer = Boolean.parseBoolean(multiplayerComboBox.getValue());
            String publisher = publisherComboBox.getValue();
            getRecommendations(genre, platform, multiplayer, publisher, recommendationsLabel);
        });

        restartButton.setOnAction(m -> {
            genreComboBox.getSelectionModel().clearSelection();
            platformComboBox.getSelectionModel().clearSelection();
            publisherComboBox.getSelectionModel().clearSelection();
            multiplayerComboBox.getSelectionModel().clearSelection();
            recommendationsLabel.setText("");
            recommendationsByGenreLabel.setText("");
        });

        Scene scene = new Scene(gridPane, 600, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm()); // Link the CSS file
        primaryStage.setScene(scene);
        primaryStage.show();

        startJade();
    }

    //Initialization and creation of Agent
    private void startJade() {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        agentContainer = rt.createMainContainer(p);
        try {
            AgentController gameRecommenderAgent = agentContainer.createNewAgent("GameRecommender", "GameRecommendationAgent", null);
            gameRecommenderAgent.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private void getRecommendations(String genre, String platform, boolean multiplayer, String publisher, Label recommendationsLabel) {
        try {
            AgentController clientAgent = agentContainer.createNewAgent("ClientAgent", "ClientAgent", new Object[]{genre, platform, multiplayer, publisher, recommendationsLabel});
            clientAgent.start();
        } catch (StaleProxyException e) {
            System.out.println("Failed to initialize Client Agent used to recommend by criteria");
            e.printStackTrace();
        }
    }

    private void getRecommendationsByGenre(String genre, Label recommendationsLabel) {
        try {
            AgentController clientAgent = agentContainer.createNewAgent("ClientAgent", "ClientAgent", new Object[]{genre, recommendationsLabel});
            clientAgent.start();
        } catch (StaleProxyException e) {
            System.out.println("Failed to initialize Client Agent used to recommend by Genre");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
