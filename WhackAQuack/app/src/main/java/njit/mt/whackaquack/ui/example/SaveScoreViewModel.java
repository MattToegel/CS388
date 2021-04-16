package njit.mt.whackaquack.ui.example;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import njit.mt.whackaquack.data.LoginRepository;
import njit.mt.whackaquack.data.Result;
import njit.mt.whackaquack.data.ScoreRepository;
import njit.mt.whackaquack.data.model.LoggedInUser;
import njit.mt.whackaquack.data.model.ScoreData;

public class SaveScoreViewModel extends ViewModel {
    ScoreRepository scoreRepository;
    LoginRepository loginRepository;


    public SaveScoreViewModel(ScoreRepository scoreRepository, LoginRepository loginRepository) {
        this.scoreRepository = scoreRepository;
        this.loginRepository = loginRepository;

    }
    public void saveScore(String score){

        LoggedInUser user = loginRepository.getUser();
        if(user != null && user.getUserId() != null) {
            ScoreData sd = new ScoreData("njit.mt.test", user.getUserId(), Integer.parseInt(score), null, null);
            sd.setMaxScore(1000);
            scoreRepository.saveScore(sd);
        }
    }

}