package cn.idev.excel.test.temp.read;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 *
 *
 * @date 2020/4/9 16:33
 **/
@Slf4j
public class TestListener extends AnalysisEventListener {

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        log.info("解析一条:{}", JSON.toJSONString(o));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {}
}
