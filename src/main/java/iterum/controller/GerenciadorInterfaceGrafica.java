package iterum.controller;

import iterum.domain.Contribuidor;
import iterum.domain.EtapaProjeto;
import iterum.domain.Projeto;
import iterum.domain.Tarefa;
import iterum.viewer.FrmPrincipal;
import iterum.viewer.dialog.DlgContribuidorCadastro;
import iterum.viewer.dialog.DlgProjetoCadastro;
import iterum.viewer.dialog.DlgTarefaCadastro;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GerenciadorInterfaceGrafica {

    FrmPrincipal mainFrame = null;
    public static final GerenciadorInterfaceGrafica instancia = new GerenciadorInterfaceGrafica();
    private final List<Projeto> projetos = new ArrayList<>();
    private final List<Contribuidor> contribuidores = new ArrayList<>();
    private Projeto projetoSelecionado;

    private GerenciadorInterfaceGrafica() {
        try {
            com.formdev.flatlaf.FlatLaf.registerCustomDefaultsSource("iterum");

            com.formdev.flatlaf.FlatLightLaf.setup();

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(FrmPrincipal.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erro ao carregar o tema IFES", ex);
        }

        carregarDadosIniciais();
    }

    public void abrirMainFrame() {
        if (mainFrame == null) {
            mainFrame = new FrmPrincipal();
        }

        mainFrame.setVisible(true);
        GerenciadorInterfaceGrafica.instancia.abrirTela(Tela.DASHBOARD);
    }

    public void definirMainFrame(FrmPrincipal frame) {
        this.mainFrame = frame;
    }

    public void abrirTela(Tela tela) {
        if (mainFrame == null) {
            abrirMainFrame();
            return;
        }

        JPanel pnlConteudo = mainFrame.getPnlConteudo();

        JPanel novaTela = tela.getPanel();

        if (novaTela.getParent() == null) {
            pnlConteudo.add(novaTela, tela.getNome());
            pnlConteudo.revalidate();
        }

        CardLayout card = (CardLayout) pnlConteudo.getLayout();
        card.show(pnlConteudo, tela.getNome());
    }

    public List<Projeto> listarProjetos() {
        return Collections.unmodifiableList(projetos);
    }

    public Projeto adicionarProjeto(String nomeProjeto) {
        Projeto projeto = new Projeto(projetos.size() + 1, nomeProjeto.trim());
        projetos.add(projeto);

        if (projetoSelecionado == null) {
            projetoSelecionado = projeto;
        }

        return projeto;
    }

    public List<Contribuidor> listarContribuidores() {
        return Collections.unmodifiableList(contribuidores);
    }

    public Contribuidor adicionarContribuidor(String nome, String email) {
        Contribuidor contribuuidor = new Contribuidor(contribuidores.size() + 1, nome.trim(), email.trim());
        contribuidores.add(contribuuidor);
        return contribuuidor;
    }

    public void selecionarProjeto(Projeto projeto) {
        this.projetoSelecionado = projeto;
    }

    public Projeto getProjetoSelecionado() {
        if (projetoSelecionado == null && !projetos.isEmpty()) {
            projetoSelecionado = projetos.getFirst();
        }
        return projetoSelecionado;
    }

    public List<EtapaProjeto> listarEtapasProjetoSelecionado() {
        Projeto projeto = getProjetoSelecionado();
        if (projeto == null) {
            return List.of();
        }
        return projeto.getEtapas();
    }

    public Tarefa adicionarTarefaNaEtapa(EtapaProjeto etapa, String nomeTarefa) {
        int novoId = etapa.getTarefas().size() + 1;
        Tarefa tarefa = new Tarefa(novoId, nomeTarefa.trim());
        etapa.getTarefas().add(tarefa);
        return tarefa;
    }

    public Optional<String> solicitarNomeNovoProjeto(Component componentePai) {
        Window janelaPai = componentePai == null ? null : SwingUtilities.getWindowAncestor(componentePai);
        DlgProjetoCadastro dialog = new DlgProjetoCadastro(janelaPai, true);
        dialog.setVisible(true);
        return dialog.getNomeProjeto();
    }

    public Optional<Contribuidor> solicitarNovoContribuidor(Component componentePai) {
        Window janelaPai = componentePai == null ? null : SwingUtilities.getWindowAncestor(componentePai);
        DlgContribuidorCadastro dialog = new DlgContribuidorCadastro(janelaPai, true);
        dialog.setVisible(true);

        if (!dialog.isConfirmado()) {
            return Optional.empty();
        }

        Contribuidor contribuidor = adicionarContribuidor(dialog.getNomeInformado(), dialog.getEmailInformado());
        return Optional.of(contribuidor);
    }

    public Optional<String> solicitarNovaTarefa(Component componentePai, String nomeEtapa) {
        Window janelaPai = componentePai == null ? null : SwingUtilities.getWindowAncestor(componentePai);
        DlgTarefaCadastro dialog = new DlgTarefaCadastro(janelaPai, true, nomeEtapa);
        dialog.setVisible(true);
        return dialog.getNomeTarefa();
    }

    private void carregarDadosIniciais() {
        Projeto appMobile = new Projeto(1, "App Mobile");
        Projeto portalCliente = new Projeto(2, "Portal do Cliente");
        Projeto biOperacional = new Projeto(3, "BI Operacional");

        projetos.add(appMobile);
        projetos.add(portalCliente);
        projetos.add(biOperacional);
        projetoSelecionado = appMobile;

        contribuidores.add(new Contribuidor(1, "Ana Ribeiro", "ana@ifes.edu.br"));
        contribuidores.add(new Contribuidor(2, "Mateus Silva", "mateus@ifes.edu.br"));
        contribuidores.add(new Contribuidor(3, "Livia Souza", "livia@ifes.edu.br"));
    }

}
