package com.example.mlove.sopranote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class TempoInputDialog extends AppCompatDialogFragment {
    private EditText tempo;
    private TempoInputListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder TempoInputBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater TempoInputInflater = getActivity().getLayoutInflater();
        View TempoInputView = TempoInputInflater.inflate(R.layout.tempo_input_dialog, null);
        TempoInputBuilder.setView(TempoInputView);
        tempo = TempoInputView.findViewById(R.id.tempo_input_dialog);

        TempoInputBuilder.setTitle("Tempo Input")
                .setMessage("Please enter a tempo")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tempoString = tempo.getText().toString();
                        listener.setTempo(tempoString);
                    }
                });


        return TempoInputBuilder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (TempoInputListener) context;
    }

    public interface TempoInputListener {
        void setTempo(String tempo);
    }
}