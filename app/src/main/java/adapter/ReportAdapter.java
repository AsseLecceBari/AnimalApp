package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Segnalazione;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder>{
    private ArrayList<Segnalazione> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView DescrizioneReport;
        private TextView TipoReport;

        private ImageView imageReport;

        public TextView getTipoReport() {
            return TipoReport;
        }



        public TextView getDescrizioneReport() {

            return DescrizioneReport;
        }


        public ImageView getImageReport() {
            return imageReport;
        }





        public ViewHolder(View view) {
            super(view);
            //Prendo i riferimenti ai widget
            DescrizioneReport = (TextView) view.findViewById(R.id.DescrizioneReport);
            TipoReport= (TextView) view.findViewById(R.id.TipoReport);


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
        holder.getDescrizioneReport().setText(localDataSet.get(position).getDescrizione());
        holder.getTipoReport().setText(localDataSet.get(position).getTipo());
        //holder.getSpecieAnimale().setText(localDataSet.get(position).getSpecie());
        //  holder.getGenereAnimale().setText(localDataSet.get(position).getGenere());
        // holder.getDataNascitaAnimale().setText(localDataSet.get(position).getDataDiNascita().toString());
        //holder.getCodiceAnimale().setText(localDataSet.get(position).getIdAnimale());
        // holder.getImageAnimal().setImageBitmap(localDataSet.get(position).getFotoProfilo());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }




}
