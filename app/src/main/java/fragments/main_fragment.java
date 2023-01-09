package fragments;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import class_general.GeolocationClass;
import fragments_segnalazioni.vistaSegnalazione;
import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;

public class main_fragment extends Fragment {

    private TabLayout tabLayout;

    public ViewPager2 getViewPager2() {
        return viewPager2;
    }

    private  ViewPager2 viewPager2;

    public VPAdapter getVpAdapter() {
        return vpAdapter;
    }

    private  VPAdapter vpAdapter;
    private FragmentActivity  activity;
    private int posizione =0;
    private int posizionePassata;
    double lat,lng;
    private Toolbar main_action_bar;

    public main_fragment(int a){
        this.posizione=a;
    }

    public main_fragment(){

    }



    public Fragment newInstance(int posizione) {
        main_fragment fragment = new main_fragment();
       fragment.posizionePassata=posizione;

        return fragment;


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            posizione = savedInstanceState.getInt("posizione");
        View view= inflater.inflate(R.layout.fragment_main_fragment, container, false);
        // serve per francesco quando aggiunge segnalazione
        int pos = getActivity().getIntent().getIntExtra("posizione", 0);
        if(pos != 0){
            posizione = pos;
        }

        tabLayout= view.findViewById(R.id.tabLayout);
        viewPager2=view.findViewById(R.id.viewPager);

        //Se l'adapter è stato già creato,viene catturato l'errore e non ne viene creato uno nuovo
        activity  = getActivity();
        vpAdapter= new VPAdapter(getChildFragmentManager(),getLifecycle());
        viewPager2.setAdapter(vpAdapter);
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.getMenu().removeGroup(R.id.imgProfiloItem);
        main_action_bar.setNavigationIcon(null);
        main_action_bar.setTitle("AnimalApp");
        main_action_bar.inflateMenu(R.menu.menu_bar_main);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();




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
                if(position<=2){
                tabLayout.getTabAt(position).select();}else{
                    tabLayout.getTabAt(0).select();
                }

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

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }
}