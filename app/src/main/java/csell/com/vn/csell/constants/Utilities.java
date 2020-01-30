package csell.com.vn.csell.constants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import csell.com.vn.csell.R;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.ContactLocal;
import csell.com.vn.csell.models.CustomerRetro;
import csell.com.vn.csell.models.FriendResponse;
import csell.com.vn.csell.models.ImageSuffix;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.models.PropertyValue;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLPropertyValue;
import csell.com.vn.csell.views.csell.activity.LoginActivity;
import csell.com.vn.csell.views.csell.activity.MainActivity;

////import com.google.firebase.firestore.Query;

/**
 * Created by cuong.nv on 3/6/2018.
 */

public class Utilities {

    public static final List<Category> lsSelectCategory = new ArrayList<>();
    public static final List<ProductCountResponse> lsSelectGroupProduct = new ArrayList<>();
    public static final int TYPE_POST_PUBLIC = 1;
    public static final int TYPE_POST_FRIEND = 2;
    public static final int TYPE_POST_ONLY_ME = 3;
    public static final int TYPE_POST_CHOOSE_FRIEND = 4;
    public static final String PROJECT_EQUAL = "_project_";
    public static final String PROJECT_CONTAINS = "project";
    public static final String LAND_PROJECT = "land_project";
    public static final String SIM_VIP = "vip";
    public static final String SIM_EASY_SPECIAL = "easyspecial";
    public static final String LAND_HOUSE = "land_house";
    public static final String HOUSE = "house";
    public static final String LAND = "land";
    public static final String SIM = "sim";
    public static final String CAR = "car";
    public static final String OTHER = "other";
    public static final String SIM_LIST_MONTH = "sim_listmonth";
    public static final String DEFAULT_IMAGE_SIM = "https://farm1.staticflickr.com/956/40963292545_81a200a62f.jpg";
    public static final String COLOR_WHITE = "trắng";
    public static final String COLOR_SILVER = "bạc";
    public static final String ACTION_LIKE = "like";
    public static final String ACTION_COMMENT = "comment";
    public static final String ACTION_NOTE_PRIVATE = "note_private";
    private final static char SPACE = ' ';
    private final static char TAB = '\t';
    private final static char BREAK_LINE = '\n';
    public static ArrayList<Integer> SUFFIXES = new ArrayList<>(Arrays.asList(
            320,
            480,
            640,
            720,
            750,
            1080,
            1125
    ));
    public static List<ProductCountResponse> lsSelectGroupProductFriend = new ArrayList<>();
    public static ArrayList<UserRetro> lsFriendsNotePrivate = new ArrayList<>();
    public static ArrayList<String> lsTitleCreate = new ArrayList<>();
    public static String PRODUCT_KEY = "";
    public static boolean isPrivateMode = false;
    public static ArrayList<FriendResponse> lsFriendRequest = new ArrayList<>();
    //    public static boolean IS_SELECT_LAND_PROJECT = false;
    public static boolean IS_SELECT_PROJECT = false;
    public static String acreageType;
    public static String USER_NAME = "";
    public static String LOG_EXCEPTION = USER_NAME + "_" + "Log: ";
    public static String contentPrivateNote = "";
    public static String TOKEN_HEADER = "";
    public static boolean IsNotificationComment = false;
    public static String DATE_REMINDER_BEFORE_FORMAT_1 = "yyyy-MM-dd'T'HH:mm:ss";
    public static String DATE_REMINDER_BEFORE_FORMAT_2 = "dd-MM-yyyy HH:mm:ss";
    public static String DATE_REMINDER_AFTER_FORMAT_HHMM = "yyyy-MM-dd HH:mm";
    public static String DATE_REMINDER_FULL = "EEE MMM dd yyyy HH:mm:ss zzz";
    public static String URL_HTTP = "http";
    private static ArrayList<String> typeDate = new ArrayList<>(Arrays.asList(
            "EEE MMM dd yyyy HH:mm:ss zzz",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",
            "dd-MM-yyyy"
    ));
    private static String DATE_REMINDER_AFTER_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private Context context;

