package dhu.cst.the170910224andthe170120321.accountbook.ui.weekly;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WeeklyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WeeklyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Weekly fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}