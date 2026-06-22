package iterum.dao;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class GenericDAO {

    public void inserir(Object obj) throws HibernateException {
        Session sessao = null;

        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();
            sessao.save(obj);
            sessao.getTransaction().commit();
        } catch (HibernateException ex) {
            rollback(sessao);
            throw new HibernateException(ex);
        } finally {
            fechar(sessao);
        }
    }

    public void alterar(Object obj) throws HibernateException {
        Session sessao = null;

        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();
            sessao.merge(obj);
            sessao.getTransaction().commit();
        } catch (HibernateException ex) {
            rollback(sessao);
            throw new HibernateException(ex);
        } finally {
            fechar(sessao);
        }
    }

    public void excluir(Object obj) throws HibernateException {
        Session sessao = null;

        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();
            Object persistido = sessao.merge(obj);
            sessao.delete(persistido);
            sessao.getTransaction().commit();
        } catch (HibernateException ex) {
            rollback(sessao);
            throw new HibernateException(ex);
        } finally {
            fechar(sessao);
        }
    }

    public <T> T buscarPorId(Class<T> classe, Integer id) throws HibernateException {
        Session sessao = null;

        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();
            T obj = sessao.get(classe, id);
            sessao.getTransaction().commit();
            return obj;
        } catch (HibernateException ex) {
            rollback(sessao);
            throw new HibernateException(ex);
        } finally {
            fechar(sessao);
        }
    }

    public List listar(Class classe) throws HibernateException {
        Session sessao = null;

        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();

            CriteriaQuery consulta = sessao.getCriteriaBuilder().createQuery(classe);
            consulta.from(classe);
            List lista = sessao.createQuery(consulta).getResultList();

            sessao.getTransaction().commit();
            return lista;
        } catch (HibernateException ex) {
            rollback(sessao);
            throw new HibernateException(ex);
        } finally {
            fechar(sessao);
        }
    }

    public <T> List<T> consultar(String hql, Map<String, Object> parametros, Class<T> classe) throws HibernateException {
        Session sessao = null;

        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();

            org.hibernate.query.Query<T> consulta = sessao.createQuery(hql, classe);
            for (Map.Entry<String, Object> parametro : parametros.entrySet()) {
                consulta.setParameter(parametro.getKey(), parametro.getValue());
            }

            List<T> lista = consulta.getResultList();
            sessao.getTransaction().commit();
            return lista;
        } catch (HibernateException ex) {
            rollback(sessao);
            throw new HibernateException(ex);
        } finally {
            fechar(sessao);
        }
    }

    public <T> List<T> consultar(String hql, Class<T> classe) throws HibernateException {
        return consultar(hql, Collections.emptyMap(), classe);
    }

    public Long contar(String hql, Map<String, Object> parametros) throws HibernateException {
        Session sessao = null;

        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();

            org.hibernate.query.Query<Long> consulta = sessao.createQuery(hql, Long.class);
            for (Map.Entry<String, Object> parametro : parametros.entrySet()) {
                consulta.setParameter(parametro.getKey(), parametro.getValue());
            }

            Long total = consulta.uniqueResult();
            sessao.getTransaction().commit();
            return total == null ? 0L : total;
        } catch (HibernateException ex) {
            rollback(sessao);
            throw new HibernateException(ex);
        } finally {
            fechar(sessao);
        }
    }

    private void rollback(Session sessao) {
        if (sessao != null && sessao.getTransaction().isActive()) {
            sessao.getTransaction().rollback();
        }
    }

    private void fechar(Session sessao) {
        if (sessao != null && sessao.isOpen()) {
            sessao.close();
        }
    }
}
