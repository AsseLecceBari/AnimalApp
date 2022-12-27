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
import model.Animale;

public class GestioneRichiesteCaricoAdapter extends RecyclerView.Adapter<GestioneRichiesteCaricoAdapter.ViewHolder>{
    //Array con tutti i dati sugli animali da inserire nella view
    private ArrayList<Animale> localDataSet=new ArrayList<>();

    private  ArrayList<Integer> positionSelectedCB=new ArrayList<>();
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nomeAnimale;
        private  TextView genereAnimale;
        private  TextView specieAnimale;



        private TextView sessoAnimale;

        private ImageView imageAnimal;

        public TextView getSessoAnimale() {return sessoAnimale;}

        public TextView getGenereAnimale() {
            return genereAnimale;
        }

        public TextView getSpecieAnimale() {
            return specieAnimale;
        }


        public ImageView getImageAnimal() {
            return imageAnimal;
        }

        public TextView getNomeAnimale() {
            return nomeAnimale;
        }

        public ViewHolder(View view) {
            super(view);

            //Prendo i riferimenti ai widget
            nomeAnimale = (TextView) view.findViewById(R.id.nomeAnimaleView);
            genereAnimale=(TextView) view.findViewById(R.id.genereAnimaleView);
            specieAnimale= (TextView) view.findViewById(R.id.specieAnimaleView);
            sessoAnimale=view.findViewById(R.id.sessoAnimaleView);
            imageAnimal=(ImageView) view.findViewById(R.id.imageAnimal);

        }

    }

    public void filterList(ArrayList<Animale> filterlist) {
        localDataSet.clear();
        localDataSet.addAll(filterlist);
        //notifica all'adapter che sono cambiati i dati nella recycle view
        notifyDataSetChanged();
    }
    //Funzione richiamata dal fragment myAnimals,il quale passa i dati degli animali
    public GestioneRichiesteCaricoAdapter(ArrayList<Animale> dataSet){
        localDataSet.clear();
        localDataSet.addAll(dataSet);

    }

    @NonNull
    @Override
    public GestioneRichiesteCaricoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_gestione_richieste, parent, false);
        return new ViewHolder(v);

    }




    @Override
    public void onBindViewHolder(@NonNull GestioneRichiesteCaricoAdapter.ViewHolder holder, int position) {
        //Vengono inseriti i dati degli animali negli item
        FirebaseStorage storage;
        StorageReference storageRef;
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        holder.getNomeAnimale().setText(localDataSet.get(position).getNome());
        holder.getSpecieAnimale().setText(localDataSet.get(position).getSpecie());
        holder.getSessoAnimale().setText(localDataSet.get(position).getSesso());
        holder.getGenereAnimale().setText(localDataSet.get(position).getGenere());
        storageRef.child(localDataSet.get(position).getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView.getContext())
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageAnimal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}

