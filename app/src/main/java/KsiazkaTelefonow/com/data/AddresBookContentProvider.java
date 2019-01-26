package KsiazkaTelefonow.com.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import KsiazkaTelefonow.com.R;

public class AddresBookContentProvider extends ContentProvider {

    /* Egzamplarz klas - umożliwia obiektowi ContentProvider uzyskanie dostępu do bazy danych */
    private AddresBookDataBaseHelper dbHelper;

    /*Pomocnik obiektu ContentProvider*/
    private static final UriMatcher urimatcher = new UriMatcher(UriMatcher.NO_MATCH);
    /*Stałe obiektu UriMathcer używane w celu określenia operacji do wykonania w bazie danych*/
    private static final int ONE_CONTACT = 1;
    private static final int CONTACTS = 2;

    /*Konfiguracja Obiektu UriMatcher*/
    static {
        /* Adres URI kontaktu o określonym identyfikatorze (#) */
        urimatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.Contact.TABLE_NAME + "/#", ONE_CONTACT);

        /* Adres URI dla całej tabeli kontaktów */
        urimatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.Contact.TABLE_NAME, CONTACTS);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        /* Deklaracja obiektu URI */
        Uri newContactUri = null;

        int deletedRows = 0;
        /* Sprawdzenie, czy adres URI odwołuje się do tabeli "contacts" */
        switch (urimatcher.match(uri)) {
            case ONE_CONTACT:
                deletedRows = dbHelper.getWritableDatabase().delete(
                        DatabaseDescription.Contact.TABLE_NAME,
                        DatabaseDescription.Contact._ID + "=?",
                        new String[]{uri.getLastPathSegment()}
                );
                getContext().getContentResolver().notifyChange(uri, null);
                break;
        }
        return deletedRows;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /* Deklaracja obiektu URI */
        Uri newContactUri = null;

        /* Sprawdzenie, czy adres URI odwołuje się do tabeli "contacts" */
        switch (urimatcher.match(uri)) {
            case CONTACTS:

                /* Wstawienie nowego kontaktu do tabeli */
                long rowId = dbHelper.getWritableDatabase().insert(DatabaseDescription.Contact.TABLE_NAME, null, values);

                /* Tworzenie adresu URI dla dodanego kontaktu */
                /* Jeżeli dodanie się powiodło... */
                if (rowId > 0) {
                    newContactUri = DatabaseDescription.Contact.buildContactUri(rowId);

                    /* Powiadomienie obiektów nasłuchujących zmian w tabeli */
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                /* Jeżeli dodanie się nie powiodło... */
                else {
                    throw new SQLException(getContext().getString(R.string.insert_failed) + uri);
                }

                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_insert_uri) + uri);
        }
        /* Zwrócenie adresu URI */
        return newContactUri;
    }

    @Override
    public boolean onCreate() {
        /* Utworzenie obiektu AddressBookDatabaseHelper */
        dbHelper = new AddresBookDataBaseHelper(getContext());

        /* Operacja utworzenia obiektu ContentProvider została zakończona sukcesem */
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        /* Obiekt SQLiteQueryBuilder służący do tworzenia zapytań SQL */
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DatabaseDescription.Contact.TABLE_NAME);

        /* Wybranie jednego lub wszystkich kontaktów z tabeli */
        switch (urimatcher.match(uri)) {
            case ONE_CONTACT:
                queryBuilder.appendWhere(DatabaseDescription.Contact._ID + "=" + uri.getLastPathSegment());
                break;
            case CONTACTS:
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_query_uri) + uri);
        }
        /* Wykonanie zapytania SQL i inicjalizacja obiektu Cursor */
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);

        /* Konfiguracja obiektu Cursor */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        /* Zwrócenie obiektu Cursor */
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        /* Przyjmuje wartość 1, jeżeli aktualizacja przebiegła pomyślnie; w przeciwnym razie 0 */
        int numberOfRowsUpdated;

        /* Sprawdza adres URI */
        switch (urimatcher.match(uri)) {
            case ONE_CONTACT:

                /* Odczytanie identyfikatora kontaktu, który ma zostać zaktualizowany */
                String id = uri.getLastPathSegment();

                /* Aktualizacja zawartości kontaktu */
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(DatabaseDescription.Contact.TABLE_NAME,
                        values, DatabaseDescription.Contact._ID + "=" + id, selectionArgs);

                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_upadate_uri) + uri);
        }

        /* Jeżeli dokonano aktualizacji, to powiadom obiekty nasłuchujące zmian w bazie danych */
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        /* Zwróć info o aktualizacji */
        return numberOfRowsUpdated;
    }
}
