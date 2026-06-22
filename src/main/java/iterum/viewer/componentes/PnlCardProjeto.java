package iterum.viewer.componentes;

import iterum.controller.GerenciadorInterfaceGrafica;
import iterum.controller.Tela;
import iterum.domain.Contribuidor;
import iterum.domain.EtapaProjeto;
import iterum.domain.Projeto;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class PnlCardProjeto extends JPanel {

    private final Projeto projeto;
    private final JLabel lblTitulo = new JLabel("-", javax.swing.SwingConstants.CENTER);
    private final JLabel lblTarefas = new JLabel("0 tarefas", javax.swing.SwingConstants.CENTER);
    private final JLabel lblEtapas = new JLabel("0 etapas", javax.swing.SwingConstants.CENTER);
    private final JLabel lblMembros = new JLabel("0 colaboradores", javax.swing.SwingConstants.CENTER);
    private final JProgressBar pbProgresso = new JProgressBar();

    public PnlCardProjeto() {
        this(null);
    }

    public PnlCardProjeto(Projeto projeto) {
        this.projeto = projeto;
        initComponents();
        aplicarProjetoNoCard();
    }

    private void aplicarProjetoNoCard() {
        if (projeto == null) {
            return;
        }

        lblTitulo.setText(projeto.getNome());

        int totalTarefas = projeto.getEtapas().stream()
                .map(EtapaProjeto::getTarefas)
                .mapToInt(java.util.List::size)
                .sum();
        int concluidas = projeto.getEtapas().stream()
                .filter(EtapaProjeto::isEtapaDeConclucao)
                .map(EtapaProjeto::getTarefas)
                .mapToInt(java.util.List::size)
                .sum();
        int colaboradores = projeto.getEtapas().stream()
                .flatMap(etapa -> etapa.getTarefas().stream())
                .flatMap(tarefa -> tarefa.getContribuidores().stream())
                .map(Contribuidor::getId)
                .collect(Collectors.toSet())
                .size();

        pbProgresso.setValue(totalTarefas == 0 ? 0 : Math.round((concluidas * 100f) / totalTarefas));
        lblTarefas.setText(totalTarefas + " tarefas | " + concluidas + " concluidas");
        lblEtapas.setText("Etapas: " + projeto.getEtapas().size());
        lblMembros.setText(colaboradores + " colaboradores");
    }

    private void initComponents() {
        setBackground(javax.swing.UIManager.getColor("Button.background"));
        setBorder(new javax.swing.border.LineBorder(javax.swing.UIManager.getColor("Button.borderColor"), 2, true));
        setMaximumSize(new java.awt.Dimension(230, 150));
        setMinimumSize(new java.awt.Dimension(230, 150));
        setPreferredSize(new java.awt.Dimension(230, 150));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        lblTitulo.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.BOLD, 22));
        JPanel titulo = new JPanel(new GridLayout(1, 1));
        titulo.setBackground(getBackground());
        titulo.add(lblTitulo);
        add(titulo);

        pbProgresso.setStringPainted(true);
        pbProgresso.setBorder(null);
        add(pbProgresso);

        JPanel detalhes = new JPanel(new GridLayout(3, 1));
        detalhes.setBackground(getBackground());
        detalhes.add(lblTarefas);
        detalhes.add(lblEtapas);
        detalhes.add(lblMembros);
        add(detalhes);

        java.awt.event.MouseAdapter mouse = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                abrirProjeto();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        };
        addMouseListener(mouse);
        pbProgresso.addMouseListener(mouse);
    }

    private void abrirProjeto() {
        if (projeto == null) {
            return;
        }
        GerenciadorInterfaceGrafica.instancia.selecionarProjeto(projeto);
        GerenciadorInterfaceGrafica.instancia.abrirTela(Tela.PROJETO);
    }
}
