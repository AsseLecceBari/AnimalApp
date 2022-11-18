package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import adapter.VPAdapter;
import adapter.VPAdapterAnimale;
import it.uniba.dib.sms2223_2.R;

public class main_fragment_animale extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    VPAdapterAnimale vpAdapter;
    FragmentActivity activity;
    private int posizione = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null){
            posizione = savedInstanceState.getInt("posizione"); //test
        }

        return inflater.inflate(R.layout.fragment_main_animale, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        tabLayout= getView().findViewById(R.id.tabLayout);
        viewPager2=getView().findViewById(R.id.viewPager);

        //Se l'adapter è stato già creato,viene catturato l'errore e non ne viene creato uno nuovo
        try {
            activity  = getActivity();
            vpAdapter= new VPAdapterAnimale(activity);
            viewPager2.setAdapter(vpAdapter);
        }catch (Exception e){
            return;
        }
        tabLayout.getTabAt(posizione).select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
                posizione = tab.getPosition();  //test
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("posizione", posizione);
    }

    public int getPosition(){

        return posizione;
    }

}