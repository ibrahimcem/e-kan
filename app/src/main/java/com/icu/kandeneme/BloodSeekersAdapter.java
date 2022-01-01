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

public class BloodSeekersAdapter extends RecyclerView.Adapter<BloodSeekersAdapter.MyViewHolder> implements Filterable {
    private final ArrayList<CardModel> cardModels;
    private final ArrayList<CardModel> allCardModels2;
    private final LayoutInflater inflater;
    Context context;

    public BloodSeekersAdapter(Context context, ArrayList<CardModel> cardModels, ArrayList<CardModel> allCardModels2) {
        inflater = LayoutInflater.from(context);
        this.cardModels = cardModels;
        this.context = context;
        this.allCardModels2 = allCardModels2;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_blood_seekers, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CardModel selectedCardModel = cardModels.get(position);
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
            ArrayList<CardModel> filteredModel = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredModel.addAll(allCardModels2);

            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CardModel model : cardModels) {
                    if (model.getCity().toLowerCase().contains(filterPattern)) {
                        filteredModel.add(model);

                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredModel;
            return results;
            /*System.out.println(allCardModels+"buu");
            ArrayList<CardModel> filteredModel=new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredModel.addAll(allCardModels);
            }else {
                for (CardModel model:allCardModels){
                   if (model.getCity().toLowerCase().contains(constraint.toString().toLowerCase())){
                       filteredModel.add(model);

                   }
                }
            }
            FilterResults results= new FilterResults();
            results.values=filteredModel;
            return results;*/
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            cardModels.clear();
            cardModels.addAll((ArrayList<CardModel>) results.values);
            notifyDataSetChanged();
        }

    };


    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtNameSurname;
        private final TextView txtDateOfSearch;
        private final TextView txtBlood;
        private final TextView txtCity;
        private final TextView txtContactNumber;
        private final TextView txtType;
        private final TextView txtHospital;
        private final Button callButton;

        public MyViewHolder(View itemView) {
            super(itemView);


            txtNameSurname = itemView.findViewById(R.id.txtNameSurname);
            txtDateOfSearch = itemView.findViewById(R.id.txtDateOfSearch);
            txtBlood = itemView.findViewById(R.id.txtBlood);
            txtCity = itemView.findViewById(R.id.txtCity);
            txtContactNumber = itemView.findViewById(R.id.txtContactNumber);
            txtHospital = itemView.findViewById(R.id.txtHospital);
            txtType = itemView.findViewById(R.id.txtType);
            callButton = itemView.findViewById(R.id.callButton);
        }

        @SuppressLint("SetTextI18n")
        public void setData(CardModel selectedModelExample) {

            String nameSurname = selectedModelExample.getNameSurname();
            String blood = selectedModelExample.getBlood();
            String dateOfSearch = String.valueOf(selectedModelExample.getDateOfSearch());
            String city = selectedModelExample.getCity();
            String contactNumber = selectedModelExample.getContact();
            String hospital = selectedModelExample.getHospital();
            String type = selectedModelExample.getType();

            txtNameSurname.setText(nameSurname);
            txtDateOfSearch.setText(dateOfSearch);
            txtBlood.setText(blood);
            txtCity.setText(city);
            txtContactNumber.setText(contactNumber);
            txtHospital.setText(hospital);
            txtType.setText(type);


        }
    }
}
