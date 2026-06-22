package iterum.viewer;

import iterum.controller.GerenciadorInterfaceGrafica;
import iterum.domain.Contribuidor;
import iterum.domain.EtapaProjeto;
import iterum.domain.Projeto;
import iterum.domain.Tarefa;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PnlRelatorios extends javax.swing.JPanel {

    private final JComboBox<ItemFiltro<Projeto>> cmbProjetos = new JComboBox<>();
    private final JComboBox<ItemFiltro<EtapaProjeto>> cmbEtapas = new JComboBox<>();
    private final JComboBox<ItemFiltro<Contribuidor>> cmbContribuidores = new JComboBox<>();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Projeto", "Etapa", "Tarefa", "Colaboradores"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(tableModel);
    private final JLabel lblResumo = new JLabel("-");

    public PnlRelatorios() {
        initComponents();
        atualizarRelatorios();
    }

    public final void atualizarRelatorios() {
        carregarFiltros();
        consultar();
    }

    private void carregarFiltros() {
        Object projetoSelecionado = cmbProjetos.getSelectedItem();
        Object contribuidorSelecionado = cmbContribuidores.getSelectedItem();

        cmbProjetos.removeAllItems();
        cmbProjetos.addItem(new ItemFiltro<>("Todos os projetos", null));
        for (Projeto projeto : GerenciadorInterfaceGrafica.instancia.listarProjetos()) {
            cmbProjetos.addItem(new ItemFiltro<>(projeto.getNome(), projeto));
        }

        cmbContribuidores.removeAllItems();
        cmbContribuidores.addItem(new ItemFiltro<>("Todos os colaboradores", null));
        for (Contribuidor contribuidor : GerenciadorInterfaceGrafica.instancia.listarContribuidores()) {
            cmbContribuidores.addItem(new ItemFiltro<>(contribuidor.toString(), contribuidor));
        }

        restaurarSelecao(cmbProjetos, projetoSelecionado);
        restaurarSelecao(cmbContribuidores, contribuidorSelecionado);
        carregarEtapas();
    }

    private void carregarEtapas() {
        Object etapaSelecionada = cmbEtapas.getSelectedItem();
        cmbEtapas.removeAllItems();
        cmbEtapas.addItem(new ItemFiltro<>("Todas as etapas", null));

        ItemFiltro<Projeto> projeto = getSelecionado(cmbProjetos);
        List<EtapaProjeto> etapas = projeto == null || projeto.valor() == null
                ? GerenciadorInterfaceGrafica.instancia.listarProjetos().stream()
                        .flatMap(p -> p.getEtapas().stream())
                        .toList()
                : projeto.valor().getEtapas();

        for (EtapaProjeto etapa : etapas) {
            cmbEtapas.addItem(new ItemFiltro<>(etapa.getNome(), etapa));
        }
        restaurarSelecao(cmbEtapas, etapaSelecionada);
    }

    private void consultar() {
        ItemFiltro<Projeto> projeto = getSelecionado(cmbProjetos);
        ItemFiltro<EtapaProjeto> etapa = getSelecionado(cmbEtapas);
        ItemFiltro<Contribuidor> contribuidor = getSelecionado(cmbContribuidores);

        Integer projetoId = projeto == null || projeto.valor() == null ? null : projeto.valor().getId();
        Integer etapaId = etapa == null || etapa.valor() == null ? null : etapa.valor().getId();
        Integer contribuidorId = contribuidor == null || contribuidor.valor() == null ? null : contribuidor.valor().getId();

        List<Tarefa> tarefas = GerenciadorInterfaceGrafica.instancia.consultarTarefas(projetoId, etapaId, contribuidorId);
        tableModel.setRowCount(0);

        for (Tarefa tarefa : tarefas) {
            String colaboradores = tarefa.getContribuidores().isEmpty()
                    ? "-"
                    : tarefa.getContribuidores().stream().map(Contribuidor::getNome).collect(Collectors.joining(", "));
            tableModel.addRow(new Object[]{
                tarefa.getEtapa().getProjeto().getNome(),
                tarefa.getEtapa().getNome(),
                tarefa.getNome(),
                colaboradores
            });
        }

        long concluidas = tarefas.stream().filter(t -> t.getEtapa().isEtapaDeConclucao()).count();
        lblResumo.setText("Total: " + tarefas.size() + " | Pendentes: " + (tarefas.size() - concluidas)
                + " | Concluidas: " + concluidas);
    }

    private <T> void restaurarSelecao(JComboBox<ItemFiltro<T>> combo, Object selecionado) {
        if (!(selecionado instanceof ItemFiltro<?> item) || item.valor() == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            ItemFiltro<T> candidato = combo.getItemAt(i);
            if (candidato.valor() != null && candidato.valor().equals(item.valor())) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> ItemFiltro<T> getSelecionado(JComboBox<ItemFiltro<T>> combo) {
        return (ItemFiltro<T>) combo.getSelectedItem();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel topo = new JPanel(new BorderLayout(10, 10));
        JLabel titulo = new JLabel("RELATORIOS");
        titulo.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.BOLD, 42));
        topo.add(titulo, BorderLayout.WEST);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(evt -> {
            carregarEtapas();
            consultar();
        });
        cmbProjetos.addActionListener(evt -> carregarEtapas());

        filtros.add(cmbProjetos);
        filtros.add(cmbEtapas);
        filtros.add(cmbContribuidores);
        filtros.add(btnConsultar);
        topo.add(filtros, BorderLayout.EAST);
        topo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(topo, BorderLayout.NORTH);

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rodape.add(lblResumo);
        add(rodape, BorderLayout.SOUTH);
    }

    private record ItemFiltro<T>(String label, T valor) {
        @Override
        public String toString() {
            return label;
        }
    }
}
