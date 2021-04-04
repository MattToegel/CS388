package njit.mt.whackaquack.ui.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GameViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is to explore touch events");
    }

    public LiveData<String> getText() {
        return mText;
    }
}