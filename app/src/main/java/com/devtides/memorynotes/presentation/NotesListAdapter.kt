package com.devtides.memorynotes.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devtides.core.data.Note
import com.devtides.memorynotes.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.Date

class NotesListAdapter(var notes: ArrayList<Note>, val actions: ListAction): RecyclerView.Adapter<NotesListAdapter.NoteViewHolder>() {

    fun updateNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteViewHolder(
        ItemNoteBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    inner class NoteViewHolder(binding: ItemNoteBinding): RecyclerView.ViewHolder(binding.root) {

        private val layout = binding.noteLayout
        private val noteTitle = binding.title
        private val noteContent = binding.content
        private val noteDate = binding.date
        private val noteWords = binding.wordCount

        fun bind(note: Note) {
            noteTitle.text = note.title
            noteContent.text = note.content

            val sdf = SimpleDateFormat("MMM dd, HH:mm:ss")
            val resultDate = Date(note.updateTime)
            noteDate.text = "Last updated: ${sdf.format(resultDate)}"

            layout.setOnClickListener { actions.onClick(note.id) }

            noteWords.text = "Words: ${note.wordCount}"
        }
    }
}