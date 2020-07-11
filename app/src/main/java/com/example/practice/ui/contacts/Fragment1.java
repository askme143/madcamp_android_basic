package com.example.practice.ui.contacts;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Fragment1 extends Fragment {
    private ArrayList<Contact> contactList;

    public Fragment1 () {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment1, null) ;

        ListView listView = (ListView) view.findViewById(R.id.retrieve_ListView);
        // get the reference of RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        contactList = getContactList();

        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), contactList);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView

        return view;
    }

    public ArrayList<Contact> getContactList() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts._ID
        };
        String[] selectionArgs = null;

        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME+" COLLATE LOCALIZED ASC";

        Cursor cursor = getActivity().getContentResolver().query(uri, projection,
                null, null, sortOrder);

        LinkedHashSet<Contact> hashSet = new LinkedHashSet<>();
        ArrayList<Contact> contactList;

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String phoneNumber = cursor.getString(1);
                long photoID = cursor.getLong(2);
                long personID = cursor.getLong(3);

                Contact contact = new Contact(name, phoneNumber, photoID, personID);

                if (phoneNumber.startsWith("01")) {
                    hashSet.add(contact);
                    //contactsList.add(myContact);
                }

            } while (cursor.moveToNext());
        }

        contactList = new ArrayList<>(hashSet);

        for (int i = 0; i < contactList.size(); i++) {
            contactList.get(i).setID(i);
        }

        cursor.close();

        return contactList;
    }
}