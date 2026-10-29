package assistente;

import com.google.api.services.gmail.model.MessagePartHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class LeitorHtml {

    private static final List<String> PALAVRAS_LIXO = List.of(
            "termos", "política", "privacidade", "ajuda", "gerenciar",
            "editar", "criar", "cancelar", "descadastr", "configura","unsubscribe"
    );

    public List<Link> extrairLinks(String html){

        List<Link> links = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements listaDeLinks = doc.select("a[href]");
        for(Element elemento: listaDeLinks){
            String texto = elemento.text();
            String endereco = elemento.attr("href");

            if(texto.isBlank()){
                continue;
            }
            if(!endereco.startsWith("http")){
                continue;
            }
            if(ehLixo(texto) || ehLixo(endereco)){
                continue;
            }
            links.add(new Link(texto, endereco));
        }
        return links;
    }

    private boolean ehLixo(String texto){
        String textoMinusculo = texto.toLowerCase();

        for(String palavra:PALAVRAS_LIXO){
            if(textoMinusculo.contains(palavra)){
                return true;
            }
        }
        return false;
    }
}
