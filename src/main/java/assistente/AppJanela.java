package assistente;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;

public class AppJanela extends Application {
    @Override
    public void start(Stage palco){

        Label nomeApp = new Label("Assistente");
        nomeApp.getStyleClass().add("nome-app");

        Button menuVagas = new Button("Vagas");
        Button menuNoticias = new Button("Notícias");
        Button menuChat = new Button("Chat");
        Button menuConfig = new Button("Configurações");

        menuVagas.getStyleClass().addAll("item-menu", "item-menu-ativo");
        menuNoticias.getStyleClass().add("item-menu");
        menuChat.getStyleClass().add("item-menu");
        menuConfig.getStyleClass().add("item-menu");

        VBox menuLateral = new VBox(8, nomeApp, menuVagas, menuNoticias, menuChat, menuConfig);
        menuLateral.getStyleClass().add("menu-lateral");

        Label titulo = new Label("Vagas novas");
        titulo.getStyleClass().add("titulo");

        Label subtitulo = new Label("Clique em Buscar vagas para começar");
        subtitulo.getStyleClass().add("subtitulo");

        VBox textos = new VBox(4, titulo, subtitulo);

        Button botaoBuscar = new Button("Buscar vagas");
        botaoBuscar.getStyleClass().add("botao-principal");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox cabecalho = new HBox(16, textos, espaco, botaoBuscar);
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        ListView<String> listaVagas = new ListView<>();
        listaVagas.setPlaceholder(new Label("Nenhuma vaga ainda."));

        VBox conteudo = new VBox(24, cabecalho, listaVagas);
        conteudo.getStyleClass().add("conteudo");
        VBox.setVgrow(listaVagas, Priority.ALWAYS);

        BorderPane raiz = new BorderPane();
        raiz.setLeft(menuLateral);
        raiz.setCenter(conteudo);

        Scene cena = new Scene(raiz, 1100, 720);
        cena.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        palco.setScene(cena);
        palco.setTitle("Assistente de E-mails");
        palco.show();

    }
}
