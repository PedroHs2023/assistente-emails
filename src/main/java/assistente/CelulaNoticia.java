package assistente;

import javafx.application.HostServices;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CelulaNoticia extends ListCell<Noticia> {

    private static final int TAMANHO_PREVIA = 300;

    private final HostServices navegador;

    public CelulaNoticia(HostServices navegador) {
        this.navegador = navegador;
        setPrefWidth(0);
    }

    @Override
    protected void updateItem(Noticia noticia, boolean vazio) {
        super.updateItem(noticia, vazio);

        if (vazio || noticia == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        Label origem = new Label(noticia.categoria().toUpperCase() + "  ·  " + noticia.remetente());
        origem.getStyleClass().add("cartao-categoria");

        Label assunto = new Label(noticia.assunto());
        assunto.getStyleClass().add("cartao-titulo");
        assunto.setWrapText(true);

        Label previa = new Label(criarPrevia(noticia.texto()));
        previa.getStyleClass().add("cartao-resumo");
        previa.setWrapText(true);

        Label dica = new Label("Clique para ler");
        dica.getStyleClass().add("cartao-dica");


        Hyperlink verNoGmail = new Hyperlink("Ver no Gmail");
        verNoGmail.getStyleClass().add("cartao-link-secundario");
        verNoGmail.setOnAction(evento ->
                navegador.showDocument("https://mail.google.com/mail/u/0/#all/" + noticia.id()));

        HBox links = new HBox(16, dica, verNoGmail);

        VBox cartao = new VBox(6, origem, assunto, previa, links);
        cartao.getStyleClass().add("cartao");

        setText(null);
        setGraphic(cartao);
    }

    // os primeiros 300 caracteres, numa linha só, com "…" no final
    private String criarPrevia(String texto) {
        String numaLinha = texto.replaceAll("\\s+", " ");
        if (numaLinha.length() <= TAMANHO_PREVIA) {
            return numaLinha;
        }
        return numaLinha.substring(0, TAMANHO_PREVIA) + "…";
    }
}