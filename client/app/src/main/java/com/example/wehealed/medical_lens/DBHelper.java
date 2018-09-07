package com.example.wehealed.medical_lens;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static SQLiteDatabase db = null;
    private static DBHelper dbHelperInstance = null;

    private static final String CLASSNAME = DBHelper.class.getSimpleName();

    private static final String DB_NAME = "WeHealed_Medical_Lens.db";
    private static final int DB_VERSION = 9;

    public static String myEmailAddress = "";
    public static String myName = "";

    private DBHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static void initialize(Context context) {

        if (dbHelperInstance == null) {
            dbHelperInstance = new DBHelper(context);

            try {
                db = dbHelperInstance.getWritableDatabase();
                Log.i(Constants.LOG_TAG, DBHelper.CLASSNAME + " instance of database " + DB_NAME + " opened");
            }
            catch (SQLiteException e) {
                Log.i(Constants.LOG_TAG, "Could not create and/or open database");
            }
        }
    }

    public static final DBHelper getInstance(Context context) {
        initialize(context);
        return dbHelperInstance;
    }

    public void close() {
        if (dbHelperInstance != null) {
            Log.i(Constants.LOG_TAG, DBHelper.CLASSNAME + " close the database " + DB_NAME + "");
            db.close();
            dbHelperInstance = null;
        }
    }

    public Cursor get(String table, String[] columns) {
        if (db != null) {
            return db.query(table, columns, null, null, null, null, null);
        }
        else {
            return null;
        }
    }

    public Cursor get(String sql) {
        if (db != null) {
            return db.rawQuery(sql, null);
        }
        else {
            return null;
        }
    }

    public void exec(String sql) {
        if (db != null) {
            db.execSQL(sql);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE MY_INFO_V5 (EMAIL_ADDRESS TEXT PRIMARY KEY, PASSWORD TEXT, HP INTEGER);");
        Log.i(Constants.LOG_TAG, "DBHelper onCreate CREATE TABLE MY_INFO_V5");

        db.execSQL("INSERT INTO MY_INFO_V5 VALUES ('contact@wehealed.com', '1', 1000);");
        Log.i(Constants.LOG_TAG, "DBHelper onCreate INSERT INTO MY_INFO_V5");

        db.execSQL("CREATE TABLE PICTURE_HISTORY_V5 (HISTORY_ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", PICTURE_PATH_AND_FILE_NAME TEXT, PICTURE_FILE_NAME TEXT, PICTURE_TIME INTEGER" +
                ", ORIGINAL_TEXT TEXT" +
                ", MACHINE_TRANSLATION_RESULT TEXT" +
                ", HUMAN_TRANSLATION_REQUESTED TEXT, HUMAN_TRANSLATION_REQUEST_TIME INTEGER, HUMAN_TRANSLATION_RESPONSE_TIME INTEGER, HUMAN_TRANSLATION_RESULT TEXT, HUMAN_TRANSLATION_CONFIRMED TEXT" +
                ", SUMMARY_TEXT TEXT, SUMMARY_SENTENCE_NUMBER INTEGER " +
                ");");
        Log.i(Constants.LOG_TAG, "DBHelper onCreate CREATE TABLE PICTURE_HISTORY_V5");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS MY_INFO_V1");
        db.execSQL("DROP TABLE IF EXISTS MY_INFO_V2");
        db.execSQL("DROP TABLE IF EXISTS MY_INFO_V3");
        db.execSQL("DROP TABLE IF EXISTS MY_INFO_V4");
        db.execSQL("DROP TABLE IF EXISTS MY_INFO_V5");
        db.execSQL("DROP TABLE IF EXISTS PICTURE_HISTORY_V1");
        db.execSQL("DROP TABLE IF EXISTS PICTURE_HISTORY_V2");
        db.execSQL("DROP TABLE IF EXISTS PICTURE_HISTORY_V3");
        db.execSQL("DROP TABLE IF EXISTS PICTURE_HISTORY_V4");
        db.execSQL("DROP TABLE IF EXISTS PICTURE_HISTORY_V5");

        onCreate(db);
    }

}
