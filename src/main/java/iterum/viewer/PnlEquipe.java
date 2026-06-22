package iterum.viewer;

import iterum.controller.GerenciadorInterfaceGrafica;
import iterum.controller.TableModelContribuidor;
import iterum.domain.Contribuidor;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class PnlEquipe extends javax.swing.JPanel {

    private final TableModelContribuidor tableModelContribuidor = new TableModelContribuidor();
    private final JTable tabela = new JTable(tableModelContribuidor);
    private final JTextField txtBusca = new JTextField(24);

    public PnlEquipe() {
        initComponents();
        atualizarTabela();
    }

    public final void atualizarTabela() {
        tableModelContribuidor.setLista(GerenciadorInterfaceGrafica.instancia.consultarContribuidores(txtBusca.getText()));
    }

    private Contribuidor getSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um colaborador.", "Equipe", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return tableModelContribuidor.getItem(tabela.convertRowIndexToModel(linha));
    }

    private void adicionar() {
        GerenciadorInterfaceGrafica.instancia.solicitarNovoContribuidor(this)
                .ifPresent(contribuidor -> atualizarTabela());
    }

    private void editar() {
        Contribuidor selecionado = getSelecionado();
        if (selecionado == null) {
            return;
        }
        GerenciadorInterfaceGrafica.instancia.solicitarEdicaoContribuidor(this, selecionado)
                .ifPresent(contribuidor -> atualizarTabela());
    }

    private void excluir() {
        Contribuidor selecionado = getSelecionado();
        if (selecionado == null) {
            return;
        }

        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Excluir o colaborador \"" + selecionado.getNome() + "\"?",
                "Excluir colaborador",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION && GerenciadorInterfaceGrafica.instancia.excluirContribuidor(selecionado)) {
            atualizarTabela();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel topo = new JPanel(new BorderLayout(10, 0));
        topo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        javax.swing.JLabel titulo = new javax.swing.JLabel("EQUIPE");
        titulo.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.BOLD, 42));
        topo.add(titulo, BorderLayout.WEST);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        txtBusca.putClientProperty("JTextField.placeholderText", "Consultar nome ou email");
        txtBusca.addActionListener(evt -> atualizarTabela());
        JButton btnBuscar = new JButton("Buscar");
        JButton btnAdicionar = new JButton("Novo");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");

        btnBuscar.addActionListener(evt -> atualizarTabela());
        btnAdicionar.addActionListener(evt -> adicionar());
        btnEditar.addActionListener(evt -> editar());
        btnExcluir.addActionListener(evt -> excluir());

        acoes.add(txtBusca);
        acoes.add(btnBuscar);
        acoes.add(btnAdicionar);
        acoes.add(btnEditar);
        acoes.add(btnExcluir);
        topo.add(acoes, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }
}
