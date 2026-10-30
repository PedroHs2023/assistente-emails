package assistente;

import java.io.IOException;
import java.net.ConnectException;
import java.security.GeneralSecurityException;
import java.util.List;

public class Main {
    public static void main(String[] args)
            throws IOException, InterruptedException, GeneralSecurityException {

        try {

            BuscadorNoticias buscador = new BuscadorNoticias();
            for (Noticia noticia : buscador.buscarTodas()) {
                System.out.println(noticia.categoria() + " · " + noticia.remetente() + " · " + noticia.assunto());
                System.out.println("   " + noticia.texto().length() + " caracteres");
            }

        } catch (ConnectException e) {
            System.out.println("Não consegui falar com o Ollama. Ele está aberto?");
        }
    }
}