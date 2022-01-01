package com.icu.kandeneme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CellSeekersAdapter extends RecyclerView.Adapter<CellSeekersAdapter.MyViewHolder> implements Filterable {
    private final ArrayList<CardModel2> cardModels;
    private final ArrayList<CardModel2> allCardModels;
    private final LayoutInflater inflater;
    Context context;

    public CellSeekersAdapter(Context context, ArrayList<CardModel2> cardModels, ArrayList<CardModel2> allCardModels) {
        inflater = LayoutInflater.from(context);
        this.cardModels = cardModels;
        this.context = context;
        this.allCardModels = allCardModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_cell_seekers, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CardModel2 selectedCardModel = cardModels.get(position);
        holder.setData(selectedCardModel);
        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + selectedCardModel.getContact()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardModels.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<CardModel2> filteredModel = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredModel.addAll(allCardModels);

            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CardModel2 model : cardModels) {
                    if (model.getCity().toLowerCase().contains(filterPattern)) {
                        filteredModel.add(model);

                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredModel;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            cardModels.clear();
            cardModels.addAll((ArrayList<CardModel2>) results.values);
            notifyDataSetChanged();
        }

    };


    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtNameSurname;
        private final TextView txtDateOfSearch;
        private final TextView txtAge;
        private final TextView txtCity;
        private final TextView txtContactNumber;
        private final TextView txtDiseases;
        private final TextView txtHospital;
        private final Button callButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtNameSurname = itemView.findViewById(R.id.txtNameSurname3);
            txtDateOfSearch = itemView.findViewById(R.id.txtDateOfSearch3);
            txtAge = itemView.findViewById(R.id.txtAge);
            txtCity = itemView.findViewById(R.id.txtCity3);
            txtContactNumber = itemView.findViewById(R.id.txtContactNumber3);
            txtHospital = itemView.findViewById(R.id.txtHospital3);
            txtDiseases = itemView.findViewById(R.id.txtDiseases);
            callButton = itemView.findViewById(R.id.callButton3);
        }

        @SuppressLint("SetTextI18n")
        public void setData(CardModel2 selectedModelExample) {

            String nameSurname = selectedModelExample.getNameSurname();
            String age = selectedModelExample.getAge();
            String dateOfSearch = String.valueOf(selectedModelExample.getDateOfSearch());
            String city = selectedModelExample.getCity();
            String contactNumber = selectedModelExample.getContact();
            String hospital = selectedModelExample.getHospital();
            String diseases = selectedModelExample.getDiseases();

            txtNameSurname.setText(nameSurname);
            txtDateOfSearch.setText(dateOfSearch);
            txtAge.setText("(" + age + ")");
            txtCity.setText(city);
            txtContactNumber.setText(contactNumber);
            txtHospital.setText(hospital);
            txtDiseases.setText(diseases);


        }
    }
}
