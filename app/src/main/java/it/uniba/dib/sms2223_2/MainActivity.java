package it.uniba.dib.sms2223_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import adapter.AnimalAdapter;
import adapter.VPAdapter;
import fragments.main_fragment;
import fragments_adozioni.adoptions_fragment;
import fragments_mieiAnimali.myanimals_fragment;
import fragments_segnalazioni.reports_fragment;
import profiloUtente.ProfiloUtenteActivity;

public class MainActivity extends AppCompatActivity {

    public Toolbar getMain_action_bar() {
        return main_action_bar;
    }

    private Toolbar main_action_bar;
    private FirebaseAuth auth;
    private int posizione;
    private TabLayout tabLayout;
    private main_fragment main_fragment;

    private MenuItem searchItem;
    private MenuItem pokedex;

    public SearchView getSearchView() {
        return searchView;
    }

    private SearchView searchView;
    private myanimals_fragment myanimals_fragment;
    private adoptions_fragment adoptions_fragment;
    private reports_fragment reports_fragment;
    private VPAdapter adapter=null;
    private ViewPager2 viewPager2=null;
    private static final int PERMISSION_REQUEST_CODE = 101;

    private void change() {
        startActivity(new Intent(getApplicationContext(),Pokedex.class));
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Imposto l'actionBar di questa activity
        main_action_bar=findViewById(R.id.main_action_bar);
        main_action_bar.setNavigationIcon(null);
        setSupportActionBar(main_action_bar);
    }

    @Override
    protected void onResume() {

        super.onResume();
        getFragmentTagReference();


    }





    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_main, menu);
        try {
            getMainFragmentReference();
            searchItem= menu.findItem(R.id.action_search);
            searchView= (SearchView) searchItem.getActionView();
            searchView.setQueryHint("Scrivi qui cosa vuoi cercare");
            pokedex=menu.findItem(R.id.pokedex);
            searchFilterListener();
            pokedex.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    startActivity(new Intent(getApplicationContext(),Pokedex.class));
                    return false;
                }
            });
        }catch (Exception e){

        }
       return true;
    }

    public main_fragment getMainFragmentReference() {
        main_fragment= (fragments.main_fragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        return main_fragment;
    }

    private void searchFilterListener() {


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*
                Log.e("dioaiutaci","CIAO");
                try {

                    myanimals_fragment= (fragments_mieiAnimali.myanimals_fragment) adapter.getFragmentArrayList().get(viewPager2.getCurrentItem());
                    myanimals_fragment.filter(query);
                    Log.e("query","animals");
                }catch (Exception e){

                }
                try {

                    adoptions_fragment= (fragments_adozioni.adoptions_fragment) adapter.getFragmentArrayList().get(viewPager2.getCurrentItem());
                    adoptions_fragment.filter(query);
                    Log.e("query","adoptions");
                }catch (Exception e){

                }
                try {

                    reports_fragment= (fragments_segnalazioni.reports_fragment) adapter.getFragmentArrayList().get(viewPager2.getCurrentItem());
                    reports_fragment.filter(query);
                    Log.e("query","reports");
                }catch (Exception e){

                }

                 */
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                try {
                    getMainFragmentReference();
                    viewPager2 = main_fragment.getViewPager2();
                    adapter = (VPAdapter) viewPager2.getAdapter();
                }catch (Exception e){

                }
                try {

                    Log.e("DOVESEI","SONOQUI");
resetSelectionCheckBox();
                    myanimals_fragment = (fragments_mieiAnimali.myanimals_fragment) adapter.getFragmentArrayList().get(viewPager2.getCurrentItem());
                    myanimals_fragment.filter(newText);
                    Log.e("query", "animals");
                } catch (Exception e) {

                }
                try {
                    resetSelectionCheckBox();
                    adoptions_fragment = (fragments_adozioni.adoptions_fragment) adapter.getFragmentArrayList().get(viewPager2.getCurrentItem());
                    adoptions_fragment.filter(newText);
                    Log.e("query", "adoptions");
                } catch (Exception e) {

                }
                try {
                    resetSelectionCheckBox();
                    reports_fragment = (fragments_segnalazioni.reports_fragment) adapter.getFragmentArrayList().get(viewPager2.getCurrentItem());
                    reports_fragment.filter(newText);
                    Log.e("query", "reports");
                } catch (Exception e) {

                }
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                try {

                    Log.e("chiuso","per ferie");

                }catch (Exception e) {

                }
                return false;
            }

        });
    }
public void getFragmentTagReference(){
    try {
        getMainFragmentReference();
        viewPager2 = main_fragment.getViewPager2();
        adapter = (VPAdapter) viewPager2.getAdapter();
    }catch (Exception e){

    }
    try {

        Log.e("DOVESEI","SONOQUI");

        myanimals_fragment = (fragments_mieiAnimali.myanimals_fragment) adapter.getFragmentArrayList().get(viewPager2.getCurrentItem());

        Log.e("query", "animals");
    } catch (Exception e) {

    }
    try {
        adoptions_fragment = (fragments_adozioni.adoptions_fragment) adapter.getFragmentArrayList().get(viewPager2.getCurrentItem());

        Log.e("query", "adoptions");
    } catch (Exception e) {

    }
    try {
        reports_fragment = (fragments_segnalazioni.reports_fragment) adapter.getFragmentArrayList().get(viewPager2.getCurrentItem());

        Log.e("query", "reports");
    } catch (Exception e) {

    }
}
    public void mostraProfilo(MenuItem item) {
        auth= FirebaseAuth.getInstance();

        // Parte l'activity mostraProfilo solo se si è loggati
        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), ProfiloUtenteActivity.class));
        }else{
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (resetSelectionCheckBox()){ return;}

        try {
            getMainFragmentReference();
        }catch (Exception e){
            super.onBackPressed();
            return;
        }


        // Quando clicchiamo back se posizione = 0 usciamo dall'applicazione, se no torniamo in i miei animali
        if(main_fragment!=null){
            tabLayout= findViewById(R.id.tabLayout);
            posizione=main_fragment.getPosition();
            switch (posizione) {
                case (0):
                    super.onBackPressed();
                    break;
                default:
                    tabLayout.getTabAt(0).select();
                    posizione=0;
                    break;
            }
        }
    }

    private boolean resetSelectionCheckBox() {
        AnimalAdapter animalAdapter= myanimals_fragment.getmAdapter();
        if(animalAdapter.isFlagCheckBox()){
            animalAdapter.setFlagCheckBox(false);
            animalAdapter.notifyDataSetChanged();
            RecyclerView mRecyclerView=myanimals_fragment.getmRecyclerView();
            myanimals_fragment.addRecycleListener(mRecyclerView);
            Log.e("recycle",mRecyclerView+"");

            return true;
        }
        return false;
    }


    public fragments_segnalazioni.reports_fragment getReports_fragment() {
        return reports_fragment;
    }

    public void scanQr(MenuItem item) {
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new scanAnimale()).commit();
    }




}