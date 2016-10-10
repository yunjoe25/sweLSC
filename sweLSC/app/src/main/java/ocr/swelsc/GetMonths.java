package ocr.swelsc;

import android.util.Log;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GetMonths {

    // Returns May, June etc
    public ArrayList<String> Months() {
        String[] months = new DateFormatSymbols().getMonths();

        ArrayList<String> monthList = new ArrayList<String>();

        for (int i = 0; i < months.length; i++) {
            String month = months[i];
            monthList.add(month);
        }
        return monthList;

    }

    // Returns Jan, Feb etc
    public ArrayList<String> ShortMonths() {

        String[] shortMonths = new DateFormatSymbols().getShortMonths();

        ArrayList<String> shortList = new ArrayList<String>();

        for (int i = 0; i < shortMonths.length; i++) {
            String shortMonth = shortMonths[i];

            shortList.add(shortMonth);
        }
        return shortList;
    }

    // Returns January, Jan, March, Mar
    public ArrayList<String> allMonths() {

        ArrayList<String> allMonths = new ArrayList<String>();
        ArrayList<String> shortList = ShortMonths();
        ArrayList<String> monthList = Months();

        allMonths.addAll(shortList);
        allMonths.addAll(monthList);

        return allMonths;
    }


    //Extracts the date form the sting and returns a information that is stored in the calendar.
    //

    public Calendar dateExtraction(ArrayList<String> Information) {

        String theDate;
        int theMonth;
        int Day;

        ArrayList<String> AllMonths = new ArrayList<String>(Months());

        Calendar calendar = Calendar.getInstance();


        for (int i = 0; i < Information.size(); i++) {

            theDate = Information.get(i);

            for (int j = 0; j < AllMonths.size(); j++)

                if (Information.get(i).contains(AllMonths.get(j))) {

                    //sets month to the number corresponding to that month. OCT = 10.
                    theMonth = j;
                    //Our test document has a comma after the date. day gets rid of comma. day = 26
                    String day = Information.get(i + 1).substring(0, Information.get(i + 1).length() - 1);

                    Day = Integer.valueOf(day);

                    Log.i("Here", theDate);
                    Log.i("The Day", String.valueOf(day));


                    calendar.set(2016, theMonth, Day);
                }
        }


        return calendar;
    }

}

