package KsiazkaTelefonow.com;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;



public class MainActivity  extends AppCompatActivity implements ContactsFragment.ContactsFragmentListener,
        DetailFragment.DetailFragmentListener, AddEditFragment.AddEditFragmentListener {
    /* Klucz przechowujący adres URI kontaktu w obiekcie przekazanym do fragmentu */
    public static final String CONTACT_URI = "contact_uri";

    /* Fragment wyświetlający listę kontaktów z bazy */
    private ContactsFragment contactsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Jeżeli rozkład głównej aktywności zawiera fragment fragmentContainer (content_main.xml),
         * to oznacza używanie rozkładu przeznaczonego dla telefonu. W takiej sytuacji tworzymy
         * i wyświetlamy fragment ContactsFragment. */
        if (savedInstanceState == null && findViewById(R.id.fragmentContainer) != null) {

            /* Utworzenie fragmentu ContactsFragment */
            contactsFragment = new ContactsFragment();

            /* Dodanie fragmentu do rozkładu FrameLayout */
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, contactsFragment);

            /* Wyświetl obiekt ContactsFragment */
            transaction.commit();
        } else {

            /* Uzyskanie odwołania do już istniejącego fragmentu ContactsFragment */
            contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.ContactsFragment);
        }
    }

    /* Utworzenie i wyświetlenie fragmentu DetailFragment */
    private void displayDetailFragment(Uri contactUri, int viewID) {

        /* Utworzenie fragmentu DetailFragment */
        DetailFragment detailFragment = new DetailFragment();

        /* Przekazanie adresu URI jako argumentu fragmentu */
        Bundle arguments = new Bundle();
        arguments.putParcelable(CONTACT_URI, contactUri);
        detailFragment.setArguments(arguments);

        /* Dodanie fragmentu do rozkładu */
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);

        /* Wyświetl obiekt DetailFragment */
        transaction.commit();
    }

    /* Utworzenie i wyświetlenie fragmentu AddEditFragment */
    private void displayAddEditFragment(Uri contactUri, int viewID) {

        /* Utworzenie fragmentu AddEditFragment */
        AddEditFragment addEditFragment = new AddEditFragment();

        /* Jeżeli edytowany jest wcześniej zapisany kontakt, to jako argument przekazywany jest contactUri */
        if (contactUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(CONTACT_URI, contactUri);
            addEditFragment.setArguments(arguments);
        }

        /* Dodanie fragmentu do rozkładu */
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);

        /* Wyświetl obiekt AddEditFragment */
        transaction.commit();
    }

    @Override
    public void onContactSelected(Uri contactUri) {

        /* Wyświetlenie fragmentu DetailFragment dla wybranego kontaktu na telefonie... */
        if (findViewById(R.id.fragmentContainer) != null) {
            displayDetailFragment(contactUri, R.id.fragmentContainer);
        }

        /* ...na tablecie. */
        else {
            getSupportFragmentManager().popBackStack();
            displayDetailFragment(contactUri, R.id.rightPanelContainer);
        }
    }

    @Override
    public void onAddContact() {

        /* Wyświetlenie fragmentu AddEditFragment dla wybranego kontaktu na telefonie... */
        if (findViewById(R.id.fragmentContainer) != null) {
            displayAddEditFragment(null, R.id.fragmentContainer);
        }

        /* ...na tablecie. */
        else {
            displayAddEditFragment(null, R.id.rightPanelContainer);
        }
    }

    @Override
    public void onContactDeleted() {

        /* Usunięcie elementu znajdującego się na szczycie stosu aplikacji */
        getSupportFragmentManager().popBackStack();

        /* Odświeżenie listy kontaktów fragmentu ContactsFragment */
        contactsFragment.updateContactList();
    }

    @Override
    public void onEditContact(Uri contactUri) {

        /* Wyświetlenie fragmentu AddEditFragment dla wybranego kontaktu na telefonie... */
        if (findViewById(R.id.fragmentContainer) != null) {
            displayAddEditFragment(contactUri, R.id.fragmentContainer);
        }

        /* ...na tablecie. */
        else {
            displayAddEditFragment(contactUri, R.id.rightPanelContainer);
        }
    }

    @Override
    public void onAddEditCompleted(Uri contactUri) {

        /* Usunięcie górnego elementu stosu aplikacji */
        getSupportFragmentManager().popBackStack();

        /* Odświeżenie listy kontaktów */
        contactsFragment.updateContactList();

        /* Obsługa aktualizacji na tablecie */
        if (findViewById(R.id.fragmentContainer) == null) {

            /* Usunięcie górnego elementu stosu aplikacji */
            getSupportFragmentManager().popBackStack();

            /* Wyświetlenie kontaktu, który został dodany lub zaktualizowany */
            displayDetailFragment(contactUri, R.id.rightPanelContainer);
        }

    }
}