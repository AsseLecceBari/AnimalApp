package class_general;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms2223_2.R;

public class fab {
    private FloatingActionButton fab;
    private FloatingActionButton fabAction1;
    private FloatingActionButton fabAction2;
    private FloatingActionButton fabActionAnnullaModifica;
    private FloatingActionButton fabActionSalvaModifiche;
    private FloatingActionButton fabAction5;
    private FloatingActionButton fabAction3;

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
        fabAction5 = rootView.findViewById(R.id.fab_preferiti);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina


        fabAction5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Aggiungi ai preferiti", Toast.LENGTH_SHORT).show();
            }
        });
    }


        public void aggiungiFabElimina(View rootView, Context con, String collectiondb, String idDocumento,Activity activity) {
            context= con;
            fabAction2 = rootView.findViewById(R.id.fab_elimina);
            //if per cambiare icona fab, se viene da radioTutti(x=0) ho il contatta, mentre da mie segnalazioni(x=1) ho il modifica

                fabAction2.setVisibility(View.VISIBLE);

                fabAction2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                       ConfermaEliminaAlertDialog(activity,collectiondb,idDocumento);

                    }
                });
        }


    public void aggiungiFabtelefono(View rootView,  Context con, String numeroTelefonico) {
        context= con;
        fabAction5 = rootView.findViewById(R.id.fab_telefono);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina

            fabAction5.setVisibility(View.VISIBLE);

            fabAction5.setOnClickListener(new View.OnClickListener() {
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
    public void aggiungiFabAnnullaModifica(View rootView, Context con, String adozioni, String idAdozione, TextInputLayout editModifica, TextView testo, TextInputEditText nuovoTesto) {
        context= con;
        fabActionAnnullaModifica = rootView.findViewById(R.id.fab_annulla_modifica);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina

       fabActionAnnullaModifica.setVisibility(View.GONE);

        fabActionAnnullaModifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

          LayoutModificaDescrizione(editModifica,testo,nuovoTesto);
          setFabInvisibile();
          setFabModificaVisibile();
      }
  });

    }
    public void aggiungiFabSalvaModifiche(View rootView, Context con, String collectiondb, String idDocumento,TextInputLayout editModifica, TextView testo, TextInputEditText nuovoTesto) {
        context= con;
        fabActionSalvaModifiche = rootView.findViewById(R.id.fab_fine_modifica);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina

        fabActionSalvaModifiche.setVisibility(View.GONE);

        fabActionSalvaModifiche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore db;
                db= FirebaseFirestore.getInstance();
                DocumentReference washingtonRef = db.collection(collectiondb).document(idDocumento);


                washingtonRef

                        .update("descrizione",nuovoTesto.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                 setFabvisibile();
                                 setFabModificaInVisibile();
                                 LayoutLeggiDescrizione(editModifica,testo,nuovoTesto);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


            }
        });

    }




    private void expandFab() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createExpandAnimator(fabAction1, offset1),
                createExpandAnimator(fabAction2, offset2),createExpandAnimator(fabActionAnnullaModifica, offset3))
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
                createCollapseAnimator(fabAction2, offset2),createCollapseAnimator(fabAction3, offset5));
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
                        if(fabAction2 != null) {
                            offset2 = fab.getY() - fabAction2.getY();
                            fabAction2.setTranslationY(offset2);
                        }
                        if(fabAction3 != null) {
                            offset3 = fab.getY() - fabAction3.getY();
                            fabAction3.setTranslationY(offset3);
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
        if(fabAction2 != null) {
  fabAction2.setVisibility(View.GONE);
        }
        if(fabActionAnnullaModifica != null) {
      fabActionAnnullaModifica.setVisibility(View.GONE);
        }
        if(fabActionSalvaModifiche!= null) {
    fabActionSalvaModifiche.setVisibility(View.GONE);
        }
        if(fabAction5!= null) {
     fabAction5.setVisibility(View.GONE);
        }





    }
    public void setFabvisibile() {

        if (fabAction1 != null) {
            fabAction1.setVisibility(View.VISIBLE);
        }
        if (fabAction2 != null) {
            fabAction2.setVisibility(View.VISIBLE);
        }
        if (fabActionAnnullaModifica != null) {
            fabActionAnnullaModifica.setVisibility(View.VISIBLE);
        }
        if (fabActionSalvaModifiche != null) {
            fabActionSalvaModifiche.setVisibility(View.VISIBLE);
        }
        if (fabAction5 != null) {
            fabAction5.setVisibility(View.VISIBLE);
        }

        expandFab();
    }

    public void setFabModificaVisibile()
    {
        fabActionAnnullaModifica.setVisibility(View.VISIBLE);
        fabActionSalvaModifiche.setVisibility(View.VISIBLE);
    }
    public void setFabModificaInVisibile()
    {
        fabActionAnnullaModifica.setVisibility(View.GONE);
        fabActionSalvaModifiche.setVisibility(View.GONE);
    }


    public void LayoutModificaDescrizione(TextInputLayout editModifica, TextView testo, TextInputEditText nuovoTesto)
    {
        editModifica.setVisibility(View.VISIBLE);
        testo.setVisibility(View.GONE);
        nuovoTesto.setText(testo.getText());

    }

    public void LayoutLeggiDescrizione(TextInputLayout editModifica, TextView testo, TextInputEditText nuovoTesto)
    {
        editModifica.setVisibility(View.GONE);
        testo.setText(nuovoTesto.getText().toString());
        testo.setVisibility(View.VISIBLE);


    }

    public void ConfermaEliminaAlertDialog(Activity activity,String collectiondb,String idDocumento)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.ConfermaEliminaAnnuncio)
                .setTitle("Elimina Annuncio")
                .setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseFirestore db;
                        db = FirebaseFirestore.getInstance();
                        db.collection(collectiondb).document(idDocumento)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {

                                    @Override
                                    public void onSuccess(Void unused) {

                                        activity.onBackPressed();

                                    }
                                });
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                builder.setCancelable(true);
                            }
                        });




        builder.show();
    }











}