    public Utilities(Context context) {
        this.context = context;
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    public static void signInWithToken(String token, Activity context) {
        try {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithCustomToken(token)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            Intent main = new Intent(context, MainActivity.class);
                            Bundle bundle = context.getIntent().getExtras();
                            if (bundle != null) {


                                String type = bundle.getString("type_noti");
                                String key = bundle.getString("key_noti");

                                if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(key)) {
                                    main.putExtra("type_noti", type);
                                    main.putExtra("key_noti", key);
                                }
                            }
                            context.startActivity(main);
                            context.finishAfterTransition();

                        } else {
                            Intent main = new Intent(context, LoginActivity.class);
                            context.startActivity(main);
                            context.finishAfterTransition();

                            Toast.makeText(context, R.string.please_try_again, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Intent main = new Intent(context, LoginActivity.class);
                        context.startActivity(main);
                        context.finishAfterTransition();
                        Toast.makeText(context, R.string.please_try_again, Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Intent main = new Intent(context, LoginActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(main);
            context.finishAfterTransition();
        }
    }

    public static int countWords(String input) {
        if (input == null) {
            return -1;
        }
        int count = 0;
        int size = input.length();
        boolean notCounted = true;
        for (int i = 0; i < size; i++) {
            if (input.charAt(i) != SPACE && input.charAt(i) != TAB
                    && input.charAt(i) != BREAK_LINE) {
                if (notCounted) {
                    count++;
                    notCounted = false;
                }
            } else {
                notCounted = true;
            }
        }
        return count;
    }

    public static ArrayList<PropertyValue> CURRENCY(Context context) {
        ArrayList<PropertyValue> data = new ArrayList<>();
        SQLPropertyValue sqlPropertyValue = new SQLPropertyValue(context);
        data.addAll(sqlPropertyValue.getPropertiesByCatePropName("all", "currency"));

        return data;
    }

    public static DividerItemDecoration getLineDivider(Context context) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.bg_line_divider_list_silver));
        return itemDecoration;
    }

