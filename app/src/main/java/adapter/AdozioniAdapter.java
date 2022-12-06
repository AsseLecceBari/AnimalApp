package adapter;

import static androidx.core.content.ContextCompat.startActivity;
import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import class_general.RecyclerItemClickListener;
import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Animale;

public class AdozioniAdapter extends RecyclerView.Adapter<adapter.AdozioniAdapter.ViewHolder> {

    private int vistamieianimali;
    private View eliminaAnnuncio;


    //Creo la classe View AnimalAdapter che contiene i riferimenti ai widget della recycleViewAnimal da popolare

    //Array con tutti i dati sugli animali da inserire nella view
    private ArrayList<Animale> localDataSet;
    private OnClickListener onClickListener;


    public static class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView nomeAnimale;
        private TextView genereAnimale;
        private TextView specieAnimale;
        private TextView dataNascitaAnimale;
        private TextView codiceAnimale;
        private ImageView imageAnimal;
        private View elimina;
        private View viewtot;

        private int posizione;





        public TextView getGenereAnimale() {
            return genereAnimale;
        }

        public TextView getSpecieAnimale() {
            return specieAnimale;
        }

        public TextView getDataNascitaAnimale() {
            return dataNascitaAnimale;
        }

        public TextView getCodiceAnimale() {
            return codiceAnimale;
        }

        public ImageView getImageAnimal() {
            return imageAnimal;
        }

        public TextView getNomeAnimale() {
            return nomeAnimale;
        }
        public View getButtone(){
            return elimina;
        }
        public View getView(){
            return viewtot;
        }





        public ViewHolder(View view) {
            super(view);






            Log.d("ciao10","ciaod");


            //Prendo i riferimenti al layout di ogni singola riga
            nomeAnimale = (TextView) view.findViewById(R.id.nomeAnimaleView);
            genereAnimale = (TextView) view.findViewById(R.id.genereAnimaleView);
            specieAnimale = (TextView) view.findViewById(R.id.specieAnimaleView);
            dataNascitaAnimale = (TextView) view.findViewById(R.id.dateNascitaAnimaleView);
            codiceAnimale = (TextView) view.findViewById(R.id.codiceAnimaleView);
            imageAnimal = (ImageView) view.findViewById(R.id.imageAnimal);
            elimina= view.findViewById(R.id.button2);
            viewtot=view;


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
















         /*   view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("ciao10","peri");
                }
            });*/

        }




    }

    //Funzione richiamata dal fragment myAnimals,il quale passa i dati degli animali
    public AdozioniAdapter(ArrayList<Animale> dataSet, int vista) {
        localDataSet = dataSet;
        vistamieianimali = vista;

    }

    @NonNull
    @Override
    public AdozioniAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (vistamieianimali == 2) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_view_adozioni, parent, false);

            return new adapter.AdozioniAdapter.ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_le_mie_adozioni, parent, false);





            // return new adapter.AdozioniAdapter.ViewHolder(v);
            return new adapter.AdozioniAdapter.ViewHolder(v);
        }


    }



    @Override

    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull adapter.AdozioniAdapter.ViewHolder holder, int position) {
        //Vengono inseriti i dati degli animali
        holder.getNomeAnimale().setText(localDataSet.get(position).getNome());
        //holder.getSpecieAnimale().setText(localDataSet.get(position).getSpecie());
        //  holder.getGenereAnimale().setText(localDataSet.get(position).getGenere());
        // holder.getDataNascitaAnimale().setText(localDataSet.get(position).getDataDiNascita().toString());
        holder.getCodiceAnimale().setText(localDataSet.get(position).getIdAnimale());
        if(holder.getButtone()!= null) {

            holder.getButtone().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.oneliminaClick(view, position);
                }
            });
        }
       holder.getView().setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               onClickListener.onitemClick(view,position);
           }
       });


        // holder.getImageAnimal().setImageBitmap(localDataSet.get(position).getFotoProfilo());


    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }




    public void filterList(ArrayList<Animale> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        localDataSet = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onitemClick(View view, int position);

        void oneliminaClick(View view, int position) ;


    }
    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener= listener;
    }










}


