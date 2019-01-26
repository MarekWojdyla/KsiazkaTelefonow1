package KsiazkaTelefonow.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import KsiazkaTelefonow.com.data.DatabaseDescription;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class AddEditFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /* Interfejs z metodą wywołania zwrotnego implementowaną przez główną aktywność */
    public interface AddEditFragmentListener {

        /* Wywołanie, gdy kontakt jest zapisywany */
        void onAddEditCompleted(Uri contactUri);
    }

    /* Pole używane do identyfikacji obiektu Loader */
    private static final int CONTACT_LOADER = 0;

    /* Pole obiektu implementującego zagnieżdżony interfejs - główna aktywność (MainActivity) */
    private AddEditFragmentListener listener;

    /* Adres URI wybranego kontaktu */
    private Uri contactUri;

    /* Dodawanie (true) / edycja (false) kontaktu */
    private boolean addingNewContact = true;

    /* Pola graficznego interfejsu użytkownika */
    private TextInputLayout nameTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout streetTextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout zipTextInputLayout;
    private FloatingActionButton saveContactFAB;

    /* Używany wraz z obiektami Snackbar */
    private CoordinatorLayout coordinatorLayout;

    /* Inicjalizacja obiektu AddEditFragmentListener przy dołączeniu fragmentu do głównej aktywności */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
    }

    /* Usunięcie obiektu AddEditFragmentListener przy odłączeniu fragmentu od głównej aktywności */
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    /* Utworzenie widoku obiektu Fragment */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        /* Przygotowanie elementów graficznego interfejsu użytkownika */
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
        nameTextInputLayout = (TextInputLayout) view.findViewById(R.id.nameTextInputLayout);
        nameTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonFAB();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneTextInputLayout = (TextInputLayout) view.findViewById(R.id.phoneTextInputLayout);
        emailTextInputLayout = (TextInputLayout) view.findViewById(R.id.emailTextInputLayout);
        streetTextInputLayout = (TextInputLayout) view.findViewById(R.id.streetTextInputLayout);
        cityTextInputLayout = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        stateTextInputLayout = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
        zipTextInputLayout = (TextInputLayout) view.findViewById(R.id.zipTextInputLayout);

        /* Przygotowanie przycisku */
        saveContactFAB = (FloatingActionButton) view.findViewById(R.id.saveFloatingActionButton);
        saveContactFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Ukrycie klawiatury ekranowej */
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromInputMethod(getView().getWindowToken(), 0);

                /* Zapisanie kontaktu w bazie danych */
                saveContact();
            }
        });
        updateSaveButtonFAB();

        /* Kod używany do wyświetlania obiektów Snackbar */
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);
        Bundle arguments = getArguments();

        if (arguments != null) {
            addingNewContact = false;
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);
        }

        /* Utworzenie obiektu Loader */
        if (contactUri != null) {
            //noinspection deprecation
            getLoaderManager().initLoader(CONTACT_LOADER, null, this);
        }

        /* Zwrócenie widoku */
        return view;
    }

    /* Wyświetla lub chowa przycisk zapisu kontaktu (FloatingActionButton) */
    private void updateSaveButtonFAB() {

        /* Pobierz tekst z pola EditText */
        String input = nameTextInputLayout.getEditText().getText().toString();

        /* Wyświetl lub schowaj przycisk zapisu kontaktu */
        if (input.trim().length() != 0) {
            saveContactFAB.show();
        } else {
            saveContactFAB.hide();
        }
    }

    /* Zapisuje dane kontaktu w bazie danych */
    private void saveContact() {

        /* Utworzenie i obsadzenie obiektu ContentValues */
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseDescription.Contact.COLUMN_NAME, nameTextInputLayout.getEditText().getText().toString());
        contentValues.put(DatabaseDescription.Contact.COLUMN_PHONE, phoneTextInputLayout.getEditText().getText().toString());
        contentValues.put(DatabaseDescription.Contact.COLUMN_EMAIL, emailTextInputLayout.getEditText().getText().toString());
        contentValues.put(DatabaseDescription.Contact.COLUMN_STREET, streetTextInputLayout.getEditText().getText().toString());
        contentValues.put(DatabaseDescription.Contact.COLUMN_CITY, cityTextInputLayout.getEditText().getText().toString());
        contentValues.put(DatabaseDescription.Contact.COLUMN_STATE, stateTextInputLayout.getEditText().getText().toString());
        contentValues.put(DatabaseDescription.Contact.COLUMN_ZIP, zipTextInputLayout.getEditText().getText().toString());

        /* Zapisanie kontaktu w bazie danych korzystając z obiektu AddressBookContentProvider i jego metody insert */
        if (addingNewContact) {

            /* Dodanie kontaktu */
            Uri newContactUri = getActivity().getContentResolver().insert(DatabaseDescription.Contact.CONTENT_URI, contentValues);

            /* Wyświetlenie obiektów Snackbar */
            if (newContactUri != null) {
                Snackbar.make(coordinatorLayout, R.string.contact_added, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newContactUri);
            } else {
                Snackbar.make(coordinatorLayout, R.string.contact_no_updated, Snackbar.LENGTH_LONG).show();
            }
        }

        /* Aktualizacja kontaktu w bazie danych korzystając z obiektu AddressBookContentProvider i jego metody update */
        else {

            /* Aktualizacja kontaktu */
            int updateRows = getActivity().getContentResolver().update(contactUri, contentValues, null, null);

            /* Wyświetlenie obiektów Snackbar */
            if (updateRows > 0) {
                Snackbar.make(coordinatorLayout, R.string.contact_updated, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(contactUri);
            } else {
                Snackbar.make(coordinatorLayout, R.string.contact_no_updated, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case CONTACT_LOADER:
                return new CursorLoader(getActivity(), contactUri, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        /* Sprawdzenie, czy wybrany kontakt istnieje w bazie danych */
        if (data != null && data.moveToFirst()) {

            /* Odczytanie indeksów kolumn z tabeli */
            int nameIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_NAME);
            int phoneIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_PHONE);
            int emailIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_EMAIL);
            int streetIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_STREET);
            int cityIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_CITY);
            int stateIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_STATE);
            int zipIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_ZIP);

            /* Wypełnienie pól EditText */
            nameTextInputLayout.getEditText().setText(data.getString(nameIndex));
            phoneTextInputLayout.getEditText().setText(data.getString(phoneIndex));
            emailTextInputLayout.getEditText().setText(data.getString(emailIndex));
            streetTextInputLayout.getEditText().setText(data.getString(streetIndex));
            cityTextInputLayout.getEditText().setText(data.getString(cityIndex));
            stateTextInputLayout.getEditText().setText(data.getString(stateIndex));
            zipTextInputLayout.getEditText().setText(data.getString(zipIndex));

            /* Wywołanie metody wyświetlającej przycisk zapisu kontaktu */
            updateSaveButtonFAB();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {


    }

}
