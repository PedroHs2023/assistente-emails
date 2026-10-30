package assistente;

import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class BuscadorNoticias {

    private static final int MAX_POR_CATEGORIA = 10;

    private final GmailService gmail;
    private final LeitorHtml leitor = new LeitorHtml();

    public BuscadorNoticias() throws IOException, GeneralSecurityException {
        this.gmail = new GmailService();
    }

    // busca as 3 categorias e junta tudo numa lista só
    public List<Noticia> buscarTodas() throws IOException {
        List<Noticia> todas = new ArrayList<>();
        todas.addAll(buscarCategoria("Gerais", Config.BUSCA_NOTICIAS_GERAIS));
        todas.addAll(buscarCategoria("Tecnologia", Config.BUSCA_NOTICIAS_TECNOLOGIA));
        todas.addAll(buscarCategoria("Economia", Config.BUSCA_NOTICIAS_ECONOMIA));
        return todas;
    }

    // busca as newsletters de UMA categoria
    private List<Noticia> buscarCategoria(String categoria, String busca) throws IOException {
        List<Noticia> noticias = new ArrayList<>();

        List<Message> mensagens = gmail.listarMensagens(busca, MAX_POR_CATEGORIA);
        if (mensagens == null) {
            return noticias;
        }

        for (Message mensagem : mensagens) {
            String id = mensagem.getId();
            String assunto = gmail.buscarAssunto(id);
            String remetente = limparRemetente(gmail.buscarRemetente(id));

            // primeiro tenta o texto simples (com parágrafos)...
            String texto = gmail.buscarTexto(id);
            String html = gmail.buscarHtml(id);

            // ...se não tiver, usa o texto do HTML
            if (texto == null) {
                if (html == null) {
                    texto = "";
                } else {
                    texto = leitor.extrairTexto(html);
                }
            }

            noticias.add(new Noticia(id, categoria, remetente, assunto, texto.strip(), html));
        }

        return noticias;
    }

    // "The Bizness <thebizness@thenewscc.com.br>"  →  "The Bizness"
    private String limparRemetente(String remetente) {
        int posicao = remetente.indexOf("<");
        if (posicao > 0) {
            return remetente.substring(0, posicao).replace("\"", "").strip();
        }
        return remetente;
    }
}