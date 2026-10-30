package assistente;

import javafx.application.Application;

import javax.imageio.ImageIO;
import java.awt.Taskbar;

public class Lancador {
    public static void main(String[] args) throws Exception {

        System.setProperty("apple.awt.application.appearance", "NSAppearanceNameDarkAqua");
        // coloca o ícone no Dock (se o sistema permitir)
        if (Taskbar.isTaskbarSupported()) {
            Taskbar barra = Taskbar.getTaskbar();
            if (barra.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                barra.setIconImage(ImageIO.read(Lancador.class.getResource("/icone_mono.png")));
            }
        }

        Application.launch(AppJanela.class, args);
    }
}