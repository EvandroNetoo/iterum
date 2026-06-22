package iterum.controller;

import iterum.dao.ConexaoHibernate;
import iterum.dao.GenericDAO;
import iterum.domain.Contribuidor;
import iterum.domain.EtapaProjeto;
import iterum.domain.Projeto;
import iterum.domain.Tarefa;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public <T> T buscarPorId(Class<T> classe, Integer id) throws HibernateException {
        return genDAO.buscarPorId(classe, id);
    }

    public Projeto inserirProjeto(String nome) throws HibernateException {
        Projeto projeto = new Projeto(nome.trim());
        genDAO.inserir(projeto);
        return projeto;
    }

    public Projeto alterarProjeto(Projeto projeto, String nome) throws HibernateException {
        Projeto persistido = buscarPorId(Projeto.class, projeto.getId());
        persistido.setNome(nome.trim());
        genDAO.alterar(persistido);
        return buscarPorId(Projeto.class, persistido.getId());
    }

    public void excluirProjeto(Projeto projeto) throws HibernateException {
        genDAO.excluir(projeto);
    }

    public List<Projeto> consultarProjetos(String termo) throws HibernateException {
        String filtro = termo == null ? "" : termo.trim().toLowerCase();
        if (filtro.isBlank()) {
            return listar(Projeto.class);
        }

        return genDAO.consultar(
                "select p from Projeto p where lower(p.nome) like :termo order by p.nome",
                Map.of("termo", "%" + filtro + "%"),
                Projeto.class);
    }

    public Contribuidor inserirContribuidor(String nome, String email) throws HibernateException {
        Contribuidor contribuidor = new Contribuidor(nome.trim(), email.trim());
        genDAO.inserir(contribuidor);
        return contribuidor;
    }

    public Contribuidor alterarContribuidor(Contribuidor contribuidor, String nome, String email)
            throws HibernateException {
        Contribuidor persistido = buscarPorId(Contribuidor.class, contribuidor.getId());
        persistido.setNome(nome.trim());
        persistido.setEmail(email.trim());
        genDAO.alterar(persistido);
        return buscarPorId(Contribuidor.class, persistido.getId());
    }

    public void excluirContribuidor(Contribuidor contribuidor) throws HibernateException {
        Session sessao = null;

        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();

            Contribuidor persistido = sessao.get(Contribuidor.class, contribuidor.getId());
            if (persistido != null) {
                for (Tarefa tarefa : new ArrayList<>(persistido.getTarefas())) {
                    tarefa.getContribuidores().remove(persistido);
                }
                persistido.getTarefas().clear();
                sessao.delete(persistido);
            }

            sessao.getTransaction().commit();
        } catch (HibernateException ex) {
            if (sessao != null && sessao.getTransaction().isActive()) {
                sessao.getTransaction().rollback();
            }
            throw new HibernateException(ex);
        } finally {
            if (sessao != null && sessao.isOpen()) {
                sessao.close();
            }
        }
    }

    public List<Contribuidor> consultarContribuidores(String termo) throws HibernateException {
        String filtro = termo == null ? "" : termo.trim().toLowerCase();
        if (filtro.isBlank()) {
            return listar(Contribuidor.class);
        }

        return genDAO.consultar(
                "select c from Contribuidor c where lower(c.nome) like :termo or lower(c.email) like :termo order by c.nome",
                Map.of("termo", "%" + filtro + "%"),
                Contribuidor.class);
    }

    public Tarefa inserirTarefa(EtapaProjeto etapa, String nome) throws HibernateException {
        return inserirTarefa(etapa, nome, List.of());
    }

    public Tarefa inserirTarefa(EtapaProjeto etapa, String nome, List<Contribuidor> contribuidores)
            throws HibernateException {
        Session sessao = null;

        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();

            EtapaProjeto etapaPersistida = sessao.get(EtapaProjeto.class, etapa.getId());
            Tarefa tarefa = new Tarefa(nome.trim());
            tarefa.setEtapa(etapaPersistida);
            etapaPersistida.getTarefas().add(tarefa);

            if (contribuidores != null) {
                for (Contribuidor contribuidor : contribuidores) {
                    Contribuidor persistido = sessao.get(Contribuidor.class, contribuidor.getId());
                    if (persistido != null && !tarefa.getContribuidores().contains(persistido)) {
                        tarefa.getContribuidores().add(persistido);
                        persistido.getTarefas().add(tarefa);
                    }
                }
            }

            sessao.save(tarefa);
            sessao.getTransaction().commit();
            return buscarPorId(Tarefa.class, tarefa.getId());
        } catch (HibernateException ex) {
            if (sessao != null && sessao.getTransaction().isActive()) {
                sessao.getTransaction().rollback();
            }
            throw new HibernateException(ex);
        } finally {
            if (sessao != null && sessao.isOpen()) {
                sessao.close();
            }
        }
    }

    public Tarefa alterarTarefa(Tarefa tarefa, String nome, List<Contribuidor> contribuidores)
            throws HibernateException {
        Session sessao = null;

        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();

            Tarefa persistida = sessao.get(Tarefa.class, tarefa.getId());
            persistida.setNome(nome.trim());
            persistida.getContribuidores().clear();

            if (contribuidores != null) {
                for (Contribuidor contribuidor : contribuidores) {
                    Contribuidor persistido = sessao.get(Contribuidor.class, contribuidor.getId());
                    if (persistido != null && !persistida.getContribuidores().contains(persistido)) {
                        persistida.getContribuidores().add(persistido);
                        persistido.getTarefas().add(persistida);
                    }
                }
            }

            sessao.merge(persistida);
            sessao.getTransaction().commit();
            return buscarPorId(Tarefa.class, persistida.getId());
        } catch (HibernateException ex) {
            if (sessao != null && sessao.getTransaction().isActive()) {
                sessao.getTransaction().rollback();
            }
            throw new HibernateException(ex);
        } finally {
            if (sessao != null && sessao.isOpen()) {
                sessao.close();
            }
        }
    }

    public void excluirTarefa(Tarefa tarefa) throws HibernateException {
        Session sessao = null;

        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();

            Tarefa persistida = sessao.get(Tarefa.class, tarefa.getId());
            if (persistida != null) {
                if (persistida.getEtapa() != null) {
                    persistida.getEtapa().getTarefas().remove(persistida);
                }
                for (Contribuidor contribuidor : new ArrayList<>(persistida.getContribuidores())) {
                    contribuidor.getTarefas().remove(persistida);
                }
                persistida.getContribuidores().clear();
                sessao.delete(persistida);
            }

            sessao.getTransaction().commit();
        } catch (HibernateException ex) {
            if (sessao != null && sessao.getTransaction().isActive()) {
                sessao.getTransaction().rollback();
            }
            throw new HibernateException(ex);
        } finally {
            if (sessao != null && sessao.isOpen()) {
                sessao.close();
            }
        }
    }

    public Tarefa moverTarefa(Tarefa tarefa, EtapaProjeto etapaDestino) throws HibernateException {
        Session sessao = null;

        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();

            Tarefa persistida = sessao.get(Tarefa.class, tarefa.getId());
            EtapaProjeto destino = sessao.get(EtapaProjeto.class, etapaDestino.getId());
            persistida.setEtapa(destino);

            sessao.merge(persistida);
            sessao.getTransaction().commit();
            return buscarPorId(Tarefa.class, persistida.getId());
        } catch (HibernateException ex) {
            if (sessao != null && sessao.getTransaction().isActive()) {
                sessao.getTransaction().rollback();
            }
            throw new HibernateException(ex);
        } finally {
            if (sessao != null && sessao.isOpen()) {
                sessao.close();
            }
        }
    }

    public List<Tarefa> consultarTarefas(Integer projetoId, Integer etapaId, Integer contribuidorId)
            throws HibernateException {
        StringBuilder hql = new StringBuilder("select distinct t from Tarefa t left join t.contribuidores c where 1=1");
        Map<String, Object> parametros = new HashMap<>();

        if (projetoId != null) {
            hql.append(" and t.etapa.projeto.id = :projetoId");
            parametros.put("projetoId", projetoId);
        }
        if (etapaId != null) {
            hql.append(" and t.etapa.id = :etapaId");
            parametros.put("etapaId", etapaId);
        }
        if (contribuidorId != null) {
            hql.append(" and c.id = :contribuidorId");
            parametros.put("contribuidorId", contribuidorId);
        }

        hql.append(" order by t.nome");
        return genDAO.consultar(hql.toString(), parametros, Tarefa.class);
    }

    public long contarProjetos() throws HibernateException {
        return genDAO.contar("select count(p) from Projeto p", Map.of());
    }

    public long contarContribuidores() throws HibernateException {
        return genDAO.contar("select count(c) from Contribuidor c", Map.of());
    }

    public long contarTarefas(boolean concluidas) throws HibernateException {
        return genDAO.contar(
                "select count(t) from Tarefa t where t.etapa.etapaDeConclucao = :concluidas",
                Map.of("concluidas", concluidas));
    }

}
