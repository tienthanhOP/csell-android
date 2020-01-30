package csell.com.vn.csell.interfaces;

import java.util.List;

import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.ProductResponseV1;

public interface GetProduct {
    void onGetDetail(ProductResponseV1 product);
    void onGetNoteProduct(List<NoteV1> lstNote);
    void onGetDetailNewfeed(Product product);
}
