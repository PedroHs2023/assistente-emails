package assistente;

import javafx.application.HostServices;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class CelulaVaga extends ListCell<Vaga> {

    private final HostServices navegador;

    public CelulaVaga(HostServices navegador) {
        this.navegador = navegador;
        setPrefWidth(0);
    }

    @Override
    protected void updateItem(Vaga vaga, boolean vazio) {
        super.updateItem(vaga, vazio);

        // célula sobrando: fica em branco
        if (vazio || vaga == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        // 1. as partes do cartão
        Label titulo = new Label(vaga.titulo());
        titulo.getStyleClass().add("cartao-titulo");

        Label empresaLocal = new Label(vaga.empresa() + " · " + vaga.local());
        empresaLocal.getStyleClass().add("cartao-meta");

        Label resumo = new Label(vaga.resumo());
        resumo.getStyleClass().add("cartao-resumo");
        resumo.setWrapText(true);

        Hyperlink abrir = new Hyperlink("Abrir vaga →");
        abrir.getStyleClass().add("cartao-link");

        // 2. o que o link faz
        if (vaga.link().isBlank()) {
            abrir.setDisable(true);
        } else {
            abrir.setOnAction(evento -> navegador.showDocument(vaga.link()));
        }

        // 3. monta o cartão e mostra
        VBox cartao = new VBox(6, titulo, empresaLocal, resumo, abrir);
        cartao.getStyleClass().add("cartao");

        setText(null);
        setGraphic(cartao);
    }
}