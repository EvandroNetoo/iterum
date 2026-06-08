package iterum.dao;

import java.util.List;
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
