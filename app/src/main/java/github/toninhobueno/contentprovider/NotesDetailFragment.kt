package github.toninhobueno.contentprovider

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import github.toninhobueno.contentprovider.database.NotesDatabaseHelper.Companion.DESCRIPTION_NOTES
import github.toninhobueno.contentprovider.database.NotesDatabaseHelper.Companion.TITLE_NOTES
import github.toninhobueno.contentprovider.database.NotesProvider.Companion.URI_NOTES

class NotesDetailFragment : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var noteEditTitle: EditText
    private lateinit var noteEditDescription: EditText
    private var id : Long = 0


    companion object {
        private const val EXTRA_ID = "id"
        fun newInstance (id: Long) : NotesDetailFragment {
            val bundle = Bundle()
            bundle.putLong(EXTRA_ID, id)

            val notesFragment =  NotesDetailFragment()
            notesFragment.arguments = bundle
            return notesFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view : View? = activity?.layoutInflater?.inflate(R.layout.note_detail,null)

        noteEditDescription = view?.findViewById(R.id.note_edt_description) as EditText
        noteEditTitle = view.findViewById(R.id.note_edt_title) as EditText


        var newNote = true

        if(arguments !=null && arguments?.getLong(EXTRA_ID) != 0L){
            id = arguments?.getLong(EXTRA_ID) as Long
            val uri = Uri.withAppendedPath(URI_NOTES, id.toString())
            val cursor =
                activity?.contentResolver?.query(uri, null, null, null, null)

            if (cursor?.moveToNext() as Boolean){
                newNote = false
                noteEditDescription.setText(cursor.getString(cursor.getColumnIndex(DESCRIPTION_NOTES)))
                noteEditTitle.setText(cursor.getString(cursor.getColumnIndex(TITLE_NOTES)))
            }
            cursor.close()
        }
        return AlertDialog.Builder(activity as Activity)
            .setTitle(if (newNote) "Nova Mensagem" else "Editar mensagem")
            .setView(view)
            .setPositiveButton("Salvar", this)
            .setNegativeButton("Cancelar", this)
            .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        val values = ContentValues()
        values.put(TITLE_NOTES,noteEditTitle.text.toString())
        values.put(DESCRIPTION_NOTES,noteEditDescription.text.toString())

        if (id != 0L){
            val uri = Uri.withAppendedPath(URI_NOTES,id.toString())
            context?.contentResolver?.update(uri, values, null, null)
        } else{
            context?.contentResolver?.insert(URI_NOTES,values)
        }

    }


}