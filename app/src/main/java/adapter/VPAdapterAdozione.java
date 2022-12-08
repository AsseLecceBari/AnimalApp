package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import fragments.anagrafica;
import fragments.librettoSanitario;

public class VPAdapterAdozione extends FragmentStateAdapter{
    private final ArrayList<Fragment> fragmentArrayList= new ArrayList<>();


    public VPAdapterAdozione(@NonNull FragmentActivity activity) {
        super(activity);
        fragmentArrayList.add(new anagrafica());
        fragmentArrayList.add(new librettoSanitario());
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
