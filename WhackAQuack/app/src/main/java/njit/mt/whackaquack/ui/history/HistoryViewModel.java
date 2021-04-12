package njit.mt.whackaquack.ui.history;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import njit.mt.whackaquack.data.ScoreDataSource;
import njit.mt.whackaquack.data.ScoreRepository;
import njit.mt.whackaquack.data.model.ScoreData;

public class HistoryViewModel extends ViewModel {

    private ScoreRepository repository;
    private final LiveData<List<ScoreData>> scoreHistory;

    public HistoryViewModel(ScoreRepository repository) {
        this.repository = repository;
        scoreHistory = this.repository.getScores();
    }
    public void queryScore(String appKey){
        repository.queryScore(appKey);
    }
    public LiveData<List<ScoreData>> getScoreHistory() {
        return scoreHistory;
    }
}