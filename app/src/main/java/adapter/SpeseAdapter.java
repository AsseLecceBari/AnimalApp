package adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import it.uniba.dib.sms2223_2.R;
import model.SpesaAnimale;

//Creo la classe View AnimalAdapter che contiene i riferimenti ai widget della recycleViewAnimal da popolare
public class SpeseAdapter extends RecyclerView.Adapter<SpeseAdapter.ViewHolder>{
    //Array con tutti i dati sugli animali da inserire nella view
    private ArrayList<SpesaAnimale> localDataSet;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private  TextView descrizione;
        private  TextView data;

        public TextView getDescrizione() {
            return descrizione;
        }

        public TextView getData() {
            return data;
        }

        public ViewHolder(View view) {
            super(view);

            //Prendo i riferimenti ai widget
            descrizione = (TextView) view.findViewById(R.id.descrizioneView);
            data = (TextView) view.findViewById(R.id.dataView);
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
        holder.getDescrizione().setText(localDataSet.get(position).getDescrizione());
        holder.getData().setText(localDataSet.get(position).getData());

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
