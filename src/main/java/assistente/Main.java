package assistente;

import java.io.IOException;
import java.net.ConnectException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.services.gmail.model.Message;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException,GeneralSecurityException {

        try {
//            OllamaClient cliente = new OllamaClient();
//
//            String sistemaJson = """
//                    Responda apenas JSON no formato: {"itens":[{"nome":"","ano":0}]}
//                    """;
//            String pergunta = "liste 3 linguagens de programação e o ano em que foram criadas";
//
//            String resposta = cliente.perguntar(sistemaJson, pergunta, true);
//
//            // 1. transforma o texto em árvore e pega a lista "itens"
//            JsonArray itens = JsonParser.parseString(resposta)
//                    .getAsJsonObject()
//                    .getAsJsonArray("itens");
//
//            // 2. para cada item da lista...
//            for (JsonElement item : itens) {
//                JsonObject linguagem = item.getAsJsonObject();     // trata o item como objeto { }
//                String nome = linguagem.get("nome").getAsString(); // texto
//                int ano = linguagem.get("ano").getAsInt();         // número
//                System.out.println(nome + " - " + ano);
//            }

            GmailService gmail = new GmailService();
            LeitorHtml leitor = new LeitorHtml();
            System.out.println("Conectado ao Gmail!");

            List<Message> mensagens = gmail.listarMensagens(Config.BUSCA_VAGAS, 2);
            if(mensagens == null){
                System.out.println("Sem vagas para relatar");
            }else{
                for(Message mensagem: mensagens){
                    String id = mensagem.getId();
                    String assunto = gmail.buscarAssunto(id);
                    String html = gmail.buscarHtml(id);

                    if(html == null){
                        System.out.println("Sem HTML: " + assunto);
                        continue;
                    }

                    String texto  = leitor.extrairTexto(html);
                    List<Link> links = leitor.extrairLinks(html);

                    Email email = new Email(id,assunto,texto,links);
                    System.out.println(email.assunto());
                    System.out.println("  Texto: " + email.texto().length()+ " caracteres");
                    System.out.println("  Links: " + email.linksFiltrados().size());

                }

            }

        } catch (ConnectException e) {
            System.out.println("Não consegui falar com o Ollama. Ele está aberto?");
        }
    }
}