package KsiazkaTelefonow.com.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {
    /* Nazwa obiektu ConstreinProvider */
    public static final String AUTHORITY = "KsiazkaTelefonow.com.data";

    /*Adres URI używany do nawiazania interakcji z obiektem ContentProvider*/
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private DatabaseDescription() {}

    public static final class Contact implements BaseColumns {

        /*Nazwa Tabeli*/
        public static final String TABLE_NAME ="content";

        /*Adres tabeli */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        /*Nazwy kolumn tabeli*/
        public static  final String COLUMN_NAME   = "name";
        public static  final String COLUMN_PHONE  = "phone";
        public static  final String COLUMN_EMAIL  = "email";
        public static  final String COLUMN_STREET = "street";
        public static  final String COLUMN_CITY   = "city";
        public static  final String COLUMN_STATE  = "state";
        public static  final String COLUMN_ZIP    = "zip";

        /*Metoda do tworzenia adresu dla nowego kontaktu*/
        public static Uri buildContactUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

}
