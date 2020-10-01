package pl.nethos.rekrutacja.kontoBankowe;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.nethos.rekrutacja.kontrahent.Kontrahent;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@Transactional
public class KontoBankoweRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<KontoBankowe> all() {
        return entityManager.createQuery("SELECT k FROM KontoBankowe k", KontoBankowe.class).getResultList();
    }

    public List<KontoBankowe> findByKontrahent(long id) {
        return entityManager.createQuery("SELECT k FROM KontoBankowe k WHERE k.idKontrahent LIKE :id", KontoBankowe.class).setParameter("id", id).getResultList();
    }

    public void update(KontoBankowe kontoBankowe) {
        entityManager.createQuery("UPDATE KontoBankowe SET dataWeryfikacji = :data, stanWeryfikacji = :stan WHERE id LIKE :id")
                .setParameter("data", kontoBankowe.getDataWeryfikacji())
                .setParameter("id", kontoBankowe.getId())
                .setParameter("stan", kontoBankowe.isStanWeryfikacji())
                .executeUpdate();
        System.out.print(kontoBankowe.toString());
    }
}
