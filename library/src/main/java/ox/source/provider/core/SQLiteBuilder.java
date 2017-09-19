package ox.source.provider.core;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ox.source.provider.anno.Table;

/**
 * @author FengPeng
 * @date 2017/2/26
 */
public final class SQLiteBuilder<T> {
    private final String tableName;
    private final Class<T> tbClazz;
    private final ContentResolver resolver;
    private final List<String> columns = new ArrayList<>();
    private final StringBuffer selections = new StringBuffer();
    private final List<String> selectionArgs = new ArrayList<>();

    public SQLiteBuilder(Context context, Class<T> tbClazz) {
        if (null == context) {
            throw new IllegalArgumentException("SQLiteBuilder context is null");
        }
        if (null == tbClazz) {
            throw new IllegalArgumentException("SQLiteBuilder tbClazz is null");
        }
        this.tbClazz = tbClazz;
        resolver = context.getContentResolver();
        String name = null;
        Table table = tbClazz.getAnnotation(Table.class);
        if (null != table) {
            name = table.name();
        }
        tableName = name;
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("SQLiteBuilder miss table name");
        }
    }

    /**
     * 获取表类
     */
    public Class<T> getTableClass() {
        return tbClazz;
    }

    /**
     * 获取表名
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 获取列名
     */
    public String[] columns() {
        return columns.toArray(new String[columns.size()]);
    }

    /**
     * 获取where语句
     */
    public String selection() {
        return selections.toString();
    }

    /**
     * 获取where语句参数
     */
    public String[] selectionArgs() {
        return selectionArgs.toArray(new String[selectionArgs.size()]);
    }

    @Override
    public String toString() {
        StringBuffer columnSbf = new StringBuffer();
        columnSbf.append("[");
        for (String e : columns()) {
            columnSbf.append(e + ",");
        }
        if (columnSbf.length() > 1) {
            columnSbf.setLength(columnSbf.length() - 1);
        }
        columnSbf.append("]");

        StringBuffer argSbf = new StringBuffer();
        argSbf.append("[");
        for (String e : selectionArgs()) {
            argSbf.append(e + ",");
        }
        if (argSbf.length() > 1) {
            argSbf.setLength(argSbf.length() - 1);
        }
        argSbf.append("]");

        return "SQLiteBuilder{" +
                "tableName='" + getTableName() + '\'' +
                ", columns=" + columnSbf.toString() +
                ", selections=" + selection() +
                ", selectionArgs=" + argSbf.toString() +
                '}';
    }

    /**********
     * 构建操作
     **********/

    public SQLiteBuilder clear() {
        columns.clear();
        selections.setLength(0);
        selectionArgs.clear();
        return this;
    }

    public void columns(String... args) {
        if (null == args && 0 == args.length) {
            return;
        }
        for (String e : args) {
            columns.add(e);
        }
    }

    public SQLiteBuilder where(String selection, String... args) {
        if (TextUtils.isEmpty(selection)) {
            if (null != args && args.length > 0) {
                throw new IllegalArgumentException("Valid selection required when including arguments=");
            }
            return this;
        }
        if (selections.length() > 0) {
            selections.append(" AND ");
        }
        selections.append("(").append(selection).append(")");
        if (null != args && args.length > 0) {
            Collections.addAll(selectionArgs, args);
        }
        return this;
    }

    public SQLiteBuilder whereEquals(String column, String value) {
        return where(column + "=?", value);
    }

    public int insert(T... items) {
        try {
            int lines = 0;
            Uri uri = SQLiteUtils.getTableUri(tbClazz);
            for (int i = 0; i < items.length; ++i) {
                ContentValues values = SQLiteUtils.getContentValues(items[i]);
                lines += null != resolver.insert(uri, values) ? 1 : 0;
            }
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int delete() {
        try {
            Uri uri = SQLiteUtils.getTableUri(tbClazz);
            SQLiteUtils.dLog("delete = " + toString());
            int lines = resolver.delete(uri, selection(), selectionArgs());
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int update(T obj) {
        try {
            Uri uri = SQLiteUtils.getTableUri(tbClazz);
            SQLiteUtils.dLog("update = " + toString());
            ContentValues values = SQLiteUtils.getContentValues(obj, columns());
            int lines = resolver.update(uri, values, selection(), selectionArgs());
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<T> query() {
        final List<T> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            Uri uri = SQLiteUtils.getTableUri(tbClazz);
            SQLiteUtils.dLog("query = " + toString());
            cursor = resolver.query(uri, columns(), selection(), selectionArgs(), null);
            List<T> objects = SQLiteUtils.getMappingObjects(tbClazz, cursor);
            if (null != objects && objects.size() > 0) {
                list.addAll(objects);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return list;
    }
}