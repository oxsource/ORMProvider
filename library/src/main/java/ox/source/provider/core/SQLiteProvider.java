package ox.source.provider.core;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ox.source.provider.anno.DataBase;

/**
 * @author FengPeng
 * @date 2017/2/25
 */
public class SQLiteProvider extends ContentProvider {
    private final DataBase dbAno;
    private SQLiteDatabase sqLite;

    protected SQLiteProvider() {
        if ((dbAno = getBuildClass().getAnnotation(DataBase.class)) == null) {
            throw new SQLiteException("'DataBase' annotation missing");
        }
    }

    @Override
    public boolean onCreate() {
        try {
            SQLiteHelper sqLiteHelper = new SQLiteHelper(getContext(), getDbAnnotation()) {
                @Override
                public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                    SQLiteProvider.this.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
                }
            };
            sqLite = sqLiteHelper.getWritableDatabase();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private ContentResolver getContentResolver() {
        Context context = getContext();
        if (context == null) {
            return null;
        }
        return context.getContentResolver();
    }

    @Override
    public Cursor query(Uri uri, String[] column, String selection, String[] selectionArgs, String sortOrder) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 1 || TextUtils.isEmpty(segments.get(0))) {
            return null;
        }
        return sqLite.query(segments.get(0), column, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 1 || TextUtils.isEmpty(segments.get(0))) {
            return null;
        }
        long rowId = sqLite.insert(segments.get(0), null, contentValues);
        if (rowId > -1) {
            getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, rowId);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 1 || TextUtils.isEmpty(segments.get(0))) {
            return -1;
        }
        int count = sqLite.delete(segments.get(0), s, strings);
        if (count > 0) {
            getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String s, String[] strings) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 1 || TextUtils.isEmpty(segments.get(0))) {
            return -1;
        }
        int count = sqLite.update(segments.get(0), values, s, strings);
        if (count > 0) {
            getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        ContentProviderResult[] result = null;
        sqLite.beginTransaction();
        try {
            result = super.applyBatch(operations);
            sqLite.setTransactionSuccessful();
        } finally {
            sqLite.endTransaction();
        }
        return result;
    }

    public final DataBase getDbAnnotation() {
        return dbAno;
    }

    protected Class<? extends SQLiteProvider> getBuildClass() {
        return getClass();
    }

    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}