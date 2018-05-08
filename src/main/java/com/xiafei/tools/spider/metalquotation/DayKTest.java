package com.xiafei.tools.spider.metalquotation;

import com.xiafei.tools.common.BeanUtils;
import com.xiafei.tools.common.MapUtils;
import com.xiafei.tools.common.StringUtils;

import java.beans.IntrospectionException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <P>Description:  日K数据文件校验. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/8/22</P>
 * <P>UPDATE DATE: 2017/8/22</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public class DayKTest {

    public static void main(String[] args) throws IntrospectionException, ReflectiveOperationException {
        final List<String> pageKList = new ArrayList<>();
        try (
                BufferedReader reader = new BufferedReader(
                        new FileReader("E:\\self-study\\tools\\src\\mail\\java\\com\\xiafei\\tools\\spider\\metalquotation\\dayk.txt"));
        ) {
            String line = reader.readLine();
            String lastTimeDay = "0";
            // 一个页面第二次出现的Au(T+D)就是mAu(T+D);
            int autdCount = 0;
            while (line != null) {
                String instId = StringUtils.getPropertyValueFromSimilarURL(line, "instId", ",").replace("'", "");
                if (DayKSpider.InstIdEnum.instance(instId) == null) {
                    System.out.println("异常的instid=" + instId + ",line=" + line);
                }
                String timeDay = StringUtils.getPropertyValueFromSimilarURL(line, "timeDay", ",").replace("'", "").replace("}", "");
                if (Integer.parseInt(lastTimeDay) > Integer.parseInt(timeDay)) {
                    System.out.println("时间顺序错误，报文:" + line);
                }
                if (lastTimeDay.equals(timeDay)) {
                    if (instId.equals("Au(T+D)")) {
                        autdCount++;
                        if (autdCount >= 2) {
                            System.out.println("发现并未改成mAu(T+D)" + line);
                        }
                    }
                } else {
                    lastTimeDay = timeDay;
                    autdCount = 0;
                }


                pageKList.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final List<DayK> dayKList = convertStrKToObjK(pageKList);
        final Map<String, List<DayK>> groupByInstMap = MapUtils.newHashMap();
        for (DayK k : dayKList) {
            if (groupByInstMap.get(k.getInstId()) == null) {
                groupByInstMap.put(k.getInstId(), new ArrayList<DayK>());
            }
            groupByInstMap.get(k.getInstId()).add(k);
        }

        for (Map.Entry<String, List<DayK>> entry : groupByInstMap.entrySet()) {
            Integer lastTimeDay = 0;
            for (DayK k : entry.getValue()) {
                if (lastTimeDay.equals(k.getTimeDay())) {
                    System.out.println("日期重复，请检查" + k);
                }
                lastTimeDay = k.getTimeDay();
            }
        }
    }


    /**
     * K线页面拔下来的数据转换成对象.
     */
    private static List<DayK> convertStrKToObjK(final List<String> dataList) throws IntrospectionException, ReflectiveOperationException {
        final List<DayK> dayKList = new ArrayList<>(dataList.size());
        for (String k : dataList) {
            final DayK dayK = BeanUtils.parseKeyValueStrToBean(k.replace("'", "").replace("}", ""), ",", false, DayK.class, null);
            dayKList.add(dayK);
        }
        return dayKList;
    }

}
