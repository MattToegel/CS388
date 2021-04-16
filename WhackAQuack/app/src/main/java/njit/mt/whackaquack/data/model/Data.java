package njit.mt.whackaquack.data.model;

import org.json.JSONObject;

public class Data {
    private String uid;
    private String appKey;
    private JSONObject value;
    private String key;

    public Data(String key, JSONObject value){
        this.key = key;
        this.value = value;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public JSONObject getValue() {
        return value;
    }

    public void setValue(JSONObject value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return String.format("Key %s, Value %s", getKey(), getValue());
    }


}
