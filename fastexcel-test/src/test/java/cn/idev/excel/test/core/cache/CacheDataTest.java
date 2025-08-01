package cn.idev.excel.test.core.cache;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.enums.CacheLocationEnum;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.metadata.FieldCache;
import cn.idev.excel.read.listener.PageReadListener;
import cn.idev.excel.test.demo.read.DemoData;
import cn.idev.excel.test.util.TestFileUtil;
import cn.idev.excel.util.ClassUtils;
import cn.idev.excel.util.FieldUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class CacheDataTest {

    private static File file07;
    private static File fileCacheInvoke;
    private static File fileCacheInvoke2;
    private static File fileCacheInvokeMemory;
    private static File fileCacheInvokeMemory2;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("cache/cache.xlsx");
        fileCacheInvoke = TestFileUtil.createNewFile("cache/fileCacheInvoke.xlsx");
        fileCacheInvoke2 = TestFileUtil.createNewFile("cache/fileCacheInvoke2.xlsx");
        fileCacheInvokeMemory = TestFileUtil.createNewFile("cache/fileCacheInvokeMemory.xlsx");
        fileCacheInvokeMemory2 = TestFileUtil.createNewFile("cache/fileCacheInvokeMemory2.xlsx");
    }

    @Test
    public void t01ReadAndWrite() throws Exception {
        Field field = FieldUtils.getField(ClassUtils.class, "FIELD_THREAD_LOCAL", true);
        ThreadLocal<Map<Class<?>, FieldCache>> fieldThreadLocal =
                (ThreadLocal<Map<Class<?>, FieldCache>>) field.get(ClassUtils.class.newInstance());
        Assertions.assertNull(fieldThreadLocal.get());
        EasyExcel.write(file07, CacheData.class).sheet().doWrite(data());
        EasyExcel.read(file07, CacheData.class, new PageReadListener<DemoData>(dataList -> {
                    Assertions.assertNotNull(fieldThreadLocal.get());
                }))
                .sheet()
                .doRead();
        Assertions.assertNull(fieldThreadLocal.get());
    }

    @Test
    public void t02ReadAndWriteInvoke() throws Exception {
        EasyExcel.write(fileCacheInvoke, CacheInvokeData.class).sheet().doWrite(dataInvoke());
        EasyExcel.read(fileCacheInvoke, CacheInvokeData.class, new AnalysisEventListener<CacheInvokeData>() {

                    @Override
                    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                        Assertions.assertEquals(2, headMap.size());
                        Assertions.assertEquals("姓名", headMap.get(0));
                        Assertions.assertEquals("年龄", headMap.get(1));
                    }

                    @Override
                    public void invoke(CacheInvokeData data, AnalysisContext context) {}

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .sheet()
                .doRead();

        Field name = FieldUtils.getField(CacheInvokeData.class, "name", true);
        ExcelProperty annotation = name.getAnnotation(ExcelProperty.class);
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        Field memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
        memberValues.setAccessible(true);
        Map map = (Map) memberValues.get(invocationHandler);
        map.put("value", new String[] {"姓名2"});

        EasyExcel.write(fileCacheInvoke2, CacheInvokeData.class).sheet().doWrite(dataInvoke());
        EasyExcel.read(fileCacheInvoke2, CacheInvokeData.class, new AnalysisEventListener<CacheInvokeData>() {

                    @Override
                    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                        Assertions.assertEquals(2, headMap.size());
                        Assertions.assertEquals("姓名2", headMap.get(0));
                        Assertions.assertEquals("年龄", headMap.get(1));
                    }

                    @Override
                    public void invoke(CacheInvokeData data, AnalysisContext context) {}

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .sheet()
                .doRead();
    }

    @Test
    public void t03ReadAndWriteInvokeMemory() throws Exception {
        EasyExcel.write(fileCacheInvokeMemory, CacheInvokeMemoryData.class)
                .filedCacheLocation(CacheLocationEnum.MEMORY)
                .sheet()
                .doWrite(dataInvokeMemory());
        EasyExcel.read(
                        fileCacheInvokeMemory,
                        CacheInvokeMemoryData.class,
                        new AnalysisEventListener<CacheInvokeMemoryData>() {

                            @Override
                            public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                                Assertions.assertEquals(2, headMap.size());
                                Assertions.assertEquals("姓名", headMap.get(0));
                                Assertions.assertEquals("年龄", headMap.get(1));
                            }

                            @Override
                            public void invoke(CacheInvokeMemoryData data, AnalysisContext context) {}

                            @Override
                            public void doAfterAllAnalysed(AnalysisContext context) {}
                        })
                .filedCacheLocation(CacheLocationEnum.MEMORY)
                .sheet()
                .doRead();

        Field name = FieldUtils.getField(CacheInvokeMemoryData.class, "name", true);
        ExcelProperty annotation = name.getAnnotation(ExcelProperty.class);
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        Field memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
        memberValues.setAccessible(true);
        Map map = (Map) memberValues.get(invocationHandler);
        map.put("value", new String[] {"姓名2"});

        EasyExcel.write(fileCacheInvokeMemory2, CacheInvokeMemoryData.class)
                .filedCacheLocation(CacheLocationEnum.MEMORY)
                .sheet()
                .doWrite(dataInvokeMemory());
        EasyExcel.read(
                        fileCacheInvokeMemory2,
                        CacheInvokeMemoryData.class,
                        new AnalysisEventListener<CacheInvokeMemoryData>() {

                            @Override
                            public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                                Assertions.assertEquals(2, headMap.size());
                                Assertions.assertEquals("姓名", headMap.get(0));
                                Assertions.assertEquals("年龄", headMap.get(1));
                            }

                            @Override
                            public void invoke(CacheInvokeMemoryData data, AnalysisContext context) {}

                            @Override
                            public void doAfterAllAnalysed(AnalysisContext context) {}
                        })
                .filedCacheLocation(CacheLocationEnum.MEMORY)
                .sheet()
                .doRead();
    }

    private List<CacheData> data() {
        List<CacheData> list = new ArrayList<CacheData>();
        for (int i = 0; i < 10; i++) {
            CacheData simpleData = new CacheData();
            simpleData.setName("姓名" + i);
            simpleData.setAge((long) i);
            list.add(simpleData);
        }
        return list;
    }

    private List<CacheInvokeData> dataInvoke() {
        List<CacheInvokeData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CacheInvokeData simpleData = new CacheInvokeData();
            simpleData.setName("姓名" + i);
            simpleData.setAge((long) i);
            list.add(simpleData);
        }
        return list;
    }

    private List<CacheInvokeMemoryData> dataInvokeMemory() {
        List<CacheInvokeMemoryData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CacheInvokeMemoryData simpleData = new CacheInvokeMemoryData();
            simpleData.setName("姓名" + i);
            simpleData.setAge((long) i);
            list.add(simpleData);
        }
        return list;
    }
}
