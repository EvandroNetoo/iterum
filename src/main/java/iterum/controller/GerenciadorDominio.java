package iterum.controller;

import iterum.dao.ConexaoHibernate;
import iterum.dao.GenericDAO;
import iterum.domain.Contribuidor;
import iterum.domain.EtapaProjeto;
import iterum.domain.Projeto;
import iterum.domain.Tarefa;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class GerenciadorDominio {

    private final GenericDAO genDAO;

    public GerenciadorDominio() throws HibernateException {
        try (Session sessao = ConexaoHibernate.getSessionFactory().openSession()) {
            // A abertura da sessão valida a configuração e a conexão.
        }
        genDAO = new GenericDAO();
    }

    public List listar(Class classe) throws HibernateException {
        return genDAO.listar(classe);
    }

    public Projeto inserirProjeto(String nome) throws HibernateException {
        Projeto projeto = new Projeto(nome.trim());
        genDAO.inserir(projeto);
        return projeto;
    }

    public Contribuidor inserirContribuidor(String nome, String email) throws HibernateException {
        Contribuidor contribuidor = new Contribuidor(nome.trim(), email.trim());
        genDAO.inserir(contribuidor);
        return contribuidor;
    }

    public Tarefa inserirTarefa(EtapaProjeto etapa, String nome) throws HibernateException {
        Tarefa tarefa = new Tarefa(nome.trim());
        tarefa.setEtapa(etapa);
        genDAO.inserir(tarefa);
        etapa.getTarefas().add(tarefa);
        return tarefa;
    }

}
