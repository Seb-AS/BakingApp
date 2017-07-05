package com.sebas.bakingapp.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.sebas.bakingapp.fragment.RecipeListFragment;
import com.sebas.bakingapp.presenter.RecipeListPresenter;
import com.sebas.bakingapp.model.Recipe;
import com.sebas.bakingapp.widget.RecipeWidgetService;

public class MainActivity extends BaseActivity implements RecipeListPresenter.Callbacks {
    @Override
    protected Fragment createFragment() {
        return new RecipeListFragment();
    }

    @Override
    public void recipeSelected(Recipe recipe) {
        Intent intent = RecipeDetailActivity.newIntent(this, recipe);
        RecipeWidgetService.startActionUpdateRecipeWidgets(this, recipe);
        startActivity(intent);
    }
}
