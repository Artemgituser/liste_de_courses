package com.example.mescourses.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mescourses.models.Produit;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String BASE_NOM = "mescourses.db";
    private static final int BASE_VERSION = 2;

    // Table produits (Master Catalog)
    public static final String NOM_TABLE = "produits";
    public static final String COL0 = "id";
    public static final String COL1 = "nom";
    public static final String COL2 = "quantite";
    public static final String COL3 = "rayon";
    public static final String COL4 = "statut";
    public static final String COL5 = "category_id";
    // statut : 0=base, 1=courses en cours, 2=archivé

    private static final String CREATE_TABLE_PRODUITS =
        "CREATE TABLE " + NOM_TABLE + " (" +
                COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT NOT NULL, " +
                COL2 + " INTEGER DEFAULT 1, " +
                COL3 + " TEXT, " +
                COL4 + " INTEGER DEFAULT 0, " +
                COL5 + " INTEGER DEFAULT -1" +
        ");";

    // Table categories
    public static final String TABLE_CATEGORIES = "categories";
    public static final String CAT_COL_ID = "id";
    public static final String CAT_COL_NOM = "nom";

    private static final String CREATE_TABLE_CATEGORIES =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    CAT_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CAT_COL_NOM + " TEXT NOT NULL UNIQUE" +
            ");";

    // Table shopping_lists
    public static final String TABLE_LISTS = "shopping_lists";
    public static final String LIST_COL_ID = "id";
    public static final String LIST_COL_NOM = "nom";
    public static final String LIST_COL_DATE_CREATION = "date_creation";
    public static final String LIST_COL_STATUT = "statut"; // 0: active, 1: finished
    public static final String LIST_COL_DATE_FIN = "date_fin";

    private static final String CREATE_TABLE_LISTS =
            "CREATE TABLE " + TABLE_LISTS + " (" +
                    LIST_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LIST_COL_NOM + " TEXT NOT NULL, " +
                    LIST_COL_DATE_CREATION + " TEXT, " +
                    LIST_COL_STATUT + " INTEGER DEFAULT 0, " +
                    LIST_COL_DATE_FIN + " TEXT" +
            ");";

    // Table shopping_list_items
    public static final String TABLE_LIST_ITEMS = "list_items";
    public static final String ITEM_COL_ID = "id";
    public static final String ITEM_COL_LIST_ID = "list_id";
    public static final String ITEM_COL_PRODUIT_ID = "produit_id";
    public static final String ITEM_COL_QUANTITE = "quantite";
    public static final String ITEM_COL_CHECKED = "checked";

    private static final String CREATE_TABLE_LIST_ITEMS =
            "CREATE TABLE " + TABLE_LIST_ITEMS + " (" +
                    ITEM_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ITEM_COL_LIST_ID + " INTEGER, " +
                    ITEM_COL_PRODUIT_ID + " INTEGER, " +
                    ITEM_COL_QUANTITE + " INTEGER DEFAULT 1, " +
                    ITEM_COL_CHECKED + " INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(" + ITEM_COL_LIST_ID + ") REFERENCES " + TABLE_LISTS + "(" + LIST_COL_ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + ITEM_COL_PRODUIT_ID + ") REFERENCES " + NOM_TABLE + "(" + COL0 + ") ON DELETE CASCADE" +
            ");";

    // Singleton
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, BASE_NOM, null, BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUITS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_LISTS);
        db.execSQL(CREATE_TABLE_LIST_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CREATE_TABLE_CATEGORIES);
            db.execSQL(CREATE_TABLE_LISTS);
            db.execSQL(CREATE_TABLE_LIST_ITEMS);
            try {
                db.execSQL("ALTER TABLE " + NOM_TABLE + " ADD COLUMN " + COL5 + " INTEGER DEFAULT -1");
            } catch (Exception e) {}
        }
    }


    public long insertProduit(String nom, int quantite, String rayon, int categoryId) {
        if (produitExiste(nom, rayon)) return -1;

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1,      nom.trim());
        cv.put(COL2, quantite);
        cv.put(COL3,    rayon.trim());
        cv.put(COL4,   Produit.STATUT_BASE);
        cv.put(COL5,   categoryId);
        long id = db.insert(NOM_TABLE, null, cv);
        db.close();
        return id;
    }

    public long insertProduit(String nom, int quantite, String rayon) {
        return insertProduit(nom, quantite, rayon, -1);
    }

   //Vérifie un produit avec son nom+rayon
    public boolean produitExiste(String nom, String rayon) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(NOM_TABLE,
            new String[]{COL0},
            "LOWER(" + COL1 + ")=? AND LOWER(" + COL3 + ")=?",
            new String[]{nom.trim().toLowerCase(), rayon.trim().toLowerCase()},
            null, null, null);
        boolean existe = c.getCount() > 0;
        c.close();
        db.close();
        return existe;
    }


    public List<Produit> getAllProduits() {
        return getByStatut(Produit.STATUT_BASE);
    }


    public List<Produit> getProduitsCourses() {
        return getByStatut(Produit.STATUT_COURSES);
    }


    public List<Produit> getProduitsArchives() {
        return getByStatut(Produit.STATUT_ARCHIVE);
    }


    private List<Produit> getByStatut(int statut) {
        List<Produit> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String orderBy = (statut == Produit.STATUT_ARCHIVE)
            ? COL1 + " COLLATE NOCASE ASC, " + COL3 + " COLLATE NOCASE ASC"
            : COL3 + " COLLATE NOCASE ASC, " + COL1 + " COLLATE NOCASE ASC";

        Cursor c = db.query(NOM_TABLE,
            null,
            COL4 + "=?",
            new String[]{String.valueOf(statut)},
            null, null, orderBy);

        if (c.moveToFirst()) {
            do {
                list.add(cursorToProduit(c));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }


    public Produit getProduitById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(NOM_TABLE, null,
            COL0 + "=?", new String[]{String.valueOf(id)},
            null, null, null);
        Produit p = null;
        if (c.moveToFirst()) p = cursorToProduit(c);
        c.close();
        db.close();
        return p;
    }

//METTRE NOM DE PRODUIT

    public int updateProduit(int id, String nom, int quantite, String rayon, int categoryId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1,      nom.trim());
        cv.put(COL2, quantite);
        cv.put(COL3,    rayon.trim());
        cv.put(COL5,    categoryId);
        int rows = db.update(NOM_TABLE, cv, COL0 + "=?",
            new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public int updateProduit(int id, String nom, int quantite, String rayon) {
        return updateProduit(id, nom, quantite, rayon, -1);
    }

    /** Change le statut d'un produit (base / courses / archivé) */
    public int updateStatut(int id, int newStatut) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL4, newStatut);
        int rows = db.update(NOM_TABLE, cv, COL0 + "=?",
            new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }


    public int deleteProduit(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(NOM_TABLE, COL0 + "=?",
            new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }


    private Produit cursorToProduit(Cursor c) {
        Produit p = new Produit(
            c.getInt(c.getColumnIndexOrThrow(COL0)),
            c.getString(c.getColumnIndexOrThrow(COL1)),
            c.getInt(c.getColumnIndexOrThrow(COL2)),
            c.getString(c.getColumnIndexOrThrow(COL3)),
            c.getInt(c.getColumnIndexOrThrow(COL4))
        );
        int catIdx = c.getColumnIndex(COL5);
        if (catIdx != -1) {
            p.setCategoryId(c.getInt(catIdx));
        }
        return p;
    }

    // --- CATEGORIES ---
    public long insertCategory(String nom) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CAT_COL_NOM, nom.trim());
        long id = db.insertWithOnConflict(TABLE_CATEGORIES, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            id = getCategoryIdByName(nom);
        }
        db.close();
        return id;
    }

    public List<com.example.mescourses.models.Category> getAllCategories() {
        List<com.example.mescourses.models.Category> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_CATEGORIES, null, null, null, null, null, CAT_COL_NOM + " ASC");
        if (c.moveToFirst()) {
            do {
                list.add(new com.example.mescourses.models.Category(
                    c.getInt(c.getColumnIndexOrThrow(CAT_COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(CAT_COL_NOM))
                ));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public int getCategoryIdByName(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_CATEGORIES, new String[]{CAT_COL_ID}, CAT_COL_NOM + "=?", new String[]{name.trim()}, null, null, null);
        int id = -1;
        if (c.moveToFirst()) id = c.getInt(0);
        c.close();
        db.close();
        return id;
    }

    // --- SHOPPING LISTS ---
    public long createShoppingList(String nom) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LIST_COL_NOM, nom);
        cv.put(LIST_COL_DATE_CREATION, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(new java.util.Date()));
        cv.put(LIST_COL_STATUT, 0);
        long id = db.insert(TABLE_LISTS, null, cv);
        db.close();
        return id;
    }

    public void finishShoppingList(int listId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LIST_COL_STATUT, 1);
        cv.put(LIST_COL_DATE_FIN, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(new java.util.Date()));
        db.update(TABLE_LISTS, cv, LIST_COL_ID + "=?", new String[]{String.valueOf(listId)});
        db.close();
    }

    public List<com.example.mescourses.models.ShoppingList> getShoppingLists(int statut) {
        List<com.example.mescourses.models.ShoppingList> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_LISTS, null, LIST_COL_STATUT + "=?", new String[]{String.valueOf(statut)}, null, null, LIST_COL_DATE_CREATION + " DESC");
        if (c.moveToFirst()) {
            do {
                list.add(new com.example.mescourses.models.ShoppingList(
                    c.getInt(c.getColumnIndexOrThrow(LIST_COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(LIST_COL_NOM)),
                    c.getString(c.getColumnIndexOrThrow(LIST_COL_DATE_CREATION)),
                    c.getInt(c.getColumnIndexOrThrow(LIST_COL_STATUT)),
                    c.getString(c.getColumnIndexOrThrow(LIST_COL_DATE_FIN))
                ));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    // --- STATS ---
    public int getTotalProductsPurchased() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + ITEM_COL_QUANTITE + ") FROM " + TABLE_LIST_ITEMS +
                               " JOIN " + TABLE_LISTS + " ON " + TABLE_LIST_ITEMS + "." + ITEM_COL_LIST_ID + " = " + TABLE_LISTS + "." + LIST_COL_ID +
                               " WHERE " + TABLE_LISTS + "." + LIST_COL_STATUT + " = 1", null);
        int total = 0;
        if (c.moveToFirst()) total = c.getInt(0);
        c.close();
        db.close();
        return total;
    }

    public String getLastShoppingDate() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT MAX(" + LIST_COL_DATE_FIN + ") FROM " + TABLE_LISTS + " WHERE " + LIST_COL_STATUT + " = 1", null);
        String date = "Jamais";
        if (c.moveToFirst() && c.getString(0) != null) date = c.getString(0);
        c.close();
        db.close();
        return date;
    }

    // --- SHOPPING LIST ITEMS ---
    public long addItemToList(int listId, int produitId, int quantite) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ITEM_COL_LIST_ID, listId);
        cv.put(ITEM_COL_PRODUIT_ID, produitId);
        cv.put(ITEM_COL_QUANTITE, quantite);
        cv.put(ITEM_COL_CHECKED, 0);
        long id = db.insert(TABLE_LIST_ITEMS, null, cv);
        db.close();
        return id;
    }

    public void updateItemChecked(int itemId, boolean checked) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ITEM_COL_CHECKED, checked ? 1 : 0);
        db.update(TABLE_LIST_ITEMS, cv, ITEM_COL_ID + "=?", new String[]{String.valueOf(itemId)});
        db.close();
    }

    public List<Produit> getItemsForList(int listId) {
        List<Produit> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT p.*, li." + ITEM_COL_QUANTITE + " as li_quantite, li." + ITEM_COL_CHECKED + ", li." + ITEM_COL_ID + " as li_id " +
                       "FROM " + NOM_TABLE + " p " +
                       "JOIN " + TABLE_LIST_ITEMS + " li ON p." + COL0 + " = li." + ITEM_COL_PRODUIT_ID + " " +
                       "WHERE li." + ITEM_COL_LIST_ID + " = ? " +
                       "ORDER BY p." + COL3 + " ASC";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(listId)});
        if (c.moveToFirst()) {
            do {
                Produit p = cursorToProduit(c);
                // Override quantity with list-specific quantity
                p.setQuantite(c.getInt(c.getColumnIndexOrThrow("li_quantite")));
                p.setChecked(c.getInt(c.getColumnIndexOrThrow(ITEM_COL_CHECKED)) == 1);
                // We might need the list item ID for updates, but Produit model uses database ID.
                // For now, this is enough for display.
                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public List<Produit> searchProduits(String query) {
        List<Produit> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(NOM_TABLE, null, COL1 + " LIKE ?", new String[]{"%" + query + "%"}, null, null, COL1 + " ASC");
        if (c.moveToFirst()) {
            do {
                list.add(cursorToProduit(c));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }
}
