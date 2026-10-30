package assistente;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class AppJanela extends Application {

    @Override
    public void start(Stage palco) throws Exception {

        BuscadorVagas buscador = new BuscadorVagas();
        BuscadorNoticias buscadorNoticias = new BuscadorNoticias();                       // 🆕

        // ===== BLOCO 1: menu lateral =====
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

        // ===== BLOCO 2: cabeçalho (vagas) =====
        Label titulo = new Label("Vagas novas");
        titulo.getStyleClass().add("titulo");

        Label subtitulo = new Label("Clique em Buscar vagas para começar");
        subtitulo.getStyleClass().add("subtitulo");

        VBox textos = new VBox(4, titulo, subtitulo);

        Button botaoBuscar = new Button("Buscar vagas");
        botaoBuscar.getStyleClass().add("botao-principal");

        ProgressIndicator carregando = new ProgressIndicator();
        carregando.setPrefSize(24, 24);
        carregando.setVisible(false);

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox cabecalho = new HBox(16, textos, espaco, carregando, botaoBuscar);
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        // ===== BLOCO 3: lista e conteúdo (vagas) =====
        ListView<Vaga> listaVagas = new ListView<>();
        listaVagas.setPlaceholder(new Label("Nenhuma vaga ainda."));
        listaVagas.setCellFactory(lista -> new CelulaVaga(getHostServices()));

        VBox conteudo = new VBox(24, cabecalho, listaVagas);
        conteudo.getStyleClass().add("conteudo");
        VBox.setVgrow(listaVagas, Priority.ALWAYS);

        // ===== 🆕 TELA DE NOTÍCIAS =====
        TelaNoticias telaNoticias = new TelaNoticias(buscadorNoticias, getHostServices());

        // ===== AÇÃO DO BOTÃO (vagas) =====
        botaoBuscar.setOnAction(evento -> {

            botaoBuscar.setDisable(true);
            botaoBuscar.setText("Buscando…");
            carregando.setVisible(true);
            subtitulo.setText("Lendo os e-mails e analisando com a IA…");

            Task<List<Vaga>> tarefa = new Task<>() {
                @Override
                protected List<Vaga> call() throws Exception {
                    return buscador.buscarNovas();
                }
            };

            tarefa.setOnSucceeded(e -> {
                List<Vaga> vagas = tarefa.getValue();
                listaVagas.getItems().setAll(vagas);
                if (vagas.isEmpty()) {
                    subtitulo.setText("Nenhuma vaga nova.");
                } else {
                    subtitulo.setText(vagas.size() + " vagas novas");
                }
                voltarAoNormal(botaoBuscar, carregando);
            });

            tarefa.setOnFailed(e -> {
                subtitulo.setText("Erro ao buscar vagas: " + tarefa.getException().getMessage());
                voltarAoNormal(botaoBuscar, carregando);
            });

            Thread trabalhador = new Thread(tarefa);
            trabalhador.setDaemon(true);
            trabalhador.start();
        });

        // ===== BLOCO 4: juntar tudo =====
        BorderPane raiz = new BorderPane();
        raiz.setLeft(menuLateral);
        raiz.setCenter(conteudo);

        // ===== 🆕 NAVEGAÇÃO DO MENU =====
        List<Button> itensMenu = List.of(menuVagas, menuNoticias, menuChat, menuConfig);

        menuVagas.setOnAction(evento -> {
            raiz.setCenter(conteudo);
            ativarMenu(menuVagas, itensMenu);
        });

        menuNoticias.setOnAction(evento -> {
            raiz.setCenter(telaNoticias);
            ativarMenu(menuNoticias, itensMenu);
        });

        // ===== mostrar =====
        Scene cena = new Scene(raiz, 1100, 720);
        cena.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        palco.setScene(cena);
        palco.setTitle("Assistente de E-mails");
        palco.show();
    }

    // ajudante: volta o botão e a rodinha ao estado normal
    private void voltarAoNormal(Button botao, ProgressIndicator carregando) {
        botao.setDisable(false);
        botao.setText("Buscar vagas");
        carregando.setVisible(false);
    }

    // 🆕 ajudante: destaca o item do menu clicado
    private void ativarMenu(Button ativo, List<Button> itens) {
        for (Button item : itens) {
            item.getStyleClass().remove("item-menu-ativo");
        }
        ativo.getStyleClass().add("item-menu-ativo");
    }
}