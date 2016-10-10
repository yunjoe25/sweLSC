package ocr.swelsc;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class HistoryCalendar extends AppCompatActivity {

    protected String name;

    private String Syllabus;

    protected static String content;

    protected static String title;

    protected static String MidTerm;

    protected static String Final;

    protected static String Events;

    protected static String Withdrawl;

    protected static String midTermDate;


    private String Description;

    // Gets the sample document form the assets folder.

    public void loadFile(Context context) throws IOException {

        try {
            AssetManager assetManager = getAssets();

            InputStream stream = assetManager.open("Test.txt");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String text = new String(buffer);
            setContent(text);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Getter

    public static String getContent() {
        return content;
    }

    //Setter
    public static void setContent(String content) {
        HistoryCalendar.content = content;
    }


    public void setTitle(String title) {
        HistoryCalendar.title = title;
    }


    public static void setMidTerm(String midEvent) {

        //String Midterm = midTerm;
        HistoryCalendar.MidTerm = midEvent;


    }


    public static void setFinal(String finalEvent) {
        HistoryCalendar.Final = finalEvent;
    }

    //Scans through doc to find midterm date. returns date as string to be sent to calendar.
    public static Calendar getMidTerm() {

        Calendar beginTime = Calendar.getInstance();

        ArrayList<String> eventInfo = new ArrayList<String>();

        GetMonths getMonths = new GetMonths();

        ArrayList<String> Months = getMonths.allMonths();

        for (String word : MidTerm.split(" ")) {

            eventInfo.add(word);

        }
        beginTime = getMonths.dateExtraction(eventInfo);


        return beginTime;

    }


    public static Calendar getFinal() {
        Calendar beginTime = Calendar.getInstance();

        ArrayList<String> eventInfo = new ArrayList<String>();

        GetMonths getMonths = new GetMonths();

        ArrayList<String> Months = getMonths.allMonths();

        for (String word : Final.split(" ")) {

            eventInfo.add(word);

        }

        beginTime = getMonths.dateExtraction(eventInfo);


        return beginTime;


    }


    public static void ImportantDates() {

        imgCapture textCapture = new imgCapture();

        String midEvent, finalEvent, wEvent;

        String Syllabus = textCapture.getRecognizedText();
        Log.println(Log.DEBUG,"Dates", Syllabus);
        if (Syllabus.length() == 0) {
            Toast.makeText(null, "No dates available.", Toast.LENGTH_LONG).show();
        } else {
            String position, pos2;

            String[] lines = Syllabus.split(" ");

            GetMonths GetMonths = new GetMonths();

            ArrayList<String> Months = GetMonths.allMonths();

            String[] monthList = new String[Months.size()];

            monthList = Months.toArray(monthList);

            for (int i = 0; i < lines.length; i++) {

                position = lines[i];

                for (int j = 0; j < monthList.length; j++) {

                    pos2 = monthList[j];

                    if (position.contains("Midterm") && position.contains(pos2)) {

                        midEvent = position;

                        setMidTerm(midEvent);

                        // Log.i("Midterm:", midEvent);


                    } else if (position.contains("Final") && position.contains(pos2)) {

                        finalEvent = position;

                        //   Log.i("Final", finalEvent);
                        setFinal(finalEvent);

                    } else if (position.contains("Withdraw") && position.contains(pos2)) {

                        wEvent = position;

                        Log.i("Withdraw", wEvent);

                    }

                }
            }
        }
    }


    public static String lineCleaner(String Line) {

        String date = "Date";

        return date;
    }
}



