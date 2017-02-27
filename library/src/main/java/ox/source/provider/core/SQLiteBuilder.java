package ox.source.provider.core;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ox.source.provider.anno.Table;

/**
 * @author FengPeng
 * @date 2017/2/26
 */
public final class SQLiteBuilder {

    private final String tableName;
    private final List<String> columns = new ArrayList<>();
    private final StringBuffer selections = new StringBuffer();
    private final List<String> selectionArgs = new ArrayList<>();

    public SQLiteBuilder(Class<?> tbClazz) {
        if (null == tbClazz) {
            throw new IllegalArgumentException("SQLiteBuilder tbClazz is null");
        }
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

    public SQLiteBuilder(String table) {
        tableName = table;
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("SQLiteBuilder miss table name");
        }
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
        return "SQLiteBuilder{" +
                "tableName='" + getTableName() + '\'' +
                ", columns=" + columns() +
                ", selections=" + selection() +
                ", selectionArgs=" + selectionArgs() +
                '}';
    }

    /**********
     * 构建操作
     **********/

    public SQLiteBuilder reset() {
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
}
