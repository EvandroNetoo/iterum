package iterum.controller;

import iterum.viewer.FrmPrincipal;
import java.awt.CardLayout;
import javax.swing.JPanel;

public class GerenciadorInterfaceGrafica {

    FrmPrincipal mainFrame = null;
    public static final GerenciadorInterfaceGrafica instancia = new GerenciadorInterfaceGrafica();

    private GerenciadorInterfaceGrafica() {
        try {
            com.formdev.flatlaf.FlatLaf.registerCustomDefaultsSource("iterum");

            com.formdev.flatlaf.FlatLightLaf.setup();

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(FrmPrincipal.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erro ao carregar o tema IFES", ex);
        }
        mainFrame = new FrmPrincipal();
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
