package dhu.cst.the170910224andthe170120321.accountbook.ui.monthly;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MonthlyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MonthlyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Monthly fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}