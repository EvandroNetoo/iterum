package iterum.viewer;

import iterum.controller.GerenciadorInterfaceGrafica;
import iterum.domain.Projeto;
import iterum.viewer.componentes.PnlCardProjeto;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class PnlProjetos extends javax.swing.JPanel {

    private final JTextField txtBusca = new JTextField(24);
    private final JPanel pnlListaProjetos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

    public PnlProjetos() {
        initComponents();
        atualizarListagem();
    }

    public final void atualizarListagem() {
        pnlListaProjetos.removeAll();

        for (Projeto projeto : GerenciadorInterfaceGrafica.instancia.consultarProjetos(txtBusca.getText())) {
            pnlListaProjetos.add(criarItemProjeto(projeto));
        }

        pnlListaProjetos.revalidate();
        pnlListaProjetos.repaint();
    }

    private JPanel criarItemProjeto(Projeto projeto) {
        JPanel item = new JPanel(new BorderLayout(0, 6));
        item.add(new PnlCardProjeto(projeto), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");

        btnEditar.addActionListener(evt -> editarProjeto(projeto));
        btnExcluir.addActionListener(evt -> excluirProjeto(projeto));

        acoes.add(btnEditar);
        acoes.add(btnExcluir);
        item.add(acoes, BorderLayout.SOUTH);
        return item;
    }

    private void editarProjeto(Projeto projeto) {
        GerenciadorInterfaceGrafica.instancia.solicitarEdicaoProjeto(this, projeto)
                .ifPresent(nome -> {
                    GerenciadorInterfaceGrafica.instancia.alterarProjeto(projeto, nome);
                    atualizarListagem();
                });
    }

    private void excluirProjeto(Projeto projeto) {
        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Excluir o projeto \"" + projeto.getNome() + "\" e todas as tarefas dele?",
                "Excluir projeto",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION && GerenciadorInterfaceGrafica.instancia.excluirProjeto(projeto)) {
            atualizarListagem();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel topo = new JPanel(new BorderLayout(10, 0));
        topo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        javax.swing.JLabel titulo = new javax.swing.JLabel("PROJETOS");
        titulo.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.BOLD, 42));
        topo.add(titulo, BorderLayout.WEST);

        JPanel busca = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        txtBusca.putClientProperty("JTextField.placeholderText", "Consultar projeto");
        txtBusca.addActionListener(evt -> atualizarListagem());
        JButton btnBuscar = new JButton("Buscar");
        JButton btnAdicionar = new JButton("Novo Projeto");
        btnBuscar.addActionListener(evt -> atualizarListagem());
        btnAdicionar.addActionListener(evt -> GerenciadorInterfaceGrafica.instancia.solicitarNomeNovoProjeto(this)
                .ifPresent(nome -> {
                    GerenciadorInterfaceGrafica.instancia.adicionarProjeto(nome);
                    atualizarListagem();
                }));

        busca.add(txtBusca);
        busca.add(btnBuscar);
        busca.add(btnAdicionar);
        topo.add(busca, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);

        pnlListaProjetos.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JScrollPane scrollPane = new JScrollPane(pnlListaProjetos);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
}
