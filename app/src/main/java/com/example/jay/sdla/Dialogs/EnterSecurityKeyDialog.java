package com.example.jay.sdla.Dialogs;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jay.sdla.R;

public class EnterSecurityKeyDialog extends DialogFragment{

    EditText securitykey, confirmSecurityKey;

    private Communicator comm;

    public EnterSecurityKeyDialog(){

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        comm = (Communicator) context;
    }

    public interface Communicator{
        public void passingKey(String key);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.setting_security_key, null);
        builder.setView(view);
        builder.setCancelable(false);
        securitykey = view.findViewById(R.id.password);
        confirmSecurityKey = view.findViewById(R.id.confirm_password);

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String newSecuritykey = securitykey.getText().toString();

                        String confirmKey = confirmSecurityKey.getText().toString();

                        if(newSecuritykey.isEmpty() || confirmKey.isEmpty()){
                            //Toast.makeText(getContext(), "Please fill all the fields for Security Key", Toast.LENGTH_LONG).show();
                            new ExceptionDialog("", "Please fill all the fields for Security Key").show(getFragmentManager(), "Dialog");
                        }else if(!newSecuritykey.equals(confirmKey)){
                            //Toast.makeText(getContext(), "Keys do not match", Toast.LENGTH_LONG).show();
                            new ExceptionDialog("", "Keys do not match").show(getFragmentManager(), "Dialog");
                        }else if(newSecuritykey.length() < 8){
                            //Toast.makeText(getContext(), "Key length must have at least 8 character", Toast.LENGTH_LONG).show();
                            new ExceptionDialog("", "Key length must have at least 8 character").show(getFragmentManager(), "Dialog");
                        }else{

                            if(comm != null){
                                comm.passingKey(newSecuritykey);
                                Toast.makeText(getContext(), "Key is successfully saved", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(), "Key is not successfully saved", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });

        builder.setNegativeButton(R.string.button_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        return builder.create();
    }
}
