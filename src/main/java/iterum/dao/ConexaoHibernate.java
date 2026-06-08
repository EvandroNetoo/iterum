package iterum.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ConexaoHibernate {

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Falha ao criar a SessionFactory. " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private ConexaoHibernate() {
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
