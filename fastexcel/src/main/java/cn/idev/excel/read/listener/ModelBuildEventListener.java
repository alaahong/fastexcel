package cn.idev.excel.read.listener;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.enums.HeadKindEnum;
import cn.idev.excel.enums.ReadDefaultReturnEnum;
import cn.idev.excel.exception.ExcelDataConvertException;
import cn.idev.excel.metadata.Head;
import cn.idev.excel.metadata.data.DataFormatData;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.read.metadata.holder.ReadSheetHolder;
import cn.idev.excel.read.metadata.property.ExcelReadHeadProperty;
import cn.idev.excel.support.cglib.beans.BeanMap;
import cn.idev.excel.util.BeanMapUtils;
import cn.idev.excel.util.ClassUtils;
import cn.idev.excel.util.ConverterUtils;
import cn.idev.excel.util.DateUtils;
import cn.idev.excel.util.MapUtils;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Convert to the object the user needs
 *
 */
public class ModelBuildEventListener implements IgnoreExceptionReadListener<Map<Integer, ReadCellData<?>>> {

    @Override
    public void invoke(Map<Integer, ReadCellData<?>> cellDataMap, AnalysisContext context) {
        ReadSheetHolder readSheetHolder = context.readSheetHolder();
        if (HeadKindEnum.CLASS.equals(readSheetHolder.excelReadHeadProperty().getHeadKind())) {
            context.readRowHolder().setCurrentRowAnalysisResult(buildUserModel(cellDataMap, readSheetHolder, context));
            return;
        }
        context.readRowHolder().setCurrentRowAnalysisResult(buildNoModel(cellDataMap, readSheetHolder, context));
    }

    private Object buildNoModel(
            Map<Integer, ReadCellData<?>> cellDataMap, ReadSheetHolder readSheetHolder, AnalysisContext context) {
        int index = 0;
        Map<Integer, Object> map = MapUtils.newLinkedHashMapWithExpectedSize(cellDataMap.size());
        for (Map.Entry<Integer, ReadCellData<?>> entry : cellDataMap.entrySet()) {
            Integer key = entry.getKey();
            ReadCellData<?> cellData = entry.getValue();
            while (index < key) {
                map.put(index, null);
                index++;
            }
            index++;

            ReadDefaultReturnEnum readDefaultReturn =
                    context.readWorkbookHolder().getReadDefaultReturn();
            if (readDefaultReturn == ReadDefaultReturnEnum.STRING) {
                // string
                map.put(key, (String) ConverterUtils.convertToJavaObject(
                        cellData,
                        null,
                        null,
                        readSheetHolder.converterMap(),
                        context,
                        context.readRowHolder().getRowIndex(),
                        key));
            } else {
                // return ReadCellData
                ReadCellData<?> convertedReadCellData = convertReadCellData(
                        cellData, context.readWorkbookHolder().getReadDefaultReturn(), readSheetHolder, context, key);
                if (readDefaultReturn == ReadDefaultReturnEnum.READ_CELL_DATA) {
                    map.put(key, convertedReadCellData);
                } else {
                    map.put(key, convertedReadCellData.getData());
                }
            }
        }
        // fix https://github.com/fast-excel/fastexcel/issues/2014
        int headSize = calculateHeadSize(readSheetHolder);
        // fix https://github.com/fast-excel/fastexcel/issues/220
        while (index <= headSize) {
            map.put(index, null);
            index++;
        }
        return map;
    }

    private ReadCellData convertReadCellData(
            ReadCellData<?> cellData,
            ReadDefaultReturnEnum readDefaultReturn,
            ReadSheetHolder readSheetHolder,
            AnalysisContext context,
            Integer columnIndex) {
        Class<?> classGeneric;
        switch (cellData.getType()) {
            case STRING:
            case DIRECT_STRING:
            case ERROR:
            case EMPTY:
                classGeneric = String.class;
                break;
            case BOOLEAN:
                classGeneric = Boolean.class;
                break;
            case NUMBER:
                DataFormatData dataFormatData = cellData.getDataFormatData();
                if (dataFormatData != null
                        && DateUtils.isADateFormat(dataFormatData.getIndex(), dataFormatData.getFormat())) {
                    classGeneric = LocalDateTime.class;
                } else {
                    classGeneric = BigDecimal.class;
                }
                break;
            default:
                classGeneric = ConverterUtils.defaultClassGeneric;
                break;
        }

        return (ReadCellData) ConverterUtils.convertToJavaObject(
                cellData,
                null,
                ReadCellData.class,
                classGeneric,
                null,
                readSheetHolder.converterMap(),
                context,
                context.readRowHolder().getRowIndex(),
                columnIndex);
    }

    private int calculateHeadSize(ReadSheetHolder readSheetHolder) {
        if (readSheetHolder.excelReadHeadProperty().getHeadMap().size() > 0) {
            return readSheetHolder.excelReadHeadProperty().getHeadMap().size();
        }
        if (readSheetHolder.getMaxNotEmptyDataHeadSize() != null) {
            return readSheetHolder.getMaxNotEmptyDataHeadSize();
        }
        return 0;
    }

    private Object buildUserModel(
            Map<Integer, ReadCellData<?>> cellDataMap, ReadSheetHolder readSheetHolder, AnalysisContext context) {
        ExcelReadHeadProperty excelReadHeadProperty = readSheetHolder.excelReadHeadProperty();
        Object resultModel;
        try {
            resultModel = excelReadHeadProperty.getHeadClazz().newInstance();
        } catch (Exception e) {
            throw new ExcelDataConvertException(
                    context.readRowHolder().getRowIndex(),
                    0,
                    new ReadCellData<>(CellDataTypeEnum.EMPTY),
                    null,
                    "Can not instance class: "
                            + excelReadHeadProperty.getHeadClazz().getName(),
                    e);
        }
        Map<Integer, Head> headMap = excelReadHeadProperty.getHeadMap();
        BeanMap dataMap = BeanMapUtils.create(resultModel);
        for (Map.Entry<Integer, Head> entry : headMap.entrySet()) {
            Integer index = entry.getKey();
            Head head = entry.getValue();
            String fieldName = head.getFieldName();
            if (!cellDataMap.containsKey(index)) {
                continue;
            }
            ReadCellData<?> cellData = cellDataMap.get(index);
            Object value = ConverterUtils.convertToJavaObject(
                    cellData,
                    head.getField(),
                    ClassUtils.declaredExcelContentProperty(
                            dataMap,
                            readSheetHolder.excelReadHeadProperty().getHeadClazz(),
                            fieldName,
                            readSheetHolder),
                    readSheetHolder.converterMap(),
                    context,
                    context.readRowHolder().getRowIndex(),
                    index);
            if (value != null) {
                dataMap.put(fieldName, value);

                // 规避由于实体类 setter 不规范导致无法赋值的问题
                // fix https://github.com/alibaba/easyexcel/issues/3524
                if (dataMap.get(fieldName) == null) {
                    Object bean = dataMap.getBean();
                    try {
                        Field field = bean.getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        field.set(bean, value);
                    } catch (NoSuchFieldException ignore) {
                        // ignore
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return resultModel;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}
}