    public static DividerItemDecoration getLineDividerSpace(Context context) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.bg_line_divider_list_silver));
        return itemDecoration;
    }

    public static File createImageFile(String name) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = name + timeStamp;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Constants.FOLDER_NAME), Constants.FOLDER_IMAGE_UPLOAD);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = new File(storageDir, imageFileName);
        return image;
    }

    public static ArrayList<String> converterBase64(Context context, ArrayList<String> listUri) {
        ArrayList<String> listImgBase64 = new ArrayList<>();
        try {
            if (listUri.size() > 0) {

                for (String path : listUri) {
                    try {
                        Uri newUri = Uri.fromFile(new java.io.File(path));
                        InputStream iStream = context.getContentResolver().openInputStream(newUri);
                        byte[] inputData = getBytes(iStream);
                        String encodedString = Base64.encodeToString(inputData, Base64.DEFAULT);
                        listImgBase64.add(encodedString);
                    } catch (Exception ex) {

                    }
                }
            }
        } catch (Exception e) {

        }
        return listImgBase64;
    }

    public static byte[] getBytes(InputStream inputStream) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        try {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            byteBuffer.flush();
            byteBuffer.close();
        } catch (IOException e) {
        }
        return byteBuffer.toByteArray();
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertTimeMillisToDateStringyyyyMMddHHmmss(Long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_REMINDER_AFTER_FORMAT);
        Date date = new Date();
        date.setTime(timeMillis);
        String format = formatter.format(date);
        return formatter.format(date.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertTimeMillisToDateStringddMMyyyy(Long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        date.setTime(timeMillis);
        return formatter.format(date.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertTimeMillisToDateStringddMMyyyyHHmmss(Long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_REMINDER_BEFORE_FORMAT_2);
        Date date = new Date();
        date.setTime(timeMillis);
        String format = formatter.format(date);
        return formatter.format(date.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateTimeToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_REMINDER_AFTER_FORMAT);
        String time = formatter.format(date);
        return time;
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateTimeToString(Date date, String regex) {
        SimpleDateFormat formatter = new SimpleDateFormat(regex);
        return formatter.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static Long convertDateStringToMilisAllType(String date) {
        long timeInMilliseconds = 0;
        int size = typeDate.size();
        for (int i = 0; i < size; i++) {
            String fr = typeDate.get(i);
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat formatter = new SimpleDateFormat(fr);
            Date mDate;
            try {
                formatter.setTimeZone(tz);
                mDate = formatter.parse(date);
                timeInMilliseconds = mDate.getTime();
                break;
            } catch (Exception ignored) {

            }
        }

        return timeInMilliseconds;
    }

    @SuppressLint("SimpleDateFormat")
    public static Calendar convertDateStringToCalendarAllType(String date) {
        Date mDate = null;
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < typeDate.size(); i++) {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat(typeDate.get(i));
            try {
                df.setTimeZone(tz);
                mDate = df.parse(date);
                cal.setTime(mDate);
                break;
            } catch (Exception e) {

            }
        }

        return cal;
    }

    @SuppressLint("SimpleDateFormat")
    public static Calendar convertDateStringToCalendarAllType2(String date) {
        Date mDate;
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < typeDate.size(); i++) {
            SimpleDateFormat df = new SimpleDateFormat(typeDate.get(i));
            try {
                mDate = df.parse(date);
                cal.setTime(mDate);
                break;
            } catch (Exception e) {

            }
        }

        return cal;
    }

    public static String convertUnixTime(long time) {
        Date date = new Date(time * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
        return sdf.format(date);
    }


    @SuppressLint("SimpleDateFormat")
    public static Calendar localTime(String date) {
        Date mDate;
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < typeDate.size(); i++) {
            SimpleDateFormat df = new SimpleDateFormat(typeDate.get(i));
            try {
                mDate = df.parse(date);
                cal.setTime(mDate);
                break;
            } catch (Exception e) {

            }
        }

        return cal;
    }


    public static Date convertTimeMillisToDate(Long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return calendar.getTime();
    }

    public static boolean compareDateSame(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(
                    Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public static void sortListContactLocal(ArrayList<ContactLocal> data) {

        Collections.sort(data, (o1, o2) -> o1.getContactName().compareToIgnoreCase(o2.getContactName()));

    }

    public static void sortListCustomer(ArrayList<CustomerRetro> data) {

        Collections.sort(data, (o1, o2) -> {
            if (o1.getName() != null && o2.getName() != null) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            } else
                return -1;
        });

    }


    public static void sortListNote(ArrayList<NoteV1> listProductNote) {

        Collections.sort(listProductNote, (o1, o2) -> {
            String calendar2 = "", calendar1 = "";
            if (!TextUtils.isEmpty(o2.getNoticeAt() + "") && !TextUtils.isEmpty(o1.getNoticeAt() + "")) {
//                    calendar2 = convertDateStringFormat(o2.getDatereminder(), DATE_REMINDER_FULL, DATE_REMINDER_AFTER_FORMAT_HHMM);
                Calendar cal2 = convertDateStringToCalendarAllType(o2.getNoticeAt() + "");
                calendar2 = convertDateTimeToString(cal2.getTime());

                Calendar cal1 = convertDateStringToCalendarAllType(o1.getNoticeAt() + "");
                calendar1 = convertDateTimeToString(cal1.getTime());
            }

            return calendar2.compareTo(calendar1);

        });
    }

    public static void sortListNoteV1(ArrayList<NoteV1> listProductNote) {

        Collections.sort(listProductNote, (o1, o2) -> {
            String calendar2 = "", calendar1 = "";
            if (!TextUtils.isEmpty(o2.getNoticeAt() + "") && !TextUtils.isEmpty(o1.getNoticeAt() + "")) {
//                    calendar2 = convertDateStringFormat(o2.getDatereminder(), DATE_REMINDER_FULL, DATE_REMINDER_AFTER_FORMAT_HHMM);
                Calendar cal2 = convertDateStringToCalendarAllType(o2.getNoticeAt() + "");
                calendar2 = convertDateTimeToString(cal2.getTime());

                Calendar cal1 = convertDateStringToCalendarAllType(o1.getNoticeAt() + "");
                calendar1 = convertDateTimeToString(cal1.getTime());
            }

            return calendar2.compareTo(calendar1);

        });
    }

    public static String formatMoney(long price, String typePrice) {
        NumberFormat formatter = new DecimalFormat("#,###");
        String money = formatter.format(price);
        money = money.replaceAll(",", "\\.");
        if (TextUtils.isEmpty(typePrice)) {
            money = money + " " + "đ";
        } else {
            if (typePrice.toLowerCase().equals("vnd")) {
                money = money + " " + "đ";
            } else if (typePrice.toLowerCase().equals("usd")) {
                money = money + " " + "USD";
            } else if (typePrice.toLowerCase().equals("bảng anh")) {
                money = money + " " + "₤";
            }
        }

        return money;
    }

    public static Long formatCurrencySplit(String price, String regex) {
        long money = 0;
        try {
            String formatter = price.replaceAll(regex, "");
            money = Long.parseLong(formatter);
        } catch (NumberFormatException e) {
            return money;
        }
        return money;
    }


    public static void pushNotificatonLocal(Context context, String title, String body) {
        Intent intent = new Intent();

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_csell_logo_notification)
                        .setBadgeIconType(R.drawable.ic_csell_logo_notification)
                        .setContentTitle(title)
                        .setContentText("" + body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify("CSELL", 0 /* ID of notification */, notificationBuilder.build());
    }

    public static void changeStatusBarColor(Activity mContext, boolean check) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (check) {
                window.setStatusBarColor(mContext.getResources().getColor(R.color.red_100));
            } else {
                window.setStatusBarColor(mContext.getResources().getColor(R.color.light_blue_100));
            }
        }
    }

    public static void resizeImageFromCamera(Intent data, ArrayList<String> listPathImageResize) {
        try {
            listPathImageResize.clear();
            Bitmap scaledBitmap;
            OutputStream outputStream;
            File file;
            ArrayList<Image> images;
            images = (ArrayList<Image>) ImagePicker.getImages(data);
            for (Image img : images) {
                try {
                    File f = new File(img.getPath());
                    Bitmap bm = BitmapFactory.decodeFile(img.getPath());

                    ExifInterface ei = new ExifInterface(img.getPath());
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bm, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bm, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bm, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bm;
                    }

                    long lengthByte = f.length();
                    if (lengthByte / 1024 < 1024) {
                        file = Utilities.createImageFile(String.valueOf(img.getId()));
                        outputStream = new FileOutputStream(file);
                        int w = rotatedBitmap.getWidth();
                        int h = rotatedBitmap.getHeight();
                        scaledBitmap = (Bitmap.createScaledBitmap(rotatedBitmap, w, h, false));
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    } else {
                        file = Utilities.createImageFile(String.valueOf(img.getId()));
                        outputStream = new FileOutputStream(file);
                        int w = (int) (rotatedBitmap.getWidth() * 0.5);
                        int h = (int) (rotatedBitmap.getHeight() * 0.5);
                        scaledBitmap = (Bitmap.createScaledBitmap(rotatedBitmap, w, h, false));
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }
//                    if (PersonalPageActivity.imgAvatar != null) {
//                        PersonalPageActivity.imgAvatar.setImageBitmap(scaledBitmap);
//                    }
                    listPathImageResize.add(file.getPath());

                } catch (IOException e) {
                    Log.d("Utilities.Class", e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.d("Utitletis.Class", e.getMessage());
        }

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static String upperFirstLetter(String name) {
        String firstLetter = name.substring(0, 1);
        return firstLetter.toUpperCase() + name.substring(1, name.length());
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static String convertTimeMillisToDateStringForm(Long timeMillis) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(DATE_REMINDER_AFTER_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return formatter.format(calendar.getTime());
    }

    public static long convertDateStringToTimeMillis(String givenDateString) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(DATE_REMINDER_AFTER_FORMAT);
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(givenDateString);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeInMilliseconds;
    }

    public static void refreshToken(Context context) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FileSave fileSave = new FileSave(context, Constants.PUT);
                                    fileSave.putTimeRefreshToken(System.currentTimeMillis());
                                }
                            });
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
                return null;
            }
        }.execute();
    }


    public static void refreshToken(Context context, String session) {
        if (session.toLowerCase().contains("session")) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
//                                            Utilities.TOKEN_HEADER = task.getResult().getToken();
//                                           if(BuildConfig.DEBUG) Log.w("TOKEN_HEADER_1", fileGet.getToken());
                                        FileSave fileSave = new FileSave(context, Constants.PUT);
                                        fileSave.putTimeRefreshToken(System.currentTimeMillis());
                                    }
                                });
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                    return null;
                }
            }.execute();
        }

    }


    public static int getResourseId(Context context, String imageName) {
        try {
            return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        } catch (Exception e) {
            Crashlytics.logException(e);
            return R.drawable.noimage;
        }

    }


    public static String encryptString(String value) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-256");
            crypt.reset();
            crypt.update(value.getBytes("UTF-8"));
            sha1 = byteToHexadecimal(crypt.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Crashlytics.logException(e);
        }
        return sha1;
    }

    private static String byteToHexadecimal(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static String convertDateStringFormat(String dateString, String originalDateFormat, String outputDateFormat) {
        String finalDate = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(originalDateFormat);
        try {
            Date date = simpleDateFormat.parse(dateString);
            simpleDateFormat = new SimpleDateFormat(outputDateFormat);
            finalDate = simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return finalDate;
    }

    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    public static int checkSizeForGetImages(Activity mActivity) {
        int size320 = 320, size480 = 480, size640 = 640, size720 = 720, size750 = 750, size1080 = 1080, size1125 = 1125;
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (width < size480)
            return size320;
        else if (width < size640)
            return size480;
        else if (width < size720)
            return size640;
        else if (width < size750)
            return size720;
        else if (width < size1080)
            return size750;
        else if (width < size1125)
            return size1080;
        else
            return size1125;
    }

    //.jpg
    public static String subStringForGetImages(String str) {
        return str.substring(0, str.length() - 4);
    }

    public static String getLinkImageCDN(Activity activity, ImageSuffix imageSuffix, boolean isRoot) {

        if (isRoot) {
            return imageSuffix.getPath();
        }
        if (imageSuffix.getSuffixes() == null) {
            return imageSuffix.getPath();
        }


        try {
            int size = Utilities.checkSizeForGetImages(activity);//480

            int getSuffix = imageSuffix.getSuffixes().size();//==5
            if (getSuffix != 0) {
                for (int j = 0; j < Utilities.SUFFIXES.size(); j++) { //0: 320
                    if (getSuffix < j)
                        break;
                    if (size == Utilities.SUFFIXES.get(j))
                        if (getSuffix > j)
                            getSuffix = j;
                }
            }


            String linkImage = Utilities.subStringForGetImages(imageSuffix.getPath());
            switch (getSuffix) {
                case 0:
                    linkImage = linkImage + Constants.JPG;
                    break;
                case 1:
                    linkImage = linkImage + "-" + Utilities.SUFFIXES.get(0) + Constants.JPG;
                    break;
                case 2:
                    linkImage = linkImage + "-" + Utilities.SUFFIXES.get(1) + Constants.JPG;
                    break;
                case 3:
                    linkImage = linkImage + "-" + Utilities.SUFFIXES.get(2) + Constants.JPG;
                    break;
                case 4:
                    linkImage = linkImage + "-" + Utilities.SUFFIXES.get(3) + Constants.JPG;
                    break;
                case 5:
                    linkImage = linkImage + "-" + Utilities.SUFFIXES.get(4) + Constants.JPG;
                    break;
                case 6:
                    linkImage = linkImage + "-" + Utilities.SUFFIXES.get(5) + Constants.JPG;
                    break;
                case 7:
                    linkImage = linkImage + "-" + Utilities.SUFFIXES.get(6) + Constants.JPG;
                    break;
            }

            return linkImage;
        } catch (Exception e) {
            return imageSuffix.getPath();
        }
    }

    public static void setLocale(Context mContext, String isoCode) {
        Locale locale = new Locale(isoCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        mContext.getResources().updateConfiguration(configuration, mContext.getResources().getDisplayMetrics());

        ((Activity) mContext).recreate();
        ((Activity) MainActivity.mainContext).recreate();
    }

    public static void setLocale2(Context mContext, String isoCode) {
        Locale locale = new Locale(isoCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        mContext.getResources().updateConfiguration(configuration, mContext.getResources().getDisplayMetrics());
    }

    public static int dpToPx(Context mContext, int dp) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static boolean isNetworkConnected(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public static InetAddress getInetAddressByName() {
        AsyncTask<String, Void, InetAddress> task = new AsyncTask<String, Void, InetAddress>() {

            @Override
            protected InetAddress doInBackground(String... params) {
                try {
                    if (!InetAddress.getByName(params[0]).equals(""))
                        return InetAddress.getByName(params[0]);
                    else
                        return null;
                } catch (UnknownHostException e) {
                    return null;
                }
            }
        };
        try {
            return task.execute("google.com.vn").get();
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        }

    }

    public static int getStatusBarHeight(Context mContext) {
        int height;

        Resources myResources = mContext.getResources();
        int idStatusBarHeight = myResources.getIdentifier(
                "status_bar_height", "dimen", "android");
        if (idStatusBarHeight > 0) {
            height = mContext.getResources().getDimensionPixelSize(idStatusBarHeight);
        } else {
            height = 0;
        }

        return height;
    }


    public static boolean checkPhoneNumber(String phone) {
        String regexPhone = "^(0[2|3|5|7|8|9])+([0-9]{8})|^(09|01[2|6|8|9])+([0-9]{8})|^(02[0-9])+([0-9]{8})";
        Pattern sPattern
                = Pattern.compile(regexPhone);
        return sPattern.matcher(phone).matches();
    }

    public static boolean checkEmail(String email) {
        String regexEmail = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern sPattern
                = Pattern.compile(regexEmail);
        return sPattern.matcher(email).matches();
    }

}
