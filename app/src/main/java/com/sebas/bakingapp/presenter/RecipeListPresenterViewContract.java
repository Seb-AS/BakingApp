package com.sebas.bakingapp.presenter;

import android.support.annotation.StringRes;
import com.sebas.bakingapp.model.Recipe;
import java.util.ArrayList;

public interface RecipeListPresenterViewContract {
    interface View {

        void updateAdapter(ArrayList<Recipe> recipeList);

        void displaySnackbarMessage(@StringRes int stringResId);

        boolean isActive();

    }

    interface Presenter {

        void fetchRecipes();

        void recipeClicked(Recipe recipe, android.view.View view);

        void viewDestroy();

    }
}
