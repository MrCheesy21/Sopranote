package com.example.mlove.sopranote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class TempoInputDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder TempoInputBuilder = new AlertDialog.Builder(getActivity());
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

                    }
                });
        LayoutInflater TempoInputInflater = getActivity().getLayoutInflater();
        View TempoInputView = TempoInputInflater.inflate(R.layout.tempo_input_dialog)
    }
}
