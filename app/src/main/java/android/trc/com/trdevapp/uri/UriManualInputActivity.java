package android.trc.com.trdevapp.uri;

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.trc.com.trdevapp.R;
import android.trc.com.trdevapp.cache.AppConfig;
import android.trc.com.trdevapp.constants.Platform;
import android.trc.com.trdevapp.util.UriUtil;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UriManualInputActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uri_manul_input_test);
        editText = findViewById(R.id.editText);

        setUpQuickKeyBoard(R.id.keys1);
        setUpQuickKeyBoard(R.id.keys2);
        setUpQuickKeyBoard(R.id.keys3);
    }

    private void setUpQuickKeyBoard(int id) {
        ViewGroup viewGroup = findViewById(id);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setOnClickListener(v -> {
                TextView textView = (TextView) v;
                editText.append(textView.getText());
                editText.setSelection(editText.getText().length());
            });
        }
    }

    public void onClickOpen(View view) {
        try {
            UriUtil.open(this, editText.getText().toString().trim());
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "抱歉，未匹配到对应平台", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClickOpenLink(View view) {
        String scheme = "default";
        switch (AppConfig.getPlatform()) {
            case Platform.CUBE:
                scheme = "tlkj";
                break;
            case Platform.FINANCE:
                scheme = "trc";
                break;
            case Platform.INSURANCE:
                scheme = "trtb";
                break;
            case Platform.MALL:
                scheme = "trmall";
                break;
            case Platform.WALLET:
                scheme = "fyd";
                break;
        }
        String targetUrl = editText.getText().toString().trim();
        String url = scheme + "://open_link_in_new_window?url=" + SafeBase64.encodeString(targetUrl);
        UriUtil.open(this, url);
    }
}
