package ox.source.provider.tools;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ox.source.provider.core.SQLiteBuilder;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView tv;
    private Button btAdd;
    private Button btDel;
    private Button btFix;
    private Button btGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        tv = (TextView) findViewById(R.id.tv);
        btAdd = (Button) findViewById(R.id.btAdd);
        btAdd.setOnClickListener(this);
        btDel = (Button) findViewById(R.id.btDel);
        btDel.setOnClickListener(this);
        btFix = (Button) findViewById(R.id.btFix);
        btFix.setOnClickListener(this);
        btGet = (Button) findViewById(R.id.btGet);
        btGet.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btAdd)) {
            insert();
        } else if (view.equals(btDel)) {
            delete();
        } else if (view.equals(btFix)) {
            update();
        } else if (view.equals(btGet)) {
            query();
        }
    }

    //query
    private void query() {
        tv.setText("");
        SQLiteBuilder<HostTable> builder = new SQLiteBuilder(getBaseContext(), HostTable.class);
        builder.whereEquals(HostTable.KEY_ID, "1");
        List<HostTable> lists = builder.query();
        for (HostTable e : lists) {
            tv.append(e.toString() + "\n");
        }
    }

    //insert
    private void insert() {
        SQLiteBuilder<HostTable> builder = new SQLiteBuilder(getBaseContext(), HostTable.class);

        List<HostTable> list = new ArrayList<>();
        HostTable table = new HostTable();
        table.setHost("http://www.baidu.com");
        table.setName("baidu");
        int lines = builder.insert(table);
        if (lines > 0) {
            showMessage("成功插入" + lines + "行");
        } else {
            showMessage("插入失败！");
        }
    }

    //delete
    private void delete() {
        SQLiteBuilder<HostTable> builder = new SQLiteBuilder(getBaseContext(), HostTable.class);
        builder = builder.whereEquals(HostTable.KEY_ID, "1");
        int lines = builder.delete();
        if (lines > 0) {
            showMessage("成功删除!");
        } else {
            showMessage("删除失败！");
        }
    }

    //update
    private void update() {
        HostTable table = new HostTable();
        table.setHost(null);
        table.setHost("update http://www.baidu.com");
        table.setVersion(3);

        SQLiteBuilder<HostTable> builder = new SQLiteBuilder(getBaseContext(), HostTable.class);
        builder.columns(HostTable.KEY_VERSION);
        builder.whereEquals(HostTable.KEY_NAME, "baidu");
        int lines = builder.update(table);
        if (lines > 0) {
            showMessage("成功修改" + lines + "行");
        } else {
            showMessage("修改失败！");
        }
    }

    private void showMessage(String text) {
        Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
    }
}