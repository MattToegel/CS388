package njit.mt.whackaquack.data;

import android.util.Log;

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
import java.util.function.Consumer;

import njit.mt.whackaquack.data.model.ScoreData;
import njit.mt.whackaquack.data.model.Stats;


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class ScoreDataSource {
    public void saveScore(ScoreData score, Consumer<Result<Stats>> success, Consumer<Result.Error> error) {
        ANRequest.PostRequestBuilder req = AndroidNetworking.post("https://class.whattheduck.app/api/saveScore");
        if (score.getAppKey() != null) {
            req.addBodyParameter("appKey", score.getAppKey());
        }
        if (score.getUid() != null) {
            req.addBodyParameter("uid", score.getUid());
        }
        if (score.getScore() > -1) {
            req.addBodyParameter("score", score.getScore() + "");
        }
        if (score.getMaxScore() > -1) {
            req.addBodyParameter("maxScore", score.getMaxScore() + "");
        }
        req.addHeaders("api-key", "1234")
                .build().getAsJSONObject((new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");

                    if (status == 200) {
                        //TODO: change response handling from Score to Stats
                        JSONObject scoreJO = (JSONObject) response.getJSONObject("data");
                        Log.v("ScoreDatasource", scoreJO.toString());
                       /* ScoreData sd = new ScoreData(
                                scoreJO.optString("appKey"),
                                scoreJO.optString("uid"),
                                scoreJO.optInt("score"),
                                scoreJO.optString("created"),
                                scoreJO.optString("uid")
                        );*/
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
                        success.accept(new Result.Success<Stats>(stats));
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

    public void getScores(ScoreData score, Consumer<Result<List<ScoreData>>> success, Consumer<Result.Error> error) {
        ANRequest.GetRequestBuilder req = AndroidNetworking.get("https://class.whattheduck.app/api/getScores");
        if (score.getAppKey() != null) {
            req.addQueryParameter("appKey", score.getAppKey());
        }
        if (score.getUid() != null) {
            req.addQueryParameter("uid", score.getUid());
        }
        if (score.getId() != null) {
            req.addQueryParameter("startAfter", score.getId());
        }
        //TODO update a way to change this, for now it's here as an example
        //possible values are "history" or "top"
        req.addQueryParameter("type", "top");
        req.addHeaders("api-key", "1234")
                .build().getAsJSONObject((new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");

                    if (status == 200) {
                        JSONArray scoreJOs = response.getJSONArray("data");
                        Log.v("ScoreDataSource", scoreJOs.toString());
                        List<ScoreData> scores = new ArrayList<ScoreData>();
                        for (int i = 0; i < scoreJOs.length(); i++) {
                            JSONObject scoreJO = (JSONObject) scoreJOs.get(i);
                            Log.v("ScoreDatasource", scoreJO.toString());
                            ScoreData sd = new ScoreData(
                                    scoreJO.optString("appKey"),
                                    scoreJO.optString("uid"),
                                    scoreJO.optInt("score"),
                                    scoreJO.optString("created"),
                                    scoreJO.optString("uid")
                            );
                            scores.add(sd);
                        }
                        success.accept(new Result.Success<List<ScoreData>>(scores));
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