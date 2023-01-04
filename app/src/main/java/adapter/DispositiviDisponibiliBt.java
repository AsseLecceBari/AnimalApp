package adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
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

public class DispositiviDisponibiliBt extends RecyclerView.Adapter<DispositiviDisponibiliBt.ViewHolder>{
    //Array con tutti i dati sugli animali da inserire nella view
    private ArrayList<BluetoothDevice> localDataSet;


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
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_dispositivononassociato, parent, false);

        return new ViewHolder(v);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.getNome().setText(localDataSet.get(position).getName());

    }







    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
