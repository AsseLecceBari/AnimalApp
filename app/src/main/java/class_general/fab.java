package class_general;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.uniba.dib.sms2223_2.R;

public class fab {
    private FloatingActionButton fab;
    private FloatingActionButton fabAction1;
    private FloatingActionButton fabAction2;
    private FloatingActionButton fabAction3;

    //FAB SPEED DIAL DECLARATION
    private static final String TRANSLATION_Y = "translationY";

    private boolean expanded = false;

    private float offset1;
    private float offset2;
    private float offset3;

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

    public void inizializzazioneFab1(View rootView, int visualizzazione, Context con) {
        /**
         * FAB INIZIALIZZAZIONI
         * ViewGroup serve per prendere il riferimento al layout dei FAB
         */
        context= con;
        fabAction1 = rootView.findViewById(R.id.fab_action_1);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina
        if (visualizzazione == 0) {
            fabAction1.setImageResource(R.drawable.star);
            fabAction1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Aggiungi ai preferiti", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (visualizzazione == 1) {
            fabAction1.setImageResource(android.R.drawable.ic_delete);
            fabAction1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Elimina Segnalazione", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
        public void inizializzazioneFab2(View rootView, int visualizzazione, Context con, String numerotelefono) {
            context= con;
            fabAction2 = rootView.findViewById(R.id.fab_action_2);
            //if per cambiare icona fab, se viene da radioTutti(x=0) ho il contatta, mentre da mie segnalazioni(x=1) ho il modifica
            if (visualizzazione == 0) {
                fabAction2.setVisibility(View.VISIBLE);
                fabAction2.setImageResource(android.R.drawable.stat_sys_phone_call);
                fabAction2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //INTENT PER IL CONTATTA
                        // trovaNumeroDiTelefono(numeroTelefono);
                    }
                });
            } else if (visualizzazione == 1) {
                fabAction2.setVisibility(View.VISIBLE);
                fabAction2.setImageResource(android.R.drawable.ic_menu_edit);
                fabAction2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(context, "Modifica Segnalazione", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    public void inizializzazioneFab3(View rootView, int visualizzazione, Context con, String email) {
        context= con;
        fabAction3 = rootView.findViewById(R.id.fab_action_3);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina
        if (visualizzazione == 0) {
            fabAction3.setVisibility(View.VISIBLE);
            fabAction3.setImageResource(android.R.drawable.sym_action_email);
            fabAction3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //composeEmail(auth.getCurrentUser().getEmail(), email);
                }
            });
        } else if (visualizzazione == 1) {
            //per ora non serve nella vista mieSegnalazioni
        }
    }


    private void expandFab() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createExpandAnimator(fabAction1, offset1),
                createExpandAnimator(fabAction2, offset2),createExpandAnimator(fabAction3, offset3))
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
                createCollapseAnimator(fabAction2, offset2),createCollapseAnimator(fabAction3, offset3));
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

                        return true;
                    }
                });
    }











}
