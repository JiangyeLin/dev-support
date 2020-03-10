package android.trc.com.trdevapp.constants;

import androidx.collection.ArrayMap;

public class Platform {
    public static final String FINANCE = "finance";
    public static final String MALL = "mall";
    public static final String WALLET = "wallet";
    public static final String INSURANCE = "insurance";
    public static final String CUBE = "cube";
    public static final String COIN = "coin";

    public static final ArrayMap<String, String> packageMap = new ArrayMap<String, String>() {{
        put(FINANCE, "com.tairanchina.taiheapp");
        put(MALL, "com.trc.mall");
        put(WALLET, "cn.pocketwallet.pocketwallet");
        put(CUBE, "io.trchain.cube");
        put(INSURANCE, "com.tairan.trtb");
        put(COIN, "com.tfabric.blp");  //泰链 币罗盘
    }};

    public static final String BASE_URL = "http://106.15.226.93:8081/api/";

}
