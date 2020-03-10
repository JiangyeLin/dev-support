package android.trc.com.trdevapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.trc.com.trdevapp.constants.Platform.COIN;
import static android.trc.com.trdevapp.constants.Platform.CUBE;
import static android.trc.com.trdevapp.constants.Platform.FINANCE;
import static android.trc.com.trdevapp.constants.Platform.INSURANCE;
import static android.trc.com.trdevapp.constants.Platform.MALL;
import static android.trc.com.trdevapp.constants.Platform.WALLET;

/**
 * JiangyeLin on 2018/6/5
 */

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<MainModel> list = new ArrayList<>();
        list.add(new MainModel("金融", FINANCE, R.drawable.ic_launcher_finance));
        list.add(new MainModel("电商", MALL, R.drawable.ic_launcher_mall));
        list.add(new MainModel("保险", INSURANCE, R.drawable.ic_launcher_insurance));
        list.add(new MainModel("魔方部落", CUBE, R.drawable.ic_launcher_cube));
        list.add(new MainModel("口袋钱包", WALLET, R.drawable.ic_launcher_wallet));
        list.add(new MainModel("币罗盘", COIN, R.drawable.ic_launcher_blp));

        MainAdapter mainAdapter = new MainAdapter(list);
        recyclerView.setAdapter(mainAdapter);

        mainAdapter.setOnItemClickListener(pos -> DetailActivity.newInstance(this, list.get(pos)));
    }

    public static class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<MainModel> list;
        OnItemClickListener onItemClickListener;

        MainAdapter(List<MainModel> list) {
            super();
            this.list = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_main, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.tvLabel.setText(list.get(position).label);
            viewHolder.imgIcon.setImageResource(list.get(position).icon);
            viewHolder.llLayout.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvLabel;
            ImageView imgIcon;
            LinearLayout llLayout;

            ViewHolder(View view) {
                super(view);
                tvLabel = view.findViewById(R.id.tv_label);
                imgIcon = view.findViewById(R.id.img_icon);
                llLayout = view.findViewById(R.id.ll_layout);
            }
        }

        interface OnItemClickListener {
            void onItemClick(int pos);
        }

        void setOnItemClickListener(OnItemClickListener listener) {
            this.onItemClickListener = listener;
        }

    }
}
