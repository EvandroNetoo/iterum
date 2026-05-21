package iterum;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HibernateSchemaGenerator {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("iterumPU");
        emf.createEntityManager().close();
        emf.close();
    }
}
