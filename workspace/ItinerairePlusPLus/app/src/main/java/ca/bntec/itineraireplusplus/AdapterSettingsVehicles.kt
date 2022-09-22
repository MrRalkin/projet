package ca.bntec.itineraireplusplus

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import classes.Vehicle

class AdapterSettingsVehicles(var mCtx: Context, var resource:Int, var items:ArrayList<Vehicle>)
    : ArrayAdapter<Vehicle>( mCtx , resource , items ){


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)

        val view : View = layoutInflater.inflate(resource , null )

        if (position % 2 == 0) {
            view.setBackgroundColor(Color.WHITE)
        } else {
            view.setBackgroundColor(Color.rgb(240, 240, 240))
        }
        val artiste = items[position]
//        val aId=view.findViewById<TextView>(R.id.art_id)
//        val aNom=view.findViewById<TextView>(R.id.art_nom)
//        val aPrenom=view.findViewById<TextView>(R.id.art_prenom)
//        val aAnnee=view.findViewById<TextView>(R.id.art_annee)

//        aId.text="ID: ${artiste.id.toString()}"
//        aNom.text="Nom: ${artiste.nom}"
//        aPrenom.text="Prenom: ${artiste.prenom}"
//        aAnnee.text="Annee: ${artiste.annee_naissance}"

        return view
    }

}