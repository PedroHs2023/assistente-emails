package assistente;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class RegistroVistos {

    private static final Path ARQUIVO = Path.of("vistos.txt");
    private final Set<String> ids = new HashSet<>();

    public RegistroVistos() throws IOException{

        if(Files.exists(ARQUIVO)){
            ids.addAll(Files.readAllLines(ARQUIVO));
        }

    }

    public boolean jaVisto(String id){
        return ids.contains(id);
    }

    public void marcar(String id){
        ids.add(id);
    }

    public void salvar() throws  IOException{
        Files.write(ARQUIVO,ids);
    }

}
