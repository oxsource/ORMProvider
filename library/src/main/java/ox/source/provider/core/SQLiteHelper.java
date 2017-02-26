package ox.source.provider.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import ox.source.provider.anno.Column;
import ox.source.provider.anno.DataBase;
import ox.source.provider.anno.Table;

/**
 * @author FengPeng
 * @date 2017/2/26
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private final Class<?>[] tbClass;

    public SQLiteHelper(Context context, DataBase db) {
        super(context, db.name(), null, db.since());
        tbClass = new Class<?>[db.tables().length];
        for (int i = 0; i < tbClass.length; ++i) {
            tbClass[i] = db.tables()[i];
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (Class<?> clazz : tbClass) {
            Table table = clazz.getAnnotation(Table.class);
            if (null == table) {
                continue;
            }
            createTable(clazz, table, sqLiteDatabase);
        }
        SQLiteUtils.dLog("SQLiteHelper create");
    }

    private void createTable(Class<?> clazz, Table table, SQLiteDatabase sqLiteDatabase) {
        ArrayList<String> columns = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (null == column) {
                continue;
            }
            try {
                columns.add(SQLiteUtils.getColumnConstraint(column));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String tableName = SQLiteUtils.getTableName(clazz, table);
        String sql = "CREATE TABLE " + tableName + " (" + TextUtils.join(", ", columns) + ");";
        SQLiteUtils.dLog("create table = " + sql);

        sqLiteDatabase.execSQL(sql);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        for (Class<?> clazz : tbClass) {
            Table table = clazz.getAnnotation(Table.class);
            if (null == table) {
                continue;
            }
            int since = table.since();
            /*create table*/
            if (oldVersion < since && newVersion >= since) {
                createTable(clazz, table, sqLiteDatabase);
                continue;
            }
            /*upgrade table*/
            for (Field field : clazz.getFields()) {
                Column column = field.getAnnotation(Column.class);
                if (null == column) {
                    continue;
                }
                if (oldVersion >= column.since() || newVersion < column.since()) {
                    continue;
                }
                try {
                    String tableName = SQLiteUtils.getTableName(clazz, table);
                    String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + SQLiteUtils.getColumnConstraint(column) + ";";
                    SQLiteUtils.dLog("create table = " + sql);

                    sqLiteDatabase.execSQL(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new SQLiteException("Can't downgrade database from version " + oldVersion + " to " + newVersion);
    }
}