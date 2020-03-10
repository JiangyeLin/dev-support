package android.trc.com.trdevapp.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UriUtil {
    public static void open(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static void open(Context context, String url) {
        open(context, Uri.parse(url));
    }
}
