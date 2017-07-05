package com.sebas.bakingapp.networking;

import com.sebas.bakingapp.model.Recipe;
import java.util.ArrayList;
import io.reactivex.Observable;
import retrofit2.http.GET;

public interface UdacityApiObservable {
    @GET(" ")
    Observable<ArrayList<Recipe>> getUdacityRecipeResult();
}
