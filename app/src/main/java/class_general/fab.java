package class_general;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import it.uniba.dib.sms2223_2.R;

public class fab {
    private FloatingActionButton fab;
    private FloatingActionButton fabAction1;
    private FloatingActionButton fabAction2;
    private FloatingActionButton fabAction3;
    private FloatingActionButton fabAction4;
    private FloatingActionButton fabAction5;
    private FloatingActionButton fabAction6;

    private FloatingActionButton fabFineModifica;



    //FAB SPEED DIAL DECLARATION
    private static final String TRANSLATION_Y = "translationY";

    private boolean expanded = false;

    private float offset1;
    private float offset2;
    private float offset3;
    private float offset4;
    private float offset5;
    private float offset6;
    private float offset7;

    Context context;
    ViewGroup fabContainer;

    public fab()
    {


    }

    public void iniziallizazioneFab(View rootView)
    {
      fabContainer = rootView.findViewById(R.id.fab_container);
        fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expanded = !expanded;
                if (expanded) {
                    expandFab();
                } else {
                    collapseFab();
                }
            }
        });

    }

    public void AggiungiFabPreferiti(View rootView, Context con) {
        /**
         * FAB INIZIALIZZAZIONI
         * ViewGroup serve per prendere il riferimento al layout dei FAB
         */
        context = con;
        fabAction1 = rootView.findViewById(R.id.fab_preferiti);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina


        fabAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Aggiungi ai preferiti", Toast.LENGTH_SHORT).show();
            }
        });
    }


        public void aggiungiFabElimina(View rootView, Context con) {
            context= con;
            fabAction3 = rootView.findViewById(R.id.fab_elimina);
            //if per cambiare icona fab, se viene da radioTutti(x=0) ho il contatta, mentre da mie segnalazioni(x=1) ho il modifica

                fabAction3.setVisibility(View.VISIBLE);

                fabAction3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //INTENT PER IL CONTATTA
                        // trovaNumeroDiTelefono(numeroTelefono);
                    }
                });

        }

    public void aggiungiFabtelefono(View rootView,  Context con, String numeroTelefonico) {
        context= con;
        fabAction3 = rootView.findViewById(R.id.fab_telefono);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina

            fabAction3.setVisibility(View.VISIBLE);

            fabAction3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //composeEmail(auth.getCurrentUser().getEmail(), email);
                }
            });

    }
    public void aggiungiFabMail(View rootView,  Context con, String indirizzoEmail) {
        context= con;
        fabAction5 = rootView.findViewById(R.id.fab_mail);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina

        fabAction5.setVisibility(View.VISIBLE);

        fabAction5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //composeEmail(auth.getCurrentUser().getEmail(), email);
            }
        });

    }
    public void aggiungiFabAnnulla(View rootView,  Context con) {
        context= con;
        fabAction2 = rootView.findViewById(R.id.fab_annulla_modifica);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina

       fabAction2.setVisibility(View.VISIBLE);

        fabAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //composeEmail(auth.getCurrentUser().getEmail(), email);
            }
        });

    }
    public void aggiungiFabModifica(View rootView, Context con, TextInputLayout editModifica, TextView testo, TextInputEditText nuovoTesto) {
        context= con;
        fabAction1 = rootView.findViewById(R.id.fab_modifica);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina

        fabAction1.setVisibility(View.VISIBLE);

        fabAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   fab.setVisibility(View.GONE);

                editModifica.setVisibility(View.VISIBLE);
                testo.setVisibility(View.GONE);
                nuovoTesto.setText(testo.getText());
                setFabInvisibile();
                fabAction2.setVisibility(View.VISIBLE);
                expandFab();







            }
        });

    }




    private void expandFab() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createExpandAnimator(fabAction1, offset1),
                createExpandAnimator(fabAction2, offset2),createExpandAnimator(fabAction3, offset3),createExpandAnimator(fabAction4, offset4),createExpandAnimator(fabAction5, offset5),createExpandAnimator(fabAction6, offset6))
        ;
        animatorSet.start();
        //animateFab();
    }
    private Animator createExpandAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0)
                .setDuration(context.getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }


    private void collapseFab() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createCollapseAnimator(fabAction1, offset1),
                createCollapseAnimator(fabAction2, offset2),createCollapseAnimator(fabAction3, offset3),createExpandAnimator(fabAction4, offset4),createExpandAnimator(fabAction5, offset5),createExpandAnimator(fabAction6, offset6));
        animatorSet.start();
        // animateFab();
    }
    private Animator createCollapseAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
                .setDuration(context.getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }


    public void FabContainerListner()
    {
        fabContainer.getViewTreeObserver().

                addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        fabContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                        if(fabAction1!= null) {
                            offset1 = fab.getY() - fabAction1.getY();

                            fabAction1.setTranslationY(offset1);
                        }
                        if(fabAction2!= null) {
                            offset2 = fab.getY() - fabAction2.getY();
                            fabAction2.setTranslationY(offset2);
                        }
                        if(fabAction3!= null) {
                            offset3 = fab.getY() - fabAction3.getY();
                            fabAction3.setTranslationY(offset3);
                        }
                        if(fabAction4!= null) {
                            offset4 = fab.getY() - fabAction4.getY();
                            fabAction4.setTranslationY(offset4);
                        }
                        if(fabAction5!= null) {
                            offset5 = fab.getY() - fabAction5.getY();
                            fabAction5.setTranslationY(offset5);
                        }
                        if(fabAction6!= null) {
                            offset6 = fab.getY() - fabAction6.getY();
                            fabAction6.setTranslationY(offset6);
                        }


                        return true;
                    }
                });
    }


    public void setFabInvisibile()
    {    collapseFab();
        if(fabAction1!= null) {
  fabAction1.setVisibility(View.GONE);
        }
        if(fabAction2!= null) {
  fabAction2.setVisibility(View.GONE);
        }
        if(fabAction3!= null) {
      fabAction3.setVisibility(View.GONE);
        }
        if(fabAction4!= null) {
    fabAction4.setVisibility(View.GONE);
        }
        if(fabAction5!= null) {
     fabAction5.setVisibility(View.GONE);
        }

        expandFab();

    }











}
