package com.example.octatunes.Utils;

public class StringUtil {
    public static String removeAccents(String input) {
        final String withAccent = "àáảãạâầấẩẫậăằắẳẵặèéẻẽẹêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵđÀÁẢÃẠÂẦẤẨẪẬĂẰẮẲẴẶÈÉẺẼẸÊỀẾỂỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘƠỜỚỞỠỢÙÚỦŨỤƯỪỨỬỮỰỲÝỶỸỴĐ";
        final String withoutAccent = "aaaaaaaaaaaaaaaaaeeeeeeeeeeeiiiiiooooooooooooooooouuuuuuuuuuuyyyyydAAAAAAAAAAAAAAAAAEEEEEEEEEEEIIIIIOOOOOOOOOOOOOOOOOUUUUUUUUUUUYYYYYD";
        if(input == null)
            return "";
        StringBuilder sb = new StringBuilder(input);
        for (int i = 0; i < sb.length(); i++) {
            int index = withAccent.indexOf(sb.charAt(i));
            if (index != -1) {
                sb.setCharAt(i, withoutAccent.charAt(index));
            }
        }
        return sb.toString();
    }
    public static String replaceSpaceWithPlus(String input) {
        return removeAccents(input.replace(" ", "+").toLowerCase());
    }
}
