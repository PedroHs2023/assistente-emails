package assistente;

import javafx.application.HostServices;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;                                                         // 🆕
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class TelaNoticias extends VBox {

    private final ListView<Noticia> listaNoticias = new ListView<>();
    private final Label subtitulo = new Label("Clique em Atualizar para carregar as newsletters");
    private final List<Button> botoesFiltro = new ArrayList<>();
    private final WebView leitor = new WebView();                                       // 🆕 o mini navegador
    private static final String MODO_ESCURO = """
        html { filter: invert(1) hue-rotate(180deg); background: white; }
        img, video { filter: invert(1) hue-rotate(180deg); }
        """;

    private List<Noticia> todas = new ArrayList<>();
    private String categoriaAtual = "Todas";

    public TelaNoticias(BuscadorNoticias buscador, HostServices navegador) {

        // ===== cabeçalho =====
        Label titulo = new Label("Notícias");
        titulo.getStyleClass().add("titulo");
        subtitulo.getStyleClass().add("subtitulo");
        VBox textos = new VBox(4, titulo, subtitulo);

        ProgressIndicator carregando = new ProgressIndicator();
        carregando.setPrefSize(24, 24);
        carregando.setVisible(false);

        Button botaoAtualizar = new Button("Atualizar");
        botaoAtualizar.getStyleClass().add("botao-principal");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox cabecalho = new HBox(16, textos, espaco, carregando, botaoAtualizar);
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        // ===== filtros =====
        HBox barraFiltros = new HBox(8);
        for (String categoria : List.of("Todas", "Gerais", "Tecnologia", "Economia")) {
            Button botao = new Button(categoria);
            botao.getStyleClass().add("filtro");
            botao.setOnAction(evento -> {
                categoriaAtual = categoria;
                marcarFiltroAtivo(botao);
                aplicarFiltro();
            });
            botoesFiltro.add(botao);
            barraFiltros.getChildren().add(botao);
        }
        marcarFiltroAtivo(botoesFiltro.get(0));

        // ===== lista =====
        listaNoticias.setPlaceholder(new Label("Nenhuma notícia ainda."));
        listaNoticias.setCellFactory(lista -> new CelulaNoticia(navegador));
        VBox.setVgrow(listaNoticias, Priority.ALWAYS);

        // ===== 🆕 modo leitura =====
        leitor.getEngine().setJavaScriptEnabled(false);                                 // 🆕 segurança!
        VBox.setVgrow(leitor, Priority.ALWAYS);

        Button botaoVoltar = new Button("← Voltar para as notícias");                  // 🆕
        botaoVoltar.getStyleClass().add("botao-secundario");

        VBox telaLeitura = new VBox(16, botaoVoltar, leitor);                           // 🆕
        VBox.setVgrow(telaLeitura, Priority.ALWAYS);

        // 🆕 clicou num cartão → abre a leitura
        listaNoticias.setOnMouseClicked(evento -> {
            Noticia escolhida = listaNoticias.getSelectionModel().getSelectedItem();
            if (escolhida != null) {
                abrirLeitura(escolhida);
                getChildren().setAll(telaLeitura);
            }
        });

        // 🆕 clicou em Voltar → volta para a lista
        botaoVoltar.setOnAction(evento -> {
            listaNoticias.getSelectionModel().clearSelection();
            leitor.getEngine().loadContent("");
            getChildren().setAll(cabecalho, barraFiltros, listaNoticias);
        });

        // ===== botão Atualizar =====
        botaoAtualizar.setOnAction(evento -> {
            botaoAtualizar.setDisable(true);
            carregando.setVisible(true);
            subtitulo.setText("Buscando as newsletters…");

            Task<List<Noticia>> tarefa = new Task<>() {
                @Override
                protected List<Noticia> call() throws Exception {
                    return buscador.buscarTodas();
                }
            };

            tarefa.setOnSucceeded(e -> {
                todas = tarefa.getValue();
                aplicarFiltro();
                subtitulo.setText(todas.size() + " newsletters dos últimos 2 dias");
                botaoAtualizar.setDisable(false);
                carregando.setVisible(false);
            });

            tarefa.setOnFailed(e -> {
                subtitulo.setText("Erro ao buscar notícias: " + tarefa.getException().getMessage());
                botaoAtualizar.setDisable(false);
                carregando.setVisible(false);
            });

            Thread trabalhador = new Thread(tarefa);
            trabalhador.setDaemon(true);
            trabalhador.start();
        });

        // ===== monta a tela =====
        setSpacing(20);
        getStyleClass().add("conteudo");
        getChildren().addAll(cabecalho, barraFiltros, listaNoticias);
    }

    // 🆕 carrega a newsletter no mini navegador
    private void abrirLeitura(Noticia noticia) {
        if (noticia.html() != null) {
            // usa as imagens originais (png/jpg)
            String html = noticia.html().replaceAll("/cdn-cgi/image/[^/]+/", "/");

            // injeta o modo escuro
            Document doc = Jsoup.parse(html);
            doc.head().appendElement("style").text(MODO_ESCURO);

            leitor.getEngine().loadContent(doc.outerHtml());
        } else {
            leitor.getEngine().loadContent(noticia.texto(), "text/plain");
        }
    }

    private void aplicarFiltro() {
        List<Noticia> visiveis = new ArrayList<>();
        for (Noticia noticia : todas) {
            if (categoriaAtual.equals("Todas") || noticia.categoria().equals(categoriaAtual)) {
                visiveis.add(noticia);
            }
        }
        listaNoticias.getItems().setAll(visiveis);
    }

    private void marcarFiltroAtivo(Button ativo) {
        for (Button botao : botoesFiltro) {
            botao.getStyleClass().remove("filtro-ativo");
        }
        ativo.getStyleClass().add("filtro-ativo");
    }
}