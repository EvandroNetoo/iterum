package iterum.viewer;

import iterum.controller.GerenciadorInterfaceGrafica;
import iterum.viewer.componentes.PnlCardDashboard;

public class PnlDashboard extends javax.swing.JPanel {

    private javax.swing.JPanel pnlCardsTopo;
    private javax.swing.JPanel pnlEspaco;

    public PnlDashboard() {
        initComponents();
        atualizarDashboard();
    }

    public final void atualizarDashboard() {
        pnlCardsTopo.removeAll();
        pnlCardsTopo.add(new PnlCardDashboard("Projetos", String.valueOf(GerenciadorInterfaceGrafica.instancia.contarProjetos())));
        pnlCardsTopo.add(new PnlCardDashboard("Pendentes", String.valueOf(GerenciadorInterfaceGrafica.instancia.contarTarefas(false))));
        pnlCardsTopo.add(new PnlCardDashboard("Concluidas", String.valueOf(GerenciadorInterfaceGrafica.instancia.contarTarefas(true))));
        pnlCardsTopo.add(new PnlCardDashboard("Colaboradores", String.valueOf(GerenciadorInterfaceGrafica.instancia.contarContribuidores())));
        pnlCardsTopo.revalidate();
        pnlCardsTopo.repaint();
    }

    private void initComponents() {
        pnlCardsTopo = new javax.swing.JPanel();
        pnlEspaco = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout(10, 10));
        pnlCardsTopo.setLayout(new java.awt.GridLayout(1, 4, 10, 0));
        add(pnlCardsTopo, java.awt.BorderLayout.NORTH);

        pnlEspaco.setLayout(new java.awt.BorderLayout());
        add(pnlEspaco, java.awt.BorderLayout.CENTER);
    }
}
