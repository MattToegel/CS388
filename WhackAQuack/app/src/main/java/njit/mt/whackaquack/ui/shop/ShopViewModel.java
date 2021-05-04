package njit.mt.whackaquack.ui.shop;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Consumer;

import njit.mt.whackaquack.data.DataStoreRepository;
import njit.mt.whackaquack.data.LoginRepository;
import njit.mt.whackaquack.data.Result;
import njit.mt.whackaquack.data.model.Data;
import njit.mt.whackaquack.data.model.LoggedInUser;

public class ShopViewModel extends ViewModel {
    LoginRepository loginRepository;
    DataStoreRepository dataStoreRepository;
    private MutableLiveData<JSONObject> shop = new MutableLiveData<>();
    public LiveData<JSONObject> getShop(){
        return shop;
    }

    //really not the best idea, but we're being hacky with datastore to simulate a shop
    public static final String shopKey = "dc17abce-d704-4b24-bf27-946c6c180906";//this should be 36 or more characters and never change
    //the 36+ is required for custom keys, firebase keys are >=20 < 36

    public ShopViewModel(LoginRepository loginRepository, DataStoreRepository instance) {
        this.loginRepository = loginRepository;
        this.dataStoreRepository = instance;
        LoggedInUser user = this.loginRepository.getUser();
        //lookup shop
        //if shop doesn't exist, initialize it
        /*
        items: [
            {
            name:"",
            cost: 0,
            id: 1
        ]
         */
        loadShop();
        //createShop();
    }

    void loadShop(){
        Log.v("SHOP", "loading shop");
        dataStoreRepository.getData("njit.mt.test", shopKey, "",
                (Result<Data> success)->{
                    Data rd = ((Result.Success<Data>) success).getData();
                    Log.v("Get Shop", rd.toString());
                    shop.setValue(rd.getValue());
                    //json.setValue(rd.getValue().toString());
                    //error.setValue("");
                },
                (Result.Error err)->{
                Log.v("Get Shop Error", err.getError().getMessage());
                String json =err.getError().getMessage();
                    try {
                        JSONObject error =  new JSONObject(json);
                        if(error.getInt("status") == 404){
                            Log.v("Shop Doesn't exist", "creating shop");
                            createShop();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                   // json.setValue("");
                    //error.setValue(err.getCode() + err.getError().getMessage());
                });
    }
    private void createShop(){
        JSONObject items = new JSONObject();
        JSONArray itemArray = new JSONArray();
        try {
        //item 1
        JSONObject item1 = new JSONObject();
        item1.put("name", "An Item 1");
        item1.put("cost", 1);
        item1.put("id", 1);

        //item 2
        JSONObject item2 = new JSONObject();
        item2.put("name", "An Item 2");
        item2.put("cost", 3);
        item2.put("id", 2);

        //item 2
        JSONObject item3 = new JSONObject();
        item3.put("name", "An Item 3");
        item3.put("cost", 5);
        item3.put("id", 3);

        itemArray.put(item1);
        itemArray.put(item2);
        itemArray.put(item3);

            items.put("items", itemArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dataStoreRepository.setData("njit.mt.test", shopKey, items.toString(), "",
                (Result<Data> success)->{
                    Data rd = ((Result.Success<Data>) success).getData();
                    Log.v("Create Shop", rd.toString());
                    shop.setValue(rd.getValue());
                    //refresh shop data
                   // json.setValue(rd.toString());
                    //error.setValue("");
                },
                (Result.Error err)->{
                    Log.v("Create Shop Error", err.getError().getMessage());
                   // json.setValue("");
                    //error.setValue(err.getCode() + err.getError().getMessage());
                });
    }
}