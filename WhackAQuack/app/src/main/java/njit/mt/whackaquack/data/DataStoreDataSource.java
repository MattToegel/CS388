package njit.mt.whackaquack.data;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Consumer;

import njit.mt.whackaquack.data.model.Data;


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class DataStoreDataSource {
    public void getData(Data d, Consumer<Result<Data>> success, Consumer<Result.Error> error){
        ANRequest.GetRequestBuilder req = AndroidNetworking.get("https://class.whattheduck.app/api/getData");
        if(d.getAppKey() != null){
            req.addQueryParameter("appKey", d.getAppKey());
        }
        if(d.getKey() != null){
            req.addQueryParameter("key", d.getKey());
        }
        if(d.getUid() != null){
            req.addQueryParameter("uid", d.getUid());
        }
        req.addHeaders("api-key", "1234")
                .build().getAsJSONObject((new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");

                    if (status == 200) {
                        JSONObject responseJO = response.getJSONObject("data");
                        Log.v("DataSource", responseJO.toString());
                        Data data = new Data(responseJO.optString("key"),
                                new JSONObject(responseJO.optString("value","{}"))
                                );
                        data.setAppKey(responseJO.optString("appKey"));
                        data.setUid(responseJO.optString("uid"));
                        success.accept(new Result.Success<Data>(data));
                    } else {
                        JSONObject respError = response.getJSONObject("error");
                        error.accept(new Result.Error(respError.getString("code"), new Exception(respError.getString("message"))));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    error.accept(new Result.Error("json-error", new Exception(e.getMessage())));
                }
            }

            @Override
            public void onError(ANError anError) {
                error.accept(new Result.Error("an-error", new Exception(anError.getErrorBody())));
            }
        }));
    }
    public void setData(Data d, Consumer<Result<Data>> success, Consumer<Result.Error> error){
        ANRequest.PostRequestBuilder req = AndroidNetworking.post("https://class.whattheduck.app/api/setData");
        if(d.getAppKey() != null){
            req.addBodyParameter("appKey", d.getAppKey());
        }
        if(d.getKey() != null){
            req.addBodyParameter("key", d.getKey());
        }
        if(d.getUid() != null){
            req.addBodyParameter("uid", d.getUid());
        }
        if(d.getValue() != null){
            req.addBodyParameter("value", d.getValue().toString());
        }
        req.addHeaders("api-key", "1234")
                .build().getAsJSONObject((new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");

                    if (status == 200) {
                        JSONObject responseJO = response.getJSONObject("data");
                        Log.v("DataSource", responseJO.toString());
                        Data data = new Data(responseJO.optString("key"),
                                new JSONObject(responseJO.optString("value", "{}"))
                        );
                        data.setAppKey(responseJO.optString("appKey"));
                        data.setUid(responseJO.optString("uid"));
                        success.accept(new Result.Success<Data>(data));
                    } else {
                        JSONObject respError = response.getJSONObject("error");
                        error.accept(new Result.Error(respError.getString("code"), new Exception(respError.getString("message"))));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    error.accept(new Result.Error("json-error", new Exception(e.getMessage())));
                }
            }

            @Override
            public void onError(ANError anError) {
                error.accept(new Result.Error("an-error", new Exception(anError.getErrorBody())));
            }
        }));
    }
}