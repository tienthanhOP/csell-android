package csell.com.vn.csell.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import csell.com.vn.csell.interfaces.GetContactsListener;
import csell.com.vn.csell.models.ContactLocal;

/**
 * Created by chuc.nq on 4/12/2018.
 */

public class GetContacts {
    private GetContactsListener mListener;
    private Context mContext;

    public GetContacts(Context context, GetContactsListener listener1) {
        this.mContext = context;
        this.mListener = listener1;
    }

    public void execute() throws UnsupportedEncodingException {
        new getAllContactsAndAddToList().execute();
    }

    @SuppressLint("StaticFieldLeak")
    class getAllContactsAndAddToList extends AsyncTask<Void, Void, ArrayList<ContactLocal>> {

        @Override
        protected ArrayList<ContactLocal> doInBackground(Void... voids) {

            ArrayList<ContactLocal> arrContacts = new ArrayList();
            HashMap<Integer, ContactLocal> tempContacts = new LinkedHashMap<>();

            final String[] projections = new String[]{
                    ContactsContract.Data.DATA1,
                    ContactsContract.Data.DATA2,
                    ContactsContract.Data.DATA3,
                    ContactsContract.Data.DISPLAY_NAME,
                    ContactsContract.Data.CONTACT_ID,
                    ContactsContract.Data.MIMETYPE
            };

            Uri uri = ContactsContract.Data.CONTENT_URI;
            String selection = ContactsContract.Data.MIMETYPE + " = ?" +
                    " OR " +
                    ContactsContract.Data.MIMETYPE + " = ?";
            Cursor cursor = mContext.getContentResolver().query(uri,
                    projections,
                    selection,
//                null,
                    new String[]{
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    },
//                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                    ContactsContract.Data.CONTACT_ID + " DESC");

            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                    int contactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                    String mime = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));

                    String email, phone1, phone2 = null;

                    if (mime.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                        email = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1));
                        phone1 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA2));
                        if (cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA3)) != null) {
                            phone2 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1));
                        }
                    } else {
                        phone1 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1));
                        email = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA2));
                    }

                    // If contact is not yet created
                    if (tempContacts.get(contactID) == null) {
                        // If type email, add all detail, else add name and photo (we don't need number)
                        if (mime.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                            tempContacts.put(contactID, new ContactLocal(contactID, name, email,
                                    null, null, false, false
                            ));
                        } else {

                            tempContacts.put(contactID, new ContactLocal(contactID, name, null,
                                    phone1, phone2, false, false
                            ));

                        }

                    } else {
                        // Contact is already present
                        // Add email if type email
                        if (mime.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                            tempContacts.get(contactID).setEmail(email);
                        } else {
                            if (tempContacts.get(contactID).getPhone1() != null) {
                                tempContacts.get(contactID).setPhone2(phone1);
                            } else {
                                tempContacts.get(contactID).setPhone1(phone1);
                            }
                        }
                    }

                    cursor.moveToNext();

                }
                cursor.close();

                // Convert to ArrayList if you need an arraylist
                for (Map.Entry<Integer, ContactLocal> contact : tempContacts.entrySet()) {
                    arrContacts.add(contact.getValue());
                }
            }
            return arrContacts;
        }

        @Override
        protected void onPostExecute(ArrayList<ContactLocal> list) {
            super.onPostExecute(list);
            mListener.onGetContactsSuccess(list);
        }
    }


}
