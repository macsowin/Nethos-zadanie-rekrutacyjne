package pl.nethos.rekrutacja;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Autowired;
import pl.nethos.rekrutacja.kontoBankowe.KontoBankowe;
import pl.nethos.rekrutacja.kontoBankowe.KontoBankoweRepository;
import pl.nethos.rekrutacja.kontrahent.Kontrahent;
import pl.nethos.rekrutacja.kontrahent.KontrahentRepository;
import pl.nip24.client.IBANStatus;
import pl.nip24.client.NIP24Client;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Route
@PWA(name = "Nethos - Zadanie rekrutacyjne na stanowisko programisty", shortName = "Nethos - Rekrutacja")
public class MainView extends VerticalLayout {

    public MainView(@Autowired KontrahentRepository kontrahentRepository, @Autowired KontoBankoweRepository kontoBankoweRepository) {
        setSizeFull();

        wyswietl(kontrahentRepository, kontoBankoweRepository);
        //wyswietl2(kontoBankoweRepository);
    }

    //public abstract void wyswietl2();

    private void wyswietl(KontrahentRepository kontrahentRepository, KontoBankoweRepository kontoBankoweRepository) {

        //add(new Label(kontrahent.toString()));
        List<Kontrahent> kontrahentList = kontrahentRepository.all();

        Grid<Kontrahent> grid = new Grid<>(Kontrahent.class);
        grid.setItems(kontrahentList);
        grid.removeColumnByKey("id");

//        grid.setSelectionMode(Grid.SelectionMode.NONE);
//
//        grid.setItemDetailsRenderer(
//                new ComponentRenderer<>(e -> {
//                    wyswietlKonta(kontoBankoweRepository, 1);
//
//                }));


        add(grid);

        grid.addItemClickListener(event ->
                wyswietlKonta(kontoBankoweRepository, event.getItem()));


    }

    private void wyswietlKonta(KontoBankoweRepository kontoBankoweRepository, Kontrahent kontrahent) {

        List<KontoBankowe> kontoBankoweList = kontoBankoweRepository.findByKontrahent(kontrahent.getId());


        Grid<KontoBankowe> grid = new Grid<>(KontoBankowe.class);


        grid.setItems(kontoBankoweList);

        grid.removeAllColumns();

        //grid.setColumns("czy aktywne?", "czy domyślne?", "czy wirtualne?", "stan weryfikacji");

        grid.addColumn(kb -> new StringBuilder(kb.getNumer().substring(0,2))
                .append(" ")
                .append(kb.getNumer().substring(2,6))
                .append(" ")
                .append(kb.getNumer().substring(6,10))
                .append(" ")
                .append(kb.getNumer().substring(10,14))
                .append(" ")
                .append(kb.getNumer().substring(14,18))
                .append(" ")
                .append(kb.getNumer().substring(18,22))
                .append(" ")
                .append(kb.getNumer().substring(22,26)).toString()).setHeader("Numer rachunku bankowego");

        grid.addColumn(kb -> kb.isAktywne()==1 ? "TAK" : "NIE").setHeader("Czy konto aktywne?");

        grid.addColumn(kb -> kb.isDomyslne()==1 ? "TAK" : "NIE").setHeader("Czy domyślne?");

        grid.addColumn(kb -> kb.isWirtualne()==1 ? "TAK" : "NIE").setHeader("Czy wirtualne?");

        grid.addColumn(kb -> kb.isStanWeryfikacji() == null || kb.isStanWeryfikacji()==0 ? "nie określono" : "zweryfikowano" ).setHeader("Status weryfikacji");

        grid.addColumn(kb-> kb.getDataWeryfikacji());

        grid.addColumn(new NativeButtonRenderer<>("Sprawdź!", item -> {
            try {
                NIP24Client nip24 = new NIP24Client();
                IBANStatus iban = nip24.getIBANStatus(kontrahent.getNip(), item.getNumer());

                if (iban != null) {
                    System.out.println("NIP kontrahenta: " + iban.getNIP());
                    System.out.println("Numer rachunku bankowego: " + iban.getIBAN());
                    System.out.println("Status konta bankowego: " + iban.isValid());
                    item.setStanWeryfikacji(iban.isValid() ? 1 : 0);
                    item.setDataWeryfikacji(Timestamp.from(iban.getDate().toInstant()));
                    System.out.println("Identyfikator MF (requestId): " + iban.getID());
                    System.out.println("Data sprawdzenia: " + iban.getDate());
                    System.out.println("Źródło: " + iban.getSource());
                } else {
                    System.out.println("Błąd: " + nip24.getLastError());
                    item.setStanWeryfikacji(null);
                    item.setDataWeryfikacji(Timestamp.from(new Date(System.currentTimeMillis()).toInstant()));
                }
                ;
            } catch (Exception e) {
                e.printStackTrace();
                item.setStanWeryfikacji(null);
                item.setDataWeryfikacji(Timestamp.from(new Date(System.currentTimeMillis()).toInstant()));
            } finally {
                kontoBankoweRepository.update(item);
                grid.getDataProvider().refreshItem(item);
                System.out.println("finally");
            }

        }));


        add(grid);
    }
}
