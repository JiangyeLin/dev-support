package android.trc.com.trdevapp.config;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.trc.com.trdevapp.R;
import android.trc.com.trdevapp.cache.AppConfig;
import android.trc.com.trdevapp.constants.Platform;
import android.trc.com.trdevapp.network.API;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 从服务器导入
 */
public class ImportConfigsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<RemoteModel.ContentBean> remoteInfoList = new ArrayList<>();
    private ArrayList<String> localNames;
    private RadioButton rbCheckAll;
    private boolean isCheckAll;
    private SparseBooleanArray checkedArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_remote);

        checkedArray = new SparseBooleanArray();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RemoteAdapter remoteAdapter = new RemoteAdapter(remoteInfoList, checkedArray);
        remoteAdapter.setOnClickListener((bean, pos) -> {
            View view = View.inflate(ImportConfigsActivity.this, R.layout.layout_remote_dialog, null);

            AlertDialog dialog = new AlertDialog.Builder(ImportConfigsActivity.this)
                    .setView(view).create();

            TextView tvContent = view.findViewById(R.id.tv_content);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            tvContent.setText(gson.toJson(bean.configList));

            TextView tvTitle = view.findViewById(R.id.tv_title);
            tvTitle.setText(bean.name);

            Button button = view.findViewById(R.id.btn_delete);
            button.setOnClickListener(v -> {
                deleteRemoteConfig(bean, pos);
                dialog.dismiss();
            });

            dialog.show();
        });

        recyclerView.setAdapter(remoteAdapter);

        rbCheckAll = findViewById(R.id.rb_checkall);

        getRemoteInfo();
    }

    //是否全选
    public void checkAllOnClick(View view) {
        isCheckAll = !isCheckAll;
        rbCheckAll.setChecked(isCheckAll);
        if (isCheckAll) {
            for (int i = 0; i < remoteInfoList.size(); i++) {
                checkedArray.put(i, true);
            }
        } else {
            checkedArray.clear();
        }
        recyclerView.getAdapter().notifyItemRangeChanged(0, 10);
    }

    //导入
    public void importOnClick(View view) {
        RemoteAdapter remoteAdapter = (RemoteAdapter) recyclerView.getAdapter();
        checkedArray = remoteAdapter.getCheckedArray();
        if (checkedArray.size() == 0) {
            Toast.makeText(this, "请选中你要导入的条目", Toast.LENGTH_SHORT).show();
            return;
        }
        //逐条导入选中的条目
        File[] configFiles = ConfigDataProvider.getConfigFiles(AppConfig.getPlatform());
        localNames = new ArrayList<>();
        for (File file : configFiles) {
            localNames.add(file.getName());
        }
        saveToFile();
    }

    @SuppressLint("CheckResult")
    private void getRemoteInfo() {
        API api = new Retrofit.Builder()
                .baseUrl(Platform.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(API.class);

        api.getRemoteInfo(AppConfig.getPlatform(), "android")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(remoteModel -> {
                    Toast.makeText(ImportConfigsActivity.this, remoteModel.message, Toast.LENGTH_SHORT).show();
                    remoteInfoList.clear();
                    remoteInfoList.addAll(remoteModel.content);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }, throwable -> throwable.printStackTrace());
    }

    //移除服务器上的配置
    @SuppressLint("CheckResult")
    private void deleteRemoteConfig(RemoteModel.ContentBean bean, int pos) {
        API api = new Retrofit.Builder()
                .baseUrl(Platform.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(API.class);

        api.delRemoteInfo(AppConfig.getPlatform(), bean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    String response = responseBody.string();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optInt("code") == 200) {
                        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                        RemoteAdapter adapter = (RemoteAdapter) recyclerView.getAdapter();
                        adapter.list.remove(pos);
                        recyclerView.getAdapter().notifyItemRemoved(pos);
                    } else {
                        Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    throwable.printStackTrace();
                });
    }

    private void saveToFile() {
        if (checkedArray.size() == 0) {
            Toast.makeText(this, "远端配置导入成功", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        int key = checkedArray.keyAt(0);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File dir = ConfigDataProvider.getConfigDir(AppConfig.getPlatform());
        RemoteModel.ContentBean bean = remoteInfoList.get(key);
        String name = bean.name;
        if (isFileConflict(name)) {
            new AlertDialog.Builder(this)
                    .setTitle("配置冲突，本地已存在以下配置")
                    .setMessage(name + "\n\n是否替换原有配置?")
                    .setPositiveButton("替换", (dialog, which) -> {
                        //啥也不用做，直接保存就行了
                        ConfigDataProvider.saveToConfigFile(dir, gson.toJson(bean.configList), name);
                        checkedArray.delete(key);
                        saveToFile();
                        dialog.dismiss();
                    })
                    .setNegativeButton("保留", (dialog, which) -> {
                        //重命名后保存
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddHHmm", Locale.CHINA);

                        ConfigDataProvider.saveToConfigFile(dir, gson.toJson(bean.configList), name + "-Remote-" + simpleDateFormat.format(new Date()));
                        checkedArray.delete(key);
                        saveToFile();
                        dialog.dismiss();
                    })
                    .show();
            return;
        }
        checkedArray.delete(key);
        ConfigDataProvider.saveToConfigFile(dir, gson.toJson(bean.configList), name);
        saveToFile();
    }

    private boolean isFileConflict(String name) {
        return localNames.contains(name);
    }

    public static class RemoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<RemoteModel.ContentBean> list;
        SparseBooleanArray checkedArray;
        OnClickListener clickListener;

        RemoteAdapter(List<RemoteModel.ContentBean> list, SparseBooleanArray checkedArray) {
            super();
            this.list = list;
            this.checkedArray = checkedArray;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_remote, parent, false);
            return new RemoteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RadioButton radioButton = ((RemoteViewHolder) holder).rbLabel;

            boolean origin = checkedArray.get(position, false);
            radioButton.setChecked(origin);
            radioButton.setText(list.get(position).name);

            radioButton.setOnClickListener(v -> {
                radioButton.setChecked(!origin);
                if (origin) {
                    checkedArray.delete(position);
                } else {
                    checkedArray.put(position, !origin);
                }
                notifyDataSetChanged();
            });

            ((RemoteViewHolder) holder).imgMore.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onClick(list.get(position), position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        class RemoteViewHolder extends RecyclerView.ViewHolder {
            RadioButton rbLabel;
            ImageView imgMore;

            RemoteViewHolder(View view) {
                super(view);
                rbLabel = view.findViewById(R.id.rb_label);
                imgMore = view.findViewById(R.id.img_more);
            }
        }

        public SparseBooleanArray getCheckedArray() {
            return checkedArray;
        }

        public interface OnClickListener {
            void onClick(RemoteModel.ContentBean bean, int pos);
        }

        public void setOnClickListener(OnClickListener clickListener) {
            this.clickListener = clickListener;
        }
    }
}
