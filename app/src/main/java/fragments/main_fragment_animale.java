package fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import adapter.VPAdapter;
import adapter.VPAdapterAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Animale;

public class main_fragment_animale extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabItem spese;
    VPAdapterAnimale vpAdapter;
    FragmentActivity activity;
    private int posizione = 0;
   private Animale animale;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            posizione = savedInstanceState.getInt("posizione");

        animale= (Animale) getActivity().getIntent().getSerializableExtra("animale");
        View root=inflater.inflate(R.layout.fragment_main_animale, container, false);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        caricamentoTab();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("posizione", posizione);
    }

    public int getPosition(){
        return posizione;
    }

    public boolean getProprietario(){
        FirebaseAuth auth= FirebaseAuth.getInstance();
        if(Objects.equals(auth.getCurrentUser().getEmail(), animale.getEmailProprietario())){
            Log.d("ciao",auth.getCurrentUser().getEmail());
            Log.d("ciao", animale.getEmailProprietario());
            return  true;
        }
        else
            return false;
    }

    private void caricamentoTab()
    {

        viewPager2=getView().findViewById(R.id.viewPager);

        //Se l'adapter è stato già creato,viene catturato l'errore e non ne viene creato uno nuovo
        try {
            activity  = getActivity();
            if(!getProprietario()) {
                vpAdapter = new VPAdapterAnimale(getChildFragmentManager(), getLifecycle(),false);
                tabLayout= getView().findViewById(R.id.tabLayout);
                tabLayout.setVisibility(View.GONE);
                tabLayout= getView().findViewById(R.id.tabLayout2);
            }
            else {
                vpAdapter = new VPAdapterAnimale(getChildFragmentManager(), getLifecycle(), true);
                tabLayout= getView().findViewById(R.id.tabLayout2);
                tabLayout.setVisibility(View.GONE);
                tabLayout = getView().findViewById(R.id.tabLayout);
            }
            viewPager2.setAdapter(vpAdapter);

        }catch (Exception e){
            return;
        }
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


}