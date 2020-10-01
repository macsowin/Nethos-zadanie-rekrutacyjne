package pl.nethos.rekrutacja.kontoBankowe;

import javax.persistence.*;

@Entity
public class KontoBankowe {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kontoBankowe_gen")
    @SequenceGenerator(name = "kontoBankowe_gen", sequenceName = "kontoBankowe_seq", allocationSize = 1)
    private long id;
    private long idKontrahent;
    private String numer;
    private int aktywne;
    private int domyslne;
    private int wirtualne;
    private Integer stanWeryfikacji;
    private java.sql.Timestamp dataWeryfikacji;

    public long getId() {
        return id;
    }

    public long getIdKontrahent() {
        return idKontrahent;
    }

    public String getNumer() {
        return numer;
    }

    public int isAktywne() {
        return aktywne;
    }

    public int isDomyslne() {
        return domyslne;
    }

    public int isWirtualne() {
        return wirtualne;
    }

    public Integer isStanWeryfikacji() {
        return stanWeryfikacji;
    }

    public void setStanWeryfikacji(Integer stanWeryfikacji) {
        this.stanWeryfikacji = stanWeryfikacji;
    }

    public java.sql.Timestamp getDataWeryfikacji() {
        return dataWeryfikacji;
    }

    public void setDataWeryfikacji(java.sql.Timestamp dataWeryfikacji) {
        this.dataWeryfikacji = dataWeryfikacji;
    }
}
