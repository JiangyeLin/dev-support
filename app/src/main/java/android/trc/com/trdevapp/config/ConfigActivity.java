package android.trc.com.trdevapp.config;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.trc.com.trdevapp.R;
import android.trc.com.trdevapp.cache.AppConfig;
import android.trc.com.trdevapp.constants.Platform;
import android.trc.com.trdevapp.network.API;
import android.trc.com.trdevapp.uri.SafeBase64;
import android.trc.com.trdevapp.util.UriUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.trc.com.trdevapp.constants.Platform.COIN;
import static android.trc.com.trdevapp.constants.Platform.CUBE;
import static android.trc.com.trdevapp.constants.Platform.FINANCE;
import static android.trc.com.trdevapp.constants.Platform.INSURANCE;
import static android.trc.com.trdevapp.constants.Platform.MALL;
import static android.trc.com.trdevapp.constants.Platform.WALLET;
import static android.trc.com.trdevapp.constants.Platform.packageMap;

public class ConfigActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    private String platform;
    private File[] configFiles;
    private File selectFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ConfigDataProvider.init(this);
        platform = AppConfig.getPlatform();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(ConfigActivity.this).inflate(R.layout.list_item_config, parent, false);
                return new RecyclerView.ViewHolder(view) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ViewGroup viewGroup = (ViewGroup) holder.itemView;
                TextView textView = (TextView) viewGroup.getChildAt(0);
                final File curItem = configFiles[position];
                textView.setText(curItem.getName());

                TextView tvStatus = viewGroup.findViewById(R.id.tv_status);
                if (curItem.equals(selectFile)) {
                    tvStatus.setText("生效中");
                } else {
                    tvStatus.setText("");
                }

                viewGroup.setOnClickListener(v -> {
                    CharSequence[] items = new CharSequence[]{"启用", "编辑", "删除", "上传至服务器"};
                    new AlertDialog.Builder(ConfigActivity.this)
                            .setTitle("请选择")
                            .setSingleChoiceItems(items, -1, (dialog, which) -> {
                                switch (which) {
                                    case 0:
                                        applyOnClick(curItem);
                                        break;
                                    case 1:
                                        editOnClick(curItem);
                                        break;
                                    case 2:
                                        deleteOnClick(curItem);
                                        break;
                                    case 3:
                                        uploadConfig(curItem);
                                        break;
                                }
                                dialog.dismiss();
                            })
                            .show();
                });
            }

            @Override
            public int getItemCount() {
                return null == configFiles ? 0 : configFiles.length;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDataSetAndFreshUi();
    }

    private void updateDataSetAndFreshUi() {
        configFiles = ConfigDataProvider.getConfigFiles(platform);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    public void createOnClick(View view) {
        Intent intent = new Intent(this, CreateOrEditConfigsActivity.class);
        startActivity(intent);
    }

    public void importOnClick(View view) {
        Intent intent = new Intent(this, ImportConfigsActivity.class);
        startActivity(intent);
    }

    private void deleteOnClick(final File file) {
        if (null != file) {
            new AlertDialog.Builder(this)
                    .setTitle("删除确认")
                    .setMessage("你将要删除" + file.getName() + "!该操作不可逆！")
                    .setPositiveButton("确定", (dialog, which) -> {
                        file.delete();
                        updateDataSetAndFreshUi();
                    })
                    .setNegativeButton("取消", (dialog, which) ->
                            dialog.dismiss())
                    .show();
        } else {
            Toast.makeText(this, "请选中配置", Toast.LENGTH_LONG).show();
        }
    }

    private void applyOnClick(File file) {
        if (file != null) {
            String fileContent = ConfigDataProvider.getConfigContent(file);
            String url = "";
            switch (platform) {
                case CUBE:
                    url = "tlkj://set_config?content=";
                    break;
                case FINANCE:
                    url = "trc://set_config?content=";
                    break;
                case INSURANCE:
                    url = "trtb://set_config?content=";
                    break;
                case WALLET:
                    url = "fyd://set_config?content=";
                    break;
                case MALL:
                    url = "trmall://set_config?content=";
                    break;
                case COIN:
                    url = "blp://set_config?content=";
                    break;
            }
            String packageName = packageMap.get(platform);
            selectFile = file;
            updateDataSetAndFreshUi();

            String json = getPrettyJson(fileContent);
            url += SafeBase64.encodeString(json);
            try {
                UriUtil.open(this, url);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "抱歉，未匹配到对应平台", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle("配置完成")
                    .setMessage("直接打开应用")
                    .setPositiveButton("确定", (dialog, which) -> {
                        PackageManager packageManager = ConfigActivity.this.getPackageManager();
                        Intent it = packageManager.getLaunchIntentForPackage(packageName);
                        startActivity(it);
                        dialog.dismiss();
                    })
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            Toast.makeText(this, "请选中配置", Toast.LENGTH_LONG).show();
        }
    }

    private void editOnClick(File file) {
        if (null != file) {
            Intent intent = new Intent(this, CreateOrEditConfigsActivity.class);
            intent.putExtra(CreateOrEditConfigsActivity.INTENT_KEY_FILE, file);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请选中配置", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("CheckResult")
    private void uploadConfig(File file) {
        API api = new Retrofit.Builder()
                .baseUrl(Platform.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(API.class);

        RemoteModel.ContentBean model = new RemoteModel.ContentBean();
        model.name = file.getName();
        model.platform = AppConfig.getPlatform();
        model.terminal = "android";
        model.type = "custom";//只允许上传 custom类型的配置

        String config = ConfigDataProvider.getConfigContent(file);
        Gson gson = new GsonBuilder().create();
        model.configList = gson.fromJson(config, HashMap.class);

        api.addRemoteInfo(AppConfig.getPlatform(), model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    String response = responseBody.string();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optInt("code") == 200) {
                        Toast.makeText(ConfigActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ConfigActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Toast.makeText(ConfigActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    throwable.printStackTrace();
                });
    }

    private String getPrettyJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HashMap hashMap = gson.fromJson(json, HashMap.class);
        return gson.toJson(hashMap);
    }
}
