package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import it.uniba.dib.sms2223_2.R;
import model.SegnalazioneSanitaria;
import model.SpesaAnimale;

//Creo la classe View AnimalAdapter che contiene i riferimenti ai widget della recycleViewAnimal da popolare
public class SpeseAdapter extends RecyclerView.Adapter<SpeseAdapter.ViewHolder>{
    //Array con tutti i dati sugli animali da inserire nella view
    private ArrayList<SpesaAnimale> localDataSet;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private  TextView descrizione, data, categoria, spesaTotale;

        public TextView getDescrizione() {
            return descrizione;
        }

        public TextView getData() {
            return data;
        }

        public TextView getCategoria() {
            return categoria;
        }

        public TextView getSpesaTotale() {
            return spesaTotale;
        }

        public ViewHolder(View view) {
            super(view);

            //Prendo i riferimenti ai widget
            descrizione = (TextView) view.findViewById(R.id.motivoConsultazioneView);
            data = (TextView) view.findViewById(R.id.dataView);
            categoria = (TextView) view.findViewById(R.id.categoriaView);
            spesaTotale = (TextView) view.findViewById(R.id.spesaTotaleView);
        }
    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<SpesaAnimale> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        localDataSet = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    //Funzione richiamata dal fragment myAnimals,il quale passa i dati degli animali
    public SpeseAdapter(ArrayList<SpesaAnimale> dataSet){
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public SpeseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_spese, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseStorage storage;
        StorageReference storageRef;
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();

        float speseTotali = localDataSet.get(position).getCostoUnitario() * localDataSet.get(position).getQuantita();
        holder.getDescrizione().setText(localDataSet.get(position).getDescrizione());
        holder.getData().setText(localDataSet.get(position).getData());
        holder.getSpesaTotale().setText(Float.toString(speseTotali));
        holder.getCategoria().setText(localDataSet.get(position).getCategoria());

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
