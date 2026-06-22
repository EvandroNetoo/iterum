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
        return consultarProjetos("");
    }

    public List<Projeto> consultarProjetos(String termo) {
        try {
            return gerenciadorDominio.consultarProjetos(termo);
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

    public Projeto alterarProjeto(Projeto projeto, String nomeProjeto) {
        try {
            Projeto atualizado = gerenciadorDominio.alterarProjeto(projeto, nomeProjeto);
            if (projetoSelecionado != null && projetoSelecionado.equals(projeto)) {
                projetoSelecionado = atualizado;
            }
            return atualizado;
        } catch (HibernateException ex) {
            mostrarErro("Erro ao alterar projeto.", ex);
            return null;
        }
    }

    public boolean excluirProjeto(Projeto projeto) {
        try {
            gerenciadorDominio.excluirProjeto(projeto);
            if (projetoSelecionado != null && projetoSelecionado.equals(projeto)) {
                projetoSelecionado = null;
            }
            return true;
        } catch (HibernateException ex) {
            mostrarErro("Erro ao excluir projeto.", ex);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Contribuidor> listarContribuidores() {
        return consultarContribuidores("");
    }

    public List<Contribuidor> consultarContribuidores(String termo) {
        try {
            return gerenciadorDominio.consultarContribuidores(termo);
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

    public Contribuidor alterarContribuidor(Contribuidor contribuidor, String nome, String email) {
        try {
            return gerenciadorDominio.alterarContribuidor(contribuidor, nome, email);
        } catch (HibernateException ex) {
            mostrarErro("Erro ao alterar contribuidor.", ex);
            return null;
        }
    }

    public boolean excluirContribuidor(Contribuidor contribuidor) {
        try {
            gerenciadorDominio.excluirContribuidor(contribuidor);
            return true;
        } catch (HibernateException ex) {
            mostrarErro("Erro ao excluir contribuidor.", ex);
            return false;
        }
    }

    public void selecionarProjeto(Projeto projeto) {
        this.projetoSelecionado = projeto;
    }

    public Projeto getProjetoSelecionado() {
        if (projetoSelecionado == null) {
            List<Projeto> projetos = listarProjetos();
            if (!projetos.isEmpty()) {
                projetoSelecionado = projetos.get(0);
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
        return adicionarTarefaNaEtapa(etapa, nomeTarefa, List.of());
    }

    public Tarefa adicionarTarefaNaEtapa(EtapaProjeto etapa, String nomeTarefa, List<Contribuidor> contribuidores) {
        try {
            Tarefa tarefa = gerenciadorDominio.inserirTarefa(etapa, nomeTarefa, contribuidores);
            recarregarProjetoSelecionado();
            return tarefa;
        } catch (HibernateException ex) {
            mostrarErro("Erro ao inserir tarefa.", ex);
            return null;
        }
    }

    public Tarefa alterarTarefa(Tarefa tarefa, String nomeTarefa, List<Contribuidor> contribuidores) {
        try {
            Tarefa atualizada = gerenciadorDominio.alterarTarefa(tarefa, nomeTarefa, contribuidores);
            recarregarProjetoSelecionado();
            return atualizada;
        } catch (HibernateException ex) {
            mostrarErro("Erro ao alterar tarefa.", ex);
            return null;
        }
    }

    public boolean excluirTarefa(Tarefa tarefa) {
        try {
            gerenciadorDominio.excluirTarefa(tarefa);
            recarregarProjetoSelecionado();
            return true;
        } catch (HibernateException ex) {
            mostrarErro("Erro ao excluir tarefa.", ex);
            return false;
        }
    }

    public boolean moverTarefa(Tarefa tarefa, EtapaProjeto etapaDestino) {
        try {
            gerenciadorDominio.moverTarefa(tarefa, etapaDestino);
            recarregarProjetoSelecionado();
            return true;
        } catch (HibernateException ex) {
            mostrarErro("Erro ao mover tarefa.", ex);
            recarregarProjetoSelecionado();
            return false;
        }
    }

    public List<Tarefa> consultarTarefas(Integer projetoId, Integer etapaId, Integer contribuidorId) {
        try {
            return gerenciadorDominio.consultarTarefas(projetoId, etapaId, contribuidorId);
        } catch (HibernateException ex) {
            mostrarErro("Erro ao consultar tarefas.", ex);
            return List.of();
        }
    }

    public long contarProjetos() {
        try {
            return gerenciadorDominio.contarProjetos();
        } catch (HibernateException ex) {
            mostrarErro("Erro ao contar projetos.", ex);
            return 0;
        }
    }

    public long contarContribuidores() {
        try {
            return gerenciadorDominio.contarContribuidores();
        } catch (HibernateException ex) {
            mostrarErro("Erro ao contar colaboradores.", ex);
            return 0;
        }
    }

    public long contarTarefas(boolean concluidas) {
        try {
            return gerenciadorDominio.contarTarefas(concluidas);
        } catch (HibernateException ex) {
            mostrarErro("Erro ao contar tarefas.", ex);
            return 0;
        }
    }

    public Optional<String> solicitarNomeNovoProjeto(Component componentePai) {
        Window janelaPai = componentePai == null ? null : SwingUtilities.getWindowAncestor(componentePai);
        DlgProjetoCadastro dialog = new DlgProjetoCadastro(janelaPai, true);
        dialog.setVisible(true);
        return dialog.getNomeProjeto();
    }

    public Optional<String> solicitarEdicaoProjeto(Component componentePai, Projeto projeto) {
        Window janelaPai = componentePai == null ? null : SwingUtilities.getWindowAncestor(componentePai);
        DlgProjetoCadastro dialog = new DlgProjetoCadastro(janelaPai, true, projeto);
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

    public Optional<Contribuidor> solicitarEdicaoContribuidor(Component componentePai, Contribuidor contribuidor) {
        Window janelaPai = componentePai == null ? null : SwingUtilities.getWindowAncestor(componentePai);
        DlgContribuidorCadastro dialog = new DlgContribuidorCadastro(janelaPai, true, contribuidor);
        dialog.setVisible(true);

        if (!dialog.isConfirmado()) {
            return Optional.empty();
        }

        Contribuidor atualizado = alterarContribuidor(contribuidor, dialog.getNomeInformado(), dialog.getEmailInformado());
        return Optional.ofNullable(atualizado);
    }

    public Optional<String> solicitarNovaTarefa(Component componentePai, String nomeEtapa) {
        Window janelaPai = componentePai == null ? null : SwingUtilities.getWindowAncestor(componentePai);
        DlgTarefaCadastro dialog = new DlgTarefaCadastro(janelaPai, true, nomeEtapa);
        dialog.setVisible(true);
        return dialog.getNomeTarefa();
    }

    public Optional<DadosTarefa> solicitarNovaTarefaCompleta(Component componentePai, String nomeEtapa) {
        Window janelaPai = componentePai == null ? null : SwingUtilities.getWindowAncestor(componentePai);
        DlgTarefaCadastro dialog = new DlgTarefaCadastro(janelaPai, true, nomeEtapa, listarContribuidores());
        dialog.setVisible(true);
        return dialog.getNomeTarefa().map(nome -> new DadosTarefa(nome, dialog.getContribuidoresSelecionados()));
    }

    public Optional<DadosTarefa> solicitarEdicaoTarefa(Component componentePai, Tarefa tarefa) {
        Window janelaPai = componentePai == null ? null : SwingUtilities.getWindowAncestor(componentePai);
        String etapa = tarefa.getEtapa() == null ? "-" : tarefa.getEtapa().getNome();
        DlgTarefaCadastro dialog = new DlgTarefaCadastro(janelaPai, true, etapa, tarefa, listarContribuidores());
        dialog.setVisible(true);
        return dialog.getNomeTarefa().map(nome -> new DadosTarefa(nome, dialog.getContribuidoresSelecionados()));
    }

    private void atualizarTela(JPanel painel) {
        if (painel instanceof PnlProjetos pnlProjetos) {
            pnlProjetos.atualizarListagem();
        } else if (painel instanceof PnlEquipe pnlEquipe) {
            pnlEquipe.atualizarTabela();
        } else if (painel instanceof PnlProjeto pnlProjeto) {
            pnlProjeto.atualizarQuadro();
        } else if (painel instanceof iterum.viewer.PnlDashboard pnlDashboard) {
            pnlDashboard.atualizarDashboard();
        } else if (painel instanceof iterum.viewer.PnlRelatorios pnlRelatorios) {
            pnlRelatorios.atualizarRelatorios();
        }
    }

    private void recarregarProjetoSelecionado() {
        if (projetoSelecionado != null && projetoSelecionado.getId() != null) {
            try {
                projetoSelecionado = gerenciadorDominio.buscarPorId(Projeto.class, projetoSelecionado.getId());
            } catch (HibernateException ex) {
                mostrarErro("Erro ao recarregar projeto selecionado.", ex);
            }
        }
    }

    private void mostrarErro(String mensagem, Throwable ex) {
        JOptionPane.showMessageDialog(mainFrame,
                mensagem + " " + ex.getMessage(),
                "Banco de dados",
                JOptionPane.ERROR_MESSAGE);
    }

    public record DadosTarefa(String nome, List<Contribuidor> contribuidores) {
    }
}
