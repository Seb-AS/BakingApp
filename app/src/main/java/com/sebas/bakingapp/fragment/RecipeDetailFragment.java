package com.sebas.bakingapp.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.sebas.bakingapp.util.OnItemClickListener;
import com.sebas.bakingapp.R;
import com.sebas.bakingapp.presenter.RecipeDetailPresenter;
import com.sebas.bakingapp.presenter.RecipeDetailPresenterViewContract;
import com.sebas.bakingapp.databinding.FragmentRecipeDetailBinding;
import com.sebas.bakingapp.model.Ingredient;
import com.sebas.bakingapp.model.Recipe;
import com.sebas.bakingapp.model.Step;
import com.sebas.bakingapp.adapter.StepAdapter;
import java.util.ArrayList;

public class RecipeDetailFragment extends Fragment implements RecipeDetailPresenterViewContract.View {
    private static final String BUNDLE_RECIPE_DATA =
            "com.sebas.bakingapp.recipe_data";
    private final String INSTANCE_KEY_ADAPTER_POSITION = "instance_key_adapter_position";
    private final String INSTANCE_KEY_RECIPE = "instance_key_recipe";
    private final String INSTANCE_KEY_INGREDIENTS_ID = "instance_key_ingredients_id";
    private final String INSTANCE_KEY_INGREDIENTS_COUNT = "instance_key_ingredients_count";
    private final String INSTANCE_KEY_STEPS = "instance_key_steps";

    private ShareActionProvider mShareActionProvider;
    private FragmentRecipeDetailBinding binding;
    private Recipe mRecipe;
    private RecipeDetailPresenter mRecipeDetailPresenter;
    private RecyclerView mStepRecyclerView;
    private StepAdapter mStepAdapter;
    private int mStepAdapterSavedPosition = 0;
    private ArrayList<Step> mStepList = new ArrayList<>();
    private ArrayList<CheckBox> mIngredientList = new ArrayList<>();

    public static RecipeDetailFragment newInstance(Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_RECIPE_DATA, recipe);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if ((arguments != null) && (arguments.containsKey(BUNDLE_RECIPE_DATA))) {
            mRecipe = arguments.getParcelable(BUNDLE_RECIPE_DATA);
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_KEY_ADAPTER_POSITION)) {
            mStepAdapterSavedPosition = savedInstanceState.getInt(INSTANCE_KEY_ADAPTER_POSITION);
        }
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(INSTANCE_KEY_RECIPE)) {
                mRecipe = savedInstanceState.getParcelable(INSTANCE_KEY_RECIPE);
            }
            if(savedInstanceState.containsKey(INSTANCE_KEY_STEPS)) {
                mStepList = savedInstanceState.getParcelableArrayList(INSTANCE_KEY_STEPS);
            }
            if(savedInstanceState.containsKey(INSTANCE_KEY_INGREDIENTS_COUNT)) {
                int ingredientCount = savedInstanceState.getInt(INSTANCE_KEY_INGREDIENTS_COUNT);
                for(int i=0; i<ingredientCount; i++) {
                    CheckBox checkBox = new CheckBox(this.getContext());
                    checkBox.setText(String.valueOf(mRecipe.getIngredients().get(i).getQuantity()) +
                            String.valueOf(mRecipe.getIngredients().get(i).getMeasure()) + " " + mRecipe.getIngredients().get(i).getIngredient());
                    checkBox.setChecked(savedInstanceState.getBoolean(INSTANCE_KEY_INGREDIENTS_ID + String.valueOf(i)));
                    mIngredientList.add(checkBox);
                }
            }
        }
        mRecipeDetailPresenter = new RecipeDetailPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail, container, false);
        final View view = binding.getRoot();

        binding.tbToolbar.toolbar.setTitle(mRecipe.getName());
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.tbToolbar.toolbar);

        //Save dynamically created checkbox view state on rotation
        if(mIngredientList.size()>0) {
            for(CheckBox cbIngredientView : mIngredientList) {
                binding.llIngredientChecklist.addView(cbIngredientView);
            }
        } else {
            if (mRecipe.getIngredients() != null && mRecipe.getIngredients().size() > 0) {
                for (Ingredient ingredient : mRecipe.getIngredients()) {
                    CheckBox checkBox = new CheckBox(this.getContext());
                    checkBox.setText(String.valueOf(ingredient.getQuantity()) +
                            String.valueOf(ingredient.getMeasure()) + " " + ingredient.getIngredient());
                    binding.llIngredientChecklist.addView(checkBox);
                    mIngredientList.add(checkBox);
                }
            }
        }
        if (mRecipe.getSteps() != null && mRecipe.getSteps().size() > 0 && mStepList.size()==0) {
            mStepList.addAll(mRecipe.getSteps());
        }

        mStepRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_detail_steps_recycler_view);
        mStepRecyclerView.setHasFixedSize(true);
        mStepRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mStepAdapter = new StepAdapter(mStepList, new OnItemClickListener<Step>() {
            @Override
            public void onClick(Step step, View view) {
                mRecipeDetailPresenter.stepClicked(mStepList, step.getId(), mRecipe.getName(), view);
            }
        });
        mStepAdapter.setStepAdapterCurrentPosition(mStepAdapterSavedPosition);
        mStepRecyclerView.setAdapter(mStepAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.recipe_detail_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_detail_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareIntent(createShareIntent());
    }
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
    private Intent createShareIntent() {
        String msg = mRecipe.getName() + "\n" + "----\n" +
                getString(R.string.ingredients_title) + ":\n" + "----\n";
        for(CheckBox ingredient : mIngredientList){
            msg += ingredient.getText() + "\n";
        }
        msg += getString(R.string.steps_title) + ":\n" + "----\n";
        for(Step step : mStepList){
            msg += step.getShortDescription() + "\n";
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, msg);
        return shareIntent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INSTANCE_KEY_ADAPTER_POSITION, mStepAdapter.getStepAdapterCurrentPosition());
        outState.putParcelable(INSTANCE_KEY_RECIPE, mRecipe);
        outState.putParcelableArrayList(INSTANCE_KEY_STEPS, mStepList);
        outState.putInt(INSTANCE_KEY_INGREDIENTS_COUNT, mIngredientList.size());
        for(int ingedientId = 0; ingedientId<mIngredientList.size(); ingedientId++) {
                    outState.putBoolean(INSTANCE_KEY_INGREDIENTS_ID + String.valueOf(ingedientId),
                    mIngredientList.get(ingedientId).isChecked());
        }
    }
}

