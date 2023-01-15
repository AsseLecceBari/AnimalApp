package fragments_segnalazioni;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.dib.sms2223_2.R;

public class aggiungi_segnalazione_fragment extends Fragment {

    ImageButton smarrimento,animaleFerito,pericolo,news,raccoltaFondi,ritrovamento;
    private TextView raccolta, smar;
    private Toolbar main_action_bar;
    private FirebaseAuth auth;

    public aggiungi_segnalazione_fragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        auth= FirebaseAuth.getInstance();
        View rootView =inflater.inflate(R.layout.fragment_aggiungi_segnalazione_fragment, container, false);
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle(R.string.aggiungi_segnalazione);
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,false);
            main_action_bar.getMenu().clear();
            main_action_bar.inflateMenu(R.menu.menu_bar_img_profilo);
            main_action_bar.setNavigationIcon(R.drawable.back);
            main_action_bar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getActivity().onBackPressed();
                }
            });
        }



        smarrimento=rootView.findViewById(R.id.smarrimento);
        animaleFerito=rootView.findViewById(R.id.animaleFerito);
        pericolo=rootView.findViewById(R.id.pericolo);
        news=rootView.findViewById(R.id.news);
        raccoltaFondi=rootView.findViewById(R.id.raccoltaFondi);
        ritrovamento=rootView.findViewById(R.id.ritrovamento);
        raccolta = rootView.findViewById(R.id.textRaccoltaFondi);
        smar = rootView.findViewById(R.id.textSmarrimento);




        // se non è loggato non puo fare due segnalazioni
        if(auth.getCurrentUser() == null){
            raccolta.setVisibility(View.GONE);
            raccoltaFondi.setVisibility(View.GONE);
            smarrimento.setVisibility(View.GONE);
            smar.setVisibility(View.GONE);
        }








        //todo: quando si segna uno smarrimento in una determinata area si notifica ai residenti della zona iscritti l'accaduto
        smarrimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getParentFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right).replace(R.id.fragmentContainerView,new choiceAnimals_fragment()).addToBackStack(null).commit();



            }
        });

        animaleFerito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right).replace(R.id.fragmentContainerView,new animale_in_pericolo_fragments()).addToBackStack(null).commit();

            }
        });

        pericolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right).replace(R.id.fragmentContainerView,new fragment_zona_pericolosa()).addToBackStack(null).commit();

            }
        });
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right).replace(R.id.fragmentContainerView,new fragment_news()).addToBackStack(null).commit();


            }
        });

        //da capire bene come fare
        raccoltaFondi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right).replace(R.id.fragmentContainerView,new choiceAnimals_fragment(1)).addToBackStack(null).commit();
                }
        });


        ritrovamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                L'utente entra nella schermata compilativa del ritrovamento; in alto ci sarà un avviso con un link rapido che suggerisce di andara prima a dare un occhiata agli smarrimenti in quella zona

                 */

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right).replace(R.id.fragmentContainerView,new ritrovamento()).addToBackStack(null).commit();

            }
        });






        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.imgProfiloItem);
            main_action_bar.setNavigationIcon(null);
            main_action_bar.setTitle("AnimalApp");
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,true);

        }
    }
}

