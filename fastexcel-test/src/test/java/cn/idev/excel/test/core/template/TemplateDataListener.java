package cn.idev.excel.test.core.template;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.test.core.simple.SimpleDataListener;
import com.alibaba.fastjson2.JSON;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TemplateDataListener extends AnalysisEventListener<TemplateData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDataListener.class);
    List<TemplateData> list = new ArrayList<TemplateData>();

    @Override
    public void invoke(TemplateData data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        Assertions.assertEquals(list.size(), 2);
        Assertions.assertEquals(list.get(0).getString0(), "字符串0");
        Assertions.assertEquals(list.get(1).getString0(), "字符串1");
        LOGGER.debug("First row:{}", JSON.toJSONString(list.get(0)));
    }
}
