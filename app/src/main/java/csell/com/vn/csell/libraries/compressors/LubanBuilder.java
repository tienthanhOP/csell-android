package csell.com.vn.csell.libraries.compressors;

import android.graphics.Bitmap;
import java.io.File;

/**
 * Created by shaohui on 2016/12/17.
 */

class LubanBuilder {

    int maxSize;

    int maxWidth;

    int maxHeight;

    File cacheDir;

    Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.WEBP;

    int gear = Luban.THIRD_GEAR;

    LubanBuilder(File cacheDir) {
        this.cacheDir = cacheDir;
    }

}
