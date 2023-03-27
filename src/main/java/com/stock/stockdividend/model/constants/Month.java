package com.stock.stockdividend.model.constants;

public enum Month {

    // 월(문자열), 매칭 월(숫자)
    Jan("Jan", 1),
    Feb("Feb", 2),
    Mar("Mar", 3),
    Apr("Apr", 4),
    May("May", 5),
    Jun("Jun", 6),
    Jul("Jul", 7),
    Aug("Aug", 8),
    Sep("Sep", 9),
    Oct("Oct", 10),
    Nov("Nov", 11),
    Dec("Dec", 12);

    private String stringMonth;
    private int number;

    /**
     * 생성자
     *
     * @param stringMonth
     * @param number
     */
    Month(String stringMonth, int number) {
        this.stringMonth = stringMonth;
        this.number = number;
    }

    /**
     * 해당하는 월(숫자) 찾기
     *
     * @param stringMonth
     * @return
     */
    public static int strToNumber(String stringMonth) {
        for (Month month : Month.values()) {
            if (month.stringMonth.equals(stringMonth)) {
                return month.number;
            }
        }

        return -1;
    }
}
