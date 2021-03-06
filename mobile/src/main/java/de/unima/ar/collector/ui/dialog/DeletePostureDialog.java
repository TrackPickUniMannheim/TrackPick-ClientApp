package de.unima.ar.collector.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.unima.ar.collector.MainActivity;
import de.unima.ar.collector.R;
import de.unima.ar.collector.controller.SQLDBController;
import de.unima.ar.collector.shared.database.SQLTableName;


/**
 * @author Fabian Kramm
 */
public class DeletePostureDialog extends DialogFragment
{
    static class Container
    {
        int    id;
        String name;
    }

    private MainActivity ma;


    public void setMainActivity(MainActivity ma)
    {
        this.ma = ma;
    }


    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Delete Posture");

        final View view = inflater.inflate(R.layout.addactivitydialog, null);

        view.findViewById(R.id.addactivity_spinner2).setVisibility(View.GONE);
        view.findViewById(R.id.textView2).setVisibility(View.GONE);

        ((TextView) view.findViewById(R.id.textView2)).setText("Posture: ");

        Spinner sp1 = (Spinner) view.findViewById(R.id.addactivity_spinner1);

        String queryString = "SELECT name, id FROM " + SQLTableName.POSTURES;
        List<String[]> result = SQLDBController.getInstance().query(queryString, null, false);

        final ArrayList<Container> postures = new ArrayList<Container>();
        ArrayList<String> sPostures = new ArrayList<String>();

        for(String[] row : result) {
            Container co = new Container();

            co.name = row[0];
            co.id = Integer.valueOf(row[1]);

            postures.add(co);
            sPostures.add(co.name);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        final ArrayAdapter subacadapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, sPostures);
        sp1.setAdapter(subacadapter);

        final Context context = this.getActivity();

        builder.setView(view);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                Spinner sp = (Spinner) view.findViewById(R.id.addactivity_spinner1);

                if(sp.getSelectedItemId() < 0) {
                    return;
                }

                int aId = postures.get((int) sp.getSelectedItemId()).id;
                SQLDBController.getInstance().delete(SQLTableName.POSTUREDATA, "pid = ?", new String[]{ "" + aId });
                SQLDBController.getInstance().delete(SQLTableName.POSTURES, "id = ?", new String[]{ "" + aId });  // Delete the activity

                Toast.makeText(context, "Posture " + postures.get((int) sp.getSelectedItemId()).name + " successfull deleted!", Toast.LENGTH_LONG).show();

                ma.showActivities();
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {

            }
        });

        return builder.create();
    }
}