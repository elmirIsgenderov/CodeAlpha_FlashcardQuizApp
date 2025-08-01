package com.example.flashcardquizapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import com.example.flashcardquizapp.R
import com.example.flashcardquizapp.utils.Resource
import com.example.flashcardquizapp.databinding.FragmentMainBinding
import com.example.flashcardquizapp.room.Flashcard
import com.example.flashcardquizapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()

    private var flashcards: List<Flashcard> = emptyList()
    private var currentFlashcard: Flashcard? = null
    private var currentIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAll()
        setupToolbar()
        observeViewModelStates()
        setupMenu()
        setupButtonListeners()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    private fun observeViewModelStates() {
        viewModel.getAll.observe(viewLifecycleOwner) { result ->
            handleResourceResult(result, onSuccess = { data ->
                flashcards = data
                if (flashcards.isNotEmpty()) {
                    currentIndex = 0
                    showFlashcard(currentIndex)
                } else {
                    binding.txtQuestion.text = "No flashcards found"
                    binding.txtAnswer.text = ""
                }
            }
            )
        }


        viewModel.insert.observe(viewLifecycleOwner) { result ->
            handleResourceResult(result, onSuccess = {
                showToast("Flashcard added")
                viewModel.getAll()
            })
        }

        viewModel.update.observe(viewLifecycleOwner) { result ->
            handleResourceResult(result, onSuccess = {
                showToast("Flashcard updated")
                viewModel.getAll()
            })
        }

        viewModel.delete.observe(viewLifecycleOwner) { result ->
            handleResourceResult(result, onSuccess = {
                showToast("Flashcard deleted")
                viewModel.getAll()
            })
        }
    }

    private fun <T> handleResourceResult(
        result: Resource<T>,
        onSuccess: (T) -> Unit,
        onError: (() -> Unit)? = null
    ) {
        when (result) {
            is Resource.Success -> onSuccess(result.data)
            is Resource.Error -> {
                showToast("An error occurred")
                onError?.invoke()
            }

            is Resource.Loading -> Unit
        }
    }


    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return handleMenuItemClick(menuItem)
            }
        }, viewLifecycleOwner)
    }

    private fun handleMenuItemClick(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_add -> {
                showAddDialog()
                true
            }

            R.id.menu_edit -> {
                showUpdateDialog()
                true
            }

            R.id.menu_detele -> {
                confirmDelete()
                true
            }

            else -> false
        }
    }

    private fun showFlashcard(index: Int) {
        val flashcard = flashcards[index]
        currentFlashcard = flashcard
        binding.txtQuestion.text = flashcard.question
        binding.txtAnswer.text = ""
        binding.txtAnswer.visibility = View.VISIBLE
    }

    private fun setupButtonListeners() {
        binding.btnShow.setOnClickListener {
            if (flashcards.isNotEmpty()) {
                binding.txtAnswer.text = flashcards[currentIndex].answer
            }
        }

        binding.btnNext.setOnClickListener {
            if (flashcards.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % flashcards.size
                showFlashcard(currentIndex)
            }
        }

        binding.btnPrevious.setOnClickListener {
            if (flashcards.isNotEmpty()) {
                currentIndex = if (currentIndex > 0) currentIndex - 1 else flashcards.lastIndex
                showFlashcard(currentIndex)
            }
        }
    }

    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.item_add_dialog, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add new flashcard")
            .setCancelable(true)
            .create()
        dialog.show()

        val edtQuestion = dialogView.findViewById<EditText>(R.id.edtQuestionAdd)
        val edtAnswer = dialogView.findViewById<EditText>(R.id.edtAnswerAdd)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)

        btnAdd.setOnClickListener {
            val question = edtQuestion.text.toString().trim()
            val answer = edtAnswer.text.toString().trim()

            if (question.isNotEmpty() && answer.isNotEmpty()) {
                viewModel.insert(Flashcard(0, question, answer))
                dialog.dismiss()
            } else {
                showToast("Fields cannot be empty")
            }
        }
    }

    private fun showUpdateDialog() {
        val dialogView = layoutInflater.inflate(R.layout.item_update_dialog, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Update flashcard")
            .setCancelable(true)
            .create()
        dialog.show()

        val edtQuestion = dialogView.findViewById<EditText>(R.id.edtQuestion)
        val edtAnswer = dialogView.findViewById<EditText>(R.id.edtAnswer)
        val btnUpdate = dialogView.findViewById<Button>(R.id.btnUpdate)

        currentFlashcard?.let {
            edtQuestion.setText(it.question)
            edtAnswer.setText(it.answer)
        }

        btnUpdate.setOnClickListener {
            val newQuestion = edtQuestion.text.toString().trim()
            val newAnswer = edtAnswer.text.toString().trim()

            if (newQuestion.isNotEmpty() && newAnswer.isNotEmpty()) {
                val updatedFlashcard = Flashcard(currentFlashcard!!.id, newQuestion, newAnswer)
                viewModel.update(updatedFlashcard)
                dialog.dismiss()
            } else {
                showToast("Fields cannot be empty")
            }
        }
    }

    private fun confirmDelete() {
        currentFlashcard?.let { flashcard ->
            AlertDialog.Builder(requireContext())
                .setTitle("Delete flashcard")
                .setMessage("Are you sure you want to delete this flashcard?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.delete(flashcard)
                }
                .setNegativeButton("No", null)
                .show()
        } ?: showToast("No flashcard selected")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
