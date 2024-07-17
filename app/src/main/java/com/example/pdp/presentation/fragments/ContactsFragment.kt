package com.example.pdp.presentation.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdp.databinding.FragmentContactsBinding
import com.example.pdp.helper.ContactsManager
import com.example.pdp.presentation.adapters.ContactsAdapter
import com.example.pdp.presentation.base.BaseFragment
import kotlinx.coroutines.launch

private const val REQUEST_READ_CONTACTS = 1

class ContactsFragment : BaseFragment<FragmentContactsBinding>() {

    private var contactsManager: ContactsManager? = null
    private var contactsAdapter: ContactsAdapter? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentContactsBinding {
        return FragmentContactsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactsManager = ContactsManager(requireContext().contentResolver)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        contactsAdapter = ContactsAdapter()
        binding.recyclerView.adapter = contactsAdapter

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED) {
            displayContacts()
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
    }

    private fun displayContacts() {
        viewLifecycleOwner.lifecycleScope.launch {
            val contacts = contactsManager?.getContacts()
            if (contacts != null) {
                contactsAdapter?.setContacts(contacts)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_CONTACTS && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            displayContacts()
        }
    }

    override fun onDestroyView() {
        contactsManager = null
        contactsAdapter = null
        super.onDestroyView()
    }
}