package adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
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
import java.util.Objects;

import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Persona;
import model.Utente;

public class AdozioniAdapter extends RecyclerView.Adapter<adapter.AdozioniAdapter.ViewHolder> {

    private int vistamieianimali;
    //Creo la classe View AnimalAdapter che contiene i riferimenti ai widget della recycleViewAnimal da popolare
    //Array con tutti i dati sugli animali da inserire nella view
    private ArrayList<Animale> localDataSet=new ArrayList<>();
    private OnClickListener onClickListener;
    private ArrayList<Persona> utenteDataset;

    public AdozioniAdapter(ArrayList<Animale> mDataset) {
        localDataSet.clear();
        localDataSet.addAll(mDataset);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nomeAnimale;
        private TextView nomeProprietario;
        private TextView specieAnimale;
        private TextView dataNascitaAnimale;
        private TextView zona;

        private ImageView imageAnimal;
        private View elimina;
        private View viewtot;

        public TextView getProprietario() {
            return nomeProprietario;
        }

        public TextView getSpecieAnimale() {
            return specieAnimale;
        }

        public TextView getDataNascitaAnimale() {
            return dataNascitaAnimale;
        }

        public TextView getZona() {return zona;       }

        public ImageView getImageAnimal() {
            return imageAnimal;
        }

        public TextView getNomeAnimale() {
            return nomeAnimale;
        }

        public View getButtone() {
            return elimina;
        }

        public View getView() {
            return viewtot;
        }


        //ViewHolder serve alla RecycleView per prendere i riferimenti ad ogni singolo Item
        public ViewHolder(View view) {
            super(view);
            //Prendo i riferimenti al layout di ogni singola riga
            nomeAnimale = (TextView) view.findViewById(R.id.nomeAnimaleView);
            nomeProprietario = (TextView) view.findViewById(R.id.nomeProprietario);
            specieAnimale = (TextView) view.findViewById(R.id.specieAnimaleView);
            dataNascitaAnimale = (TextView) view.findViewById(R.id.dateNascitaAnimaleView);
            zona = (TextView) view.findViewById(R.id.zona);
            imageAnimal = (ImageView) view.findViewById(R.id.imageAnimal);
            elimina = view.findViewById(R.id.button2);
            viewtot = view;
        }
    }

    //Funzione richiamata dal fragment myAnimals,il quale passa i dati degli animali
    public AdozioniAdapter(ArrayList<Animale> dataSet, int vista, ArrayList<Persona> utente) {
        localDataSet.clear();
        localDataSet.addAll(dataSet);
        vistamieianimali = vista;
        utenteDataset= utente;
    }
    public AdozioniAdapter(ArrayList<Animale> dataSet, int vista) {
        localDataSet.clear();
        localDataSet.addAll(dataSet);
        vistamieianimali = vista;

    }

    @NonNull
    @Override
    public AdozioniAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
// in base a quale filtro stiamo utilizzando cambia la recycleview da caricare
        if (vistamieianimali == 2) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_adozioni, parent, false);
            return new adapter.AdozioniAdapter.ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_le_mie_adozioni, parent, false);
            return new adapter.AdozioniAdapter.ViewHolder(v);
        }
    }
    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull adapter.AdozioniAdapter.ViewHolder holder, int position) {
        //Vengono inseriti i dati degli animali
        FirebaseStorage storage;
        StorageReference storageRef;
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        if (vistamieianimali == 2) {
            holder.getZona().setText(setZona(position));
            holder.getProprietario().setText(setNomeProprietario(position));
        }
        holder.getNomeAnimale().setText(localDataSet.get(position).getNome());
        //holder.getSpecieAnimale().setText(localDataSet.get(position).getSpecie());
        //  holder.getGenereAnimale().setText(localDataSet.get(position).getGenere());
        // holder.getDataNascitaAnimale().setText(localDataSet.get(position).getDataDiNascita().toString());

        storageRef.child(localDataSet.get(position).getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView.getContext())
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageAnimal);
            }
        });
        if (holder.getButtone() != null) {
            holder.getButtone().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.oneliminaClick(view, position);
                }
            });
        }
        if (holder.getView() != null) {
            holder.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        onClickListener.onitemClick(view, position);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }


    public void filterList(ArrayList<Animale> filterlist) {
        localDataSet.clear();
        localDataSet.addAll(filterlist);
        //notifica all'adapter che sono cambiati i dati nella recycle view
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onitemClick(View view, int position);

        void oneliminaClick(View view, int position);


    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    public void aggiornadataset(ArrayList<Animale> animali) {
        localDataSet = animali;
    }

    public String setZona(int position)
    {

            for(int b=0; b<utenteDataset.size();b++){
                if(Objects.equals(localDataSet.get(position).getEmailProprietario(), utenteDataset.get(b).getEmail())){
                    return utenteDataset.get(b).getIndirizzo().get("città");
                }
            }


        return null;
    }
    public String setNomeProprietario(int position)
    {

        for(int b=0; b<utenteDataset.size();b++){
            if(Objects.equals(localDataSet.get(position).getEmailProprietario(), utenteDataset.get(b).getEmail()))
            {
                return utenteDataset.get(b).getNome()+ " " +utenteDataset.get(b).getCognome();
            }
        }


        return null;
    }


}


