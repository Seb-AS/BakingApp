package com.sebas.bakingapp.presenter;

import com.sebas.bakingapp.model.Step;
import java.util.ArrayList;

public interface RecipeDetailPresenterViewContract {

    interface View {}

    interface Presenter {

        void stepClicked(ArrayList<Step> stepList, int currentStep,
                         String recipeName, android.view.View view);

    }
}

