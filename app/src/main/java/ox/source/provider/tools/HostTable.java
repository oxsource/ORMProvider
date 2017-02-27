package ox.source.provider.tools;

import ox.source.provider.anno.Column;
import ox.source.provider.anno.Table;

/**
 * @author FengPeng
 * @date 2017/2/26
 */
@Table(
        name = "hostTable",
        db = ToolsProvider.class,
        since = 1)
public class HostTable {
    @Column(name = "_id", type = Column.FieldType.INTEGER, primary = true, autoIncrement = true)
    private int id;

    @Column(name = "name", type = Column.FieldType.TEXT, notNull = true)
    private String name;

    @Column(name = "host", type = Column.FieldType.TEXT, notNull = true)
    private String host;

    @Column(name = "version", type = Column.FieldType.INTEGER, notNull = true)
    private int version;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "HostTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}