package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import fragments.adoptions_fragment;
import fragments.myanimals_fragment;
import fragments.reports_fragment;
import it.uniba.dib.sms2223_2.VistaAdozioneActivity;

public class VPAdapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> fragmentArrayList= new ArrayList<>();

    public VPAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        fragmentArrayList.add(new myanimals_fragment());
        fragmentArrayList.add(new adoptions_fragment());
        fragmentArrayList.add(new reports_fragment());
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
