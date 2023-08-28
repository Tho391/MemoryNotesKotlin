package com.devtides.memorynotes.presentation


import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.devtides.core.data.Note
import com.devtides.memorynotes.R
import com.devtides.memorynotes.databinding.FragmentNoteBinding
import com.devtides.memorynotes.framework.NoteViewModel

class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding
    private var noteId = 0L
    private lateinit var viewModel: NoteViewModel
    private var currentNote = Note("", "", 0L, 0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this)[NoteViewModel::class.java]

        arguments?.let {
            noteId = NoteFragmentArgs.fromBundle(it).noteId
        }

        if(noteId != 0L) {
            viewModel.getNote(noteId)
        }

        binding.checkButton.setOnClickListener {
            if(binding.titleView.text.toString() != "" || binding.contentView.text.toString() != "") {
                val time = System.currentTimeMillis()
                currentNote.title = binding.titleView.text.toString()
                currentNote.content = binding.contentView.text.toString()
                currentNote.updateTime = time
                if(currentNote.id == 0L) {
                    currentNote.creationTime = time
                }
                viewModel.saveNote(currentNote)
            } else {
                findNavController().popBackStack()
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.saved.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show()
                hideKeyboard()
                findNavController().popBackStack()
            } else {
                Toast.makeText(
                    context,
                    "Something went wrong, please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.currentNote.observe(viewLifecycleOwner) { note ->
            note?.let {
                currentNote = it
                binding.titleView.setText(it.title, TextView.BufferType.EDITABLE)
                binding.contentView.setText(it.content, TextView.BufferType.EDITABLE)
            }
        }
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.titleView.windowToken, 0)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Suppress("OverrideDeprecatedMigration", "OverrideDeprecatedMigration")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.deleteNote -> {
                if(context != null && noteId != 0L) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete note")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Yes") { _, _ ->
                            viewModel.deleteNote(currentNote)
                        }
                        .setNegativeButton("Cancel") { _, _ ->  }
                        .create()
                        .show()
                }
            }
        }

        return true
    }
}
