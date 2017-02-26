package ox.source.provider.tools;

import android.database.sqlite.SQLiteDatabase;

import ox.source.provider.anno.DataBase;
import ox.source.provider.core.SQLiteProvider;

/**
 * @author FengPeng
 * @date 2017/2/26
 */
@DataBase(
        name = "tools.db",
        authority = "ox.source.provider.tools",
        tables = {HostTable.class},
        since = 1)
public class ToolsProvider extends SQLiteProvider {

    @Override
    protected Class<? extends SQLiteProvider> getBuildClass() {
        return getClass();
    }

    @Override
    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}