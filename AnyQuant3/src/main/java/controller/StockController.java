package controller;

import bl.factory.BLFactory;
import bl.impl.PushData;
import bl.service.GetStockService;
import com.fasterxml.jackson.core.JsonProcessingException;
import model.stock.StockNumber;
import model.stock.StockVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import util.exception.BadInputException;
import util.exception.NotFoundException;
import util.json.JsonConverter;
import util.time.DateCount;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kylin on 16/5/24.
 * All rights reserved.
 *
 */
@Controller
public class StockController {

    /**
     * 获取逻辑层数据的服务
     */
    private GetStockService getStockService;

    /**
     * 向界面返回数据的辅助类
     */
    private PushData pushData;

    private String startDate;
    private String endDate;

    //TODO 如果没有数据,需要界面处理
    public StockController() throws NotFoundException {
        this.getStockService = BLFactory.getInstance().getGetStockService();
        pushData = new PushData(getStockService);
        //获取日期信息
        this.endDate = DateCount.getToday();
        this.startDate = DateCount.count(endDate, -300);
    }

    /**
     * 界面传入参数获取股票信息的主方法
     *
     * @return 一日分时图数据与股票历史数据
     */
    @RequestMapping(value = "*.search", method = {RequestMethod.GET})
    public ModelAndView getStock(HttpServletRequest httpServletRequest)
            throws NotFoundException, IOException, BadInputException {
        //从用户的输入取参数
        String number = httpServletRequest.getParameter("number");
        String view = httpServletRequest.getParameter("view");

        //将新的参数加入session,封装成json格式
        StockNumber stockNumber= new StockNumber(number);
        String str = JsonConverter.jsonOfObject(stockNumber);
        //If an object of the same name is already bound to the session,the object is replaced.
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("number",str);

        Map<String, Object> model = new HashMap<>();
        StockVO stockVO = getStockService.getStock(number, startDate, endDate);
        //一日分时图数据(分时价格折线图与成交量)
        pushData.pushDailyModel(model,number,"2016-05-19");
        //单只股票基本信息(K线图,成交量柱状图)
        pushData.pushStockModel(model,stockVO);
        model.put("number",number);
        return new ModelAndView(view, model);
    }

    @RequestMapping(value = "*.stock", method = {RequestMethod.GET})
    public ModelAndView notChangeStock(HttpServletRequest httpServletRequest)
            throws NotFoundException, IOException, BadInputException {
        //直接从session取参数
        HttpSession session = httpServletRequest.getSession();
        String number = (String) session.getAttribute("number");

        Map<String, Object> model = new HashMap<>();
        StockVO stockVO = getStockService.getStock(number, startDate, endDate);
        //一日分时图数据(分时价格折线图与成交量)
        pushData.pushDailyModel(model,number,"2016-05-19");
        //单只股票基本信息(K线图,成交量柱状图)
        pushData.pushStockModel(model,stockVO);
        model.put("number",number);
        return new ModelAndView("singleStock", model);
    }

    @RequestMapping(value = "*.history", method = {RequestMethod.GET})
    //返回StockVO历史数据
    public ModelAndView getHistory(HttpServletRequest httpServletRequest)
            throws NotFoundException, IOException, BadInputException {

        HttpSession session = httpServletRequest.getSession();
        String number = (String) session.getAttribute("number");

        StockVO stockVO = getStockService.getStock(number, startDate, endDate);

        Map<String, Object> model = new HashMap<>();
        model.put("stockVO", stockVO);
        return new ModelAndView("history",model);
    }


    @RequestMapping(value = "*.analysis", method = {RequestMethod.GET})
    public ModelAndView getAnalysis(HttpServletRequest httpServletRequest)

            throws NotFoundException, BadInputException, JsonProcessingException {

        HttpSession session = httpServletRequest.getSession();
        String number = (String) session.getAttribute("number");

        //获取URL参数,StockVO
        StockVO stockVO = getStockService.getStock(number, startDate, endDate);

        Map<String, Object> model = new HashMap<>();
        //数据分析结果
        pushData.pushAnalyseModel(model, stockVO);
        return new ModelAndView("analysis",model);
    }


}
