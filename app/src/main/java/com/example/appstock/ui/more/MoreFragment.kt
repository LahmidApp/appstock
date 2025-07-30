package com.example.appstock.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appstock.R

/**
 * Fragment providing access to additional modules like clients, invoices, quotes,
 * expenses and settings. Each button shows a placeholder message for now.
 */
class MoreFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)
        val buttonClients: Button = view.findViewById(R.id.button_clients)
        val buttonInvoices: Button = view.findViewById(R.id.button_invoices)
        val buttonQuotes: Button = view.findViewById(R.id.button_quotes)
        val buttonExpenses: Button = view.findViewById(R.id.button_expenses)
        val buttonSettings: Button = view.findViewById(R.id.button_settings)

        val placeholder = { title: String ->
            Toast.makeText(requireContext(), "$title module en développement", Toast.LENGTH_SHORT).show()
        }
        buttonClients.setOnClickListener { placeholder("Clients") }
        buttonInvoices.setOnClickListener { placeholder("Factures") }
        buttonQuotes.setOnClickListener { placeholder("Devis") }
        buttonExpenses.setOnClickListener { placeholder("Dépenses") }
        buttonSettings.setOnClickListener { placeholder("Paramètres") }

        return view
    }
}