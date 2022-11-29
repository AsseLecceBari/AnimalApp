package fragments;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import adapter.VPAdapter;
import it.uniba.dib.sms2223_2.R;

public class main_fragment extends Fragment {

    private TabLayout tabLayout;

    public ViewPager2 getViewPager2() {
        return viewPager2;
    }

    private ViewPager2 viewPager2;
    private VPAdapter vpAdapter;
    private FragmentActivity  activity;
    private int posizione = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            posizione = savedInstanceState.getInt("posizione");
        return inflater.inflate(R.layout.fragment_main_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        tabLayout= getView().findViewById(R.id.tabLayout);
        viewPager2=getView().findViewById(R.id.viewPager);

        //Se l'adapter è stato già creato,viene catturato l'errore e non ne viene creato uno nuovo

            activity  = getActivity();
            vpAdapter= new VPAdapter(getChildFragmentManager(),getLifecycle());
            viewPager2.setAdapter(vpAdapter);

        tabLayout.getTabAt(posizione).select();
        viewPager2.setCurrentItem(posizione);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
                posizione = tab.getPosition();
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
        // Salvo in che tab mi trovo quando esco
        outState.putInt("posizione", posizione);
    }

    public int getPosition() {
        return posizione;
    }
}