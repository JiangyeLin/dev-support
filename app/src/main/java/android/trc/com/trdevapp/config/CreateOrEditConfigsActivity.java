package android.trc.com.trdevapp.config;

import android.content.DialogInterface;
import android.os.Bundle;
import android.trc.com.trdevapp.R;
import android.trc.com.trdevapp.cache.AppConfig;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.HashMap;

public class CreateOrEditConfigsActivity extends AppCompatActivity {
    public static String INTENT_KEY_FILE = "configFile";
    private EditText etName;
    private EditText etContent;
    private File configFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_edit);
        etName = findViewById(R.id.etConfigName);
        etContent = findViewById(R.id.etConfigContent);
        configFile = (File) getIntent().getSerializableExtra(INTENT_KEY_FILE);
        if (null != configFile) {
            etName.setText(configFile.getName());
            etContent.setText(ConfigDataProvider.getConfigContent(configFile));
        }

        setUpQuickKeyBoard(R.id.keys1);
        setUpQuickKeyBoard(R.id.keys2);
    }

    private void setUpQuickKeyBoard(int id) {
        ViewGroup viewGroup = findViewById(id);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!etContent.hasFocus()) {
                        return;
                    }
                    TextView textView = (TextView) v;
                    String content = etContent.getText().toString();
                    int focusPosition = etContent.getSelectionStart();
                    String newContent = content.substring(0, focusPosition);
                    String keyboardContent = textView.getText().toString();
                    newContent += keyboardContent;
                    newContent += content.substring(focusPosition);
                    etContent.setText(newContent);
                    etContent.setSelection(focusPosition + keyboardContent.length());
                }
            });
        }
    }


    public void onClickSave(View view) {
        try {

            String name = etName.getText().toString();
            String content = etContent.getText().toString();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            HashMap map = gson.fromJson(content, HashMap.class);
            File dir = ConfigDataProvider.getConfigDir(AppConfig.getPlatform());
            ConfigDataProvider.saveToConfigFile(dir, gson.toJson(map), name);
            if (null != configFile && !name.equals(configFile.getName())) {
                new AlertDialog.Builder(this)
                        .setMessage("是否保留原有配置?")
                        .setTitle("配置名称已改变")
                        .setPositiveButton("保留", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                configFile.delete();
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();

            } else {
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "请检查JSON格式有误", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickFormat(View view) {
        try {
            String content = etContent.getText().toString();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            HashMap map = gson.fromJson(content, HashMap.class);
            etContent.setText(gson.toJson(map));
            Toast.makeText(this, "格式化完成", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "请检查JSON格式有误", Toast.LENGTH_LONG).show();
        }
    }
}
