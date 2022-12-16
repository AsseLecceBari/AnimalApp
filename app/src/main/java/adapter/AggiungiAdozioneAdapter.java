package adapter;



import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class AggiungiAdozioneAdapter extends RecyclerView.Adapter<AggiungiAdozioneAdapter.ViewHolder>{
    //Creo la classe View AnimalAdapter che contiene i riferimenti ai widget della recycleViewAnimal da popolare

    //Array con tutti i dati sugli animali da inserire nella view
    private ArrayList<Animale> localDataSet;
    private OnClickListener onClickListener;
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageAnimal;
        private CheckBox checkBox;
        private View viewtot;




        public ImageView getImageAnimal() {
            return imageAnimal;
        }



        public CheckBox getCheckBox(){return checkBox;}
        public View getView(){return viewtot;}



        public ViewHolder(View view) {
            super(view);
            //Prendo i riferimenti al layout di ogni singola riga

            imageAnimal=(ImageView) view.findViewById(R.id.imageAnimal);
            checkBox=view.findViewById(R.id.checkBoxAggiungiadozioni);
            viewtot= view;
        }


    }

    //Funzione richiamata dal fragment myAnimals,il quale passa i dati degli animali
    public AggiungiAdozioneAdapter(ArrayList<Animale>  dataSet) {
        localDataSet = dataSet;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_aggiungiadozione, parent, false);

        return new AggiungiAdozioneAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        FirebaseStorage storage;
        StorageReference storageRef;
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

       // holder.getNomeAnimale().setText(localDataSet.get(position).getNome());
      //  holder.getSpecieAnimale().setText(localDataSet.get(position).getSpecie());
        //  holder.getGenereAnimale().setText(localDataSet.get(position).getGenere());
        // holder.getDataNascitaAnimale().setText(localDataSet.get(position).getDataDiNascita().toString());
    //    holder.getCodiceAnimale().setText(localDataSet.get(position).getIdAnimale());

        storageRef.child(localDataSet.get(position).getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView.getContext())
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageAnimal);
            }
        });
        holder.getCheckBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.oncheckboxclick(view,position);
            }
        }) ;

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onViewClick(view,position);
            }
        });










            //holder.getImageAnimal().setImageBitmap(localDataSet.get(position).getFotoProfilo());
    }



    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
    public interface OnClickListener {

       void onViewClick(View view , int position);
        void oncheckboxclick(View view, int position);


    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener =  listener;
    }





}


