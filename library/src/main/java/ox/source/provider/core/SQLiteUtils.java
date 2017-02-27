package ox.source.provider.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ox.source.provider.anno.Column;
import ox.source.provider.anno.DataBase;
import ox.source.provider.anno.Table;

/**
 * @author FengPeng
 * @date 2017/2/25
 */
public final class SQLiteUtils {
    private final static String SQL_TAG = "SQLiteUtils";

    private SQLiteUtils() {
        /* Utility classes must not have a public constructor */
    }

    public static String getTableName(Class<?> clazz, Table table) {
        String value = table.name();
        if (TextUtils.isEmpty(value)) {
            return pluralize(clazz.getSimpleName());
        } else {
            return value;
        }
    }

    public static String pluralize(String string) {
        string = string.toLowerCase(Locale.US);

        if (string.endsWith("s")) {
            return string;
        } else if (string.endsWith("ay")) {
            return string.replaceAll("ay$", "ays");
        } else if (string.endsWith("ey")) {
            return string.replaceAll("ey$", "eys");
        } else if (string.endsWith("oy")) {
            return string.replaceAll("oy$", "oys");
        } else if (string.endsWith("uy")) {
            return string.replaceAll("uy$", "uys");
        } else if (string.endsWith("y")) {
            return string.replaceAll("y$", "ies");
        } else {
            return string + "s";
        }
    }

    public static String getColumnConstraint(Column column) throws IllegalAccessException {
        return column.name() + " " + column.type()
                + (column.primary() ? " PRIMARY KEY" : "")
                + (column.notNull() || column.primary() ? " NOT NULL" : "")
                + (column.unique() || column.primary() ? " UNIQUE" : "");
    }


    public static Uri getTableUri(Class<?> clazz) {
        if (null == clazz) {
            return null;
        }
        final Table table = clazz.getAnnotation(Table.class);
        if (null == table) {
            return null;
        }
        final Class<?> dbClazz = table.db();
        final DataBase ano;
        if (null == dbClazz || (ano = dbClazz.getAnnotation(DataBase.class)) == null) {
            return null;
        }
        String name = getTableName(clazz, table);
        Uri uri = Uri.parse("content://" + ano.authority() + "/" + name);
        return uri;
    }

    public static List<Field> getColumnedFields(Class<?> clazz, String... columns) {
        List<Field> fieldList = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        if (null == fields || fields.length == 0) {
            return fieldList;
        }

        List<String> colList = null;
        if (null != columns && columns.length > 0) {
            colList = new ArrayList<>();
            for (String e : columns) {
                colList.add(e);
            }
        }

        for (int i = 0; i < fields.length; ++i) {
            Column column;
            if ((column = fields[i].getAnnotation(Column.class)) == null) {
                continue;
            }
            //no columns
            if (null == colList) {
                fieldList.add(fields[i]);
                continue;
            }
            //with columns
            for (int j = 0; j < colList.size(); ++j) {
                if (column.name().equals(colList.get(j))) {
                    fieldList.add(fields[i]);
                    colList.remove(j);
                    break;
                }
            }
        }
        return fieldList;
    }

    public static <T> ContentValues getContentValues(T obj, String... columns) throws Exception {
        ContentValues values = new ContentValues();
        if (null == obj) {
            return values;
        }
        List<Field> fields = getColumnedFields(obj.getClass(), columns);
        for (int i = 0; i < fields.size(); ++i) {
            Field field = fields.get(i);
            field.setAccessible(true);
            Column column = field.getAnnotation(Column.class);
            String name = column.name();
            Object value = field.get(obj);

            switch (column.type()) {
                case BLOB:
                    if (null == value && !column.notNull()) {
                        String content = null;
                        values.put(name, content);
                    } else if (value instanceof byte[]) {
                        values.put(name, (byte[]) value);
                    }
                    break;
                case TEXT:
                    if (null == value && !column.notNull()) {
                        String content = null;
                        values.put(name, content);
                    } else if (value instanceof String) {
                        values.put(name, (String) value);
                    }
                    break;
                case REAL:
                    if (value instanceof Float) {
                        values.put(name, (float) value);
                    }
                    break;
                case INTEGER:
                    if (value instanceof Integer && !column.autoIncrement()) {
                        values.put(name, (int) value);
                    }
                    break;
                case NULL:
                    break;
            }
        }
        return values;
    }

    public static <T> List<T> getMappingObjects(Class<T> clazz, Cursor cursor) throws Exception {
        final List<T> list = new ArrayList<>();
        if (null == cursor || !cursor.moveToFirst()) {
            return list;
        }
        do {
            T obj = clazz.newInstance();
            List<Field> fields = getColumnedFields(obj.getClass());
            for (int i = 0; i < fields.size(); ++i) {
                Field field = fields.get(i);
                field.setAccessible(true);

                Column column = field.getAnnotation(Column.class);
                int index = cursor.getColumnIndex(column.name());
                if (index < 0 || index >= cursor.getColumnCount()) {
                    continue;
                }
                switch (column.type()) {
                    case BLOB:
                        field.set(obj, cursor.getBlob(index));
                        break;
                    case TEXT:
                        field.set(obj, cursor.getString(index));
                        break;
                    case REAL:
                        field.set(obj, cursor.getFloat(index));
                        break;
                    case INTEGER:
                        field.set(obj, cursor.getInt(index));
                        break;
                    case NULL:
                        field.set(obj, null);
                        break;
                }
            }
            list.add(obj);
        } while (cursor.moveToNext());
        return list;
    }

    public static void eLog(String msg) {
        Log.e(SQL_TAG, msg);
    }

    public static void dLog(String msg) {
        Log.d(SQL_TAG, msg);
    }
}
