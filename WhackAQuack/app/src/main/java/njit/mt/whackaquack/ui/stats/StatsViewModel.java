package njit.mt.whackaquack.ui.stats;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import njit.mt.whackaquack.data.ScoreDataSource;
import njit.mt.whackaquack.data.ScoreRepository;
import njit.mt.whackaquack.data.model.ScoreData;
import njit.mt.whackaquack.data.model.Stats;

public class StatsViewModel extends ViewModel {


    ScoreRepository repository;
    public StatsViewModel(ScoreRepository repository ) {
        this.repository = repository;
    }
    public LiveData<Stats> getLastStats(){
        return this.repository.getLastStats();
    }
}