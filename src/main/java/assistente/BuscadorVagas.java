package assistente;

import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class BuscadorVagas {

    private final GmailService gmail;
    private final LeitorHtml leitor = new LeitorHtml();
    private final AnalisadorVagas analisador = new AnalisadorVagas();
    private final RegistroVistos registro;

    public BuscadorVagas() throws IOException, GeneralSecurityException {
        this.gmail = new GmailService();
        this.registro = new RegistroVistos();


    }
    public List<Vaga> buscarNovas() throws IOException, InterruptedException {

        List<Vaga> todasAsVagas = new ArrayList<>();

        List<Message> mensagens = gmail.listarMensagens(Config.BUSCA_VAGAS, 2);
        if (mensagens == null) {
            return todasAsVagas;
        }
        //    (sem o else! o return já sai do método)
        for (Message mensagem : mensagens) {
            String id = mensagem.getId();

            if (registro.jaVisto(id)) {
                continue;
            }

            String assunto = gmail.buscarAssunto(id);
            String html = gmail.buscarHtml(id);

            if (html == null) {
                registro.marcar(id);
                registro.salvar();
                continue;
            }

            String texto = leitor.extrairTexto(html);
            List<Link> links = leitor.extrairLinks(html);
            Email email = new Email(id, assunto, texto, links);

            List<Vaga> vagas = analisador.analisar(email);
            todasAsVagas.addAll(vagas);

            registro.marcar(id);
            registro.salvar();
        }

        return todasAsVagas;
    }


}
