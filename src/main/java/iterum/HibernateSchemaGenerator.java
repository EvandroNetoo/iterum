package iterum;

import iterum.dao.ConexaoHibernate;
import org.hibernate.Session;

public class HibernateSchemaGenerator {
    public static void main(String[] args) {
        try (Session sessao = ConexaoHibernate.getSessionFactory().openSession()) {
            System.out.println("Esquema do Iterum validado.");
        }
        ConexaoHibernate.getSessionFactory().close();
    }
}
