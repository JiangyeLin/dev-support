package android.trc.com.trdevapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.trc.com.trdevapp.cache.AppConfig;
import android.trc.com.trdevapp.config.ConfigActivity;
import android.trc.com.trdevapp.uri.UriTestEntryActivity;
import android.trc.com.trdevapp.util.UriUtil;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static android.trc.com.trdevapp.constants.Platform.COIN;
import static android.trc.com.trdevapp.constants.Platform.CUBE;
import static android.trc.com.trdevapp.constants.Platform.FINANCE;
import static android.trc.com.trdevapp.constants.Platform.INSURANCE;
import static android.trc.com.trdevapp.constants.Platform.MALL;
import static android.trc.com.trdevapp.constants.Platform.WALLET;
import static android.trc.com.trdevapp.constants.Platform.packageMap;

public class DetailActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    String[] titles = new String[]{"Uri测试", "App配置"};
    Class[] classes = new Class[]{UriTestEntryActivity.class, ConfigActivity.class};
    int length = titles.length;
    private MainModel model;

    public static void newInstance(Context context, MainModel model) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("model", model);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        model = getIntent().getParcelableExtra("model");
        if (model == null) {
            return;
        }
        AppConfig.setPlatform(model.platform);

        TextView tvLabel = findViewById(R.id.tv_label);
        ImageView imgIcon = findViewById(R.id.img_icon);
        TextView tvPackageName = findViewById(R.id.tv_packagename);

        tvLabel.setText(model.label);
        imgIcon.setImageResource(model.icon);
        String packageName = packageMap.get(model.platform);
        tvPackageName.setText(packageName);

        linearLayout = findViewById(R.id.linearLayout);
        for (int i = 0; i < length; i++) {
            Button button = new Button(this);
            button.setText(titles[i]);
            button.setId(i);
            button.setOnClickListener(v -> {
                Intent intent = new Intent(DetailActivity.this, classes[v.getId()]);
                startActivity(intent);
            });
            linearLayout.addView(button);
        }
    }

    public void onClickSysSet(View view) {
        String packageName = packageMap.get(model.platform);
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        startActivity(intent);
    }

    public void clearConfigOnClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle("清除确认")
                .setMessage("你确定要清除所有配置么？该操作不可逆！")
                .setPositiveButton("确定", (dialog, which) -> {
                    String url;
                    switch (AppConfig.getPlatform()) {
                        case CUBE:
                            url = "tlkj://clear_config";
                            break;
                        case FINANCE:
                            url = "trc://clear_config";
                            break;
                        case INSURANCE:
                            url = "trtb://clear_config";
                            break;
                        case WALLET:
                            url = "fyd://clear_config";
                            break;
                        case MALL:
                            url = "trmall://clear_config";
                            break;
                        case COIN:
                            url = "blp://clear_config";
                            break;
                        default:
                            Toast.makeText(this, "抱歉，未匹配到对应平台", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                    }
                    try {
                        UriUtil.open(this, url);
                        Toast.makeText(this, "操作完成", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "未找到能处理" + url + "的页面", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss()).show();
    }
}
