package adapter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import fragments.anagrafica;
import fragments.librettoSanitario;
import fragments.spese;
import fragments_adozioni.info_Proprietario;
import fragments_adozioni.info_animale;

public class VPAdapterAnimale extends FragmentStateAdapter {
    private final ArrayList<Fragment> fragmentArrayList= new ArrayList<>();

    public VPAdapterAnimale(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, @NonNull boolean proprietario) {
        super(fragmentManager, lifecycle);

        if(!proprietario) {

            fragmentArrayList.add(new info_animale());
            fragmentArrayList.add(new info_Proprietario());


            Log.d("ciao","sono dentro");

        }
        else{
            Log.d("ciao","sono dentroi");

            fragmentArrayList.add(new anagrafica());
            fragmentArrayList.add(new spese());
            fragmentArrayList.add(new librettoSanitario());

        }
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentArrayList.size();
    }
}
