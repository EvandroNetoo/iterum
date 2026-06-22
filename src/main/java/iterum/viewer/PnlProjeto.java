package iterum.viewer;

import iterum.controller.GerenciadorInterfaceGrafica;
import iterum.domain.Contribuidor;
import iterum.domain.EtapaProjeto;
import iterum.domain.Projeto;
import iterum.domain.Tarefa;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;

public class PnlProjeto extends javax.swing.JPanel {

    private final JLabel lblNomeProjeto = new JLabel("-");
    private final JPanel pnlQuadro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

    public PnlProjeto() {
        initComponents();
        atualizarQuadro();
    }

    public final void atualizarQuadro() {
        Projeto projeto = GerenciadorInterfaceGrafica.instancia.getProjetoSelecionado();
        lblNomeProjeto.setText(projeto == null ? "Selecione um projeto" : projeto.getNome());
        pnlQuadro.removeAll();

        if (projeto != null) {
            for (EtapaProjeto etapa : GerenciadorInterfaceGrafica.instancia.listarEtapasProjetoSelecionado()) {
                pnlQuadro.add(criarColuna(etapa));
            }
        }

        pnlQuadro.revalidate();
        pnlQuadro.repaint();
    }

    private JPanel criarColuna(EtapaProjeto etapa) {
        JPanel coluna = new JPanel(new BorderLayout());
        coluna.setPreferredSize(new Dimension(260, 520));
        coluna.setBorder(BorderFactory.createLineBorder(javax.swing.UIManager.getColor("Button.borderColor")));

        JPanel cabecalho = new JPanel(new BorderLayout(8, 0));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel titulo = new JLabel(etapa.getNome());
        titulo.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.BOLD, 15));
        JButton adicionar = new JButton("+ Tarefa");
        adicionar.addActionListener(evt -> adicionarTarefa(etapa));
        cabecalho.add(titulo, BorderLayout.CENTER);
        cabecalho.add(adicionar, BorderLayout.EAST);
        coluna.add(cabecalho, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        lista.setTransferHandler(new EtapaTransferHandler(etapa));

        for (Tarefa tarefa : etapa.getTarefas()) {
            lista.add(criarCardTarefa(tarefa));
        }

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(null);
        coluna.add(scrollPane, BorderLayout.CENTER);
        return coluna;
    }

    private JPanel criarCardTarefa(Tarefa tarefa) {
        JPanel card = new JPanel(new BorderLayout(8, 6));
        card.setPreferredSize(new Dimension(230, 132));
        card.setMaximumSize(new Dimension(230, 132));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(javax.swing.UIManager.getColor("Button.borderColor")),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        card.setBackground(Color.WHITE);
        card.setOpaque(true);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setTransferHandler(new TarefaTransferHandler(tarefa));

        JTextArea nome = criarTextoCard(tarefa.getNome(), java.awt.Font.BOLD, 2);
        card.add(nome, BorderLayout.NORTH);

        String colaboradores = tarefa.getContribuidores().isEmpty()
                ? "Sem colaboradores"
                : tarefa.getContribuidores().stream().map(Contribuidor::getNome).collect(Collectors.joining(", "));
        card.add(criarTextoCard(colaboradores, java.awt.Font.PLAIN, 2), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new GridLayout(1, 2, 6, 0));
        JButton editar = new JButton("Editar");
        JButton excluir = new JButton("Excluir");
        editar.addActionListener(evt -> editarTarefa(tarefa));
        excluir.addActionListener(evt -> excluirTarefa(tarefa));
        acoes.add(editar);
        acoes.add(excluir);
        card.add(acoes, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                card.getTransferHandler().exportAsDrag(card, evt, TransferHandler.MOVE);
            }
        });

        return card;
    }

    private JTextArea criarTextoCard(String texto, int estiloFonte, int linhas) {
        JTextArea area = new JTextArea(texto == null ? "" : texto);
        area.setToolTipText(texto);
        area.setFont(new java.awt.Font("Liberation Sans", estiloFonte, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFocusable(false);
        area.setOpaque(false);
        area.setBorder(null);
        area.setRows(linhas);
        area.setMinimumSize(new Dimension(0, area.getPreferredSize().height));
        area.setPreferredSize(new Dimension(210, area.getPreferredSize().height));
        return area;
    }

    private void adicionarTarefa(EtapaProjeto etapa) {
        GerenciadorInterfaceGrafica.instancia.solicitarNovaTarefaCompleta(this, etapa.getNome())
                .ifPresent(dados -> {
                    GerenciadorInterfaceGrafica.instancia.adicionarTarefaNaEtapa(etapa, dados.nome(), dados.contribuidores());
                    atualizarQuadro();
                });
    }

    private void editarTarefa(Tarefa tarefa) {
        GerenciadorInterfaceGrafica.instancia.solicitarEdicaoTarefa(this, tarefa)
                .ifPresent(dados -> {
                    GerenciadorInterfaceGrafica.instancia.alterarTarefa(tarefa, dados.nome(), dados.contribuidores());
                    atualizarQuadro();
                });
    }

    private void excluirTarefa(Tarefa tarefa) {
        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Excluir a tarefa \"" + tarefa.getNome() + "\"?",
                "Excluir tarefa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION && GerenciadorInterfaceGrafica.instancia.excluirTarefa(tarefa)) {
            atualizarQuadro();
        }
    }

    private Tarefa encontrarTarefa(Integer tarefaId) {
        for (EtapaProjeto etapa : GerenciadorInterfaceGrafica.instancia.listarEtapasProjetoSelecionado()) {
            for (Tarefa tarefa : etapa.getTarefas()) {
                if (tarefa.getId().equals(tarefaId)) {
                    return tarefa;
                }
            }
        }
        return null;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("PROJETO");
        titulo.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.BOLD, 42));
        titulo.setAlignmentX(0.5f);
        lblNomeProjeto.setAlignmentX(0.5f);
        topo.add(titulo);
        topo.add(lblNomeProjeto);
        add(topo, BorderLayout.NORTH);

        pnlQuadro.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JScrollPane scrollPane = new JScrollPane(pnlQuadro);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private class TarefaTransferHandler extends TransferHandler {
        private final Tarefa tarefa;

        TarefaTransferHandler(Tarefa tarefa) {
            this.tarefa = tarefa;
        }

        @Override
        protected Transferable createTransferable(javax.swing.JComponent c) {
            return new StringSelection(String.valueOf(tarefa.getId()));
        }

        @Override
        public int getSourceActions(javax.swing.JComponent c) {
            return MOVE;
        }
    }

    private class EtapaTransferHandler extends TransferHandler {
        private final EtapaProjeto etapaDestino;

        EtapaTransferHandler(EtapaProjeto etapaDestino) {
            this.etapaDestino = etapaDestino;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            try {
                String valor = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                Tarefa tarefa = encontrarTarefa(Integer.valueOf(valor));
                if (tarefa == null || tarefa.getEtapa().equals(etapaDestino)) {
                    return false;
                }
                boolean movida = GerenciadorInterfaceGrafica.instancia.moverTarefa(tarefa, etapaDestino);
                atualizarQuadro();
                return movida;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(PnlProjeto.this, "Nao foi possivel mover a tarefa.", "Projeto",
                        JOptionPane.ERROR_MESSAGE);
                atualizarQuadro();
                return false;
            }
        }
    }
}
