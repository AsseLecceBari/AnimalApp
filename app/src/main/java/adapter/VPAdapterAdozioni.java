package adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import fragments_adozioni.info_Proprietario;
import fragments_adozioni.info_adozione;
import fragments_adozioni.riepilogo_adozione;

public class VPAdapterAdozioni extends FragmentStateAdapter {

    private final ArrayList<Fragment> fragmentArrayList= new ArrayList<>();
    public VPAdapterAdozioni(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, @NonNull boolean proprietario) {
        super(fragmentManager, lifecycle);

        if(!proprietario) {

            fragmentArrayList.add(new info_adozione());
            fragmentArrayList.add(new info_Proprietario());
            Log.d("ciao127", "size" + fragmentArrayList.size());
        }
        else{
            fragmentArrayList.add(new riepilogo_adozione());



        }
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return  fragmentArrayList.size();
    }
}
