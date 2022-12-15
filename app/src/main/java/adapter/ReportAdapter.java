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

import java.util.ArrayList;

import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Segnalazione;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder>{
    private ArrayList<Segnalazione> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dataReport;
        private TextView tipoReport;

        private ImageView imageReport;

        public TextView getTipoReport() {
            return tipoReport;
        }



        public TextView getDataReport() {

            return dataReport;
        }


        public ImageView getImageReport() {
            return imageReport;
        }





        public ViewHolder(View view) {
            super(view);
            //Prendo i riferimenti ai widget
            dataReport = (TextView) view.findViewById(R.id.dataPub);
            tipoReport= (TextView) view.findViewById(R.id.tipoReport);


            imageReport=(ImageView) view.findViewById(R.id.imageReport);
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

        // below line is to add our filtered
        // list in our course array list.
        localDataSet = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }



}
