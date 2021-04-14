package njit.mt.whackaquack.ui.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import njit.mt.whackaquack.data.DataStoreRepository;
import njit.mt.whackaquack.data.LoginRepository;
import njit.mt.whackaquack.data.Result;
import njit.mt.whackaquack.data.model.Data;
import njit.mt.whackaquack.data.model.LoggedInUser;
public class DataStoreViewModel extends ViewModel {
    DataStoreRepository dataStoreRepository;
    LoginRepository loginRepository;
    private MutableLiveData<String> error=  new MutableLiveData<>();
    private MutableLiveData<String> json = new MutableLiveData<>();

    public LiveData<String> getJSON(){
        return json;
    }
    public LiveData<String> getError(){
        return error;
    }

    public DataStoreViewModel(DataStoreRepository dataStoreRepository, LoginRepository loginRepository) {
        this.dataStoreRepository = dataStoreRepository;
        this.loginRepository = loginRepository;

    }
    public void setData(String key, String value){
        LoggedInUser user = loginRepository.getUser();
        if(user != null && user.getUserId() != null) {
            dataStoreRepository.setData("njit.mt.test", key, value, user.getUserId(),
                    (Result<Data> success)->{
                        Data rd = ((Result.Success<Data>) success).getData();
                        json.setValue(rd.toString());
                        error.setValue("");
                    },
                    (Result.Error err)->{
                        json.setValue("");
                        error.setValue(err.getCode() + err.getError().getMessage());
                    });

        }
    }
    public void getData(String key){
        LoggedInUser user = loginRepository.getUser();
        if(user != null && user.getUserId() != null) {
            dataStoreRepository.getData("njit.mt.test", key, user.getUserId(),
                    (Result<Data> success)->{
                        Data rd = ((Result.Success<Data>) success).getData();
                        json.setValue(rd.getValue().toString());
                        error.setValue("");
                    },
                    (Result.Error err)->{
                        json.setValue("");
                        error.setValue(err.getCode() + err.getError().getMessage());
                    });
        }
    }
}