package adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import it.uniba.dib.sms2223_2.R;
import model.Animale;

public class DispositiviDisponibiliBt extends RecyclerView.Adapter<DispositiviDisponibiliBt.ViewHolder>{
    //Array con tutti i dati sugli animali da inserire nella view
    private ArrayList<BluetoothDevice> localDataSet = new ArrayList<>();
    int mvista;




    public DispositiviDisponibiliBt(int vista) {
mvista= vista;


    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView Nome;

        public TextView getNome() {
            return Nome;
        }



        public ViewHolder(View view) {
            super(view);

            //Prendo i riferimenti ai widget
            Nome = (TextView) view.findViewById(R.id.NomeDispositivo);

        }
    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<BluetoothDevice> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        localDataSet = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(mvista==2) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view_dispositivononassociato, parent, false);
            return new ViewHolder(v);
        }
        else {
            Log.d("ciao21","ciaso");
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view_dispositivoassociato, parent, false);
            return new ViewHolder(v);
        }



    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.getNome().setText(localDataSet.get(position).getName());

    }

@SuppressLint("MissingPermission")
public void aggiornalista(BluetoothDevice device)
{
    int cont =0;
    for(int i=0; i<localDataSet.size(); i++) {
        if(Objects.equals(localDataSet.get(i).getName(), device.getName()))
        {
           cont++;
        }
    }
    if(cont==0)
    {
        localDataSet.add(device);
    }
}
    public void aggiornalista(ArrayList<BluetoothDevice> device)
    {
        localDataSet= device;
    }






    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
