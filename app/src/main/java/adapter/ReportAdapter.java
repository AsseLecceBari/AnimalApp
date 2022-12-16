package adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Segnalazione;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder>{
    private ArrayList<Segnalazione> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dataReport,tipoReport,titoloReport;

        private ImageView imageReport;

        public TextView getTipoReport() {
            return tipoReport;
        }



        public TextView getDataReport() {

            return dataReport;
        }

        public TextView getTitoloReport() {
            return titoloReport;
        }

        public ImageView getImageReport() {
            return imageReport;
        }





        public ViewHolder(View view) {
            super(view);
            //Prendo i riferimenti ai widget
            dataReport =  view.findViewById(R.id.dataPub);
            tipoReport=  view.findViewById(R.id.tipoReport);
            titoloReport=  view.findViewById(R.id.titoloReport);


            imageReport= view.findViewById(R.id.imageReport);
        }


    }


    //Funzione richiamata dal fragment myAnimals,il quale passa i dati degli animali
    public ReportAdapter(ArrayList<Segnalazione> dataSet) {

        localDataSet = dataSet;
    }



    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_reports, parent, false);

        return new adapter.ReportAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter.ReportAdapter.ViewHolder holder, int position) {
        //Vengono inseriti i dati degli animali
        holder.getDataReport().setText(localDataSet.get(position).getData());
        holder.getTipoReport().setText(localDataSet.get(position).getTipo());
        holder.getTitoloReport().setText(localDataSet.get(position).getTitolo());

        FirebaseStorage storage;
        StorageReference storageRef;
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();

        if(localDataSet.get(position).getUrlFoto() != null){
            storageRef.child(localDataSet.get(position).getUrlFoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(holder.itemView.getContext())
                            .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.imageReport);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
    public void filterList(ArrayList<Segnalazione> filterlist) {

        localDataSet.clear();
        localDataSet.addAll(filterlist);
        //notifica all'adapter che sono cambiati i dati nella recycle view
        notifyDataSetChanged();
    }




}
