package csell.com.vn.csell.interfaces;

import java.util.ArrayList;

import csell.com.vn.csell.models.ContactLocal;

/**
 * Created by chuc.nq on 4/12/2018.
 */

public interface GetContactsListener {
    void onGetContactsSuccess(ArrayList<ContactLocal> data);
}
