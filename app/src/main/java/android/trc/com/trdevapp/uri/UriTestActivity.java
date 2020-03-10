package android.trc.com.trdevapp.uri;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UriTestActivity extends AppCompatActivity {
    String[] titleUriPairs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleUriPairs = getIntent().getStringArrayExtra("SOURCE");
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(new Button(UriTestActivity.this)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                Button button = (Button) holder.itemView;
                button.setText(titleUriPairs[position * 2]);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = titleUriPairs[position * 2 + 1];
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = Uri.parse(url);
                            intent.setData(uri);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(UriTestActivity.this, "未找到能处理" + url + "的页面", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public int getItemCount() {
                return titleUriPairs.length / 2;
            }
        });
        setContentView(recyclerView);
    }
}
