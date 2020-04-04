package com.darpan.firestoreexample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.document("Notebook/My First Note");

    //  private ListenerRegistration noteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        textViewData = findViewById(R.id.text_view_data);
    }

    public void saveNote(View v) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        Map<String, Object> note = new HashMap<>();
        note.put(KEY_TITLE, title);
        note.put(KEY_DESCRIPTION, description);

      /*  db.collection("Notebook").document("My First Note").set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });*/

        // this is also can be written as

        noteRef.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public void loadNote(View v) {
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                          /* We ca also use this --->!
                           String title = documentSnapshot.getString(KEY_TITLE);
                            String description = documentSnapshot.getString(KEY_DESCRIPTION);*/


                            Note note = documentSnapshot.toObject(Note.class);//changing the snapshot in out note object to ise getter()

                            String title = note.getTitle();
                            String description = note.getDescription();


                            //Map<String, Object> note = documentSnapshot.getData();

                            textViewData.setText("Title: " + title + "\n" + "Description: " + description);
                        } else {
                            Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //we use only listener i only in start activity as this reudce the bW as the
        //the listener is attached

        //saving the listener in reference variable
        // noteListener=noteRef.addSnapshotListener( this,new EventListener<DocumentSnapshot>()
        // for the sake of simplicity we use this without assingning the listenerRegistration
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null) {//something went wrong
                    Toast.makeText(MainActivity.this, "Error while loading!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                    return;//it important to leave this method is there is exception otherwise
                    //documentSnapshot.exists will return a null pointer exception
                }

                if (documentSnapshot.exists()) {
                  /*  String title = documentSnapshot.getString(KEY_TITLE);
                    String description = documentSnapshot.getString(KEY_DESCRIPTION);
*/

                    Note note = documentSnapshot.toObject(Note.class);//changing the snapshot in out note object to ise getter()

                    String title = note.getTitle();
                    String description = note.getDescription();

                    textViewData.setText("Title: " + title + "\n" + "Description: " + description);
                } else {
                    /*even after deleting the note it might hppen that our textViewData show the
                     *  previous value so if document not exist
                     * we need to textViewData to set null*/
                    textViewData.setText("");
                }
            }
        });
        /*we have attached this listener now we also want to remove the listener we do
         * it by providing the the reference to the listener*/
    }
/*
    @Override
    protected void onStop() {
        super.onStop();
        noteListener.remove();// this would remove this callback in stop method addSnapshotListener(this,new (...))
        //the listener can also be removed by providing the this context in
    }
    */

    public void updateDescription(View v) {
        String description = editTextDescription.getText().toString();

        //Map<String, Object> note = new HashMap<>();
        //note.put(KEY_DESCRIPTION, description);

        //noteRef.set(note, SetOptions.merge());//this will override the description only and if not exist then it will create docs withe onyl description

        noteRef.update(KEY_DESCRIPTION, description);
        // if the doc does not exist then it will do nothing it will not create new doc
        //this update method is only to update the existing one by only providing the keys and value it will update the
        //value of that unique key as given specified
        //this the only difference in SetOptions.merge and update(key , value)
    }

    public void deleteDescription(View v) {
        //Map<String, Object> note = new HashMap<>();
        //note.put(KEY_DESCRIPTION, FieldValue.delete());

        //noteRef.update(note);

        noteRef.update(KEY_DESCRIPTION, FieldValue.delete());/*we can also onsucces and OnFailure Listner to it*/
    }

    public void deleteNote(View v) {
        //if we delete the all doc in collection then collection will also be deleted
        noteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Can not delete Note " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void SaveNoteUsingModel(View view) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        Note note = new Note(title, description);

        noteRef.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }
}
