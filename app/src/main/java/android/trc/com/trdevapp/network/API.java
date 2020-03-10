package android.trc.com.trdevapp.network;

import android.trc.com.trdevapp.config.RemoteModel;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * JiangyeLin on 2018/6/6
 */
public interface API {
    //获取远程配置列表
    @GET("{platform}/info")
    Observable<RemoteModel> getRemoteInfo(@Path("platform") String platform,
                                          @Query("terminal") String terminal);

    //上传自定义配置
    @Headers("Content-Type: application/json")
    @POST("{platform}/add")
    Observable<ResponseBody> addRemoteInfo(@Path("platform") String platform,
                                           @Body RemoteModel.ContentBean body);

    //删除配置
    @Headers("Content-Type: application/json")
    @POST("{platform}/del")
    Observable<ResponseBody> delRemoteInfo(@Path("platform") String platform,
                                           @Body RemoteModel.ContentBean body);

}
