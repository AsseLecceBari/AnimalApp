package fragments_adozioni;

import android.os.Bundle;

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

import adapter.VPAdapterAdozioni;
import it.uniba.dib.sms2223_2.R;
import model.Animale;


public class mainFragmentVistaAdozioni extends Fragment {
    private TabLayout tabLayout;
    private int posizione = 0;
    private Animale animale;
    private ViewPager2 viewPager2;
    private TabItem spese;
    VPAdapterAdozioni vpAdapter;
    FragmentActivity activity;

    public mainFragmentVistaAdozioni() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("ciao127","ciao");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            posizione = savedInstanceState.getInt("posizione");

        animale= (Animale) getActivity().getIntent().getSerializableExtra("animale");
        View root=inflater.inflate(R.layout.fragment_main_adozioni, container, false);
        tabLayout= root.findViewById(R.id.tabLayout2);

        if(!getProprietario()) {

            vpAdapter = new VPAdapterAdozioni(getChildFragmentManager(), getLifecycle(),false);
            Log.d("ciao127","ciao3");

        }
        else {

            vpAdapter = new VPAdapterAdozioni(getChildFragmentManager(), getLifecycle(), true);
            tabLayout.setVisibility(View.GONE);

        }
        viewPager2=root.findViewById(R.id.viewPagerAdozioni);
        caricamentoTab();
        return root;



    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("ciao127", String.valueOf(this));

    }

    private void caricamentoTab()
    {
        Log.d("ciao127","ciao2");



        //Se l'adapter è stato già creato,viene catturato l'errore e non ne viene creato uno nuovo

            activity  = getActivity();

            viewPager2.setAdapter(vpAdapter);


        tabLayout.getTabAt(posizione).select();
        viewPager2.setCurrentItem(posizione);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
                posizione = tab.getPosition();
                Log.d("ciao127", String.valueOf(posizione));
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

    public boolean getProprietario(){
        FirebaseAuth auth;
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!= null) {
            return Objects.equals(auth.getCurrentUser().getEmail(), animale.getEmailProprietario());
        }
        else return false;
    }
    public int getPosition(){
        return posizione;
    }
}