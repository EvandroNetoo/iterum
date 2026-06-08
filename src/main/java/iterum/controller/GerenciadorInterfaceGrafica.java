package iterum.controller;

import iterum.domain.Contribuidor;
import iterum.domain.EtapaProjeto;
import iterum.domain.Projeto;
import iterum.domain.Tarefa;
import iterum.viewer.FrmPrincipal;
import iterum.viewer.PnlEquipe;
import iterum.viewer.PnlProjeto;
import iterum.viewer.PnlProjetos;
import iterum.viewer.dialog.DlgContribuidorCadastro;
import iterum.viewer.dialog.DlgProjetoCadastro;
import iterum.viewer.dialog.DlgTarefaCadastro;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Window;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.hibernate.HibernateException;

public class GerenciadorInterfaceGrafica {

    FrmPrincipal mainFrame = null;
    public static final GerenciadorInterfaceGrafica instancia = new GerenciadorInterfaceGrafica();
    private GerenciadorDominio gerenciadorDominio;
    private Projeto projetoSelecionado;

    private GerenciadorInterfaceGrafica() {
        try {
            com.formdev.flatlaf.FlatLaf.registerCustomDefaultsSource("iterum");

            com.formdev.flatlaf.FlatLightLaf.setup();

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(FrmPrincipal.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erro ao carregar o tema IFES", ex);
        }

        try {
            gerenciadorDominio = new GerenciadorDominio();
        } catch (HibernateException | ExceptionInInitializerError ex) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao inicializar a conexão com o banco de dados. " + ex.getMessage(),
                    "Inicialização",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
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

        atualizarTela(novaTela);

        CardLayout card = (CardLayout) pnlConteudo.getLayout();
        card.show(pnlConteudo, tela.getNome());
    }

    public GerenciadorDominio getGerenciadorDominio() {
        return gerenciadorDominio;
    }

    @SuppressWarnings("unchecked")
    public List<Projeto> listarProjetos() {
        try {
            return gerenciadorDominio.listar(Projeto.class);
        } catch (HibernateException ex) {
            mostrarErro("Erro ao listar projetos.", ex);
            return List.of();
        }
    }

    public Projeto adicionarProjeto(String nomeProjeto) {
        try {
            Projeto projeto = gerenciadorDominio.inserirProjeto(nomeProjeto);
            if (projetoSelecionado == null) {
                projetoSelecionado = projeto;
            }
            return projeto;
        } catch (HibernateException ex) {
            mostrarErro("Erro ao inserir projeto.", ex);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Contribuidor> listarContribuidores() {
        try {
            return gerenciadorDominio.listar(Contribuidor.class);
        } catch (HibernateException ex) {
            mostrarErro("Erro ao listar contribuidores.", ex);
            return List.of();
        }
    }

    public Contribuidor adicionarContribuidor(String nome, String email) {
        try {
            return gerenciadorDominio.inserirContribuidor(nome, email);
        } catch (HibernateException ex) {
            mostrarErro("Erro ao inserir contribuidor.", ex);
            return null;
        }
    }

    public void selecionarProjeto(Projeto projeto) {
        this.projetoSelecionado = projeto;
    }

    public Projeto getProjetoSelecionado() {
        if (projetoSelecionado == null) {
            List<Projeto> projetos = listarProjetos();
            if (!projetos.isEmpty()) {
                projetoSelecionado = projetos.getFirst();
            }
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
        try {
            return gerenciadorDominio.inserirTarefa(etapa, nomeTarefa);
        } catch (HibernateException ex) {
            mostrarErro("Erro ao inserir tarefa.", ex);
            return null;
        }
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
        return Optional.ofNullable(contribuidor);
    }

    public Optional<String> solicitarNovaTarefa(Component componentePai, String nomeEtapa) {
        Window janelaPai = componentePai == null ? null : SwingUtilities.getWindowAncestor(componentePai);
        DlgTarefaCadastro dialog = new DlgTarefaCadastro(janelaPai, true, nomeEtapa);
        dialog.setVisible(true);
        return dialog.getNomeTarefa();
    }

    private void atualizarTela(JPanel painel) {
        if (painel instanceof PnlProjetos pnlProjetos) {
            pnlProjetos.atualizarListagem();
        } else if (painel instanceof PnlEquipe pnlEquipe) {
            pnlEquipe.atualizarTabela();
        } else if (painel instanceof PnlProjeto pnlProjeto) {
            pnlProjeto.atualizarQuadro();
        }
    }

    private void mostrarErro(String mensagem, Throwable ex) {
        JOptionPane.showMessageDialog(mainFrame,
                mensagem + " " + ex.getMessage(),
                "Banco de dados",
                JOptionPane.ERROR_MESSAGE);
    }
}
