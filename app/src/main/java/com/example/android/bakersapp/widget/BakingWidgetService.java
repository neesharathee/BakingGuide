package com.example.android.bakersapp.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakersapp.R;

import java.util.List;

import static com.example.android.bakersapp.widget.BakingAppWidgetProvider.ingredientsList;

public class BakingWidgetService extends RemoteViewsService {


    private List<String> remoteIngredientsList;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        final Context mContext;

        GridRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            remoteIngredientsList = ingredientsList;
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return remoteIngredientsList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_item);
            views.setTextViewText(R.id.tv_widget_grid_item, remoteIngredientsList.get(position));
            Intent fillInIntent = new Intent();
            views.setOnClickFillInIntent(R.id.tv_widget_grid_item, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}

