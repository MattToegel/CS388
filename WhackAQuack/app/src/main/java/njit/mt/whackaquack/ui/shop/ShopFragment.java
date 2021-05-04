package njit.mt.whackaquack.ui.shop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import njit.mt.whackaquack.R;
import njit.mt.whackaquack.data.LoginDataSource;
import njit.mt.whackaquack.data.LoginRepository;
import njit.mt.whackaquack.data.Result;
import njit.mt.whackaquack.data.model.Data;
import njit.mt.whackaquack.data.model.Item;
import njit.mt.whackaquack.data.model.LoggedInUser;
import njit.mt.whackaquack.data.model.Stats;
import njit.mt.whackaquack.ui.history.HistoryListAdapter;
import njit.mt.whackaquack.ui.history.HistoryViewModel;
import njit.mt.whackaquack.ui.history.HistoryViewModelFactory;

public class ShopFragment extends Fragment {

    private ShopViewModel shopViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.v("ShopFrag", "oncreate");
        shopViewModel =

        new ViewModelProvider(this, new ShopViewModelFactory(getActivity().getApplication()))
                .get(ShopViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shop, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.items);
        final ItemListAdapter adapter = new ItemListAdapter(new ItemListAdapter.ItemDiff(), this::purchase);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        //ShopViewModel viewModel = new ViewModelProvider(this, new ShopViewModelFactory(getActivity().getApplication())).get(ShopViewModel.class);
        shopViewModel.getShop().observe(getViewLifecycleOwner(), shop -> {
            List<Item> items = new ArrayList<Item>();
            try {
               JSONArray jItems = shop.getJSONArray("items");
               for(int i = 0; i < jItems.length(); i++){
                   JSONObject jItem = (JSONObject) jItems.get(i);
                   Item item = new Item();
                   item.setName(jItem.optString("name"));
                   item.setCost(jItem.optInt("cost"));
                   item.setId(jItem.optInt("id"));
                   items.add(item);
               }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Update the cached copy of the words in the adapter.
            adapter.submitList(items);
        });
        return root;
    }
    public void purchase(Item item){
            //TODO move this to a datasource
        LoginRepository login = LoginRepository.getInstance(new LoginDataSource(), getActivity());
        LoggedInUser user = login.getUser();
        if(user != null && user.getUserId() != null) {
            //points
            //uid
            ANRequest.PostRequestBuilder req = AndroidNetworking.post("https://class.whattheduck.app/api/changePoints");
            req.addBodyParameter("uid", user.getUserId());
            req.addBodyParameter("points", String.valueOf(-item.getCost()));

            req.addHeaders("api-key", "1234")
                    .build().getAsJSONObject((new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int status = response.getInt("status");

                        if (status == 200) {
                            JSONObject scoreJO = (JSONObject) response.getJSONObject("data");
                            Log.v("ScoreDatasource", scoreJO.toString());
                            HashMap<String, Integer> map = new HashMap<>();
                            JSONArray plays = scoreJO.getJSONArray("plays");
                            for(int i = 0; i < plays.length(); i++){
                                JSONObject play = plays.getJSONObject(i);
                                String key = play.keys().next();
                                int value = play.getInt(key);
                                map.put(key, value);
                                Log.v("ScoreDataSource", play.keys().next());
                            }
                            Stats stats = new Stats(
                                    scoreJO.optInt("points"),
                                    scoreJO.optInt("experience"),
                                    scoreJO.optString("updated"),
                                    scoreJO.optString("uid"),
                                    map,
                                    scoreJO.optInt("level"));
                            Log.v("Bought", item.getName());
                            Log.v("Purchase Success", stats.toString());
                          //  success.accept(new Result.Success<Data>(data));
                        } else {
                            JSONObject respError = response.getJSONObject("error");
                            Log.e("Purchase Error", respError.toString());
                            //error.accept(new Result.Error(respError.getString("code"), new Exception(respError.getString("message"))));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Purchase Error", e.getMessage());
                        //error.accept(new Result.Error("json-error", new Exception(e.getMessage())));
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Log.e("Purchase Error", anError.getMessage());
                    //error.accept(new Result.Error("an-error", new Exception(anError.getErrorBody())));
                }
            }));
        }

    }

}