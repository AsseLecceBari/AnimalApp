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
public class SegnalazioneSanitariaAdapter extends RecyclerView.Adapter<SegnalazioneSanitariaAdapter.ViewHolder>{
    //Array con tutti i dati sugli animali da inserire nella view
    private ArrayList<SegnalazioneSanitaria> localDataSet;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private  TextView data, motivoConsultazione;


        public TextView getData() {
            return data;
        }

        public TextView getMotivoConsultazione() {
            return motivoConsultazione;
        }

        public ViewHolder(View view) {
            super(view);

            //Prendo i riferimenti ai widget
            data = (TextView) view.findViewById(R.id.dataView);
            motivoConsultazione = (TextView) view.findViewById(R.id.motivoConsultazioneView);
        }


    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<SegnalazioneSanitaria> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        localDataSet = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    //Funzione richiamata dal fragment myAnimals,il quale passa i dati degli animali
    public SegnalazioneSanitariaAdapter(ArrayList<SegnalazioneSanitaria> dataSet){
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public SegnalazioneSanitariaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_libretto, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseStorage storage;
        StorageReference storageRef;
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();

        holder.getData().setText(localDataSet.get(position).getData());
        holder.getMotivoConsultazione().setText(localDataSet.get(position).getMotivoConsultazione());

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
