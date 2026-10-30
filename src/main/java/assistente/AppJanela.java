package assistente;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AppJanela extends Application {
    @Override
    public void start(Stage palco){

        Label titulo = new Label("Assistente de E-mails");

        VBox raiz = new VBox(titulo);

        Scene cena = new Scene(raiz, 800, 600);
        cena.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        palco.setScene(cena);
        palco.setTitle("Assistente de E-mails");
        palco.show();
    }
}
