package com.example.android.bakersapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.bakersapp.R;
import com.example.android.bakersapp.RecipeDetailActivity;

import java.util.ArrayList;

public class BakingAppWidgetProvider extends AppWidgetProvider {


    static ArrayList<String> ingredientsList = new ArrayList<>();

    private static void updateAppWidget(Context context,
                                        AppWidgetManager appWidgetManager,
                                        int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        Intent appIntent = new Intent(context, RecipeDetailActivity.class);
        appIntent.addCategory(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent appPendingIntent = PendingIntent.getActivity(
                context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);

        Intent intent = new Intent(context, BakingWidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void onUpdateWidgets(Context context,
                                        AppWidgetManager appWidgetManager,
                                        int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BakingAppWidgetProvider.class));

        final String action = intent.getAction();

        if (action.equals("android.appwidget.action.APPWIDGET_UPDATE2")) {
            ingredientsList = intent.getExtras().getStringArrayList("Passed_Ingredients");
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
            BakingAppWidgetProvider.onUpdateWidgets(context, appWidgetManager, appWidgetIds);
            super.onReceive(context, intent);
        }
    }

}

