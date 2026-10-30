package assistente;

import javafx.application.Application;
import javafx.concurrent.Task;                                    // 🆕
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;                    // 🆕
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

        // ===== BLOCO 2: cabeçalho =====
        Label titulo = new Label("Vagas novas");
        titulo.getStyleClass().add("titulo");

        Label subtitulo = new Label("Clique em Buscar vagas para começar");
        subtitulo.getStyleClass().add("subtitulo");

        VBox textos = new VBox(4, titulo, subtitulo);

        Button botaoBuscar = new Button("Buscar vagas");
        botaoBuscar.getStyleClass().add("botao-principal");

        ProgressIndicator carregando = new ProgressIndicator();   // 🆕 a rodinha
        carregando.setPrefSize(24, 24);                           // 🆕
        carregando.setVisible(false);                             // 🆕 começa escondida

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox cabecalho = new HBox(16, textos, espaco, carregando, botaoBuscar);   // 🆕 carregando antes do botão
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        // ===== BLOCO 3: lista e conteúdo =====
        ListView<String> listaVagas = new ListView<>();
        listaVagas.setPlaceholder(new Label("Nenhuma vaga ainda."));

        VBox conteudo = new VBox(24, cabecalho, listaVagas);
        conteudo.getStyleClass().add("conteudo");
        VBox.setVgrow(listaVagas, Priority.ALWAYS);

        // ===== AÇÃO DO BOTÃO =====
        botaoBuscar.setOnAction(evento -> {

            // 🆕 ANTES de começar: mostra que está carregando
            botaoBuscar.setDisable(true);
            botaoBuscar.setText("Buscando…");
            carregando.setVisible(true);
            subtitulo.setText("Lendo os e-mails e analisando com a IA…");

            // 🆕 o trabalho pesado, numa thread de fundo
            Task<List<Vaga>> tarefa = new Task<>() {
                @Override
                protected List<Vaga> call() throws Exception {
                    return buscador.buscarNovas();
                }
            };

            // 🆕 quando terminar bem: mostra as vagas (de volta na thread da tela)
            tarefa.setOnSucceeded(e -> {
                List<Vaga> vagas = tarefa.getValue();
                listaVagas.getItems().clear();
                for (Vaga vaga : vagas) {
                    String linha = vaga.titulo() + " — " + vaga.empresa() + " (" + vaga.local() + ")";
                    listaVagas.getItems().add(linha);
                }
                if (vagas.isEmpty()) {
                    subtitulo.setText("Nenhuma vaga nova.");
                } else {
                    subtitulo.setText(vagas.size() + " vagas novas");
                }
                voltarAoNormal(botaoBuscar, carregando);
            });

            // 🆕 quando der erro: avisa
            tarefa.setOnFailed(e -> {
                subtitulo.setText("Erro ao buscar vagas: " + tarefa.getException().getMessage());
                voltarAoNormal(botaoBuscar, carregando);
            });

            // 🆕 coloca para rodar numa thread nova
            Thread trabalhador = new Thread(tarefa);
            trabalhador.setDaemon(true);
            trabalhador.start();
        });

        // ===== BLOCO 4: juntar tudo e mostrar =====
        BorderPane raiz = new BorderPane();
        raiz.setLeft(menuLateral);
        raiz.setCenter(conteudo);

        Scene cena = new Scene(raiz, 1100, 720);
        cena.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        palco.setScene(cena);
        palco.setTitle("Assistente de E-mails");
        palco.show();
    }

    // 🆕 ajudante: volta o botão e a rodinha ao estado normal
    private void voltarAoNormal(Button botao, ProgressIndicator carregando) {
        botao.setDisable(false);
        botao.setText("Buscar vagas");
        carregando.setVisible(false);
    }
}