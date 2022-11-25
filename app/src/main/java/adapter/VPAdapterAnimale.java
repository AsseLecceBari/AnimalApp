package adapter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import fragments.adoptions_fragment;
import fragments.anagrafica;
import fragments.librettoSanitario;
import fragments.myanimals_fragment;
import fragments.reports_fragment;
import fragments.spese;

public class VPAdapterAnimale extends FragmentStateAdapter {
    private final ArrayList<Fragment> fragmentArrayList= new ArrayList<>();

    public VPAdapterAnimale(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, @NonNull boolean proprietario) {
        super(fragmentManager, lifecycle);

        if(!proprietario) {

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