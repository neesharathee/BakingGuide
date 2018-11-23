package com.example.android.bakersapp.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;


public class UpdateBakingWidgetService extends IntentService {


    public UpdateBakingWidgetService() {
        super("UpdateBakingWidgetService");
    }

    public static void startBakingService(Context context,
                                          ArrayList<String> fromActivityIngredientsList) {
        Intent intent = new Intent(context, UpdateBakingWidgetService.class);
        intent.putExtra("Passed_Ingredients", fromActivityIngredientsList);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ArrayList<String> fromActivityIngredientsList =
                    intent.getExtras().getStringArrayList("Passed_Ingredients");
            handleActionUpdateBakingWidgets(fromActivityIngredientsList);
        }
    }

    private void handleActionUpdateBakingWidgets(ArrayList<String> fromActivityIngredientsList) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE2");
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE2");
        intent.putStringArrayListExtra("Passed_Ingredients", fromActivityIngredientsList);
        sendBroadcast(intent);
    }

}
