package com.example.android.bakersapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakersapp.R;
import com.example.android.bakersapp.models.Ingredient;

import java.util.List;
import java.util.Locale;


public class IngredientListRecyclerAdapter
        extends RecyclerView.Adapter<IngredientListRecyclerAdapter.IngredientsViewHolder> {

    private List<Ingredient> dataList;

    public void setIngredientData(List<Ingredient> ingredientData) {
        dataList = ingredientData;
        notifyDataSetChanged();
    }

    @Override
    public IngredientsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.rvi_ingredient_list_item, viewGroup, false);
        return new IngredientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientsViewHolder holder, int position) {
        Double quantity = dataList.get(position).getQuantity();
        String measure = dataList.get(position).getMeasure();
        String ingredient = dataList.get(position).getIngredient();

        holder.mIngredientQuantityTextView.setText(
                String.format(Locale.getDefault(), (quantity % 1 == 0 ? "%.0f" : "%.2f"), quantity));
        holder.mIngredientMeasureTextView.setText(measure);
        holder.mIngredientNameTextView.setText(ingredient);
    }

    @Override
    public int getItemCount() {
        return (dataList == null) ? 0 : dataList.size();
    }

    public class IngredientsViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView mIngredientNameTextView;
        private final TextView mIngredientQuantityTextView;
        private final TextView mIngredientMeasureTextView;

        private IngredientsViewHolder(View itemView) {
            super(itemView);

            mIngredientNameTextView = (TextView) itemView.findViewById(R.id.tv_ingredient_name);
            mIngredientQuantityTextView = (TextView) itemView.findViewById(R.id.tv_ingredient_value);
            mIngredientMeasureTextView = (TextView) itemView.findViewById(R.id.tv_ingredient_measure);
        }

    }

}
