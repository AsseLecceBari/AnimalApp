package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import fragments_adozioni.adoptions_fragment;
import fragments_mieiAnimali.myanimals_fragment;
import fragments_segnalazioni.reports_fragment;

public class VPAdapter extends FragmentStateAdapter {
    public ArrayList<Fragment> getFragmentArrayList() {
        return fragmentArrayList;
    }
    private final static myanimals_fragment myanimals_fragment= new myanimals_fragment();
    private final static adoptions_fragment adoptions_fragmentt= new adoptions_fragment();
    private final static reports_fragment reports_fragment= new reports_fragment();
    private final ArrayList<Fragment> fragmentArrayList= new ArrayList<>();

    public VPAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        fragmentArrayList.add(myanimals_fragment);
        fragmentArrayList.add(adoptions_fragmentt);
        fragmentArrayList.add(reports_fragment);
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
