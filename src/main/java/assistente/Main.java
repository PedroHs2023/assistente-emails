package assistente;

import java.io.IOException;
import java.net.ConnectException;
import java.security.GeneralSecurityException;
import java.util.List;

public class Main {
    public static void main(String[] args)
            throws IOException, InterruptedException, GeneralSecurityException {

        try {

            BuscadorVagas buscador = new BuscadorVagas();
            System.out.println("Conectado ao Gmail!");


            List<Vaga> vagas = buscador.buscarNovas();


            if (vagas.isEmpty()) {
                System.out.println("Nenhuma vaga nova.");
            } else {
                System.out.println(vagas.size() + " vagas novas:");
                for (Vaga vaga : vagas) {
                    System.out.println("  💼 " + vaga.titulo() + " — " + vaga.empresa() + " (" + vaga.local() + ")");
                    System.out.println("     " + vaga.resumo());
                    System.out.println("     " + vaga.link());
                }
            }

        } catch (ConnectException e) {
            System.out.println("Não consegui falar com o Ollama. Ele está aberto?");
        }
    }
}