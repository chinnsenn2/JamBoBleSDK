package com.jianbao.jamboble.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
    /**
     * 00 格式化数字，保留2位
     */
    public static final DecimalFormat DF_2 = new DecimalFormat("00");

    /**
     * 时间字符串转化成秒
     *
     * @param s
     * @return
     */
    public static long convertTimeStringToSec(String s) {
        // hh:mm:ss.xxx
        if (null == s)
            return 0;

        long res = 0;
        String temp = null;
        String temp2 = null;

        int nDot = s.lastIndexOf('.');
        if (nDot != -1) {
            temp2 = s.substring(nDot + 1);
            s = s.substring(0, nDot);
        }

        int i = 0;
        // sec
        do {
            int nSem = s.lastIndexOf(':');
            if (nSem == -1) {
                temp = s;
                s = null;
            } else {
                temp = s.substring(nSem + 1);
                s = s.substring(0, nSem);
            }

            try {
                res += Integer.parseInt(temp) * Math.pow(60, i++);
            } catch (NumberFormatException e) {
                return 0;
            }
        } while (s != null);

        try {
            int mSec = Integer.parseInt(temp2);
            if (mSec > 500) {
                res = res + 1;
            }
        } catch (Exception e) {

        }
        return res;
    }

    /**
     * 秒转成计时时间，格式为 “xx时：xx分:xx秒”或者“xx分:xx秒”
     *
     * @param lSeconds
     * @param bKeepZeroHour
     * @return
     */
    public static String convertSecToTimeString(long lSeconds,
                                                boolean bKeepZeroHour) {
        long nHour = lSeconds / 3600;
        long nMin = lSeconds % 3600;
        long nSec = nMin % 60;
        nMin = nMin / 60;

        return (nHour == 0 && !bKeepZeroHour) ? String.format("%02d:%02d", nMin, nSec) : String.format("%02d:%02d:%02d", nHour, nMin, nSec);
    }

    /**
     * 秒转成时间字符串，格式为“年-月-日  时：分：秒”
     *
     * @param timeMillion
     * @return
     */
    public static String getDateYmdHms(long timeMillion) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(timeMillion);
        return formatter.format(curDate);
    }

    /**
     * 秒转成时间字符串 "年-月"
     *
     * @param timeMillion
     * @return
     */
    public static String getDateYM(long timeMillion) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        Date curDate = new Date(timeMillion);
        return formatter.format(curDate);
    }

    /**
     * 秒转成时间字符串 "年-月-日"
     *
     * @param timeMillion
     * @return
     */
    public static String getDateYmd(long timeMillion) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(timeMillion);
        return formatter.format(curDate);
    }

    /**
     * Date对象转时间字符串，格式为“年-月-日  时：分：秒”
     *
     * @param date
     * @return
     */
    public static String getDateYmdHms(Date date) {
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    /**
     * Date对象转时间字符串，格式为 时：分：秒”
     *
     * @param date
     * @return
     */
    public static String getDateHmss(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(date);
    }

    /**
     * Date对象转时间字符串，格式为 mm'ss''”
     *
     * @param date
     * @return
     */
    public static String getDatemmss(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }

    /**
     * Date对象转时间字符串，格式为 时：分”
     *
     * @param date
     * @return
     */
    public static String getDateHm(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }

    public static String getDateYmdHms(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    /**
     * Date对象转时间字符串，格式为“年-月-日  时：分”
     *
     * @param date
     * @return
     */
    public static String getDateYmdHm(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(date);
    }

    /**
     * Date对象转时间字符串，格式为“年-月-日 ”
     *
     * @param date
     * @return
     */
    public static String getDateYmd(Date date) {
        SimpleDateFormat formatter;
        if (date != null) {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(date);
        } else {
            return "";
        }
    }

    public static String getMonth(Date date) {
        SimpleDateFormat formatter;
        if (date != null) {
            formatter = new SimpleDateFormat("M");
            return formatter.format(date);
        } else {
            return "";
        }
    }

    public static String getDateYm(Date date) {
        SimpleDateFormat formatter;
        if (date != null) {
            formatter = new SimpleDateFormat("yyyy年M月");
            return formatter.format(date);
        } else {
            return "";
        }
    }

    /**
     * 字符串转Date，字符串格式为“年-月-日 ... ”
     *
     * @param dateString
     * @return
     */
    public static Date stringToDate(String dateString) {
        Date dateValue = new Date();
        try {
            ParsePosition position = new ParsePosition(0);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateValue = simpleDateFormat.parse(dateString, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateValue;
    }

    /**
     * 字符串转Date，字符串格式为“年-月-日 ... ”
     *
     * @param dateString
     * @return
     */
    public static Date stringToDateUTC(String dateString) {
        Date dateValue = new Date();
        try {
            ParsePosition position = new ParsePosition(0);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            dateValue = simpleDateFormat.parse(dateString, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateValue;
    }

    public static Date stringToDate(String dateString, String pattern) {
        Date dateValue = new Date();
        try {
            ParsePosition position = new ParsePosition(0);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            dateValue = simpleDateFormat.parse(dateString, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateValue;
    }

    /**
     * 获取当前月日字符串<05/24>
     */
    public static StringBuffer getSpecDateStr() {
        Calendar c = Calendar.getInstance();
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        StringBuffer date = new StringBuffer();
        date.append("< ");
        if ((mMonth + 1) < 10) {
            date.append("0" + mMonth);
        } else {
            date.append(ValueCast.intToString(mMonth + 1));
        }
        date.append("/");
        if (mDay < 10) {
            date.append("0" + mDay);
        } else {
            date.append(ValueCast.intToString(mDay));
        }
        date.append(" >");
        return date;
    }

    /**
     * 获取月日 如：5月3日
     *
     * @param date
     * @return
     */
    public static String getDateMD(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        try {
            SimpleDateFormat dfOld = new SimpleDateFormat("yyyy-MM-dd");
            Date oldDate = dfOld.parse(date);

            SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
            String newDate = df.format(oldDate);
            return newDate;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getDateMDate(String date) {
        return getDateMD(date, "MM/dd");
    }

    public static String getDateMD(String date, String newFormatter) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        try {
            SimpleDateFormat dfOld = new SimpleDateFormat("yyyy-MM-dd");
            Date oldDate = dfOld.parse(date);

            SimpleDateFormat df = new SimpleDateFormat(newFormatter);
            String newDate = df.format(oldDate);
            return newDate;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param date 2016-12-16 17:05
     * @return 2016-12-16 12:12:12
     */
    public static String getDateMDHM(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }

        try {
            SimpleDateFormat dfOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date oldDate = dfOld.parse(date);

            SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss");
            String newDate = df.format(oldDate);
            return newDate;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param date 2016-12-16 17:05
     * @return 2016-12-16 12:12:12
     */
    public static String getDateMDHMZ(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }

        try {
            SimpleDateFormat dfOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date oldDate = dfOld.parse(date);

            dfOld = new SimpleDateFormat("MM月dd日 HH:mm");
            return dfOld.format(oldDate);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getDateYMDHMS(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }

        try {
            SimpleDateFormat dfOld = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date oldDate = dfOld.parse(date);

            SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            String newDate = df.format(oldDate);
            return newDate;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param date 2016-10-10 10:10:10
     * @return 2016-10-10
     */
    public static String getDateYMD(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }

        try {
            SimpleDateFormat dfOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date oldDate = dfOld.parse(date);

//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            dfOld.applyPattern("yyyy-MM-dd");
            return dfOld.format(oldDate);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getTimeHSByY(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        try {
            SimpleDateFormat dfOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date oldDate = dfOld.parse(time);

            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            String newDate = df.format(oldDate);
            return newDate;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getTimeHMSByY(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        try {
            SimpleDateFormat dfOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date oldDate = dfOld.parse(time);

            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String newDate = df.format(oldDate);
            return newDate;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getTimeHS(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        try {
            SimpleDateFormat dfOld = new SimpleDateFormat("HH:mm:ss");
            Date oldDate = dfOld.parse(time);

            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            String newDate = df.format(oldDate);
            return newDate;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 返回月
     *
     * @param timeMillion
     * @return
     */
    public static String getMonth(long timeMillion) {
        SimpleDateFormat formatter = new SimpleDateFormat("M月");
        Date curDate = new Date(timeMillion);
        return formatter.format(curDate);
    }

    public static String getYear(long timeMillion) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年");
        Date curDate = new Date(timeMillion);
        return formatter.format(curDate);
    }

    /**
     * 返回日
     *
     * @param timeMillion
     * @return
     */
    public static String getDate(long timeMillion) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        Date curDate = new Date(timeMillion);
        return formatter.format(curDate);
    }

    /**
     * 返回小时
     *
     * @param time : 格式为HH:mm:ss
     * @return
     */
    public static int getHour(String time) {
        try {
            SimpleDateFormat dfOld = new SimpleDateFormat("HH:mm:ss");
            Date date = dfOld.parse(time);
            return date.getHours();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * @param date
     * @return true 参数时间大于当前时间
     */
    public static boolean compareDate(Date date) {
        if (date != null) {
            Date now = new Date();
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(date);
            c2.setTime(now);

            int result = c1.compareTo(c2);
            return result >= 0;
        }
        return false;
    }

    /**
     * @param date
     * @param date2
     * @return true date更大
     */
    public static boolean compareDate(Date date, Date date2) {
        if (date != null && date2 != null) {
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(date);
            c2.setTime(date2);

            int result = c1.compareTo(c2);
            return result >= 0;
        }
        return false;
    }

    public static String getDateMD(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        return sdf.format(date);
    }

    public static String getDateMDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        return sdf.format(date);
    }

    /**
     * @param time 单位秒
     * @return
     */
    public static String getStringTime(int time) {

//		long hour = time/3600;
        time = time % 3600;
        long min = time / 60;
        time = time % 60;
        return String.format(Locale.CHINA, "%02d:%02d", min, time);
    }

    /**
     * 把milliseconds转换为00:00时间显示
     *
     * @param milliseconds
     * @return 00:00
     */
    public static String changeToTimeStr(long milliseconds) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long minus = seconds / 60;
        long second = seconds % 60;
        if (second < 10) {
            return minus + ":0" + second;
        } else {
            return minus + ":" + second;
        }
    }

    public static String changeToTimeStrChina(long milliseconds) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long minus = seconds / 60;
        long second = seconds % 60;
        if (second < 10) {
            return minus + "分0" + second + "秒";
        } else {
            return minus + "分" + second + "秒";
        }

    }

    public static String secondToHMS(int seconds) {
        int hour = seconds / 3600;
        int minus = (seconds - hour * 3600) / 60;
        int second = (seconds - hour * 3600 - minus * 60) % 60;
        return String.format("%02d:%02d:%02d", hour,  minus, second);
    }

    public static String secondToMS(int seconds) {
        int minus = seconds / 60;
        int second = (seconds - minus * 60) % 60;
        return String.format(Locale.CHINA, "%d'%d''", minus, second);
    }

    public static String getDate(String date, String oldPattern, String newPattern) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        try {
            SimpleDateFormat dfOld = new SimpleDateFormat(oldPattern);
            Date oldDate = dfOld.parse(date);

            SimpleDateFormat df = new SimpleDateFormat(newPattern);
            String newDate = df.format(oldDate);
            return newDate;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 是否是双11期间
     *
     * @return
     */
    public static boolean isDouble11() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return month == 10 && (day > 6 && day < 12);
    }

    public static boolean isFeatureDay(String mSelectedDay) {
        String today = TimeUtil.getDateYmd(new Date());
        return today.compareTo(mSelectedDay) < 0;
    }

    public static boolean isToday(String mSelectedDay) {
        String today = TimeUtil.getDateYmd(new Date());
        return today.compareTo(mSelectedDay) == 0;
    }

    public static int getYearSepBy(String yearMonth, String oldPatter, String newPatter) {
        String temp = "1990";
        try {
            temp = TimeUtil.getDate(yearMonth, oldPatter, newPatter);
        } catch (Exception e) {

        }
        if (TextUtils.isEmpty(temp)) {
            temp = TimeUtil.getDateYmdHms(new Date(), newPatter);
        }

        return Integer.valueOf(temp);
    }

    public static int getMonthSepBy(String yearMonth, String oldPatter, String newPatter) {
        String temp = "1";
        try {
            temp = TimeUtil.getDate(yearMonth, oldPatter, newPatter);
        } catch (Exception e) {

        }
        if (TextUtils.isEmpty(temp)) {
            temp = TimeUtil.getDateYmdHms(new Date(), newPatter);
        }

        return Integer.valueOf(temp);
    }

    public static int getDaySepBy(String recordDate, String oldPatter, String newPatter) {
        String temp = "1";
        try {
            temp = TimeUtil.getDate(recordDate, oldPatter, newPatter);
        } catch (Exception e) {

        }
        if (TextUtils.isEmpty(temp)) {
            temp = TimeUtil.getDateYmdHms(new Date(), newPatter);
        }

        return Integer.valueOf(temp);
    }

    /**
     * 描述：获取手机TimeZone时区，单位 小时
     * 包含了 夏时令 和 冬时令的 偏移
     *
     * @return
     */
    public static float getTimeZone() {
        TimeZone tz = TimeZone.getDefault();

        //String s = "TimeZone:"+tz.getDisplayName(false, TimeZone.SHORT)+",seqid:" +tz.getID();

        Calendar calendar = Calendar.getInstance();
        float f = (tz.getRawOffset() + calendar.get(Calendar.DST_OFFSET)) / 1000f / 60f / 60f;

        //LogUtil.showMsg(TAG+" getTimeZone s:"+s+",f:" +f+",str:" + StringUtil.DF_P_2.format(f));

        return (int) (f * 100) / 100f;
    }

    /**
     * <h3>获取当前时间的 时间戳</h3>
     * <ul>
     * <li>单位为S</li>
     * </ul>
     *
     * @return
     */
    public static int getCurrentTimeInt() {
        Calendar c = getCalendar(-100);
        return (int) (c.getTimeInMillis() / 1000);
    }

    /**
     * 根据时区获取 对应的 calendar
     *
     * @param tzone
     * @return
     */
    public static Calendar getCalendar(float tzone) {
        Calendar c = Calendar.getInstance();
        TimeZone tz = null;
        if (tzone == -100) {
            tzone = getTimeZone();
            tz = TimeZone.getTimeZone(getTimeZone(tzone));
        } else {
            tz = TimeZone.getTimeZone(getTimeZone(tzone));
        }

        c.setTimeZone(tz);
        c.setMinimalDaysInFirstWeek(1);
        c.setFirstDayOfWeek(Calendar.MONDAY);
//		c.setFirstDayOfWeek(Calendar.SUNDAY);

        return c;
    }

    /**
     * 描述：根据时区的值，获取String 时区值
     *
     * @param tzone
     * @return
     */
    public static String getTimeZone(float tzone) {
        String timezone = "";
        int zone = (int) tzone;
        int mode = (int) ((tzone * 100) % 100);
        if (mode == 0) {//整数
            if (tzone >= 0) {
                timezone = "GMT+" + zone;
            } else if (tzone == -100) {
                timezone = "";
            } else {
                timezone = "GMT" + zone;
            }
//			LogUtil.showMsg(TAG+" getTimeZone tzone:" + tzone+",str:" + timezone);
        } else {

            int minute = (int) (60 * (mode / 100f));

            if (tzone >= 0) {
                timezone = "GMT+" + DF_2.format(zone) + ":" + DF_2.format(minute);
            } else if (tzone == -100) {
                timezone = "";
            } else {
                timezone = "GMT" + DF_2.format(zone) + ":" + DF_2.format(minute);
            }

//			LogUtil.showMsg(TAG+" getTimeZone tzone:" + tzone+",str:" + timezone);
        }
        return timezone;
    }

    /**
     * <p>
     * 用于格式化当前日期,作为日志文件名的一部分
     * </p>
     * <p>
     * </p>
     * 2015年7月10日
     */
    public static String getTime() {
        // 用于格式化日期,作为日志文件名的一部分
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        long timestamp = System.currentTimeMillis();
        String time = formatter.format(new Date());
        return time + "----" + timestamp;
    }

    /**
     * 判断是不是同一天
     *
     * @param date1
     * @param Date2
     * @return
     */
    public static boolean inSameDay(Date date1, Date Date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int year1 = calendar.get(Calendar.YEAR);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(Date2);
        int year2 = calendar.get(Calendar.YEAR);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);

        return (year1 == year2) && (day1 == day2);
    }

    /**
     * 判断是不是同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean inSameDay(String date1, String date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(date1));
        int year1 = calendar.get(Calendar.YEAR);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(stringToDate(date2));
        int year2 = calendar.get(Calendar.YEAR);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);

        return (year1 == year2) && (day1 == day2);
    }

    public static int getAgeByBirth(Date birthday) {
        int age;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = Calendar.getInstance();
            birth.setTime(birthday);

            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
            return 0;
        }
    }

    public static String formatDisplayTime(String time) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;

        if (time != null) {
            try {
                Date tDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today)).getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    SimpleDateFormat halfDf = new SimpleDateFormat("yyyy/MM/dd");
                    SimpleDateFormat timeDf = new SimpleDateFormat("HH:mm");
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = halfDf.format(tDate);
                    } else {

                        if (dTime < tMin) {
                            //display = "刚刚";
                            display = timeDf.format(tDate);

                        } else if (dTime < tHour) {
                            //display = (int) Math.ceil(dTime / tMin) + "分钟前";
                            display = timeDf.format(tDate);

                        } else if (dTime < tDay && tDate.after(yesterday)) {
                            //display = (int) Math.ceil(dTime / tHour) + "小时前";
                            display = timeDf.format(tDate);

                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                            display = "昨天";

                        } else {
                            display = halfDf.format(tDate);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                display = time;
            }
        }

        return display;
    }

}