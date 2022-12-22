package adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import it.uniba.dib.sms2223_2.R;
import model.Animale;

public class PokedexAdapter extends  RecyclerView.Adapter<adapter.PokedexAdapter.ViewHolder>{
    private ArrayList<Animale> localDataSet=new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private  TextView nomeAnimale;
        private ImageView imageAnimal;


        public ImageView getImageAnimal() {
            return imageAnimal;
        }

        public TextView getNomeAnimale() {
            return nomeAnimale;
        }

        public ViewHolder(View view) {
            super(view);

            //Prendo i riferimenti ai widget
            nomeAnimale = (TextView) view.findViewById(R.id.nomeAnimalePokedex);
            imageAnimal=(ImageView) view.findViewById(R.id.imagePokedex);
        }

    }
    public PokedexAdapter(ArrayList<Animale> dataSet){

        localDataSet.clear();
        localDataSet.addAll(dataSet);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_pokedex, parent, false);
        return new PokedexAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PokedexAdapter.ViewHolder holder, int position) {
        holder.getNomeAnimale().setText(localDataSet.get(position).getNome());
        /*
        FirebaseStorage storage;
        StorageReference storageRef;
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        storageRef.child(localDataSet.get(position).getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView.getContext())
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageAnimal);
            }
        });
         */

    }


    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
