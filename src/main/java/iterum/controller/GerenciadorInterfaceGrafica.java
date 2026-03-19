package iterum.controller;

import iterum.viewer.MainFrame;
import java.awt.CardLayout;
import javax.swing.JPanel;

public class GerenciadorInterfaceGrafica {

    MainFrame mainFrame = null;
    public static final GerenciadorInterfaceGrafica instancia = new GerenciadorInterfaceGrafica();

    private GerenciadorInterfaceGrafica() {
        System.out.println("1");
        try {
            com.formdev.flatlaf.FlatLaf.registerCustomDefaultsSource("iterum");

            com.formdev.flatlaf.FlatLightLaf.setup();

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erro ao carregar o tema IFES", ex);
        }
        mainFrame = new MainFrame();
    }

    public void abrirMainFrame() {
        mainFrame.setVisible(true);
        GerenciadorInterfaceGrafica.instancia.abrirTela(Tela.DASHBOARD);
    }

    public void abrirTela(Tela tela) {
        JPanel pnlConteudo = mainFrame.getPnlConteudo();

        JPanel novaTela = tela.getPanel();

        if (novaTela.getParent() == null) {
            pnlConteudo.add(novaTela, tela.getNome());
            pnlConteudo.revalidate();
        }

        CardLayout card = (CardLayout) pnlConteudo.getLayout();
        card.show(pnlConteudo, tela.getNome());
    }

}
