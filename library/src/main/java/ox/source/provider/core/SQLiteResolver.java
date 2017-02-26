package ox.source.provider.core;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FengPeng
 * @date 2017/2/25
 */
public final class SQLiteResolver {
    private final ContentResolver resolver;

    public SQLiteResolver(Context context) {
        this.resolver = context.getContentResolver();
    }

    public <T> int insert(List<T> list) {
        try {
            int lines = 0;
            Uri uri = SQLiteUtils.getTableUri(list.get(0).getClass());
            for (int i = 0; i < list.size(); ++i) {
                ContentValues values = SQLiteUtils.getContentValues(list.get(i));
                lines += null != resolver.insert(uri, values) ? 1 : 0;
            }
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public <T> int delete(Class<T> clazz, SQLiteBuilder builder) {
        try {
            Uri uri = SQLiteUtils.getTableUri(clazz);
            builder = null == builder ? new SQLiteBuilder(clazz) : builder;
            SQLiteUtils.dLog("delete = " + builder.toString());
            int lines = resolver.delete(uri, builder.selection(), builder.selectionArgs());
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public <T> int update(T obj, SQLiteBuilder builder) {
        try {
            Uri uri = SQLiteUtils.getTableUri(obj.getClass());
            builder = null == builder ? new SQLiteBuilder(obj.getClass()) : builder;
            SQLiteUtils.dLog("update = " + builder.toString());
            ContentValues values = SQLiteUtils.getContentValues(obj);
            int lines = resolver.update(uri, values, builder.selection(), builder.selectionArgs());
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public <T> List<T> query(Class<T> clazz, SQLiteBuilder builder) {
        final List<T> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            Uri uri = SQLiteUtils.getTableUri(clazz);
            builder = null == builder ? new SQLiteBuilder(clazz) : builder;
            SQLiteUtils.dLog("query = " + builder.toString());
            cursor = resolver.query(uri, builder.projection(), builder.selection(), builder.selectionArgs(), null);
            List<T> objects = SQLiteUtils.getMappingObjects(clazz, cursor);
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