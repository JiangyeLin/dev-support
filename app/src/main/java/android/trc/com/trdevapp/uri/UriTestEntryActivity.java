package android.trc.com.trdevapp.uri;

import android.content.Intent;
import android.os.Bundle;
import android.trc.com.trdevapp.R;
import android.trc.com.trdevapp.cache.AppConfig;
import android.trc.com.trdevapp.constants.Platform;
import android.trc.com.trdevapp.uri.data.UriDataForCube;
import android.trc.com.trdevapp.uri.data.UriDataForFinance;
import android.trc.com.trdevapp.uri.data.UriDataForPowcetwallet;
import android.trc.com.trdevapp.uri.data.UriDataForTrc;
import android.trc.com.trdevapp.uri.data.UriDataForTrtb;
import android.trc.com.trdevapp.uri.data.rn.UriDataForRnCube;
import android.trc.com.trdevapp.uri.data.rn.UriDataForRnFinance;
import android.trc.com.trdevapp.uri.data.rn.UriDataForRnTrc;
import android.trc.com.trdevapp.uri.data.rn.UriDataForRnTrtb;
import android.trc.com.trdevapp.uri.data.rn.UriDataForRnWallet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UriTestEntryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uri_test);
    }

    public void onClickNative(View view) {
        switch (AppConfig.getPlatform()) {
            case Platform.CUBE:
                start(UriDataForCube.TITLE_URI_PAIR_ARRAY);
                break;
            case Platform.FINANCE:
                start(UriDataForFinance.TITLE_URI_PAIR_ARRAY);
                break;
            case Platform.INSURANCE:
                start(UriDataForTrtb.TITLE_URI_PAIR_ARRAY);
                break;
            case Platform.MALL:
                start(UriDataForTrc.TITLE_URI_PAIR_ARRAY);
                break;
            case Platform.WALLET:
                start(UriDataForPowcetwallet.TITLE_URI_PAIR_ARRAY);
                break;
        }

    }

    public void onClickReactNative(View view) {
        switch (AppConfig.getPlatform()) {
            case Platform.CUBE:
                start(UriDataForRnCube.TITLE_URI_PAIR_ARRAY);
                break;
            case Platform.FINANCE:
                start(UriDataForRnFinance.TITLE_URI_PAIR_ARRAY);
                break;
            case Platform.INSURANCE:
                start(UriDataForRnTrtb.TITLE_URI_PAIR_ARRAY);
                break;
            case Platform.MALL:
                start(UriDataForRnTrc.TITLE_URI_PAIR_ARRAY);
                break;
            case Platform.WALLET:
                start(UriDataForRnWallet.TITLE_URI_PAIR_ARRAY);
                break;
        }
    }


    public void onClickManual(View view) {
        Intent intent = new Intent(this, UriManualInputActivity.class);
        startActivity(intent);
    }

    private void start(String[] titleUriPairArray) {
        Intent intent = new Intent(this, UriTestActivity.class);
        intent.putExtra("SOURCE", titleUriPairArray);
        startActivity(intent);
    }

}
