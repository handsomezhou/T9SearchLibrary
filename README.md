T9SearchLibrary
===============

Provide data analysis method, data matching method and so on for T9 search.


Library:
T9Search,a Java Library Which provide data analysis methods, data matching method  for T9 search algorithm.

Import packages when use T9Search Library:
import com.t9search.util.*;
import com.t9search.model.*;

Data structure:PinyinUnit
PinyinUnit as a base data structure to save the string that Chinese characters  converted to Pinyin characters.

Function:
public static void chineseStringToPinyinUnit(String chineseString,List<PinyinUnit> pinyinUnit);
public static boolean matchPinyinUnits(final List<PinyinUnit> pinyinUnits,final String baseData, String search,StringBuffer chineseKeyWord);

Function call methods:
T9MatchPinyinUnits.matchPinyinUnits(...);
PinyinUtil.chineseStringToPinyinUnit(...);

Function call methods in detail:
Reference T9SearchDemo Project.
